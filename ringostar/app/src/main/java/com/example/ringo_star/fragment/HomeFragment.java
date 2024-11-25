package com.example.ringo_star.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ringo_star.MainActivity;
import com.example.ringo_star.R;
import com.example.ringo_star.UserInfoActivity;
import com.example.ringo_star.components.QuestionnaireBoxComponent;
import com.example.ringo_star.questionnaire.HowAreYouQuestionnaire;

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

        questionnaireGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(HowAreYouQuestionnaire.class);
            }
        });

        return view;
    }

    private void handleButtonClick(Class classActivity) {
        startActivity(new Intent(requireContext(), classActivity));
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        requireActivity().finish();
    }
}