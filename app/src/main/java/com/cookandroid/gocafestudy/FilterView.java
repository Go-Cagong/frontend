package com.cookandroid.gocafestudy;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterView extends LinearLayout {

    private LinearLayout filterContainer;
    private final Map<String, List<String>> filterMap = new HashMap<>();
    private final Map<String, String> appliedFilters = new HashMap<>();

    private PopupWindow currentPopup = null;
    private String currentKey = null;

    public FilterView(Context context) {
        super(context);
        init(context);
    }

    public FilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.filter_button, this, true);
        filterContainer = findViewById(R.id.filterContainer);

        // 필터 옵션 초기화
        filterMap.put("분위기", Arrays.asList("조용함", "대화가능", "시끄러움"));
        filterMap.put("콘센트", Arrays.asList("많음", "중간", "적음"));
        filterMap.put("가격", Arrays.asList("저렴함", "보통", "비쌈"));
        filterMap.put("와이파이", Arrays.asList("있음", "없음"));

        setupFilterButtons();
    }

    private void setupFilterButtons() {
        for (int i = 0; i < filterContainer.getChildCount(); i++) {
            final Button filterBtn = (Button) filterContainer.getChildAt(i);
            final String key = filterBtn.getText().toString();

            filterBtn.setOnClickListener(v -> {
                // 기존 팝업 닫기
                if (currentPopup != null) currentPopup.dismiss();

                currentKey = key;
                showPopup(filterBtn, filterMap.get(key));
                updateFilterButtonColors();
            });
        }
    }

    private void showPopup(Button anchor, List<String> options) {
        Context context = getContext();

        ListView listView = new ListView(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, options) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                String option = options.get(position);
                boolean isSelected = appliedFilters.containsKey(currentKey)
                        && appliedFilters.get(currentKey).equals(option);

                textView.setBackgroundColor(isSelected ? Color.parseColor("#FFD700") : Color.WHITE);
                textView.setTextColor(isSelected ? Color.BLACK : Color.DKGRAY);

                return view;
            }
        };

        listView.setAdapter(adapter);

        PopupWindow popup = new PopupWindow(listView,
                anchor.getWidth(),
                LayoutParams.WRAP_CONTENT,
                true);

        popup.setBackgroundDrawable(null); // 필요시 배경 추가
        popup.setOutsideTouchable(true);
        currentPopup = popup;
        popup.showAsDropDown(anchor);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = options.get(position);

            // 토글 방식
            if (appliedFilters.containsKey(currentKey) &&
                    appliedFilters.get(currentKey).equals(selectedOption)) {
                appliedFilters.remove(currentKey);
            } else {
                appliedFilters.put(currentKey, selectedOption);
            }

            updateFilterButtonColors();
            adapter.notifyDataSetChanged(); // 여기서 옵션 색상 갱신
            popup.dismiss();
            currentPopup = null;
        });
    }

    private void updateFilterButtonColors() {
        for (int i = 0; i < filterContainer.getChildCount(); i++) {
            Button btn = (Button) filterContainer.getChildAt(i);
            String key = btn.getText().toString();

            if (appliedFilters.containsKey(key)) {
                btn.setBackgroundColor(Color.parseColor("#FFD700"));
                btn.setTextColor(Color.BLACK);
            } else {
                btn.setBackgroundColor(Color.WHITE);
                btn.setTextColor(Color.DKGRAY);
            }
        }
    }
}
