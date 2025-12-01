package com.cookandroid.gocafestudy.api;
// api/CafeApi.java

import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.CafeMapResponse; // <-- 이 모델을 사용합니다
import com.cookandroid.gocafestudy.models.GET.CafeReviewResponse;
import com.cookandroid.gocafestudy.models.POST.ReviewCreateResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.POST; // 🌟 추가
import retrofit2.http.Multipart; // 🌟 추가
import retrofit2.http.Part; // 🌟 추가

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

    // 🌟 리뷰 등록 API (Multipart/form-data)
    @Multipart
    @POST("api/cafe/{cafeId}/review")
    Call<ReviewCreateResponse> createReview(
            @Path("cafeId") int cafeId,
            @Part("rating") RequestBody rating, // 텍스트 필드
            @Part("content") RequestBody content, // 텍스트 필드
            @Part List<MultipartBody.Part> images // 이미지 파일 리스트 (최대 5장)
    );

    @GET("api/cafe/{cafeId}/review")
    Call<CafeReviewResponse> getReviewsByCafeId(@Path("cafeId") int cafeId);
}