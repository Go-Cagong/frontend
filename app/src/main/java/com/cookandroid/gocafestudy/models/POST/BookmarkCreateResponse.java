//카페 저장 후 서버 응답
// /api/cafes/{cafe_id}/bookmark
// Bookmark는 Request없음. 서버가 URL과 토큰만으로 처리 가능.


package com.cookandroid.gocafestudy.models.POST;

public class BookmarkCreateResponse {
    public String message;
    public int bookmark_id;
    public int cafe_id;

    public String getMessage() { return message; }
    public int getBookmarkId() { return bookmark_id; }
    public int getCafeId() { return cafe_id; }
}

