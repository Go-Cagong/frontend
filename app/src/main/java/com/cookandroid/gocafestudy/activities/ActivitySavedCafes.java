package com.cookandroid.gocafestudy.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.ColorStateList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.adapters.SavedCafesAdapter;
import com.cookandroid.gocafestudy.api.CafeApi;
import com.cookandroid.gocafestudy.models.DELETE.BookmarkDeleteResponse;
import com.cookandroid.gocafestudy.models.GET.Bookmark;
import com.cookandroid.gocafestudy.models.GET.BookmarkIsSavedResponse;
import com.cookandroid.gocafestudy.models.GET.BookmarkListResponse;
import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.CafeReviewResponse;
import com.cookandroid.gocafestudy.models.GET.Review;
import com.cookandroid.gocafestudy.models.POST.BookmarkCreateResponse;
import com.cookandroid.gocafestudy.repository.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySavedCafes extends AppCompatActivity {
    private static final String TAG = "AcitivitySavedCafes";

    private RecyclerView rvSavedCafes;
    private SavedCafesAdapter adapter;

    private CafeApi cafeApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cafes);

        cafeApi = RetrofitClient.getCafeApi();

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
    // -------------------------
    private void showCafeDetailBottomSheet(int cafeId) {
        // ❌ MockRepository 호출 제거
        // CafeDetail cafe = mockRepository.getCafeDetail(cafeId);

        cafeApi.getCafeDetail(cafeId).enqueue(new Callback<CafeDetail>() {
            @Override
            public void onResponse(Call<CafeDetail> call, Response<CafeDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API 호출 성공 시 BottomSheet 표시 함수 호출
                    displayCafeDetailSheet(cafeId, response.body());
                } else {
                    Toast.makeText(ActivitySavedCafes.this, "카페 상세 정보 로드 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Detail API Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CafeDetail> call, Throwable t) {
                Toast.makeText(ActivitySavedCafes.this, "카페 상세 정보 네트워크 오류", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Detail API Call Failure", t);
            }
        });
    }

    private void loadBookmarkState(Context context, int cafeId, MaterialButton btnSave) {

        RetrofitClient.getBookmarkApi(context)
                .getBookmarkState(cafeId)
                .enqueue(new Callback<BookmarkIsSavedResponse>() {

                    @Override
                    public void onResponse(Call<BookmarkIsSavedResponse> call,
                                           Response<BookmarkIsSavedResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            boolean saved = response.body().isSaved();

                            // 👉 아이콘 변경
                            if (saved) {
                                btnSave.setIcon(getDrawable(R.drawable.ic_bookmark_filled));
                                btnSave.setIconTint(ColorStateList.valueOf(getColor(R.color.yellow_primary)));
                                btnSave.setTag("saved");
                            } else {
                                btnSave.setIcon(getDrawable(R.drawable.ic_bookmark));
                                btnSave.setIconTint(ColorStateList.valueOf(getColor(R.color.gray_500)));
                                btnSave.setTag("not_saved");
                            }

                            Log.d("Bookmark", "isSaved = " + saved);
                        } else {
                            Log.e("Bookmark", "조회 실패: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkIsSavedResponse> call, Throwable t) {
                        Log.e("Bookmark", "네트워크 오류", t);
                    }
                });


    }
    private void createBookmark(Context context, int cafeId, MaterialButton btnSave) {

        RetrofitClient.getBookmarkApi(context)
                .createBookmark(cafeId)
                .enqueue(new Callback<BookmarkCreateResponse>() {

                    @Override
                    public void onResponse(Call<BookmarkCreateResponse> call,
                                           Response<BookmarkCreateResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            btnSave.setIcon(getDrawable(R.drawable.ic_bookmark_filled));
                            btnSave.setIconTint(ColorStateList.valueOf(getColor(R.color.yellow_primary)));
                            btnSave.setTag("saved");
                            Toast.makeText(context, "저장되었습니다", Toast.LENGTH_SHORT).show();
                            Log.d("BookmarkPOST", "저장 완료: " + response.body().getMessage());
                        } else {
                            Log.e("BookmarkPOST", "저장 실패: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkCreateResponse> call, Throwable t) {
                        Log.e("BookmarkPOST", "네트워크 오류", t);
                    }
                });
    }

    private void deleteBookmark(Context context, int cafeId, MaterialButton btnSave) {

        RetrofitClient.getBookmarkApi(context)
                .deleteBookmark(cafeId)
                .enqueue(new Callback<BookmarkDeleteResponse>() {

                    @Override
                    public void onResponse(Call<BookmarkDeleteResponse> call,
                                           Response<BookmarkDeleteResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            btnSave.setIcon(getDrawable(R.drawable.ic_bookmark));
                            btnSave.setIconTint(ColorStateList.valueOf(getColor(R.color.gray_500)));
                            btnSave.setTag("not_saved");
                            Toast.makeText(context, "저장이 취소되었습니다", Toast.LENGTH_SHORT).show();
                            Log.d("BookmarkDELETE", "저장 해제됨: " + response.body().getMessage());
                        } else {
                            Log.e("BookmarkDELETE", "해제 실패: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkDeleteResponse> call, Throwable t) {
                        Log.e("BookmarkDELETE", "네트워크 오류", t);
                    }
                });
    }




    /**
     * BottomSheet UI를 구성하고 표시하는 헬퍼 함수
     */
    private void displayCafeDetailSheet(int cafeId, CafeDetail cafe) {
        BottomSheetDialog dialog = new BottomSheetDialog(ActivitySavedCafes.this);
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_cafe_detail, null);

        TextView tvName      = v.findViewById(R.id.tv_cafe_name);
        TextView tvAddress   = v.findViewById(R.id.tv_address);
        TextView tvHours     = v.findViewById(R.id.tv_hours);
        TextView tvTel       = v.findViewById(R.id.tel);
        TextView tvMood      = v.findViewById(R.id.cafe_atmosphere);
        TextView tvPrice     = v.findViewById(R.id.cafe_price);
        TextView tvParking   = v.findViewById(R.id.cafe_parking);
        TextView tvAiSummary = v.findViewById(R.id.tv_ai_summary);
        TextView tvDescription = v.findViewById(R.id.description);

        // 평점 + (리뷰 개수 표시하고 싶으면 추가)
        TextView tvRating    = v.findViewById(R.id.tv_rating);
        //TextView tvReviewCount = v.findViewById(R.id.tv_review_count); // 레이아웃에 있으면
        Button   btnReview   = v.findViewById(R.id.btn_view_all_saved);

        ImageView ivMain  = v.findViewById(R.id.iv_cafe_image);
        ImageView ivSub1  = v.findViewById(R.id.iv_cafe_sub1);
        ImageView ivSub2  = v.findViewById(R.id.iv_cafe_sub2);
        ImageView ivSub3  = v.findViewById(R.id.iv_cafe_sub3);
        ImageView ivSub4  = v.findViewById(R.id.iv_cafe_sub4);

        // ✅ 리뷰 미리보기 RecyclerView
        RecyclerView rvPreviewReviews = v.findViewById(R.id.rv_preview_reviews);
        rvPreviewReviews.setLayoutManager(new LinearLayoutManager(ActivitySavedCafes.this));

        List<Review> previewReviews = new ArrayList<>();
        ReviewAdapter previewAdapter = new ReviewAdapter(previewReviews);
        rvPreviewReviews.setAdapter(previewAdapter);

        // ------------------------------
        // 1) 카페 기본 정보 채우기
        // ------------------------------
        tvName.setText(cafe.getName());
        tvAddress.setText(cafe.getAddress());
        tvHours.setText(cafe.getBusinessHours());
        tvTel.setText(cafe.getPhone());
        tvMood.setText(cafe.getMood());
        tvPrice.setText(cafe.getAmericanoPrice() + "원");
        tvParking.setText(cafe.isHasParking() ? "주차 가능" : "주차 불가");
        tvAiSummary.setText(cafe.getAiSummary());
        tvDescription.setText(cafe.getDescription());

        // 여기서는 일단 "로딩중" 문구만
        if (tvRating != null) {
            tvRating.setText("평점 로딩중...");
        }
        //if (tvReviewCount != null) {
        //tvReviewCount.setText(""); // 혹은 "0개" 등
        //}

        List<String> images = cafe.getImages();
        if (images != null) {
            if (images.size() > 0)
                Glide.with(ActivitySavedCafes.this).load(images.get(0)).placeholder(R.drawable.ic_camera_gray).into(ivMain);
            if (images.size() > 1)
                Glide.with(ActivitySavedCafes.this).load(images.get(1)).placeholder(R.drawable.ic_camera_gray).into(ivSub1);
            if (images.size() > 2)
                Glide.with(ActivitySavedCafes.this).load(images.get(2)).placeholder(R.drawable.ic_camera_gray).into(ivSub2);
            if (images.size() > 3)
                Glide.with(ActivitySavedCafes.this).load(images.get(3)).placeholder(R.drawable.ic_camera_gray).into(ivSub3);
            if (images.size() > 4)
                Glide.with(ActivitySavedCafes.this).load(images.get(4)).placeholder(R.drawable.ic_camera_gray).into(ivSub4);
        }

        // ------------------------------
        // 2) 🔥 서버에서 리뷰 + 평점 요약 가져오기
        // ------------------------------
        cafeApi.getReviewsByCafeId(cafeId).enqueue(new Callback<CafeReviewResponse>() {
            @Override
            public void onResponse(Call<CafeReviewResponse> call, Response<CafeReviewResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "preview review error code = " + response.code());
                    return;
                }

                CafeReviewResponse body = response.body();
                if (body == null || body.getReviews() == null) {
                    Log.e(TAG, "preview review body or reviews is null");
                    return;
                }

                // ★ 여기서 평균 평점 / 리뷰 개수 세팅
                double avg = body.getAverageRating();   // DTO 필드명에 맞게
                int count  = body.getReviewCount();

                if (tvRating != null) {
                    tvRating.setText(String.format("%.1f / 5.0", avg));
                }
                //if (tvReviewCount != null) {
                //tvReviewCount.setText("(" + count + "개의 리뷰)");
                //}

                List<Review> allReviews = body.getReviews();

                // createdAt "yyyy-MM-dd" 까지만 보이게 슬라이스
                for (Review r : allReviews) {
                    String createdAt = r.getCreatedAt();
                    if (createdAt != null && createdAt.length() >= 10) {
                        r.setCreatedAt(createdAt.substring(0, 10));
                    }
                }

                // 최대 3개까지만 BottomSheet에 미리보기로 보여주기
                previewReviews.clear();
                for (int i = 0; i < Math.min(3, allReviews.size()); i++) {
                    previewReviews.add(allReviews.get(i));
                }
                previewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CafeReviewResponse> call, Throwable t) {
                Log.e(TAG, "preview review onFailure", t);
            }
        });

        // ------------------------------
        // 3) '리뷰 전체보기' 버튼
        // ------------------------------
        btnReview.setOnClickListener(click -> {
            Intent intent = new Intent(ActivitySavedCafes.this, ActivityReviewList.class);
            intent.putExtra("cafeId", cafeId);
            startActivity(intent);
            dialog.dismiss();
        });

        MaterialButton btnSave = v.findViewById(R.id.btn_save);
        loadBookmarkState(ActivitySavedCafes.this, cafeId, btnSave);

        btnSave.setOnClickListener(view -> {
            String currentState = (String) btnSave.getTag();

            if ("saved".equals(currentState)) {
                deleteBookmark(ActivitySavedCafes.this, cafeId, btnSave);
            } else {
                createBookmark(ActivitySavedCafes.this, cafeId, btnSave);
            }
        });

        dialog.setContentView(v);
        dialog.show();
    }

}
