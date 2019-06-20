package com.theogapplepie.mywalmartschedule;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {



    /**
     * Creating an instance of FirebaseAuth object in order to detect if user is authorized to use
     * the app
     */
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mFirestoreDatabase;
    private FirebaseFirestoreSettings mFirebaseFirestoreSettings;
    NumberFormat nFormat = new DecimalFormat("00");
    ArrayList<Shift> mShifts = new ArrayList<>();

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
            startActivity(new Intent(this, Login.class));
            finish();
        }
        if(mFirebaseUser.getDisplayName() == null || mFirebaseUser.getDisplayName().isEmpty() ||
                mFirebaseUser.getDisplayName().equals("")){
            setBadgeNumber();
        }
        mFirestoreDatabase = FirebaseFirestore.getInstance();
        mFirebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true).build();
        mFirestoreDatabase.setFirestoreSettings(mFirebaseFirestoreSettings);
        getShiftsFromFirestore();


        BottomNavigationView buttonNav = findViewById(R.id.navigation);
        buttonNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,
                new TodaysScheduleFragment()).commit();
    }

    public void setBadgeNumber() {
            startActivity(new Intent(this, UpdateUserName.class));
            Toast.makeText(this, "Please provide us with your badge number", Toast.LENGTH_SHORT).show();
            finish();
    }

    public void getShiftsFromFirestore() {
        String badgeNumber = mFirebaseUser.getDisplayName();
        mShifts.clear();
        try {
            mFirestoreDatabase.collection(badgeNumber).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot d : task.getResult()) {
                            Shift shift = d.toObject(Shift.class);
                            mShifts.add(shift);
                        }
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public int[] getShiftForDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();

        int[] shiftData = new int[4];
        shiftData[0] = 0;
        shiftData[1] = 0;
        shiftData[2] = 0;
        shiftData[3] = 0;

        for (int index = 0; index < mShifts.size(); index++) {
            calendar.setTimeInMillis(mShifts.get(index).getBegin().getTime());
            if (calendar.get(Calendar.DAY_OF_MONTH) == day && calendar.get(Calendar.MONTH) == month
                    && calendar.get(Calendar.YEAR) == year) {
                Calendar otherTempCal = Calendar.getInstance();
                otherTempCal.setTimeInMillis(mShifts.get(index).getEnd().getTime());
                shiftData[0] = calendar.get(Calendar.HOUR_OF_DAY);
                shiftData[1] = calendar.get(Calendar.MINUTE);
                shiftData[2] = otherTempCal.get(Calendar.HOUR_OF_DAY);
                shiftData[3] = otherTempCal.get(Calendar.MINUTE);
            }
        }
        return shiftData;
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

        if (shiftLength > fourHrs) {
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

    public void logout() {
        mAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
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
            case R.id.nav_logout:
                logout();
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
}