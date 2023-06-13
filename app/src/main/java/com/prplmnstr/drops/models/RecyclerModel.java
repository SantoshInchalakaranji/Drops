package com.prplmnstr.drops.models;



import android.content.res.TypedArray;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.prplmnstr.drops.R;


public class RecyclerModel extends BaseObservable {
   private String headerName;
   private String subTitleName;
    private String date;
   private int imageIndex;

    public RecyclerModel(String headerName, String subTitleName, String date, int imageIndex) {
        this.headerName = headerName;
        this.subTitleName = subTitleName;
        this.date = date;
        this.imageIndex = imageIndex;
    }



    public RecyclerModel() {

    }
    @Bindable
    public String getHeaderName() {
        return headerName;

    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
        notifyPropertyChanged(BR.headerName);
    }
    @Bindable
    public String getSubTitleName() {
        return subTitleName;
    }

    public void setSubTitleName(String subTitleName) {
        this.subTitleName = subTitleName;
        notifyPropertyChanged(BR.subTitleName);
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
