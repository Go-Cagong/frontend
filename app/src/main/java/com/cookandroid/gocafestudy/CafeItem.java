package com.cookandroid.gocafestudy;

public class CafeItem {
    private String name;
    private String location;
    private String imageUrl; // 서버 연동 시 URL 사용

    public CafeItem(String name, String location, String imageUrl) {
        this.name = name;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getImageUrl() { return imageUrl; }
}
