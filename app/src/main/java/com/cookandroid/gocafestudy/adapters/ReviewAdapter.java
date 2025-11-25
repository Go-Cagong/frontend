package com.cookandroid.gocafestudy.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.models.GET.Review;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tvUserName.setText(review.getUserName());

        holder.ratingBar.setRating(review.getRating());
        holder.tvReviewText.setText(review.getContent());
        holder.tvReviewDate.setText(review.getCreatedAt());

        // --- 리뷰 이미지 처리 ---
        holder.layoutReviewImages.removeAllViews(); // 재사용 대비 초기화

        if (review.getImages() != null && !review.getImages().isEmpty()) {
            holder.scrollReviewImages.setVisibility(View.VISIBLE);

            for (String imageUrl : review.getImages()) {
                ImageView imageView = new ImageView(holder.itemView.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160, 160);
                params.setMargins(0, 0, 16, 0);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // Glide 사용 예시
                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_cafe1_img)
                        .into(imageView);

                holder.layoutReviewImages.addView(imageView);
            }
        } else {
            holder.scrollReviewImages.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        RatingBar ratingBar;
        TextView tvReviewText;
        TextView tvReviewDate;
        HorizontalScrollView scrollReviewImages;
        LinearLayout layoutReviewImages;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            scrollReviewImages = itemView.findViewById(R.id.scrollReviewImages);
            layoutReviewImages = itemView.findViewById(R.id.layoutReviewImages);
        }
    }

}
