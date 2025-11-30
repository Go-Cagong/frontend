package com.cookandroid.gocafestudy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginCallbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 서버 redirect: cagong://login-success?access_token=...
        Uri data = getIntent().getData();

        if (data != null) {
            String accessToken = data.getQueryParameter("access_token");

            if (accessToken != null && !accessToken.isEmpty()) {
                saveAccessToken(accessToken);

                // 로그인 성공 → 메인으로 이동
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "access_token을 받지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void saveAccessToken(String token) {
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .putString("access_token", token)  
                .apply();
    }
}
