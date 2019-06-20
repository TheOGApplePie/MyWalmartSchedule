package com.theogapplepie.mywalmartschedule;


import java.util.Date;

public class Shift {
    private Date begin, end;

    public Shift(){}

    public Shift(Date begin, Date end) {

    this.begin = begin;
    this.end = end;
}

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }
}
