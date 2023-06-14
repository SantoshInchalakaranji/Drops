package com.prplmnstr.drops.repository.admin;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.models.Unit;
import com.prplmnstr.drops.models.User;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.grpc.LoadBalancer;

public class DashboardFBRepository {
    private OnFirebaseRespond onFirebaseRespond;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private List<Record> records = new ArrayList<>();
    private List<User> workers = new ArrayList<>();
    List<Attendance> attendances = new ArrayList<>();


    public DashboardFBRepository(OnFirebaseRespond onFirebaseRespond){
        this.onFirebaseRespond = onFirebaseRespond;
    }


    public void getAttendanceRecord(String plantName){

        Date today = Helper.getTodayDateObject();
        firebaseFirestore.collection(Constants.WORKER)
                .whereEqualTo(Constants.PLANT_NAME,plantName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            workers = task.getResult().toObjects(User.class);
                            getAttendance(workers,plantName);
                        }
                    }
                });
    }

    public void saveAttendance(Attendance attendance){
        Date today = Helper.getTodayDateObject();

        firebaseFirestore.collection(Constants.ATTENDANCE)
                .document(attendance.getPlantName()+"_"+attendance.getUserName()+"_"+today.getDateInStringFormat())
                .set(attendance).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            onFirebaseRespond.onAttendanceChanged(true);
                        }
                        else{
                            onFirebaseRespond.onAttendanceChanged(false);
                        }
                    }
                });
    }

    public void deleteWorker(Attendance attendance){
        Date today = Helper.getTodayDateObject();

        firebaseFirestore.collection(Constants.ATTENDANCE)
                .document(attendance.getPlantName()+"_"+attendance.getUserName()+"_"+today.getDateInStringFormat())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            onFirebaseRespond.onWorkerDelete(true);
                        }
                        else{
                            onFirebaseRespond.onWorkerDelete(false);
                        }
                    }
                });



    }

    public void getAllAttendanceOfUser(Attendance attendance){

        firebaseFirestore.collection(Constants.ATTENDANCE)
                .whereEqualTo(Constants.PLANT_NAME,attendance.getPlantName())
                .whereEqualTo("userName", attendance.getUserName())
                .orderBy("year", Query.Direction.DESCENDING)
                .orderBy("month",Query.Direction.DESCENDING)
                .orderBy("day",Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                             attendances = task.getResult().toObjects(Attendance.class);
                             onFirebaseRespond.onGettingAttendanceOfUser(attendances);
                        }else{
                            onFirebaseRespond.onGettingAttendanceOfUser(null);
                        }
                    }
                });

    }
    private void getAttendance(List<User> workers,String plantName) {
        Date today = Helper.getTodayDateObject();
        List<Attendance> attendances = new ArrayList<>();
        for(User worker:workers){
            firebaseFirestore.collection(Constants.ATTENDANCE)
                    .whereEqualTo(Constants.PLANT_NAME,plantName)
                    .whereEqualTo("userName",worker.getUserName())
                    .orderBy("year", Query.Direction.DESCENDING)
                    .orderBy("month",Query.Direction.DESCENDING)
                    .orderBy("day",Query.Direction.DESCENDING)

                    .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                               Attendance attendance  = task.getResult().toObjects(Attendance.class).get(0);
                                String lastAtteded = Helper.getDateInStringFormat(attendance.getDay(),attendance.getMonth(), attendance.getYear());
                                if(!lastAtteded.equals(today.getDateInStringFormat())){
                                    attendance.setDay(today.getDay());
                                    attendance.setMonth(today.getMonth());
                                    attendance.setAttendance(Constants.NO_ATTENDANCE);
                                    attendance.setYear(today.getYear());

                                }
                                attendances.add(attendance);
                                if(attendances.size()== workers.size()){
                                    onFirebaseRespond.onGettingAttendaceList(attendances);
                                }
                            }
                        }
                    });
        }


    }

    public void addNewUnit(String plantName,String unitName,String type, int opening,int waterOpening){

       Map<String, Object> map = new HashMap<>();
       map.put(Constants.UNIT_NAME,unitName);
       map.put(Constants.UNIT_TYPE_STRING,type);
        firebaseFirestore.collection(plantName)
                .document(unitName)

                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            addEmptyRecord(plantName, unitName, type, opening, waterOpening);
                        }else{
                            onFirebaseRespond.onUnitAdded(false);
                        }
                    }
                });


    }
    public void getNoOfUnits(String plantName) {

        firebaseFirestore.collection(plantName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                          QuerySnapshot snapshot= task.getResult();
                          onFirebaseRespond.onGettingTodayRecord(records,snapshot.size());

                            }else{

                                onFirebaseRespond.onGettingTodayRecord(null,0);
                            }
                        }

                });

    }

    public void NoOfUnits(String plantName) {

        firebaseFirestore.collection(plantName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot= task.getResult();
                           onFirebaseRespond.noOfUnitsLoaded(snapshot.size());

                        }else{

                            onFirebaseRespond.noOfUnitsLoaded(0);
                        }
                    }

                });

    }



    public void getTodayRecord(String plantName){

           Date today = Helper.getTodayDateObject();
            firebaseFirestore.collection(Constants.RECORDS)
                   .whereEqualTo(Constants.PLANT_NAME,plantName)
                   .whereEqualTo("day", today.getDay())
                    .whereEqualTo("month", today.getMonth())
                    .whereEqualTo("year", today.getYear())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    Log.i("TAG","dfsdfssf"+ String.valueOf(task.getResult().size()));
                                    records  = task.getResult().toObjects(Record.class);

                                    getNoOfUnits(plantName);

                                }
                        }
                    });

    }

    private void addEmptyRecord(String plantName,String unitName,String type, int opening,int waterOpening) {
        Date date =  Helper.getTodayDateObject();
        Record record = new Record(type,plantName,unitName,opening,opening,0,waterOpening,waterOpening,0, date.getDay(), date.getMonth(), date.getYear());

        String recordDocumentName = plantName+"_"+unitName+"_"+date.getDateInStringFormat();
        firebaseFirestore.collection(Constants.RECORDS)
                .document(recordDocumentName)
                .set(record).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                onFirebaseRespond.onUnitAdded(true);
                            }else{
                                onFirebaseRespond.onUnitAdded(false);
                            }
                    }
                });
    }

    public interface OnFirebaseRespond{

        void onUnitAdded(Boolean result);
        void onAttendanceChanged(Boolean result);

        void onWorkerDelete(Boolean result);

      void noOfUnitsLoaded(int noOfUnits);

        void onGettingTodayRecord(List<Record> records,int noOfUnits);
        void onGettingAttendaceList(List<Attendance> attendances);
        void onGettingAttendanceOfUser(List<Attendance> attendances);
    }
}