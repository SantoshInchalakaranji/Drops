package com.prplmnstr.drops.viewModel;



import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.R;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.repository.worker.TaskFragmentRepository;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskFragmentViewModel extends ViewModel implements TaskFragmentRepository.OnFirebaseRespond {
    private TaskFragmentRepository repository;
    private MutableLiveData<List<Record>> records;
    private MutableLiveData<List<String>> plants;
    private MutableLiveData<Integer> collection;
    private MutableLiveData<Integer> waterSupply;
    private MutableLiveData<Integer> taskCount;
    private MutableLiveData<Boolean> result;

    public TaskFragmentViewModel() {
        this.repository = new TaskFragmentRepository(this);
        records = new MutableLiveData<>();
        plants=new MutableLiveData<>();
        collection = new MutableLiveData<>();
        waterSupply = new MutableLiveData<>();
        taskCount = new MutableLiveData<>();
        result = new MutableLiveData<>();


    }



    public MutableLiveData<Integer> getTaskCount(){
        return taskCount;
    }
    public MutableLiveData<Integer> getWaterSupply(){
        return waterSupply;
    }
    public MutableLiveData<Integer> getCollection(){
        return collection;
    }


    public MutableLiveData<List<String>> getPlants(){
        repository.getPlants();
        return plants;
    }
    public MutableLiveData<List<Record>> getRecords(String plantName){

        repository.getRecords(plantName);

        return records;
    }


    public List<RecyclerModel> getRecycleItems(List<Record> records,Resources resources, int checkmarkId) {
       int sum = 0;
       int waterSupply =0;
       int taskCount = 0;
        Date today = Helper.getTodayDateObject();

        List<RecyclerModel> list = new ArrayList<>();
        for (Record record : records) {
            RecyclerModel recyclerItem = new RecyclerModel();

            String recordDate = Helper.getDateInStringFormat(record.getDay(), record.getMonth(), record.getYear());
            if (recordDate.equals(today.getDateInStringFormat())) {
                recyclerItem.setSubTitleName("₹ " + record.getAmount());
                if(record.getType().equals(Constants.RECHARGE_UNIT)){
                    recyclerItem.setHeaderName(record.getUnitName() );

                }else{
                    recyclerItem.setHeaderName(record.getUnitName() + " (" + record.getWaterSupply() + "L)");
                }

                recyclerItem.setDate("edit");
                recyclerItem.setImageIndex(checkmarkId);


                sum = sum+ record.getAmount();
                waterSupply = waterSupply+record.getWaterSupply();
                taskCount++;


            }else{
                recyclerItem.setHeaderName(record.getUnitName() );
                recyclerItem.setSubTitleName("₹ --" );
                recyclerItem.setDate("pending");

                if(record.getType().equals(Constants.STATIONARY_UNIT)){
                    final TypedArray imgs = resources.obtainTypedArray(R.array.store);
                    final Random rand = new Random();

                    int rndInt = rand.nextInt(imgs.length());
                    int resID = imgs.getResourceId(rndInt, R.drawable.employee_1);
                    recyclerItem.setImageIndex(resID);
                }else if(record.getType().equals(Constants.MOBILE_UNIT)){
                    final TypedArray imgs = resources.obtainTypedArray(R.array.truck);
                    final Random rand = new Random();

                    int rndInt = rand.nextInt(imgs.length());
                    int resID = imgs.getResourceId(rndInt, R.drawable.water_truck_1);
                    recyclerItem.setImageIndex(resID);
                }else if(record.getType().equals(Constants.RECHARGE_UNIT)){
                    final TypedArray imgs = resources.obtainTypedArray(R.array.machine);
                    final Random rand = new Random();

                    int rndInt = rand.nextInt(imgs.length());
                    int resID = imgs.getResourceId(rndInt, R.drawable.vending_machine_1);
                    recyclerItem.setImageIndex(resID);
                }
            }
            list.add(recyclerItem);
        }
        this.collection.setValue(sum);
        this.waterSupply.setValue(waterSupply);
        this.taskCount.setValue(taskCount);
        return list;
    }

    public MutableLiveData<Boolean> addRecord(Record record){
        repository.addRecord(record);
        return result;
    }

    @Override
    public void onPlantsLoaded(List<String> plants) {
        this.plants.setValue(plants);
    }

    @Override
    public void onTasksLoaded(List<Record> records) {

        Log.i("RECCC", "onTasksLoaded: "+records.size());
        this.records.setValue(records);
    }

    @Override
    public void onRecordAdded(Boolean result) {
        this.result.setValue(result);
    }
}


