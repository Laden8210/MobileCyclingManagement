package com.example.mobilecyclingmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecyclingmanagement.R;

import java.util.ArrayList;
import java.util.List;

public class NewFeedAdapter extends RecyclerView.Adapter<NewFeedAdapter.MyViewHolder> {
    @NonNull
    @Override
    public NewFeedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_news_feed, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewFeedAdapter.MyViewHolder holder, int position) {

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.sample);
        images.add(R.drawable.sample);
        images.add(R.drawable.sample);
        images.add(R.drawable.sample);
        int numColumns = calculateNumberOfColumns(images.size());

        holder.recyclerView.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), numColumns));
        holder.recyclerView.setAdapter(new ImageAdapter(holder.itemView.getContext(), images));
    }

    private int calculateNumberOfColumns(int imageCount) {
        int minColumns = 2;
        int maxColumns = 3;

        if (imageCount <= 1) {
            return 1;
        } else if (imageCount <= 4) {
            return 2;
        } else if (imageCount <= 6) {
            return 3;
        } else {
            return maxColumns;
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}
