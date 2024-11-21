package com.example.mobilecyclingmanagement.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Post {

    private String userUID;
    private String postID;
    private String post;

    private Timestamp timestamp;

    private List<String> imageUrl;

    private List<String> likes;

    private List<Comments> comments;

    public Post() {
    }

    public Post(String userUID, String postID, String post, Timestamp timestamp, List<String> imageUrl, List<String> likes, List<Comments> comments) {
        this.userUID = userUID;
        this.postID = postID;
        this.post = post;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.likes = likes;
        this.comments = comments;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }
}
