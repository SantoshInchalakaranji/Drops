package com.prplmnstr.drops.viewModel;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.R;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.repository.admin.DailyInfoRepository;
import com.prplmnstr.drops.repository.worker.TaskFragmentRepository;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;
import com.prplmnstr.drops.views.admin.DailyInfoFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DailyInfoViewModel extends ViewModel implements DailyInfoRepository.OnFirebaseRespond {
    private DailyInfoRepository repository;
    private MutableLiveData<List<Record>> records;

    private MutableLiveData<Integer> collection;
    private MutableLiveData<Integer> waterSupply;
    private MutableLiveData<Integer> taskCount;

    public DailyInfoViewModel() {
        this.repository = new DailyInfoRepository(this);
        records = new MutableLiveData<>();

        collection = new MutableLiveData<>();
        waterSupply = new MutableLiveData<>();
        taskCount = new MutableLiveData<>();
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


    public MutableLiveData<List<Record>> getRecords(String plantName,Date date){

        repository.loadUnits(plantName, date);

        return records;
    }

    public List<RecyclerModel> getRecycleItems(List<Record> records, Resources resources, int checkmarkId) {
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

                recyclerItem.setDate("");
                recyclerItem.setImageIndex(checkmarkId);


                sum = sum+ record.getAmount();
                waterSupply = waterSupply+record.getWaterSupply();
                taskCount++;


            }else{
                recyclerItem.setHeaderName(record.getUnitName() );
                recyclerItem.setSubTitleName("₹ --" );
                recyclerItem.setDate("");

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


    public List<RecyclerModel> getPreviousDate(List<Record> records, Resources resources, int checkmarkId) {
        int sum = 0;
        int waterSupply =0;
        int taskCount = 0;


        List<RecyclerModel> list = new ArrayList<>();
        for (Record record : records) {
            RecyclerModel recyclerItem = new RecyclerModel();


                if(isRecordDummy(record)) {
                    recyclerItem.setSubTitleName("₹ " + record.getAmount());
                    recyclerItem.setHeaderName(record.getUnitName() );
                    recyclerItem.setDate("Record not added");
                }else{


                recyclerItem.setSubTitleName("₹ " + record.getAmount());
                if(record.getType().equals(Constants.RECHARGE_UNIT)){
                    recyclerItem.setHeaderName(record.getUnitName() );

                }else{
                    recyclerItem.setHeaderName(record.getUnitName() + " (" + record.getWaterSupply() + "L)");
                }

                recyclerItem.setDate("");



                sum = sum+ record.getAmount();
                waterSupply = waterSupply+record.getWaterSupply();
                taskCount++;


            }



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
            list.add(recyclerItem);
            }
        this.collection.setValue(sum);
        this.waterSupply.setValue(waterSupply);
        this.taskCount.setValue(taskCount);
        return list;
        }

    private boolean isRecordDummy(Record record) {


        if(record.getAmount()==0 && record.getWaterSupply()==0 &&
        record.getClosing()==0 && record.getOpening()==0 &&
                record.getWaterOpen()==0 && record.getWaterClose()==0){
            return true;

    }
       else{
           return false;
        }
    }


    @Override
    public void onTasksLoaded(List<Record> records) {
        this.records.setValue(records);
    }
}

