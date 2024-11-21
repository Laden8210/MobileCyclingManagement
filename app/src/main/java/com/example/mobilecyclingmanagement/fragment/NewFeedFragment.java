package com.example.mobilecyclingmanagement.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.adapter.NewFeedAdapter;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.Post;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class NewFeedFragment extends Fragment {


    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirestoreRepositoryImpl<Post> postRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_new_feed, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        displayRecentPost();
        return view;
    }

    private void displayRecentPost() {
        postRepository = new FirestoreRepositoryImpl<>("posts", Post.class);
        postRepository.readAll(new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                ArrayList<Post> posts = (ArrayList<Post>) result;
                recyclerView.setAdapter(new NewFeedAdapter(getContext(), posts));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}