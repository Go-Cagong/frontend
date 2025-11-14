package com.cookandroid.gocafestudy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // 서버 이미지 로딩

import java.util.List;

public class SavedCafesAdapter extends RecyclerView.Adapter<SavedCafesAdapter.CafeViewHolder> {

    private Context context;
    private List<CafeItem> cafeList;

    public SavedCafesAdapter(Context context, List<CafeItem> cafeList) {
        this.context = context;
        this.cafeList = cafeList;
    }

    @NonNull
    @Override
    public CafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_cafe, parent, false);
        return new CafeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CafeViewHolder holder, int position) {
        CafeItem cafe = cafeList.get(position);
        holder.tvCafeName.setText(cafe.getName());
        holder.tvCafeLocation.setText(cafe.getLocation());

        // 서버 연동 시 이미지 URL 로딩
        Glide.with(context)
                .load(cafe.getImageUrl())
                .placeholder(R.drawable.ic_cafe1_img)
                .into(holder.ivCafeImage);
    }

    @Override
    public int getItemCount() {
        return cafeList.size();
    }

    public static class CafeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCafeImage;
        TextView tvCafeName, tvCafeLocation;

        public CafeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCafeImage = itemView.findViewById(R.id.ivCafeImage);
            tvCafeName = itemView.findViewById(R.id.tvCafeName);
            tvCafeLocation = itemView.findViewById(R.id.tvCafeLocation);
        }
    }
}
