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
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ringo_star.R;
import com.example.ringo_star.utils.RingoStarRDF4J;
import com.google.android.material.materialswitch.MaterialSwitch;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Values;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class HowAreYouQuestionnaire extends AppCompatActivity {
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_how_are_you_questionnaire);

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

        SeekBar seekFeel = findViewById(R.id.seekFeel);
        MaterialSwitch switchLackEnergy = findViewById(R.id.switchLackEnergy);
        MaterialSwitch switchMentalConfusion = findViewById(R.id.switchMentalConfusion);
        SeekBar seekStressed = findViewById(R.id.seekStressed);
        EditText txtBloodSugar = findViewById(R.id.txtCholesterol);
        EditText txtSystolicPressure = findViewById(R.id.txtSystolicPressure);
        EditText txtDiastolicPressure = findViewById(R.id.txtDiastolicPressure);
        EditText txtPhysicalActivity = findViewById(R.id.txtPhysicalActivity);
        MaterialSwitch switchMeals = findViewById(R.id.switchMeals);
        MaterialSwitch switchSugar = findViewById(R.id.switchSugar);

        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int feelValue = seekFeel.getProgress();
                String[] feelsLevels = {"Very bad", "Bad", "Neutral", "Good", "Very good"};
                String feel = feelsLevels[feelValue];

                boolean lackEnergy = switchLackEnergy.isChecked();
                boolean mentalConfusion = switchMentalConfusion.isChecked();

                int stressedValue = seekStressed.getProgress();
                String[] stressLevels = {"Not stressed at all", "Slightly stressed", "Moderately stressed", "Very stressed"};
                String stressed = stressLevels[stressedValue];

                String bloodSugar = txtBloodSugar.getText().toString() + " mg/dL";
                String pressure = txtSystolicPressure.getText().toString() + "/" + txtDiastolicPressure.getText().toString() + " mmHg";
                String physicalActivity = txtPhysicalActivity.getText().toString() + " minutes";

                boolean mealsRegularly = switchMeals.isChecked();
                boolean sugaryConsumed = switchSugar.isChecked();

                if(
                    txtBloodSugar.getText().toString().isEmpty() ||
                    txtSystolicPressure.getText().toString().isEmpty() ||
                    txtDiastolicPressure.getText().toString().isEmpty() ||
                    txtPhysicalActivity.getText().toString().isEmpty()
                ) {
                    if(vibrator.hasVibrator()) {
                        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.vibrate(vibrationEffect);
                    }

                    ConstraintLayout main = findViewById(R.id.main);

                    Animation shake = AnimationUtils.loadAnimation(HowAreYouQuestionnaire.this, R.anim.shake);
                    main.startAnimation(shake);

                    Toast.makeText(getApplicationContext(), R.string.toast_all_fields_requires, Toast.LENGTH_SHORT).show();
                } else {
                    Model model = RingoStarRDF4J.loadModelFromFile(getApplicationContext(), "kg.ttl");

                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedToday = today.format(formatter);

                    IRI nodeQuestionnaireIRI = Values.iri(RingoStarRDF4J.nodeNS, "questionnaire" + UUID.randomUUID());

                    model.add(RingoStarRDF4J.nodeUserIRI, RingoStarRDF4J.relationCompile, nodeQuestionnaireIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyName, Values.literal("How are you today?"));
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyDate, Values.literal(formattedToday));

                    IRI nodeFirstQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("How do you feel?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(feel));

                    IRI nodeSecondQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeSecondQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Do you notice a lack of energy?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(lackEnergy ? "Yes" : "No"));

                    IRI nodeThirdQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeThirdQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Do you notice mental confusion?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(mentalConfusion ? "Yes" : "No"));

                    IRI nodeFourthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeFourthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("How stressed do you feel?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(stressed));

                    IRI nodeFifthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeFifthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Blood sugar"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(bloodSugar));

                    IRI nodeSixthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeSixthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Pressure"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(pressure));

                    IRI nodeSeventhQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeSeventhQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Physical activity"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(physicalActivity));

                    IRI nodeEighthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeEighthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Do you eat meals regularly?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(mealsRegularly ? "Yes" : "No"));

                    IRI nodeNinthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeNinthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Have you consumed sweets of sugary drinks?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(sugaryConsumed ? "Yes" : "No"));

                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFirstQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeSecondQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeThirdQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFourthQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFifthQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeSixthQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeSeventhQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeEighthQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeNinthQuestionIRI);

                    new Thread(() -> {
                        RingoStarRDF4J.saveModelToFile(model, getApplicationContext(), "kg.ttl");

                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Questionnaire submitted", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }).start();
                }
            }
        });
    }
}