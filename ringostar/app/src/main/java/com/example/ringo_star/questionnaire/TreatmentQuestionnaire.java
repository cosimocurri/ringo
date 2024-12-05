package com.example.ringo_star.questionnaire;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ringo_star.R;
import com.google.android.material.materialswitch.MaterialSwitch;

public class TreatmentQuestionnaire extends AppCompatActivity {
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_treatment_questionnaire);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ImageView imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MaterialSwitch switchAllMedicine = findViewById(R.id.switchAllMedicine);
        MaterialSwitch switchDifficultSchedule = findViewById(R.id.switchDifficultSchedule);
        EditText txtSideEffects = findViewById(R.id.txtSideEffects);
        MaterialSwitch switchChangeInsulin = findViewById(R.id.switchChangeInsulin);
        MaterialSwitch switchMonitorGlucose = findViewById(R.id.switchMonitorGlucose);

        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allMedicine = switchAllMedicine.isChecked();
                boolean difficultSchedule = switchDifficultSchedule.isChecked();
                String sideEffects = txtSideEffects.getText().toString();
                boolean changeInsulin = switchChangeInsulin.isChecked();
                boolean monitorGlucose = switchMonitorGlucose.isChecked();

                if(sideEffects.isEmpty()) {
                    if(vibrator.hasVibrator()) {
                        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.vibrate(vibrationEffect);
                    }

                    ConstraintLayout main = findViewById(R.id.main);

                    Animation shake = AnimationUtils.loadAnimation(TreatmentQuestionnaire.this, R.anim.shake);
                    main.startAnimation(shake);

                    Toast.makeText(getApplicationContext(), R.string.toast_all_fields_requires, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}