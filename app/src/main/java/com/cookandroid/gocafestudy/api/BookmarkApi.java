package com.cookandroid.gocafestudy.api;

import com.cookandroid.gocafestudy.models.DELETE.BookmarkDeleteResponse;
import com.cookandroid.gocafestudy.models.GET.BookmarkIsSavedResponse;
import com.cookandroid.gocafestudy.models.GET.BookmarkListResponse;
import com.cookandroid.gocafestudy.models.POST.BookmarkCreateResponse;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookmarkApi {

    @GET("/api/cafes/{cafe_id}/bookmark")
    Call<BookmarkIsSavedResponse> getBookmarkState(
            @Path("cafe_id") int cafeId
    );
    @POST("/api/cafes/{cafe_id}/bookmark")
    Call<BookmarkCreateResponse> createBookmark(
            @Path("cafe_id") int cafeId
    );

    @DELETE("/api/cafes/{cafe_id}/bookmark")
    Call<BookmarkDeleteResponse> deleteBookmark(
            @Path("cafe_id") int cafeId
    );

    @GET("/api/user/bookmarks")
    Call<BookmarkListResponse> getMyBookmarks();
}

