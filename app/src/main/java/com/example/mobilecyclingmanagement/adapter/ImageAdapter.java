package com.example.mobilecyclingmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.api.ApiAddress;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Context context;
    private final List<String> images;

    public ImageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_card, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String url = ApiAddress.BASE_URL + images.get(position);
        Glide.with(context)
                .load(url)
                .into(holder.imageView);
        holder.cardView.setOnClickListener(view -> showImageDialog(url));


    }

    private void showImageDialog(String url) {
        // Create a custom layout for the dialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null);
        ImageView dialogImageView = dialogView.findViewById(R.id.dialogImageView);


        Glide.with(context).load(url).into(dialogImageView);
        new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .show();
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
