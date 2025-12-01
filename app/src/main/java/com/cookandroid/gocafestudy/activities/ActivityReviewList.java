package com.cookandroid.gocafestudy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.api.CafeApi;
import com.cookandroid.gocafestudy.models.GET.CafeReviewResponse;
import com.cookandroid.gocafestudy.models.GET.Review;
import com.cookandroid.gocafestudy.repository.RetrofitClient;
import com.cookandroid.gocafestudy.models.GET.CafeReviewResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityReviewList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    private CafeApi cafeApi;
    private int cafeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        // 1) 인텐트에서 cafeId 받기
        cafeId = getIntent().getIntExtra("cafeId", -1);
        if (cafeId == -1) {
            Toast.makeText(this, "카페 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2) RecyclerView + Adapter 세팅
        recyclerView = findViewById(R.id.rv_reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(reviewAdapter);

        // 3) RetrofitClient에서 CafeApi 가져오기 (비인증용)
        cafeApi = RetrofitClient.getCafeApi();

        // 4) 서버에서 리뷰 목록 불러오기
        loadReviewsFromServer(cafeId);

        // 🔙 뒤로가기 버튼
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // ✏️ 리뷰 작성 버튼
        Button btnWriteReview = findViewById(R.id.btn_write_review);
        if (btnWriteReview != null) {
            btnWriteReview.setOnClickListener(v -> {
                Intent intent = new Intent(ActivityReviewList.this, ActivityWriteReview.class);
                intent.putExtra("cafeId", cafeId);
                startActivity(intent);
            });
        }
    }

    private void loadReviewsFromServer(int cafeId) {
        cafeApi.getReviewsByCafeId(cafeId).enqueue(new Callback<CafeReviewResponse>() {
            @Override
            public void onResponse(Call<CafeReviewResponse> call, Response<CafeReviewResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e("ActivityReviewList", "review error code = " + response.code());
                    Toast.makeText(ActivityReviewList.this,
                            "리뷰 불러오기 실패 (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                CafeReviewResponse body = response.body();
                if (body == null) {
                    Log.e("ActivityReviewList", "review body is null");
                    Toast.makeText(ActivityReviewList.this,
                            "리뷰 응답이 비어 있습니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ 서버에서 받은 실제 리뷰 리스트

                // ✅ 실제 리뷰 리스트 꺼내기
                List<Review> reviews = body.getReviews();

                if (reviews == null || reviews.isEmpty()) {
                    reviewList.clear();
                    reviewAdapter.notifyDataSetChanged();
                    Toast.makeText(ActivityReviewList.this,
                            "등록된 리뷰가 없습니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ createdAt을 "yyyy-MM-dd" 형식(일 단위까지만)으로 잘라 넣기
                for (Review r : reviews) {
                    String createdAt = r.getCreatedAt();  // "2025-11-14T10:21:00"
                    if (createdAt != null && createdAt.length() >= 10) {
                        r.setCreatedAt(createdAt.substring(0, 10)); // "2025-11-14"
                    }
                }

                reviewList.clear();
                reviewList.addAll(reviews);
                reviewAdapter.notifyDataSetChanged();

                // 필요하면 여기서 평균 평점/리뷰 개수도 쓸 수 있음
                // double avg = body.getAverageRating();
                // int count = body.getReviewCount();
                // TextView에 표시하고 싶으면 여기서 처리하면 됨
            }

            @Override
            public void onFailure(Call<CafeReviewResponse> call, Throwable t) {
                Log.e("ActivityReviewList", "review onFailure", t);
                Toast.makeText(ActivityReviewList.this,
                        "리뷰 서버 오류: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
