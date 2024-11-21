package com.example.mobilecyclingmanagement.callback;

public interface FirestoreCallback {
    void onSuccess(Object result);
    void onFailure(Exception e);
}