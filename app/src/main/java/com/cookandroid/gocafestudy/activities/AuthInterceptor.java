package com.cookandroid.gocafestudy.activities;

import android.content.Context;

import java.io.IOException;

public class AuthInterceptor implements okhttp3.Interceptor {

    private final Context appContext;

    public AuthInterceptor(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        String token = appContext.getSharedPreferences("auth", Context.MODE_PRIVATE)
                .getString("access_token", null);   

        okhttp3.Request original = chain.request();
        okhttp3.Request.Builder builder = original.newBuilder();

        if (token != null) {
            // 🚨 수정: Cookie 헤더에 access_token=... 형식으로 토큰 전달
            builder.header("Cookie", "access_token=" + token);
        }

        return chain.proceed(builder.build());
    }
}


