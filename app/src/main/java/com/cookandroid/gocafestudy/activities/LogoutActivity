package com.cookandroid.gocafestudy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LogoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ 토큰 삭제
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .remove("access_token")
                .apply();

        Toast.makeText(this, "로그아웃 완료!", Toast.LENGTH_SHORT).show();

        // ✅ 로그인 화면으로 이동
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }
}
