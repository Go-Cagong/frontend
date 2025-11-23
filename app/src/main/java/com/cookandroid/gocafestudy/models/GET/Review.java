//리뷰 전체 조회
///api/cafe/{id}/review

package com.cookandroid.gocafestudy.models.GET;

import java.util.Date;

public class Review {
    private int reviewId;
    private int userId;
    private int cafeId;
    private int rating;
    private String content;
    private String createdAt;

    public Review(int reviewId, int userId, int cafeId, int rating, String content, String createdAt) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.cafeId = cafeId;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
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
}
