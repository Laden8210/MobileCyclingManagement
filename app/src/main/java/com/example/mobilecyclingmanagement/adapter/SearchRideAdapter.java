package com.example.mobilecyclingmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecyclingmanagement.R;

public class SearchRideAdapter extends RecyclerView.Adapter<SearchRideAdapter.MyViewHolder> {
    @NonNull
    @Override
    public SearchRideAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ride, parent, false );
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRideAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
