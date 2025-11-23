//작성 후 서버 응답
///api/cafe/{id}/review

package com.cookandroid.gocafestudy.models.POST;

public class ReviewCreateRequest {
    private int rating;
    private String content;

    public ReviewCreateRequest(int rating, String content) {
        this.rating = rating;
        this.content = content;
    }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
