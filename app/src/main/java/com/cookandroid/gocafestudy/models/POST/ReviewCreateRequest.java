package com.cookandroid.gocafestudy.models.POST;

import java.util.List;

public class ReviewCreateRequest {
    private int rating;
    private String content;
    private List<String> images; // 0~5장, 선택사항

    public ReviewCreateRequest(int rating, String content, List<String> images) {
        this.rating = rating;
        this.content = content;
        this.images = images;
    }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}
