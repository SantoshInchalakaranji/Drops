package com.prplmnstr.drops.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class Attendance extends BaseObservable {
    private String plantName,userName;
    private int day,month,year, attendance;

    public Attendance() {

    }

    public Attendance(String plantName, String userName, int day, int month, int year, int attendance) {
        this.plantName = plantName;
        this.userName = userName;
        this.day = day;
        this.month = month;
        this.year = year;
        this.attendance = attendance;
    }
    @Bindable
    public String getPlantName() {
        return plantName;
    }
    @Bindable
    public String getUserName() {
        return userName;
    }
    @Bindable
    public int getDay() {
        return day;
    }
    @Bindable
    public int getMonth() {
        return month;
    }
    @Bindable
    public int getYear() {
        return year;
    }
    @Bindable
    public int getAttendance() {
        return attendance;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
        notifyPropertyChanged(BR.plantName);
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    public void setDay(int day) {
        this.day = day;
        notifyPropertyChanged(BR.day);
    }

    public void setMonth(int month) {
        this.month = month;
        notifyPropertyChanged(BR.month);
    }

    public void setYear(int year) {
        this.year = year;
        notifyPropertyChanged(BR.year);
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
        notifyPropertyChanged(BR.attendance);
    }
}
