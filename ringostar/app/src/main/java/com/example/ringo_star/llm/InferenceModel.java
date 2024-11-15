package com.example.ringo_star.llm;

import android.content.Context;
import android.os.Environment;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import java.io.File;

public class InferenceModel {
    private final String modelPath;
    private final LlmInference llmInference;
    private static InferenceModel instance;

    private InferenceModel(Context context, String model) {
        modelPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + model + ".bin";

        if(!modelExists()) {
            throw new IllegalArgumentException("Model not found at path: " + modelPath);
        }

        LlmInference.LlmInferenceOptions options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(1024)
                .build();

        llmInference = LlmInference.createFromOptions(context, options);
    }

    public static synchronized InferenceModel getInstance(Context context, String model) {
        if(instance == null) {
            instance = new InferenceModel(context, model);
        }

        return instance;
    }

    public String generateResponse(String prompt) {
        return llmInference.generateResponse(prompt);
    }

    public String getModelName() {
        String modelName = "";

        if(instance.getModelPath().contains("gemma2b"))
            modelName = "Gemma 2B";
        else if(instance.getModelPath().contains("falcon_rw1b"))
            modelName = "Falcon-RW-1B";
        else if(instance.getModelPath().contains("stable_lm3b"))
            modelName = "StableLM-3B";
        else if(instance.getModelPath().contains("phi2"))
            modelName = "Phi-2";

        return modelName;
    }

    public String getModelPath() {
        return modelPath;
    }

    private boolean modelExists() {
        return new File(modelPath).exists();
    }
}