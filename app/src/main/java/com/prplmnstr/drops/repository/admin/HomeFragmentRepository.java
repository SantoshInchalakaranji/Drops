package com.prplmnstr.drops.repository.admin;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragmentRepository  {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private OnFirebaseRespond onFirebaseRespond;

    public HomeFragmentRepository(OnFirebaseRespond onFirebaseRespond) {
        this.onFirebaseRespond = onFirebaseRespond;
    }

    public void addNewPlant(Plant plant){

        firebaseFirestore.collection(Constants.PLANTS)
                .document(plant.getPlantName())
                .set(plant)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            onFirebaseRespond.onPlantAdded(true);
                        }else{
                            onFirebaseRespond.onPlantAdded(false);
                        }
                    }
                });

    }

    public void updatePlant(Plant plant){

        Map<String, Object> map = new HashMap<>();
        map.put(Constants.PLANT_IMAGE, plant.getImage());
        firebaseFirestore.collection(Constants.PLANTS)
                .document(plant.getPlantName())
                .update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("TAG", "onComplete: true");
                        if(task.isSuccessful()){
                            Log.i("TAG", "onComplete: true");
                           onFirebaseRespond.onPlantUpdated(true);
                        }else{
                            onFirebaseRespond.onPlantUpdated(false);
                        }
                    }
                });

    }

    public void getPlants(){
        firebaseFirestore.collection(Constants.PLANTS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                onFirebaseRespond.onPlantsLoadingSuccess(
                                        task.getResult().toObjects(Plant.class)
                                );
                            }else{
                                onFirebaseRespond.onPlantsLoadingSuccess(
                                        null
                                );
                            }

                        }
                        else{
                            onFirebaseRespond.onPlantsLoadingFailure(task.getException());
                            System.out.println("Not Successful");
                        }
                    }
                });
    }
    public interface OnFirebaseRespond{
        void onPlantsLoadingSuccess(List<Plant> plants);
        void onPlantAdded(Boolean result);

        void onPlantUpdated(Boolean result);
         void onPlantsLoadingFailure(Exception e);
    }
}
