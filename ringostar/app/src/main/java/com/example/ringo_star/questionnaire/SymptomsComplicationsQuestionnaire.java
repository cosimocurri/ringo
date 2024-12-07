package com.example.ringo_star.questionnaire;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

                Model model = RingoStarRDF4J.loadModelFromFile(getApplicationContext(), "kg.ttl");

                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedToday = today.format(formatter);

                IRI nodeQuestionnaireIRI = Values.iri(RingoStarRDF4J.nodeNS, "questionnaire" + UUID.randomUUID());

                model.add(RingoStarRDF4J.nodeUserIRI, RingoStarRDF4J.relationCompile, nodeQuestionnaireIRI);
                model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyName, Values.literal("Symptoms and complications"));
                model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyDate, Values.literal(formattedToday));

                IRI nodeFirstQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Have you noticed any changes in your vision?"));
                model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(changeVision ? "Yes" : "No"));

                IRI nodeSecondQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                model.add(nodeSecondQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Have you had recurrent infections in the past month?"));
                model.add(nodeSecondQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(infections ? "Yes" : "No"));

                IRI nodeThirdQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                model.add(nodeThirdQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Have you experienced pain, tingling, or loss of sensation in your feet?"));
                model.add(nodeThirdQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(lossSensation ? "Yes" : "No"));

                IRI nodeFourthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                model.add(nodeFourthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Have you observed wounds or ulcers on your feet that are not healing?"));
                model.add(nodeFourthQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(woundsFeet ? "Yes" : "No"));

                IRI nodeFifthQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                model.add(nodeFifthQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("Have you noticed swelling or pain in your joints?"));
                model.add(nodeFifthQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(joints ? "Yes" : "No"));

                model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFirstQuestionIRI);
                model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeSecondQuestionIRI);
                model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeThirdQuestionIRI);
                model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFourthQuestionIRI);
                model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFifthQuestionIRI);

                new Thread(() -> {
                    RingoStarRDF4J.saveModelToFile(model, getApplicationContext(), "kg.ttl");

                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Questionnaire submitted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }).start();
            }
        });
    }
}