package com.prplmnstr.drops.viewModel;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.R;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.repository.admin.DashboardFBRepository;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DashboardViewModel extends ViewModel implements DashboardFBRepository.OnFirebaseRespond{



    private DashboardFBRepository repository;


    private MutableLiveData<Boolean> result;
    private MutableLiveData<Boolean> attendanceAdded;
    private MutableLiveData<Boolean> deleteTheWorker;
    private MutableLiveData<Integer> noOfUnits;
    private MutableLiveData<Integer> unitNumber;
    private MutableLiveData<List<Record>> records;
    private MutableLiveData<List<Attendance>> attendanceList;
    private MutableLiveData<List<Attendance>> attendanceOfUser;
    public DashboardViewModel(){
        repository = new DashboardFBRepository(this);
        result = new MutableLiveData<>();
        noOfUnits = new MutableLiveData<>();
        unitNumber = new MutableLiveData<>();
        records = new MutableLiveData<>();
        attendanceList = new MutableLiveData<>();
        attendanceAdded = new MutableLiveData<>();
        deleteTheWorker = new MutableLiveData<>();
        attendanceOfUser = new MutableLiveData<>();


    }

    public MutableLiveData<Boolean> addNewUnit(String plantName,String unitName,String type,int opening,int waterOpening){
        repository.addNewUnit(plantName, unitName, type, opening, waterOpening);

        return result;
    }

    public MutableLiveData<List<Attendance>> getAttendances(String plantName){
        repository.getAttendanceRecord(plantName);
        return attendanceList;
    }

    public MutableLiveData<List<Attendance>> getAttendanceOfUser(Attendance attendance){
        repository.getAllAttendanceOfUser(attendance);
        return attendanceOfUser;
    }
    public MutableLiveData<List<Record>> getTodayRecord(String plantName){
        repository.getTodayRecord(plantName);
        return records;
    }

    public MutableLiveData<Integer> getNoOfUnits(){
        return noOfUnits;
    }

    public MutableLiveData<Boolean> addAttendance(Attendance attendance){

        repository.saveAttendance(attendance);
        return attendanceAdded;
    }
    public MutableLiveData<Boolean> deleteWorker(Attendance attendance){

        repository.saveAttendance(attendance);
        return deleteTheWorker;
    }
    public MutableLiveData<Integer> noOfUnits(String plantName){
        repository.NoOfUnits(plantName);
        return unitNumber;
    }

    public Map<String,String> getSumOfAttendances(List<Attendance> attendances){
        int present = 0;
        int absent = 0;
        for(Attendance data : attendances){
            if(data.getAttendance()==Constants.PRESENT){
                present++;
            }
            if(data.getAttendance()==Constants.ABSENT){
                absent++;
            }
        }
        Map<String, String > map = new HashMap<>();
        map.put("present", String.valueOf(present));
        map.put("absent", String.valueOf(absent));
        return map;
    }

    public Map<String,Integer> getBlueBoxDetails(List<Record> records) {
        int sum = 0;
        int waterSupply =0;


        for (Record record : records) {


            sum = sum + record.getAmount();
            waterSupply = waterSupply + record.getWaterSupply();


        }
        Map <String, Integer> map = new HashMap<>();
        map.put("sum",sum);
        map.put("waterSupply",waterSupply);
        return map;
    }

    @Override
    public void onUnitAdded(Boolean result) {
        this.result.setValue(result);
    }

    @Override
    public void onAttendanceChanged(Boolean result) {
        this.attendanceAdded.setValue(result);
    }

    @Override
    public void onWorkerDelete(Boolean result) {
        this.deleteTheWorker.setValue(result);
    }

    @Override
    public void noOfUnitsLoaded(int noOfUnits) {
        this.unitNumber.setValue(noOfUnits);
    }



    @Override
    public void onGettingTodayRecord(List<Record> records, int noOfUnits) {
        this.noOfUnits.setValue(noOfUnits);
        this.records.setValue(records);
    }

    @Override
    public void onGettingAttendaceList(List<Attendance> attendances) {
        this.attendanceList.setValue(attendances);
    }

    @Override
    public void onGettingAttendanceOfUser(List<Attendance> attendances) {
        this.attendanceOfUser.setValue(attendances);
    }
}