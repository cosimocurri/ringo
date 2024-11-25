package com.example.ringo_star.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.ringo_star.R;

public class QuestionnaireBoxComponent extends LinearLayout {
    private TextView txtTitle;
    private TextView txtDescription;
    private ImageView imgIcon;

    public QuestionnaireBoxComponent(Context context) {
        super(context);
        init(context, null);
    }

    public QuestionnaireBoxComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public QuestionnaireBoxComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.questionnaire_box, this, true);

        txtTitle = findViewById(R.id.titleTextView);
        txtDescription = findViewById(R.id.descriptionTextView);
        imgIcon = findViewById(R.id.iconImageView);

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.QuestionnaireBoxComponent, 0, 0);

            try {
                String title = typedArray.getString(R.styleable.QuestionnaireBoxComponent_titleText);
                String description = typedArray.getString(R.styleable.QuestionnaireBoxComponent_descriptionText);
                Drawable icon = typedArray.getDrawable(R.styleable.QuestionnaireBoxComponent_iconSrc);

                setTitle(title != null ? title : "...");
                setDescription(description != null ? description : "...");
                setIcon(icon);
            } finally {
                typedArray.recycle();
            }
        }
    }

    public void setTitle(String title) {
        txtTitle.setText(title);
    }

    public void setDescription(String description) {
        txtDescription.setText(description);
    }

    public void setIcon(Drawable drawable) {
        if(drawable != null)
            imgIcon.setImageDrawable(drawable);
        else
            imgIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_launcher_foreground));
    }
}