package com.cookandroid.gocafestudy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.adapters.MyReviewsAdapter;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.MyReviewItem;
import com.cookandroid.gocafestudy.models.GET.MyReviewsResponse;
import com.cookandroid.gocafestudy.repository.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

                    MyReviewsAdapter adapter = new MyReviewsAdapter(
                            ActivityMyReviews.this,
                            myReviews,
                            cafeId -> showCafeDetailBottomSheet(cafeId)   // ⭐ 클릭 → 바텀시트!
                    );

                    rvMyReviews.setAdapter(adapter);

                } else {
                    Toast.makeText(ActivityMyReviews.this, "리뷰 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyReviewsResponse> call, Throwable t) {
                Toast.makeText(ActivityMyReviews.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCafeDetailBottomSheet(int cafeId) {
        RetrofitClient.getCafeApi().getCafeDetail(cafeId)
                .enqueue(new Callback<CafeDetail>() {
                    @Override
                    public void onResponse(Call<CafeDetail> call, Response<CafeDetail> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        CafeDetail cafe = response.body();
                        BottomSheetDialog dialog = new BottomSheetDialog(ActivityMyReviews.this);
                        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_cafe_detail, null);

                        // ---- 기존 SavedCafes 코드 전체 동일 ----
                        // 이름, 주소, 시간, 전화번호 등 바인딩
                        TextView tvName = v.findViewById(R.id.tv_cafe_name);
                        TextView tvAddress = v.findViewById(R.id.tv_address);
                        TextView tvHours = v.findViewById(R.id.tv_hours);
                        TextView tvTel = v.findViewById(R.id.tel);
                        TextView tvMood = v.findViewById(R.id.cafe_atmosphere);
                        TextView tvPrice = v.findViewById(R.id.cafe_price);
                        TextView tvParking = v.findViewById(R.id.cafe_parking);
                        TextView tvAiSummary = v.findViewById(R.id.tv_ai_summary);
                        TextView tvDescription = v.findViewById(R.id.description);
                        TextView tvRating = v.findViewById(R.id.tv_rating);

                        ImageView ivMain = v.findViewById(R.id.iv_cafe_image);
                        ImageView ivSub1 = v.findViewById(R.id.iv_cafe_sub1);
                        ImageView ivSub2 = v.findViewById(R.id.iv_cafe_sub2);
                        ImageView ivSub3 = v.findViewById(R.id.iv_cafe_sub3);
                        ImageView ivSub4 = v.findViewById(R.id.iv_cafe_sub4);

                        RecyclerView rvPreviewReviews = v.findViewById(R.id.rv_preview_reviews);
                        rvPreviewReviews.setLayoutManager(new LinearLayoutManager(ActivityMyReviews.this));

                        tvName.setText(cafe.getName());
                        tvAddress.setText(cafe.getAddress());
                        tvHours.setText(cafe.getBusinessHours());
                        tvTel.setText(cafe.getPhone());
                        tvMood.setText(cafe.getMood());
                        tvPrice.setText(cafe.getAmericanoPrice() + "원");
                        tvParking.setText(cafe.isHasParking() ? "주차 가능" : "주차 불가");
                        tvAiSummary.setText(cafe.getAiSummary());
                        tvDescription.setText(cafe.getDescription());
                        tvRating.setText(String.format("%.1f / 5.0", cafe.getReviewAverage()));

                        List<String> imgs = cafe.getImages();
                        if (imgs.size() > 0) Glide.with(ActivityMyReviews.this).load(imgs.get(0)).into(ivMain);
                        if (imgs.size() > 1) Glide.with(ActivityMyReviews.this).load(imgs.get(1)).into(ivSub1);
                        if (imgs.size() > 2) Glide.with(ActivityMyReviews.this).load(imgs.get(2)).into(ivSub2);
                        if (imgs.size() > 3) Glide.with(ActivityMyReviews.this).load(imgs.get(3)).into(ivSub3);
                        if (imgs.size() > 4) Glide.with(ActivityMyReviews.this).load(imgs.get(4)).into(ivSub4);

                        rvPreviewReviews.setAdapter(new ReviewAdapter(cafe.getRecentReviews()));

                        dialog.setContentView(v);
                        dialog.show();
                    }

                    @Override
                    public void onFailure(Call<CafeDetail> call, Throwable t) {
                    }
                });
    }


}
