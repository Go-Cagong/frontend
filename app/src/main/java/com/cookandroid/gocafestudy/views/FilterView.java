package com.cookandroid.gocafestudy.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cookandroid.gocafestudy.R;
import com.google.android.material.button.MaterialButton;

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

    // ------------------------------
    // 콜백 인터페이스
    // ------------------------------
    private OnFilterChangeListener filterChangeListener;

    public interface OnFilterChangeListener {
        void onFilterChanged(Map<String, String> appliedFilters);
    }

    public void setOnFilterChangeListener(OnFilterChangeListener listener) {
        this.filterChangeListener = listener;
    }

    // ------------------------------
    // 생성자
    // ------------------------------
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

        // 필터 옵션 초기화 (이모지 포함된 키로 매핑)
        filterMap.put("분위기", Arrays.asList("조용함", "편안함", "활기참"));
        filterMap.put("가격", Arrays.asList("3000원 이하", "3000~5000원", "5000원 이상"));
        filterMap.put("주차", Arrays.asList("가능", "불가능"));

        setupFilterButtons();
    }

    private void setupFilterButtons() {
        for (int i = 0; i < filterContainer.getChildCount(); i++) {
            final MaterialButton filterBtn = (MaterialButton) filterContainer.getChildAt(i);
            final String key = filterBtn.getText().toString();

            filterBtn.setOnClickListener(v -> {
                if (currentPopup != null) currentPopup.dismiss();

                currentKey = key;
                showPopup(filterBtn, filterMap.get(key));
                updateFilterButtonColors();
            });
        }
    }

    private void showPopup(MaterialButton anchor, List<String> options) {
        Context context = getContext();

        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setPadding(16, 16, 16, 16);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, options) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);

                String option = options.get(position);
                boolean isSelected = appliedFilters.containsKey(currentKey)
                        && appliedFilters.get(currentKey).equals(option);

                // Toss Style - 깔끔한 디자인
                if (isSelected) {
                    textView.setBackgroundResource(R.drawable.bg_popup_item_selected);
                    textView.setTextColor(context.getColor(R.color.gray_900));
                } else {
                    textView.setBackgroundResource(R.drawable.bg_popup_item);
                    textView.setTextColor(context.getColor(R.color.gray_700));
                }

                textView.setPadding(32, 20, 32, 20);
                textView.setTextSize(15);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                // 아이템 간격
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 8);
                textView.setLayoutParams(params);

                return view;
            }
        };

        listView.setAdapter(adapter);

        PopupWindow popup = new PopupWindow(listView,
                (int) (anchor.getWidth() * 1.5),
                LayoutParams.WRAP_CONTENT,
                true);

        popup.setBackgroundDrawable(context.getDrawable(R.drawable.bg_filter_popup));
        popup.setOutsideTouchable(true);
        popup.setElevation(8);
        popup.setAnimationStyle(android.R.style.Animation_Dialog);
        currentPopup = popup;
        popup.showAsDropDown(anchor, 0, 8);

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
            adapter.notifyDataSetChanged();
            popup.dismiss();
            currentPopup = null;

            // ------------------------------
            // 콜백 호출
            // ------------------------------
            if (filterChangeListener != null) {
                filterChangeListener.onFilterChanged(new HashMap<>(appliedFilters));
            }
        });
    }

    private void updateFilterButtonColors() {
        for (int i = 0; i < filterContainer.getChildCount(); i++) {
            MaterialButton btn = (MaterialButton) filterContainer.getChildAt(i);
            String key = btn.getText().toString();

            if (appliedFilters.containsKey(key)) {
                btn.setBackgroundResource(R.drawable.bg_filter_chip_selected);
                btn.setTextColor(getContext().getColor(R.color.gray_900));
            } else {
                btn.setBackgroundResource(R.drawable.bg_filter_chip);
                btn.setTextColor(getContext().getColor(R.color.gray_700));
            }
        }
    }
}
