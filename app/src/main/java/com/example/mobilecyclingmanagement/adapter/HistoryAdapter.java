package com.example.mobilecyclingmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecyclingmanagement.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {



    public HistoryAdapter() {

    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView originText;
        TextView destinationText;
        TextView timeText;
        TextView dateText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            originText = itemView.findViewById(R.id.origin_text);
            destinationText = itemView.findViewById(R.id.destination_text);
            timeText = itemView.findViewById(R.id.time_text);
            dateText = itemView.findViewById(R.id.date_text);
        }
    }
}
