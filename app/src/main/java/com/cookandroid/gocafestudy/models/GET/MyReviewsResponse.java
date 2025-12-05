package com.cookandroid.gocafestudy.models.GET;

import java.util.List;

public class MyReviewsResponse {
    private int userId;
    private String userName;
    private int reviewCount;
    private List<MyReviewItem> reviews;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public List<MyReviewItem> getReviews() { return reviews; }
    public void setReviews(List<MyReviewItem> reviews) { this.reviews = reviews; }
}
