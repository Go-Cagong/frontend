package com.cookandroid.gocafestudy.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cookandroid.gocafestudy.R;

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

        // 필터 옵션 초기화
        filterMap.put("분위기", Arrays.asList("조용함", "편안함", "활기참"));
        filterMap.put("아메리카노 가격", Arrays.asList("3000원 이하", "3000~5000원", "5000원 이상"));
        filterMap.put("주차", Arrays.asList("가능", "불가능"));

        setupFilterButtons();
    }

    private void setupFilterButtons() {
        for (int i = 0; i < filterContainer.getChildCount(); i++) {
            final Button filterBtn = (Button) filterContainer.getChildAt(i);
            final String key = filterBtn.getText().toString();

            filterBtn.setOnClickListener(v -> {
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
                TextView textView = view.findViewById(android.R.id.text1);

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

        popup.setBackgroundDrawable(null);
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
