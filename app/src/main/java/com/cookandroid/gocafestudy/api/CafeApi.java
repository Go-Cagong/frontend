package com.cookandroid.gocafestudy.api;
// api/CafeApi.java

import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.CafeMapResponse; // <-- 이 모델을 사용합니다
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CafeApi {

    // ⚠️ 반환 타입을 Call<List<CafeMapItem>> 에서 Call<CafeMapResponse>로 변경
    @GET("api/cafe")
    Call<CafeMapResponse> getCafeMapItems(
            @Query("lat") double latitude,
            @Query("lon") double longitude
    );

    // 단일 카페 상세 정보 GET 요청 (예시)
    @GET("api/cafe/{cafeId}")
    Call<CafeDetail> getCafeDetail(@Path("cafeId") int cafeId);
}