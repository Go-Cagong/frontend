package com.cookandroid.gocafestudy.models.GET;

import java.util.List;

// List<Review> reviews; 필드를 제거하고 필요한 필드만 남깁니다.

public class UserResponse {
    private int id;
    private String email;
    private String name;
    private String provider; // 필요하다면 유지
    private String role;     // 필요하다면 유지

    // getter
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getProvider() { return provider; }
    public String getRole() { return role; }
}
