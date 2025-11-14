package com.cookandroid.gocafestudy;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;

public class ActivityWriteReview extends AppCompatActivity {

    private EditText etReview;
    private Button btnSubmit, btnCamera, btnGallery;
    private ImageView ivReceiptPreview, ivRemoveImage;
    private LinearLayout layoutReceiptStatus, layoutImagePlaceholder;
    private TextView tvReceiptStatus, tvCharCount;
    private RatingBar ratingBar;

    private ImageView ivReceiptStatusIcon;

    private boolean receiptVerified = false;

    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        etReview = findViewById(R.id.etReview);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        ivReceiptPreview = findViewById(R.id.ivReceiptPreview);
        ivRemoveImage = findViewById(R.id.ivRemoveImage);
        layoutReceiptStatus = findViewById(R.id.layoutReceiptStatus);
        layoutImagePlaceholder = findViewById(R.id.layoutImagePlaceholder);
        tvReceiptStatus = findViewById(R.id.tvReceiptStatus);
        tvCharCount = findViewById(R.id.tvCharCount);
        ratingBar = findViewById(R.id.ratingBar);
        ivReceiptStatusIcon = findViewById(R.id.ivReceiptStatusIcon);

        // 글자 수 표시
        etReview.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(s.length() + "자");
                checkSubmitEnable();
            }
            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });

        // 별점 변경 시
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> checkSubmitEnable());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Activity Result API
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), this::setReceiptImage);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if(uri != null){
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    setReceiptImage(bitmap);
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "이미지 처리 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCamera.setOnClickListener(v -> {
            if(checkPermission()) cameraLauncher.launch(null);
        });

        btnGallery.setOnClickListener(v -> {
            if(checkPermission()) galleryLauncher.launch("image/*");
        });

        ivRemoveImage.setOnClickListener(v -> removeReceipt());

        btnSubmit.setOnClickListener(v -> {
            String reviewText = etReview.getText().toString();
            android.content.Intent intent = new android.content.Intent();
            intent.putExtra("review", reviewText);
            setResult(RESULT_OK, intent);
            finish();
        });

        // 초기 상태
        btnSubmit.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnSubmit.setEnabled(false);
    }
    // 영수증 인식 완료 시
    private void setReceiptImage(Bitmap bitmap){
        ivReceiptPreview.setImageBitmap(bitmap);
        ivReceiptPreview.setVisibility(View.VISIBLE);
        ivRemoveImage.setVisibility(View.VISIBLE);
        layoutImagePlaceholder.setVisibility(View.GONE);

        receiptVerified = true;
        layoutReceiptStatus.setVisibility(View.VISIBLE);
        tvReceiptStatus.setText("영수증 인식 완료");

        // 상태 아이콘 표시
        ivReceiptStatusIcon.setVisibility(View.VISIBLE);
        ivReceiptStatusIcon.setImageResource(R.drawable.ic_check_circle);

        checkSubmitEnable();
    }

    private void removeReceipt(){
        ivReceiptPreview.setVisibility(View.GONE);
        ivRemoveImage.setVisibility(View.GONE);
        layoutImagePlaceholder.setVisibility(View.VISIBLE);

        receiptVerified = false;
        layoutReceiptStatus.setVisibility(View.VISIBLE);
        tvReceiptStatus.setText("영수증 사진을 올려야 리뷰 등록이 가능합니다.");

        // 상태 아이콘 표시
        ivReceiptStatusIcon.setVisibility(View.VISIBLE);
        ivReceiptStatusIcon.setImageResource(R.drawable.ic_error_circle);

        checkSubmitEnable();
    }



    private void checkSubmitEnable(){
        boolean enable = etReview.getText().length() >= 10 && receiptVerified && ratingBar.getRating() > 0;
        btnSubmit.setEnabled(enable);

        // 버튼 색상 변경
        if(enable){
            btnSubmit.setBackgroundColor(getResources().getColor(R.color.yellow_primary));
        }else{
            btnSubmit.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 2000);
            return false;
        }
        return true;
    }
}
