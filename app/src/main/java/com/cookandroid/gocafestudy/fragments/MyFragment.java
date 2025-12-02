package com.cookandroid.gocafestudy.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.activities.ActivityMyReviews;
import com.cookandroid.gocafestudy.activities.ActivityReviewList;
import com.cookandroid.gocafestudy.activities.ActivitySavedCafes;
import com.cookandroid.gocafestudy.activities.LoginActivity;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.adapters.SavedCafesAdapter;
import com.cookandroid.gocafestudy.models.DELETE.BookmarkDeleteResponse;
import com.cookandroid.gocafestudy.models.GET.Bookmark;
import com.cookandroid.gocafestudy.models.GET.BookmarkIsSavedResponse;
import com.cookandroid.gocafestudy.models.GET.BookmarkListResponse;
import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.MyPageInfo;
import com.cookandroid.gocafestudy.models.GET.Review;
import com.cookandroid.gocafestudy.models.POST.BookmarkCreateResponse;
import com.cookandroid.gocafestudy.repository.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFragment extends Fragment {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        loadMyPageInfo(view);
        loadSavedCafes(view);

        // --- 버튼 클릭 ---
        View menuMyReviews = view.findViewById(R.id.menu_my_reviews);
        if (menuMyReviews != null) {
            menuMyReviews.setOnClickListener(v ->
                    startActivity(new Intent(getContext(), ActivityMyReviews.class))
            );
        }

        TextView btnViewAllSaved = view.findViewById(R.id.btn_view_all_saved);
        if (btnViewAllSaved != null) {
            btnViewAllSaved.setOnClickListener(v ->
                    startActivity(new Intent(getContext(), ActivitySavedCafes.class))
            );
        }

        Button btnLogout = view.findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                requireContext()
                        .getSharedPreferences("auth", Context.MODE_PRIVATE)
                        .edit()
                        .remove("access_token")
                        .remove("refresh_token")
                        .apply();

                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        }
    }

    // ★ 다른 액티비티(리뷰 삭제 / 저장 카페 관리) 다녀온 뒤에 자동 리프레시
    @Override
    public void onResume() {
        super.onResume();
        if (rootView != null) {
            loadMyPageInfo(rootView);
            loadSavedCafes(rootView);
        }
    }

    // -----------------------------
    // 1. 마이페이지 정보 로드
    // -----------------------------
    private void loadMyPageInfo(View view) {
        TextView tvProfileName = view.findViewById(R.id.tv_profile_name);
        TextView tvReviewCount = view.findViewById(R.id.tv_review_count);
        TextView tvBookmarkCount = view.findViewById(R.id.tv_bookmark_count);

        RetrofitClient.getUserApi(getContext())
                .getMyPage()
                .enqueue(new Callback<MyPageInfo>() {
                    @Override
                    public void onResponse(Call<MyPageInfo> call, Response<MyPageInfo> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MyPageInfo info = response.body();
                            tvProfileName.setText(info.getUser().getName());
                            tvReviewCount.setText(String.valueOf(info.getReviewCount()));
                            tvBookmarkCount.setText(String.valueOf(info.getBookmarkCount()));

                            Log.d("MyPage", "마이페이지 불러오기 성공");
                        } else {
                            Log.e("MyPage", "불러오기 실패: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPageInfo> call, Throwable t) {
                        Log.e("MyPage", "네트워크 오류", t);
                    }
                });
    }

    // -----------------------------
    // 2. 북마크 카페 리스트 로드
    // -----------------------------
    private void loadSavedCafes(View view) {
        RecyclerView rvSavedCafes = view.findViewById(R.id.rv_saved_cafes);
        rvSavedCafes.setLayoutManager(new LinearLayoutManager(getContext()));

        RetrofitClient.getBookmarkApi(getContext())
                .getMyBookmarks()
                .enqueue(new Callback<BookmarkListResponse>() {
                    @Override
                    public void onResponse(Call<BookmarkListResponse> call, Response<BookmarkListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Bookmark> list = response.body().getBookmarks();

                            List<Bookmark> preview = new ArrayList<>();
                            for (int i = 0; i < Math.min(3, list.size()); i++) {
                                preview.add(list.get(i));
                            }

                            SavedCafesAdapter adapter = new SavedCafesAdapter(
                                    getContext(),
                                    preview,
                                    cafeId -> showCafeDetailBottomSheet(cafeId)
                            );
                            rvSavedCafes.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkListResponse> call, Throwable t) {
                        Log.e("BookMark", "북마크 불러오기 실패", t);
                    }
                });
    }

    // -----------------------------
    // 3. BottomSheet 상세보기
    // -----------------------------
    private void showCafeDetailBottomSheet(int cafeId) {
        RetrofitClient.getCafeApi()
                .getCafeDetail(cafeId)
                .enqueue(new Callback<CafeDetail>() {
                    @Override
                    public void onResponse(Call<CafeDetail> call, Response<CafeDetail> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayCafeDetailSheet(cafeId, response.body());
                        } else {
                            Toast.makeText(getContext(), "카페 상세 로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CafeDetail> call, Throwable t) {
                        Log.e("CafeDetail", "네트워크 오류", t);
                    }
                });
    }

    private void displayCafeDetailSheet(int cafeId, CafeDetail cafe) {
        if (cafe == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_cafe_detail, null);

        // --- 기본 정보 ---
        TextView tvName = v.findViewById(R.id.tv_cafe_name);
        TextView tvAddress = v.findViewById(R.id.tv_address);
        TextView tvHours = v.findViewById(R.id.tv_hours);
        TextView tvTel = v.findViewById(R.id.tel);
        TextView tvMood = v.findViewById(R.id.cafe_atmosphere);
        TextView tvPrice = v.findViewById(R.id.cafe_price);
        TextView tvParking = v.findViewById(R.id.cafe_parking);
        TextView tvRating = v.findViewById(R.id.tv_rating);
        Button btnReview = v.findViewById(R.id.btn_view_all_saved);
        TextView tvAiSummary = v.findViewById(R.id.tv_ai_summary);
        TextView tvDescription = v.findViewById(R.id.description);

        ImageView ivMain = v.findViewById(R.id.iv_cafe_image);
        ImageView ivSub1 = v.findViewById(R.id.iv_cafe_sub1);
        ImageView ivSub2 = v.findViewById(R.id.iv_cafe_sub2);
        ImageView ivSub3 = v.findViewById(R.id.iv_cafe_sub3);
        ImageView ivSub4 = v.findViewById(R.id.iv_cafe_sub4);

        RecyclerView rvPreviewReviews = v.findViewById(R.id.rv_preview_reviews);
        rvPreviewReviews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // --- 데이터 연결 ---
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

        // --- 이미지 로드 ---
        List<String> images = cafe.getImages();
        if (images != null) {
            if (images.size() > 0)
                Glide.with(requireContext()).load(images.get(0)).placeholder(R.drawable.ic_cafe1_img).into(ivMain);
            if (images.size() > 1)
                Glide.with(requireContext()).load(images.get(1)).placeholder(R.drawable.ic_cafe1_img).into(ivSub1);
            if (images.size() > 2)
                Glide.with(requireContext()).load(images.get(2)).placeholder(R.drawable.ic_cafe1_img).into(ivSub2);
            if (images.size() > 3)
                Glide.with(requireContext()).load(images.get(3)).placeholder(R.drawable.ic_cafe1_img).into(ivSub3);
            if (images.size() > 4)
                Glide.with(requireContext()).load(images.get(4)).placeholder(R.drawable.ic_cafe1_img).into(ivSub4);
        }

        // --- 리뷰 최근 3개 ---
        List<Review> recentReviews = cafe.getRecentReviews();
        if (recentReviews == null) recentReviews = new ArrayList<>();
        List<Review> previewReviews = new ArrayList<>();
        for (int i = 0; i < Math.min(3, recentReviews.size()); i++) {
            previewReviews.add(recentReviews.get(i));
        }
        rvPreviewReviews.setAdapter(new ReviewAdapter(previewReviews));

        // --- 리뷰 전체보기 버튼 ---
        btnReview.setOnClickListener(click -> {
            Intent intent = new Intent(requireContext(), ActivityReviewList.class);
            intent.putExtra("cafeId", cafeId);
            startActivity(intent);
            dialog.dismiss();
        });

        // --- 북마크 버튼 ---
        Button btnSave = v.findViewById(R.id.btn_save);
        loadBookmarkState(getContext(), cafeId, btnSave);
        btnSave.setOnClickListener(view -> toggleBookmark(getContext(), cafeId, btnSave));

        dialog.setContentView(v);
        dialog.show();
    }

    // -----------------------------
    // 4. 북마크 API
    // -----------------------------
    private void loadBookmarkState(Context context, int cafeId, Button btnSave) {
        RetrofitClient.getBookmarkApi(context)
                .getBookmarkState(cafeId)
                .enqueue(new Callback<BookmarkIsSavedResponse>() {
                    @Override
                    public void onResponse(Call<BookmarkIsSavedResponse> call, Response<BookmarkIsSavedResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean saved = response.body().isSaved();
                            btnSave.setText(saved ? "저장 취소하기" : "저장하기");
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

    private void createBookmark(Context context, int cafeId, Button btnSave) {
        RetrofitClient.getBookmarkApi(context)
                .createBookmark(cafeId)
                .enqueue(new Callback<BookmarkCreateResponse>() {
                    @Override
                    public void onResponse(Call<BookmarkCreateResponse> call, Response<BookmarkCreateResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            btnSave.setText("저장 취소하기");

                            // ★ 북마크 생성 후 마이페이지 정보 & 북마크 목록 새로고침
                            if (rootView != null) {
                                loadMyPageInfo(rootView);
                                loadSavedCafes(rootView);
                            }
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

    private void deleteBookmark(Context context, int cafeId, Button btnSave) {
        RetrofitClient.getBookmarkApi(context)
                .deleteBookmark(cafeId)
                .enqueue(new Callback<BookmarkDeleteResponse>() {
                    @Override
                    public void onResponse(Call<BookmarkDeleteResponse> call, Response<BookmarkDeleteResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            btnSave.setText("저장하기");

                            // ★ 북마크 삭제 후 마이페이지 정보 & 북마크 목록 새로고침
                            if (rootView != null) {
                                loadMyPageInfo(rootView);
                                loadSavedCafes(rootView);
                            }
                        } else {
                            Log.e("BookmarkDELETE", "삭제 실패: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkDeleteResponse> call, Throwable t) {
                        Log.e("BookmarkDELETE", "네트워크 오류", t);
                    }
                });
    }

    private void toggleBookmark(Context context, int cafeId, Button btnSave) {
        String text = btnSave.getText().toString();
        if (text.equals("저장하기")) {
            createBookmark(context, cafeId, btnSave);
        } else {
            deleteBookmark(context, cafeId, btnSave);
        }
    }
}
