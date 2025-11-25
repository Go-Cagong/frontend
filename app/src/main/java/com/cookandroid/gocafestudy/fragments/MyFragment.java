package com.cookandroid.gocafestudy.fragments;

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
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.adapters.SavedCafesAdapter;
import com.cookandroid.gocafestudy.models.DELETE.BookmarkDeleteResponse;
import com.cookandroid.gocafestudy.models.GET.Bookmark;
import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.MyPageInfo;
import com.cookandroid.gocafestudy.models.GET.Review;
import com.cookandroid.gocafestudy.models.POST.BookmarkCreateResponse;
import com.cookandroid.gocafestudy.repository.MockRepository;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class MyFragment extends Fragment {

    private MockRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        repository = new MockRepository();
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. 마이페이지 정보 표시 ---
        MyPageInfo myPageInfo = repository.getMyPageInfo(1);
        TextView tvProfileName = view.findViewById(R.id.tv_profile_name);
        TextView tvReviewCount = view.findViewById(R.id.tv_review_count);
        TextView tvBookmarkCount = view.findViewById(R.id.tv_bookmark_count);

        tvProfileName.setText(myPageInfo.getUser().getName());
        tvReviewCount.setText(String.valueOf(myPageInfo.getReviewCount()));
        tvBookmarkCount.setText(String.valueOf(myPageInfo.getBookmarkCount()));

        // --- 2. RecyclerView 설정 ---
        RecyclerView rvSavedCafes = view.findViewById(R.id.rv_saved_cafes);
        rvSavedCafes.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Bookmark> allBookmarks = repository.getBookmarksByUserId(1);
        List<Bookmark> previewBookmarks = new ArrayList<>();
        for (int i = 0; i < Math.min(3, allBookmarks.size()); i++) {
            previewBookmarks.add(allBookmarks.get(i));
        }

        SavedCafesAdapter adapter = new SavedCafesAdapter(
                getContext(),
                previewBookmarks,
                cafeId -> {
                    Log.d("MyFragment", "Clicked cafeId=" + cafeId);
                    showCafeDetailBottomSheet(cafeId);
                }
        );
        rvSavedCafes.setAdapter(adapter);

        // --- 3. 버튼 클릭 ---
        View menuMyReviews = view.findViewById(R.id.menu_my_reviews);
        if (menuMyReviews != null) {
            menuMyReviews.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), ActivityMyReviews.class));
            });
        }

        TextView btnViewAllSaved = view.findViewById(R.id.btn_view_all_saved);
        if (btnViewAllSaved != null) {
            btnViewAllSaved.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), ActivitySavedCafes.class));
            });
        }
    }

    // -----------------------------
    // BottomSheet 메서드
    // -----------------------------
    private void showCafeDetailBottomSheet(int cafeId) {
        CafeDetail cafe = repository.getCafeDetail(cafeId);
        if (cafe == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_cafe_detail, null);

        // --- 뷰 초기화 ---
        TextView tvName = v.findViewById(R.id.tv_cafe_name);
        TextView tvAddress = v.findViewById(R.id.tv_address);
        TextView tvHours = v.findViewById(R.id.tv_hours);
        TextView tvTel = v.findViewById(R.id.tel);
        TextView tvMood = v.findViewById(R.id.cafe_atmosphere);
        TextView tvPrice = v.findViewById(R.id.cafe_price);
        TextView tvParking = v.findViewById(R.id.cafe_parking);
        TextView tvAiSummary = v.findViewById(R.id.tv_ai_summary);
        TextView tvRating = v.findViewById(R.id.tv_rating);
        Button btnReview = v.findViewById(R.id.btn_view_all_saved);

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
        tvAiSummary.setText(cafe.getDescription());
        tvRating.setText(String.format("%.1f / 5.0", cafe.getReviewAverage()));

        List<String> images = cafe.getImages();
        if (images.size() > 0) Glide.with(requireContext()).load(images.get(0)).placeholder(R.drawable.ic_cafe1_img).into(ivMain);
        if (images.size() > 1) Glide.with(requireContext()).load(images.get(1)).placeholder(R.drawable.ic_cafe1_img).into(ivSub1);
        if (images.size() > 2) Glide.with(requireContext()).load(images.get(2)).placeholder(R.drawable.ic_cafe1_img).into(ivSub2);
        if (images.size() > 3) Glide.with(requireContext()).load(images.get(3)).placeholder(R.drawable.ic_cafe1_img).into(ivSub3);
        if (images.size() > 4) Glide.with(requireContext()).load(images.get(4)).placeholder(R.drawable.ic_cafe1_img).into(ivSub4);

        // --- 리뷰 연결 (최근 3개) ---
        List<Review> recentReviews = cafe.getRecentReviews();
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


        Button btnSave = v.findViewById(R.id.btn_save);
        if (cafe.isSaved()) {
            // true일 때
            btnSave.setText("삭제");
            btnSave.setOnClickListener(click -> {
                BookmarkDeleteResponse response = repository.deleteBookmark(cafeId);
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show();
            });
        } else {
            // false일 때
            btnSave.setOnClickListener(click -> {
                BookmarkCreateResponse response = repository.createBookmark(cafeId);
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show();
            });
        }

        dialog.setContentView(v);
        dialog.show();
    }
}
