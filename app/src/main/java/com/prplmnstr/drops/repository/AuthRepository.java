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

public class AuthRepository {

    private Application application;
    private FirebaseAuth firebaseAuth;
    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;



    public AuthRepository(Application application){
        this.application = application;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUserMutableLiveData = new MutableLiveData<>();

    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public void signUp(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
}
