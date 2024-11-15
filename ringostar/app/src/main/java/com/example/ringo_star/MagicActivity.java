package com.example.ringo_star;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.ringo_star.llm.InferenceModel;

public class MagicActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_magic);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activityMagicLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handler = new Handler();

        radioGroup = findViewById(R.id.radioGroup);

        radioButtonGemma = findViewById(R.id.radioButtonGemma);
        radioButtonFalcon = findViewById(R.id.radioButtonFalcon);
        radioButtonStable = findViewById(R.id.radioButtonStable);
        radioButtonPhi = findViewById(R.id.radioButtonPhi);

        txtModelDescription = findViewById(R.id.txtModelDescription);
        txtModelDescription.setText(Html.fromHtml(txtModelDescription.getText().toString(), Html.FROM_HTML_MODE_LEGACY));

        manageModelDownloadable();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);

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

        btnDownloadModel = findViewById(R.id.btnDownloadModel);
        constraintLayoutProgressBar = findViewById(R.id.constraintLayoutProgressBar);
        progressBarDownload = findViewById(R.id.progressBarDownload);
        txtPercentage = findViewById(R.id.txtPercentage);
        imgMagic = findViewById(R.id.imgMagic);

        if(modelExists("gemma2b")) {
            btnDownloadModel.setText(getString(R.string.delete));
            imgMagic.setVisibility(View.VISIBLE);
        }

        btnDownloadModel.setOnClickListener(v -> {
            String name = getSelectedRadioButtonName();

            if(btnDownloadModel.getText().toString().equals(getString(R.string.download))) {
                String fileURL = "http://172.20.10.2:3000/api/download/" + name;
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
            imgMagic.setImageResource(R.drawable.ic_loading);
            imgMagic.setClickable(false);

            ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(imgMagic, "rotation", 0f, 360f);
            rotateAnimation.setDuration(1000);
            rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.start();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    InferenceModel im = InferenceModel.getInstance(MagicActivity.this, name);

                    String modelName = im.getModelName();

                    if(!im.getModelPath().contains(name))
                        runOnUiThread(() -> Toast.makeText(MagicActivity.this, getString(R.string.another_model, modelName), Toast.LENGTH_SHORT).show());

                    String response = "";

                    try {
                        response = im.generateResponse("Hello, how are you?");
                    } catch(IllegalStateException ignored) {
                        runOnUiThread(() -> Toast.makeText(MagicActivity.this, getString(R.string.model_used, modelName), Toast.LENGTH_SHORT).show());
                    }

                    String finalResponse = response;

                    handler.post(() -> {
                        if(!finalResponse.isEmpty())
                            System.out.println("**********************" + finalResponse);
                    });
                } catch(IllegalArgumentException ignored) {
                    handler.post(() -> {
                        runOnUiThread(() -> {
                            Toast.makeText(MagicActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
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
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileURL));

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
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
            Toast.makeText(MagicActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            finish();
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
        Toast.makeText(MagicActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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