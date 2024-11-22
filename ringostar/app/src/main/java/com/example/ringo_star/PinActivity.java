package com.example.ringo_star;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ringo_star.data.DatabaseClient;
import com.example.ringo_star.data.entity.User;
import com.example.ringo_star.utils.PinUtils;

public class PinActivity extends AppCompatActivity {
    ConstraintLayout activityPinLayout;
    Window window;
    Vibrator vibrator;

    ConstraintLayout txtNumberLayout;
    ConstraintLayout txtNumberLayoutContainer;
    EditText txtNumber1;
    EditText txtNumber2;
    EditText txtNumber3;
    EditText txtNumber4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_pin);

        window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activityPinLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        activityPinLayout = findViewById(R.id.activityPinLayout);

        txtNumberLayout = findViewById(R.id.txtNumberLayout);
        txtNumberLayoutContainer = findViewById(R.id.txtNumberLayoutContainer);
        txtNumber1 = findViewById(R.id.txtNumber1);
        txtNumber2 = findViewById(R.id.txtNumber2);
        txtNumber3 = findViewById(R.id.txtNumber3);
        txtNumber4 = findViewById(R.id.txtNumber4);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        txtNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 1) {
                    txtNumber2.requestFocus();
                }
            }
        });

        txtNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 1) {
                    txtNumber3.requestFocus();
                }
            }
        });

        txtNumber2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if(i == KeyEvent.KEYCODE_DEL && txtNumber2.getText().length() == 0) {
                        txtNumber1.requestFocus();
                        return true;
                    }
                }

                return false;
            }
        });

        txtNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 1){
                    txtNumber4.requestFocus();
                }
            }
        });

        txtNumber3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if(i == KeyEvent.KEYCODE_DEL && txtNumber3.getText().length() == 0) {
                        txtNumber2.requestFocus();
                        return true;
                    }
                }

                return false;
            }
        });

        txtNumber4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 1) {
                    new Thread(() -> {
                        DatabaseClient databaseClient = DatabaseClient.getInstance(getApplicationContext());
                        User user = databaseClient.getDatabase().userDAO().getUser();

                        String hashedPIN = user.getHashedPin();
                        byte[] salt = user.getSalt();

                        String clearInputPIN = txtNumber1.getText().toString() + txtNumber2.getText().toString() + txtNumber3.getText().toString() + txtNumber4.getText().toString();

                        try {
                            String hashedInputPIN = PinUtils.hashPin(clearInputPIN, salt);

                            if(hashedInputPIN.equals(hashedPIN)) {
                                startActivity(new Intent(PinActivity.this, HomeActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                if(vibrator.hasVibrator()) {
                                    VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                                    vibrator.vibrate(vibrationEffect);
                                }

                                runOnUiThread(() -> {
                                    Animation shake = AnimationUtils.loadAnimation(PinActivity.this, R.anim.shake);
                                    txtNumberLayout.startAnimation(shake);

                                    txtNumber1.setText("");
                                    txtNumber2.setText("");
                                    txtNumber3.setText("");
                                    txtNumber4.setText("");

                                    txtNumber1.requestFocus();
                                });
                            }
                        } catch(Exception ignored) {}
                    }).start();
                }
            }
        });

        txtNumber4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if(i == KeyEvent.KEYCODE_DEL && txtNumber4.getText().length() == 0) {
                        txtNumber3.requestFocus();
                        return true;
                    }
                }

                return false;
            }
        });
    }
}