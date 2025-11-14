package com.cookandroid.gocafestudy;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class ActivitySavedCafes extends AppCompatActivity {

    private RecyclerView rvSavedCafes;
    private SavedCafesAdapter adapter;
    private List<CafeItem> cafeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cafes);

        rvSavedCafes = findViewById(R.id.rv_saved_cafes);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 초기 더미 데이터 (나중에 서버에서 받아오는 데이터로 대체)
        cafeList = new ArrayList<>();
        cafeList.add(new CafeItem("카페 모닝", "서울 강남구", "https://example.com/image1.jpg"));
        cafeList.add(new CafeItem("카페 브런치", "서울 서초구", "https://example.com/image2.jpg"));
        cafeList.add(new CafeItem("카페 오후", "서울 송파구", "https://example.com/image3.jpg"));

        adapter = new SavedCafesAdapter(this, cafeList);
        rvSavedCafes.setLayoutManager(new LinearLayoutManager(this));
        rvSavedCafes.setAdapter(adapter);
    }

    // 서버 연동 시 fetchSavedCafes() 같은 함수 만들어서 데이터 가져오기 가능
}
