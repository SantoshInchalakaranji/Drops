package com.prplmnstr.drops.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class PlantReport extends BaseObservable {
    private String plantName, pressure;
    private  int flow,tds, meterOpen, meterClose, usage, day,month,year;

    public PlantReport() {
    }

    public PlantReport(String plantName, String pressure, int flow, int tds, int meterOpen, int meterClose, int usage, int day, int month, int year) {
        this.plantName = plantName;
        this.pressure = pressure;
        this.flow = flow;
        this.tds = tds;
        this.meterOpen = meterOpen;
        this.meterClose = meterClose;
        this.usage = usage;
        this.day = day;
        this.month = month;
        this.year = year;
    }
    @Bindable
    public int getMeterOpen() {
        return meterOpen;
    }
    @Bindable
    public int getMeterClose() {
        return meterClose;
    }
    @Bindable
    public int getUsage() {
        return usage;
    }

    @Bindable
    public String getPlantName() {
        return plantName;
    }
    @Bindable
    public String getPressure() {
        return pressure;
    }
    @Bindable
    public int getFlow() {
        return flow;
    }
    @Bindable
    public int getTds() {
        return tds;
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

    public void setPressure(String pressure) {
        this.pressure = pressure;
        notifyPropertyChanged(BR.pressure);
    }

    public void setFlow(int flow) {
        this.flow = flow;
        notifyPropertyChanged(BR.flow);
    }

    public void setMeterOpen(int meterOpen) {
        this.meterOpen = meterOpen;
        notifyPropertyChanged(BR.meterOpen);
    }

    public void setMeterClose(int meterClose) {
        this.meterClose = meterClose;
        notifyPropertyChanged(BR.meterClose);
    }

    public void setUsage(int usage) {
        this.usage = usage;
        notifyPropertyChanged(BR.usage);
    }

    public void setTds(int tds) {
        this.tds = tds;
        notifyPropertyChanged(BR.tds);
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
