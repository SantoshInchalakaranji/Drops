package com.prplmnstr.drops.viewModel;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.repository.admin.HomeFragmentRepository;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class HomeFragmentViewModel extends ViewModel implements HomeFragmentRepository.OnFirebaseRespond {
    private HomeFragmentRepository repository;
    private MutableLiveData<List<Plant>> plants ;
    private MutableLiveData<Boolean> addingPlantResult;
    private MutableLiveData<Boolean> updatingPlantResult;

    public HomeFragmentViewModel() {
       this.plants = new MutableLiveData<>();
       this.addingPlantResult = new MutableLiveData<>();
       this.updatingPlantResult = new MutableLiveData<>();
        this.repository = new HomeFragmentRepository(this);
    }

    public MutableLiveData<Boolean> addNewPlant(Plant plant){
         repository.addNewPlant(plant);

        return addingPlantResult;
    }
    public MutableLiveData<Boolean> updatePlant(Plant plant){
         repository.updatePlant(plant);

        return updatingPlantResult;
    }

    public MutableLiveData<List<Plant>> getPlants(){
        repository.getPlants();
        return plants;
    }

    public String convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onPlantsLoadingSuccess(List<Plant> plants) {
        this.plants.setValue(plants);
    }

    @Override
    public void onPlantAdded(Boolean result) {
        this.addingPlantResult.setValue(result);
    }

    @Override
    public void onPlantUpdated(Boolean result) {
        this.updatingPlantResult.setValue(result);
    }

    @Override
    public void onPlantsLoadingFailure(Exception e) {
            System.out.println(e);
    }
}
