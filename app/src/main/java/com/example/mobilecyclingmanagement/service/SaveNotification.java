package com.example.mobilecyclingmanagement.service;

import android.content.Context;
import android.widget.Toast;

import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.Notification;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;

public class SaveNotification {

    public static void saveNotification(Context context, String userId, String message) {
        FirestoreRepositoryImpl<Notification> repository = new FirestoreRepositoryImpl<>("notifications", Notification.class);
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        Notification notification = new Notification(userId, message, new com.google.firebase.Timestamp(currentTimeInSeconds, 0));

        repository.create(notification, new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Notification failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
