package com.example.ringo_star.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ringo_star.MainActivity;
import com.example.ringo_star.R;
import com.example.ringo_star.ReportCreatedActivity;
import com.example.ringo_star.data.DatabaseClient;
import com.example.ringo_star.data.entity.User;
import com.example.ringo_star.llm.InferenceModel;
import com.example.ringo_star.utils.PDFUtils;
import com.example.ringo_star.utils.RingoStarRDF4J;

import org.eclipse.rdf4j.model.Model;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MagicFragment extends Fragment {
    private long downloadId;
    private DownloadManager downloadManager;
    private Handler handler;
    private Runnable runnable;

    RadioGroup radioGroup;

    RadioButton radioButtonGemma;
    RadioButton radioButtonFalcon;
    RadioButton radioButtonStable;
    RadioButton radioButtonPhi;

    TextView txtModelDescription;

    ConstraintLayout constraintLayoutProgressBar;
    ProgressBar progressBarDownload;

    TextView txtPercentage;

    Button btnDownloadModel;

    ImageView imgMagic;

    private static final long MAX_WAIT_TIME_MS = 5000;
    private long startTime;

    public MagicFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_magic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler();

        radioGroup = view.findViewById(R.id.radioGroup);

        radioButtonGemma = view.findViewById(R.id.radioButtonGemma);
        radioButtonFalcon = view.findViewById(R.id.radioButtonFalcon);
        radioButtonStable = view.findViewById(R.id.radioButtonStable);
        radioButtonPhi = view.findViewById(R.id.radioButtonPhi);

        txtModelDescription = view.findViewById(R.id.txtModelDescription);
        txtModelDescription.setText(Html.fromHtml(txtModelDescription.getText().toString(), Html.FROM_HTML_MODE_LEGACY));

        manageModelDownloadable();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = view.findViewById(checkedId);

            if(selectedRadioButton.getId() == R.id.radioButtonGemma) {
                txtModelDescription.setText(Html.fromHtml(getString(R.string.gemma2b_hint), Html.FROM_HTML_MODE_LEGACY));
                handleRadioButtons("gemma2b");
            } else if(selectedRadioButton.getId() == R.id.radioButtonFalcon) {
                txtModelDescription.setText(Html.fromHtml(getString(R.string.falcon_rw1b_hint), Html.FROM_HTML_MODE_LEGACY));
                handleRadioButtons("falcon_rw1b");
            } else if(selectedRadioButton.getId() == R.id.radioButtonStable) {
                txtModelDescription.setText(Html.fromHtml(getString(R.string.stable_lm3b_hint), Html.FROM_HTML_MODE_LEGACY));
                handleRadioButtons("stable_lm3b");
            }else if(selectedRadioButton.getId() == R.id.radioButtonPhi) {
                txtModelDescription.setText(Html.fromHtml(getString(R.string.phi2_hint), Html.FROM_HTML_MODE_LEGACY));
                handleRadioButtons("phi2");
            }

            manageModelDownloadable();
        });

        btnDownloadModel = view.findViewById(R.id.btnDownloadModel);
        constraintLayoutProgressBar = view.findViewById(R.id.constraintLayoutProgressBar);
        progressBarDownload = view.findViewById(R.id.progressBarDownload);
        txtPercentage = view.findViewById(R.id.txtPercentage);
        imgMagic = view.findViewById(R.id.imgMagic);

        if(modelExists("gemma2b")) {
            btnDownloadModel.setText(getString(R.string.delete));
            imgMagic.setVisibility(View.VISIBLE);
        }

        btnDownloadModel.setOnClickListener(v -> {
            String name = getSelectedRadioButtonName();

            if(btnDownloadModel.getText().toString().equals(getString(R.string.download))) {
                String fileURL = "http://192.168.1.10:3000/api/download/" + name;
                String filename = name + ".bin";

                downloadFile(fileURL, filename);

                progressBarDownload.setProgress(0);
                txtPercentage.setText("0%");
                constraintLayoutProgressBar.setVisibility(View.VISIBLE);
                txtPercentage.setVisibility(View.VISIBLE);
                btnDownloadModel.setText(getString(R.string.cancel));
                imgMagic.setVisibility(View.GONE);
                handleRadioGroup(false);

                checkDownloadStatus();
            } else if(btnDownloadModel.getText().toString().equals(getString(R.string.cancel))) {
                downloadManager.remove(downloadId);
                constraintLayoutProgressBar.setVisibility(View.GONE);
                txtPercentage.setVisibility(View.GONE);
                btnDownloadModel.setText(getString(R.string.download));
                imgMagic.setVisibility(View.GONE);
                handleRadioGroup(true);
            } else if(btnDownloadModel.getText().toString().equals(getString(R.string.delete))) {
                deleteModel(name);
                btnDownloadModel.setText(R.string.download);
                imgMagic.setVisibility(View.GONE);
                manageModelDownloadable();
            }
        });

        imgMagic.setOnClickListener(v -> {
            String name = getSelectedRadioButtonName();

            handleRadioGroup(false);
            btnDownloadModel.setEnabled(false);
            imgMagic.setImageResource(R.drawable.hourglass);
            imgMagic.setClickable(false);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    InferenceModel im = InferenceModel.getInstance(requireContext(), name);

                    String modelName = im.getModelName();

                    if(!im.getModelPath().contains(name))
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), getString(R.string.another_model, modelName), Toast.LENGTH_SHORT).show());

                    Model model = RingoStarRDF4J.loadModelFromFile(requireContext(), "kg.ttl");

                    String prompt = "Briefly describe the following information as if you were a doctor:\n" + RingoStarRDF4J.promptifyModel(model);
                    String response = "";

                    try {
                        response = im.generateResponse(prompt);
                    } catch(IllegalStateException ignored) {
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), getString(R.string.model_used, modelName), Toast.LENGTH_SHORT).show());
                    }

                    String finalResponse = response;

                    handler.post(() -> {
                        if(!finalResponse.isEmpty()) {
                            new Thread(() -> {
                                DatabaseClient databaseClient = DatabaseClient.getInstance(requireContext());
                                User user = databaseClient.getDatabase().userDAO().getUser();

                                requireActivity().runOnUiThread(() -> {
                                    PDFUtils.createPdf(requireContext(), user, finalResponse);

                                    handleRadioGroup(true);
                                    btnDownloadModel.setEnabled(true);
                                    imgMagic.setImageResource(R.drawable.ic_magic);
                                    imgMagic.setClickable(true);

                                    startActivity(new Intent(requireActivity(), ReportCreatedActivity.class));
                                    requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    requireActivity().finish();
                                });
                            }).start();
                        }
                    });
                } catch(IllegalArgumentException ignored) {
                    handler.post(() -> requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        requireActivity().finish();
                    }));
                }
            });
        });
    }

    public String getSelectedRadioButtonName() {
        int selectedID = radioGroup.getCheckedRadioButtonId();

        String name = "";

        if(selectedID == R.id.radioButtonGemma)
            name = "gemma2b";
        else if(selectedID == R.id.radioButtonFalcon)
            name = "falcon_rw1b";
        else if(selectedID == R.id.radioButtonStable)
            name = "stable_lm3b";
        else if(selectedID == R.id.radioButtonPhi)
            name = "phi2";

        return name;
    }

    public void downloadFile(String fileURL, String filename) {
        downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileURL));

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setAllowedOverRoaming(false);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        downloadId = downloadManager.enqueue(request);
    }

    private void checkDownloadStatus() {
        startTime = System.currentTimeMillis();

        runnable = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);

                Cursor cursor = downloadManager.query(query);

                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    @SuppressLint("Range") int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    @SuppressLint("Range") int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    switch(status) {
                        case DownloadManager.STATUS_RUNNING:
                            int progress = Math.abs((int) ((bytesDownloaded * 100L) / bytesTotal));

                            progressBarDownload.setProgress(progress);

                            if(progress > 100)
                                txtPercentage.setText("100%");
                            else
                                txtPercentage.setText(progress + "%");

                            if((System.currentTimeMillis() - startTime > MAX_WAIT_TIME_MS) && progress == 0) {
                                downloadManager.remove(downloadId);
                                handleDownloadFailed();
                                return;
                            }

                            break;

                        case DownloadManager.STATUS_SUCCESSFUL:
                            constraintLayoutProgressBar.setVisibility(View.GONE);
                            txtPercentage.setVisibility(View.GONE);
                            btnDownloadModel.setText(getString(R.string.delete));
                            imgMagic.setVisibility(View.VISIBLE);
                            handleRadioGroup(true);
                            handler.removeCallbacks(runnable);
                            manageModelDownloadable();
                            return;

                        case DownloadManager.STATUS_FAILED:
                            handleDownloadFailed();
                            return;
                    }
                }

                cursor.close();
                handler.postDelayed(this, 2000);
            }
        };

        handler.post(runnable);
    }

    private void deleteModel(String filename) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File model = new File(dir, filename + ".bin");

        if(!model.delete()) {
            Toast.makeText(requireContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }
    }

    private void handleRadioGroup(boolean status) {
        for(int i=0; i<radioGroup.getChildCount(); i++) {
            View radioButton = radioGroup.getChildAt(i);

            if(radioButton instanceof RadioButton) {
                radioButton.setEnabled(status);
            }
        }
    }

    private void handleRadioButtons(String filename) {
        if(modelExists(filename)) {
            btnDownloadModel.setText(getString(R.string.delete));
            imgMagic.setVisibility(View.VISIBLE);
        } else {
            btnDownloadModel.setText(getString(R.string.download));
            imgMagic.setVisibility(View.GONE);
        }
    }

    private void handleDownloadFailed() {
        constraintLayoutProgressBar.setVisibility(View.GONE);
        txtPercentage.setVisibility(View.GONE);
        btnDownloadModel.setText(getString(R.string.download));
        imgMagic.setVisibility(View.GONE);
        handleRadioGroup(true);
        Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        handler.removeCallbacks(runnable);
    }

    @SuppressLint("SetTextI18n")
    private void manageModelDownloadable() {
        String text;

        if(!modelExists("gemma2b")) {
            text = getString(R.string.gemma2b) + " (<strong>↓<font color='#FF0000'>*</font></strong>)";
            radioButtonGemma.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else
            radioButtonGemma.setText(getString(R.string.gemma2b));

        if(!modelExists("falcon_rw1b")) {
            text = getString(R.string.falcon_rw1b) + " (<strong>↓<font color='#FF0000'>*</font></strong>)";
            radioButtonFalcon.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else
            radioButtonFalcon.setText(getString(R.string.falcon_rw1b));

        if(!modelExists("stable_lm3b")) {
            text = getString(R.string.stable_lm3b) + " (<strong>↓<font color='#FF0000'>*</font></strong>)";
            radioButtonStable.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else
            radioButtonStable.setText(getString(R.string.stable_lm3b));

        if(!modelExists("phi2")) {
            text = getString(R.string.phi2) + " (<strong>↓<font color='#FF0000'>*</font></strong>)";
            radioButtonPhi.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else
            radioButtonPhi.setText(getString(R.string.phi2));
    }

    private boolean modelExists(String filename) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".bin").exists();
    }
}