package com.prplmnstr.drops.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.prplmnstr.drops.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private FirebaseUser currentUser;
    private AuthRepository authRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);

        authRepository = new AuthRepository(application);
        currentUser = authRepository.getCurrentUser();
        firebaseUserMutableLiveData = authRepository.getFirebaseUserMutableLiveData();

    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void signUp(String email, String password){
        authRepository.signUp(email, password);
    }
    public void signIn(String email, String password){
        authRepository.signIn(email, password);
    }


}
