package com.prplmnstr.drops.viewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.models.User;

import com.prplmnstr.drops.repository.admin.AddUserFragmentRepository;
import com.prplmnstr.drops.utils.Constants;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class AddUserViewModel extends ViewModel implements AddUserFragmentRepository.OnFirebaseRespond {
    private AddUserFragmentRepository repository;
    private MutableLiveData<Boolean> result;
    private MutableLiveData<Boolean> deleteResult;
    private MutableLiveData<List<User>> workers;
    private MutableLiveData<List<User>> investors;



    public AddUserViewModel( ) {
        this.repository =new AddUserFragmentRepository(this);
        this.result = new MutableLiveData<>();
        this.workers = new MutableLiveData<>();
        this.investors = new MutableLiveData<>();
        this.deleteResult = new MutableLiveData<>();

    }

    public MutableLiveData<Boolean> addUser(User user,boolean newUser,Context context){
        repository.addNewUser(user,newUser,context);
        return result;
    }

    public MutableLiveData<Boolean> getDeleteResult(User user, Context context){
        repository.deleteUser(user,context);
        return deleteResult;
    }
    public MutableLiveData<List<User>> getWorkers(String plantName){
        repository.getWorkers(plantName);
        return workers;
    }

    public MutableLiveData<List<User>> getInvestors(String plantName){
        repository.getInvestors(plantName);
        return investors;
    }
    public List<RecyclerModel> mapUsersToRecyclerItem(List<User> users,String plantName){
        List<RecyclerModel> recyclerModelList = new ArrayList<>();

        for(User user: users) {
            if (user.getPlantName().equals(plantName)) {
                RecyclerModel model = new RecyclerModel();
                model.setImageIndex(0);
                model.setDate(user.getPassword());
                model.setHeaderName(user.getEmail());
                model.setSubTitleName(user.getUserName());
                recyclerModelList.add(model);
            }
        }
        return recyclerModelList;
    }

    public User isEmailExists(List<User> objectList, String targetEmail) {
        for (User object : objectList) {
            if (object.getEmail().equals(targetEmail)) {
                return object;// Email address exists in the list
            }
        }
        return null; // Email address does not exist in the list
    }








    @Override
    public void onUserAdded(Boolean result) {
        this.result.setValue(result);
    }

    @Override
    public void onInvetorLoaded(List<User> investors) {
        this.investors.setValue(investors);
    }

    @Override
    public void onWorkersLoaded(List<User> worker) {
        this.workers.setValue(worker);
    }

    @Override
    public void onUserDeleted(Boolean result) {
        this.deleteResult.setValue(result);
    }


}
