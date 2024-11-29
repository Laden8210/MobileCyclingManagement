package com.example.mobilecyclingmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.example.mobilecyclingmanagement.view.AdminActivity;
import com.example.mobilecyclingmanagement.view.HeroActivity;
import com.example.mobilecyclingmanagement.view.RegisterActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;

import com.google.firebase.auth.FirebaseAuth;

public class LandingActivity extends AppCompatActivity {

    TextView tvRegister;
    Button btnLogin;
    private TextInputEditText emailField;
    private TextInputEditText passwordField;
    private FirebaseAuth firebaseAuth;
    private FirestoreRepositoryImpl<User> firestoreRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing);
        firebaseAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        FirebaseApp.initializeApp(getApplicationContext());

        tvRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);

        tvRegister.setOnClickListener(this::registerAction);
        btnLogin.setOnClickListener(this::loginAction);
        firestoreRepository = new FirestoreRepositoryImpl<>("users", User.class);

        if (firebaseAuth.getCurrentUser() != null) {

            firestoreRepository.readByField("uid", firebaseAuth.getCurrentUser().getUid(), new FirestoreCallback() {
                @Override
                public void onSuccess(Object result) {
                    User user = (User) result;

                    Log.d("User Verification", user.isVerified() + ":"  + user.isAdmin());
                    if (user.isVerified()) {

                        if (user.isAdmin()) {
                            Intent intent = new Intent(LandingActivity.this, AdminActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                        Toast.makeText(LandingActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LandingActivity.this, HeroActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LandingActivity.this, "Please contact the administrator to verify your email address", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {

                }
            });

        }

    }

    private void loginAction(View view) {

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        firestoreRepository.readByField("uid", firebaseAuth.getCurrentUser().getUid(), new FirestoreCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                User user = (User) result;

                                Log.d("User Verification", user.isVerified() + ":"  + user.isAdmin());
                                if (user.isVerified()) {

                                    if (user.isAdmin()) {
                                        Intent intent = new Intent(LandingActivity.this, AdminActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    Toast.makeText(LandingActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LandingActivity.this, HeroActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LandingActivity.this, "Please contact the administrator to verify your email address", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });


                    } else {

                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerAction(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}