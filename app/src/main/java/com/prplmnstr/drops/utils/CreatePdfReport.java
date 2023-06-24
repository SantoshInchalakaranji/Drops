package com.prplmnstr.drops.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.common.collect.Table;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import android.graphics.pdf.PdfDocument.*;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Expense;
import com.prplmnstr.drops.models.PlantReport;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.Unit;
import com.prplmnstr.drops.models.User;






import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePdfReport  {

    Context context;
    Resources resources;
    Activity activity;
    int height = (int) PageSize.A4.getWidth();
    int width = (int)PageSize.A4.getHeight();
    Date today = Helper.getTodayDateObject();

    public CreatePdfReport(Context context, Resources resources,Activity activity) {
        this.context = context;
        this.activity = activity;
        this.resources = resources;

    }

    Map<String, Object> map = new HashMap<>();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    List<Unit> units  = new ArrayList<>();

    private List<User> workers = new ArrayList<>();
    List<Expense> expenses = new ArrayList<>();



    PlantReport plantReport = new PlantReport();



    public  void downloadableData(String plantName,Date recordDate){



        ///loadUnits
        firebaseFirestore.collection(plantName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (!snapshot.isEmpty()) {
                                units = snapshot.toObjects(Unit.class);
                                //  Log.i("TAG", "date-----------------------"+date.getDay()+"-"+date.getMonth());

                                if(recordDate.getDateInStringFormat().equals(today.getDateInStringFormat())){
                                    getTasks(units,today,plantName);
                                }else{
                                    getPreviousTasks(units,recordDate,plantName);
                                }

                              //  Toast.makeText(context, "Units data came", Toast.LENGTH_SHORT).show();
                            }else{

                            }
                        }
                    }
                });



    }

    private void getPreviousTasks(List<Unit> units, Date recordDate, String plantName) {
        List<Record> records  = new ArrayList<>();
        Query query;

        // Log.i("TAG", "onComplete: "+String.valueOf(units.size()));
        for(Unit unit : units){


            // Log.i("TAG", "today----------------: "+units.get(0).getUnitName());

            query =  firebaseFirestore.collection(Constants.RECORDS)
                    .whereEqualTo(Constants.PLANT_NAME,plantName)
                    .whereEqualTo(Constants.UNIT_NAME,unit.getUnitName())
                    .whereEqualTo("year", recordDate.getYear())
                    .whereEqualTo("month",recordDate.getMonth())
                    .whereEqualTo("day",recordDate.getDay())

                    .limit(1);





            query .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){

                        Record record = new Record();
                        QuerySnapshot snapshot = task.getResult();
                        if(snapshot.isEmpty()){
                            record.setUnitName(unit.getUnitName());
                            record.setMonth(recordDate.getMonth());
                            record.setYear(recordDate.getYear());
                            record.setDay(recordDate.getDay());
                            record.setPlantName(plantName);
                            record.setType(unit.getUnitType());
                            record.setWaterClose(0);
                            record.setWaterOpen(0);
                            record.setOpening(0);
                            record.setClosing(0);
                            record.setAmount(0);
                            record.setWaterSupply(0);
                        }else {
                            record = task.getResult().toObjects(Record.class).get(0);
                        }


                        records.add(record);
                        Log.i("TAG", record.getDay()+unit.getUnitName()+"onComplete: "+records.size()+"==="+units.size());

                        if(records.size()== units.size()){
                            Log.i("SIZE", "onCompletefsdfsfffsf: "+record.getUnitName());
                            map.put(Constants.RECORDS,records);
                            getDownlaodablePlantReportPrevious(plantName,recordDate);
                            // Toast.makeText(context, "task data came", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Log.i("TAG", "fail: "+task.isSuccessful());
                        Log.i("TAG", "fail: "+task.getException().getMessage());

                    }
                }
            });
        }

    }

    private void getDownlaodablePlantReportPrevious(String plantName, Date recordDate) {

        firebaseFirestore.collection(Constants.PLANT_REPORTS)
                .whereEqualTo(Constants.PLANT_NAME,plantName)
                .whereEqualTo("year", recordDate.getYear())
                .whereEqualTo("month",recordDate.getMonth())
                .whereEqualTo("day",recordDate.getDay())

                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            if(task.getResult().isEmpty()){
                                Log.i("TAG", "onComplete: result  empty");
                               plantReport = new PlantReport(plantName,"-",
                                       0,0,0,0,0, recordDate.getDay(),
                                       recordDate.getMonth(),
                                       recordDate.getYear());

                                // Toast.makeText(context, "d", Toast.LENGTH_SHORT).show();
                            }else{
                                plantReport = task.getResult().toObjects(PlantReport.class).get(0);
                            }
                            map.put(Constants.PLANT_REPORTS, plantReport);
                            getDownloadableAttendenceRecordPrevious(plantName,recordDate);

                        }else{

                        }
                    }
                });
    }

    private void getDownloadableAttendenceRecordPrevious(String plantName, Date recordDate) {
        firebaseFirestore.collection(Constants.WORKER)
                .whereEqualTo(Constants.PLANT_NAME,plantName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            if(!task.getResult().isEmpty()){
                                workers = task.getResult().toObjects(User.class);

                               getExpensesPrevious(plantName,workers,recordDate);
                            }

                            // Toast.makeText(context, "attendance data came", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getDownloadableAttendancePrevious(List<User> workers, String plantName, Date recordDate) {
        List<Attendance> attendances = new ArrayList<>();
        for(User worker:workers){
            firebaseFirestore.collection(Constants.ATTENDANCE)
                    .whereEqualTo(Constants.PLANT_NAME,plantName)
                    .whereEqualTo("userName",worker.getUserName())
                    .whereEqualTo("year", recordDate.getYear())
                    .whereEqualTo("month",recordDate.getMonth())
                    .whereEqualTo("day",recordDate.getDay())

                    .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot snapshot = task.getResult();
                                Attendance attendance= new Attendance();
                                if(snapshot.isEmpty()){
                                    attendance.setUserName(worker.getUserName());
                                    attendance.setPlantName(plantName);
                                    attendance.setDay(recordDate.getDay());
                                    attendance.setMonth(recordDate.getMonth());
                                    attendance.setAttendance(Constants.NO_ATTENDANCE);
                                    attendance.setYear(recordDate.getYear());

                                }else{
                                    attendance  = task.getResult().toObjects(Attendance.class).get(0);
                                }

                                attendances.add(attendance);
                                if(attendances.size()== workers.size()){
                                    map.put(Constants.ATTENDANCE,attendances);
                                    Toast.makeText(context, "Creating PDF...", Toast.LENGTH_SHORT).show();
                                    try {
                                        if (ContextCompat.checkSelfPermission(activity,  android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                                        } else {
                                            // Permission already granted, create the PDF
                                            createPdf(map,recordDate);
                                        }
                                    } catch (FileNotFoundException e) {
                                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                                    } catch (DocumentException e) {
                                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        }
                    });
        }


    }

    private void getTasks(List<Unit> units, Date date, String plantName)  {
        List<Record> records  = new ArrayList<>();
        Date today = Helper.getTodayDateObject();
        Query query;

       // Log.i("TAG", "onComplete: "+String.valueOf(units.size()));
        for(Unit unit : units){


           // Log.i("TAG", "today----------------: "+units.get(0).getUnitName());

            query =  firebaseFirestore.collection(Constants.RECORDS)
                    .whereEqualTo(Constants.PLANT_NAME,plantName)
                    .whereEqualTo(Constants.UNIT_NAME,unit.getUnitName())
                    .orderBy("year", Query.Direction.DESCENDING)
                    .orderBy("month",Query.Direction.DESCENDING)
                    .orderBy("day",Query.Direction.DESCENDING)

                    .limit(1);





            query .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){

                        Record record = new Record();
                        QuerySnapshot snapshot = task.getResult();
                        if(snapshot.isEmpty()){
                            record.setUnitName(unit.getUnitName());
                            record.setMonth(date.getMonth());
                            record.setYear(date.getYear());
                            record.setDay(date.getDay());
                            record.setPlantName(plantName);
                            record.setType(unit.getUnitType());
                            record.setWaterClose(0);
                            record.setWaterOpen(0);
                            record.setOpening(0);
                            record.setClosing(0);
                            record.setAmount(0);
                            record.setWaterSupply(0);
                        }else {
                            record = task.getResult().toObjects(Record.class).get(0);
                        }

                        if(!today.getDateInStringFormat().equals(
                                Helper.getDateInStringFormat(record.getDay(), record.getMonth(), record.getYear())
                        )){
                            record.setMonth(date.getMonth());
                            record.setYear(date.getYear());
                            record.setDay(date.getDay());
                           int lastClose = record.getClosing();
                           int lastWaterClose = record.getWaterClose();
                            record.setWaterClose(0);
                            record.setWaterOpen(lastWaterClose);
                            record.setOpening(lastClose);
                            record.setClosing(0);
                            record.setAmount(0);
                            record.setWaterSupply(0);
                        }


                        records.add(record);
                        Log.i("TAG", record.getDay()+unit.getUnitName()+"onComplete: "+records.size()+"==="+units.size());

                        if(records.size()== units.size()){
                            Log.i("SIZE", "onCompletefsdfsfffsf: "+record.getUnitName());
                            map.put(Constants.RECORDS,records);
                            getDownlaodablePlantReport(plantName);
                          // Toast.makeText(context, "task data came", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Log.i("TAG", "fail: "+task.isSuccessful());
                        Log.i("TAG", "fail: "+task.getException().getMessage());

                    }
                }
            });
        }
    }
    public void getDownlaodablePlantReport(String plantName){
        Date today = Helper.getTodayDateObject();
        firebaseFirestore.collection(Constants.PLANT_REPORTS)
                .whereEqualTo(Constants.PLANT_NAME,plantName)
                .orderBy("year", Query.Direction.DESCENDING)
                .orderBy("month",Query.Direction.DESCENDING)
                .orderBy("day",Query.Direction.DESCENDING)

                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                Log.i("TAG", "onComplete: result not empty");
                                plantReport = task.getResult().toObjects(PlantReport.class).get(0);
                                map.put(Constants.PLANT_REPORTS, plantReport);
                                getDownloadableAttendenceRecord(plantName);
                               // Toast.makeText(context, "plant report came", Toast.LENGTH_SHORT).show();
                            }


                        }else{

                        }
                    }
                });
    }
    public void getDownloadableAttendenceRecord(String plantName){

        Date today = Helper.getTodayDateObject();
        firebaseFirestore.collection(Constants.WORKER)
                .whereEqualTo(Constants.PLANT_NAME,plantName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                workers = task.getResult().toObjects(User.class);
                                getExpenses(plantName,workers);

                            }else{
                                Toast.makeText(context, "Add workers to generate efficient report", Toast.LENGTH_LONG).show();
                            }

                          // Toast.makeText(context, "attendance data came", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void getDownloadableAttendance(List<User> workers,String plantName) {
        Date today = Helper.getTodayDateObject();
        List<Attendance> attendances = new ArrayList<>();
        for(User worker:workers){
            firebaseFirestore.collection(Constants.ATTENDANCE)
                    .whereEqualTo(Constants.PLANT_NAME,plantName)
                    .whereEqualTo("userName",worker.getUserName())
                    .orderBy("year", Query.Direction.DESCENDING)
                    .orderBy("month",Query.Direction.DESCENDING)
                    .orderBy("day",Query.Direction.DESCENDING)

                    .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot snapshot = task.getResult();
                                Attendance attendance= new Attendance();
                                if(snapshot.isEmpty()){
                                    attendance.setUserName(worker.getUserName());
                                    attendance.setPlantName(plantName);
                                    attendance.setDay(today.getDay());
                                    attendance.setMonth(today.getMonth());
                                    attendance.setAttendance(Constants.NO_ATTENDANCE);
                                    attendance.setYear(today.getYear());

                                }else{
                                    attendance  = task.getResult().toObjects(Attendance.class).get(0);
                                }
                                String lastAtteded = Helper.getDateInStringFormat(attendance.getDay(),attendance.getMonth(), attendance.getYear());
                                if(!lastAtteded.equals(today.getDateInStringFormat())){
                                    attendance.setDay(today.getDay());
                                    attendance.setMonth(today.getMonth());
                                    attendance.setAttendance(Constants.NO_ATTENDANCE);
                                    attendance.setYear(today.getYear());

                                }
                                attendances.add(attendance);
                                if(attendances.size()== workers.size()){
                                    map.put(Constants.ATTENDANCE,attendances);
                                    Toast.makeText(context, "Creating PDF...", Toast.LENGTH_SHORT).show();
                                    try {
                                        if (ContextCompat.checkSelfPermission(activity,  android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                                        } else {
                                            // Permission already granted, create the PDF
                                            createPdf(map,Helper.getTodayDateObject());
                                        }
                                    } catch (FileNotFoundException e) {
                                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                                    } catch (DocumentException e) {
                                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        }
                    });
        }


    }


    public void getExpenses(String plantName,List<User> worker){

        Date today = Helper.getTodayDateObject();
        firebaseFirestore.collection(Constants.EXPENSES)
                .whereEqualTo(Constants.PLANT_NAME,plantName )
                .whereEqualTo("year", today.getYear())
                .whereEqualTo("month", today.getMonth())
                .whereEqualTo("day",today.getDay())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                              expenses = document.toObjects(Expense.class);
                                map.put(Constants.EXPENSES,expenses);
                            }else{
                                List<Expense> newList = new ArrayList<>();
                                Expense expense = new Expense(plantName,
                                        "-",0,today.getDay(),
                                        today.getMonth(),
                                        today.getYear());

                                newList.add(expense);
                                map.put(Constants.EXPENSES,expenses);
                            }

                            getDownloadableAttendance(worker,plantName);


                        }

                    }
                });
    }


    public void getExpensesPrevious(String plantName, List<User> worker, Date date){


        firebaseFirestore.collection(Constants.EXPENSES)
                .whereEqualTo(Constants.PLANT_NAME,plantName )
                .whereEqualTo("year", date.getYear())
                .whereEqualTo("month", date.getMonth())
                .whereEqualTo("day",date.getDay())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                expenses = document.toObjects(Expense.class);
                                map.put(Constants.EXPENSES,expenses);
                            }else{
                                List<Expense> newList = new ArrayList<>();
                                Expense expense = new Expense(plantName,
                                        "-",0,today.getDay(),
                                        today.getMonth(),
                                        today.getYear());

                                newList.add(expense);
                                map.put(Constants.EXPENSES,newList);
                            }

                            getDownloadableAttendancePrevious(worker,plantName,date);


                        }

                    }
                });
    }

    private void createPdf(Map<String, Object> map,Date recordDate) throws IOException, DocumentException {
        List<Record> records = (List<Record>) map.get(Constants.RECORDS);
        PlantReport plantReport = (PlantReport) map.get(Constants.PLANT_REPORTS);
        List<Attendance> attendances = (List<Attendance>) map.get(Constants.ATTENDANCE);
        List<Expense> expenseList  = new ArrayList<>();
             expenseList=    (List<Expense>) map.get(Constants.EXPENSES);



        String fileName = plantReport.getPlantName()+"_"+recordDate.getDateInStringFormat();
        String stringFilePath = Environment.getExternalStorageDirectory().getPath() + "/Download/"+fileName+".pdf";
         File file = new File(stringFilePath);


      PdfDocument pdfDocument  = new PdfDocument();






       // title
        Paint titlepaint = new Paint();
        titlepaint.setTextAlign(Paint.Align.CENTER);
        titlepaint.setTextSize(12);
        titlepaint.setTypeface(ResourcesCompat.getFont(context, R.font.segeo_regular));


        Paint date = new Paint();
        date.setTextAlign(Paint.Align.CENTER);
        date.setTextSize(14);
        date.setTypeface(ResourcesCompat.getFont(context, R.font.segeo_semi_bold));

        Paint line = new Paint();
        line.setTextAlign(Paint.Align.CENTER);
        line.setTextSize(32);
        line.setTypeface(ResourcesCompat.getFont(context, R.font.segeo_bold));


        int listSize1 = Math.max(attendances.size()+1 + records.size(), (7 + records.size()));
        int listSize = listSize1;
        if(expenseList.size()!=0) {
            listSize  = Math.max(listSize1, (1 + records.size() + expenseList.size()));
        }
        int pageHeight = 225 + (listSize)*25;

        PdfDocument.PageInfo mypageinfo = new PdfDocument.PageInfo.Builder(width, pageHeight , 1).create();
        PdfDocument.Page page1 = pdfDocument.startPage(mypageinfo);
        Canvas canvas = page1.getCanvas();


        canvas.drawText("DATE", w(10), h(4), titlepaint);
        canvas.drawText(plantReport.getPlantName(), w(50), h(4), date);

        //date text
        canvas.drawText(recordDate.getDateInStringFormat(), w(20), h(4), date);
        //date line

        canvas.drawLine(w(12),h(5),w(40),h(5),line);












// start line

        //end line

        List<String> headings = new ArrayList<>();
        headings.add("Unit");
        headings.add("Opening");
        headings.add("Closing");
        headings.add("Amount");
        headings.add("Disp Opening");
        headings.add("Disp Closing");
        headings.add("Water Supply");


        float recordsHeight = 60+ (records.size() *25);


        float columnWidth = (width-40)/7;
        float textWidth  = 20+(columnWidth)/2;
        float textStart = textWidth;
        float columnStart = 20;
        for(int i = 0;i<7;i++){
            columnStart = columnStart+columnWidth;

            canvas.drawText(headings.get(i), textStart,50, date);
            textStart = textStart+columnWidth;
            canvas.drawLine(columnStart,35,columnStart,recordsHeight,line);
        }
        // 1st line
        canvas.drawLine(20,35,20,recordsHeight,line);

        canvas.drawLine(20,35,columnStart,35,line);
        canvas.drawLine(20,60,columnStart,60,line);


        float horizontalLineStart = 60;

        float textHorizontal = 77;
        int totalWaterSupply = 0;
        int totalAmount = 0;
        for(Record record: records) {
            totalWaterSupply += record.getWaterSupply();
            totalAmount += record.getAmount();

            List<String> recordList  = new ArrayList<>();
            recordList.add(record.getUnitName());
            recordList.add(String.valueOf(record.getOpening()));
            recordList.add(String.valueOf(record.getClosing()));
            recordList.add(String.valueOf(record.getAmount()));
            recordList.add(String.valueOf(record.getWaterOpen()));
            recordList.add(String.valueOf(record.getWaterClose()));
            recordList.add(String.valueOf(record.getWaterSupply()));
            horizontalLineStart += 25;

            textStart = textWidth;
            for (int i = 0; i < 7; i++) {


                canvas.drawText(recordList.get(i), textStart, textHorizontal, titlepaint);
                textStart = textStart + columnWidth;

            }
            textHorizontal = horizontalLineStart+17;
            canvas.drawLine(20, horizontalLineStart,columnStart , horizontalLineStart, line);


        }



        //add space to horizontal linestart and add the attendance heading and two lines
        horizontalLineStart = horizontalLineStart+25;
        textHorizontal = horizontalLineStart+17;


        //plant report



        // attendance table

        textStart = textWidth;
        float attendaceTableWidthEnd = (2*columnWidth) + 20;
        float reportTableStart = (2*columnWidth)+ 20+ (columnWidth/2);
        float reportTableEnd = (4*columnWidth)+ 20+(columnWidth/2);

        float expenseTableStart = (5*columnWidth)+20;



        float attendaceHeight = horizontalLineStart+25+ (attendances.size()*25);
        float reportTableHeight = horizontalLineStart+25+ (6*25);
        float expenseTableHeight = horizontalLineStart+25+ (expenseList.size()*25);

        // vertical line 1st
        canvas.drawLine(20,horizontalLineStart,20,attendaceHeight,line);

        //report table
        canvas.drawLine(reportTableStart,horizontalLineStart,reportTableStart,reportTableHeight,line);
        //total expense table
        canvas.drawLine(reportTableEnd+(columnWidth/2),horizontalLineStart,reportTableEnd+(columnWidth/2),expenseTableHeight,line);

        // vertical line last
        canvas.drawLine(attendaceTableWidthEnd,horizontalLineStart,attendaceTableWidthEnd,attendaceHeight,line);
        // report table
        canvas.drawLine(reportTableEnd,horizontalLineStart,reportTableEnd,reportTableHeight,line);
        //water table
        canvas.drawLine(columnStart,horizontalLineStart,columnStart,expenseTableHeight,line);

        //verticle middle
        canvas.drawLine(20+columnWidth,horizontalLineStart,20+columnWidth,attendaceHeight,line);
        //report table
        canvas.drawLine(20+(3*columnWidth)+(columnWidth/2),horizontalLineStart,20+(3*columnWidth)+(columnWidth/2),reportTableHeight,line);
        //expense table middle
        canvas.drawLine(20+(6*columnWidth),horizontalLineStart,20+(6*columnWidth),expenseTableHeight,line);


        //horizontal end
        canvas.drawLine(20,attendaceHeight,attendaceTableWidthEnd,attendaceHeight,line);
        //report table
        canvas.drawLine(reportTableStart,reportTableHeight,reportTableEnd,reportTableHeight,line);
        //total water
        canvas.drawLine(reportTableEnd+(columnWidth/2),expenseTableHeight,columnStart,expenseTableHeight,line);





        canvas.drawLine(20, horizontalLineStart,attendaceTableWidthEnd , horizontalLineStart, line);
        //report table
        canvas.drawLine(reportTableStart, horizontalLineStart,reportTableEnd , horizontalLineStart, line);
        //total water
        canvas.drawLine(reportTableEnd+(columnWidth/2), horizontalLineStart,columnStart , horizontalLineStart, line);


        float waterTextStart =textStart+ (5*columnWidth);
        canvas.drawText("Worker", textStart, textHorizontal, date);
        canvas.drawText("Plant Report", textStart+(2*columnWidth)+(columnWidth/2), textHorizontal, date);
        canvas.drawText("Expense", waterTextStart, textHorizontal, date);
        canvas.drawText("Amount", waterTextStart+columnWidth, textHorizontal, date);

        canvas.drawText("Attendance", textStart+columnWidth, textHorizontal, date);
        canvas.drawText("Reading", textStart+(3*columnWidth)+(columnWidth/2), textHorizontal, date);
        horizontalLineStart +=25;
        canvas.drawLine(20, horizontalLineStart,attendaceTableWidthEnd , horizontalLineStart, line);
        //report table
        canvas.drawLine(reportTableStart, horizontalLineStart,reportTableEnd , horizontalLineStart, line);
        //water supply
        canvas.drawLine(reportTableEnd+(columnWidth/2), horizontalLineStart,columnStart , horizontalLineStart, line);

        textHorizontal = horizontalLineStart+17;

        // total water



        float reportTableHorzontalLineStart = horizontalLineStart;
        float expenseTableHorizontalLineStart = horizontalLineStart;
        // attendances

        for(Attendance attendance : attendances){
            String attend;
            if(attendance.getAttendance()==Constants.PRESENT){
                attend = "P";
            }else if(attendance.getAttendance()==Constants.ABSENT){
                attend = "A";
            }else{
                attend = "NA";
            }

            horizontalLineStart +=25;
            textStart = textWidth;
            canvas.drawText(attendance.getUserName(), textStart, textHorizontal, titlepaint);
            canvas.drawText(attend, textStart+columnWidth, textHorizontal, titlepaint);
            textHorizontal = horizontalLineStart+17;
            canvas.drawLine(20, horizontalLineStart,attendaceTableWidthEnd , horizontalLineStart, line);
        }


        int totalExpense = 0;
        for(Expense expense : expenseList){

           totalExpense += expense.getAmount();
            textHorizontal = expenseTableHorizontalLineStart+17;
            textStart = waterTextStart;
            canvas.drawText(expense.getTitle(), textStart, textHorizontal, titlepaint);
            canvas.drawText(String.valueOf(expense.getAmount()), textStart+columnWidth, textHorizontal, titlepaint);

            expenseTableHorizontalLineStart +=25;
            canvas.drawLine(expenseTableStart, expenseTableHorizontalLineStart,columnStart , expenseTableHorizontalLineStart, line);
        }



        textHorizontal = reportTableHorzontalLineStart+17;

            textStart = textWidth+(2*columnWidth)+(columnWidth/2);
            canvas.drawText("Flow", textStart,textHorizontal , titlepaint);
            canvas.drawText(String.valueOf(plantReport.getFlow()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
            canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);



        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(2*columnWidth)+(columnWidth/2);
        canvas.drawText("Pressure", textStart,textHorizontal , titlepaint);
        canvas.drawText(plantReport.getPressure(), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);




        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(2*columnWidth)+(columnWidth/2);
        canvas.drawText("TDS", textStart,textHorizontal , titlepaint);
        canvas.drawText(String.valueOf(plantReport.getTds()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);




        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(2*columnWidth)+(columnWidth/2);
        canvas.drawText("Meter Open", textStart,textHorizontal , titlepaint);
        canvas.drawText(String.valueOf(plantReport.getMeterOpen()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);



        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(2*columnWidth)+(columnWidth/2);
        canvas.drawText("Meter Close", textStart,textHorizontal , titlepaint);
        canvas.drawText(String.valueOf(plantReport.getMeterClose()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);



        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(2*columnWidth)+(columnWidth/2);
        canvas.drawText("Electricity Usage", textStart,textHorizontal , titlepaint);
        canvas.drawText(String.valueOf(plantReport.getUsage()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);


         headings = new ArrayList<>();
        headings.add("Total Amount");
        headings.add("Total Expense");
        headings.add("Net Amount");
        headings.add("Net Water Supply");

        horizontalLineStart = listSize*25 + 110;
        columnWidth = columnWidth+ columnWidth/2;

         textWidth  = 20+(columnWidth)/2;
         textStart = textWidth;
         columnStart = 20;

        canvas.drawLine(20,horizontalLineStart,20,horizontalLineStart+50,line);

        for(int i = 0;i<4;i++){
            columnStart = columnStart+columnWidth;

            canvas.drawText(headings.get(i), textStart,horizontalLineStart+17, date);
            textStart = textStart+columnWidth;
            canvas.drawLine(columnStart,horizontalLineStart,columnStart,horizontalLineStart+50,line);
        }
        canvas.drawLine(20,horizontalLineStart,columnStart,horizontalLineStart,line);
        canvas.drawLine(20,horizontalLineStart+25,columnStart,horizontalLineStart+25,line);
        canvas.drawLine(20,horizontalLineStart+50,columnStart,horizontalLineStart+50,line);

        textStart = textWidth;
        horizontalLineStart = horizontalLineStart+25;
        canvas.drawText(String.valueOf(totalAmount), textStart,horizontalLineStart+17, date);
        canvas.drawText(String.valueOf(totalExpense), textStart+columnWidth,horizontalLineStart+17, date);
        canvas.drawText(String.valueOf(totalAmount-totalExpense), textStart+(2*columnWidth),horizontalLineStart+17, date);
        canvas.drawText(String.valueOf(totalWaterSupply), textStart+(3*columnWidth),horizontalLineStart+17, date);

        pdfDocument.finishPage(page1);


        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();


        Toast.makeText(context,"downloaded in /downloads folder. ",Toast.LENGTH_LONG).show();

//        Intent intent = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            intent = new Intent(Intent.ACTION_VIEW, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
//        }
//        if(intent!=null) {
//
//
////        intent.setType("application/pdf");
//            intent.setType("*/*");
//            activity.startActivity(intent);
//        }




        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application found to open the PDF file.", Toast.LENGTH_LONG).show();
        }


    }
    public  float w(double percentage){
        return (float) (percentage*(float)width)/(float)100;
    }
    public  float h(double percentage){
        return (float) (percentage*(float)height)/(float)100;
    }


}










