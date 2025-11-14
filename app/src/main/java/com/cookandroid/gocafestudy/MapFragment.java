package com.cookandroid.gocafestudy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;

// ★ 추가 import
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.util.FusedLocationSource;

public class MapFragment extends Fragment {

    // ★ 현재 위치 추적에 필요한 필드
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMapRef;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        Fragment existing = fm.findFragmentById(R.id.naver_map_fragment);

        if (existing == null) {
            com.naver.maps.map.MapFragment naverMapFragment =
                    com.naver.maps.map.MapFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.map_container, naverMapFragment, "naver_map_fragment_tag")
                    .commitNow();
            naverMapFragment.getMapAsync(this::onMapReady);
        } else {
            ((com.naver.maps.map.MapFragment) existing).getMapAsync(this::onMapReady);
        }

        View mapContainer = view.findViewById(R.id.map_container);
        if (mapContainer != null) {
            mapContainer.setOnClickListener(v -> {
                // showCafeDetailBottomSheet();
            });
        }
    }










    private void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMapRef = naverMap;

        naverMapRef.setLocationSource(locationSource);
        naverMapRef.getUiSettings().setLocationButtonEnabled(true);

        LatLng center = new LatLng(37.3982989, 126.6337295);
        naverMapRef.moveCamera(CameraUpdate.scrollTo(center));

        Marker marker = new Marker();
        marker.setPosition(center);
        marker.setMap(naverMapRef);

        marker.setOnClickListener(overlay -> {
            showCafeDetailBottomSheet();
            return true;
        });

        ensureLocationTracking();
    }

    private void ensureLocationTracking() {
        boolean fineGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarseGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (fineGranted || coarseGranted) {
            if (naverMapRef != null) {
                naverMapRef.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        } else {
            requestPermissions(
                    new String[]{
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
            return;
        }
    }

    private void showCafeDetailBottomSheet() {
        if (getContext() == null) return;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(
                R.layout.bottom_sheet_cafe_detail,
                null
        );

        // 리뷰보기 버튼 클릭 시 ActivityReviewList로 이동
        Button btnViewAllSaved = bottomSheetView.findViewById(R.id.btn_view_all_saved);
        btnViewAllSaved.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ActivityReviewList.class);
            startActivity(intent);
            bottomSheetDialog.dismiss(); // BottomSheet 닫기
        });

        // TODO: bottomSheetView.findViewById(...)로 카페 데이터 바인딩
        ImageView cafeImage = bottomSheetView.findViewById(R.id.iv_cafe_image);
        cafeImage.setImageResource(R.drawable.ic_cafe1_img);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}
