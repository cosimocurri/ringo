package com.example.ringo_star;

import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.ringo_star.fragment.HomeFragment;
import com.example.ringo_star.fragment.MagicFragment;
import com.example.ringo_star.fragment.ProfileFragment;
import com.example.ringo_star.fragment.SettingsFragment;

import io.ak1.BubbleTabBar;
import io.ak1.OnBubbleClickListener;

public class HomeActivity extends AppCompatActivity {
    BubbleTabBar bubbleTabBar;

    Fragment homeFragment, magicFragment, profileFragment, settingsFragment, activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_home);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeFragment = new HomeFragment();
        magicFragment = new MagicFragment();
        profileFragment = new ProfileFragment();
        settingsFragment = new SettingsFragment();

        activeFragment = homeFragment;

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, magicFragment, "MagicFragment").hide(magicFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, profileFragment, "ProfileFragment").hide(profileFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, settingsFragment, "SettingsFragment").hide(settingsFragment).commit();

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, homeFragment, "HomeFragment").commit();

        bubbleTabBar = findViewById(R.id.bubbleTabBar);

        bubbleTabBar.addBubbleListener(new OnBubbleClickListener() {
            @Override
            public void onBubbleClick(int id) {
                if(id == R.id.home)
                    switchFragment(homeFragment);
                else if(id == R.id.magic)
                    switchFragment(magicFragment);
                else if(id == R.id.profile)
                    switchFragment(profileFragment);
                else if(id == R.id.settings)
                    switchFragment(settingsFragment);
            }
        });
    }

    private void switchFragment(Fragment targetFragment) {
        if(activeFragment != targetFragment) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(activeFragment).show(targetFragment).commit();
            activeFragment = targetFragment;
        }
    }
}