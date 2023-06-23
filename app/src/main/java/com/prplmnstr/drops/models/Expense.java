package com.prplmnstr.drops.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.prplmnstr.drops.BR;

public class Expense extends BaseObservable {
    private String plantName, title;
    private int amount,day,month,year;

    public Expense() {
    }

    public Expense(String plantName, String title, int amount, int day, int month, int year) {
        this.plantName = plantName;
        this.title = title;
        this.amount = amount;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Bindable
    public String getTitle() {
        return title;
    }
@Bindable
    public int getAmount() {
        return amount;
    }
    @Bindable
    public String getPlantName() {
        return plantName;
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

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    public void setAmount(int amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
        notifyPropertyChanged(BR.plantName);
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
}
