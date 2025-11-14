package com.cookandroid.gocafestudy;

import java.util.Arrays;
import java.util.List;

public class Cafe {
    private int id;
    private String name;
    private String address;
    private float rating;
    private boolean wifi;
    private boolean outlets;
    private String hours;
    private String imageUrl;
    private double lat;
    private double lng;
    private String aiSummary;
    private int seats;
    private String noise;

    public Cafe(int id, String name, String address, float rating, boolean wifi,
                boolean outlets, String hours, String imageUrl, double lat, double lng,
                String aiSummary, int seats, String noise) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.wifi = wifi;
        this.outlets = outlets;
        this.hours = hours;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
        this.aiSummary = aiSummary;
        this.seats = seats;
        this.noise = noise;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public float getRating() {
        return rating;
    }

    public boolean hasWifi() {
        return wifi;
    }

    public boolean hasOutlets() {
        return outlets;
    }

    public String getHours() {
        return hours;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public int getSeats() {
        return seats;
    }

    public String getNoise() {
        return noise;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public void setOutlets(boolean outlets) {
        this.outlets = outlets;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public void setNoise(String noise) {
        this.noise = noise;
    }
}

class MockData {
    public static List<Cafe> getCafes() {
        return Arrays.asList(
                new Cafe(
                        1,
                        "센트럴파크 카페",
                        "인천 연수구 센트럴로 263",
                        4.8f,
                        true,
                        true,
                        "08:00 - 22:00",
                        "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=800",
                        37.395,
                        126.64,
                        "넓은 공간과 편안한 좌석이 인상적이에요. 콘센트가 모든 테이블에 있어서 장시간 작업하기 좋고, 센트럴파크 전망이 아름다워요. 주말에도 비교적 조용한 편입니다.",
                        80,
                        "조용함"
                ),
                new Cafe(
                        2,
                        "송도 스터디룸",
                        "인천 연수구 송도동 123",
                        4.9f,
                        true,
                        true,
                        "07:00 - 23:00",
                        "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800",
                        37.38,
                        126.65,
                        "카공 전용 공간이라 분위기가 최고예요. 개인 스탠드와 칸막이가 있어 집중하기 좋고, 음료 무제한 리필이 가능합니다. 사람들이 조용히 공부하는 분위기라 방해받지 않아요.",
                        50,
                        "매우 조용함"
                ),
                new Cafe(
                        3,
                        "트리플 스트리트 북카페",
                        "인천 연수구 송도동 30-1",
                        4.6f,
                        true,
                        true,
                        "09:00 - 21:00",
                        "https://images.unsplash.com/photo-1445116572660-236099ec97a0?w=800",
                        37.39,
                        126.635,
                        "책도 읽고 작업도 할 수 있는 복합 공간이에요. 인테리어가 세련되고 자연광이 잘 들어와요. 커피 맛도 훌륭하고, 주변 쇼핑몰과 연결되어 있어 편의시설 이용이 편리합니다.",
                        60,
                        "보통"
                ),
                new Cafe(
                        4,
                        "컨벤시아 카페거리",
                        "인천 연수구 센트럴로 123",
                        4.7f,
                        true,
                        true,
                        "08:30 - 22:30",
                        "https://images.unsplash.com/photo-1559496417-e7f25c8b1b8e?w=800",
                        37.385,
                        126.645,
                        "현대적인 디자인과 넓은 좌석 간격이 특징이에요. 와이파이 속도가 빠르고 콘센트가 충분해서 노트북 작업하기 완벽합니다. 주차도 편리하고 24시간 운영하는 편의점이 근처에 있어요.",
                        70,
                        "조용함"
                )
        );
    }
}
