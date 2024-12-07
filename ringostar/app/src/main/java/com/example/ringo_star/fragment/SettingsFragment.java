package com.example.ringo_star.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ringo_star.R;
import com.example.ringo_star.UserInfoActivity;
import com.example.ringo_star.data.DatabaseClient;
import com.example.ringo_star.data.dao.UserDAO;

import java.io.File;

public class SettingsFragment extends Fragment {
    Button btnUpdateProfile;
    Button btnCloseApp;
    Button btnDeleteAll;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnCloseApp = view.findViewById(R.id.btnCloseApp);
        btnDeleteAll = view.findViewById(R.id.btnDeleteAll);

        btnUpdateProfile.setBackgroundResource(R.drawable.button_settings);
        btnUpdateProfile.setBackgroundTintList(null);

        btnCloseApp.setBackgroundResource(R.drawable.button_settings);
        btnCloseApp.setBackgroundTintList(null);

        btnDeleteAll.setBackgroundResource(R.drawable.button_settings_alert);
        btnDeleteAll.setBackgroundTintList(null);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), UserInfoActivity.class));
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                requireActivity().finish();
            }
        });

        btnCloseApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finishAffinity();
                System.exit(0);
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(() -> {
                    DatabaseClient databaseClient = DatabaseClient.getInstance(requireContext());
                    UserDAO userDAO = databaseClient.getDatabase().userDAO();

                    requireActivity().runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Are you sure?");

                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            btnUpdateProfile.setEnabled(false);
                            btnCloseApp.setEnabled(false);
                            btnDeleteAll.setEnabled(false);

                            Toast.makeText(requireContext(), "Please wait...", Toast.LENGTH_SHORT).show();

                            File file = new File(requireContext().getFilesDir(), "kg.ttl");
                            file.delete();

                            new Thread(userDAO::delete).start();

                            dialog.dismiss();

                            requireActivity().runOnUiThread(() -> new Handler().postDelayed(() -> {
                                startActivity(new Intent(requireContext(), UserInfoActivity.class));
                                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                requireActivity().finish();
                            }, 3000));
                        });

                        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                }).start();
            }
        });
    }
}