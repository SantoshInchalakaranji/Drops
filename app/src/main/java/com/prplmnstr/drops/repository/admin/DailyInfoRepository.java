package com.prplmnstr.drops.repository.admin;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.Unit;
import com.prplmnstr.drops.repository.worker.TaskFragmentRepository;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class DailyInfoRepository {
    private OnFirebaseRespond onFirebaseRespond;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    List<Unit> units  = new ArrayList<>();


    public DailyInfoRepository(OnFirebaseRespond onFirebaseRespond) {
        this.onFirebaseRespond = onFirebaseRespond;
    }





    public void loadUnits(String plantName,Date date) {

        firebaseFirestore.collection(plantName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (!snapshot.isEmpty()) {
                                units = snapshot.toObjects(Unit.class);
                                Log.i("TAG", "date-----------------------"+date.getDay()+"-"+date.getMonth());
                                getTasks(units,date,plantName);

                            }else{
                                onFirebaseRespond.onTasksLoaded(null);
                            }
                        }
                    }
                });

    }

    private void getTasks(List<Unit> units,Date date,String plantName)  {
        List<Record> records  = new ArrayList<>();
        Date today = Helper.getTodayDateObject();
        Query query;

     //   Log.i("TAG", "onComplete: "+String.valueOf(units.size()));
        for(Unit unit : units){

            if(today.getDateInStringFormat().equals(date.getDateInStringFormat())){
              //  Log.i("TAG", "today----------------: "+units.get(0).getUnitName());

                query =  firebaseFirestore.collection(Constants.RECORDS)
                        .whereEqualTo(Constants.PLANT_NAME,plantName)
                        .whereEqualTo(Constants.UNIT_NAME,unit.getUnitName())
                        .orderBy("year", Query.Direction.DESCENDING)
                        .orderBy("month",Query.Direction.DESCENDING)
                        .orderBy("day",Query.Direction.DESCENDING)

                        .limit(1);
            }else{
              //  Log.i("TAG", "yesterday----------------: "+unit.getUnitName());
                query = firebaseFirestore.collection(Constants.RECORDS)

                        .whereEqualTo(Constants.PLANT_NAME,plantName)
                        .whereEqualTo(Constants.UNIT_NAME,unit.getUnitName())
                        .whereEqualTo("year", date.getYear())
                        .whereEqualTo("month", date.getMonth())
                        .whereEqualTo("day", date.getDay())
                        .limit(1);;
            }




                   query .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                // records.clear();
                                Record record = new Record();
                               QuerySnapshot snapshot = task.getResult();
                               if(snapshot.isEmpty()){
                                   record.setUnitName(unit.getUnitName());
                                   record.setMonth(date.getMonth());
                                   record.setYear(date.getYear());
                                   record.setDay(date.getDay());
                                   record.setPlantName(plantName);
                                   record.setType(unit.getUnitType());
                                   record.setWaterClose(0);
                                   record.setWaterOpen(0);
                                   record.setOpening(0);
                                   record.setClosing(0);
                                   record.setAmount(0);
                                   record.setWaterSupply(0);
                               }else{
                                   record = task.getResult().toObjects(Record.class).get(0);
                               }


                                records.add(record);
                                Log.i("TAG", record.getDay()+unit.getUnitName()+"onComplete: "+records.size()+"==="+units.size());

                                if(records.size()== units.size()){
                                    Log.i("SIZE", "onCompletefsdfsfffsf: "+record.getUnitName());
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



//    private void updateRecord(Record record) {
//
//        Date date =  Helper.getTodayDateObject();
//
//        String recordDocumentName = record.getPlantName()+"_"+record.getUnitName()+"_"+date.getDateInStringFormat();
//        firebaseFirestore.collection(Constants.RECORDS)
//                .document(recordDocumentName)
//                .update(record).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            onFirebaseRespond.onRecordAdded(true);
//                        }else{
//                            onFirebaseRespond.onRecordAdded(false);
//                        }
//                    }
//                });
//    }


    public interface OnFirebaseRespond {


        void onTasksLoaded(List<Record> records);





    }
}