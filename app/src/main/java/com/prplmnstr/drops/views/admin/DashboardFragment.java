package com.prplmnstr.drops.views.admin;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import android.Manifest;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.AttendanceRvAdapter;
import com.prplmnstr.drops.adapters.DashboardRecyclerAdapter;
import com.prplmnstr.drops.databinding.AddPlantReportDialogBinding;
import com.prplmnstr.drops.databinding.AddUnitDialogBinding;
import com.prplmnstr.drops.databinding.AttendanceItemBinding;
import com.prplmnstr.drops.databinding.CalenderDialogBinding;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Expense;
import com.prplmnstr.drops.models.PlantReport;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.CreatePdfReport;
import com.prplmnstr.drops.utils.Helper;
import com.prplmnstr.drops.viewModel.admin.DashboardViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashboardFragment extends Fragment implements NavController.OnDestinationChangedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentDashboardBinding binding;
    private AttendanceRvAdapter adapter;
    private RecyclerView attendanceRV;
    private DashboardViewModel viewModel;
    private ArrayAdapter spinnerAdapter;
    private Dialog loader;
    private Dialog addUnitDialog;

    private Dialog addPlantReportDailog;
    private AddPlantReportDialogBinding addPlantReportDialogBinding;
    private Dialog calenderDialog;
    private CalenderDialogBinding calenderDialogBinding;
    private AddUnitDialogBinding addUnitDialogBinding;
    private BottomNavigationView bottomNavigationView;
    private  PlantReport newPlantReport;



    List<Expense> expenses = new ArrayList<>();
    private RecyclerView expenseRV;
    private List<RecyclerModel> expenseRecyclerItems = new ArrayList<>();
    private DashboardRecyclerAdapter expenseAdapter;
    private  int collection;
    private int dayExpense = 0;
    private  int monthlyExpense;


    // adding unit variables
    private String unitType, unitName;
    private int opening, waterOpening;

    private NavController navController;
    public static String PLANT_NAME;
    private List<Record> todayRecords = new ArrayList<>();
    private List<Attendance> attendanceList = new ArrayList<>();
    private int noOfUits;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);



        navController = Navigation.findNavController(view);
        navController.addOnDestinationChangedListener(this);


        bottomNavigationView = requireActivity().findViewById(R.id.navigationBar);

        // Set the OnNavigationItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        PLANT_NAME = DashboardFragmentArgs.fromBundle(getArguments()).getPlant();






        try {
            initialize_loader();
            loader.show();


            checkUnitSize(PLANT_NAME);


            binding.dashboardTV.setText(PLANT_NAME);
            binding.addUnitImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    loadDialog();
                }
            });


            binding.noUnitAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    loadDialog();
                }
            });

            binding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navController.navigate(R.id.action_dashboardFragment_to_dailyInfoFragment);
                }
            });

            binding.seeAllTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navController.navigate(R.id.action_dashboardFragment_to_dailyInfoFragment);
                }
            });

            binding.newPlantReportTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadAddPlantReportDailog(newPlantReport);
                }
            });

            binding.backButtonDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavDirections action = DashboardFragmentDirections.actionDashboardFragmentToHomeFragment();
                    navController.navigate(action);
                }
            });


            binding.downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                   CreatePdfReport createPdfReport = new CreatePdfReport(getContext(),getResources(),getActivity());
                    createPdfReport.downloadableData(PLANT_NAME,Helper.getTodayDateObject());



                }
            });






        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


