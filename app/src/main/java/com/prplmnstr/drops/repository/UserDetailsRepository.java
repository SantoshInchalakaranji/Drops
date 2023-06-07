package com.prplmnstr.drops.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserDetailsRepository {

    private FirebaseFirestore firebaseFirestore;
    private OnOutletsLoaded onOutletsLoaded;
    private OnOutletSaved onOutletSaved;



    public UserDetailsRepository(OnOutletsLoaded onOutletsLoaded,OnOutletSaved onOutletSaved){
        this.onOutletsLoaded = onOutletsLoaded;
        this.onOutletSaved = onOutletSaved;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    public  void getOutletNames(){

        List<String> outlets = new ArrayList<>();


       firebaseFirestore.collection(Constants.OUTLETS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                for (DocumentSnapshot data : document){
                                    outlets.add((String)data.get("name"));

                                }
                                onOutletsLoaded.onSuccess(outlets);
                                Log.i("TAG", "onComplete: "+outlets.size());


                            } else {
                                Log.d("DOC", "No such document");
                            }
                        } else {
                            Log.d("DOC", "get failed with ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

//    public  void getOutletName(){
//
//
//        firebaseFirestore.collection(Constants.USERS)
//                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        outletName = task.getResult().get("outletName").toString();
//                        onOutletNameLoaded.onSuccess(outletName);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//
//    }

    public void addOutlet(String outlet){
        Map<String,String> map = new HashMap<>();
        map.put(Constants.NAME,outlet);

        firebaseFirestore.collection(Constants.OUTLETS).document(outlet)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onOutletSaved.onSuccess(true);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onOutletSaved.onSuccess(false);
                    }
                });

    }

    public interface OnOutletsLoaded{
        void onSuccess(List<String> outlets);
        void onOnfailure();
    }

    public interface OnOutletSaved{
        void onSuccess(Boolean result);
    }



}
