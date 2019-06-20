package com.theogapplepie.mywalmartschedule;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class CustomCalendarViewAdapter extends ArrayAdapter<Date>{

    // days with events
    private HashSet<Date> eventDays;

    // for view inflation
    private LayoutInflater inflater;

    public CustomCalendarViewAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
    {
        super(context, R.layout.activity_custom_calendar_view, days);
        this.eventDays = eventDays;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        // day in question
        Date date = getItem(position);

        // today
        Date today = new Date();

        // inflate item if it does not exist yet
        if (view == null)
            view = inflater.inflate(R.layout.activity_custom_calendar_view, parent, false);

        // if this day has an event, specify event image
        view.setBackgroundResource(eventDays.contains(date)) ? R.drawable.reminder : 0);

        // clear styling
        view.setTypeface(null, Typeface.NORMAL);
        view.setTextColor(Color.BLACK);
        if (date.getMonth() != today.getMonth() ||
                date.getYear() != today.getYear())
        {
            // if this day is outside current month, grey it out
            view.setTextColor(getResources().getColor(R.color.greyed_out));
        }
        else if (date.getDate() == today.getDate())
        {
            // if it is today, set it to blue/bold
            view.setTypeface(null, Typeface.BOLD);
            view.setTextColor(getResources().getColor(R.color.today));
        }

        // set text
        view.setText(String.valueOf(date.getDate()));

        return view;
    }
}