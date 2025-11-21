package com.cookandroid.gocafestudy.fragments;

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

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.activities.ActivityMyReviews;
import com.cookandroid.gocafestudy.activities.ActivitySavedCafes;
import com.cookandroid.gocafestudy.adapters.SavedCafesAdapter;
import com.cookandroid.gocafestudy.datas.MockData;
import com.cookandroid.gocafestudy.models.Bookmark;
import com.cookandroid.gocafestudy.models.CafeItem;

import java.util.ArrayList;
import java.util.List;

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
        // ★ Bookmark → CafeItem 변환 (ActivitySavedCafes와 동일한 로직)
        List<Bookmark> bookmarks = MockData.getBookmarks();
        List<CafeItem> allSaved = new ArrayList<>();

        for (Bookmark b : bookmarks) {
            allSaved.add(new CafeItem(
                    b.getCafeName(),
                    b.getAddress(),
                    b.getMainImageUrl()
            ));
        }

        // ★ 3개만 표시
        List<CafeItem> previewList = new ArrayList<>();
        for (int i = 0; i < Math.min(3, allSaved.size()); i++) {
            previewList.add(allSaved.get(i));
        }

        SavedCafesAdapter adapter = new SavedCafesAdapter(getContext(), previewList);
        rvSavedCafes.setAdapter(adapter);

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
