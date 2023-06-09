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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class DashboardFBRepository {
    private onFirestoreTaskComplete onFirestoreTaskComplete;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference reference = firebaseFirestore.collection("Recents");

    public DashboardFBRepository(onFirestoreTaskComplete onFirestoreTaskComplete){
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
    }
    public void getRecyclerItems(){
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    onFirestoreTaskComplete.recyclerDataLoaded(task.getResult()
                            .toObjects(RecyclerModel.class));
                }else{
                    onFirestoreTaskComplete.onError(task.getException());
                }
            }
        });
    }

    public interface onFirestoreTaskComplete{
        void recyclerDataLoaded(List<RecyclerModel> recyclerModels);
        void onError(Exception e);
    }
}