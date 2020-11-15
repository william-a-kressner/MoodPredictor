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
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d("William", "Started SignUpActivity");
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUpUser(View view){
        EditText usernameEditText = findViewById(R.id.userNameText);
        EditText firstPassEditText = findViewById(R.id.passwordText);
        EditText secondPassEditText = findViewById(R.id.passwordConfirmText);
        String username = usernameEditText.getText().toString();
        String passOne = firstPassEditText.getText().toString();
        String passTwo = secondPassEditText.getText().toString();
        if (!passOne.equals(passTwo)){
            // Passwords ain't equal
            Context context = getApplicationContext();
            CharSequence text = "Passwords do not match.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(username, passOne)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Context context = getApplicationContext();
                        if (task.isSuccessful()){
                            Log.d("William", "Account creation success");
                            CharSequence text = "Successfully created account.";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                        }else{
                            Log.d("William", task.getException().toString());
                            Log.d("William", "Account creation failure");
                            CharSequence text = "Error! Check to make sure your email is formatted correctly.";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
    }
}
