
//마이페이지 정보
///api/user/mypage

package com.cookandroid.gocafestudy.models.GET;

public class MyPageInfo {
    private User user;
    private int reviewCount;
    private int bookmarkCount;

    public MyPageInfo(User user, int reviewCount, int bookmarkCount) {
        this.user = user;
        this.reviewCount = reviewCount;
        this.bookmarkCount = bookmarkCount;
    }
    // Getter & Setter
    // ...

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getBookmarkCount() {
        return bookmarkCount;
    }

    public void setBookmarkCount(int bookmarkCount) {
        this.bookmarkCount = bookmarkCount;
    }
}
