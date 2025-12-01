package com.cookandroid.gocafestudy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.adapters.MyReviewsAdapter;
import com.cookandroid.gocafestudy.models.GET.MyReviewItem;
import com.cookandroid.gocafestudy.models.GET.MyReviewsResponse;
import com.cookandroid.gocafestudy.repository.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMyReviews extends AppCompatActivity {

    private RecyclerView rvMyReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        // 뒤로가기 버튼
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvMyReviews = findViewById(R.id.rv_my_reviews);
        rvMyReviews.setLayoutManager(new LinearLayoutManager(this));

        loadMyReviews();
    }

    private void loadMyReviews() {
        RetrofitClient.getReviewApi(this).getMyReviews().enqueue(new Callback<MyReviewsResponse>() {
            @Override
            public void onResponse(Call<MyReviewsResponse> call, Response<MyReviewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MyReviewItem> myReviews = response.body().getReviews();
                    MyReviewsAdapter adapter = new MyReviewsAdapter(ActivityMyReviews.this, myReviews);
                    rvMyReviews.setAdapter(adapter);
                } else {
                    Toast.makeText(ActivityMyReviews.this, "리뷰 불러오기 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyReviewsResponse> call, Throwable t) {
                Toast.makeText(ActivityMyReviews.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
