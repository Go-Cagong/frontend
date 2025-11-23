
// 사용자가 저장한 카페 정보
///api/user/bookmarks

package com.cookandroid.gocafestudy.models.GET;

import java.util.Date;

public class Bookmark {
    private int bookmarkId;
    private int userId;
    private int cafeId;
    private String cafeName;
    private String address;
    private double avgRating;
    private String mainImageUrl;


    private Date savedAt;

    public Bookmark(int bookmarkId, int userId, int cafeId, String cafeName, String address,
                    double avgRating, String mainImageUrl, Date savedAt) {
        this.bookmarkId = bookmarkId;
        this.userId = userId;
        this.cafeId = cafeId;
        this.cafeName = cafeName;
        this.address = address;
        this.avgRating = avgRating;
        this.mainImageUrl = mainImageUrl;
        this.savedAt = savedAt;
    }


    public int getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(int bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCafeId() {
        return cafeId;
    }

    public void setCafeId(int cafeId) {
        this.cafeId = cafeId;
    }

    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public Date getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(Date savedAt) {
        this.savedAt = savedAt;
    }
    // Getter, Setter 생략
}

