package com.example.mobilecyclingmanagement.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.adapter.HistoryAdapter;
import com.example.mobilecyclingmanagement.adapter.SearchRideAdapter;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.Route;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {


    private RecyclerView recyclerView;
    private FirestoreRepositoryImpl<Route> repository;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_history, container, false);

        mAuth = FirebaseAuth.getInstance();
        repository = new FirestoreRepositoryImpl<>("route", Route.class);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        repository.readAll(new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {

                List<Route> routes = (List<Route>) result;

                List<Route> filteredRoutes = new ArrayList<>();

                for (Route route : routes) {
                    if (route.getUserId().equals(mAuth.getCurrentUser().getUid())) {
                        filteredRoutes.add(route);
                    }
                }

                recyclerView.setAdapter(new SearchRideAdapter(getContext(), filteredRoutes));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        return view;
    }
}