package com.example.ringo_star.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ringo_star.R;
import com.example.ringo_star.components.QuestionnaireBoxComponent;
import com.example.ringo_star.questionnaire.CardiovascularRiskQuestionnaire;
import com.example.ringo_star.questionnaire.FINDRISCQuestionnaire;
import com.example.ringo_star.questionnaire.HbA1cQuestionnaire;
import com.example.ringo_star.questionnaire.HowAreYouQuestionnaire;
import com.example.ringo_star.questionnaire.LifestyleQuestionnaire;
import com.example.ringo_star.questionnaire.SymptomsComplicationsQuestionnaire;
import com.example.ringo_star.questionnaire.TreatmentQuestionnaire;

public class HomeFragment extends Fragment {
    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        QuestionnaireBoxComponent questionnaireGeneral = view.findViewById(R.id.questionnaireGeneral);
        QuestionnaireBoxComponent questionnaireTreatment = view.findViewById(R.id.questionnaireTreatment);
        QuestionnaireBoxComponent questionnaireLifestyle = view.findViewById(R.id.questionnaireLifestyle);
        QuestionnaireBoxComponent questionnaireSymptoms = view.findViewById(R.id.questionnaireSymptoms);
        QuestionnaireBoxComponent questionnaireHbA1c = view.findViewById(R.id.questionnaireHbA1c);
        QuestionnaireBoxComponent questionnaireCardiovascularRisk = view.findViewById(R.id.questionnaireCardiovascularRisk);
        QuestionnaireBoxComponent questionnaireFINDRISC = view.findViewById(R.id.questionnaireFINDRISC);

        questionnaireGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(HowAreYouQuestionnaire.class);
            }
        });

        questionnaireTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(TreatmentQuestionnaire.class);
            }
        });

        questionnaireLifestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(LifestyleQuestionnaire.class);
            }
        });

        questionnaireSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(SymptomsComplicationsQuestionnaire.class);
            }
        });

        questionnaireHbA1c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(HbA1cQuestionnaire.class);
            }
        });

        questionnaireCardiovascularRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(CardiovascularRiskQuestionnaire.class);
            }
        });

        questionnaireFINDRISC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(FINDRISCQuestionnaire.class);
            }
        });

        return view;
    }

    private void handleButtonClick(Class classActivity) {
        startActivity(new Intent(requireContext(), classActivity));
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}