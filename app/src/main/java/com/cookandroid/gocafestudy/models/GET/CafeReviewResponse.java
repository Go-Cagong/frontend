// CafeReviewResponse.java
package com.cookandroid.gocafestudy.models.GET;

import java.util.List;

public class CafeReviewResponse {
    private int cafeId;
    private int reviewCount;
    private double averageRating;
    private List<Review> reviews;  // ✅ JSON의 리뷰 배열 key

    public int getCafeId() { return cafeId; }
    public int getReviewCount() { return reviewCount; }
    public double getAverageRating() { return averageRating; }
    public List<Review> getReviews() { return reviews; }
}
