package com.cookandroid.gocafestudy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ReviewViewHolder> {

    private Context context;
    private List<MyReviewItem> reviewList;

    public MyReviewsAdapter(Context context, List<MyReviewItem> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        MyReviewItem item = reviewList.get(position);

        holder.tvCafeName.setText(item.cafeName);
        holder.tvReviewText.setText(item.reviewText);
        holder.ratingBar.setRating(item.rating);

        // 카페 사진 로드
        Glide.with(context).load(item.imageUrl).into(holder.ivCafeImage);

        // 삭제 버튼 클릭
        holder.btnDelete.setOnClickListener(v -> {
            reviewList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, reviewList.size());
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCafeImage;
        TextView tvCafeName, tvReviewText, btnDelete;
        RatingBar ratingBar;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCafeImage = itemView.findViewById(R.id.ivCafeImage);
            tvCafeName = itemView.findViewById(R.id.tvCafeName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
