package com.prplmnstr.drops.viewModel;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.repository.admin.HomeFragmentRepository;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class HomeFragmentViewModel extends ViewModel implements HomeFragmentRepository.OnFirebaseRespond {
    private HomeFragmentRepository repository;
    private MutableLiveData<List<Plant>> plants ;
    public HomeFragmentViewModel() {
       this.plants = new MutableLiveData<>();
        this.repository = new HomeFragmentRepository(this);
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
    public void onPlantsLoadingFailure(Exception e) {
            System.out.println(e);
    }
}
