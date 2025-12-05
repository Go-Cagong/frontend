// MainActivity.java

package com.cookandroid.gocafestudy.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.cookandroid.gocafestudy.fragments.MapFragment;
import com.cookandroid.gocafestudy.fragments.MyFragment;
import com.cookandroid.gocafestudy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.cookandroid.gocafestudy.utils.UserSessionManager; // 🌟 유저 정보 확인용

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new MapFragment());
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_map) {
                loadFragment(new MapFragment());
                return true;
            } else if (itemId == R.id.navigation_my) {
                loadFragment(new MyFragment());
                return true;
            }

            return false;
        });

        // ⭐ 로그인 시 유저 정보가 잘 저장되었는지 확인하는 로그 (옵션)
        if (UserSessionManager.getInstance().isLoggedIn()) {
            android.util.Log.d("MainActivity", "User Session Active: " + UserSessionManager.getInstance().getCurrentUser().getName());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}