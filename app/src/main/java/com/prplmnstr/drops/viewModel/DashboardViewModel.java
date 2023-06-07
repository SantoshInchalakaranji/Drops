package com.prplmnstr.drops.viewModel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.repository.DashboardFBRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardViewModel extends ViewModel implements DashboardFBRepository.onFirestoreTaskComplete{

    private MutableLiveData<List<RecyclerModel>> recyclerLiveData = new MutableLiveData<>();

    private DashboardFBRepository repository = new DashboardFBRepository(this);

    public MutableLiveData<List<RecyclerModel>> getRecyclerItems() {
        return recyclerLiveData;
    }

    public DashboardViewModel(){

        repository.getRecyclerItems();
    }
    public int getProgress(){
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        return hourOfDay;
    }

    @Override
    public void recyclerDataLoaded(List<RecyclerModel> recyclerModels) {
        recyclerLiveData.setValue(recyclerModels);
    }



    @Override
    public void onError(Exception e) {
        Log.d("ERROR", "onError: " + e.getMessage());
    }
}