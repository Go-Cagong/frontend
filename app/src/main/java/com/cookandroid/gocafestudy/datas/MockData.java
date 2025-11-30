package com.cookandroid.gocafestudy.datas;

import com.cookandroid.gocafestudy.models.GET.Bookmark;
import com.cookandroid.gocafestudy.models.GET.CafeDetail;
import com.cookandroid.gocafestudy.models.GET.CafeMapItem;
import com.cookandroid.gocafestudy.models.GET.MyPageInfo;
import com.cookandroid.gocafestudy.models.GET.MyReviewItem;
import com.cookandroid.gocafestudy.models.GET.Review;
import com.cookandroid.gocafestudy.models.GET.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MockData {

    // ---------------------------
    // 1. /api/cafe/map
    // ---------------------------
    public static List<CafeMapItem> getCafeMap() {
        List<CafeMapItem> cafes = new ArrayList<>();
        cafes.add(new CafeMapItem(1, "카페 꼼마 송도점", 37.398306, 126.633733, "조용함", 5300, true));
        cafes.add(new CafeMapItem(2, "스테이그린", 37.383189, 126.640720, "조용함", 2000, false));
        cafes.add(new CafeMapItem(3, "라운지25", 37.396184, 126.637766, "조용함", 4000, true));
        return cafes;
    }

    private static List<User> allUsers = Arrays.asList(
            new User(1, "김재웅"),
            new User(2, "이영희"),
            new User(3, "박민수")
    );

    // 특정 userId에 해당하는 이름 반환
    private static String getUserNameById(int userId) {
        for (User user : allUsers) {
            if (user.getUserId() == userId) return user.getName();
        }
        return "익명";
    }

    // ---------------------------
    // 2. /api/cafe/{id}
    // ---------------------------
    // package com.cookandroid.gocafestudy.datas;

// ... (기존 import 및 클래스 정의)

    // ---------------------------
// 2. /api/cafe/{id}
// ---------------------------
    public static CafeDetail getCafeDetail(int cafeId) {
        // ... (기존 images, recentReviews 코드)
        List<String> images = Arrays.asList(
                "https://go-cagong-bucket.s3.eu-north-1.amazonaws.com/cafe/1671c901-cc59-4630-b3f9-1bbb0a81ff0d_1000040212.jpg", // API 응답 이미지 사용
                "https://cdn.example.com/cafe1_2.jpg",
                "https://cdn.example.com/cafe1_3.jpg",
                "https://cdn.example.com/cafe1_4.jpg",
                "https://cdn.example.com/cafe1_5.jpg"
        );

        List<Review> recentReviews = getCafeReviews(cafeId);

        switch (cafeId) {
            case 1:
                return new CafeDetail(
                        1,
                        "카페 꼼마 송도점",
                        "인천 연수구 센트럴로 263",
                        37.398306, // 💡 추가: latitude
                        126.633733, // 💡 추가: longitude
                        "조용하고 넓은 좌석이 특징인 스터디 카페입니다.",
                        "032-111-1111", // phone (tel)
                        5300,
                        "09:00 - 22:00",
                        true,
                        "조용함",
                        "넓고 조용한 분위기, 다양한 음료가 준비되어 있습니다.", // 💡 추가: aiSummary
                        true,
                        images,
                        4.6F,
                        recentReviews.size(),
                        recentReviews
                );

            case 2:
                return new CafeDetail(
                        2,
                        "스테이그린",
                        "인천 연수구 송도동 30-1",
                        37.383189, // 💡 추가: latitude
                        126.640720, // 💡 추가: longitude
                        "식물 인테리어 감성 카페입니다.",
                        "032-222-2222",
                        2000,
                        "10:00 - 21:00",
                        true,
                        "조용함",
                        "화려한 식물 인테리어와 저렴한 가격이 특징입니다.", // 💡 추가: aiSummary
                        true,
                        images,
                        4.2F,
                        recentReviews.size(),
                        recentReviews
                );

            case 3:
                return new CafeDetail(
                        3,
                        "라운지25",
                        "인천 연수구 송도동 15-7",
                        37.396184, // 💡 추가: latitude
                        126.637766, // 💡 추가: longitude
                        "편의점+라운지형 카공공간입니다.",
                        "032-333-3333",
                        4000,
                        "24시간",
                        true,
                        "보통",
                        "24시간 이용 가능, 편의점과 결합된 신개념 카공 공간입니다.", // 💡 추가: aiSummary
                        false,
                        images,
                        4.0F,
                        recentReviews.size(),
                        recentReviews
                );
            case 4:
                return new CafeDetail(
                        4,
                        "라운지25",
                        "인천 연수구 송도동 15-7",
                        37.396184, // 💡 추가: latitude
                        126.637766, // 💡 추가: longitude
                        "편의점+라운지형 카공공간입니다.",
                        "032-333-3333",
                        4000,
                        "24시간",
                        true,
                        "보통",
                        "24시간 이용 가능, 편의점과 결합된 신개념 카공 공간입니다.", // 💡 추가: aiSummary
                        false,
                        images,
                        4.0F,
                        recentReviews.size(),
                        recentReviews
                );

            default:
                return new CafeDetail();
        }
    }
// ... (나머지 코드 유지)


    // ---------------------------
    // 3. /api/cafe/{id}/review
    // ---------------------------
    public static List<Review> getCafeReviews(int cafeId) {
        List<Review> filtered = new ArrayList<>();
        for (Review r : allReviews) {
            if (r.getCafeId() == cafeId) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    // ---------------------------
    // 4. /api/user/mypage
    // ---------------------------
    public static MyPageInfo getMyPageInfo() {
        // User 객체 생성 (id와 name만 있음)
        User user = new User(1, "카공러");
        return new MyPageInfo(user, 8, 6);
    }

    // ---------------------------
    // 5. /api/user/reviews
    // ---------------------------
    public static List<MyReviewItem> getMyReviews() {
        String img = "ic_cafe1_img";
        List<MyReviewItem> reviews = new ArrayList<>();
        reviews.add(new MyReviewItem(101, 201, "센트럴파크 카페", "ic_cafe1_img", 5, "넓은 공간과 편안한 좌석이 인상적이에요...",getRandomImages(img)));
        reviews.add(new MyReviewItem(98, 305, "송도 스터디룸", "ic_cafe1_img", 4, "카공 전용 공간이라 분위기가 최고예요.",getRandomImages(img)));
        return reviews;
    }

    // ---------------------------
    // 6. /api/user/bookmarks
    // ---------------------------
    public static List<Bookmark> getBookmarks() {
        List<Bookmark> bookmarks = new ArrayList<>();
        bookmarks.add(new Bookmark(50, 1, 1, "카페 꼼마 송도점", "인천 연수구 센트럴로 263", 4.8, "url", null));
        bookmarks.add(new Bookmark(51, 1, 2, "스테이그린", "인천 연수구 송도동 123", 4.5, "url", null));
        return bookmarks;
    }

    // MockData.java
    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "카공러"));
        users.add(new User(2, "김네이버"));
        users.add(new User(3, "송구글"));
        users.add(new User(4, "박다음"));
        return users;
    }

    private static final List<Review> allReviews = new ArrayList<>();

    static {
        // 이미지 통일 이름
        String img = "ic_cafe1_img";

        // -------------------
        // 카페 1 리뷰
        // -------------------
        allReviews.add(new Review(301, 21,"테드창",1, 5, "조용하고 분위기 좋아요! 디저트도 맛있고 재방문 의사 있습니다.", "2025-11-14", getRandomImages(img)));
        allReviews.add(new Review(302, 22,"김경태",1, 4, "자리 넓고 공부하기 좋아요.", "2025-11-13", getRandomImages(img)));
        allReviews.add(new Review(303, 23,"빈지노",1, 5, "커피 향이 진하고 만족스러워요.", "2025-11-12", getRandomImages(img)));
        allReviews.add(new Review(304, 24,"비와이",1, 4, "조용해서 집중하기 최적입니다.", "2025-11-11", getRandomImages(img)));

        // -------------------
        // 카페 2 리뷰
        // -------------------
        allReviews.add(new Review(401, 25,"유저1",2, 5, "식물 인테리어가 정말 예쁘네요.", "2025-11-14", getRandomImages(img)));
        allReviews.add(new Review(402, 26,"유저2",2, 4, "커피 맛도 좋고 편안한 분위기.", "2025-11-13", getRandomImages(img)));
        allReviews.add(new Review(403, 27,"유저3",2, 4, "주말에는 조금 붐비지만 그래도 좋아요.", "2025-11-12", getRandomImages(img)));
        allReviews.add(new Review(404, 28,"유저4",2, 5, "친구랑 사진 찍기 좋은 공간!", "2025-11-11", getRandomImages(img)));

        // -------------------
        // 카페 3 리뷰
        // -------------------
        allReviews.add(new Review(501, 29,"유저5",3, 4, "편의점과 라운지가 같이 있어서 편리해요.", "2025-11-14", getRandomImages(img)));
        allReviews.add(new Review(502, 30,"유저6",3, 5, "24시간 운영이라 언제든 공부 가능.", "2025-11-13", getRandomImages(img)));
        allReviews.add(new Review(503, 31,"유저7",3, 4, "조용하지만 사람 많으면 조금 시끄러움.", "2025-11-12", getRandomImages(img)));
        allReviews.add(new Review(504, 32,"유저8",3, 5, "자리 넓고 편안해서 좋아요.", "2025-11-11", getRandomImages(img)));

        allReviews.add(new Review(601, 29,"유저5",4, 4, "편의점과 라운지가 같이 있어서 편리해요.", "2025-11-14", getRandomImages(img)));
        allReviews.add(new Review(602, 30,"유저6",4, 5, "24시간 운영이라 언제든 공부 가능.", "2025-11-13", getRandomImages(img)));
        allReviews.add(new Review(603, 31,"유저7",4, 4, "조용하지만 사람 많으면 조금 시끄러움.", "2025-11-12", getRandomImages(img)));
        allReviews.add(new Review(604, 32,"유저8",4, 5, "자리 넓고 편안해서 좋아요.", "2025-11-11", getRandomImages(img)));

    }

    // 0~5장 랜덤 이미지 생성
    private static List<String> getRandomImages(String img) {
        Random random = new Random();
        int count = random.nextInt(6); // 0~5장 랜덤
        List<String> images = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            images.add(img);
        }
        return images;
    }



    public static List<Review> getReviews() {
        return allReviews;
    }

    private static int bookmarkCounter = 1;
    private static List<Integer> bookmarkedCafeIds = new ArrayList<>();

    public static int getNextBookmarkId() {
        return bookmarkCounter++;
    }

    public static void addBookmark(int cafeId, int bookmarkId) {
        bookmarkedCafeIds.add(cafeId);
    }

}
