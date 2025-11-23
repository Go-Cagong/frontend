package com.cookandroid.gocafestudy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.gocafestudy.R;
import com.cookandroid.gocafestudy.models.GET.Bookmark;

import java.util.List;

public class SavedCafesAdapter extends RecyclerView.Adapter<SavedCafesAdapter.ViewHolder> {

    private Context context;
    private List<Bookmark> bookmarkList;
    private OnCafeClickListener listener;

    public interface OnCafeClickListener {
        void onCafeClick(int cafeId);
    }

    public SavedCafesAdapter(Context context, List<Bookmark> bookmarkList, OnCafeClickListener listener) {
        this.context = context;
        this.bookmarkList = bookmarkList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_cafe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bookmark bookmark = bookmarkList.get(position);

        holder.tvCafeName.setText(bookmark.getCafeName());
        holder.tvAddress.setText(bookmark.getAddress());

        Glide.with(context)
                .load(bookmark.getMainImageUrl())
                .placeholder(R.drawable.ic_cafe1_img)
                .into(holder.ivCafeImage);

        holder.itemView.setOnClickListener(v -> {
            Log.d("SavedCafesAdapter", "Clicked cafeId=" + bookmark.getCafeId());
            if (listener != null) listener.onCafeClick(bookmark.getCafeId());
        });
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCafeImage;
        TextView tvCafeName, tvAddress;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCafeImage = itemView.findViewById(R.id.ivCafeImage);
            tvCafeName = itemView.findViewById(R.id.tvCafeName);
            tvAddress = itemView.findViewById(R.id.tvCafeLocation);
        }
    }


}
