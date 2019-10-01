package com.theogapplepie.mywalmartschedule;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    EditText emailEditText;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        findViewById(R.id.sendEmail).setOnClickListener(this);
        findViewById(R.id.f_login).setOnClickListener(this);
        emailEditText = findViewById(R.id.account_email);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sendEmail){
            sendEmail();
        }else{
            startActivity(new Intent(this, LogIn.class));
            finish();
        }
    }

    public void sendEmail() {
        if (emailEditText.getText().toString().isEmpty()) {
            emailEditText.setError("It looks like you forgot to enter your email!");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString().trim()).matches()) {
            emailEditText.setError("You need to enter a valid email address. Example: example@mail.com");
            emailEditText.requestFocus();
            return;
        }
        mAuth.sendPasswordResetEmail(emailEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this, "An email has been sent. Please check your email for further instructions.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPassword.this, LogIn.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
