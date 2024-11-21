package com.example.mobilecyclingmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.Comments;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comments> commentList;

    // Constructor
    private FirestoreRepositoryImpl<User> repository;

    public CommentAdapter(List<Comments> commentList) {
        this.commentList = commentList;
        repository = new FirestoreRepositoryImpl<>("users", User.class);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comments comment = commentList.get(position);
        holder.tvName.setText(comment.getUserID());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        Date date = comment.getTimestamp().toDate();
        String formattedDate = dateFormat.format(date);
        holder.tvDate.setText(formattedDate);

        holder.tvComment.setText(comment.getComment());

        repository.readByField("uid", comment.getUserID(), new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User) result;
                holder.tvName.setText(user.getFirstName() + " " + user.getLastName());
            }

            @Override
            public void onFailure(Exception e) {
                holder.tvName.setText("Unknown User");
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvComment = itemView.findViewById(R.id.tvComment);
        }
    }
}
