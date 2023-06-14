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

