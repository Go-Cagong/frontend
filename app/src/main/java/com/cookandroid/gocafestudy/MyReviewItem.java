package com.cookandroid.gocafestudy;

public class MyReviewItem {

    public String cafeName;
    public String reviewText;
    public String imageUrl;
    public int rating;

    public MyReviewItem(String cafeName, String reviewText, String imageUrl, int rating) {
        this.cafeName = cafeName;
        this.reviewText = reviewText;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }
}