//


    }

    private void loadAddPlantReportDailog(PlantReport plantReport) {

        addPlantReportDialogBinding = AddPlantReportDialogBinding.inflate(LayoutInflater.from(getContext()));


        addPlantReportDailog = new Dialog(getActivity());
        addPlantReportDailog.setContentView(addPlantReportDialogBinding.getRoot());
        addPlantReportDailog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        addPlantReportDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addPlantReportDailog.setCancelable(true);
        addPlantReportDailog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;
        addPlantReportDialogBinding.meterOpen.setText(String.valueOf(plantReport.getMeterClose()));


        addPlantReportDailog.show();



//        addPlantReportDialogBinding.dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//             String flow =    addPlantReportDialogBinding.flow.getText().toString();
//             String tds =    addPlantReportDialogBinding.tds.getText().toString();
//             String pressure =    addPlantReportDialogBinding.pressure.getText().toString();
//             String meterOpen =    addPlantReportDialogBinding.meterOpen.getText().toString();
//             String meterClose =    addPlantReportDialogBinding.meterClose.getText().toString();
//          //   String expense =    addPlantReportDialogBinding.expense.getText().toString();
//
//
//             if(flow.isEmpty() || tds.isEmpty() || pressure.isEmpty() ||
//                     meterOpen.isEmpty() || meterClose.isEmpty() ){
//                 Toast.makeText(getActivity(), "Fields cant be empty", Toast.LENGTH_SHORT).show();
//                 return;
//             }else{
//                Date today =  Helper.getTodayDateObject();
//                int  usage = Math.abs(Integer.parseInt(meterClose)-Integer.parseInt(meterOpen));
//                 PlantReport plantReport = new PlantReport(PLANT_NAME,pressure,Integer.parseInt(flow),
//                         Integer.parseInt(tds)
//                         ,Integer.parseInt(meterOpen),
//                         Integer.parseInt(meterClose),
//                         usage,
//                         Integer.parseInt(expense),
//
//                         today.getDay(),today.getMonth(),today.getYear());
//
//                 binding.flowNumber.setText(String.valueOf(plantReport.getFlow()));
//                 binding.tdsNumber.setText(String.valueOf(plantReport.getTds()));
//                 binding.pressureNumber.setText(plantReport.getPressure());
//                 binding.plantReportDate.setText(today.getDateInStringFormat());
//                 binding.expenseNumber.setText(expense);
//                 viewModel.savePlantReport(plantReport,getContext());
//                 Toast.makeText(getActivity(), "Adding report...", Toast.LENGTH_SHORT).show();
//                 addPlantReportDailog.dismiss();
//             }
//            }
//        });
    }

    private void checkUnitSize(String plantName) {

        viewModel.noOfUnits(plantName).observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer size) {
                if(size>0){
                    load_blue_box();
                    loadAttendance(PLANT_NAME);
                    loadPlantReport(PLANT_NAME);
                    loadMonthlyReport(PLANT_NAME);
                    loadExpenses(PLANT_NAME);
                    binding.mainLayout.setVisibility(View.VISIBLE);
                    binding.noPlantsLayout.setVisibility(View.INVISIBLE);



                }else{
                    binding.mainLayout.setVisibility(View.INVISIBLE);
                    binding.noPlantsLayout.setVisibility(View.VISIBLE);
                }
                loader.dismiss();
            }
        });
    }

    private void loadMonthlyReport(String plantName) {
        viewModel.getMonthlyData(plantName).observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                if(records!= null){
                    Map<String, String> map = new HashMap<>();
                   map =  viewModel.getMonthlySum(records);
                   float textSize = viewModel.calculateTextSize(map.get("waterSupply"));
                   binding.monthWaterNumber.setTextSize(textSize);
                   binding.monthAmountNumber.setTextSize(textSize);
                 //  binding.monthAmountNumber.setText("₹"+map.get("sum"));
                   binding.monthWaterNumber.setText(map.get("waterSupply"));

                   int monthSum = Integer.parseInt(map.get("sum"));

                    viewModel.getMonthlyExpense(PLANT_NAME).observe(getViewLifecycleOwner(), new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer expenses) {
                            monthlyExpense = expenses;
                            binding.monthAmountNumber.setText("₹ "+(monthSum-expenses));
                        }
                    });
                }
            }
        });
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
                        binding.inHandNumber.setText("₹"+String.valueOf(collection));

                    }
                }else{
                    expenseRecyclerItems.clear();
                    expenseAdapter.setRecyclerItems(expenseRecyclerItems);
                    binding.expenseNumber.setText("0");
                    binding.totalExpenseNumber.setText("0");
                    binding.inHandNumber.setText("₹"+String.valueOf(collection));
                }

                loader.dismiss();
            }
        });
    }

    private void loadPlantReport(String plantName) {
        viewModel.getPlantReport(plantName).observe(getViewLifecycleOwner(), new Observer<PlantReport>() {
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
                  //  binding.expenseNumber.setText(String.valueOf(plantReport.getExpense()));
                    binding.meterCloseNumber.setText(String.valueOf(plantReport.getMeterClose()));
                    binding.usageNumber.setText(String.valueOf(plantReport.getUsage()));
                }
            }
        });
    }

    private void loadAttendance(String plantName) {

        attendanceRV = binding.attendanceRV;
        attendanceRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        attendanceRV.setHasFixedSize(true);
        adapter = new AttendanceRvAdapter();
        attendanceRV.setAdapter(adapter);




        adapter.setListener(new AttendanceRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Attendance attendance, int clickPosition) {
                loadAttendanceDailog(attendance);
            }

            @Override
            public void onPresentClick(Attendance attendance, int position, AttendanceItemBinding attendanceItemBinding) {
//                Toast.makeText(getContext(), "Registering attendance please wait...", Toast.LENGTH_LONG).show();
//                adapter.presentClick(attendanceItemBinding);
//                attendance.setAttendance(Constants.PRESENT);
//                attendanceList.get(position).setAttendance(Constants.PRESENT);
//                displayTotalAttendance(attendanceList);
//
//                viewModel.addAttendance(attendance,getContext());
            }

            @Override
            public void onAbsentClick(Attendance attendance,int postition,AttendanceItemBinding attendanceItemBinding) {

//                adapter.absentClick(attendanceItemBinding);
//                attendance.setAttendance(Constants.ABSENT);
//                attendanceList.get(postition).setAttendance(Constants.ABSENT);
//
//
//                displayTotalAttendance(attendanceList);
//
//                Toast.makeText(getContext(), "Registering attendance please wait...", Toast.LENGTH_LONG).show();
//                viewModel.addAttendance(attendance,getContext());
        }

            @Override
            public void onDeleteRequest(Attendance attendance) {


            }
        });

        viewModel.getAttendances(PLANT_NAME).observe(getViewLifecycleOwner(), new Observer<List<Attendance>>() {
            @Override
            public void onChanged(List<Attendance> attendances) {
                if(attendances!= null){
                    attendanceList = attendances;
                    displayTotalAttendance(attendances);
                }


                adapter.setResources(getResources());

                adapter.setAttendanceList(attendanceList);

            }
        });
    }

    private void displayTotalAttendance(List<Attendance> attendances) {
        Map<String,String> map = new HashMap<>();
        map = viewModel.getSumOfAttendances(attendances);
        binding.presentNumber.setText(map.get("present"));
        binding.absentNumber.setText(map.get("absent"));
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

        viewModel.getAttendanceOfUser(attendance).observe(getViewLifecycleOwner(), new Observer<List<Attendance>>() {
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

    private void load_blue_box() {

        viewModel.getTodayRecord(PLANT_NAME).observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> recordList) {
                if(recordList!=null){
                    todayRecords = recordList;
                }
                viewModel.getNoOfUnits().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        noOfUits = integer;
                       int  taskCount = todayRecords.size();
                        binding.percentageTV.setText(taskCount+"/"+noOfUits);
                        binding.linearProgressBar.setProgress(taskCount);
                        binding.linearProgressBar.setMax(noOfUits);

                        binding.percentageTV.setText(todayRecords.size()+"/"+ noOfUits);
                        Map<String ,Integer> map = viewModel.getBlueBoxDetails(recordList);
                        binding.totalAmountTV.setText("₹ "+map.get("sum"));
                        binding.totalCashNumber.setText("₹"+map.get("sum"));
                        collection = map.get("sum");
                        binding.inHandNumber.setText("₹" + String.valueOf(collection - dayExpense));
                        binding.waterDispenseTV.setText("Water dispense(L) : "+map.get("waterSupply"));
                        loader.dismiss();
                    }
                });


            }
        });
    }

    private void loadDialog() {

        addUnitDialogBinding = AddUnitDialogBinding.inflate(LayoutInflater.from(getContext()));


        addUnitDialog = new Dialog(getActivity());
        addUnitDialog.setContentView(addUnitDialogBinding.getRoot());
        addUnitDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        addUnitDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addUnitDialog.setCancelable(true);
        addUnitDialog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;
       addUnitDialog.show();
        // set spinner
        spinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item,
                Constants.UNIT_TYPE);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addUnitDialogBinding.spinner.setAdapter(spinnerAdapter);

        addUnitDialogBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                unitType = Constants.UNIT_TYPE.get(i);
                if (unitType.equals(Constants.RECHARGE_UNIT)) {
                    addUnitDialogBinding.waterOpening.setVisibility(View.GONE);
                    addUnitDialogBinding.openingET.setHint("Enter Opening Balance");
                } else {
                    addUnitDialogBinding.waterOpening.setVisibility(View.VISIBLE);
                    addUnitDialogBinding.openingET.setHint("Enter Opening Reading(cash/coin)");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addUnitDialogBinding.dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unitName = addUnitDialogBinding.nameET.getText().toString().trim();
                String openingText = addUnitDialogBinding.openingET.getText().toString();
                if (openingText.isEmpty()) {

                    addUnitDialogBinding.openingET.setError("Enter opening reading/balance");
                    return;
                } else {
                    opening = Integer.parseInt(openingText);
                }
                String openingWater = addUnitDialogBinding.waterOpening.getText().toString();

                if (unitType.equals(Constants.RECHARGE_UNIT)) {
                    waterOpening = 0;
                } else {


                    if (openingWater.isEmpty()) {
                        addUnitDialogBinding.waterOpening.setError("Enter opening reading");
                        return;
                    } else {
                        waterOpening = Integer.parseInt(openingWater);
                    }
                }
                if (!unitType.equals(Constants.RECHARGE_UNIT)) {
                    waterOpening = Integer.parseInt(openingWater);
                } else {
                    waterOpening = 0;
                }
                if (unitName.isEmpty()) {
                    addUnitDialogBinding.nameET.setError("Enter unit name");
                } else {
                    loader.show();

                    viewModel.addNewUnit(PLANT_NAME, unitName, unitType, opening, waterOpening).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean result) {
                            loader.dismiss();
                            if (result) {

                                checkUnitSize(PLANT_NAME);
                                Toast.makeText(getActivity(), "Unit Added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                            addUnitDialog.dismiss();
                        }
                    });
                }

            }
        });

    }


    private void initialize_loader() {
        loader = new Dialog(getActivity());
        loader.setContentView(R.layout.loader);
        loader.getWindow().setBackgroundDrawableResource(R.color.transparent);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loader.setCancelable(false);
        loader.getWindow().getAttributes().windowAnimations = R.style.animation;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(DashboardViewModel.class);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to the previous fragment
                NavDirections action = DashboardFragmentDirections.actionDashboardFragmentToHomeFragment();
                navController.navigate(action);
            }
        });

    }


    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addUserFragment) {
            if(binding.noPlantsLayout.getVisibility()== View.VISIBLE){
                Toast.makeText(getContext(), "Please add units first", Toast.LENGTH_SHORT).show();
                return false;
            }
            NavDirections action = DashboardFragmentDirections.actionDashboardFragmentToAddUserFragment(PLANT_NAME);
            navController.navigate(action);
            return true;
        }
        return false;
    }
}

