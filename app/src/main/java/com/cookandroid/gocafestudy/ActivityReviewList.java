package com.cookandroid.gocafestudy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActivityReviewList extends AppCompatActivity {

    private RecyclerView rvReviews;
    private List<ReviewModel> reviewList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        rvReviews = findViewById(R.id.rv_reviews);

        // 뒤로가기 버튼
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 리뷰 작성 버튼
        findViewById(R.id.btn_write_review).setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityWriteReview.class);
            startActivity(intent);
        });

        // 더미 리뷰들 (나중에 서버에서 불러오기 쉽게 구조 설계)
        reviewList = new ArrayList<>();
        reviewList.add(new ReviewModel("카페 분위기가 좋습니다.", "2025-01-01", 5));
        reviewList.add(new ReviewModel("커피가 진짜 맛있어요!", "2025-01-03", 4));
        reviewList.add(new ReviewModel("콘센트가 많아서 공부하기 좋아요.", "2025-01-05", 5));
        reviewList.add(new ReviewModel("사람이 많아서 조금 시끄러웠어요.", "2025-01-06", 3));
        reviewList.add(new ReviewModel("직원분이 친절합니다.", "2025-01-08", 5));

        // RecyclerView 세팅
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(new ReviewAdapter(reviewList));
    }
}
