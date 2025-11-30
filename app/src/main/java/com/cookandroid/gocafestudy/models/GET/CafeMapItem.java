package com.cookandroid.gocafestudy.models.GET;

import com.google.gson.annotations.SerializedName; // <-- 이 부분을 import해야 합니다.

// 지도용 카페
public class CafeMapItem {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String mood;
    private int americanoPrice;

    // API 응답 필드명 "hasParking"을 자바 필드 parkingAvailable에 매핑
    @SerializedName("hasParking")
    private boolean parkingAvailable;

    // 생성자 (기존 필드명 유지)
    public CafeMapItem(int id, String name, double latitude, double longitude, String mood, int americanoPrice, boolean parkingAvailable) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mood = mood;
        this.americanoPrice = americanoPrice;
        this.parkingAvailable = parkingAvailable;
    }

    // Getter & Setter (기존 필드명 유지)

    public int getId() {
        return id;
    }

    // ... (다른 Getter/Setter 생략)

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public int getAmericanoPrice() {
        return americanoPrice;
    }

    public void setAmericanoPrice(int americanoPrice) {
        this.americanoPrice = americanoPrice;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

}