package com.cookandroid.gocafestudy.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.repository.MockRepository;
import com.cookandroid.gocafestudy.models.POST.ReviewCreateRequest;
import com.cookandroid.gocafestudy.models.POST.ReviewCreateResponse;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityWriteReview extends AppCompatActivity {

    private EditText etReview;
    private Button btnSubmit, btnCamera, btnGallery;

    // --- 기존 영수증 관련 ---
    private ImageView ivReceiptPreview, ivRemoveImage, ivReceiptStatusIcon;
    private LinearLayout layoutReceiptStatus, layoutImagePlaceholder;
    private TextView tvReceiptStatus, tvCharCount;
    private RatingBar ratingBar;
    private boolean receiptVerified = false;

    // --- 기존 이미지 선택용 ---
    private List<String> selectedImages = new ArrayList<>();

    // --- 리뷰 사진 추가용 ---
    private LinearLayout layoutReviewImages;
    private Button btnReviewCamera, btnReviewGallery;
    private List<Bitmap> reviewBitmaps = new ArrayList<>();
    private static final int MAX_REVIEW_IMAGES = 5;

    // --- ActivityResultLauncher ---
    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Void> reviewCameraLauncher;
    private ActivityResultLauncher<String> reviewGalleryLauncher;

    private static final int PERMISSION_CAMERA_REQUEST = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // --- View 초기화 ---
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

        // --- 리뷰 사진 관련 View ---
        layoutReviewImages = findViewById(R.id.layoutReviewImages);
        btnReviewCamera = findViewById(R.id.btnReviewCamera);
        btnReviewGallery = findViewById(R.id.btnReviewGallery);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // --- 글자 수 감지 ---
        etReview.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(s.length() + "자");
                checkSubmitEnable();
            }
        });

        // --- 별점 감지 ---
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> checkSubmitEnable());

        // --- 기존 ActivityResultLauncher 등록 ---
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

        // --- 리뷰 사진 추가용 ActivityResultLauncher ---
        reviewCameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                this::addReviewImage
        );
        reviewGalleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            addReviewImage(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "이미지 처리 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // --- 기존 버튼 클릭 ---
        btnCamera.setOnClickListener(v -> checkPermissionAndLaunchCamera());
        btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        ivRemoveImage.setOnClickListener(v -> removeReceipt());
        btnSubmit.setOnClickListener(v -> submitReview());

        // --- 리뷰 사진 버튼 클릭 ---
        btnReviewCamera.setOnClickListener(v -> {
            if (reviewBitmaps.size() >= MAX_REVIEW_IMAGES) {
                Toast.makeText(this, "최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            reviewCameraLauncher.launch(null);
        });

        btnReviewGallery.setOnClickListener(v -> {
            if (reviewBitmaps.size() >= MAX_REVIEW_IMAGES) {
                Toast.makeText(this, "최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            reviewGalleryLauncher.launch("image/*");
        });

        // 초기 버튼 상태
        btnSubmit.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnSubmit.setEnabled(false);
    }

    // --- 권한 체크 및 카메라 실행 ---
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

    // --- 기존 영수증 제거 ---
    private void removeReceipt() {
        ivReceiptPreview.setVisibility(View.GONE);
        ivRemoveImage.setVisibility(View.GONE);
        layoutImagePlaceholder.setVisibility(View.VISIBLE);

        receiptVerified = false;
        layoutReceiptStatus.setVisibility(View.VISIBLE);
        tvReceiptStatus.setText("영수증 사진을 올려야 리뷰 등록이 가능합니다.");
        ivReceiptStatusIcon.setVisibility(View.VISIBLE);
        ivReceiptStatusIcon.setImageResource(R.drawable.ic_error_circle);

        checkSubmitEnable();
    }

    // --- 기존 영수증 OCR 처리 ---
    private void processReceiptBitmap(Bitmap bitmap) {
        if (bitmap == null) return;

        ivReceiptPreview.setImageBitmap(bitmap);
        ivReceiptPreview.setVisibility(View.VISIBLE);
        ivRemoveImage.setVisibility(View.VISIBLE);
        layoutImagePlaceholder.setVisibility(View.GONE);

        layoutReceiptStatus.setVisibility(View.VISIBLE);
        tvReceiptStatus.setText("영수증 인식 중...");
        ivReceiptStatusIcon.setVisibility(View.GONE);

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build())
                .process(image)
                .addOnSuccessListener(this::analyzeText)
                .addOnFailureListener(e -> {
                    tvReceiptStatus.setText("인식 오류 ❌");
                    ivReceiptStatusIcon.setVisibility(View.VISIBLE);
                    ivReceiptStatusIcon.setImageResource(R.drawable.ic_error_circle);
                });
    }

    private void analyzeText(Text result) {
        String text = result.getText();
        if (text.isEmpty()) {
            tvReceiptStatus.setText("❌ 텍스트 인식 실패 (감지된 글자 없음)");
            ivReceiptStatusIcon.setVisibility(View.VISIBLE);
            ivReceiptStatusIcon.setImageResource(R.drawable.ic_error_circle);
            receiptVerified = false;
            checkSubmitEnable();
            return;
        }

        List<String> keywords = Arrays.asList("합계", "금액", "원", "결제", "영수증", "카드", "매장명", "일자", "승인", "구매", "POS");
        int matchCount = 0;
        for (String keyword : keywords) {
            if (text.contains(keyword)) matchCount++;
        }

        receiptVerified = matchCount >= 2;

        if (receiptVerified) {
            tvReceiptStatus.setText("✅ 영수증으로 인식됨");
            ivReceiptStatusIcon.setVisibility(View.VISIBLE);
            ivReceiptStatusIcon.setImageResource(R.drawable.ic_check_circle);
        } else {
            tvReceiptStatus.setText("❌ 영수증이 아님");
            ivReceiptStatusIcon.setVisibility(View.VISIBLE);
            ivReceiptStatusIcon.setImageResource(R.drawable.ic_error_circle);
        }

        checkSubmitEnable();
    }

    // --- 리뷰 사진 추가 ---
    private void addReviewImage(Bitmap bitmap) {
        if (bitmap == null) return;

        if (reviewBitmaps.size() >= MAX_REVIEW_IMAGES) {
            Toast.makeText(this, "최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewBitmaps.add(bitmap);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160, 160);
        params.setMargins(0, 0, 16, 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);

        // 클릭 시 제거 가능
        imageView.setOnClickListener(v -> {
            layoutReviewImages.removeView(imageView);
            reviewBitmaps.remove(bitmap);
        });
        final HorizontalScrollView scrollView = findViewById(R.id.scrollReviewImages);
        scrollView.post(() -> scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT));

        layoutReviewImages.addView(imageView);
    }

    // --- 제출 버튼 활성화 체크 ---
    private void checkSubmitEnable() {
        boolean enable = etReview.getText().length() >= 10 && receiptVerified && ratingBar.getRating() > 0;
        btnSubmit.setEnabled(enable);
        btnSubmit.setBackgroundColor(enable ?
                getResources().getColor(R.color.yellow_primary) :
                getResources().getColor(android.R.color.darker_gray));
    }

    private static final int MOCK_USER_ID = 1;

    // --- 리뷰 제출 ---
    private void submitReview() {
        String reviewText = etReview.getText().toString();
        int rating = (int) ratingBar.getRating();
        int cafeId = getIntent().getIntExtra("cafeId", -1);
        int userId = MOCK_USER_ID;

        // 리뷰 이미지 bitmaps -> 임시 String 리스트로 변환
        // 백엔드 연결시 멀티파트바디로 교체
        List<String> reviewImageStrings = new ArrayList<>();
        for (int i = 0; i < reviewBitmaps.size(); i++) {
            reviewImageStrings.add("review_image_" + i);
        }

        ReviewCreateRequest request = new ReviewCreateRequest(rating, reviewText, reviewImageStrings);

        MockRepository repository = new MockRepository();
        ReviewCreateResponse response = repository.addReview(cafeId, request, userId);

        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

        android.content.Intent intent = new android.content.Intent();
        intent.putExtra("newReviewId", response.getReview().getReviewId());
        setResult(RESULT_OK, intent);

        finish();
    }
}