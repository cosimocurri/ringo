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
import com.example.ringo_star.utils.RingoStarRDF4J;
import com.google.android.material.materialswitch.MaterialSwitch;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Values;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class LifestyleQuestionnaire extends AppCompatActivity {
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_lifestyle_questionnaire);

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

        EditText txtNumberMeals = findViewById(R.id.txtNumberMeals);
        EditText txtSugaryFoods = findViewById(R.id.txtSugaryFoods);
        MaterialSwitch switchDietChanged = findViewById(R.id.switchDietChanged);
        EditText txtNumberExercises = findViewById(R.id.txtNumberExercises);
        MaterialSwitch switchSleep = findViewById(R.id.switchSleep);
        MaterialSwitch switchStress = findViewById(R.id.switchStress);

        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberMeals = txtNumberMeals.getText().toString();
                String sugaryFoods = txtSugaryFoods.getText().toString();
                boolean dietChanged = switchDietChanged.isChecked();
                String numberExercises = txtNumberExercises.getText().toString();
                boolean sleep = switchSleep.isChecked();
                boolean stress = switchStress.isChecked();

                if(
                    numberMeals.isEmpty() ||
                    sugaryFoods.isEmpty() ||
                    numberExercises.isEmpty()
                ) {
                    if(vibrator.hasVibrator()) {
                        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.vibrate(vibrationEffect);
                    }

                    ConstraintLayout main = findViewById(R.id.main);

                    Animation shake = AnimationUtils.loadAnimation(LifestyleQuestionnaire.this, R.anim.shake);
                    main.startAnimation(shake);

                    Toast.makeText(getApplicationContext(), R.string.toast_all_fields_requires, Toast.LENGTH_SHORT).show();
                } else {
                    Model model = RingoStarRDF4J.loadModelFromFile(getApplicationContext(), "kg.ttl");

                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedToday = today.format(formatter);

                    IRI nodeQuestionnaireIRI = Values.iri(RingoStarRDF4J.nodeNS, "questionnaire" + UUID.randomUUID());

                    model.add(RingoStarRDF4J.nodeUserIRI, RingoStarRDF4J.relationCompile, nodeQuestionnaireIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyName, Values.literal("Lifestyle"));
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyDate, Values.literal(formattedToday));

                    IRI nodeFirstQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("How many meals do you eat per day?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(numberMeals));

                    IRI nodeSecondQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeSecondQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("How often do you eat sugary foods?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(sugaryFoods));

                    IRI nodeThirdQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeThirdQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Have you made any changes to your diet since the last questionnaire?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(dietChanged ? "Yes" : "No"));

                    IRI nodeFourthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeFourthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("How often do you work out?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(numberExercises));

                    IRI nodeFifthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeFifthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Do you sleep at least 7 hours per night?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(sleep ? "Yes" : "No"));

                    IRI nodeSixthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeSixthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Have you experienced significant periods of stress recently?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(stress ? "Yes" : "No"));

                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFirstQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeSecondQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeThirdQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFourthQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFifthQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeSixthQuestionIRI);

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