package com.example.moodpredictor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("William", "LoginActivity started");
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view){
        EditText usernameEditText = findViewById(R.id.userNameText);
        EditText passEditText = findViewById(R.id.passwordText);
        String username = usernameEditText.getText().toString();
        String password = passEditText.getText().toString();
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Context context = getApplicationContext();
                        if (task.isSuccessful()){
                            Log.d("William", "Login success");
                            CharSequence text = "Successfully logged in.";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Log.d("William", "Login failure");
                            CharSequence text = "Failed to login. Check your credentials.";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
    }
}
