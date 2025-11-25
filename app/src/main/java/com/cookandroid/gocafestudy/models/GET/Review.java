//리뷰 전체 조회
///api/cafe/{id}/review

package com.cookandroid.gocafestudy.models.GET;

import java.util.Date;
import java.util.List;

public class Review {
    private int reviewId;
    private int userId;
    private String userName; // 새로 추가
    private int cafeId;
    private int rating;
    private String content;
    private String createdAt;

    private List<String> images;  // 새로 추가



    public Review(int reviewId, int userId, String userName, int cafeId, int rating, String content, String createdAt, List<String> images) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.userName = userName;
        this.cafeId = cafeId;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.images = images;

    }

    // getter, setter 생략

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

}
