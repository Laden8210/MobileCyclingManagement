package com.example.mobilecyclingmanagement.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    // Declare class-level variables for all text fields
    private TextInputEditText emailField;
    private TextInputEditText firstNameField;
    private TextInputEditText lastNameField;
    private TextInputEditText genderField;
    private TextInputEditText birthdateField;
    private TextInputEditText addressField;
    private TextInputEditText passwordField;
    private TextInputEditText confirmPasswordField;
    private Button registerButton;


    private FirebaseAuth firebaseAuth;
    private FirestoreRepositoryImpl<User> firestoreRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = findViewById(R.id.emailField);
        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        genderField = findViewById(R.id.genderField);
        birthdateField = findViewById(R.id.birthdateField);
        addressField = findViewById(R.id.addressField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);

        firebaseAuth = FirebaseAuth.getInstance();
        firestoreRepository = new FirestoreRepositoryImpl<>("users", User.class);


        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(view -> validateAndRegister());


    }

    private void validateAndRegister() {
        String email = emailField.getText().toString().trim();
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String gender = genderField.getText().toString().trim();
        String birthdate = birthdateField.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                gender.isEmpty() || birthdate.isEmpty() || address.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {

                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(verificationTask -> {
                                            if (verificationTask.isSuccessful()) {
                                                String userId = firebaseUser.getUid();
                                                User user = new User(userId, email, firstName, lastName, gender, birthdate, address, password, false, false);

                                                firestoreRepository.create(user, new FirestoreCallback() {
                                                    @Override
                                                    public void onSuccess(Object result) {
                                                        Toast.makeText(RegisterActivity.this,
                                                                "Registration successful. Please verify your email before logging in.",
                                                                Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(Exception e) {
                                                        Toast.makeText(RegisterActivity.this,
                                                                "Registration successful but saving data failed: " + e.getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(RegisterActivity.this,
                                                        "Failed to send verification email: " + verificationTask.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

}
