package com.example.ringo_star;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ringo_star.data.DatabaseClient;
import com.example.ringo_star.data.entity.User;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.motionLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Thread(() -> {
            DatabaseClient databaseClient = DatabaseClient.getInstance(getApplicationContext());
            User user = databaseClient.getDatabase().userDAO().getUser();

            runOnUiThread(() -> new Handler().postDelayed(() -> {
                Class<?> nextActivity = (user == null) ? UserInfoActivity.class : PinActivity.class;
                startActivity(new Intent(MainActivity.this, nextActivity));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }, 3000));
        }).start();
    }
}