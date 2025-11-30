package com.cookandroid.gocafestudy.models.GET;// package com.cookandroid.gocafestudy.models.GET;

import com.cookandroid.gocafestudy.models.GET.CafeMapItem;

import java.util.List;

public class CafeMapResponse {
    private int count;
    private List<CafeMapItem> cafes; // 필드 이름을 API 응답과 동일하게 'cafes'로 지정

    // 기본 생성자 (필수)
    public CafeMapResponse() {}

    // Getter & Setter (Lombok 미사용 시 직접 구현)
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CafeMapItem> getCafes() { // MapFragment에서 이 메소드를 사용해 리스트를 얻습니다.
        return cafes;
    }

    public void setCafes(List<CafeMapItem> cafes) {
        this.cafes = cafes;
    }
}