package com.example.mobilecyclingmanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    // Declare variables for the TextViews and Buttons
    private TextView profileName, profilePhone, profileAddress;


    private FirestoreRepositoryImpl<User> repository = new FirestoreRepositoryImpl<>("users", User.class);
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle arguments if any
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextViews
        profileName = view.findViewById(R.id.profile_name);

        profilePhone = view.findViewById(R.id.profile_phone);
        profileAddress = view.findViewById(R.id.profile_address);



        // Set default values or update them based on user data
        initializeProfileData();

        return view;
    }

    // Initialize or update profile data
    private void initializeProfileData() {


        // Get the current user's data from Firestore
        repository.readByField("uid", auth.getUid(), new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User) result;
                profileName.setText(user.getFirstName() + " " + user.getLastName());
                profilePhone.setText(user.getEmail());
                profileAddress.setText(user.getAddress());
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }
}
