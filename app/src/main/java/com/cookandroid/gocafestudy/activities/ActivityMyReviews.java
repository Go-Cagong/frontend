package com.cookandroid.gocafestudy.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.adapters.MyReviewsAdapter;
import com.cookandroid.gocafestudy.datas.MockData;
import com.cookandroid.gocafestudy.models.GET.MyReviewItem;

import java.util.List;

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

        // 수정: MockData에서 바로 MyReviewItem 리스트를 가져옴 (필터링 불필요)
        List<MyReviewItem> myReviewList = MockData.getMyReviews();

        // 어댑터 연결
        MyReviewsAdapter adapter = new MyReviewsAdapter(this, myReviewList);
        rvMyReviews.setAdapter(adapter);

    }
}
