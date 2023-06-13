package com.prplmnstr.drops.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class Unit extends BaseObservable {
    private String unitName, unitType;

    public Unit(String unitName, String unitType) {
        this.unitName = unitName;
        this.unitType = unitType;
    }

    public Unit() {
    }

    @Bindable
    public String getUnitName() {
        return unitName;
    }

    @Bindable
    public String getUnitType() {
        return unitType;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
        notifyPropertyChanged(BR.unitName);
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
        notifyPropertyChanged(BR.unitType);
    }
}
