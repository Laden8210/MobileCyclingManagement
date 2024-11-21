package com.example.mobilecyclingmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.model.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {


    private Context context;
    private List<Notification> filteredNotification;

    public NotificationAdapter(Context context, List<Notification> filteredNotification) {
        this.context = context;
        this.filteredNotification = filteredNotification;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = filteredNotification.get(position);

        holder.notificationText.setText(notification.getMessage());


        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        Date date = notification.getTimestamp().toDate();
        String formattedDate = dateFormat.format(date);
        holder.notificationTime.setText(formattedDate);

    }

    @Override
    public int getItemCount() {
        return filteredNotification.size();
    }

    // ViewHolder class to hold item views
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationText;
        TextView notificationTime;
        CardView notificationCard;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationText = itemView.findViewById(R.id.notificationText);
            notificationTime = itemView.findViewById(R.id.notificationTime);

        }
    }
}
