package com.prplmnstr.drops.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.prplmnstr.drops.repository.AuthRepository;

public class MainActivityViewModel extends AndroidViewModel {

    private FirebaseUser currentUser;
    private AuthRepository authRepository;
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        currentUser = authRepository.getCurrentUser();
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }
}
