package com.cookandroid.gocafestudy.repository;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.cookandroid.gocafestudy.api.CafeApi;

public class RetrofitClient {
    private static final String BASE_URL = "https://go-cagong.ddns.net/"; // 백엔드 서버 URL로 변경하세요
    private static Retrofit retrofit = null;

    private RetrofitClient() {}

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // CafeApi 서비스 인스턴스를 제공하는 메소드
    public static CafeApi getCafeApi() {
        return getClient().create(CafeApi.class);
    }
}