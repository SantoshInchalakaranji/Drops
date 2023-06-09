package com.prplmnstr.drops.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.prplmnstr.drops.BR;

public class Plant extends BaseObservable {
    String plantName;
    String image;

    public Plant(String plantName, String image) {
        this.plantName = plantName;
        this.image = image;
    }

    public Plant() {
    }
    @Bindable
    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
        notifyPropertyChanged(BR.plantName);
    }
    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }
}
