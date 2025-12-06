package com.cookandroid.gocafestudy.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.android.material.button.MaterialButton;

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
    private static final int PERMISSION_CAMERA_REQUEST = 2000;
    private static final int MAX_REVIEW_IMAGES = 5;

    private EditText etReview;
    private MaterialButton btnSubmit, btnReviewCamera, btnReviewGallery;
    private TextView tvCharCount, tvCafeName;
    private RatingBar ratingBar;
    private LinearLayout layoutReviewImages;
    private List<Bitmap> reviewBitmaps = new ArrayList<>();

    private int cafeId;
    private String cafeName;

    private ActivityResultLauncher<Void> reviewCameraLauncher;
    private ActivityResultLauncher<String> reviewGalleryLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // Intent에서 cafeId, cafeName 받기
        cafeId = getIntent().getIntExtra("cafeId", -1);
        cafeName = getIntent().getStringExtra("cafeName");

        if (cafeId == -1) {
            Toast.makeText(this, "카페 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // View 초기화
        etReview = findViewById(R.id.etReview);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvCharCount = findViewById(R.id.tvCharCount);
        ratingBar = findViewById(R.id.ratingBar);
        layoutReviewImages = findViewById(R.id.layoutReviewImages);
        btnReviewCamera = findViewById(R.id.btnReviewCamera);
        btnReviewGallery = findViewById(R.id.btnReviewGallery);
        tvCafeName = findViewById(R.id.tvCafeName);

        tvCafeName.setText(cafeName != null ? cafeName : "");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 글자 수 감지
        etReview.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(s.length() + "자");
                checkSubmitEnable();
            }
        });

        // 별점 감지
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> checkSubmitEnable());

        // 리뷰 사진용 카메라/갤러리
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

        btnSubmit.setOnClickListener(v -> submitReview());

        checkSubmitEnable();
    }

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
                reviewCameraLauncher.launch(null);
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addReviewImage(Bitmap bitmap) {
        if (bitmap == null) return;

        if (reviewBitmaps.size() >= MAX_REVIEW_IMAGES) {
            Toast.makeText(this, "최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewBitmaps.add(bitmap);

        ImageView imageView = new ImageView(this);
        int sizePx = (int) (120 * getResources().getDisplayMetrics().density);
        int marginPx = (int) (12 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
        params.setMargins(0, 0, marginPx, 0);

        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);
        imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_glass_card));

        imageView.setOnClickListener(v -> {
            layoutReviewImages.removeView(imageView);
            reviewBitmaps.remove(bitmap);
            Toast.makeText(this, "사진 삭제됨", Toast.LENGTH_SHORT).show();
        });

        layoutReviewImages.addView(imageView);

        final HorizontalScrollView scrollView = findViewById(R.id.scrollReviewImages);
        scrollView.post(() -> scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT));
    }

    private void checkSubmitEnable() {
        boolean enable = etReview.getText().length() >= 10
                && ratingBar.getRating() > 0;
        btnSubmit.setEnabled(enable);
    }

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

    private void submitReview() {
        String reviewText = etReview.getText().toString();
        int rating = (int) ratingBar.getRating();

        UserResponse currentUser = UserSessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ActivityWriteReview.this, "리뷰 등록 성공!", Toast.LENGTH_LONG).show();

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
