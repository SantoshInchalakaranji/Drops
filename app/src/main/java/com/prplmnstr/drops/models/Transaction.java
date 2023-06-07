package com.prplmnstr.drops.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.prplmnstr.drops.BR;

public class Transaction extends BaseObservable {
    int day;
    int month;
    int year;
    int amount;
    int ltr;

    public Transaction() {

    }

    public Transaction(int day, int month, int year, int amount, int ltr) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.ltr = ltr;
    }
    @Bindable
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
        notifyPropertyChanged(BR.day);
    }
    @Bindable
    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        notifyPropertyChanged(BR.month);
    }
    @Bindable
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        notifyPropertyChanged(BR.year);
    }
    @Bindable
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }
    @Bindable
    public int getLtr() {
        return ltr;
    }

    public void setLtr(int ltr) {
        this.ltr = ltr;
        notifyPropertyChanged(BR.ltr);
    }
}
