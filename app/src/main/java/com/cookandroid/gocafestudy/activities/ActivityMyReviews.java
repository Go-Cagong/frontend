package com.cookandroid.gocafestudy.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.adapters.MyReviewsAdapter;
import com.cookandroid.gocafestudy.models.GET.MyReviewItem;
import com.cookandroid.gocafestudy.repository.MockRepository;
import java.util.List;

public class ActivityMyReviews extends AppCompatActivity {

    private RecyclerView rvMyReviews;

    private MockRepository repository = new MockRepository();
    private static final int MOCK_USER_ID = 1; // 가정한 사용자 ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        // 뒤로가기 버튼
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvMyReviews = findViewById(R.id.rv_my_reviews);
        rvMyReviews.setLayoutManager(new LinearLayoutManager(this));

        // 수정: MockData에서 바로 MyReviewItem 리스트를 가져옴 (필터링 불필요)유저아이디 반환받아서 넘겨야함
        List<MyReviewItem> myReviewList = repository.getMyReviews(MOCK_USER_ID);
        // 어댑터 연결
        MyReviewsAdapter adapter = new MyReviewsAdapter(this, myReviewList);
        rvMyReviews.setAdapter(adapter);

    }
}
