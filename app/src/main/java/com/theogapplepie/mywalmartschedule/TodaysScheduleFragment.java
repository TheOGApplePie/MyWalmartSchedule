package com.theogapplepie.mywalmartschedule;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;

public class TodaysScheduleFragment extends Fragment {

    private Context context;
    NumberFormat nFormat = new DecimalFormat("00");
    int startHour, startMin,endHour,endMin;
    TextView tShiftSetter, tB1Setter, tMealSetter, tB3Setter,tShiftTitle, tB1Title, tMealTitle,
            tB3Title, tBreakEstimate, tShiftColour1, tShiftColour2;
    int [] tShift = new int[4];
    CustomDayView ccdv;
    String [] tShiftBreakdown = new String[3];
    String  shiftToText, tB1, tMeal, tB3;
    MainActivity mainActivity;
    Calendar today = Calendar.getInstance();

    public TodaysScheduleFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.todays_shifts, container, false);
        mainActivity = (MainActivity)getActivity();
        tShiftSetter = view.findViewById(R.id.tShiftSetter);
        tB1Setter = view.findViewById(R.id.tB1Setter);
        tMealSetter = view.findViewById(R.id.tMealSetter);
        tB3Setter = view.findViewById(R.id.tB3Setter);
        tShiftTitle = view.findViewById(R.id.tShiftTitle);
        tB1Title = view.findViewById(R.id.tB1Title);
        tMealTitle = view.findViewById(R.id.tMealTitle);
        tB3Title = view.findViewById(R.id.tB3Title);
        tBreakEstimate = view.findViewById(R.id.tBreakEstimate);
        tShiftColour1 = view.findViewById(R.id.YourShiftDescription);
        tShiftColour2 = view.findViewById(R.id.YourColleaguesShiftDescription);
        ccdv = view.findViewById(R.id.ccdv);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mainActivity.mTodaysShifts.size()>0){
            mainActivity.zeroShifts.setVisibility(View.GONE);
            Collections.sort(mainActivity.mTodaysShifts);
            setShift(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        }else{
            tShiftColour1.setVisibility(View.GONE);
            tShiftColour2.setVisibility(View.GONE);
            ccdv.setVisibility(View.GONE);
            tShiftSetter.setVisibility(View.GONE);
            tB1Setter.setVisibility(View.GONE);
            tMealSetter.setVisibility(View.GONE);
            tB3Setter.setVisibility(View.GONE);
            tShiftTitle.setVisibility(View.GONE);
            tB1Title.setVisibility(View.GONE);
            tMealTitle.setVisibility(View.GONE);
            tB3Title.setVisibility(View.GONE);
            tBreakEstimate.setVisibility(View.GONE);
            mainActivity.zeroShifts.setVisibility(View.VISIBLE);
        }

    }

    public void setShift(int year, int month, int day){
        tShift = mainActivity.getShiftForDay(year, month, day);
        startHour = tShift[0];
        startMin = tShift[1];
        endHour = tShift[2];
        endMin = tShift[3];
        if (endHour - startHour > 0){
            shiftToText = nFormat.format(startHour) + ":" + nFormat.format(startMin) + " - " +
                    nFormat.format(endHour) +":"+ nFormat.format(endMin);
        } else {
            shiftToText = "No Shift Assigned";
        }
        tShiftBreakdown = mainActivity.getBreaks(startHour, startMin, endHour, endMin);
        tB1 = tShiftBreakdown[0];
        tMeal = tShiftBreakdown[1];
        tB3 = tShiftBreakdown[2];
        tShiftSetter.setText(shiftToText);
        tB1Setter.setText(tB1);
        tMealSetter.setText(tMeal);
        tB3Setter.setText(tB3);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
