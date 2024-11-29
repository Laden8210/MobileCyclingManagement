package com.example.mobilecyclingmanagement.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.adapter.NotificationAdapter;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.Notification;
import com.example.mobilecyclingmanagement.model.Route;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class NoticationFragment extends Fragment {

    private FloatingActionButton fab;

    private FirestoreRepositoryImpl<Notification> repository;
    private FirebaseAuth mAuth;
    private RecyclerView rvNotification;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notication, container, false);

        repository = new FirestoreRepositoryImpl<>("notifications", Notification.class);

        mAuth = FirebaseAuth.getInstance();

        rvNotification = view.findViewById(R.id.recyclerView);
        rvNotification.setLayoutManager(new LinearLayoutManager(getContext()));



        repository.readAll(new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                List<Notification> filteredNotification = new ArrayList<>();
                List<Notification> notifications = (List<Notification>) result;

                for (Notification notification : notifications) {
                    Log.d("Notification", notification.getUserId() + " " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                    if (notification.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        filteredNotification.add(notification);
                    }
                }

                rvNotification.setAdapter(new NotificationAdapter(getContext(), filteredNotification));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        return view;
    }
}