package com.example.re_solomio;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyDesignAdapter extends RecyclerView.Adapter<MyDesignAdapter.ViewHolder> {

    private final List<Bitmap> designList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Bitmap bitmap);
    }

    public MyDesignAdapter(List<Bitmap> designList, OnItemClickListener listener) {
        this.designList = designList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.design_thumbnail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap = designList.get(position);
        holder.thumbnailView.setImageBitmap(bitmap);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(bitmap));
    }

    @Override
    public int getItemCount() {
        return designList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.thumbnail_image);
        }
    }
}