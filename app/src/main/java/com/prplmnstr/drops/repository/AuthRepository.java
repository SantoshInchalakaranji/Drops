package com.prplmnstr.drops.repository;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.repository.admin.HomeFragmentRepository;

import java.util.ArrayList;
import java.util.List;

public class AuthRepository {

    private Application application;
    private FirebaseAuth firebaseAuth;
    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private OnFirebaseRespond onFirebaseRespond;




    public AuthRepository(Application application, OnFirebaseRespond onFirebaseRespond){
        this.application = application;
        this.onFirebaseRespond = onFirebaseRespond;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUserMutableLiveData = new MutableLiveData<>();

    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public void isUserExist( String userType){
        List<String> users = new ArrayList<>();
        firebaseFirestore.collection(userType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot snapshot  = task.getResult();
                            if(snapshot.isEmpty()){
                                onFirebaseRespond.onUsersLoaded(null);
                                return;
                            }
                            for (DocumentSnapshot document : task.getResult()) {
                                // Retrieve the value of the desired field from the document
                                String fieldValue = document.get("email").toString();

                                // Add the field value to the list
                                users.add(fieldValue);
                            }
                            onFirebaseRespond.onUsersLoaded(users);
                        }else{
                            onFirebaseRespond.onUsersLoaded(null);
                        }
                    }
                });
    }

    public  void signIn(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                }else{

                    Toast.makeText(application, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
public interface OnFirebaseRespond{
        void onUsersLoaded(List<String> users);
}

}
