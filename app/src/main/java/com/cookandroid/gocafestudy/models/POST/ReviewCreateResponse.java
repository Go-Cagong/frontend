//카페 저장 후 서버 응답
// /api/cafes/{cafe_id}/bookmark

package com.cookandroid.gocafestudy.models.POST;

import java.util.Date;

public class ReviewCreateResponse {
    private String message;      // 성공 메시지
    private Review review;       // 생성된 리뷰 객체

    public ReviewCreateResponse(String message, Review review) {
        this.message = message;
        this.review = review;
    }

    // Getter & Setter
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }

    // Review 클래스 참고
    public static class Review {
        private int reviewId;
        private int cafeId;
        private int userId;
        private int rating;
        private String content;
        private Date createdAt;

        public Review(int reviewId, int cafeId, int userId, int rating, String content, Date createdAt) {
            this.reviewId = reviewId;
            this.cafeId = cafeId;
            this.userId = userId;
            this.rating = rating;
            this.content = content;
            this.createdAt = createdAt;
        }

        // Getter & Setter
        public int getReviewId() { return reviewId; }
        public void setReviewId(int reviewId) { this.reviewId = reviewId; }

        public int getCafeId() { return cafeId; }
        public void setCafeId(int cafeId) { this.cafeId = cafeId; }

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    }
}

