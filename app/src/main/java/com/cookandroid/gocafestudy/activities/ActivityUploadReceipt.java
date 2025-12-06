package com.cookandroid.gocafestudy.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cookandroid.gocafestudy.R;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

public class ActivityUploadReceipt extends AppCompatActivity {

    private static final String TAG = "UploadReceiptActivity";
    private static final int PERMISSION_CAMERA_REQUEST = 2000;

    private ImageView ivReceiptPreview, ivRemoveImage, ivReceiptStatusIcon;
    private LinearLayout layoutReceiptStatus, layoutImagePlaceholder;
    private TextView tvReceiptStatus, tvCafeName;
    private MaterialButton btnNext, btnCamera, btnGallery;

    private boolean receiptVerified = false;
    private int cafeId;
    private String cafeName;

    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_receipt);

        // Intent에서 cafeId, cafeName 받기
        cafeId = getIntent().getIntExtra("cafeId", -1);
        cafeName = getIntent().getStringExtra("cafeName");

        if (cafeId == -1) {
            Toast.makeText(this, "카페 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // View 초기화
        ivReceiptPreview = findViewById(R.id.ivReceiptPreview);
        ivRemoveImage = findViewById(R.id.ivRemoveImage);
        layoutReceiptStatus = findViewById(R.id.layoutReceiptStatus);
        layoutImagePlaceholder = findViewById(R.id.layoutImagePlaceholder);
        tvReceiptStatus = findViewById(R.id.tvReceiptStatus);
        ivReceiptStatusIcon = findViewById(R.id.ivReceiptStatusIcon);
        tvCafeName = findViewById(R.id.tvCafeName);
        btnNext = findViewById(R.id.btnNext);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        tvCafeName.setText(cafeName != null ? cafeName : "");

        // Camera/Gallery Launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                this::processReceiptBitmap
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            processReceiptBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "이미지 처리 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Button Listeners
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnCamera.setOnClickListener(v -> checkPermissionAndLaunchCamera());
        btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        ivRemoveImage.setOnClickListener(v -> removeReceipt());
        btnNext.setOnClickListener(v -> goToWriteReview());

        updateNextButton();
    }

    private void checkPermissionAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST);
        } else {
            cameraLauncher.launch(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null);
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeReceipt() {
        ivReceiptPreview.setVisibility(View.GONE);
        ivRemoveImage.setVisibility(View.GONE);
        layoutImagePlaceholder.setVisibility(View.VISIBLE);
        layoutReceiptStatus.setVisibility(View.GONE);

        receiptVerified = false;
        updateNextButton();
    }

    private void processReceiptBitmap(Bitmap bitmap) {
        if (bitmap == null) return;

        ivReceiptPreview.setImageBitmap(bitmap);
        ivReceiptPreview.setVisibility(View.VISIBLE);
        ivRemoveImage.setVisibility(View.VISIBLE);
        layoutImagePlaceholder.setVisibility(View.GONE);

        layoutReceiptStatus.setVisibility(View.VISIBLE);
        tvReceiptStatus.setText("영수증 분석 중...");
        ivReceiptStatusIcon.setVisibility(View.GONE);

        receiptVerified = false;
        updateNextButton();

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build())
                .process(image)
                .addOnSuccessListener(this::analyzeText)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "OCR 실패", e);
                    tvReceiptStatus.setText("❌ 영수증을 인식하지 못했습니다");
                    ivReceiptStatusIcon.setVisibility(View.VISIBLE);
                    ivReceiptStatusIcon.setImageResource(R.drawable.ic_warning);
                    receiptVerified = false;
                    updateNextButton();
                });
    }

    private void analyzeText(Text result) {
        String fullText = result.getText();
        Log.d(TAG, "OCR 결과:\n" + fullText);

        if (fullText == null) fullText = "";

        String normalized = fullText.replace(" ", "").replace("\n", "").toLowerCase();

        String[] keywords = {
                "영수증", "합계", "결제", "금액", "카드", "현금",
                "승인", "부가세", "세액", "매출", "일시", "판매",
                "원", "vat", "total"
        };

        int keywordMatchCount = 0;
        for (String keyword : keywords) {
            if (normalized.contains(keyword)) {
                keywordMatchCount++;
                Log.d(TAG, "키워드 일치: " + keyword);
            }
        }

        final int KEYWORD_THRESHOLD = 3;
        boolean isReceipt = keywordMatchCount >= KEYWORD_THRESHOLD;

        if (isReceipt) {
            tvReceiptStatus.setText("영수증 인증 완료");
            ivReceiptStatusIcon.setVisibility(View.VISIBLE);
            ivReceiptStatusIcon.setImageResource(R.drawable.ic_check_circle);
            ivReceiptStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.naver_green));
            receiptVerified = true;
            Toast.makeText(this, "영수증 인증 완료!", Toast.LENGTH_SHORT).show();
        } else {
            tvReceiptStatus.setText("영수증으로 인식되지 않았습니다");
            ivReceiptStatusIcon.setVisibility(View.VISIBLE);
            ivReceiptStatusIcon.setImageResource(R.drawable.ic_warning);
            ivReceiptStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.red));
            receiptVerified = false;
            Toast.makeText(this, "영수증이 아닌 것 같아요", Toast.LENGTH_SHORT).show();
        }

        updateNextButton();
    }

    private void updateNextButton() {
        btnNext.setEnabled(receiptVerified);
    }

    private void goToWriteReview() {
        if (!receiptVerified) {
            Toast.makeText(this, "영수증 인증이 필요합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ActivityWriteReview.class);
        intent.putExtra("cafeId", cafeId);
        intent.putExtra("cafeName", cafeName);
        intent.putExtra("receiptVerified", true);
        startActivity(intent);
        finish();
    }
}
