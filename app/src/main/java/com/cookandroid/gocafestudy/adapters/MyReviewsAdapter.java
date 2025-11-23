package com.cookandroid.gocafestudy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        this.repository = new MockRepository(); // 목 레파지토리 연결
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
        // 이미지는 목데이터로 고정
        holder.ivCafeImage.setImageResource(R.drawable.ic_cafe1_img);

        // 삭제 버튼 클릭
        holder.btnDelete.setOnClickListener(v -> {
            // 목 레파지토리 삭제 호출
            repository.deleteReview(review.getReviewId());

            // RecyclerView에서 아이템 제거
            reviews.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, reviews.size());

            Toast.makeText(context, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCafeImage;
        TextView tvCafeName, tvReviewText, btnDelete;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCafeImage = itemView.findViewById(R.id.ivCafeImage);
            tvCafeName = itemView.findViewById(R.id.tvCafeName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
