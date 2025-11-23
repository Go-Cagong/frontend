
//지도용 카페 위치 정보
///api/cafe/map

package com.cookandroid.gocafestudy.models.GET;

import java.util.Date;
import java.util.List;

// 지도용 카페
public class CafeMapItem {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String mood;
    private int americanoPrice;
    private boolean parkingAvailable;

    public CafeMapItem(int id, String name, double latitude, double longitude, String mood, int americanoPrice, boolean parkingAvailable) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mood = mood;
        this.americanoPrice = americanoPrice;
        this.parkingAvailable = parkingAvailable;
    }

    // Getter & Setter
    // ...

    public int getId() {
        return id;
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

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

}

