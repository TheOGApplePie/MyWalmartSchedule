package com.theogapplepie.mywalmartschedule;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserName extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
private FirebaseAuth mAuth;
private FirebaseUser mUser;
private FirebaseFirestore mFirestore;
ArrayAdapter<CharSequence> stores, jobs;
EditText userName;
Spinner position, storesSpinner;
String job, store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);
        userName = findViewById(R.id.userNameEditText);

        storesSpinner = findViewById(R.id.spinner_stores);
        stores = ArrayAdapter.createFromResource(this, R.array.stores, android.R.layout.simple_spinner_item);
        stores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storesSpinner.setAdapter(stores);
        storesSpinner.setOnItemSelectedListener(this);

        position = findViewById(R.id.spinner_jobs);
        jobs = ArrayAdapter.createFromResource(this, R.array.jobs, android.R.layout.simple_spinner_item);
        jobs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        position.setAdapter(jobs);
        position.setOnItemSelectedListener(this);

        findViewById(R.id.continueButton).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings mFirebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder().build();
        mFirestore.setFirestoreSettings(mFirebaseFirestoreSettings);
    }

    @Override
    public void onClick(View v) {
        submitUserInfo();
    }

    public void submitUserInfo(){
        String userNameString = userName.getText().toString().trim();
        if (userNameString.isEmpty()){
            userName.setError("It looks like you forgot about your name! Remember to include your legal first name and your last name's initial");
            userName.requestFocus();
            return;
        }
        userNameString = userNameString.substring(0,1).toUpperCase()
                .concat(userNameString.substring(1,userNameString.length()-1))
                .concat(userNameString.substring(userNameString.length()-1).toUpperCase());
        Toast.makeText(this, userNameString, Toast.LENGTH_SHORT).show();

        if(store == null || store.equals("")){
            Toast.makeText(this, "You need to select your store number.", Toast.LENGTH_SHORT).show();
            storesSpinner.requestFocus();
            return;
        }
        if(job == null || job.equals("")){
            Toast.makeText(this, "You need to select your position.", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("Store Number", store);
        userInfo.put("Job", job);
        userInfo.put("Name", userNameString);

        Map<String, Object> deviceTokens = new HashMap<>();
        deviceTokens.put("token", SharedPreferenceManager.getInstance(this).getToken());
        mFirestore.collection(store).document(job).collection("Tokens").add(deviceTokens);
        mFirestore.collection("Users").document(mUser.getEmail()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Thank you! You can now view your shifts!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Something went wrong. Please email the developer.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getAdapter() == jobs){
            job = parent.getItemAtPosition(position).toString();
            switch (job){
                case "Select Your Position":
                    job = "";
                    break;
                case "Cashier":
                    job = "Cashier";
                    break;
                case "Customer Service Manager":
                    job = "CSM";
                    break;
                case "Customer Service Associate":
                    job = "CS";
                    break;
                case "Self Checkout Associate":
                    job = "SCO";
                    break;
            }
        }
        if(parent.getAdapter() == stores){
            store = parent.getItemAtPosition(position).toString();
            if (store.equals("Select Your Store")){
                    store = "";
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}