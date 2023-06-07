package com.prplmnstr.drops.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prplmnstr.drops.models.Transaction;
import com.prplmnstr.drops.utils.Helper;

public class UploadDataRepository {

    private FirebaseFirestore firebaseFirestore;
    Helper helper = new Helper();
    Context context;
    UserDetailsRepository  userDetailsRepository;


    public UploadDataRepository(Context context) {
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
       // userDetailsRepository = new UserDetailsRepository();

    }

    public void saveTransaction(Transaction transaction){
        String outletName = "userDetailsRepository.getOutletName();";
        String date = helper.getDateInStringFormat(transaction.getDay(),
                transaction.getMonth(),transaction.getYear());
        String doucmentId = outletName+"_"+date;


        firebaseFirestore.collection(outletName)
                .document(doucmentId)
                .set(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Data Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Data Upload Failed Due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
