package com.cookandroid.gocafestudy.models.GET;

import com.google.gson.annotations.SerializedName;

public class Bookmark {

    @SerializedName("bookmark_id")
    private int bookmarkId;

    @SerializedName("cafe_id")
    private int cafeId;

    @SerializedName("cafe_name")
    private String cafeName;

    @SerializedName("address")
    private String address;

    @SerializedName("avg_rating")
    private double avgRating;

    @SerializedName("main_image_url")
    private String mainImageUrl;

    @SerializedName("saved_at")
    private String savedAt;  // ISO8601 문자열 그대로 저장 (Date로 파싱 필요 X)

    // Getter만 있어도 됨
    public int getBookmarkId() {
        return bookmarkId;
    }

    public int getCafeId() {
        return cafeId;
    }

    public String getCafeName() {
        return cafeName;
    }

    public String getAddress() {
        return address;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public String getSavedAt() {
        return savedAt;
    }
}
