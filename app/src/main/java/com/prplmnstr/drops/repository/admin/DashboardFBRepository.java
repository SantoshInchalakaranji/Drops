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
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class DashboardFBRepository {
    private OnFirebaseRespond onFirebaseRespond;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public DashboardFBRepository(OnFirebaseRespond onFirebaseRespond){
        this.onFirebaseRespond = onFirebaseRespond;
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

        void onUnitUpdated(Boolean result);

    }
}