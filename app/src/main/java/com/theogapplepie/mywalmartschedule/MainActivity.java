package com.theogapplepie.mywalmartschedule;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    /**
     * Creating an instance of FirebaseAuth object in order to detect if user is authorized to use
     * the app
     */
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestoreDatabase;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestoreSettings mFirebaseFirestoreSettings;



    NumberFormat nFormat = new DecimalFormat("00");
    ArrayList<Shift> mShifts = new ArrayList<>();

    ArrayList<Shift> mTodaysShifts = new ArrayList<>();

    ProgressBar progressBar;
    String currentUser, position, store;
    TextView fetchingShift, zeroShifts;
    BottomNavigationView buttonNav;

    /**
     * Creating an options menu to be displayed on the top right
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.extra_menu, menu);
        return true;
    }

    /**
     * In this method, a bottom navigation button menu is being created. We need a navigation
     * listener to pass in which button was selected. Based on that, we set the view of the fragment
     * container to the appropriate fragment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();


        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LogIn.class));
            finish();
        }
        mFirestoreDatabase = FirebaseFirestore.getInstance();
        mFirebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder().build();
        mFirestoreDatabase.setFirestoreSettings(mFirebaseFirestoreSettings);
        zeroShifts = findViewById(R.id.zero_shifts);
        zeroShifts.setVisibility(View.GONE);

        String email = mFirebaseUser.getEmail();
        DocumentReference documentReference = mFirestoreDatabase.collection("Users").document(email);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    try{
                        currentUser = document.get("Name").toString();
                        position = document.get("Job").toString();
                        store = document.get("Store Number").toString();
                        progressBar = findViewById(R.id.progress_circular);
                        fetchingShift = findViewById(R.id.simple_message);

                        buttonNav = findViewById(R.id.navigation);
                        mTodaysShifts.clear();
                        getAssociatesListFromFirestore(position, store);
                        buttonNav.setOnNavigationItemSelectedListener(navListener);
                    } catch (Exception e){
                        setBadgeNumber();
                        Toast.makeText(MainActivity.this, "It looks like we're missing some of your details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    logOut();
                }
            }
        });
    }

    public void setBadgeNumber() {
            startActivity(new Intent(this, UpdateUserName.class));
    }

    public void getAssociatesListFromFirestore(final String job, final String storeNumber) {
        buttonNav.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        fetchingShift.setVisibility(View.VISIBLE);
        mShifts.clear();
        try {
                mFirestoreDatabase.collection(storeNumber).document(job).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            @SuppressWarnings("unchecked")
                            List<String> listOfAssociates = (List<String>)documentSnapshot.get("Associates");
                            if(listOfAssociates!= null && listOfAssociates.size() > 0){
                                if(listOfAssociates.contains(currentUser)){
                                    for (String i: listOfAssociates){
                                        getShiftsFromFirestore(job,storeNumber, i);
                                        }
                                    }else{
                                        zeroShifts.setVisibility(View.VISIBLE);
                                        zeroShifts.setText(R.string.your_not_on_this_schedule);
                                        fetchingShift.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        }
                                }else{
                                    zeroShifts.setVisibility(View.VISIBLE);
                                    zeroShifts.setText(R.string.your_store_department);
                                    fetchingShift.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    }
                            }
                    }
                });
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            logOut();
            }
        }
    public void getShiftsFromFirestore(String job, final String storeNumber, final String user) {
        final Calendar today = Calendar.getInstance();
        try {
            mFirestoreDatabase.collection(storeNumber).document(job).collection(user).orderBy("begin", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Shift shift = queryDocumentSnapshot.toObject(Shift.class);
                            if(shift.getEnd().getTime() - shift.getBegin().getTime()>= 14400000){
                                shift.setEmployee(user);
                                if (user.equals(currentUser)){
                                    mShifts.add(shift);
                                }
                                Calendar shiftCalendar = Calendar.getInstance();
                                shiftCalendar.setTimeInMillis(shift.getBegin().getTime());
                                if (shiftCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) &&
                                        shiftCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                        shiftCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
                                    mTodaysShifts.add(shift);
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        fetchingShift.setVisibility(View.GONE);
                        buttonNav.setVisibility(View.VISIBLE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,new TodaysScheduleFragment()).commit();
                    }
                }

            });
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            logOut();
        }
    }

    public int[] getShiftForDay(int year, int month, int day) {
        Calendar startTimeCalendar = Calendar.getInstance();
        Calendar endTimeCalendar = Calendar.getInstance();
        int[] shiftTime = new int[4];
        shiftTime[0] = 0;
        shiftTime[1] = 0;
        shiftTime[2] = 0;
        shiftTime[3] = 0;

        for (int index = 0; index < mShifts.size(); index++) {
            startTimeCalendar.setTimeInMillis(mShifts.get(index).getBegin().getTime());
            if (startTimeCalendar.get(Calendar.DAY_OF_MONTH) == day && startTimeCalendar.get(Calendar.MONTH) == month
                    && startTimeCalendar.get(Calendar.YEAR) == year) {
                endTimeCalendar.setTimeInMillis(mShifts.get(index).getEnd().getTime());
                shiftTime[0] = startTimeCalendar.get(Calendar.HOUR_OF_DAY);
                shiftTime[1] = startTimeCalendar.get(Calendar.MINUTE);
                shiftTime[2] = endTimeCalendar.get(Calendar.HOUR_OF_DAY);
                shiftTime[3] = endTimeCalendar.get(Calendar.MINUTE);
            }
        }

        return shiftTime;
    }

    public String[] getBreaks(int shiftStartHour, int shiftStartMin, int shiftEndHour, int shiftEndMin) {
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        String[] shiftBreaks = new String[3];
        String firstHalf;
        String secondHalf;
        shiftBreaks[0] = "Not Applicable";
        shiftBreaks[1] = "Not Applicable";
        shiftBreaks[2] = "Not Applicable";
        long fourHrs = 14400000;
        long fiveHrs = 18000000;
        long sevenHrs = 25200000;

        startCalendar.set(Calendar.HOUR_OF_DAY, shiftStartHour);
        startCalendar.set(Calendar.MINUTE, shiftStartMin);
        endCalendar.set(Calendar.HOUR_OF_DAY, shiftEndHour);
        endCalendar.set(Calendar.MINUTE, shiftEndMin);
        long shiftLength = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();

        if (shiftLength >= fourHrs) {
            startCalendar.add(Calendar.HOUR_OF_DAY, 2);
            firstHalf = nFormat.format(startCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    nFormat.format(startCalendar.get(Calendar.MINUTE));
            startCalendar.add(Calendar.MINUTE, 15);

            secondHalf = nFormat.format(startCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    nFormat.format(startCalendar.get(Calendar.MINUTE));
            shiftBreaks[0] = firstHalf + " - " + secondHalf;
        }
        if (shiftLength >= sevenHrs) {
            startCalendar.add(Calendar.HOUR_OF_DAY, 2);
            firstHalf = nFormat.format(startCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    nFormat.format(startCalendar.get(Calendar.MINUTE));
            startCalendar.add(Calendar.HOUR_OF_DAY, 1);

            secondHalf = nFormat.format(startCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    nFormat.format(startCalendar.get(Calendar.MINUTE));
            shiftBreaks[1] = firstHalf + " - " + secondHalf;

            startCalendar.add(Calendar.HOUR_OF_DAY, 1);
            firstHalf = nFormat.format(startCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    nFormat.format(startCalendar.get(Calendar.MINUTE));
            startCalendar.add(Calendar.MINUTE, 15);

            secondHalf = nFormat.format(startCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    nFormat.format(startCalendar.get(Calendar.MINUTE));
            shiftBreaks[2] = firstHalf + " - " + secondHalf;
        } else if (shiftLength > fiveHrs) {
            startCalendar.add(Calendar.HOUR_OF_DAY, 2);
            firstHalf = nFormat.format(startCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    nFormat.format(startCalendar.get(Calendar.MINUTE));
            startCalendar.add(Calendar.MINUTE, 30);
            secondHalf = nFormat.format(startCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    nFormat.format(startCalendar.get(Calendar.MINUTE));
            shiftBreaks[1] = firstHalf + " - " + secondHalf;
        }
        return shiftBreaks;
    }

    public void logOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = new TodaysScheduleFragment();
                    switch (menuItem.getItemId()) {
                        case R.id.navMyShifts:
                            selectedFragment = new MyShiftsFragment();
                            zeroShifts.setVisibility(View.GONE);
                            break;
                        case R.id.navTodaysSchedule:
                            selectedFragment = new TodaysScheduleFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Privacy Policy" menu option
            case R.id.nav_privacy:
                openWebPage("https://thedanialali.wordpress.com/privacy-policy/");
                return true;
            // Respond to a click on the "Feedback" menu option
            case R.id.nav_feedback:
                openWebPage("https://goo.gl/forms/qcCEFSJK4ZBKuffx2");
                return true;
            // Respond to a click on the "Feedback" menu option
            case R.id.nav_edit_user_data:
                Intent intent = new Intent(MainActivity.this, UpdateUserName.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            return true;
            // Respond to a click on the "Log Out" menu option
            case R.id.nav_logout:
                logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openWebPage(String url) {
        //Create intent to visit web page with specified URL
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public ArrayList<Shift> getTodaysShifts(){

        return mTodaysShifts;
    }
    public String getCurrentUser(){
        return currentUser;
    }
}