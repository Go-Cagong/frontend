package com.cookandroid.gocafestudy.api;

import com.cookandroid.gocafestudy.models.GET.MyReviewsResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ReviewApi {
    @GET("/api/review/me")
    Call<MyReviewsResponse> getMyReviews();

    @DELETE("/api/review/{id}")
    Call<Void> deleteReview(@Path("id") int reviewId);

}
