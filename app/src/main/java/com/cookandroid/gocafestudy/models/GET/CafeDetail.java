package com.cookandroid.gocafestudy.models.GET;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

// 카페 상세정보
public class CafeDetail {
    private int cafeId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String description;

    // API 필드 'tel'을 DTO 필드 'phone'에 매핑
    @SerializedName("tel")
    private String phone;

    private int americanoPrice;
    private String businessHours;
    private boolean hasParking;
    private String mood;

    private String aiSummary;
    private boolean isSaved;
    private List<String> images;
    @SerializedName("averageRating")
    private float reviewAverage;
    private int reviewCount;

    // 💡 NullPointerException 방지를 위해 기본값으로 빈 리스트 초기화
    private List<Review> recentReviews = new ArrayList<>();

    // 전체 생성자
    public CafeDetail(int cafeId, String name, String address,
                      double latitude, double longitude,
                      String description, String phone,
                      int americanoPrice, String businessHours, boolean hasParking,
                      String mood, String aiSummary,
                      boolean isSaved,
                      List<String> images, float reviewAverage, int reviewCount,
                      List<Review> recentReviews) {

        this.cafeId = cafeId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.phone = phone;
        this.americanoPrice = americanoPrice;
        this.businessHours = businessHours;
        this.hasParking = hasParking;
        this.mood = mood;
        this.aiSummary = aiSummary;
        this.isSaved = isSaved;
        this.images = images;
        this.reviewAverage = reviewAverage;
        this.reviewCount = reviewCount;
        // null 체크 후 할당
        this.recentReviews = recentReviews != null ? recentReviews : new ArrayList<>();
    }

    // 기본 생성자 (Retrofit/Gson용)
    public CafeDetail() {
        this.recentReviews = new ArrayList<>();
    }

    // --- Getter & Setter ---
    public int getCafeId() { return cafeId; }
    public void setCafeId(int cafeId) { this.cafeId = cafeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getAmericanoPrice() { return americanoPrice; }
    public void setAmericanoPrice(int americanoPrice) { this.americanoPrice = americanoPrice; }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public boolean isHasParking() { return hasParking; }
    public void setHasParking(boolean hasParking) { this.hasParking = hasParking; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public float getReviewAverage() { return reviewAverage; }
    public void setReviewAverage(float reviewAverage) { this.reviewAverage = reviewAverage; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public List<Review> getRecentReviews() { return recentReviews; }
    public void setRecentReviews(List<Review> recentReviews) {
        this.recentReviews = recentReviews != null ? recentReviews : new ArrayList<>();
    }
}
