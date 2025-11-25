package com.cookandroid.gocafestudy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.models.GET.MyReviewItem;
import com.cookandroid.gocafestudy.repository.MockRepository;

import java.util.List;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ViewHolder> {

    private Context context;
    private List<MyReviewItem> reviews;
    private MockRepository repository;

    public MyReviewsAdapter(Context context, List<MyReviewItem> reviews) {
        this.context = context;
        this.reviews = reviews;
        this.repository = new MockRepository();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyReviewItem review = reviews.get(position);

        // 1. 텍스트 및 별점 데이터 바인딩
        holder.tvCafeName.setText(review.getCafeName());
        holder.ratingBar.setRating(review.getRating());
        holder.tvReviewText.setText(review.getContent());

        // 2. 대표 이미지 처리 (String 이름 -> int 리소스 ID 변환)
        int cafeImgResId = getResourceId(review.getCafeImageUrl());
        if (cafeImgResId != 0) {
            holder.ivCafeImage.setImageResource(cafeImgResId);
        } else {
            // 이미지를 못 찾았을 경우 기본 이미지 설정 (필요시)
            holder.ivCafeImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // 3. 리뷰 이미지 목록 처리 (동적 생성)
        // 기존에 추가된 뷰들을 모두 제거 (재사용 문제 방지)
        holder.layoutReviewImages.removeAllViews();

        List<String> reviewImages = review.getImages(); // 모델에 getReviewImages()가 있다고 가정

        if (reviewImages != null && !reviewImages.isEmpty()) {
            // 이미지가 있으면 스크롤뷰 보이기
            holder.scrollReviewImages.setVisibility(View.VISIBLE);

            for (String imgName : reviewImages) {
                // ImageView 동적 생성
                ImageView imageView = new ImageView(context);

                // 이미지 크기 설정 (dp 단위를 픽셀로 변환)
                int sizeInDp = 100; // 가로세로 100dp
                int sizeInPx = (int) (sizeInDp * context.getResources().getDisplayMetrics().density);

                // 마진 설정
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
                params.setMargins(0, 0, 16, 0); // 오른쪽 마진
                imageView.setLayoutParams(params);

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // 이미지 리소스 설정
                int reviewImgResId = getResourceId(imgName);
                if (reviewImgResId != 0) {
                    imageView.setImageResource(reviewImgResId);
                }

                // 레이아웃에 추가
                holder.layoutReviewImages.addView(imageView);
            }
        } else {
            // 이미지가 없으면 스크롤뷰 숨기기 (공간 차지 안 함)
            holder.scrollReviewImages.setVisibility(View.GONE);
        }

        // 4. 삭제 버튼 클릭
        holder.btnDelete.setOnClickListener(v -> {
            repository.deleteReview(review.getReviewId());
            reviews.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, reviews.size());
            Toast.makeText(context, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    // 문자열(파일명)로 리소스 ID를 찾는 헬퍼 메소드
    private int getResourceId(String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCafeImage;
        TextView tvCafeName, tvReviewText, btnDelete;
        RatingBar ratingBar;

        // 추가된 뷰
        HorizontalScrollView scrollReviewImages;
        LinearLayout layoutReviewImages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCafeImage = itemView.findViewById(R.id.ivCafeImage);
            tvCafeName = itemView.findViewById(R.id.tvCafeName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ratingBar = itemView.findViewById(R.id.ratingBar);

            // XML ID 연결
            scrollReviewImages = itemView.findViewById(R.id.scrollReviewImages);
            layoutReviewImages = itemView.findViewById(R.id.layoutReviewImages);
        }
    }
}