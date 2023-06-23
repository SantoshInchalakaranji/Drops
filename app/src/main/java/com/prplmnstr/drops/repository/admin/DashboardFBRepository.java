package com.prplmnstr.drops.repository.admin;

import android.app.Application;
import android.content.Context;
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
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Expense;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.PlantReport;
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
    List<Unit> units  = new ArrayList<>();
    List<Record> montlyRecords  = new ArrayList<>();
    List<Expense> monthlyExpense  = new ArrayList<>();
    PlantReport plantReport = new PlantReport();




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


    public void saveAttendance(Attendance attendance, Context context){
        Date today = Helper.getTodayDateObject();

        firebaseFirestore.collection(Constants.ATTENDANCE)
                .document(attendance.getPlantName()+"_"+attendance.getUserName()+"_"+today.getDateInStringFormat())
                .set(attendance).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Attendance registered.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Attendance registration failed", Toast.LENGTH_SHORT).show();
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

//    public void getMonthlyData(String plantName){
//
//
//            firebaseFirestore.collection(plantName)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                QuerySnapshot snapshot = task.getResult();
//                                if (!snapshot.isEmpty()) {
//                                    units = snapshot.toObjects(Unit.class);
//                                 //   Log.i("TAG", "date-----------------------"+date.getDay()+"-"+date.getMonth());
//                                   getUnitsMonthlyData (units,plantName);
//
//                                }else{
//                                    onFirebaseRespond.onGettingMontlyData(null);
//                                }
//                            }
//                        }
//                    });
//
//
//    }








    public  void savePlantReport(PlantReport plantReport,Context context){
        Date today = Helper.getTodayDateObject();
        firebaseFirestore.collection(Constants.PLANT_REPORTS)
                .document(plantReport.getPlantName()+"_"+today.getDateInStringFormat())
                .set(plantReport).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Plant report Added", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Adding failed please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getPlantReport(String plantName){
        Date today = Helper.getTodayDateObject();
        firebaseFirestore.collection(Constants.PLANT_REPORTS)
                .whereEqualTo(Constants.PLANT_NAME,plantName)
                .orderBy("year", Query.Direction.DESCENDING)
                .orderBy("month",Query.Direction.DESCENDING)
                .orderBy("day",Query.Direction.DESCENDING)

                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                Log.i("TAG", "onComplete: result not empty");
                                plantReport = task.getResult().toObjects(PlantReport.class).get(0);
                                onFirebaseRespond.onPlantReportLoaded(plantReport);
                            }


                        }else{
                            onFirebaseRespond.onPlantReportLoaded(null);
                        }
                    }
                });
    }




    public void getMonthlyData( String plantName) {


        Date today = Helper.getTodayDateObject();




            firebaseFirestore.collection(Constants.RECORDS)
                    .whereEqualTo(Constants.PLANT_NAME,plantName)
                    .whereEqualTo("year",today.getYear())
                    .whereEqualTo("month",today.getMonth())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){

                        QuerySnapshot snapshot = task.getResult();
                        if(snapshot.isEmpty()){
                            Record record = new Record();
                            record.setUnitName("");
                            record.setMonth(today.getMonth());
                            record.setYear(today.getYear());
                            record.setDay(today.getDay());
                            record.setPlantName(plantName);
                            record.setType("");
                            record.setWaterClose(0);
                            record.setWaterOpen(0);
                            record.setOpening(0);
                            record.setClosing(0);
                            record.setAmount(0);
                            record.setWaterSupply(0);
                            montlyRecords.add(record);
                        }else{
                            montlyRecords = task.getResult().toObjects(Record.class);
                        }
                            onFirebaseRespond.onGettingMontlyData(montlyRecords);

                    }else{

                        onFirebaseRespond.onGettingMontlyData(null);
                    }
                }
            });
        }


    public void getMonthlyExpense( String plantName) {


        Date today = Helper.getTodayDateObject();




        firebaseFirestore.collection(Constants.EXPENSES)
                .whereEqualTo(Constants.PLANT_NAME,plantName)
                .whereEqualTo("year",today.getYear())
                .whereEqualTo("month",today.getMonth())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int totalExpense=0;
                            QuerySnapshot snapshot = task.getResult();
                            if(snapshot.isEmpty()){
                               onFirebaseRespond.onGettingMontlyExpense(0);
                            }else{
                                monthlyExpense = task.getResult().toObjects(Expense.class);
                                for(Expense expense: monthlyExpense){
                                    totalExpense += expense.getAmount();
                                }
                                onFirebaseRespond.onGettingMontlyExpense(totalExpense);
                            }


                        }else{

                            onFirebaseRespond.onGettingMontlyExpense(0);
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

    public void getExpenses(String plantName){
        Date today = Helper.getTodayDateObject();
        firebaseFirestore.collection(Constants.EXPENSES)
                .whereEqualTo(Constants.PLANT_NAME,plantName )
                .whereEqualTo("year", today.getYear())
                .whereEqualTo("month", today.getMonth())
                .whereEqualTo("day",today.getDay())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                onFirebaseRespond.onExpenseLoaded(
                                        task.getResult().toObjects(Expense.class)
                                );
                            }else{
                                onFirebaseRespond.onExpenseLoaded(
                                        null
                                );
                            }

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
                                QuerySnapshot snapshot = task.getResult();
                                Attendance attendance= new Attendance();
                                if(snapshot.isEmpty()){
                                    attendance.setUserName(worker.getUserName());
                                    attendance.setPlantName(plantName);
                                    attendance.setDay(today.getDay());
                                    attendance.setMonth(today.getMonth());
                                    attendance.setAttendance(Constants.NO_ATTENDANCE);
                                    attendance.setYear(today.getYear());

                                }else{
                                    attendance  = task.getResult().toObjects(Attendance.class).get(0);
                                }

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
        void onGettingMontlyData(List<Record> monthlyRecords);
        void onPlantReportLoaded(PlantReport plantReport);

        void onExpenseLoaded(List<Expense> expenses);

        void onGettingMontlyExpense(Integer totalExpense);
    }
}