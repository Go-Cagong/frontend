package com.cookandroid.gocafestudy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log; // Log import 추가
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.gocafestudy.models.GET.UserResponse;
import com.cookandroid.gocafestudy.utils.UserSessionManager;
import com.cookandroid.gocafestudy.repository.RetrofitClient;
import com.cookandroid.gocafestudy.api.TestAuthApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginCallbackActivity extends AppCompatActivity {

    private static final String TAG = "LoginCallbackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();

        if (data != null) {
            String accessToken = data.getQueryParameter("access_token");

            if (accessToken != null && !accessToken.isEmpty()) {
                // ✅ 액세스 토큰 로그 출력
                Log.d(TAG, "Access Token Received: " + accessToken);

                saveAccessToken(accessToken);
                // 🌟 유저 정보를 가져와 저장하는 핵심 로직 호출
                fetchUserInfo(accessToken);
            } else {
                Toast.makeText(this, "access_token을 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        // 이 곳에서는 finish()를 호출하지 않고, API 호출 완료 후에 처리합니다.
    }

    private void saveAccessToken(String token) {
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .putString("access_token", token)
                .apply();
    }

    // 🌟 유저 정보 API 호출 및 전역 저장 메서드
    private void fetchUserInfo(String token) {
        // Context를 넘겨야 AuthInterceptor가 SharedPreferences에 접근 가능
        TestAuthApi api = RetrofitClient.getAuthApi(this);

        api.getMyUserInfo().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();

                    // 1. 유저 정보 전역 저장
                    UserSessionManager.getInstance().login(user);

                    // 2. 로그 및 토스트 출력
                    String message = "로그인 성공: " + user.getName() + " (" + user.getEmail() + ")";
                    android.util.Log.d(TAG, message);
                    Toast.makeText(LoginCallbackActivity.this, message, Toast.LENGTH_LONG).show();

                    // 3. 메인으로 이동
                    Intent intent = new Intent(LoginCallbackActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else {
                    android.util.Log.e(TAG, "유저 정보 로드 실패: " + response.code() + ", Body: " + (response.errorBody() != null ? response.errorBody().toString() : "N/A"));
                    Toast.makeText(LoginCallbackActivity.this, "유저 정보 로드 실패", Toast.LENGTH_SHORT).show();
                }
                finish(); // 성공/실패와 관계없이 콜백 액티비티 종료
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                android.util.Log.e(TAG, "네트워크 오류", t);
                Toast.makeText(LoginCallbackActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}