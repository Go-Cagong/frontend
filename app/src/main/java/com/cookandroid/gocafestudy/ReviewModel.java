package com.cookandroid.gocafestudy;

public class ReviewModel {
    private String content;
    private String date;
    private float rating;

    public ReviewModel(String content, String date, float rating) {
        this.content = content;
        this.date = date;
        this.rating = rating;
    }

    public String getContent() { return content; }
    public String getDate() { return date; }
    public float getRating() { return rating; }
}