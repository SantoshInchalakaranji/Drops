package com.prplmnstr.drops.repository.worker;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.Unit;
import com.prplmnstr.drops.repository.admin.AddUserFragmentRepository;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class TaskFragmentRepository {
    private OnFirebaseRespond onFirebaseRespond;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    List<Unit> units  = new ArrayList<>();
    List<Record> records  = new ArrayList<>();

    public TaskFragmentRepository(OnFirebaseRespond onFirebaseRespond) {
        this.onFirebaseRespond = onFirebaseRespond;
    }


    public void getPlants() {
        String user = firebaseAuth.getCurrentUser().getEmail().toString();
        List<String> plants = new ArrayList<>();
        firebaseFirestore.collection(Constants.WORKER)
                .whereEqualTo("email", user)
                .get()

                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot.isEmpty()) {
                                onFirebaseRespond.onPlantsLoaded(null);
                                return;
                            }
                            for (DocumentSnapshot document : task.getResult()) {
                                // Retrieve the value of the desired field from the document
                                String fieldValue = document.get("plantName").toString();

                                // Add the field value to the list
                                plants.add(fieldValue);
                            }
                            onFirebaseRespond.onPlantsLoaded(plants);
                            loadUnits(plants.get(0));

                        } else {
                            onFirebaseRespond.onPlantsLoaded(null);
                        }
                    }
                });
    }

    public void loadUnits(String plantName) {

        firebaseFirestore.collection(plantName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                                QuerySnapshot snapshot = task.getResult();
                            if (!snapshot.isEmpty()) {
                                units = snapshot.toObjects(Unit.class);
                                getTasks(units);
                                Log.i("TAG", "onComplete: "+units.get(0).getUnitName());
                            }else{
                                onFirebaseRespond.onTasksLoaded(null);
                            }
                        }
                    }
                });

    }

    private void getTasks(List<Unit> units)  {
        Date today = Helper.getTodayDateObject();
        Log.i("TAG", "onComplete: "+String.valueOf(units.size()));
        for(Unit unit : units){
            firebaseFirestore.collection(Constants.RECORDS)
                    .whereEqualTo(Constants.UNIT_NAME,unit.getUnitName())
                    .orderBy("year", Query.Direction.DESCENDING)
                    .orderBy("month",Query.Direction.DESCENDING)
                    .orderBy("day",Query.Direction.DESCENDING)
                    .limit(1)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                Log.i("TAG", "onComplete: "+task.isSuccessful());
                                Record record = new Record();
                                record = task.getResult().toObjects(Record.class).get(0);
                                records.add(record);
                                Log.i("TAG", "onComplete: "+record.getUnitName());

                                if(records.size()== units.size()){
                                    onFirebaseRespond.onTasksLoaded(records);
                                }


                            }else{
                                Log.i("TAG", "fail: "+task.isSuccessful());
                                Log.i("TAG", "fail: "+task.getException().getMessage());

                            }
                        }
                    });
        }
    }

    public interface OnFirebaseRespond {
        void onPlantsLoaded(List<String> plants);

        void onTasksLoaded(List<Record> records);

    }
}
