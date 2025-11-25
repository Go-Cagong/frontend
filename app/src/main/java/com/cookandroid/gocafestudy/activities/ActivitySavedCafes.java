package com.cookandroid.gocafestudy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.adapters.SavedCafesAdapter;
import com.cookandroid.gocafestudy.datas.MockData;
import com.cookandroid.gocafestudy.models.DELETE.BookmarkDeleteResponse;
import com.cookandroid.gocafestudy.models.GET.Bookmark;
import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.Review;
import com.cookandroid.gocafestudy.models.POST.BookmarkCreateResponse;
import com.cookandroid.gocafestudy.repository.MockRepository;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class ActivitySavedCafes extends AppCompatActivity {

    private RecyclerView rvSavedCafes;
    private SavedCafesAdapter adapter;
    private MockRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cafes);

        repository = new MockRepository(); // 레포지토리 초기화

        rvSavedCafes = findViewById(R.id.rv_saved_cafes);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        List<Bookmark> bookmarks = repository.getBookmarksByUserId(1);

        adapter = new SavedCafesAdapter(this, bookmarks, cafeId -> {
            showCafeDetailBottomSheet(cafeId);
        });

        rvSavedCafes.setLayoutManager(new LinearLayoutManager(this));
        rvSavedCafes.setAdapter(adapter);
    }

    private void showCafeDetailBottomSheet(int cafeId) {
        CafeDetail cafe = repository.getCafeDetail(cafeId);
        if (cafe == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_cafe_detail, null);

        // --- 기본 정보 초기화 ---
        TextView tvName = v.findViewById(R.id.tv_cafe_name);
        TextView tvAddress = v.findViewById(R.id.tv_address);
        TextView tvHours = v.findViewById(R.id.tv_hours);
        TextView tvTel = v.findViewById(R.id.tel);
        TextView tvMood = v.findViewById(R.id.cafe_atmosphere);
        TextView tvPrice = v.findViewById(R.id.cafe_price);
        TextView tvParking = v.findViewById(R.id.cafe_parking);
        TextView tvAiSummary = v.findViewById(R.id.tv_ai_summary);
        TextView tvRating = v.findViewById(R.id.tv_rating);

        ImageView ivMain = v.findViewById(R.id.iv_cafe_image);
        ImageView ivSub1 = v.findViewById(R.id.iv_cafe_sub1);
        ImageView ivSub2 = v.findViewById(R.id.iv_cafe_sub2);
        ImageView ivSub3 = v.findViewById(R.id.iv_cafe_sub3);
        ImageView ivSub4 = v.findViewById(R.id.iv_cafe_sub4);

        RecyclerView rvPreviewReviews = v.findViewById(R.id.rv_preview_reviews);
        rvPreviewReviews.setLayoutManager(new LinearLayoutManager(this));

        // 데이터 연결
        tvName.setText(cafe.getName());
        tvAddress.setText(cafe.getAddress());
        tvHours.setText(cafe.getBusinessHours());
        tvTel.setText(cafe.getPhone());
        tvMood.setText(cafe.getMood());
        tvPrice.setText(cafe.getAmericanoPrice() + "원");
        tvParking.setText(cafe.isHasParking() ? "주차 가능" : "주차 불가");
        tvAiSummary.setText(cafe.getDescription());
        tvRating.setText(String.format("%.1f / 5.0", cafe.getReviewAverage()));

        // --- 이미지 연결 ---
        List<String> images = cafe.getImages();
        if (images.size() > 0) Glide.with(this).load(images.get(0)).placeholder(R.drawable.ic_cafe1_img).into(ivMain);
        if (images.size() > 1) Glide.with(this).load(images.get(1)).placeholder(R.drawable.ic_cafe1_img).into(ivSub1);
        if (images.size() > 2) Glide.with(this).load(images.get(2)).placeholder(R.drawable.ic_cafe1_img).into(ivSub2);
        if (images.size() > 3) Glide.with(this).load(images.get(3)).placeholder(R.drawable.ic_cafe1_img).into(ivSub3);
        if (images.size() > 4) Glide.with(this).load(images.get(4)).placeholder(R.drawable.ic_cafe1_img).into(ivSub4);

        // 최근 리뷰 3개
        List<Review> recentReviews = cafe.getRecentReviews();
        List<Review> previewReviews = recentReviews.subList(0, Math.min(3, recentReviews.size()));
        rvPreviewReviews.setAdapter(new ReviewAdapter(previewReviews));

        // 전체 리뷰 버튼
        v.findViewById(R.id.btn_view_all_saved).setOnClickListener(click -> {
            Intent intent = new Intent(this, ActivityReviewList.class);
            intent.putExtra("cafeId", cafeId);
            startActivity(intent);
            dialog.dismiss();
        });

        Button btnSave = v.findViewById(R.id.btn_save);
        if (cafe.isSaved()) {
            // true일 때
            btnSave.setText("삭제");
            btnSave.setOnClickListener(click -> {
                BookmarkDeleteResponse response = repository.deleteBookmark(cafeId);
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
            });
        } else {
            // false일 때
            btnSave.setOnClickListener(click -> {
                BookmarkCreateResponse response = repository.createBookmark(cafeId);
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
            });
        }

        dialog.setContentView(v);
        dialog.show();
    }

}


