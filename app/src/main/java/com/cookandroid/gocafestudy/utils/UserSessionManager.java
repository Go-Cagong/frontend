// UserSessionManager.java

package com.cookandroid.gocafestudy.utils; // 패키지 생성 및 저장 권장

import com.cookandroid.gocafestudy.models.GET.UserResponse;

public class UserSessionManager {
    private static UserSessionManager instance;
    private UserResponse currentUser;

    private UserSessionManager() {}

    public static synchronized UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    public void login(UserResponse user) {
        this.currentUser = user;
    }

    public UserResponse getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
        // 토큰 삭제 로직은 LogoutActivity에서 담당합니다.
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}