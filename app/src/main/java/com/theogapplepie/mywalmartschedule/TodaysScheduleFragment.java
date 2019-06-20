package com.theogapplepie.mywalmartschedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.framgia.library.calendardayview.CalendarDayView;
import com.framgia.library.calendardayview.data.IEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TodaysScheduleFragment extends Fragment {

    //Gets the context for this fragment
    private Context context;


    //Creating an instance of CalendarDayView object
    CalendarDayView dayView;

    //Defining a custom Array object to store class of type Event in array
    ArrayList<IEvent> shiftBar;
    NumberFormat nFormat = new DecimalFormat("00");
    int startHour, startMin,endHour,endMin;
    TextView tShiftSetter, tB1Setter, tMealSetter, tB3Setter;
    int [] tShift = new int[4];
    String [] tShiftBreakdown = new String[3];
    String  shiftToText, tB1, tMeal, tB3;
    MainActivity mainActivity;
    Calendar today = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.todays_shifts, container, false);
        mainActivity = (MainActivity)getActivity();
        dayView = v.findViewById(R.id.calendar_daily_view);
        dayView.setLimitTime(0, 23);
        shiftBar = new ArrayList<>();
        tShiftSetter = v.findViewById(R.id.tShiftSetter);
        tB1Setter = v.findViewById(R.id.tB1Setter);
        tMealSetter = v.findViewById(R.id.tMealSetter);
        tB3Setter = v.findViewById(R.id.tB3Setter);
        setShift(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        int eventColour = ContextCompat.getColor(context, R.color.colorAccent);
        Calendar timeStart = Calendar.getInstance();
        timeStart.set(Calendar.HOUR_OF_DAY, startHour);
        timeStart.set(Calendar.MINUTE, startMin);
        Calendar timeEnd = (Calendar) timeStart.clone();
        timeEnd.set(Calendar.HOUR_OF_DAY, endHour);
        timeEnd.set(Calendar.MINUTE, endMin);
        Event event = new Event(1, timeStart, timeEnd, "Shift", "Danial", eventColour);

        shiftBar.add(event);
        dayView.setEvents(shiftBar);
        return v;
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
