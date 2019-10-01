package com.theogapplepie.mywalmartschedule;


import java.util.Date;

public class Shift implements Comparable <Shift>{
    private Date begin, end;
    private String employee;

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

    public void setEmployee(String emp){employee = emp;}

    public String getEmployee() {
        return employee;
    }

    @Override
    public int compareTo(Shift shiftToCompare) {

        long compareShift = shiftToCompare.getBegin().getTime();
        // For Ascending order
        long compareValue = this.begin.getTime() - compareShift;
        return (int)(compareValue);
    }
}
