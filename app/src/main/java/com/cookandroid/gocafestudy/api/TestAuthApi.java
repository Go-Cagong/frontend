// TestAuthApi.java (새로 만들거나 기존 API 파일에 추가)
package com.cookandroid.gocafestudy.api; // 혹은 api 패키지

import com.cookandroid.gocafestudy.models.GET.UserResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TestAuthApi {
    @GET("/api/auth/me")
    Call<UserResponse> getMyUserInfo();
}