package com.example.mobilecyclingmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecyclingmanagement.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {



    // Constructor for NotificationAdapter
    public NotificationAdapter() {

    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    // ViewHolder class to hold item views
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationText;
        TextView notificationTime;
        CardView notificationCard;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
