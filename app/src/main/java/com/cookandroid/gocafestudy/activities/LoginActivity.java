package com.cookandroid.gocafestudy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.gocafestudy.R;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://go-cagong.ddns.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialButton btnGoogleLogin = findViewById(R.id.btn_google_login);
        MaterialButton btnKakaoLogin = findViewById(R.id.btn_kakao_login);
        MaterialButton btnNaverLogin = findViewById(R.id.btn_naver_login);

        btnGoogleLogin.setOnClickListener(v -> openOAuthPage("google"));
        btnKakaoLogin.setOnClickListener(v -> openOAuthPage("kakao"));
        btnNaverLogin.setOnClickListener(v -> openOAuthPage("naver"));
    }

    private void openOAuthPage(String provider) {
        String url = BASE_URL + "/oauth2/authorization/" + provider;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
