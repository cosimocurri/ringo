package com.example.ringo_star.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ringo_star.R;
import com.example.ringo_star.data.DatabaseClient;
import com.example.ringo_star.data.entity.User;

import java.time.format.DateTimeFormatter;

public class ProfileFragment extends Fragment {
    TextView lblName;
    TextView lblGender;
    TextView lblBirthday;
    TextView lblHeight;
    TextView lblWeight;
    TextView lblBloodGroup;
    TextView lblSmoke;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lblName = view.findViewById(R.id.lblName);
        lblGender = view.findViewById(R.id.lblGender);
        lblBirthday = view.findViewById(R.id.lblBirthday);
        lblHeight = view.findViewById(R.id.lblHeight);
        lblWeight = view.findViewById(R.id.lblWeight);
        lblBloodGroup = view.findViewById(R.id.lblBloodGroup);
        lblSmoke = view.findViewById(R.id.lblSmoke);

        new Thread(() -> {
            DatabaseClient databaseClient = DatabaseClient.getInstance(requireContext());
            User user = databaseClient.getDatabase().userDAO().getUser();

            String name = user.getFirstname() + " " + user.getLastname();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String birthday = user.getBirthday().format(formatter);

            String bloodGroup = user.getBloodGroup().toString()
                    .replace("zero", "0")
                    .replace("_positive", "+")
                    .replace("_negative", "-");

            String height = user.getHeight() + " cm";
            String weight = user.getWeight() + " kg";
            String smoke = user.isSmoke() ? "Smoke" : "Doesn't smoke";

            lblName.setText(name);
            lblGender.setText(capitalizeFirstLetter(user.getGender().toString()));
            lblBirthday.setText(birthday);
            lblHeight.setText(height);
            lblWeight.setText(weight);
            lblBloodGroup.setText(bloodGroup);
            lblSmoke.setText(smoke);
        }).start();
    }

    public static String capitalizeFirstLetter(String input) {
        if(input == null || input.isEmpty()) {
            return input;
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}