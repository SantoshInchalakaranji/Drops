package com.prplmnstr.drops.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.prplmnstr.drops.BR;

public class User extends BaseObservable {
    String plantName;
    String userType;
    String userName;
    String email;
    String password;

    public User() {
    }

    public User(String plantName, String userType, String userName, String email, String password) {
        this.plantName = plantName;
        this.userType = userType;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
    @Bindable
    public String getPlantName() {
        return plantName;
    }
    @Bindable
    public String getUserType() {
        return userType;
    }
    @Bindable
    public String getUserName() {
        return userName;
    }
    @Bindable
    public String getEmail() {
        return email;
    }
    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
        notifyPropertyChanged(BR.plantName);
    }

    public void setUserType(String userType) {
        this.userType = userType;
        notifyPropertyChanged(BR.userType);

    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }
}
