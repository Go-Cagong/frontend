package com.cookandroid.gocafestudy.repository;

import com.cookandroid.gocafestudy.datas.MockData;
import com.cookandroid.gocafestudy.models.GET.*;
import com.cookandroid.gocafestudy.models.POST.*;
import com.cookandroid.gocafestudy.models.DELETE.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MockRepository {

    // ---------------------------
    // User 관련
    // ---------------------------
    public static List<User> getUsers() {
        return MockData.getUsers();
    }

    // ---------------------------
    // Cafe 관련 (GET)
    // ---------------------------
    public List<CafeMapItem> getCafeMap() {
        return MockData.getCafeMap();
    }

    public CafeDetail getCafeDetail(int cafeId) {
        return MockData.getCafeDetail(cafeId);
    }

    // ---------------------------
    // Review 관련 (GET)
    // ---------------------------
    public List<Review> getReviewsByCafeId(int cafeId) {
        return MockData.getCafeReviews(cafeId);
    }

    /*
    // ---------------------------
    // Review 관련 (POST/DELETE)
    */
    public ReviewCreateResponse addReview(int cafeId, ReviewCreateRequest request, int userId) {
        int newReviewId = MockData.getReviews().size() + 301;


        // POST DTO 타입으로 Review 객체 생성
        ReviewCreateResponse.Review postReview =
                new ReviewCreateResponse.Review(
                        newReviewId,
                        cafeId,
                        userId,
                        request.getRating(),
                        request.getContent(),
                        new java.util.Date(),  // Date 사용
                        request.getImages()

                );

//        // GET Review 타입은 MockData에 넣어서 리스트 유지
//        List<String> images = request.getImages() != null ? request.getImages() : new ArrayList<>();
//        MockData.getReviews().add(
//                new com.cookandroid.gocafestudy.models.GET.Review(
//                        newReviewId,
//                        userId,
//                        userName,
//                        cafeId,
//                        request.getRating(),
//                        request.getContent(),
//                        new java.util.Date().toString(),
//                        images
//                )
//        );


        return new ReviewCreateResponse("리뷰가 등록되었습니다.", postReview);
    }

    public ReviewDeleteResponse deleteReview(int reviewId) {
        Review target = null;
        for (Review r : MockData.getReviews()) {
            if (r.getReviewId() == reviewId) {
                target = r;
                break;
            }
        }

        ReviewDeleteResponse response = new ReviewDeleteResponse();

        if (target != null) {
            MockData.getReviews().remove(target);
            response.message = "리뷰가 삭제되었습니다.";
            response.reviewId = reviewId;
        } else {
            response.message = "삭제할 리뷰를 찾을 수 없습니다.";
            response.reviewId = reviewId;
        }

        return response;
    }

    // ---------------------------
    // Bookmark 관련 (GET)
    // ---------------------------
//    public List<Bookmark> getBookmarksByUserId(int userId) {
//        List<Bookmark> result = new ArrayList<>();
//        for (Bookmark b : MockData.getBookmarks()) {
//            if (b.getUserId() == userId) result.add(b);
//        }
//        return result;
//    }
    public BookmarkCreateResponse createBookmark(int cafeId) {
        // 실제 서버는 Request Body 없이 URL + 토큰으로 처리 가능
        // MockData에서는 bookmark_id 자동 증가처럼 처리
        int newBookmarkId = MockData.getNextBookmarkId(); // MockData에서 생성 함수 필요

        BookmarkCreateResponse response = new BookmarkCreateResponse();
        response.message = "카페가 저장되었습니다.";
        response.bookmark_id = newBookmarkId;
        response.cafe_id = cafeId;

        // MockData에 저장 (목적: 상태 관리용)
        MockData.addBookmark(cafeId, newBookmarkId);

        return response;
    }
//    public BookmarkDeleteResponse deleteBookmark(int cafeId) {
//        boolean removed = false;
//        Iterator<Bookmark> iter = MockData.getBookmarks().iterator();
//        while (iter.hasNext()) {
//            Bookmark b = iter.next();
//            if (b.getCafeId() == cafeId) {
//                iter.remove();
//                removed = true;
//                break;
//            }
//        }
//        BookmarkDeleteResponse response = new BookmarkDeleteResponse();
//        response.message = removed ? "저장한 카페가 삭제되었습니다." : "삭제할 카페를 찾을 수 없습니다.";
//        return response;
//    }

    // ---------------------------
    // MyPage / User Info (GET)
    // ---------------------------

//    public List<MyReviewItem> getMyReviews(int userId) {
//        // 이미 MockData에 API 응답 형식으로 데이터가 정의되어 있으므로,
//        // 별도의 변환 없이 MockData 리스트를 바로 반환합니다.
//        return MockData.getMyReviews();
//    }
}
