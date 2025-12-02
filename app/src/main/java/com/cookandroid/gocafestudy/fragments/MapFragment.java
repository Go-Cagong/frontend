package com.cookandroid.gocafestudy.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.gocafestudy.models.GET.BookmarkIsSavedResponse;
import com.cookandroid.gocafestudy.models.GET.CafeReviewResponse;
import com.naver.maps.map.OnMapReadyCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.activities.ActivityReviewList;

import com.cookandroid.gocafestudy.adapters.ReviewAdapter;

import com.cookandroid.gocafestudy.api.CafeApi;
import com.cookandroid.gocafestudy.repository.RetrofitClient;
import com.cookandroid.gocafestudy.models.GET.CafeMapResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.cookandroid.gocafestudy.models.DELETE.BookmarkDeleteResponse;
import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.CafeMapItem;
import com.cookandroid.gocafestudy.models.GET.Review;
import com.cookandroid.gocafestudy.models.POST.BookmarkCreateResponse;
import com.cookandroid.gocafestudy.repository.MockRepository; // MockRepository는 임시로 남겨두고 필드는 제거
import com.cookandroid.gocafestudy.views.FilterView;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMapRef;

    // MockRepository를 CafeApi로 대체
    private CafeApi cafeApi;

    private List<Marker> markerList = new ArrayList<>();
    private List<CafeMapItem> cafeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // MockRepository 초기화 대신 CafeApi 초기화
        cafeApi = RetrofitClient.getCafeApi();
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // 필터뷰 연결
        FilterView filterView = view.findViewById(R.id.filterView);

        // -------------------------
        // 마커 데이터 초기화 (Mock 호출 제거)
        // cafeList = repository.getCafeMap(); // Mock 호출 제거
        // -------------------------

        // -------------------------
        // 필터 콜백 설정
        // -------------------------
        filterView.setOnFilterChangeListener(appliedFilters -> updateMarkers(appliedFilters));

        // NaverMap 초기화
        // NaverMap 초기화
        Fragment existing = fm.findFragmentById(R.id.naver_map_fragment);
        if (existing == null) {
            com.naver.maps.map.MapFragment naverMapFragment = com.naver.maps.map.MapFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.map_container, naverMapFragment, "naver_map_fragment_tag")
                    .commitNow();
            naverMapFragment.getMapAsync(this); // 👈 MapFragment 자체가 콜백 인터페이스(this)
        } else {
            ((com.naver.maps.map.MapFragment) existing).getMapAsync(this); // 👈 MapFragment 자체가 콜백 인터페이스(this)
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMapRef = naverMap;

        naverMapRef.setLocationSource(locationSource);
        naverMapRef.getUiSettings().setLocationButtonEnabled(true);

        LatLng center = new LatLng(37.3982989, 126.6337295); // 초기 중심 좌표
        naverMapRef.moveCamera(CameraUpdate.scrollTo(center));

        // -------------------------
        // API 호출을 통해 마커 데이터 로드
        // -------------------------
        loadCafeMapItems(center.latitude, center.longitude);

        naverMapRef.addOnCameraChangeListener((reason, animated) -> {
            float zoom = (float) naverMapRef.getCameraPosition().zoom;
            int size = (int) (40 + (zoom - 10) * 10);
            size = Math.max(20, Math.min(size, 120));
            for (Marker marker : markerList) {
                marker.setWidth(size);
                marker.setHeight(size);
            }
            // TODO: 카메라 이동이 멈췄을 때, 화면 영역에 해당하는 API를 다시 호출하는 로직 추가 (디바운싱 권장)
        });

        ensureLocationTracking();
    }

    /**
     * API를 호출하여 카페 맵 아이템을 가져오고 지도에 마커를 표시합니다.
     */
    private void loadCafeMapItems(double lat, double lon) {
        cafeApi.getCafeMapItems(lat, lon).enqueue(new Callback<CafeMapResponse>() {
            @Override
            public void onResponse(Call<CafeMapResponse> call, Response<CafeMapResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CafeMapItem> newCafes = response.body().getCafes();

                    clearMapMarkers();
                    cafeList.clear();
                    cafeList.addAll(newCafes);

                    addMapMarkers(newCafes);

                } else {
                    Toast.makeText(requireContext(), "카페 목록 로드 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API Response Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CafeMapResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_LONG).show();
                Log.e(TAG, "API Call Failure", t);
            }
        });
    }

    /**
     * 기존 마커를 지도에서 제거합니다.
     */
    private void clearMapMarkers() {
        for (Marker marker : markerList) {
            marker.setMap(null);
        }
        markerList.clear();
    }

    /**
     * 새로운 카페 목록을 지도에 마커로 표시합니다.
     */
    private void addMapMarkers(List<CafeMapItem> cafes) {
        if (naverMapRef == null) return;

        for (CafeMapItem cafe : cafes) {
            Marker marker = new Marker();
            marker.setPosition(new LatLng(cafe.getLatitude(), cafe.getLongitude()));

            // 커스텀 마커 아이콘
            marker.setIcon(OverlayImage.fromResource(R.drawable.ic_cafe_marker));

            //  카페 이름 표시
            marker.setCaptionText(cafe.getName());
            marker.setCaptionTextSize(14);        // 글자 크기
            marker.setCaptionColor(0xFF000000);   // 글자 색 (검정)
            marker.setCaptionHaloColor(0xFFFFFFFF); // 글자 테두리(가독성 UP)

            marker.setWidth(90);
            marker.setHeight(90);

            marker.setMap(naverMapRef);
            markerList.add(marker);

            marker.setOnClickListener(overlay -> {
                showCafeDetailBottomSheet(cafe.getId());
                return true;
            });
        }
    }




    private void updateMarkers(Map<String, String> appliedFilters) {
        for (int i = 0; i < cafeList.size(); i++) {
            CafeMapItem cafe = cafeList.get(i);
            Marker marker = markerList.get(i);

            boolean visible = true;

            // 분위기 필터
            if (appliedFilters.containsKey("분위기")) {
                String mood = appliedFilters.get("분위기");
                if (!cafe.getMood().equals(mood)) visible = false;
            }

            // 아메리카노 가격 필터
            if (appliedFilters.containsKey("아메리카노 가격")) {
                String priceFilter = appliedFilters.get("아메리카노 가격");
                int price = cafe.getAmericanoPrice();
                if (priceFilter.equals("3000원 이하") && price > 3000) visible = false;
                else if (priceFilter.equals("3000~5000원") && (price < 3000 || price > 5000)) visible = false;
                else if (priceFilter.equals("5000원 이상") && price < 5000) visible = false;
            }

            // 주차 필터 (isParkingAvailable()은 @SerializedName("hasParking")을 사용해 매핑됨)
            if (appliedFilters.containsKey("주차")) {
                String parking = appliedFilters.get("주차");
                if (parking.equals("가능") && !cafe.isParkingAvailable()) visible = false;
                if (parking.equals("불가능") && cafe.isParkingAvailable()) visible = false;
            }

            marker.setMap(visible ? naverMapRef : null);
        }
    }

    private void ensureLocationTracking() {
        boolean fineGranted = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarseGranted = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (fineGranted || coarseGranted) {
            if (naverMapRef != null) {
                naverMapRef.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        } else {
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (locationSource != null &&
                locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (locationSource.isActivated()) {
                if (naverMapRef != null) {
                    naverMapRef.setLocationTrackingMode(LocationTrackingMode.Follow);
                }
            } else {
                if (naverMapRef != null) {
                    naverMapRef.setLocationTrackingMode(LocationTrackingMode.None);
                }
            }
        }
    }

    // -------------------------
    // 카페 상세 BottomSheet (TODO: 다음 단계에서 API 연동 필요)
    // -------------------------
    private void showCafeDetailBottomSheet(int cafeId) {
        // ❌ MockRepository 호출 제거
        // CafeDetail cafe = mockRepository.getCafeDetail(cafeId);

        cafeApi.getCafeDetail(cafeId).enqueue(new Callback<CafeDetail>() {
            @Override
            public void onResponse(Call<CafeDetail> call, Response<CafeDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API 호출 성공 시 BottomSheet 표시 함수 호출
                    displayCafeDetailSheet(cafeId, response.body());
                } else {
                    Toast.makeText(requireContext(), "카페 상세 정보 로드 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Detail API Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CafeDetail> call, Throwable t) {
                Toast.makeText(requireContext(), "카페 상세 정보 네트워크 오류", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Detail API Call Failure", t);
            }
        });
    }

    private void loadBookmarkState(Context context, int cafeId, Button btnSave) {

        RetrofitClient.getBookmarkApi(context)
                .getBookmarkState(cafeId)
                .enqueue(new Callback<BookmarkIsSavedResponse>() {

                    @Override
                    public void onResponse(Call<BookmarkIsSavedResponse> call,
                                           Response<BookmarkIsSavedResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            boolean saved = response.body().isSaved();

                            // 👉 텍스트만 변경
                            if (saved) {
                                btnSave.setText("저장 취소하기");
                            } else {
                                btnSave.setText("저장하기");
                            }

                            Log.d("Bookmark", "isSaved = " + saved);
                        } else {
                            Log.e("Bookmark", "조회 실패: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkIsSavedResponse> call, Throwable t) {
                        Log.e("Bookmark", "네트워크 오류", t);
                    }
                });


    }
    private void createBookmark(Context context, int cafeId, Button btnSave) {

        RetrofitClient.getBookmarkApi(context)
                .createBookmark(cafeId)
                .enqueue(new Callback<BookmarkCreateResponse>() {

                    @Override
                    public void onResponse(Call<BookmarkCreateResponse> call,
                                           Response<BookmarkCreateResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            btnSave.setText("저장 취소하기");
                            Log.d("BookmarkPOST", "저장 완료: " + response.body().getMessage());
                        } else {
                            Log.e("BookmarkPOST", "저장 실패: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkCreateResponse> call, Throwable t) {
                        Log.e("BookmarkPOST", "네트워크 오류", t);
                    }
                });
    }

    private void deleteBookmark(Context context, int cafeId, Button btnSave) {

        RetrofitClient.getBookmarkApi(context)
                .deleteBookmark(cafeId)
                .enqueue(new Callback<BookmarkDeleteResponse>() {

                    @Override
                    public void onResponse(Call<BookmarkDeleteResponse> call,
                                           Response<BookmarkDeleteResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            btnSave.setText("저장하기");
                            Log.d("BookmarkDELETE", "저장 해제됨: " + response.body().getMessage());
                        } else {
                            Log.e("BookmarkDELETE", "해제 실패: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkDeleteResponse> call, Throwable t) {
                        Log.e("BookmarkDELETE", "네트워크 오류", t);
                    }
                });
    }




    /**
     * BottomSheet UI를 구성하고 표시하는 헬퍼 함수
     */
    private void displayCafeDetailSheet(int cafeId, CafeDetail cafe) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_cafe_detail, null);

        TextView tvName      = v.findViewById(R.id.tv_cafe_name);
        TextView tvAddress   = v.findViewById(R.id.tv_address);
        TextView tvHours     = v.findViewById(R.id.tv_hours);
        TextView tvTel       = v.findViewById(R.id.tel);
        TextView tvMood      = v.findViewById(R.id.cafe_atmosphere);
        TextView tvPrice     = v.findViewById(R.id.cafe_price);
        TextView tvParking   = v.findViewById(R.id.cafe_parking);
        TextView tvAiSummary = v.findViewById(R.id.tv_ai_summary);

        // 평점 + (리뷰 개수 표시하고 싶으면 추가)
        TextView tvRating    = v.findViewById(R.id.tv_rating);
        //TextView tvReviewCount = v.findViewById(R.id.tv_review_count); // 레이아웃에 있으면
        Button   btnReview   = v.findViewById(R.id.btn_view_all_saved);

        ImageView ivMain  = v.findViewById(R.id.iv_cafe_image);
        ImageView ivSub1  = v.findViewById(R.id.iv_cafe_sub1);
        ImageView ivSub2  = v.findViewById(R.id.iv_cafe_sub2);
        ImageView ivSub3  = v.findViewById(R.id.iv_cafe_sub3);
        ImageView ivSub4  = v.findViewById(R.id.iv_cafe_sub4);

        // ✅ 리뷰 미리보기 RecyclerView
        RecyclerView rvPreviewReviews = v.findViewById(R.id.rv_preview_reviews);
        rvPreviewReviews.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Review> previewReviews = new ArrayList<>();
        ReviewAdapter previewAdapter = new ReviewAdapter(previewReviews);
        rvPreviewReviews.setAdapter(previewAdapter);

        // ------------------------------
        // 1) 카페 기본 정보 채우기
        // ------------------------------
        tvName.setText(cafe.getName());
        tvAddress.setText(cafe.getAddress());
        tvHours.setText(cafe.getBusinessHours());
        tvTel.setText(cafe.getPhone());
        tvMood.setText(cafe.getMood());
        tvPrice.setText(cafe.getAmericanoPrice() + "원");
        tvParking.setText(cafe.isHasParking() ? "주차 가능" : "주차 불가");
        tvAiSummary.setText(cafe.getDescription());

        // 여기서는 일단 "로딩중" 문구만
        if (tvRating != null) {
            tvRating.setText("평점 로딩중...");
        }
        //if (tvReviewCount != null) {
            //tvReviewCount.setText(""); // 혹은 "0개" 등
       //}

        List<String> images = cafe.getImages();
        if (images != null) {
            if (images.size() > 0)
                Glide.with(requireContext()).load(images.get(0)).placeholder(R.drawable.ic_camera_gray).into(ivMain);
            if (images.size() > 1)
                Glide.with(requireContext()).load(images.get(1)).placeholder(R.drawable.ic_camera_gray).into(ivSub1);
            if (images.size() > 2)
                Glide.with(requireContext()).load(images.get(2)).placeholder(R.drawable.ic_camera_gray).into(ivSub2);
            if (images.size() > 3)
                Glide.with(requireContext()).load(images.get(3)).placeholder(R.drawable.ic_camera_gray).into(ivSub3);
            if (images.size() > 4)
                Glide.with(requireContext()).load(images.get(4)).placeholder(R.drawable.ic_camera_gray).into(ivSub4);
        }

        // ------------------------------
        // 2) 🔥 서버에서 리뷰 + 평점 요약 가져오기
        // ------------------------------
        cafeApi.getReviewsByCafeId(cafeId).enqueue(new Callback<CafeReviewResponse>() {
            @Override
            public void onResponse(Call<CafeReviewResponse> call, Response<CafeReviewResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "preview review error code = " + response.code());
                    return;
                }

                CafeReviewResponse body = response.body();
                if (body == null || body.getReviews() == null) {
                    Log.e(TAG, "preview review body or reviews is null");
                    return;
                }

                // ★ 여기서 평균 평점 / 리뷰 개수 세팅
                double avg = body.getAverageRating();   // DTO 필드명에 맞게
                int count  = body.getReviewCount();

                if (tvRating != null) {
                    tvRating.setText(String.format("%.1f / 5.0", avg));
                }
                //if (tvReviewCount != null) {
                    //tvReviewCount.setText("(" + count + "개의 리뷰)");
                //}

                List<Review> allReviews = body.getReviews();

                // createdAt "yyyy-MM-dd" 까지만 보이게 슬라이스
                for (Review r : allReviews) {
                    String createdAt = r.getCreatedAt();
                    if (createdAt != null && createdAt.length() >= 10) {
                        r.setCreatedAt(createdAt.substring(0, 10));
                    }
                }

                // 최대 3개까지만 BottomSheet에 미리보기로 보여주기
                previewReviews.clear();
                for (int i = 0; i < Math.min(3, allReviews.size()); i++) {
                    previewReviews.add(allReviews.get(i));
                }
                previewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CafeReviewResponse> call, Throwable t) {
                Log.e(TAG, "preview review onFailure", t);
            }
        });

        // ------------------------------
        // 3) '리뷰 전체보기' 버튼
        // ------------------------------
        btnReview.setOnClickListener(click -> {
            Intent intent = new Intent(requireContext(), ActivityReviewList.class);
            intent.putExtra("cafeId", cafeId);
            startActivity(intent);
            dialog.dismiss();
        });

        Button btnSave = v.findViewById(R.id.btn_save);
        loadBookmarkState(getContext(), cafeId, btnSave);

        btnSave.setOnClickListener(view -> {
            String currentText = btnSave.getText().toString();

            if (currentText.equals("저장하기")) {
                createBookmark(getContext(), cafeId, btnSave);
            } else {
                deleteBookmark(getContext(), cafeId, btnSave);
            }
        });

        dialog.setContentView(v);
        dialog.show();
    }

}
