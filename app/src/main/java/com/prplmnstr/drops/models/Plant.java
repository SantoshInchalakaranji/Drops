package com.prplmnstr.drops.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.prplmnstr.drops.BR;

public class Plant extends BaseObservable {
    String plantName;
    String ImageUri;

    public Plant(String plantName, String imageUri) {
        this.plantName = plantName;
        ImageUri = imageUri;
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
    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
        notifyPropertyChanged(BR.imageUri);
    }
}
