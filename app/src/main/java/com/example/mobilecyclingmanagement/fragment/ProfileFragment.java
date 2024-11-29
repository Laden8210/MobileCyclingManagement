package com.example.mobilecyclingmanagement.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.model.User;
import com.example.mobilecyclingmanagement.repository.FirestoreRepositoryImpl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView profileName, profilePhone, profileAddress;


    private FirestoreRepositoryImpl<User> repository = new FirestoreRepositoryImpl<>("users", User.class);
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Button btnEdit, btnChangePassword;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextViews
        profileName = view.findViewById(R.id.profile_name);
        profilePhone = view.findViewById(R.id.profile_phone);
        profileAddress = view.findViewById(R.id.profile_address);
        btnChangePassword = view.findViewById(R.id.btn_change_password);

        // Initialize Edit button
        btnEdit = view.findViewById(R.id.btn_edit_details);

        // Set default values or update them based on user data
        initializeProfileData();

        // Set Edit button listener
        btnEdit.setOnClickListener(v -> showEditDialog());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

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
                Toast.makeText(getContext(), "Failed to load profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show dialog for editing user details
    private void showEditDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_details, null);

        TextInputEditText etFirstName = dialogView.findViewById(R.id.et_first_name);
        TextInputEditText etLastName = dialogView.findViewById(R.id.et_last_name);
        TextInputEditText etAddress = dialogView.findViewById(R.id.et_address);

        // Prefill fields with current user data
        repository.readByField("uid", auth.getUid(), new FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                User user = (User) result;
                etFirstName.setText(user.getFirstName());
                etLastName.setText(user.getLastName());
                etAddress.setText(user.getAddress());
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Edit Details")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String firstName = etFirstName.getText().toString().trim();
                    String lastName = etLastName.getText().toString().trim();
                    String address = etAddress.getText().toString().trim();

                    if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(address)) {
                        Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update user details
                    User updatedUser = new User();
                    updatedUser.setFirstName(firstName);
                    updatedUser.setLastName(lastName);
                    updatedUser.setAddress(address);

                    repository.updateField("users", auth.getUid(), "firstName", firstName, new FirestoreCallback() {
                        @Override
                        public void onSuccess(Object result) {

                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                    repository.updateField("users", auth.getUid(), "lastName", lastName, new FirestoreCallback() {
                        @Override
                        public void onSuccess(Object result) {

                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                    repository.updateField("users", auth.getUid(), "address", address, new FirestoreCallback() {
                        @Override
                        public void onSuccess(Object result) {

                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });

                    // Update UI
                    profileName.setText(firstName + " " + lastName);
                    profileAddress.setText(address);

                    Toast.makeText(getContext(), "Details updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);

        TextInputEditText etOldPassword = dialogView.findViewById(R.id.et_old_password);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String oldPassword = etOldPassword.getText().toString().trim();
                    String newPassword = etNewPassword.getText().toString().trim();
                    String confirmPassword = etConfirmPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                        Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(getContext(), "New passwords do not match!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    updatePassword(oldPassword, newPassword);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updatePassword(String oldPassword, String newPassword) {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();

            if (email != null) {
                // Re-authenticate the user
                auth.signInWithEmailAndPassword(email, oldPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Update password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Failed to update password: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "Authentication failed. Incorrect old password.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
