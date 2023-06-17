package com.prplmnstr.drops.viewModel.investor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.PlantReport;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.repository.admin.DashboardFBRepository;
import com.prplmnstr.drops.repository.worker.TaskFragmentRepository;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.views.worker.WorkerTaskFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvestorDashboardViewModel extends ViewModel implements DashboardFBRepository.OnFirebaseRespond, TaskFragmentRepository.OnFirebaseRespond{


    private DashboardFBRepository repository;
    private TaskFragmentRepository taskRepo;


    private MutableLiveData<Boolean> result;

    private MutableLiveData<Integer> noOfUnits;
    private MutableLiveData<Integer> unitNumber;
    private MutableLiveData<List<Record>> records;

    private MutableLiveData<List<Record>> monthlyData;
    private MutableLiveData<PlantReport> plantReport;
    private MutableLiveData<List<String>> plants;


    public InvestorDashboardViewModel(){
        repository = new DashboardFBRepository(this);
        taskRepo = new TaskFragmentRepository(this);
        result = new MutableLiveData<>();
        noOfUnits = new MutableLiveData<>();
        unitNumber = new MutableLiveData<>();
        records = new MutableLiveData<>();


        monthlyData = new MutableLiveData<>();
        plantReport = new MutableLiveData<>();
        plants = new MutableLiveData<>();


    }


    public MutableLiveData<List<String>> getPlants(){
        taskRepo.getPlants();
        return plants;
    }

    public MutableLiveData<List<Record>> getTodayRecord(String plantName){
        repository.getTodayRecord(plantName);
        return records;
    }

    public MutableLiveData<List<Record>> getMonthlyData(String plantName){
        repository.getMonthlyData(plantName);
        return monthlyData;
    }

    public MutableLiveData<PlantReport> getPlantReport(String plantName){
        repository.getPlantReport(plantName);
        return plantReport;
    }

    public MutableLiveData<Integer> getNoOfUnits(){
        return noOfUnits;
    }



    public MutableLiveData<Integer> noOfUnits(String plantName){
        repository.NoOfUnits(plantName);
        return unitNumber;
    }


    public Map<String,String> getMonthlySum(List<Record> records){
        int sum = 0;
        int waterSupply = 0;
        for(Record data : records){
            sum += data.getAmount();
            waterSupply+= data.getWaterSupply();
        }
        Map<String, String > map = new HashMap<>();
        map.put("sum", String.valueOf(sum));
        map.put("waterSupply", String.valueOf(waterSupply));
        return map;
    }

    public float calculateTextSize(String text) {
        int textLength = text.length();
        if(textLength<9 ){
            return 24;
        }if(textLength>15){
            return 12;
        }
        float baseTextSize = 26; // Set your desired base text size here
        float textSizeIncrement = 1; // Set the increment value for each character

        float calculatedTextSize = baseTextSize - (textLength );
        if (calculatedTextSize < 12) {
            calculatedTextSize = 12; // Set a minimum text size to avoid extremely small text
        }

        return calculatedTextSize;
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

    }

    @Override
    public void onWorkerDelete(Boolean result) {

    }

    @Override
    public void noOfUnitsLoaded(int noOfUnits) { this.unitNumber.setValue(noOfUnits);
    }



    @Override
    public void onGettingTodayRecord(List<Record> records, int noOfUnits) {
        this.noOfUnits.setValue(noOfUnits);
        this.records.setValue(records);
    }

    @Override
    public void onGettingAttendaceList(List<Attendance> attendances) {

    }

    @Override
    public void onGettingAttendanceOfUser(List<Attendance> attendances) {

    }

    @Override
    public void onGettingMontlyData(List<Record> monthlyRecords) {
        this.monthlyData.setValue(monthlyRecords);
    }

    @Override
    public void onPlantReportLoaded(PlantReport plantReport) {
        this.plantReport.setValue(plantReport);
    }

    @Override
    public void onPlantsLoaded(List<String> plants) {
        this.plants.setValue(plants);
    }

    @Override
    public void onTasksLoaded(List<Record> records) {

    }

    @Override
    public void onRecordAdded(Boolean result) {

    }
}
