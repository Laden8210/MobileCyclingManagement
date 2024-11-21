package com.example.mobilecyclingmanagement.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.callback.FirestoreRepository;
import com.example.mobilecyclingmanagement.model.Coordination;
import com.example.mobilecyclingmanagement.model.Route;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ShareRouteActivity extends AppCompatActivity {

    private TextInputEditText fromField;
    private TextInputEditText toField;
    private TextInputEditText descriptionField;
    private MaterialButton shareButton;
    private FirebaseAuth mAuth;

    private FirestoreRepositoryImpl<Route> routeRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_route);

        mAuth = FirebaseAuth.getInstance();

        routeRepository = new FirestoreRepositoryImpl<>("route", Route.class);

        fromField = findViewById(R.id.from);
        toField = findViewById(R.id.to);
        descriptionField = findViewById(R.id.description);
        shareButton = findViewById(R.id.shareButton);

        String startingLocation = "";
        String endingLocation = "";

        Coordination startingCoordination;
        Coordination endingCoordination;
        if (getIntent().hasExtra("startingLocation") && getIntent().hasExtra("destinationLocation") && getIntent().hasExtra("startingCoordination") && getIntent().hasExtra("destinationCoordination")) {
            startingLocation = getIntent().getStringExtra("startingLocation");
            endingLocation = getIntent().getStringExtra("destinationLocation");

            startingCoordination = getIntent().getParcelableExtra("startingCoordination");
            endingCoordination = getIntent().getParcelableExtra("destinationCoordination");


            fromField.setText(startingLocation);
            toField.setText(endingLocation);
        } else {
            endingCoordination = null;
            startingCoordination = null;
        }

        shareButton.setOnClickListener(v -> {
            String fromText = fromField.getText() != null ? fromField.getText().toString().trim() : "";
            String toText = toField.getText() != null ? toField.getText().toString().trim() : "";
            String descriptionText = descriptionField.getText() != null ? descriptionField.getText().toString().trim() : "";

            if (fromText.isEmpty()) {
                Toast.makeText(this, "Please enter the starting point.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (toText.isEmpty()) {
                Toast.makeText(this, "Please enter the destination.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (descriptionText.isEmpty()) {
                Toast.makeText(this, "Please add a description.", Toast.LENGTH_SHORT).show();
                return;
            }

            shareRoute(fromText, toText, descriptionText, startingCoordination, endingCoordination);
        });
    }

    private void shareRoute(String from, String to, String description, Coordination startingCoordination, Coordination endingCoordination) {
        Route route = new Route();
        route.setStartingName(from);
        route.setEndingName(to);
        route.setDescription(description);
        route.setStartingCoordination(startingCoordination);
        route.setEndingCoordination(endingCoordination);
        route.setUserId(mAuth.getCurrentUser().getUid());

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        route.setDatePosted(new com.google.firebase.Timestamp(currentTimeInSeconds, 0));

        routeRepository.create(route, new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                Toast.makeText(ShareRouteActivity.this, "Route shared successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ShareRouteActivity.this, "Failed to share route: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
