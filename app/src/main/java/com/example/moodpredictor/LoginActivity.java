package com.example.moodpredictor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
    }


}
