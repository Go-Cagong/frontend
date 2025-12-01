package com.cookandroid.gocafestudy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.adapters.SavedCafesAdapter;
import com.cookandroid.gocafestudy.models.DELETE.BookmarkDeleteResponse;
import com.cookandroid.gocafestudy.models.GET.Bookmark;
import com.cookandroid.gocafestudy.models.GET.BookmarkListResponse;
import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.Review;
import com.cookandroid.gocafestudy.repository.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySavedCafes extends AppCompatActivity {

    private RecyclerView rvSavedCafes;
    private SavedCafesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cafes);

        rvSavedCafes = findViewById(R.id.rv_saved_cafes);
        rvSavedCafes.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadSavedCafes();  // ⭐ API 호출
    }

    /**
     * ⭐ 저장한 카페 목록 불러오기
     */
    private void loadSavedCafes() {
        RetrofitClient.getBookmarkApi(this)
                .getMyBookmarks()
                .enqueue(new Callback<BookmarkListResponse>() {
                    @Override
                    public void onResponse(Call<BookmarkListResponse> call, Response<BookmarkListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            List<Bookmark> list = response.body().getBookmarks();

                            adapter = new SavedCafesAdapter(
                                    ActivitySavedCafes.this,
                                    list,
                                    cafeId -> showCafeDetailBottomSheet(cafeId)  // ⭐ 상세 보기
                            );

                            rvSavedCafes.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkListResponse> call, Throwable t) {
                        Log.e("SavedCafes", "북마크 목록 불러오기 실패", t);
                    }
                });
    }

    /**
     * ⭐ 카페 상세 보기 BottomSheet
     */
    private void showCafeDetailBottomSheet(int cafeId) {

        // ------- 상세정보 API --------
        RetrofitClient.getCafeApi()
                .getCafeDetail(cafeId)
                .enqueue(new Callback<CafeDetail>() {
                    @Override
                    public void onResponse(Call<CafeDetail> call, Response<CafeDetail> response) {

                        if (!response.isSuccessful() || response.body() == null) return;

                        CafeDetail cafe = response.body();

                        BottomSheetDialog dialog = new BottomSheetDialog(ActivitySavedCafes.this);
                        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_cafe_detail, null);

                        // 기본 정보
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
                        rvPreviewReviews.setLayoutManager(new LinearLayoutManager(ActivitySavedCafes.this));

                        // ----- 데이터 연결 -----
                        tvName.setText(cafe.getName());
                        tvAddress.setText(cafe.getAddress());
                        tvHours.setText(cafe.getBusinessHours());
                        tvTel.setText(cafe.getPhone());
                        tvMood.setText(cafe.getMood());
                        tvPrice.setText(cafe.getAmericanoPrice() + "원");
                        tvParking.setText(cafe.isHasParking() ? "주차 가능" : "주차 불가");
                        tvAiSummary.setText(cafe.getDescription());
                        tvRating.setText(String.format("%.1f / 5.0", cafe.getReviewAverage()));

                        // 이미지 5장 적용
                        List<String> imgs = cafe.getImages();
                        if (imgs == null) imgs = new ArrayList<>();

                        if (imgs.size() > 0) Glide.with(ActivitySavedCafes.this).load(imgs.get(0)).into(ivMain);
                        if (imgs.size() > 1) Glide.with(ActivitySavedCafes.this).load(imgs.get(1)).into(ivSub1);
                        if (imgs.size() > 2) Glide.with(ActivitySavedCafes.this).load(imgs.get(2)).into(ivSub2);
                        if (imgs.size() > 3) Glide.with(ActivitySavedCafes.this).load(imgs.get(3)).into(ivSub3);
                        if (imgs.size() > 4) Glide.with(ActivitySavedCafes.this).load(imgs.get(4)).into(ivSub4);

                        // 리뷰 미리보기 3개
                        List<Review> preview = new ArrayList<>();
                        if (preview == null) preview = new ArrayList<>();
                        for (int i = 0; i < Math.min(3, cafe.getRecentReviews().size()); i++) {
                            preview.add(cafe.getRecentReviews().get(i));
                        }
                        rvPreviewReviews.setAdapter(new ReviewAdapter(preview));

                        // 전체 리뷰 보기 버튼
                        v.findViewById(R.id.btn_view_all_saved).setOnClickListener(click -> {
                            Intent intent = new Intent(ActivitySavedCafes.this, ActivityReviewList.class);
                            intent.putExtra("cafeId", cafeId);
                            startActivity(intent);
                        });

                        // ----- 저장 취소 (삭제) 버튼 -----
                        Button btnSave = v.findViewById(R.id.btn_save);
                        btnSave.setText("삭제하기");

                        btnSave.setOnClickListener(click -> {

                            RetrofitClient.getBookmarkApi(ActivitySavedCafes.this)
                                    .deleteBookmark(cafeId)
                                    .enqueue(new Callback<BookmarkDeleteResponse>() {
                                        @Override
                                        public void onResponse(Call<BookmarkDeleteResponse> call,
                                                               Response<BookmarkDeleteResponse> response) {

                                            if (response.isSuccessful() && response.body() != null) {
                                                Toast.makeText(ActivitySavedCafes.this,
                                                        response.body().getMessage(),
                                                        Toast.LENGTH_SHORT).show();

                                                dialog.dismiss();
                                                loadSavedCafes();  // ⭐ 목록 새로고침
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<BookmarkDeleteResponse> call, Throwable t) {
                                            Log.e("Bookmark", "삭제 실패", t);
                                        }
                                    });
                        });

                        dialog.setContentView(v);
                        dialog.show();
                    }

                    @Override
                    public void onFailure(Call<CafeDetail> call, Throwable t) {
                        Log.e("CafeDetail", "상세 조회 실패", t);
                    }
                });
    }
}
