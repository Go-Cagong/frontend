package com.cookandroid.gocafestudy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.models.GET.Review;
import com.cookandroid.gocafestudy.repository.MockRepository;

import java.util.List;

public class ActivityReviewList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private MockRepository repository = new MockRepository();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        recyclerView = findViewById(R.id.rv_reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int cafeId = getIntent().getIntExtra("cafeId", -1);
        List<Review> reviews = repository.getReviewsByCafeId(cafeId);

        reviewAdapter = new ReviewAdapter(reviews);
        recyclerView.setAdapter(reviewAdapter);

        // 🔙 뒤로가기 버튼
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // ✏️ 리뷰 작성 버튼
        Button btnWriteReview = findViewById(R.id.btn_write_review);
        btnWriteReview.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityReviewList.this, ActivityWriteReview.class);
            intent.putExtra("cafeId", cafeId);
            startActivity(intent);
        });
    }
}
