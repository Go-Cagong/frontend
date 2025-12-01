package com.cookandroid.gocafestudy.api;

import com.cookandroid.gocafestudy.models.GET.MyPageInfo;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApi {

    @GET("/api/user/mypage")
    Call<MyPageInfo> getMyPage();
}
