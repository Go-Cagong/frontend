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

import com.bumptech.glide.Glide;
import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.models.GET.MyReviewItem;
import com.cookandroid.gocafestudy.repository.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ViewHolder> {

    private Context context;
    private List<MyReviewItem> reviews;
    private OnReviewClickListener listener;

    // ⭐ 클릭 리스너 추가
    public interface OnReviewClickListener {
        void onReviewClick(int cafeId);
    }

    public MyReviewsAdapter(Context context, List<MyReviewItem> reviews, OnReviewClickListener listener) {
        this.context = context;
        this.reviews = reviews;
        this.listener = listener;
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

        holder.tvCafeName.setText(review.getCafeName());
        holder.ratingBar.setRating(review.getRating());
        holder.tvReviewText.setText(review.getContent());

        // 리뷰 이미지
        holder.layoutReviewImages.removeAllViews();
        List<String> reviewImages = review.getImages();
        if (reviewImages != null && !reviewImages.isEmpty()) {
            holder.scrollReviewImages.setVisibility(View.VISIBLE);

            for (String imgUrl : reviewImages) {
                ImageView imageView = new ImageView(context);
                int size = (int) (100 * context.getResources().getDisplayMetrics().density);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                params.setMargins(0, 0, 16, 0);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Glide.with(context).load(imgUrl).into(imageView);
                holder.layoutReviewImages.addView(imageView);
            }
        } else {
            holder.scrollReviewImages.setVisibility(View.GONE);
        }

        // ⭐ 삭제 버튼
        holder.btnDelete.setOnClickListener(v -> deleteReview(review.getReviewId(), position));

        // ⭐ 아이템 클릭 → 상세 바텀시트
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReviewClick(review.getCafeId());
            }
        });
    }

    private void deleteReview(int reviewId, int position) {
        RetrofitClient.getReviewApi(context).deleteReview(reviewId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            reviews.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, reviews.size());
                            Toast.makeText(context, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "삭제 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "삭제 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCafeName, tvReviewText, btnDelete;
        RatingBar ratingBar;
        HorizontalScrollView scrollReviewImages;
        LinearLayout layoutReviewImages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCafeName = itemView.findViewById(R.id.tvCafeName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            scrollReviewImages = itemView.findViewById(R.id.scrollReviewImages);
            layoutReviewImages = itemView.findViewById(R.id.layoutReviewImages);
        }
    }
}
