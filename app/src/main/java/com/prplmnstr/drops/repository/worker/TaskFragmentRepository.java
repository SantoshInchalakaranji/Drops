package com.prplmnstr.drops.repository.worker;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.MainActivity;
import com.prplmnstr.drops.R;
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
    List<Unit> units = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 123;


    public TaskFragmentRepository(OnFirebaseRespond onFirebaseRespond) {
        this.onFirebaseRespond = onFirebaseRespond;
    }

    public void getRecords(String plantName) {
        loadUnits(plantName);
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
                                Log.i("TAG", "onComplete: " + units.get(0).getUnitName());
                            } else {
                                onFirebaseRespond.onTasksLoaded(null);
                            }
                        }
                    }
                });

    }

    private void getTasks(List<Unit> units) {
        List<Record> records = new ArrayList<>();
        Date today = Helper.getTodayDateObject();
        Log.i("TAG", "onComplete: " + String.valueOf(units.size()));
        for (Unit unit : units) {
            firebaseFirestore.collection(Constants.RECORDS)
                    .whereEqualTo(Constants.UNIT_NAME, unit.getUnitName())
                    .orderBy("year", Query.Direction.DESCENDING)
                    .orderBy("month", Query.Direction.DESCENDING)
                    .orderBy("day", Query.Direction.DESCENDING)

                    .limit(1)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // records.clear();
                                Log.i("TAG", "onComplete: " + task.isSuccessful());
                                Record record = new Record();
                                record = task.getResult().toObjects(Record.class).get(0);
                                records.add(record);
                                Log.i("TAG", "onComplete: " + records.size() + "===" + units.size());

                                if (records.size() == units.size()) {
                                    Log.i("SIZE", "onCompletefsdfsfffsf: " + record.getUnitName());
                                    onFirebaseRespond.onTasksLoaded(records);
                                }


                            } else {
                                Log.i("TAG", "fail: " + task.isSuccessful());
                                Log.i("TAG", "fail: " + task.getException().getMessage());

                            }
                        }
                    });
        }
    }

    public void addRecord(Record record,Context context) {

        Date date = Helper.getTodayDateObject();

        String recordDocumentName = record.getPlantName() + "_" + record.getUnitName() + "_" + date.getDateInStringFormat();
        firebaseFirestore.collection(Constants.RECORDS)
                .document(recordDocumentName)
                .set(record).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           // sendNotification("Data Added", record.getUnitName()+" data is added to plant"+ record.getPlantName(),context);
                            Toast.makeText(context, "Record submit successful", Toast.LENGTH_SHORT).show();
                            onFirebaseRespond.onRecordAdded(true);
                        } else {
                            Toast.makeText(context, "Record submit failed", Toast.LENGTH_SHORT).show();
                            onFirebaseRespond.onRecordAdded(false);
                        }
                    }
                });
    }

    public void sendNotification(String title, String message, Context context) {
        // Create an intent for the activity you want to open when the user taps the notification
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Create the notification builder
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "your_notification_channel_id")
                .setSmallIcon(R.drawable.water_drop)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Get an instance of the NotificationManagerCompat and show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
           // ActivityCompat.requestPermissions( context, new String[]{Manifest.permission.NOTIFICATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        notificationManager.notify(0, notificationBuilder.build());
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
        void onPlantsLoaded(List<String> plants);

        void onTasksLoaded(List<Record> records);

        void onRecordAdded(Boolean result);



    }
}
