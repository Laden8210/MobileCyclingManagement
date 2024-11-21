package com.example.mobilecyclingmanagement.callback;

public interface ImageUploadCallback {
    void onSuccess(String filePath);
    void onFailure(String errorMessage);
}
