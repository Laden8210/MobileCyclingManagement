package com.example.mobilecyclingmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.fragment.HomeFragment;
import com.example.mobilecyclingmanagement.model.Comments;
import com.example.mobilecyclingmanagement.model.Post;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.example.mobilecyclingmanagement.service.SaveNotification;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class NewFeedAdapter extends RecyclerView.Adapter<NewFeedAdapter.MyViewHolder> {
    private Context context;
    private List<Post> posts;

    private FirestoreRepositoryImpl<User> repository;

    public NewFeedAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        repository = new FirestoreRepositoryImpl<>("users", User.class);
    }

    @NonNull
    @Override
    public NewFeedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_news_feed, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewFeedAdapter.MyViewHolder holder, int position) {

        Post post = posts.get(position);
        List<String> images = new ArrayList<>(post.getImageUrl());
        int numColumns = calculateNumberOfColumns(images.size());

        holder.recyclerView.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), numColumns));
        holder.recyclerView.setAdapter(new ImageAdapter(holder.itemView.getContext(), images));

        holder.tvPost.setText(post.getPost());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        Date date = post.getTimestamp().toDate();
        String formattedDate = dateFormat.format(date);
        holder.tvDate.setText(formattedDate);


        holder.comment.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        if (post.getComments() == null) {
            post.setComments(new ArrayList<>());
        }
        holder.comment.setAdapter(new CommentAdapter(post.getComments()));


        int likeCount = (post.getLikes() != null) ? post.getLikes().size() : 0;
        holder.btnLike.setText(likeCount + " Likes");

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boolean isLikedByCurrentUser = post.getLikes() != null && post.getLikes().contains(currentUserUID);
        holder.btnLike.setOnClickListener(e -> {
            if (post.getLikes() == null) {
                post.setLikes(new ArrayList<>());
            }

            if (post.getLikes().contains(currentUserUID)) {
                // Unlike the post
                post.getLikes().remove(currentUserUID);
            } else {
                // Like the post
                post.getLikes().add(currentUserUID);
            }

            repository.updateField("posts", post.getPostID(), "likes", post.getLikes(), new FirestoreCallback() {
                @Override
                public void onSuccess(Object result) {
                    updateLikeButton(holder, post, currentUserUID);

                    // Notify the post owner about the like
                    repository.readByField("uid", currentUserUID, new FirestoreCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            User user = (User) result;
                            SaveNotification.saveNotification(
                                    holder.itemView.getContext(),
                                    post.getUserUID(),
                                    "Your post has been liked by " + user.getFirstName() + " " + user.getLastName()
                            );
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(holder.itemView.getContext(), "Failed to retrieve user info", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(holder.itemView.getContext(), "Failed to update likes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        repository.readByField("uid", post.getUserUID(), new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User) result;
                holder.tvRideName.setText(user.getFirstName() + " " + user.getLastName());
            }

            @Override
            public void onFailure(Exception e) {
                holder.tvRideName.setText("Unknown User");
            }
        });

        holder.btnComment.setOnClickListener(e -> {
            String comment = holder.etComment.getText().toString();
            if (comment.isEmpty()) {
                Toast.makeText(holder.itemView.getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            repository.readByField("uid", currentUserUID, new FirestoreCallback() {
                @Override
                public void onSuccess(Object result) {
                    User user = (User) result;
                    Comments comment = new Comments();
                    comment.setUserID(currentUserUID);
                    comment.setComment(holder.etComment.getText().toString());
                    comment.setTimestamp(new Timestamp(new Date()));

                    // Initialize the comments list if it's null
                    if (post.getComments() == null) {
                        post.setComments(new ArrayList<>()); // Initialize with a new ArrayList
                    }

                    post.getComments().add(comment);

                    repository.updateField("posts", post.getPostID(), "comments", post.getComments(), new FirestoreCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            // Notify the adapter and clear the comment input field
                            holder.comment.getAdapter().notifyDataSetChanged();
                            holder.etComment.setText("");

                            repository.readByField("uid", currentUserUID, new FirestoreCallback() {
                                @Override
                                public void onSuccess(Object result) {
                                    User user = (User) result;
                                    SaveNotification.saveNotification(holder.itemView.getContext(), post.getUserUID(), "Your post has been commented by " + user.getFirstName() + " " + user.getLastName());
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    holder.tvRideName.setText("Unknown User");
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(holder.itemView.getContext(), "Failed to add comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(holder.itemView.getContext(), "Failed to add comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void updateLikeButton(MyViewHolder holder, Post post, String currentUserUID) {
        int likeCount = (post.getLikes() != null) ? post.getLikes().size() : 0;
        holder.btnLike.setText(likeCount + " Likes");
        if (post.getLikes() != null && post.getLikes().contains(currentUserUID)) {
            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_thumb_up_off_alt_24, 0, 0, 0);
        } else {
            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_thumb_up_alt_24, 0, 0, 0);
        }
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
        return posts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView, comment;

        private TextView tvPost, tvRideName, tvDate;

        private Button btnLike, btnComment;

        TextInputEditText etComment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            tvPost = itemView.findViewById(R.id.tvContent);
            tvRideName = itemView.findViewById(R.id.tvRiderName);
            tvDate = itemView.findViewById(R.id.tvDate);

            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);

            comment = itemView.findViewById(R.id.comment);
            etComment = itemView.findViewById(R.id.etComment);

        }
    }
}
