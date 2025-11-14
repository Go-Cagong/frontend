package com.cookandroid.gocafestudy;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialButton btnGoogleLogin = findViewById(R.id.btn_google_login);
        MaterialButton btnKakaoLogin = findViewById(R.id.btn_kakao_login);

        btnGoogleLogin.setOnClickListener(v -> {
            // TODO: Implement Google login
            navigateToMain();
        });

        btnKakaoLogin.setOnClickListener(v -> {
            // TODO: Implement Kakao login
            navigateToMain();
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
