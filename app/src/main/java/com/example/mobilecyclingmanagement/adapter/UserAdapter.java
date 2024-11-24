package com.example.mobilecyclingmanagement.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    private FirestoreRepositoryImpl<User> repository = new FirestoreRepositoryImpl<>("users", User.class);

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.getFirstName() + " " + user.getLastName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvDetails.setText("Gender: " + user.getGender() + " | Birthdate: " + user.getBirthdate());
        holder.tvStatus.setText("Admin: " + (user.isAdmin() ? "Yes" : "No") + " | Verified: " + (user.isVerified() ? "Yes" : "No"));
        holder.btnConfirm.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirm Verification")
                    .setMessage("Are you sure you want to verify this user?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        user.setVerified(true);
                        repository.update(user.getUid(), user, new FirestoreCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                new AlertDialog.Builder(holder.itemView.getContext())
                                        .setTitle("Success")
                                        .setMessage("The user has been successfully verified.")
                                        .setPositiveButton("OK", (innerDialog, innerWhich) -> innerDialog.dismiss())
                                        .show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                new AlertDialog.Builder(holder.itemView.getContext())
                                        .setTitle("Error")
                                        .setMessage("Failed to verify the user. Please try again.")
                                        .setPositiveButton("OK", (innerDialog, innerWhich) -> innerDialog.dismiss())
                                        .show();
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvDetails, tvStatus;
        Button btnConfirm;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
        }
    }
}
