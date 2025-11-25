//내 리뷰 목록
///api/user/reviews

package com.cookandroid.gocafestudy.models.GET;

import java.util.List;

// 개별 리뷰 항목
public class MyReviewItem {
    private int reviewId;       // review_id
    private int cafeId;         // cafe_id
    private String cafeName;    // cafe_name
    private String cafeImageUrl; // cafe_image_url
    private int rating;         // 별점
    private String content;     // 리뷰 내용

    private List<String> images;  // 새로 추가

    public MyReviewItem(int reviewId, int cafeId, String cafeName, String cafeImageUrl, int rating, String content,List<String> images) {
        this.reviewId = reviewId;
        this.cafeId = cafeId;
        this.cafeName = cafeName;
        this.rating = rating;
        this.content = content;
        this.images = images;
        this.cafeImageUrl = cafeImageUrl;
    }

    // Getter & Setter
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getCafeId() { return cafeId; }
    public void setCafeId(int cafeId) { this.cafeId = cafeId; }

    public String getCafeName() { return cafeName; }
    public void setCafeName(String cafeName) { this.cafeName = cafeName; }

    public String getCafeImageUrl() { return cafeImageUrl; }
    public void setCafeImageUrl(String cafeImageUrl) { this.cafeImageUrl = cafeImageUrl; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
