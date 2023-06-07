package com.prplmnstr.drops.models;



import android.content.res.TypedArray;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.prplmnstr.drops.R;


public class RecyclerModel extends BaseObservable {
   private String outletName;
   private String outletCollection;
    private String date;
   private int imageIndex;

    public RecyclerModel(String outletName, String outletCollection, String date, int imageIndex) {
        this.outletName = outletName;
        this.outletCollection = outletCollection;
        this.date = date;
        this.imageIndex = imageIndex;
    }

    public RecyclerModel() {

    }
    @Bindable
    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
        notifyPropertyChanged(BR.outletName);
    }
    @Bindable
    public String getOutletCollection() {
        return outletCollection;

    }

    public void setOutletCollection(String outletCollection) {
        this.outletCollection = outletCollection;
        notifyPropertyChanged(BR.outletCollection);
    }
    @Bindable
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        notifyPropertyChanged(BR.date);
    }
    @Bindable
    public int getImageIndex() {

        return  imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
        notifyPropertyChanged(BR.imageIndex);
    }
}
