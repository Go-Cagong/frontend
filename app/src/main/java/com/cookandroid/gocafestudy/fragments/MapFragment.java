package com.cookandroid.gocafestudy.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.activities.ActivityReviewList;
import com.cookandroid.gocafestudy.adapters.ReviewAdapter;
import com.cookandroid.gocafestudy.models.Cafe;
import com.cookandroid.gocafestudy.models.Review;
import com.cookandroid.gocafestudy.repository.MockRepository;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMapRef;

    private MockRepository repository;
    private List<Marker> markerList = new ArrayList<>(); // 마커 저장

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        repository = new MockRepository();  // 목 레포지토리 초기화
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // NaverMap 프래그먼트 초기화
        Fragment existing = fm.findFragmentById(R.id.naver_map_fragment);
        if (existing == null) {
            com.naver.maps.map.MapFragment naverMapFragment = com.naver.maps.map.MapFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.map_container, naverMapFragment, "naver_map_fragment_tag")
                    .commitNow();
            naverMapFragment.getMapAsync(this::onMapReady);
        } else {
            ((com.naver.maps.map.MapFragment) existing).getMapAsync(this::onMapReady);
        }
    }

    private void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMapRef = naverMap;

        naverMapRef.setLocationSource(locationSource);
        naverMapRef.getUiSettings().setLocationButtonEnabled(true);

        // 기본 위치 설정
        LatLng center = new LatLng(37.3982989, 126.6337295);
        naverMapRef.moveCamera(CameraUpdate.scrollTo(center));

        // -------------------------
        // 목데이터 기반 마커 생성
        // -------------------------
        List<Cafe> cafeList = repository.getCafes();
        for (Cafe cafe : cafeList) {
            Marker marker = new Marker();
            marker.setPosition(new LatLng(cafe.getLatitude(), cafe.getLongitude()));
            marker.setMap(naverMapRef);
            markerList.add(marker);

            marker.setOnClickListener(overlay -> {
                showCafeDetailBottomSheet(cafe.getCafeId());
                return true;
            });
        }

        // -------------------------
        // 줌 레벨에 따라 마커 크기 비례 조정
        // -------------------------
        naverMapRef.addOnCameraChangeListener((reason, animated) -> {
            float zoom = (float) naverMapRef.getCameraPosition().zoom;
            // 예: 기본 10일 때 40, 줌 증가마다 10씩 증가
            int size = (int) (40 + (zoom - 10) * 10);
            if (size < 20) size = 20; // 최소 크기 제한
            if (size > 120) size = 120; // 최대 크기 제한

            for (Marker marker : markerList) {
                marker.setWidth(size);
                marker.setHeight(size);
            }
        });

        ensureLocationTracking();
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

    // ===============================================================
    // ⭐ 카페 상세 BottomSheet + 리뷰보기 버튼 연결
    // ===============================================================
    private void showCafeDetailBottomSheet(int cafeId) {
        Cafe cafe = repository.getCafeById(cafeId);
        if (cafe == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_cafe_detail, null);

        TextView tvName = v.findViewById(R.id.tv_cafe_name);
        TextView tvAddress = v.findViewById(R.id.tv_address);
        ImageView ivImage = v.findViewById(R.id.iv_cafe_image);
        Button btnReview = v.findViewById(R.id.btn_view_all_saved);

        // RecyclerView 초기화
        RecyclerView rvPreviewReviews = v.findViewById(R.id.rv_preview_reviews);
        rvPreviewReviews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 리뷰 데이터 가져오기 (백엔드 연동 시 repository.getReviewsByCafeId() 사용)
        List<Review> allReviews = repository.getReviewsByCafeId(cafeId);

        // 최대 3개만 보여주기
        List<Review> previewReviews = new ArrayList<>();
        for (int i = 0; i < Math.min(3, allReviews.size()); i++) {
            previewReviews.add(allReviews.get(i));
        }

        ReviewAdapter adapter = new ReviewAdapter(previewReviews);
        rvPreviewReviews.setAdapter(adapter);

        // 기본 데이터 적용
        tvName.setText(cafe.getName());
        tvAddress.setText(cafe.getAddress());
        if (!cafe.getPhotos().isEmpty()) {
            ivImage.setImageResource(R.drawable.ic_cafe1_img);
        }

        // 리뷰 전체보기 버튼 클릭 → ActivityReviewList로 이동
        btnReview.setOnClickListener(click -> {
            Intent intent = new Intent(requireContext(), ActivityReviewList.class);
            intent.putExtra("cafeId", cafeId);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.setContentView(v);
        dialog.show();
    }

}
