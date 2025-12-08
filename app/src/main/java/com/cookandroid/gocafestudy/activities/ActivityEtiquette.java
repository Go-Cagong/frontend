package com.cookandroid.gocafestudy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cookandroid.gocafestudy.R;

import java.util.Random;

public class ActivityEtiquette extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etiquette);

        // 뷰 참조
        TextView tvIcon = findViewById(R.id.tv_etiquette_icon);
        TextView tvMessage = findViewById(R.id.tv_etiquette_message);
        ConstraintLayout rootLayout = findViewById(R.id.root_layout);

        // 랜덤 에티켓 선택
        String[] messages = getResources().getStringArray(R.array.etiquette_messages);
        String[] icons = getResources().getStringArray(R.array.etiquette_icons);

        Random random = new Random();
        int randomIndex = random.nextInt(messages.length);

        // UI 업데이트
        tvIcon.setText(icons[randomIndex]);
        tvMessage.setText(messages[randomIndex]);

        // 화면 전체 클릭 리스너 - 터치하면 MainActivity로 이동
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 이동
                Intent intent = new Intent(ActivityEtiquette.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼 비활성화 (에티켓 화면은 반드시 거쳐야 함)
        // 원하면 super.onBackPressed()를 호출하여 뒤로가기 허용 가능
    }
}