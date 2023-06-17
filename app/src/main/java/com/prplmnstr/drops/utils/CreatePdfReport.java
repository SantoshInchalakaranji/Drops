package com.prplmnstr.drops.utils;

import android.app.Activity;
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

    public CreatePdfReport(Context context, Resources resources,Activity activity) {
        this.context = context;
        this.activity = activity;
        this.resources = resources;

    }

    Map<String, Object> map = new HashMap<>();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    List<Unit> units  = new ArrayList<>();

    private List<User> workers = new ArrayList<>();



    PlantReport plantReport = new PlantReport();



    public  void downloadableData(String plantName){



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
                                getTasks(units,Helper.getTodayDateObject(),plantName);
                              //  Toast.makeText(context, "Units data came", Toast.LENGTH_SHORT).show();
                            }else{

                            }
                        }
                    }
                });



    }
    private void getTasks(List<Unit> units, Date date, String plantName)  {
        List<Record> records  = new ArrayList<>();
        Date today = Helper.getTodayDateObject();
        Query query;

        Log.i("TAG", "onComplete: "+String.valueOf(units.size()));
        for(Unit unit : units){


            Log.i("TAG", "today----------------: "+units.get(0).getUnitName());

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
                            workers = task.getResult().toObjects(User.class);
                            getDownloadableAttendance(workers,plantName);
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
                                Attendance attendance  = task.getResult().toObjects(Attendance.class).get(0);
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
                                   // Toast.makeText(context, "All data came", Toast.LENGTH_SHORT).show();
                                    try {
                                        if (ContextCompat.checkSelfPermission(activity,  android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                                        } else {
                                            // Permission already granted, create the PDF
                                            createPdf(map);
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

    private void createPdf(Map<String, Object> map) throws IOException, DocumentException {
        List<Record> records = (List<Record>) map.get(Constants.RECORDS);
        PlantReport plantReport = (PlantReport) map.get(Constants.PLANT_REPORTS);
        List<Attendance> attendances = (List<Attendance>) map.get(Constants.ATTENDANCE);



        String fileName = plantReport.getPlantName()+"_"+Helper.getTodayDateObject().getDateInStringFormat();
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


        int listSize = Math.max(attendances.size() + records.size(), (7 + records.size()));
        int pageHeight = 150 + (listSize)*25;

        PdfDocument.PageInfo mypageinfo = new PdfDocument.PageInfo.Builder(width, pageHeight , 1).create();
        PdfDocument.Page page1 = pdfDocument.startPage(mypageinfo);
        Canvas canvas = page1.getCanvas();

        Date date1 = Helper.getTodayDateObject();
        canvas.drawText("DATE", w(10), h(4), titlepaint);

        //date text
        canvas.drawText(date1.getDateInStringFormat(), w(20), h(4), date);
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
        float reportTableStart = (3*columnWidth)+ 20;
        float reportTableEnd = (5*columnWidth)+ 20;

        float attendaceHeight = horizontalLineStart+25+ (attendances.size()*25);
        float reportTableHeight = horizontalLineStart+25+ (6*25);

        // vertical line 1st
        canvas.drawLine(20,horizontalLineStart,20,attendaceHeight,line);

        //report table
        canvas.drawLine(reportTableStart,horizontalLineStart,reportTableStart,reportTableHeight,line);
        //total water table
        canvas.drawLine(reportTableEnd+(columnWidth/2),horizontalLineStart,reportTableEnd+(columnWidth/2),horizontalLineStart+50,line);

        // vertical line last
        canvas.drawLine(attendaceTableWidthEnd,horizontalLineStart,attendaceTableWidthEnd,attendaceHeight,line);
        // report table
        canvas.drawLine(reportTableEnd,horizontalLineStart,reportTableEnd,reportTableHeight,line);
        //water table
        canvas.drawLine(columnStart,horizontalLineStart,columnStart,horizontalLineStart+50,line);

        //verticle middle
        canvas.drawLine(20+columnWidth,horizontalLineStart,20+columnWidth,attendaceHeight,line);
        //report table
        canvas.drawLine(20+(4*columnWidth),horizontalLineStart,20+(4*columnWidth),reportTableHeight,line);

        //horizontal end
        canvas.drawLine(20,attendaceHeight,attendaceTableWidthEnd,attendaceHeight,line);
        //report table
        canvas.drawLine(reportTableStart,reportTableHeight,reportTableEnd,reportTableHeight,line);
        //total water
        canvas.drawLine(reportTableEnd+(columnWidth/2),horizontalLineStart+50,columnStart,horizontalLineStart+50,line);





        canvas.drawLine(20, horizontalLineStart,attendaceTableWidthEnd , horizontalLineStart, line);
        //report table
        canvas.drawLine(reportTableStart, horizontalLineStart,reportTableEnd , horizontalLineStart, line);
        //total water
        canvas.drawLine(reportTableEnd+(columnWidth/2), horizontalLineStart,columnStart , horizontalLineStart, line);

        float waterText = (columnStart - (reportTableEnd+(columnWidth/2)))/2;
        float waterTextStart = reportTableEnd+(columnWidth/2)+waterText;
        canvas.drawText("Worker", textStart, textHorizontal, date);
        canvas.drawText("Plant Report", textStart+(3*columnWidth), textHorizontal, date);
        canvas.drawText("Total Water Supply", waterTextStart, textHorizontal, date);
        canvas.drawText("Attendance", textStart+columnWidth, textHorizontal, date);
        canvas.drawText("Reading", textStart+(4*columnWidth), textHorizontal, date);
        horizontalLineStart +=25;
        canvas.drawLine(20, horizontalLineStart,attendaceTableWidthEnd , horizontalLineStart, line);
        //report table
        canvas.drawLine(reportTableStart, horizontalLineStart,reportTableEnd , horizontalLineStart, line);
        //water supply
        canvas.drawLine(reportTableEnd+(columnWidth/2), horizontalLineStart,columnStart , horizontalLineStart, line);

        textHorizontal = horizontalLineStart+17;

        // total water
        canvas.drawText(String.valueOf(totalWaterSupply), waterTextStart, textHorizontal, date);


        float reportTableHorzontalLineStart = horizontalLineStart;
        // attendances

        for(Attendance attendance : attendances){
            String attend;
            if(attendance.getAttendance()==0){
                attend = "P";
            }else if(attendance.getAttendance()==1){
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



        textHorizontal = reportTableHorzontalLineStart+17;

            textStart = textWidth+(3*columnWidth);
            canvas.drawText("Flow", textStart,textHorizontal , titlepaint);
            canvas.drawText(String.valueOf(plantReport.getFlow()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
            canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);



        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(3*columnWidth);
        canvas.drawText("Pressure", textStart,textHorizontal , titlepaint);
        canvas.drawText(plantReport.getPressure(), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);




        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(3*columnWidth);
        canvas.drawText("TDS", textStart,textHorizontal , titlepaint);
        canvas.drawText(String.valueOf(plantReport.getTds()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);




        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(3*columnWidth);
        canvas.drawText("Meter Open", textStart,textHorizontal , titlepaint);
        canvas.drawText(String.valueOf(plantReport.getMeterOpen()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);



        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(3*columnWidth);
        canvas.drawText("Meter Close", textStart,textHorizontal , titlepaint);
        canvas.drawText(String.valueOf(plantReport.getMeterClose()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);



        textHorizontal = reportTableHorzontalLineStart+17;

        textStart = textWidth+(3*columnWidth);
        canvas.drawText("Electricity Usage", textStart,textHorizontal , titlepaint);
        canvas.drawText(String.valueOf(plantReport.getUsage()), textStart+columnWidth, textHorizontal, titlepaint);
        reportTableHorzontalLineStart += 25;
        canvas.drawLine(reportTableStart, reportTableHorzontalLineStart,reportTableEnd , reportTableHorzontalLineStart, line);





        pdfDocument.finishPage(page1);


        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();


        Toast.makeText(context,"downloaded ",Toast.LENGTH_LONG).show();



    }
    public  float w(double percentage){
        return (float) (percentage*(float)width)/(float)100;
    }
    public  float h(double percentage){
        return (float) (percentage*(float)height)/(float)100;
    }


}














//    private void createpdf(String homeID,String bill_NO,String date,long items,List<String> name_list,
//                           List<String> mrp_list,List<String> price_list,List<String> qty_list,
//                           List<String> amt_list) {
//        int serial = 0;
//        int page_width=400,page_height_base=230,space1=30 ,space2 = 60,page_end = 160;
//        int pageheight = (int) (page_end+page_height_base+(items*space2));
//
//        ///created a new folder
//
//        File path = Environment.getExternalStorageDirectory();
//        File dir = new File(path.getAbsolutePath()+"/Bill/");
//
//        if(!dir.exists()){
//            dir.mkdir();
//        }
//        /////
//
//
//
//
//
//        PdfDocument pdfDocument = new PdfDocument();
//
//
//        /////paints
//
//
//        Paint paint = new Paint();
//
//
//
//        //title
//        Paint titlepaint = new Paint();
//        titlepaint.setTextAlign(Paint.Align.CENTER);
//        titlepaint.setTextSize(32);
//        titlepaint.setTypeface(ResourcesCompat.getFont(context, R.font.audiowide));
//
//
//        //slogan
//        Paint slogan_paint = new Paint();
//        slogan_paint.setTextAlign(Paint.Align.CENTER);
//        slogan_paint.setTextSize(16);
//        slogan_paint.setTypeface(ResourcesCompat.getFont(context, R.font.questrial));
//
//        //header left
//        Paint header_paint_left = new Paint();
//        header_paint_left.setTextAlign(Paint.Align.LEFT);
//        header_paint_left.setTextSize(16);
//        header_paint_left.setTypeface(ResourcesCompat.getFont(context, R.font.f25_bank_printer_regular));
//
//        //header right
//        Paint header_paint_right = new Paint();
//        header_paint_right.setTextAlign(Paint.Align.RIGHT);
//        header_paint_right.setTextSize(16);
//        header_paint_right.setTypeface(ResourcesCompat.getFont(context, R.font.f25_bank_printer_regular));
//
//
//
//        //lines
//        Paint dash = new Paint();
//        dash.setTextAlign(Paint.Align.CENTER);
//        dash.setTextSize(16);
//        dash.setTypeface(ResourcesCompat.getFont(context, R.font.f25_bank_printer_regular));
//
//        ///sl no
//        Paint sl_no = new Paint();
//        sl_no.setTextAlign(Paint.Align.LEFT);
//        sl_no.setTextSize(16);
//        sl_no.setTypeface(ResourcesCompat.getFont(context, R.font.f25_bank_printer_regular));
//
//        ///Item
//        item = new Paint();
//        item.setTextAlign(Paint.Align.LEFT);
//        item.setTextSize(16);
//        item.setTypeface(ResourcesCompat.getFont(context, R.font.f25_bank_printer_regular));
//
//        ///total
//
//
//        ///Price * qty
//        Paint price = new Paint();
//        price.setTextAlign(Paint.Align.CENTER);
//        price.setTextSize(16);
//        price.setTypeface(ResourcesCompat.getFont(context, R.font.f25_bank_printer_regular));
//        PdfDocument.PageInfo mypageinfo = new PdfDocument.PageInfo.Builder(400, pageheight , 1).create();
//        PdfDocument.Page page1 = pdfDocument.startPage(mypageinfo);
//        canvas = page1.getCanvas();
//
//
//
//
//
//        canvas.drawText("FRESH MART", page_width/2, 50, titlepaint);
//        canvas.drawText("groceries at your doorstep",page_width/2,70,slogan_paint);
//        canvas.drawText("contact : 8904212184",page_width/2,85,slogan_paint);
//        canvas.drawText("freshmart.farm.r@gmail.com",page_width/2,100,slogan_paint);
//        canvas.drawText("Bill No:"+bill_NO,10,125,header_paint_left);
//        canvas.drawText("Home ID:"+homeID,390,125,header_paint_right);
//        canvas.drawText("Date:"+date,10,145,header_paint_left);
//        canvas.drawText("--------------------------------------",200,165,dash);
//        canvas.drawText("Sl.No",10,185,sl_no);
//        canvas.drawText("Item Name",100,185,item);
//        canvas.drawText("Mrp",10,205,sl_no);
//        canvas.drawText("Price*qty",200,205,price);
//        canvas.drawText("Amt",390,205,header_paint_right);
//        canvas.drawText("--------------------------------------",page_width/2,225,dash);
//        int total_price=0,total_mrp=0;
//        for( int i = 0; i<name_list.size();i++){
//
//            total_price = total_price+(Integer.parseInt(price_list.get(i))*Integer.parseInt(qty_list.get(i)));
//            total_mrp = total_mrp+(Integer.parseInt(mrp_list.get(i))*Integer.parseInt(qty_list.get(i)));
//            serial = serial+1;
//            canvas.drawText(String.valueOf(serial)+")",10,page_height_base+space1,sl_no);
//            canvas.drawText(name_list.get(i),50,page_height_base+space1,item);
//            canvas.drawText(mrp_list.get(i),10,page_height_base+space2,sl_no);
//            canvas.drawText(price_list.get(i)+"*"+qty_list.get(i),page_width/2,page_height_base+space2,price);
//            canvas.drawText(amt_list.get(i),390,page_height_base+space2,header_paint_right);
//
//            page_height_base = page_height_base+space2;
//        }
//
//        canvas.drawText("--------------------------------------",page_width/2,page_height_base+20,dash);
//        canvas.drawText("Total:₹"+String.valueOf(total_mrp),10,page_height_base+50,header_paint_left);
//        canvas.drawText("Delivery Charge:₹"+(charge),390,page_height_base+50,header_paint_right);
//        canvas.drawText("Payable:₹"+String.valueOf(total_price+Integer.parseInt(charge)),390,page_height_base+70,header_paint_right);
//        canvas.drawText("FM price:₹"+String.valueOf(total_price),10,page_height_base+70,header_paint_left);
//        canvas.drawText("saving:₹"+String.valueOf(total_mrp-total_price),10,page_height_base+90,header_paint_left);
//        canvas.drawText("--------------------------------------",page_width/2,page_height_base+130,dash);
//        canvas.drawText("***Thank You***",page_width/2,page_height_base+160,dash);
//
//
//
//
//        pdfDocument.finishPage(page1);
//
//        /// addede to dir folder
//
//
//        File file = new File(dir+ "/"+homeID+date+".pdf");
//
//        try {
//            pdfDocument.writeTo(new FileOutputStream(file));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        pdfDocument.close();
//        Toast.makeText(context,"Bill Saved",Toast.LENGTH_LONG).show();
//
//        ////bill no upadate
//
//        int s  = Integer.parseInt(bill_no)+1;
//        DatabaseReference up = FirebaseDatabase.getInstance().getReference("bill_number");
//        up.setValue(String.valueOf(s));
//
//
//    }
//








//        try {
//                // Create a PdfWriter instance to write the document to a file or output stream
//                PdfWriter pdfWriter =    PdfWriter.getInstance(document, new FileOutputStream(file));
//                document.addWriter(pdfWriter);
//                }catch (Exception e){
//                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
//                }
//
//                document.open();
//
//        // Create the table with 7 columns
//        PdfPTable table = new PdfPTable(7);
//
//        // Add table headers
//        PdfPCell cell1 = new PdfPCell(new Phrase("Unit Name" ));
//        PdfPCell cell2 = new PdfPCell(new Phrase("Opening"));
//        PdfPCell cell3 = new PdfPCell(new Phrase("Closing"));
//        PdfPCell cell4 = new PdfPCell(new Phrase("Total Amount"));
//        PdfPCell cell5 = new PdfPCell(new Phrase("Water Opening"));
//        PdfPCell cell6 = new PdfPCell(new Phrase("Water Closing"));
//        PdfPCell cell7 = new PdfPCell(new Phrase("Water Dispense"));
//
//
//
//        // Add cells to the table header
//        table.addCell(cell1);
//        table.addCell(cell2);
//        table.addCell(cell3);
//        table.addCell(cell4);
//        table.addCell(cell5);
//        table.addCell(cell6);
//        table.addCell(cell7);
//
//        // Add table rows
//        for (Record record : records) {
//        table.addCell(record.getUnitName());
//        table.addCell(String.valueOf(record.getOpening()));
//        table.addCell(String.valueOf(record.getClosing()));
//        table.addCell(String.valueOf(record.getAmount()));
//        table.addCell(String.valueOf(record.getWaterOpen()));
//        table.addCell(String.valueOf(record.getClosing()));
//        table.addCell(String.valueOf(record.getWaterSupply()));
//        }
//
//        PdfPTable table2 = new PdfPTable(2);
//        PdfPCell cell11 = new PdfPCell(new Phrase("Worker" ));
//        PdfPCell cell22 = new PdfPCell(new Phrase("Attendance"));
//
//        table2.addCell(cell11);
//        table2.addCell(cell22);
//
//
//        for (Attendance attendance : attendances) {
//        table2.addCell(attendance.getUserName());
//        if(attendance.getAttendance()==Constants.PRESENT){
//        table2.addCell("Present");
//        }else{
//        table2.addCell("Absent");
//        }
//
//
//
//        }
//
//
//        PdfPTable plantTable = new PdfPTable(3);
//
//        PdfPCell cell111 = new PdfPCell(new Phrase("Flow" ));
//        PdfPCell cell222 = new PdfPCell(new Phrase("Pressure"));
//        PdfPCell cell333 = new PdfPCell(new Phrase("TDS"));
//
//
//        plantTable.addCell(cell111);
//        plantTable.addCell(cell222);
//        plantTable.addCell(cell333);
//        plantTable.addCell(String.valueOf(plantReport.getFlow()));
//        plantTable.addCell(String.valueOf(plantReport.getPressure()));
//        plantTable.addCell(String.valueOf(plantReport.getTds()));
//
//
//
//        // Add the table to the document
//        document.add(table);
//
//        document.close();

