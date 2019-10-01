package com.theogapplepie.mywalmartschedule;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomCalendarViewAdapter extends ArrayAdapter {
    ArrayList<Date> dates;
    List<Shift> mShifts;

    Calendar currentDate;
    LayoutInflater inflator;




    public CustomCalendarViewAdapter(@NonNull Context context, ArrayList<Date> dates, Calendar currentDate){
        super(context, R.layout.single_cell_layout);
        this.dates = dates;
        this.currentDate = currentDate;
        inflator = LayoutInflater.from(context);
        MainActivity mainActivity= (MainActivity)context;
        mShifts = mainActivity.mShifts;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = inflator.inflate(R.layout.single_cell_layout, parent, false);
        }
        ImageView redDot = convertView.findViewById(R.id.redDotForShift);
        redDot.setVisibility(View.GONE);

        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int dayNumber = dateCalendar.get(Calendar.DAY_OF_MONTH);

        // Date to display on calendar
        int displayMonth = dateCalendar.get(Calendar.MONTH);
        int displayYear = dateCalendar.get(Calendar.YEAR);

        // Current actual date
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentYear = currentDate.get(Calendar.YEAR);


        // Hide cells of days not part of current month
       if (displayMonth == currentMonth && displayYear == currentYear){
            convertView.setVisibility(View.VISIBLE);
        }
        else{
            convertView.setVisibility(View.GONE);
        }

        // Setting the day number in the textview
        TextView dayNumberText = convertView.findViewById(R.id.calendar_day_number);
        dayNumberText.setText(String.valueOf(dayNumber));

        // Traversing through the array of shifts and seeing if there is a shift for current day in calendar.
        // If there is, set the red dot of the day to be visible
        for (int i = 0; i < mShifts.size(); i++) {
            Calendar startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.setTimeInMillis(mShifts.get(i).getBegin().getTime());
            if (startTimeCalendar.get(Calendar.DAY_OF_MONTH) == dayNumber &&
                    startTimeCalendar.get(Calendar.MONTH) == displayMonth &&
                    startTimeCalendar.get(Calendar.YEAR) == displayYear) {
                        redDot.setVisibility(View.VISIBLE);
            }


        }
        return convertView;
    }

    @Override
    public int getCount() {
        return dates.size();
    }


    @Nullable
    @Override
    public Date getItem(int position) {
        return dates.get(position);
    }




}
