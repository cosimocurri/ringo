package com.example.ringo_star.questionnaire;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ringo_star.R;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SymptomsComplicationsQuestionnaire extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_symptoms_complications_questionnaire);

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

        MaterialSwitch switchChangeVision = findViewById(R.id.switchChangeVision);
        MaterialSwitch switchInfections = findViewById(R.id.switchInfections);
        MaterialSwitch switchLossSensation = findViewById(R.id.switchLossSensation);
        MaterialSwitch switchWoundsFeet = findViewById(R.id.switchWoundsFeet);
        MaterialSwitch switchJoints = findViewById(R.id.switchJoints);

        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean changeVision = switchChangeVision.isChecked();
                boolean infections = switchInfections.isChecked();
                boolean lossSensation = switchLossSensation.isChecked();
                boolean woundsFeet = switchWoundsFeet.isChecked();
                boolean joints = switchJoints.isChecked();
            }
        });
    }
}