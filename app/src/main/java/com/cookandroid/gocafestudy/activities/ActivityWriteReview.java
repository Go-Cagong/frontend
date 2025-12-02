package com.cookandroid.gocafestudy.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.cookandroid.gocafestudy.api.CafeApi;
import com.cookandroid.gocafestudy.repository.RetrofitClient;
import com.cookandroid.gocafestudy.utils.UserSessionManager;
import com.cookandroid.gocafestudy.models.GET.UserResponse;
import com.cookandroid.gocafestudy.models.POST.ReviewCreateResponse;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityWriteReview extends AppCompatActivity {

    private static final String TAG = "WriteReviewActivity";

    private EditText etReview;
    private Button btnSubmit, btnCamera, btnGallery;

    // --- 영수증 관련 ---
    private ImageView ivReceiptPreview, ivRemoveImage, ivReceiptStatusIcon;
    private LinearLayout layoutReceiptStatus, layoutImagePlaceholder;
    private TextView tvReceiptStatus, tvCharCount;
    private RatingBar ratingBar;
    private boolean receiptVerified = false;   // 🔥 이제 기본값은 false

    // --- 리뷰 사진 관련 ---
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

        layoutReviewImages = findViewById(R.id.layoutReviewImages);
        btnReviewCamera = findViewById(R.id.btnReviewCamera);
        btnReviewGallery = findViewById(R.id.btnReviewGallery);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 🔥 초기에는 영수증 인증 안내만, verified=false
        layoutReceiptStatus.setVisibility(View.VISIBLE);
        tvReceiptStatus.setText("영수증 사진을 등록해 주세요.");
        ivReceiptStatusIcon.setVisibility(View.GONE); // 아직 인증 전

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

        // --- 영수증용 카메라/갤러리 ---
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

        // --- 리뷰 사진용 카메라/갤러리 ---
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

        // --- 버튼 클릭 리스너 ---
        btnCamera.setOnClickListener(v -> checkPermissionAndLaunchCamera());
        btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        ivRemoveImage.setOnClickListener(v -> removeReceipt());
        btnSubmit.setOnClickListener(v -> submitReview());

        btnReviewCamera.setOnClickListener(v -> {
            if (reviewBitmaps.size() >= MAX_REVIEW_IMAGES) {
                Toast.makeText(this, "최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            checkReviewImagePermissionAndLaunchCamera();
        });

        btnReviewGallery.setOnClickListener(v -> {
            if (reviewBitmaps.size() >= MAX_REVIEW_IMAGES) {
                Toast.makeText(this, "최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            reviewGalleryLauncher.launch("image/*");
        });

        checkSubmitEnable();
    }

    // --- 권한 체크 (영수증) ---
    private void checkPermissionAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST);
        } else {
            cameraLauncher.launch(null);
        }
    }

    // --- 권한 체크 (리뷰 사진) ---
    private void checkReviewImagePermissionAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST);
        } else {
            reviewCameraLauncher.launch(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 여기서는 영수증 카메라 쪽으로 통일
                cameraLauncher.launch(null);
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- 영수증 제거 ---
    private void removeReceipt() {
        ivReceiptPreview.setVisibility(View.GONE);
        ivRemoveImage.setVisibility(View.GONE);
        layoutImagePlaceholder.setVisibility(View.VISIBLE);

        receiptVerified = false;   // 🔥 다시 false로
        layoutReceiptStatus.setVisibility(View.VISIBLE);
        tvReceiptStatus.setText("영수증 사진을 등록해 주세요.");
        ivReceiptStatusIcon.setVisibility(View.GONE);

        checkSubmitEnable();
    }

    // --- 영수증 사진 처리 + OCR ---
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
        checkSubmitEnable();

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build())
                .process(image)
                .addOnSuccessListener(this::analyzeText)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "OCR 실패", e);
                    tvReceiptStatus.setText("영수증을 인식하지 못했습니다. 다시 시도해 주세요.");
                    ivReceiptStatusIcon.setVisibility(View.VISIBLE);
                    ivReceiptStatusIcon.setImageResource(R.drawable.ic_warning); // 경고 아이콘 하나 만들어놨으면 좋음
                    receiptVerified = false;
                    checkSubmitEnable();
                });
    }

    // --- OCR 결과 분석: 영수증 여부 판단 ---
    private void analyzeText(Text result) {
        String fullText = result.getText();
        Log.d(TAG, "OCR 결과:\n" + fullText);

        if (fullText == null) fullText = "";
        String normalized = fullText.replace(" ", "");

        // 간단한 규칙 기반 체크
        boolean hasKeyword =
                normalized.contains("영수증") ||
                        normalized.contains("합계")   ||
                        normalized.contains("결제")   ||
                        normalized.contains("금액")   ||
                        normalized.contains("카드")   ||
                        normalized.contains("현금")   ||
                        normalized.contains("승인");

        boolean hasWon = normalized.contains("원");
        boolean longEnough = fullText.length() > 20;

        boolean isReceipt = (hasKeyword && hasWon) || (hasWon && longEnough);

        if (isReceipt) {
            // 영수증으로 인정
            tvReceiptStatus.setText("✅ 영수증으로 인식되었습니다.");
            ivReceiptStatusIcon.setVisibility(View.VISIBLE);
            ivReceiptStatusIcon.setImageResource(R.drawable.ic_check_circle);
            receiptVerified = true;
            Toast.makeText(this, "영수증 인증 완료!", Toast.LENGTH_SHORT).show();
        } else {
            // 영수증 아님
            tvReceiptStatus.setText(" 영수증으로 인식되지 않았습니다. 다른 사진을 등록해 주세요.");
            ivReceiptStatusIcon.setVisibility(View.VISIBLE);
            ivReceiptStatusIcon.setImageResource(R.drawable.ic_warning);
            receiptVerified = false;
            Toast.makeText(this, "영수증이 아닌 것으로 인식되었습니다.", Toast.LENGTH_SHORT).show();
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
        int sizePx = (int) (160 * getResources().getDisplayMetrics().density);
        int marginPx = (int) (16 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
        params.setMargins(0, 0, marginPx, 0);

        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);

        imageView.setOnClickListener(v -> {
            layoutReviewImages.removeView(imageView);
            reviewBitmaps.remove(bitmap);
        });

        layoutReviewImages.addView(imageView);

        final HorizontalScrollView scrollView = findViewById(R.id.scrollReviewImages);
        scrollView.post(() -> scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT));
    }

    // --- 제출 버튼 활성화 ---
    private void checkSubmitEnable() {
        boolean enable = etReview.getText().length() >= 10
                && receiptVerified
                && ratingBar.getRating() > 0;
        btnSubmit.setEnabled(enable);
        btnSubmit.setBackgroundColor(enable ?
                ContextCompat.getColor(this, R.color.yellow_primary) :
                ContextCompat.getColor(this, android.R.color.darker_gray));
    }

    // --- Bitmap → File ---
    private File getTempFileFromBitmap(Context context, Bitmap bitmap, String fileName) throws IOException {
        File file = new File(context.getCacheDir(), fileName + ".jpg");
        if (file.exists()) file.delete();
        file.createNewFile();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        bos.close();
        return file;
    }

    // --- Bitmap → MultipartBody.Part ---
    private MultipartBody.Part createMultipartPart(Bitmap bitmap, String partName, int index) {
        try {
            File file = getTempFileFromBitmap(this, bitmap, "review_image_" + index);

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    file
            );

            return MultipartBody.Part.createFormData("images", file.getName(), requestFile);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create multipart part: " + e.getMessage());
            return null;
        }
    }

    // --- 리뷰 제출 ---
    private void submitReview() {
        String reviewText = etReview.getText().toString();
        int rating = (int) ratingBar.getRating();
        int cafeId = getIntent().getIntExtra("cafeId", -1);

        UserResponse currentUser = UserSessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cafeId == -1) {
            Toast.makeText(this, "카페 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody ratingBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(rating));
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), reviewText);

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (int i = 0; i < reviewBitmaps.size(); i++) {
            MultipartBody.Part part = createMultipartPart(reviewBitmaps.get(i), "images", i);
            if (part != null) {
                imageParts.add(part);
            }
        }

        CafeApi api = RetrofitClient.getAuthCafeApi(this);
        Call<ReviewCreateResponse> call = api.createReview(
                cafeId,
                ratingBody,
                contentBody,
                imageParts
        );

        call.enqueue(new Callback<ReviewCreateResponse>() {
            @Override
            public void onResponse(Call<ReviewCreateResponse> call, Response<ReviewCreateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewCreateResponse reviewResponse = response.body();
                    Toast.makeText(ActivityWriteReview.this, "리뷰 등록 성공: " + reviewResponse.getMessage(), Toast.LENGTH_LONG).show();

                    android.content.Intent intent = new android.content.Intent();
                    intent.putExtra("newReviewId", reviewResponse.getReview().getReviewId());
                    setResult(RESULT_OK, intent);

                } else {
                    String errorMsg = "리뷰 등록 실패: HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Review API Error: " + errorBody);
                            errorMsg += "\n" + errorBody;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ActivityWriteReview.this, errorMsg, Toast.LENGTH_LONG).show();
                }
                finish();
            }

            @Override
            public void onFailure(Call<ReviewCreateResponse> call, Throwable t) {
                Log.e(TAG, "Review API Failure", t);
                Toast.makeText(ActivityWriteReview.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
