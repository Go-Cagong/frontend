
// 카페 상세정보
///api/cafe/{id}


package com.cookandroid.gocafestudy.models.GET;

import java.util.List;

// 카페 상세정보
public class CafeDetail {
    private int cafeId;
    private String name;
    private String address;
    private String description;
    private String phone;
    private int americanoPrice;
    private String businessHours;
    private boolean hasParking;
    private String mood;
    private List<String> images;
    private float reviewAverage;
    private int reviewCount;
    private List<Review> recentReviews;

    public CafeDetail(int cafeId, String name, String address, String description, String phone,
                      int americanoPrice, String businessHours, boolean hasParking, String mood,
                      List<String> images, float reviewAverage, int reviewCount, List<Review> recentReviews) {
        this.cafeId = cafeId;
        this.name = name;
        this.address = address;
        this.description = description;
        this.phone = phone;
        this.americanoPrice = americanoPrice;
        this.businessHours = businessHours;
        this.hasParking = hasParking;
        this.mood = mood;
        this.images = images;
        this.reviewAverage = reviewAverage;
        this.reviewCount = reviewCount;
        this.recentReviews = recentReviews;
    }
    // Getter & Setter
    // ...

    public int getCafeId() {
        return cafeId;
    }

    public void setCafeId(int cafeId) {
        this.cafeId = cafeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAmericanoPrice() {
        return americanoPrice;
    }

    public void setAmericanoPrice(int americanoPrice) {
        this.americanoPrice = americanoPrice;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public boolean isHasParking() {
        return hasParking;
    }

    public void setHasParking(boolean hasParking) {
        this.hasParking = hasParking;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public float getReviewAverage() {
        return reviewAverage;
    }

    public void setReviewAverage(float reviewAverage) {
        this.reviewAverage = reviewAverage;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<Review> getRecentReviews() {
        return recentReviews;
    }

    public void setRecentReviews(List<Review> recentReviews) {
        this.recentReviews = recentReviews;
    }
}
