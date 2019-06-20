package com.theogapplepie.mywalmartschedule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateUserName extends AppCompatActivity implements View.OnClickListener{
private FirebaseAuth mAuth;
private FirebaseUser mFirebaseUser;
EditText badgeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);
        badgeNumber = findViewById(R.id.badgeNumberEditText);
        findViewById(R.id.ContinueButton).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        submitUserInfo();
    }
    public void submitUserInfo(){
        String badge_number = badgeNumber.getText().toString().trim();
        if (badge_number.isEmpty()){
            badgeNumber.setError("It looks like you forgot about your badge number!");
            badgeNumber.requestFocus();
            return;
        }
        try{Integer.parseInt(badge_number);
        } catch(Exception e){
            badgeNumber.setError("Your badge number can only be numeric!");
            badgeNumber.requestFocus();
            return;
        }

        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(badge_number).build();
        mFirebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(UpdateUserName.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Thank you! You can now view your shifts!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
