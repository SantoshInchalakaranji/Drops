package com.prplmnstr.drops.views.worker;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.AttendanceRvAdapter;
import com.prplmnstr.drops.adapters.DashboardRecyclerAdapter;
import com.prplmnstr.drops.databinding.AddExpenseDialogBinding;
import com.prplmnstr.drops.databinding.AddPlantReportDialogBinding;
import com.prplmnstr.drops.databinding.AddRecordDialogBinding;
import com.prplmnstr.drops.databinding.AttendanceItemBinding;
import com.prplmnstr.drops.databinding.CalenderDialogBinding;
import com.prplmnstr.drops.databinding.FragmentWorkerTaskBinding;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Expense;
import com.prplmnstr.drops.models.PlantReport;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.CreatePdfReport;
import com.prplmnstr.drops.utils.Helper;
import com.prplmnstr.drops.viewModel.admin.DashboardViewModel;
import com.prplmnstr.drops.viewModel.worker.TaskFragmentViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerTaskFragment extends Fragment implements NavController.OnDestinationChangedListener {

    private FragmentWorkerTaskBinding binding;
    private Dialog calenderDialog;
    private Dialog addExpenseDailog;
    private AddExpenseDialogBinding addExpenseDialogBinding;
    private Dialog addPlantReportDailog;
    private AddPlantReportDialogBinding addPlantReportDialogBinding;
    private CalenderDialogBinding calenderDialogBinding;
    private TaskFragmentViewModel viewModel;
    private  DashboardViewModel dashboardViewModel;
    private List<Record> records = new ArrayList<>();
    private List<RecyclerModel> recyclerItems = new ArrayList<>();
    private List<RecyclerModel> expenseRecyclerItems = new ArrayList<>();
    private DashboardRecyclerAdapter expenseAdapter;
    private  int collection;
    private  int dayExpense=0;
    private RecyclerView expenseRV;
    private List<String> plants = new ArrayList<>();
    private RecyclerView recyclerView;

    private DashboardRecyclerAdapter adapter;

    private Dialog loader;
    private RecyclerView attendanceRV;
    private AttendanceRvAdapter attendancdeRvAdapter;
    private Dialog editRecordDialog;
    private ArrayAdapter spinnerAdapter;
    private AddRecordDialogBinding addRecordDialogBinding;
    private int taskCount;
    private List<Attendance> attendanceList = new ArrayList<>();
    private String PLANT_NAME;
    private  PlantReport newPlantReport;
    List<Expense> expenses = new ArrayList<>();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkerTaskBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize_loader();
        loader.show();
        initialize_recycler();

        NavController navController = Navigation.findNavController(view);
        navController.addOnDestinationChangedListener(this);



      loadUI();

      binding.downloadButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Toast.makeText(getContext(), "Downloading...", Toast.LENGTH_SHORT).show();
              CreatePdfReport createPdfReport = new CreatePdfReport(getContext(),getResources(),getActivity());

              createPdfReport.downloadableData(PLANT_NAME,Helper.getTodayDateObject());
          }
      });

      binding.newExpenseTV.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
             loadExpenseDialog();
          }
      });

        binding.newPlantReportTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAddPlantReportDailog();
            }
        });

        binding.logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.logout_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        FirebaseAuth firebaseAuth =   FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();



//                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE, getContext().MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.remove(Constants.SAVED_USER_TYPE);
//                            editor.apply();




                        navController.navigate(R.id.action_workerTaskFragment_to_mainActivity);
                        getActivity().finish();
