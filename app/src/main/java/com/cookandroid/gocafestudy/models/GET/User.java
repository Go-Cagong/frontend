//유저 정보 조회
///api/user 등
package com.cookandroid.gocafestudy.models.GET;

public class User {
    private int userId;
    private String name;

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

}
