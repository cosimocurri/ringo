package com.example.ringo_star.questionnaire;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ringo_star.R;
import com.example.ringo_star.data.DatabaseClient;
import com.example.ringo_star.data.entity.User;
import com.example.ringo_star.enumeration.Gender;
import com.example.ringo_star.utils.RingoStarRDF4J;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Values;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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
                String waistCircumference = waistCircumferenceSpinner.getSelectedItem().toString();
                String activity30 = activity30spinner.getSelectedItem().toString();
                String fruitVegetable = fruitVegetableSpinner.getSelectedItem().toString();
                String bloodPressureMedication = bloodPressureMedicationSpinner.getSelectedItem().toString();
                String highBloodSugarLevel = highBloodSugarLevelSpinner.getSelectedItem().toString();
                String familyMembersDiabete = familyMembersDiabeteSpinner.getSelectedItem().toString();

                new Thread(() -> {
                    DatabaseClient databaseClient = DatabaseClient.getInstance(getApplicationContext());
                    User user = databaseClient.getDatabase().userDAO().getUser();

                    int age = Period.between(user.getBirthday(), LocalDate.now()).getYears();
                    int height = user.getHeight();
                    float bmi = (float) user.getWeight() / (height * height) * 10000;

                    int points = 0;

                    if(age < 45)
                        points += 0;
                    else if(age >= 45 && age <= 54)
                        points += 2;
                    else if(age >= 55 && age <= 64)
                        points += 3;
                    else
                        points += 4;

                    if(bmi < 25)
                        points += 0;
                    else if(bmi >= 25 && bmi <= 30)
                        points += 1;
                    else
                        points += 2;

                    if(user.getGender() == Gender.MALE) {
                        switch(waistCircumference) {
                            case "Under 94 cm":
                                points += 0;
                                break;

                            case "94–102 cm":
                                points += 3;
                                break;

                            case "Over 102 cm":
                                points += 4;
                                break;
                        }
                    } else {
                        switch(waistCircumference) {
                            case "Under 80 cm":
                                points += 0;
                                break;

                            case "80–88 cm":
                                points += 3;
                                break;

                            case "Over 88 cm":
                                points += 4;
                                break;
                        }
                    }

                    switch(activity30) {
                        case "No":
                            points += 2;
                            break;

                        case "Yes":
                            points += 0;
                            break;
                    }

                    switch(fruitVegetable) {
                        case "Every day":
                            points += 0;
                            break;

                        case "Not every day":
                            points += 1;
                            break;
                    }

                    switch(bloodPressureMedication) {
                        case "No":
                            points += 0;
                            break;

                        case "Yes":
                            points += 2;
                            break;
                    }

                    switch(highBloodSugarLevel) {
                        case "No":
                            points += 0;
                            break;

                        case "Yes":
                            points += 5;
                            break;
                    }

                    switch(familyMembersDiabete) {
                        case "No":
                            points += 0;
                            break;

                        case "Yes, but not parents, siblings or children":
                            points += 3;
                            break;

                        case "Yes, including parents, siblings or children":
                            points += 5;
                            break;
                    }

                    String result = "";

                    if(points < 7)
                        result = "The risk of developing diabetes is low (1 in 100 chance)";
                    else if(points >= 7 && points <= 11)
                        result = "The risk of developing diabetes is slightly high (1 in 25 chance)";
                    else if(points >= 12 && points <= 14)
                        result = "The risk of developing diabetes is moderate (1 in 6 chance)";
                    else if(points >= 15 && points <= 20)
                        result = "The risk of developing diabetes is high (1 in 3 chance)";
                    else
                        result = "The risk of developing diabetes is very high (1 in 2 chance)";

                    Model model = RingoStarRDF4J.loadModelFromFile(getApplicationContext(), "kg.ttl");

                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedToday = today.format(formatter);

                    IRI nodeQuestionnaireIRI = Values.iri(RingoStarRDF4J.nodeNS, "questionnaire" + UUID.randomUUID());

                    model.add(RingoStarRDF4J.nodeUserIRI, RingoStarRDF4J.relationCompile, nodeQuestionnaireIRI);
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyName, Values.literal("FINDRISC"));
                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.propertyDate, Values.literal(formattedToday));

                    IRI nodeFirstQuestionIRI = Values.iri(RingoStarRDF4J.nodeNS, "question" + UUID.randomUUID());
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyText, Values.literal("What's your risk of developing diabetes?"));
                    model.add(nodeFirstQuestionIRI, RingoStarRDF4J.propertyValue, Values.literal(result));

                    model.add(nodeQuestionnaireIRI, RingoStarRDF4J.relationHas, nodeFirstQuestionIRI);

                    new Thread(() -> {
                        RingoStarRDF4J.saveModelToFile(model, getApplicationContext(), "kg.ttl");

                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Questionnaire submitted", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }).start();
                }).start();
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