package com.cookandroid.gocafestudy.models.GET;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookmarkListResponse {

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("bookmarks")
    private List<Bookmark> bookmarks;

    public int getTotalCount() {
        return totalCount;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }
}
