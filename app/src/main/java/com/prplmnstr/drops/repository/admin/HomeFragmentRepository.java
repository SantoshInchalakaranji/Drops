package com.prplmnstr.drops.repository.admin;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.utils.Constants;

import java.util.List;

public class HomeFragmentRepository  {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private OnFirebaseRespond onFirebaseRespond;

    public HomeFragmentRepository(OnFirebaseRespond onFirebaseRespond) {
        this.onFirebaseRespond = onFirebaseRespond;
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
         void onPlantsLoadingFailure(Exception e);
    }
}
