package com.example.mobilecyclingmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobilecyclingmanagement.view.HeroActivity;
import com.example.mobilecyclingmanagement.view.RegisterActivity;

public class LandingActivity extends AppCompatActivity {

    TextView tvRegister;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing);

        tvRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);

        tvRegister.setOnClickListener(this::registerAction);
        btnLogin.setOnClickListener(this::loginAction);

    }

    private void loginAction(View view) {
        Intent intent = new Intent(this, HeroActivity.class);
        startActivity(intent);
    }

    private void registerAction(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}