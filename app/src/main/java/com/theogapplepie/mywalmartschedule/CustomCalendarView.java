package com.theogapplepie.mywalmartschedule;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomCalendarView extends LinearLayout {

    ImageView NextButton, PreviousButton;
    TextView currentDateText;
    GridView gridView;
    public static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    MainActivity mainActivity;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    MyShiftsFragment myShiftsFragment;

    CustomCalendarViewAdapter ccAdapter;
    ArrayList<Date> dates = new ArrayList<>();

    public CustomCalendarView(Context context){
        super(context);
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeLayout();
        mainActivity = (MainActivity) getContext();
        myShiftsFragment = new MyShiftsFragment();

        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                setupCalendar();
            }
        });

        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                setupCalendar();
            }
        });
    }
    private void initializeLayout(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_custom_calendar_view, this);
        NextButton = view.findViewById(R.id.calendar_next_button);
        PreviousButton = view.findViewById(R.id.calendar_prev_button);
        currentDateText = view.findViewById(R.id.calendar_date_display);
        gridView = view.findViewById(R.id.calendar_grid);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void setupCalendar(){
        String currentDate = dateFormat.format(calendar.getTime());
        currentDateText.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        while (dates.size() <  MAX_CALENDAR_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        ccAdapter = new CustomCalendarViewAdapter(context, dates, calendar);
        gridView.setAdapter(ccAdapter);

    }

    public Date getDate(int position){
        return ccAdapter.getItem(position);
    }
}