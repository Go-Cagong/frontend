package com.cookandroid.gocafestudy.fragments;

import android.Manifest;
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
// [Retrofit Imports]
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
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
            if (appliedFilters.containsKey(" 분위기 ")) {
                String mood = appliedFilters.get(" 분위기 ");
                if (!cafe.getMood().equals(mood)) visible = false;
            }

            // 아메리카노 가격 필터
            if (appliedFilters.containsKey(" 아메리카노 가격 ")) {
                String priceFilter = appliedFilters.get(" 아메리카노 가격 ");
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
                    displayCafeDetailSheet(response.body());
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

    /**
     * BottomSheet UI를 구성하고 표시하는 헬퍼 함수
     */
    private void displayCafeDetailSheet(CafeDetail cafe) {
        if (cafe == null) return;

        // ⚠️ 임시 MockRepository 인스턴스 (북마크/리뷰는 아직 API 연동이 안 되었으므로 임시로 사용)
        MockRepository mockRepository = new MockRepository();

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_cafe_detail, null);

        // --- 기본 정보 ---
        TextView tvName = v.findViewById(R.id.tv_cafe_name);
        TextView tvAddress = v.findViewById(R.id.tv_address);
        TextView tvHours = v.findViewById(R.id.tv_hours);
        TextView tvTel = v.findViewById(R.id.tel);
        TextView cafeAtmosphere = v.findViewById(R.id.cafe_atmosphere);
        TextView cafePrice = v.findViewById(R.id.cafe_price);
        TextView cafeParking = v.findViewById(R.id.cafe_parking);

        TextView tvAiSummary = v.findViewById(R.id.tv_ai_summary); // AI 요약

        ImageView ivMain = v.findViewById(R.id.iv_cafe_image);
        ImageView ivSub1 = v.findViewById(R.id.iv_cafe_sub1);
        ImageView ivSub2 = v.findViewById(R.id.iv_cafe_sub2);
        ImageView ivSub3 = v.findViewById(R.id.iv_cafe_sub3);
        ImageView ivSub4 = v.findViewById(R.id.iv_cafe_sub4);

        TextView tvRating = v.findViewById(R.id.tv_rating);
        Button btnReview = v.findViewById(R.id.btn_view_all_saved);

        RecyclerView rvPreviewReviews = v.findViewById(R.id.rv_preview_reviews);
        rvPreviewReviews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // --- 기본 정보 연결 ---
        tvName.setText(cafe.getName());
        tvAddress.setText(cafe.getAddress());
        tvHours.setText(cafe.getBusinessHours());

        // API 필드 'tel'이 DTO의 'phone' 필드에 매핑되어 사용됨
        tvTel.setText(cafe.getPhone());

        cafeAtmosphere.setText(cafe.getMood());
        cafePrice.setText(cafe.getAmericanoPrice() + "원");
        cafeParking.setText(cafe.isHasParking() ? "주차 가능" : "주차 불가");

        // --- AI 요약 카드 연결 ---
        // API 필드 'aiSummary'가 DTO에 추가되어 사용됨
        tvAiSummary.setText(cafe.getAiSummary());

        // --- 평점 ---
        tvRating.setText(String.format("%.1f / 5.0", cafe.getReviewAverage()));

        // --- 이미지 연결 ---
        List<String> images = cafe.getImages();
        if (images != null) {
            if (images.size() > 0) Glide.with(requireContext()).load(images.get(0)).placeholder(R.drawable.ic_cafe1_img).into(ivMain);
            if (images.size() > 1) Glide.with(requireContext()).load(images.get(1)).placeholder(R.drawable.ic_cafe1_img).into(ivSub1);
            if (images.size() > 2) Glide.with(requireContext()).load(images.get(2)).placeholder(R.drawable.ic_cafe1_img).into(ivSub2);
            if (images.size() > 3) Glide.with(requireContext()).load(images.get(3)).placeholder(R.drawable.ic_cafe1_img).into(ivSub3);
            if (images.size() > 4) Glide.with(requireContext()).load(images.get(4)).placeholder(R.drawable.ic_cafe1_img).into(ivSub4);
        }

        // --- 리뷰 연결 (최근 3개) ---
        // ⚠️ API 응답에 recentReviews가 없으므로 Mock 데이터를 사용하거나 null 체크 필요
        List<Review> recentReviews = mockRepository.getCafeDetail(cafe.getCafeId()).getRecentReviews();
        List<Review> previewReviews = new ArrayList<>();

        if (recentReviews != null) {
            for (int i = 0; i < Math.min(3, recentReviews.size()); i++) {
                previewReviews.add(recentReviews.get(i));
            }
        }

        ReviewAdapter adapter = new ReviewAdapter(previewReviews);
        rvPreviewReviews.setAdapter(adapter);


        // --- 리뷰 전체보기 버튼 ---
        btnReview.setOnClickListener(click -> {
            Intent intent = new Intent(requireContext(), ActivityReviewList.class);
            intent.putExtra("cafeId", cafe.getCafeId());
            startActivity(intent);
            dialog.dismiss();
        });

        // POST 카페 저장 요청 (MockRepository 호출 유지)
        // ⚠️ API 응답에 isSaved가 없으므로 Mock 데이터를 사용하거나 false로 초기화 필요
        boolean isSaved = mockRepository.getCafeDetail(cafe.getCafeId()).isSaved();

        Button btnSave = v.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(click -> {
            BookmarkCreateResponse response = mockRepository.createBookmark(cafe.getCafeId());
            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show();
        });

        dialog.setContentView(v);
        dialog.show();
    }
}