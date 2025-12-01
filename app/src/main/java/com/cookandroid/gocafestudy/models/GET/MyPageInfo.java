
//마이페이지 정보
///api/user/mypage

package com.cookandroid.gocafestudy.models.GET;

import com.google.gson.annotations.SerializedName;

public class MyPageInfo {

    @SerializedName("user")
    private User user;

    @SerializedName("counts")
    private Counts counts;

    public User getUser() {
        return user;
    }

    public Counts getCounts() {
        return counts;
    }

    public int getReviewCount() {
        return counts != null ? counts.getReviewCount() : 0;
    }

    public int getBookmarkCount() {
        return counts != null ? counts.getBookmarkCount() : 0;
    }

    public static class Counts {
        @SerializedName("bookmark_count")
        private int bookmarkCount;

        @SerializedName("review_count")
        private int reviewCount;

        public int getBookmarkCount() {
            return bookmarkCount;
        }

        public int getReviewCount() {
            return reviewCount;
        }
    }
}

