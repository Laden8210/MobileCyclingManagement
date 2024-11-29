package com.example.mobilecyclingmanagement.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.mobilecyclingmanagement.LandingActivity;
import com.example.mobilecyclingmanagement.R;
import com.example.mobilecyclingmanagement.fragment.HistoryFragment;
import com.example.mobilecyclingmanagement.fragment.HomeFragment;
import com.example.mobilecyclingmanagement.fragment.NewFeedFragment;
import com.example.mobilecyclingmanagement.fragment.NoticationFragment;
import com.example.mobilecyclingmanagement.fragment.ProfileFragment;
import com.example.mobilecyclingmanagement.fragment.SearchRideFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HeroActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    // Launcher for requesting notification permission
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        Toast.makeText(HeroActivity.this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                        // Call your method to show notifications here
                        showTestNotification();
                    } else {
                        Toast.makeText(HeroActivity.this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.menu_home);
        }

        // Request POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_home) {
            changeFragment(new HomeFragment());
        } else if (item.getItemId() == R.id.menu_news_feeds) {
            changeFragment(new NewFeedFragment());
        } else if (item.getItemId() == R.id.menu_notification) {
            changeFragment(new NoticationFragment());
        } else if (item.getItemId() == R.id.menu_setting) {
            changeFragment(new ProfileFragment());
        } else if (item.getItemId() == R.id.menu_history) {
            changeFragment(new HistoryFragment());
        } else if (item.getItemId() == R.id.menu_search_ride) {
            changeFragment(new SearchRideFragment());
        } else if (item.getItemId() == R.id.menu_logout) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(HeroActivity.this, LandingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void showTestNotification() {
        // Placeholder method for showing a notification
        Toast.makeText(this, "Test Notification Logic", Toast.LENGTH_SHORT).show();
        // Implement your notification logic here
    }
}
