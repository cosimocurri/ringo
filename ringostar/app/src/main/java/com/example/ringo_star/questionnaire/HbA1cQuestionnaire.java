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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Values;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class HbA1cQuestionnaire extends AppCompatActivity {
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_hb_a1c_questionnaire);

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
        
        EditText txtAvgGlucose = findViewById(R.id.txtAvgGlucose);
        EditText txtHypoglycemia = findViewById(R.id.txtHypoglycemia);
        EditText txtHyperglycemia = findViewById(R.id.txtHyperglycemia);
        EditText txtMeasure = findViewById(R.id.txtMeasure);

        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String avgGlucose = txtAvgGlucose.getText().toString() + " mmol/L";
                String hypoglycemia = txtHypoglycemia.getText().toString();
                String hyperglycemia = txtHyperglycemia.getText().toString();
                String measure = txtMeasure.getText().toString() + " mmol/mol";

                if(
                    txtAvgGlucose.getText().toString().isEmpty() ||
                    hypoglycemia.isEmpty() ||
                    hyperglycemia.isEmpty() ||
                    txtMeasure.getText().toString().isEmpty()
                ) {
                    if(vibrator.hasVibrator()) {
                        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.vibrate(vibrationEffect);
                    }

                    ConstraintLayout main = findViewById(R.id.main);

                    Animation shake = AnimationUtils.loadAnimation(HbA1cQuestionnaire.this, R.anim.shake);
                    main.startAnimation(shake);

                    Toast.makeText(getApplicationContext(), R.string.toast_all_fields_requires, Toast.LENGTH_SHORT).show();
                } else {
                    Model model = RingoStarRDF4J.loadModelFromFile(getApplicationContext(), "kg.ttl");

                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedToday = today.format(formatter);

                    IRI nodeQuestionnaireIRI = Values.iri(RingoStarRDF4J.nodeNS, "questionnaire" + UUID.randomUUID());

                    model.add(RingoStarRDF4J.nodeUserIRI, RingoStarRDF4J.relationCompile, nodeQuestionnaireIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyName, Values.literal("HbA1c"));
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyDate, Values.literal(formattedToday));

                    IRI nodeFirstQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Average fasting blood glucose"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(avgGlucose));

                    IRI nodeSecondQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeSecondQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("How many episodes of hypoglycemia did you have last month?"));
                    model.add(nodeSecondQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(hypoglycemia));

                    IRI nodeThirdQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeThirdQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("How many episodes of hyperglycemia did you have last month?"));
                    model.add(nodeThirdQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(hyperglycemia));

                    IRI nodeFourthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeFourthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("HbA1c"));
                    model.add(nodeFourthQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(measure));

                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFirstQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeSecondQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeThirdQuestionIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFourthQuestionIRI);

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