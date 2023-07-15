package com.prplmnstr.drops.viewModel.authentication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.prplmnstr.drops.repository.authentication.AuthRepository;

import java.util.List;

public class AuthViewModel extends AndroidViewModel implements AuthRepository.OnFirebaseRespond{

    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private FirebaseUser currentUser;
    private AuthRepository authRepository;
    private MutableLiveData<List<String>> users ;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        users = new MutableLiveData<>();
        authRepository = new AuthRepository(application,this);
        currentUser = authRepository.getCurrentUser();
        firebaseUserMutableLiveData = authRepository.getFirebaseUserMutableLiveData();

    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }


    public void signIn(String email, String password){
        authRepository.signIn(email, password);
    }

    public MutableLiveData<List<String>> isUserExist(String userType){
        authRepository.isUserExist(userType);
        return users;
    }


    @Override
    public void onUsersLoaded(List<String> users) {
        this.users.setValue(users);
    }
}
