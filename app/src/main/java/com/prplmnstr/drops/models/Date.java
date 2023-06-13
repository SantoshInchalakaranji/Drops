package com.prplmnstr.drops.models;

public class Date {
    private int day,month,year;
    private String dateInStringFormat;

    public Date() {
    }

    public Date(int day, int month, int year, String dateInStringFormat) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.dateInStringFormat = dateInStringFormat;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDateInStringFormat() {
        return dateInStringFormat;
    }

    public void setDateInStringFormat(String dateInStringFormat) {
        this.dateInStringFormat = dateInStringFormat;
    }
}
