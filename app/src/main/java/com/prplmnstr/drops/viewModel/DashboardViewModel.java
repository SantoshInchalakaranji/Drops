package com.prplmnstr.drops.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.repository.admin.DashboardFBRepository;

import java.util.Calendar;
import java.util.List;

public class DashboardViewModel extends ViewModel implements DashboardFBRepository.OnFirebaseRespond{



    private DashboardFBRepository repository;


    private MutableLiveData<Boolean> result;
    public DashboardViewModel(){
        repository = new DashboardFBRepository(this);
        result = new MutableLiveData<>();

    }

    public MutableLiveData<Boolean> addNewUnit(String plantName,String unitName,String type,int opening,int waterOpening){
        repository.addNewUnit(plantName, unitName, type, opening, waterOpening);

        return result;
    }






    @Override
    public void onUnitAdded(Boolean result) {
        this.result.setValue(result);
    }

    @Override
    public void onUnitUpdated(Boolean result) {

    }
}