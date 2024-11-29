package com.example.mobilecyclingmanagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.Route;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.example.mobilecyclingmanagement.view.ViewSharedRouteActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchRideAdapter extends RecyclerView.Adapter<SearchRideAdapter.MyViewHolder> {

    private Context context;
    private List<Route> routeList;

    private FirestoreRepositoryImpl<User> repository;

    public SearchRideAdapter(Context context, List<Route> routeList) {
        this.context = context;
        this.routeList = routeList;
        repository = new FirestoreRepositoryImpl<>("users", User.class);
    }

    @NonNull
    @Override
    public SearchRideAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ride, parent, false );
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRideAdapter.MyViewHolder holder, int position) {
        Route route = routeList.get(position);

        holder.originText.setText("Origin: " + route.getStartingName());
        holder.destinationText.setText("Destination: " + route.getEndingName());
        if (route.getDatePosted() != null) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            Date date = route.getDatePosted().toDate();


            holder.dateText.setText("Date: " + dateFormat.format(date));
            holder.timeText.setText("Time: " + timeFormat.format(date));
        } else {

            holder.dateText.setText("Date: Not available");
            holder.timeText.setText("Time: Not available");
        }

        repository.readByField("uid", route.getUserId(), new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User) result;

                holder.userNameText.setText("Posted by: " + user.getFirstName() + " " + user.getLastName());
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        holder.viewAllButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewSharedRouteActivity.class);
            intent.putExtra("route", route);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView originText, destinationText, userNameText, dateText, timeText;
        Button viewAllButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            originText = itemView.findViewById(R.id.origin_text);
            destinationText = itemView.findViewById(R.id.destination_text);
            userNameText = itemView.findViewById(R.id.user_name_text);
            dateText = itemView.findViewById(R.id.date_text);
            timeText = itemView.findViewById(R.id.time_text);
            viewAllButton = itemView.findViewById(R.id.view_all_button);
        }
    }

}
