package com.example.ringo_star.questionnaire;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ringo_star.R;
import com.example.ringo_star.data.DatabaseClient;
import com.example.ringo_star.data.entity.User;

import java.time.format.DateTimeFormatter;

public class FINDRISCQuestionnaire extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_findriscquestionnaire);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Spinner waistCircumferenceSpinner = findViewById(R.id.waistCircumferenceSpinner);
        Spinner activity30spinner = findViewById(R.id.activity30spinner);
        Spinner fruitVegetableSpinner = findViewById(R.id.fruitVegetableSpinner);
        Spinner bloodPressureMedicationSpinner = findViewById(R.id.bloodPressureMedicationSpinner);
        Spinner highBloodSugarLevelSpinner = findViewById(R.id.highBloodSugarLevelSpinner);
        Spinner familyMembersDiabeteSpinner = findViewById(R.id.familyMembersDiabeteSpinner);

        new Thread(() -> {
            DatabaseClient databaseClient = DatabaseClient.getInstance(getApplicationContext());
            User user = databaseClient.getDatabase().userDAO().getUser();

            String gender = user.getGender().toString();

            switch(gender) {
                case "MALE":
                    setupSpinner(waistCircumferenceSpinner, R.array.waistCircumferenceArrayMale);
                    break;

                case "FEMALE":
                    setupSpinner(waistCircumferenceSpinner, R.array.waistCircumferenceArrayFemale);
                    break;
            }
        }).start();

        setupSpinner(activity30spinner, R.array.activity30Array);
        setupSpinner(fruitVegetableSpinner, R.array.fruitVegetableArray);
        setupSpinner(bloodPressureMedicationSpinner, R.array.bloodPressureMedicationArray);
        setupSpinner(highBloodSugarLevelSpinner, R.array.highBloodSugarLevelArray);
        setupSpinner(familyMembersDiabeteSpinner, R.array.familyMembersDiabeteArray);

        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void setupSpinner(Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        this,
                arrayResourceId,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}