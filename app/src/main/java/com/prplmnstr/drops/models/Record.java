package com.prplmnstr.drops.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.prplmnstr.drops.BR;

public class Record extends BaseObservable {
    private String type,plantName,unitName;
    private int opening, closing, amount, waterOpen, waterClose, waterSupply, day,month,year;

    public Record() {
    }

    public Record(String type, String plantName, String unitName, int opening, int closing, int amount, int waterOpen, int waterClose, int waterSupply, int day, int month, int year) {
        this.type = type;
        this.plantName = plantName;
        this.unitName = unitName;
        this.opening = opening;
        this.closing = closing;
        this.amount = amount;
        this.waterOpen = waterOpen;
        this.waterClose = waterClose;
        this.waterSupply = waterSupply;
        this.day = day;
        this.month = month;
        this.year = year;
    }
    @Bindable
    public String getPlantName() {
        return plantName;
    }
    @Bindable
    public String getUnitName() {
        return unitName;
    }

    @Bindable
    public String getType() {
        return type;
    }
    @Bindable
    public int getOpening() {
        return opening;
    }
    @Bindable
    public int getClosing() {
        return closing;
    }
    @Bindable
    public int getAmount() {
        return amount;
    }
    @Bindable
    public int getWaterOpen() {
        return waterOpen;
    }
    @Bindable
    public int getWaterClose() {
        return waterClose;
    }
    @Bindable
    public int getWaterSupply() {
        return waterSupply;
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


    public void setPlantName(String plantName) {
        this.plantName = plantName;
        notifyPropertyChanged(BR.plantName);
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
        notifyPropertyChanged(BR.unitName);
    }

    public void setType(String type) {
        this.type = type;
        notifyPropertyChanged(BR.type);
    }

    public void setOpening(int opening) {
        this.opening = opening;
        notifyPropertyChanged(BR.opening);
    }

    public void setClosing(int closing) {
        this.closing = closing;
        notifyPropertyChanged(BR.closing);
    }

    public void setAmount(int amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }



    public void setWaterOpen(int waterOpen) {
        this.waterOpen = waterOpen;
        notifyPropertyChanged(BR.waterOpen);
    }

    public void setWaterClose(int waterClose) {
        this.waterClose = waterClose;
        notifyPropertyChanged(BR.waterClose);
    }

    public void setWaterSupply(int waterSupply) {
        this.waterSupply = waterSupply;
        notifyPropertyChanged(BR.waterSupply);
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
