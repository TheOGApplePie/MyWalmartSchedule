package com.theogapplepie.mywalmartschedule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CustomDayView extends View {
    Paint myShiftBarColour, myColleaguesShiftBarColour, mGridPaint, mGuidlinePaint;
    String mbadgeNumber;
    MainActivity mainActivity;
    ArrayList<Shift> mTodaysShifts;
    ScaleGestureDetector mScaleDetector;
    float mScaleFactor = 1.f;
    NumberFormat numberFormat = new DecimalFormat("00");
    int mPadding = 5;

    public CustomDayView(Context context) {
        super(context);

    }

    public CustomDayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        myShiftBarColour = new Paint();
        myShiftBarColour.setStyle(Paint.Style.FILL_AND_STROKE);
        myShiftBarColour.setColor(getResources().getColor(R.color.colorAccent));

        myColleaguesShiftBarColour = new Paint();
        myColleaguesShiftBarColour.setStyle(Paint.Style.FILL_AND_STROKE);
        myColleaguesShiftBarColour.setColor(getResources().getColor(R.color.colorPrimary));

        mGridPaint = new Paint();
        mGridPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mGridPaint.setColor(Color.BLACK);
        mGridPaint.setStrokeWidth(2);
        mGridPaint.setTextSize(35);

        mGuidlinePaint = new Paint();
        mGuidlinePaint.setStyle(Paint.Style.STROKE);
        mGuidlinePaint.setColor(Color.LTGRAY);
        mGuidlinePaint.setStrokeWidth(1);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());


    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(4800, mTodaysShifts.size() * 250 + 50);
    }

    public CustomDayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(@Nullable AttributeSet attributeSet) {
        mainActivity = (MainActivity) getContext();
        mTodaysShifts = mainActivity.getTodaysShifts();
        mbadgeNumber = mainActivity.getCurrentUser();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        mTodaysShifts = mainActivity.getTodaysShifts();

        final int height = getHeight();
        final int width = getWidth();
        final float gridLeft = mPadding;
        final float gridRight = width - mPadding;
        final float gridTop = mPadding;
        final float gridBottom = height - mPadding;

        /* Drawing the grid lines */
        //The Y-Axis
        canvas.drawLine(gridLeft, gridBottom, gridLeft, gridTop, mGridPaint);
        //The X-Axis
        canvas.drawLine(gridLeft, gridBottom, gridRight, gridBottom, mGridPaint);

        //Drawing the quarter-hour and hour guide lines
        float spacing = 50;
        float scale = 4;
        float x;
        for (int i = 0; i < 96; i += 4) {
            x = gridLeft + i * spacing;
            canvas.drawLine(x, gridBottom - 30, x, gridTop, mGridPaint);
            if (i != 0) {
                canvas.drawText(i / 4 + ":00", x - 5 * mPadding, gridBottom, mGridPaint);
            }
        }
        for (float i = 0; i < 96; i++) {
            x = gridLeft + i * spacing;
            canvas.drawLine(x, gridBottom - 30, x, gridTop, mGuidlinePaint);
        }
        // Drawing the rectangles
        for (int i = 0; i < mTodaysShifts.size(); i++) {
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(mTodaysShifts.get(i).getBegin().getTime());
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(mTodaysShifts.get(i).getEnd().getTime());
            int startMinutes;
            int endMinutes;

            if (start.get(Calendar.MINUTE) == 15) {
                startMinutes = 1;
            } else if (start.get(Calendar.MINUTE) == 30) {
                startMinutes = 2;
            } else if (start.get(Calendar.MINUTE) == 45) {
                startMinutes = 3;
            } else {
                startMinutes = 0;
            }

            if (end.get(Calendar.MINUTE) == 15) {
                endMinutes = 1;
            } else if (end.get(Calendar.MINUTE) == 30) {
                endMinutes = 2;
            } else if (end.get(Calendar.MINUTE) == 45) {
                endMinutes = 3;
            } else {
                endMinutes = 0;
            }

            // Drawing the horizontal block depending on if it's the user's shift or their colleague's
            if (mTodaysShifts.get(i).getEmployee().equals(mbadgeNumber)) {
                canvas.drawRect(start.get(Calendar.HOUR_OF_DAY) * scale * spacing + mPadding +
                                startMinutes * spacing, i * 251, end.get(Calendar.HOUR_OF_DAY)
                                * scale * spacing + mPadding + endMinutes * spacing, i * 251 + 250,
                        myShiftBarColour);
            } else {
                canvas.drawRect(start.get(Calendar.HOUR_OF_DAY) * scale * spacing + mPadding +
                                startMinutes * spacing, i * 251, end.get(Calendar.HOUR_OF_DAY)
                                * scale * spacing + mPadding + endMinutes * spacing, i * 251 + 250,
                        myColleaguesShiftBarColour);
            }
            // Writing the shift on the horizontal block
            canvas.drawText(mTodaysShifts.get(i).getEmployee().substring(0,mTodaysShifts
                    .get(i).getEmployee().length()-1) + ": "+ numberFormat.format(start
                            .get(Calendar.HOUR_OF_DAY)) + ":" + numberFormat.format(start
                            .get(Calendar.MINUTE)) + " - " + numberFormat.format(end
                            .get(Calendar.HOUR_OF_DAY)) + ":" + numberFormat.format(end
                            .get(Calendar.MINUTE)), (start.get(Calendar.HOUR_OF_DAY) + end
                            .get(Calendar.HOUR_OF_DAY)) * 0.5f * spacing * scale, i * 250 + 125, mGridPaint);
        }
        canvas.restore();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }
}