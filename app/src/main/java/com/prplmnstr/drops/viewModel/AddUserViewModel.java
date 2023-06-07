package com.prplmnstr.drops.viewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.repository.UserDetailsRepository;
import com.prplmnstr.drops.utils.Constants;

import java.io.Closeable;
import java.util.List;

public class AddUserViewModel extends ViewModel implements UserDetailsRepository.OnOutletsLoaded,UserDetailsRepository.OnOutletSaved {
   private UserDetailsRepository userDetailsRepository ;
    private MutableLiveData<Boolean> result;
    private MutableLiveData<List<String>> outlets ;

    public AddUserViewModel( ) {
        this.userDetailsRepository =new UserDetailsRepository(this,this);
        this.result = new MutableLiveData<>();
        this.outlets = new MutableLiveData<>();
    }



    public MutableLiveData<List<String>> getOutlets(){
        userDetailsRepository.getOutletNames();

        return outlets;
    }

    public MutableLiveData<Boolean> addOutlet(String outlet){
        userDetailsRepository.addOutlet(outlet);
        return result;
    }


    @Override
    public void onSuccess(List<String> outletsName) {
        Log.i("TAG", "getOutlets:fdfd "+outletsName.size());
        outlets.setValue(outletsName);
    }

    @Override
    public void onOnfailure() {

    }



    @Override
    public void onSuccess(Boolean result) {
        this.result.setValue(result);
    }
}
