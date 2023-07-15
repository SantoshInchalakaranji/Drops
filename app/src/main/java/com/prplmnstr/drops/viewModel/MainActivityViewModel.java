package com.prplmnstr.drops.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.prplmnstr.drops.repository.authentication.AuthRepository;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements AuthRepository.OnFirebaseRespond{

    private FirebaseUser currentUser;
    private AuthRepository authRepository;
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application,this);
        currentUser = authRepository.getCurrentUser();
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    @Override
    public void onUsersLoaded(List<String> users) {

    }
}
