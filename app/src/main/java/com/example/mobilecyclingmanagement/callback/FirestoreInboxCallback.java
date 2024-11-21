package com.example.mobilecyclingmanagement.callback;

public interface FirestoreInboxCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
