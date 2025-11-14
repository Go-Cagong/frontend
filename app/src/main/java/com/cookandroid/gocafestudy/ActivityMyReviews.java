package com.cookandroid.gocafestudy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActivityMyReviews extends AppCompatActivity {

    private RecyclerView rvMyReviews;
    private List<MyReviewItem> myReviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvMyReviews = findViewById(R.id.rv_my_reviews);
        rvMyReviews.setLayoutManager(new LinearLayoutManager(this));

        // ⭐ 예시 데이터 3개 추가
        myReviewList = new ArrayList<>();
        myReviewList.add(new MyReviewItem(
                "스타벅스 송도점",
                "음료도 맛있고 자리도 편해서 공부하기 좋았습니다.",
                "https://picsum.photos/100",
                5
        ));

        myReviewList.add(new MyReviewItem(
                "메가커피 인천대입구점",
                "싸고 맛있긴 한데 사람이 너무 많아요.",
                "https://picsum.photos/101",
                4
        ));

        myReviewList.add(new MyReviewItem(
                "빽다방 연수점",
                "아메리카노는 괜찮았는데 소음이 좀 심했어요.",
                "https://picsum.photos/102",
                3
        ));

        // 어댑터 연결
        MyReviewsAdapter adapter = new MyReviewsAdapter(this, myReviewList);
        rvMyReviews.setAdapter(adapter);

        // 상단 카운트 업데이트
        findViewById(R.id.tv_review_count);
        ((android.widget.TextView)findViewById(R.id.tv_review_count))
                .setText(myReviewList.size() + "개");
    }
}