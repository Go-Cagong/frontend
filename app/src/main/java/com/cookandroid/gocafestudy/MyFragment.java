package com.cookandroid.gocafestudy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyFragment extends Fragment {

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

        // RecyclerView 초기화
        RecyclerView rvSavedCafes = view.findViewById(R.id.rv_saved_cafes);
        rvSavedCafes.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Set adapter for saved cafes

        // 기존 메뉴 클릭 이벤트
        View menuMyReviews = view.findViewById(R.id.menu_my_reviews);
        if (menuMyReviews != null) {
            menuMyReviews.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ActivityMyReviews.class);
                startActivity(intent);
            });
        }

        // 새롭게 추가: "View All Saved" 클릭 시 ActivitySavedCafes로 이동
        TextView btnViewAllSaved = view.findViewById(R.id.btn_view_all_saved);
        if (btnViewAllSaved != null) {
            btnViewAllSaved.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ActivitySavedCafes.class);
                startActivity(intent);
            });
        }
    }
}
