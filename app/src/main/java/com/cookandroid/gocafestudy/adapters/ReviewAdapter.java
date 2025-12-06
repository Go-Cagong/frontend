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
import java.util.Random;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;
    private static final int[] ANIMAL_PROFILES = {
            R.drawable.ic_profile_cat,
            R.drawable.ic_profile_dog,
            R.drawable.ic_profile_rabbit,
            R.drawable.ic_profile_panda,
            R.drawable.ic_profile_bear
    };
    private Random random = new Random();

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

        // 랜덤 동물 프로필 사진 배정
        int randomAnimal = ANIMAL_PROFILES[random.nextInt(ANIMAL_PROFILES.length)];
        holder.ivProfilePicture.setImageResource(randomAnimal);

        holder.tvUserName.setText(review.getUserName());

        holder.ratingBar.setRating(review.getRating());
        holder.tvReviewText.setText(review.getContent());
        holder.tvReviewDate.setText(review.getCreatedAt());

        String createdAt = review.getCreatedAt();   // 예: "2025-11-14T10:21:00"
        String displayDate = createdAt;
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
        ImageView ivProfilePicture;
        TextView tvUserName;
        RatingBar ratingBar;
        TextView tvReviewText;
        TextView tvReviewDate;
        HorizontalScrollView scrollReviewImages;
        LinearLayout layoutReviewImages;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            scrollReviewImages = itemView.findViewById(R.id.scrollReviewImages);
            layoutReviewImages = itemView.findViewById(R.id.layoutReviewImages);
        }
    }

}
