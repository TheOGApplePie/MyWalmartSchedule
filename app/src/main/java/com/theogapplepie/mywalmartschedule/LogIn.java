package com.theogapplepie.mywalmartschedule;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity implements View.OnClickListener {
    EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            if (mUser.isEmailVerified()) {
                loadMainActivity();
            }
        }
        findViewById(R.id.signup).setOnClickListener(this);
        findViewById(R.id.forgot_password).setOnClickListener(this);
        findViewById(R.id.submitButton).setOnClickListener(this);

        emailEditText = findViewById(R.id.emailSignInEditText);
        passwordEditText = findViewById(R.id.passwordSignInEditText);
    }

    public void loadMainActivity() {
        mUser = mAuth.getCurrentUser();
        if (mUser.isEmailVerified()) {
            Intent intent = new Intent(LogIn.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LogIn.this, "You need to verify your email in order to continue.", Toast.LENGTH_SHORT).show();
            passwordEditText.setText("");
            emailEditText.setText("");
        }
    }

    private void signIn() {

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Your email address is required.");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address. Ex. example@mail.com");
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("You must enter a password with at least 6 characters.");
            passwordEditText.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Your password must be at least 6 characters long.");
            passwordEditText.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadMainActivity();
                } else if (task.isCanceled()) {
                    Toast.makeText(LogIn.this, "Log in cancelled. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        emailEditText.setError("The email is not associated with any account. Please review the email and try again or create a new account.");
                        emailEditText.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        passwordEditText.setError("The password is incorrect.");
                        passwordEditText.requestFocus();
                    } catch (Exception e) {
                        Toast.makeText(LogIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitButton:
                signIn();
                break;
            case R.id.signup:
                startActivity(new Intent(this, SignUp.class));
                finish();
                break;
            case R.id.forgot_password:
                startActivity(new Intent(this, ForgotPassword.class));
                finish();
                break;
        }
    }
}
