12-01
3.1 로그인 및 토큰 처리
LoginActivity에서 OAuth 로그인 수행
로그인 성공 시 JWT 토큰을 UserSessionManager에 저장
AuthInterceptor가 Retrofit 요청 시 자동으로 Header에 Authorization: Bearer {Token} 추가
TestAuthApi 호출로 로그인한 유저 정보 반환 ** 토큰 부분 모를 경우 참고하세요

3.2 카페 지도 및 마커
MapFragment에서 NaverMap 초기화
CafeApi.getCafeMapItems() 호출로 지도 마커 표시
마커 클릭 → showCafeDetailBottomSheet() 호출 → 상세정보 BottomSheet 표시

3.3 북마크 처리
BottomSheet의 "저장하기/저장 취소" 버튼
BookmarkApi.getBookmarkState() 호출 → 현재 저장 상태 확인
버튼 클릭 시
저장되지 않은 경우: BookmarkApi.createBookmark() 호출
저장된 경우: BookmarkApi.deleteBookmark() 호출
응답 메시지 Toast로 표시, 버튼 텍스트 동적으로 변경

3.4 리뷰 관리
리뷰 작성: ActivityWriteReview → ReviewCreateRequest POST