//                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
                        return true;
                    }
                });
            }
        });


    }

    private void loadExpenseDialog() {

        addExpenseDialogBinding = addExpenseDialogBinding.inflate(LayoutInflater.from(getContext()));


        addExpenseDailog = new Dialog(getActivity());
        addExpenseDailog.setContentView(addExpenseDialogBinding.getRoot());
        addExpenseDailog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        addExpenseDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addExpenseDailog.setCancelable(true);
        addExpenseDailog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;



        addExpenseDailog.show();

        addExpenseDialogBinding.dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = addExpenseDialogBinding.titleET.getText().toString().trim();
                String amount = addExpenseDialogBinding.amountET.getText().toString().trim();

                if(title.isEmpty() || amount.isEmpty()){
                    Toast.makeText(getContext(), "Fields can't be empty", Toast.LENGTH_SHORT).show();

                }else{
                    Date today= Helper.getTodayDateObject();
                    Expense expense = new Expense(PLANT_NAME,title,Integer.parseInt(amount),
                            today.getDay(), today.getMonth() ,today.getYear());
                    saveExpenses(expense);
                }
            }
        });
    }


    private void loadAddPlantReportDailog() {

        addPlantReportDialogBinding = AddPlantReportDialogBinding.inflate(LayoutInflater.from(getContext()));


        addPlantReportDailog = new Dialog(getActivity());
        addPlantReportDailog.setContentView(addPlantReportDialogBinding.getRoot());
        addPlantReportDailog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        addPlantReportDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addPlantReportDailog.setCancelable(true);
        addPlantReportDailog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;
        addPlantReportDialogBinding.meterOpen.setText(String.valueOf(newPlantReport.getMeterClose()));


        addPlantReportDailog.show();



        addPlantReportDialogBinding.dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flow =    addPlantReportDialogBinding.flow.getText().toString();
                String tds =    addPlantReportDialogBinding.tds.getText().toString();
                String pressure =    addPlantReportDialogBinding.pressure.getText().toString();
                String meterOpen =    addPlantReportDialogBinding.meterOpen.getText().toString();
                String meterClose =    addPlantReportDialogBinding.meterClose.getText().toString();
              //  String expense =    addPlantReportDialogBinding.expense.getText().toString();


                if(flow.isEmpty() || tds.isEmpty() || pressure.isEmpty() ||
                        meterOpen.isEmpty() || meterClose.isEmpty() ){
                    Toast.makeText(getActivity(), "Fields cant be empty", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Date today =  Helper.getTodayDateObject();
                    int  usage = Math.abs(Integer.parseInt(meterClose)-Integer.parseInt(meterOpen));

                    newPlantReport.setDay(today.getDay());
                    newPlantReport.setMonth(today.getMonth());
                    newPlantReport.setYear(today.getYear());
                    newPlantReport.setFlow(Integer.parseInt(flow));
                  //  newPlantReport.setExpense(Integer.parseInt(expense));
                    newPlantReport.setPressure(pressure);
                    newPlantReport.setTds( Integer.parseInt(tds));
                    newPlantReport.setMeterOpen(Integer.parseInt(meterOpen));
                    newPlantReport.setMeterClose(Integer.parseInt(meterClose));
                    newPlantReport.setUsage(usage);


                    binding.flowNumber.setText(String.valueOf(newPlantReport.getFlow()));
                    binding.tdsNumber.setText(String.valueOf(newPlantReport.getTds()));
                    binding.pressureNumber.setText(newPlantReport.getPressure());
                    binding.plantReportDate.setText(today.getDateInStringFormat());

                    binding.meterCloseNumber.setText(meterClose);
                    binding.usageNumber.setText(String.valueOf(usage));
                    dashboardViewModel.savePlantReport(newPlantReport,getContext());
                    Toast.makeText(getActivity(), "Adding report...", Toast.LENGTH_SHORT).show();
                    addPlantReportDailog.dismiss();
                }
            }
        });
    }

    private void initialize_recycler() {
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new DashboardRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setListener(new DashboardRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerModel recyclerModel, int clickPosition) {

                loadDialog(records.get(clickPosition),clickPosition);
            }

            @Override
            public void onItemLongClick(RecyclerModel recyclerModel, int clickPosition) {

            }
        });





    }
    public void initialize_loader() {
        loader = new Dialog(getActivity());
        loader.setContentView(R.layout.loader);
        loader.getWindow().setBackgroundDrawableResource(R.color.transparent);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loader.setCancelable(false);
        loader.getWindow().getAttributes().windowAnimations = R.style.animation;
    }
    private void set_spinner(List<String> plants) {
        // set spinner
        spinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item,
                plants);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(spinnerAdapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loader.show();

                 PLANT_NAME = plants.get(i);
               load_recycler_items(PLANT_NAME);
               loadAttendance(PLANT_NAME);
               loadPlantReport(PLANT_NAME);
                loadExpenses(PLANT_NAME);
                Toast.makeText(getActivity(), "Loading please wait...", Toast.LENGTH_LONG).show();
            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    public void saveExpenses(Expense expense){
        expenses.add(expense);
        String drawableName = "expense"; // Replace with the name of your drawable
        int expenseResourceId = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
        expenseRecyclerItems = viewModel.getRecycleItemsOfExpense(expenses,getResources(),expenseResourceId);

        expenseAdapter.setRecyclerItems(expenseRecyclerItems);
         viewModel.getSumOfExpenses().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer sum) {
                binding.expenseNumber.setText(String.valueOf(sum));
                binding.totalExpenseNumber.setText(String.valueOf(sum));
                dayExpense = sum;
                binding.inHandNumber.setText("₹" + String.valueOf(collection - sum));
            }
        });

        addExpenseDailog.dismiss();
        viewModel.saveExpense(expense, getContext());
    }
    private void loadExpenses(String plantName) {
        // expense recycler
        expenseRV = binding.expenseRV;
        expenseRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        expenseRV.setHasFixedSize(true);
        expenseAdapter = new DashboardRecyclerAdapter();
        expenseRV.setAdapter(expenseAdapter);
        viewModel.getExpenses(plantName).observe(getViewLifecycleOwner(), new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenseList) {
                if (expenseList != null) {
                    if (!expenseList.isEmpty()) {
                        expenses = expenseList;
                        String drawableName = "expense"; // Replace with the name of your drawable
                        int expenseResourceId = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
                        expenseRecyclerItems = viewModel.getRecycleItemsOfExpense(expenses, getResources(), expenseResourceId);

                        expenseAdapter.setRecyclerItems(expenseRecyclerItems);
                        viewModel.getSumOfExpenses().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer sum) {
                                binding.expenseNumber.setText(String.valueOf(sum));
                                binding.totalExpenseNumber.setText(String.valueOf(sum));
                                dayExpense = sum;
                                binding.inHandNumber.setText("₹" + String.valueOf(collection - sum));
                            }
                        });




                } else {
                        expenseRecyclerItems.clear();
                        expenseAdapter.setRecyclerItems(expenseRecyclerItems);
                    binding.expenseNumber.setText("0");
                    binding.totalExpenseNumber.setText("0");
                    binding.inHandNumber.setText(String.valueOf(collection));
                }
            }else{
                    expenseRecyclerItems.clear();
                    expenseAdapter.setRecyclerItems(expenseRecyclerItems);
                    binding.expenseNumber.setText("0");
                    binding.totalExpenseNumber.setText("0");
                    binding.inHandNumber.setText(String.valueOf(collection));
                }

                loader.dismiss();
            }
        });
    }


    private void loadPlantReport(String plantName) {
        dashboardViewModel.getPlantReport(plantName).observe(getViewLifecycleOwner(), new Observer<PlantReport>() {
            @Override
            public void onChanged(PlantReport plantReport) {
                if(plantReport!= null){
                    newPlantReport = plantReport;
                    binding.flowNumber.setText(String.valueOf(plantReport.getFlow()));
                    binding.tdsNumber.setText(String.valueOf(plantReport.getTds()));
                    binding.pressureNumber.setText(plantReport.getPressure());
                    binding.plantReportDate.setText(Helper.getDateInStringFormat(
                            plantReport.getDay(), plantReport.getMonth(), plantReport.getYear()
                    ));

                    binding.meterCloseNumber.setText(String.valueOf(plantReport.getMeterClose()));
                    binding.usageNumber.setText(String.valueOf(plantReport.getUsage()));
                }
            }
        });
    }
    private void loadDialog(Record record, int clickPosition) {

        addRecordDialogBinding = AddRecordDialogBinding.inflate(LayoutInflater.from(getContext()));


        editRecordDialog = new Dialog(getActivity());
        editRecordDialog.setContentView(addRecordDialogBinding.getRoot());
        editRecordDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        editRecordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        editRecordDialog.setCancelable(true);
        editRecordDialog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;

       addRecordDialogBinding.unitNameTV.setText(record.getUnitName());
       addRecordDialogBinding.openingET.setText(String.valueOf(record.getClosing()));

        addRecordDialogBinding.waterOpening.setText(String.valueOf(record.getWaterClose()));
        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Integer closeInt;
                if(s!=null){
                    String closeString = s.toString();
                    if(!closeString.isEmpty()){
                      closeInt   = Integer.parseInt(closeString);
                    }else{
                        closeInt = 0;
                    }

                    Integer amount = Math.abs(closeInt-Integer.valueOf(
                            addRecordDialogBinding.openingET.getText().toString()
                    ));
                    addRecordDialogBinding.totalAmountTV.setText(String.valueOf(amount.toString()));
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }


        };

        addRecordDialogBinding.closingET.addTextChangedListener(inputTextWatcher);
        if(record.getType().equals(Constants.RECHARGE_UNIT)){
            addRecordDialogBinding.waterClosing.setText("0");
            addRecordDialogBinding.waterOpening.setText("0");
            addRecordDialogBinding.wateLayout.setVisibility(View.GONE);

        }else {
            addRecordDialogBinding.wateLayout.setVisibility(View.VISIBLE);
        }

        addRecordDialogBinding.dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String open =  addRecordDialogBinding.openingET.getText().toString();
                String close =  addRecordDialogBinding.closingET.getText().toString();
                String waterOpen =  addRecordDialogBinding.waterOpening.getText().toString();
                String waterClose =  addRecordDialogBinding.waterClosing.getText().toString();
                
                if(open.isEmpty() || close.isEmpty()|| waterOpen.isEmpty()|| waterClose.isEmpty()){
                    Toast.makeText(getActivity(), "Enter All Data", Toast.LENGTH_SHORT).show();
                }else{
                    Date  today = Helper.getTodayDateObject();
                   // String recordDate = Helper.getDateInStringFormat(record.getDay(),record.getMonth(),record.getYear());
                    int amount = (Math.abs(Integer.parseInt(close)-Integer.parseInt(open)));
                    int waterSupply = (Math.abs(Integer.parseInt(waterClose)-Integer.parseInt(waterOpen)));
                    
                    
                    record.setAmount(amount);
                    record.setOpening(Integer.parseInt(open));
                    record.setClosing(Integer.parseInt(close));
                    record.setWaterSupply(waterSupply);
                    record.setWaterOpen(Integer.parseInt(waterOpen));
                    record.setWaterClose(Integer.parseInt(waterClose));
                    record.setDay(today.getDay());
                    record.setMonth(today.getMonth());
                    record.setYear(today.getYear());
                    loader.show();
                   viewModel.addRecord(record,getContext()).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                       @Override
                       public void onChanged(Boolean result) {
                           loader.dismiss();
                           if(result){


                              reloadOnEdit(record, clickPosition);
                           }else{

                           }
                           editRecordDialog.dismiss();
                       }
                   });
                }
            }
        });

        editRecordDialog.show();
    }



    private void load_recycler_items(String plantName) {

        viewModel.getRecords(plantName)
                .observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
                    @Override
                    public void onChanged(List<Record> recordList) {
//                        records.clear();
//                        if(recordList==null || recordList.isEmpty()){
//                            Toast.makeText(getContext(), "No units addad yet", Toast.LENGTH_SHORT).show();
//                            loader.dismiss();
//                           // adapter.setRecyclerItems();
//                            return;
//                        }
                        records = recordList;
                        String drawableName = "checkmark"; // Replace with the name of your drawable
                        int checkMarkResourseId = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
                        recyclerItems = viewModel.getRecycleItems(records,getResources(),checkMarkResourseId);

                        adapter.setRecyclerItems(recyclerItems);

                        binding.container.setVisibility(View.VISIBLE);



                        viewModel.getTaskCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                taskCount = integer;
                                binding.percentageTV.setText(taskCount+"/"+recyclerItems.size());
                                binding.linearProgressBar.setProgress(taskCount);
                                binding.linearProgressBar.setMax(recordList.size());

                            }
                        });

                        viewModel.getCollection().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                collection = integer;
                                binding.inHandNumber.setText("₹" + String.valueOf(collection - dayExpense));
                                binding.totalAmountTV.setText("₹ "+integer);
                                binding.totalCashNumber.setText("₹"+integer);

                            }
                        });
                        viewModel.getWaterSupply().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                binding.waterDispenseTV.setText("Water dispense(L) : "+integer);
                            }
                        });

                        binding.dayTV.setText(Helper.getTodayDateObject().getDateInStringFormat());

                    }
                });
    }



    private void loadAttendance(String plantName) {

        attendanceRV = binding.attendanceRV;
        attendanceRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        attendanceRV.setHasFixedSize(true);
        attendancdeRvAdapter = new AttendanceRvAdapter();
        attendanceRV.setAdapter(attendancdeRvAdapter);




        attendancdeRvAdapter.setListener(new AttendanceRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Attendance attendance, int clickPosition) {
                loadAttendanceDailog(attendance);
            }

            @Override
            public void onPresentClick(Attendance attendance, int position, AttendanceItemBinding attendanceItemBinding) {
                Toast.makeText(getContext(), "Registering attendance please wait...", Toast.LENGTH_LONG).show();
                attendancdeRvAdapter.presentClick(attendanceItemBinding);
                attendance.setAttendance(Constants.PRESENT);
                attendanceList.get(position).setAttendance(Constants.PRESENT);
                displayTotalAttendance(attendanceList);

                dashboardViewModel.addAttendance(attendance,getContext());
            }

            @Override
            public void onAbsentClick(Attendance attendance,int postition,AttendanceItemBinding attendanceItemBinding) {

                attendancdeRvAdapter.absentClick(attendanceItemBinding);
                attendance.setAttendance(Constants.ABSENT);
                attendanceList.get(postition).setAttendance(Constants.ABSENT);


                displayTotalAttendance(attendanceList);

                Toast.makeText(getContext(), "Registering attendance please wait...", Toast.LENGTH_LONG).show();
                dashboardViewModel.addAttendance(attendance,getContext());
            }

            @Override
            public void onDeleteRequest(Attendance attendance) {


            }
        });

        dashboardViewModel.getAttendances(plantName).observe(getViewLifecycleOwner(), new Observer<List<Attendance>>() {
            @Override
            public void onChanged(List<Attendance> attendances) {
                if(attendances!= null){
                    attendanceList = attendances;
                    displayTotalAttendance(attendances);
                }


                attendancdeRvAdapter.setResources(getResources());

                attendancdeRvAdapter.setAttendanceList(attendanceList);

            }
        });
    }

    private void loadAttendanceDailog(Attendance attendance) {
        loader.show();
        calenderDialogBinding = CalenderDialogBinding.inflate(LayoutInflater.from(getContext()));


        calenderDialog = new Dialog(getActivity());
        calenderDialog.setContentView(calenderDialogBinding.getRoot());
        calenderDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        calenderDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        calenderDialog.setCancelable(true);
        calenderDialog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;

        calenderDialogBinding.unitNameTV.setText(attendance.getUserName());
        calenderDialogBinding.calender.setSelectionMode (MaterialCalendarView.SELECTION_MODE_NONE);

        dashboardViewModel.getAttendanceOfUser(attendance).observe(getViewLifecycleOwner(), new Observer<List<Attendance>>() {
            @Override
            public void onChanged(List<Attendance> attendances) {
                loader.dismiss();
                if(attendances!=null){

                    for(Attendance data : attendances){

                        CalendarDay calendarDay = CalendarDay.from(data.getYear(), data.getMonth(), data.getDay());
                        calenderDialogBinding.calender.setDateSelected(calendarDay,true);
                    }
                }else{
                    Toast.makeText(getActivity(), "Failed to load attendance record", Toast.LENGTH_SHORT).show();
                }
            }
        });

        calenderDialog.show();
    }

    private void displayTotalAttendance(List<Attendance> attendances) {
        Map<String,String> map = new HashMap<>();
        map = dashboardViewModel.getSumOfAttendances(attendances);
        binding.presentNumber.setText(map.get("present"));
        binding.absentNumber.setText(map.get("absent"));
    }
    public void reloadOnEdit(Record record, int clickPosition){
      records.set(clickPosition,record);

        String drawableName = "checkmark"; // Replace with the name of your drawable
        int checkMarkResourseId = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
        recyclerItems = viewModel.getRecycleItems(records,getResources(),checkMarkResourseId);

        adapter.setRecyclerItems(recyclerItems);


        loader.dismiss();


        viewModel.getTaskCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                taskCount = integer;
                binding.percentageTV.setText(taskCount+"/"+recyclerItems.size());
                binding.linearProgressBar.setProgress(taskCount);
                binding.linearProgressBar.setMax(records.size());

            }
        });

        viewModel.getCollection().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.totalAmountTV.setText("₹ "+integer);
            }
        });
        viewModel.getWaterSupply().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.waterDispenseTV.setText("Water dispense(L) : "+integer);
            }
        });

        binding.dayTV.setText(Helper.getTodayDateObject().getDateInStringFormat());
    }
    private void loadUI() {
//        viewModel.getPlants().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
//            @Override
//            public void onChanged(List<String> plantList) {
//
//                plants = plantList;
//                set_spinner(plants);
//            }
//        });


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String user = firebaseAuth.getCurrentUser().getEmail().toString();
        List<String> plants = new ArrayList<>();
        firebaseFirestore.collection(Constants.WORKER)
                .whereEqualTo("email", user)
                .limit(1)
                .get()


                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot.isEmpty()) {

                                return;
                            }
                            for (DocumentSnapshot document : task.getResult()) {
                                // Retrieve the value of the desired field from the document
                                String fieldValue = document.get("plantName").toString();

                                // Add the field value to the list
                                plants.add(fieldValue);



                            }

                            set_spinner(plants);

                        } else {
                            Toast.makeText(getContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(TaskFragmentViewModel.class);


        dashboardViewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(DashboardViewModel.class);

//        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                // Navigate to the previous fragment
//              getActivity().finish();
//            }
//        });
//
   }


    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {

    }
}