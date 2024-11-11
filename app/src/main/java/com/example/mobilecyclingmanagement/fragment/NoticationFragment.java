package com.example.mobilecyclingmanagement.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.adapter.NotificationAdapter;


public class NoticationFragment extends Fragment {


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

        rvNotification = view.findViewById(R.id.recyclerView);
        rvNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotification.setAdapter(new NotificationAdapter());

        return view;
    }
}