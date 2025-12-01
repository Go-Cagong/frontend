package com.cookandroid.gocafestudy.models.GET;

import java.util.List;

public class MyReviewItem {
    private int reviewId;
    private int cafeId;
    private String cafeName;
    private int rating;
    private String content;
    private List<String> images;
    private String createdAt;

    // 생성자
    public MyReviewItem(int reviewId, int cafeId, String cafeName, int rating, String content, List<String> images, String createdAt) {
        this.reviewId = reviewId;
        this.cafeId = cafeId;
        this.cafeName = cafeName;
        this.rating = rating;
        this.content = content;
        this.images = images;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getCafeId() { return cafeId; }
    public void setCafeId(int cafeId) { this.cafeId = cafeId; }

    public String getCafeName() { return cafeName; }
    public void setCafeName(String cafeName) { this.cafeName = cafeName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
