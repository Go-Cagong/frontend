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
import com.cookandroid.gocafestudy.adapters.SavedCafesAdapter;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.models.DELETE.BookmarkDeleteResponse;
import com.cookandroid.gocafestudy.models.GET.Bookmark;
import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.MyPageInfo;
import com.cookandroid.gocafestudy.models.POST.BookmarkCreateResponse;
import com.cookandroid.gocafestudy.repository.MockRepository;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.cookandroid.gocafestudy.datas.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MyFragment extends Fragment {

    private MockRepository repository;
    private ApiClient.CafeApi cafeApi;  

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        repository = new MockRepository();
        cafeApi = ApiClient.getCafeApi(requireContext());

        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ 서버에서 마이페이지 정보 불러오기
        loadMyPageInfo(view);


        // ✅ 로그아웃 버튼 기능 연결
        Button btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            requireContext()
                    .getSharedPreferences("auth", Context.MODE_PRIVATE)
                    .edit()
                    .remove("access_token")
                    .apply();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadMyPageInfo(View view) {
        TextView tvProfileName = view.findViewById(R.id.tv_profile_name);
        TextView tvReviewCount = view.findViewById(R.id.tv_review_count);
        TextView tvBookmarkCount = view.findViewById(R.id.tv_bookmark_count);

        cafeApi.getMyPageInfo().enqueue(new Callback<MyPageInfo>() {
            @Override
            public void onResponse(Call<MyPageInfo> call, Response<MyPageInfo> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(requireContext(), "마이페이지 응답 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("MyFragment", "Error code: " + response.code());
                    return;
                }

                MyPageInfo body = response.body();
                if (body == null) {
                    Toast.makeText(requireContext(), "JSON 구조가 DTO와 안 맞거나 응답이 null 입니다", Toast.LENGTH_SHORT).show();
                    Log.e("MyFragment", "Response body null");
                    return;
                }

                tvProfileName.setText(body.getUser().getName());
                tvReviewCount.setText(String.valueOf(body.getCounts().getReviewCount()));
                tvBookmarkCount.setText(String.valueOf(body.getCounts().getBookmarkCount()));
            }

            @Override
            public void onFailure(Call<MyPageInfo> call, Throwable t) {
                Toast.makeText(requireContext(), "서버 통신 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MyFragment", "Network error", t);
            }
        });
    }
}
