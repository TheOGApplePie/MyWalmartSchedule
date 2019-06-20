package com.theogapplepie.mywalmartschedule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class MyShiftsFragment extends Fragment {
    @Nullable
    CalendarView mCalendarView;
    NumberFormat nFormat = new DecimalFormat("00");
    int [] mShift = new int[4];
    int startHour, startMin,endHour,endMin;
    String [] mShiftBreakdown = new String[3];
    String shiftToText,mB1, mMeal, mB3;
    MainActivity mainActivity;
    TextView mShiftSetter, mB1Setter, mMealSetter, mB3Setter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.my_shifts, container, false);
        mainActivity = (MainActivity)getActivity();
        mShiftSetter = v.findViewById(R.id.myShiftsSetter);
        mB1Setter = v.findViewById(R.id.MB1Setter);
        mMealSetter = v.findViewById(R.id.MMealSetter);
        mB3Setter = v.findViewById(R.id.MB3Setter);
        mCalendarView = v.findViewById(R.id.calendar);
        Calendar c = Calendar.getInstance();
        setShift(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                setShift(year, month, dayOfMonth);
            }
        });

        return v;
    }
    public void setShift(int year, int month, int day){
        mShift = mainActivity.getShiftForDay(year, month, day);
        startHour = mShift[0];
        startMin = mShift[1];
        endHour = mShift[2];
        endMin = mShift[3];
        if(endHour - startHour > 0){

            shiftToText = nFormat.format(startHour) + ":" + nFormat.format(startMin) + " - " +
                    nFormat.format(endHour) + ":" + nFormat.format(endMin);
        }else{
            shiftToText = "No Shift Assigned";
        }
        mShiftBreakdown = mainActivity.getBreaks(startHour, startMin, endHour, endMin);
        mB1 = mShiftBreakdown[0];
        mMeal = mShiftBreakdown[1];
        mB3 = mShiftBreakdown[2];
        mShiftSetter.setText(shiftToText);
        mB1Setter.setText(mB1);
        mMealSetter.setText(mMeal);
        mB3Setter.setText(mB3);
    }
}
