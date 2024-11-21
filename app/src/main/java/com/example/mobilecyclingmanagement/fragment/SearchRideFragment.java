package com.example.mobilecyclingmanagement.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.adapter.SearchRideAdapter;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.Route;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.example.mobilecyclingmanagement.view.MapActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;


public class SearchRideFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private FirestoreRepositoryImpl<Route> repository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_search_ride, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        fab = view.findViewById(R.id.fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextInputEditText search = view.findViewById(R.id.searchRide);
        repository = new FirestoreRepositoryImpl<>("route", Route.class);

        repository.readAll(new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                recyclerView.setAdapter(new SearchRideAdapter(getContext(), (List<Route>) result));

                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<Route> filteredList = new ArrayList<>();

                        if (s.toString().isEmpty()) {
                            recyclerView.setAdapter(new SearchRideAdapter(getContext(), (List<Route>) result));
                            return;
                        }

                        for (Route route : (List<Route>) result) {
                            String query = s.toString().toLowerCase();
                            if (route.getStartingName().toLowerCase().contains(query) || route.getEndingName().toLowerCase().contains(query)
                            ) {
                                filteredList.add(route);
                            }
                        }

                        recyclerView.setAdapter(new SearchRideAdapter(getContext(), filteredList));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MapActivity.class));
            }
        });
        return view;
    }
}