package com.example.mobilecyclingmanagement.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecyclingmanagement.LandingActivity;
import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.adapter.UserAdapter;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private FirestoreRepositoryImpl<User> repository = new FirestoreRepositoryImpl<>("users", User.class);

    private FloatingActionButton fabLogout;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository.readAll(new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                recyclerView.setAdapter(new UserAdapter((List<User>) result));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        fabLogout = findViewById(R.id.logout);
        fabLogout.setOnClickListener(v -> {
            // Create and show a confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        mAuth.signOut();
                        startActivity(new Intent(this, LandingActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> {

                        dialog.dismiss();
                    })
                    .show();
        });


    }
}