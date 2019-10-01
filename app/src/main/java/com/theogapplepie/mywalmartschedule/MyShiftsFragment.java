package com.theogapplepie.mywalmartschedule;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

public class MyShiftsFragment extends Fragment {
    @Nullable
    CustomCalendarView mCustomCalendarView;
    NumberFormat nFormat = new DecimalFormat("00");
    int [] mShift = new int[4];
    int startHour, startMin,endHour,endMin;
    String [] mShiftBreakdown = new String[3];
    String shiftToText,mB1, mMeal, mB3;
    MainActivity mainActivity;
    TextView mShiftSetter, mB1Setter, mMealSetter, mB3Setter;
    int previousViewPosition = -1;

    public MyShiftsFragment(){
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.my_shifts, container, false);
        mShiftSetter = v.findViewById(R.id.myShiftsSetter);
        mB1Setter = v.findViewById(R.id.MB1Setter);
        mMealSetter = v.findViewById(R.id.MMealSetter);
        mB3Setter = v.findViewById(R.id.MB3Setter);
        mCustomCalendarView = v.findViewById(R.id.customCalendar);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity)getActivity();
        mCustomCalendarView.setupCalendar();
        mCustomCalendarView.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Calendar c = Calendar.getInstance();
                Date date = mCustomCalendarView.getDate(position);
                view.setBackgroundResource(R.drawable.circle);
                View previousView = mCustomCalendarView.gridView.getChildAt(previousViewPosition);
                if(previousViewPosition!= -1){
                    previousView.setSelected(false);
                    previousView.setBackgroundColor(Color.alpha(0));
                }
                previousViewPosition = position;
                c.setTime(date);
                setShift(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            }
        });
    }

    public void setShift(int year , int month, int day){
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
