package com.prplmnstr.drops.views.admin;

import android.app.Dialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;

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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prplmnstr.drops.MainActivity;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.AttendanceRvAdapter;
import com.prplmnstr.drops.adapters.DashboardRecyclerAdapter;
import com.prplmnstr.drops.databinding.AddPlantDialogBinding;
import com.prplmnstr.drops.databinding.AddUnitDialogBinding;
import com.prplmnstr.drops.databinding.CalenderDialogBinding;
import com.prplmnstr.drops.databinding.DashboardListViewItemBinding;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.databinding.FragmentSignInBinding;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.viewModel.AuthViewModel;
import com.prplmnstr.drops.viewModel.DashboardViewModel;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class DashboardFragment extends Fragment implements NavController.OnDestinationChangedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentDashboardBinding binding;
    private AttendanceRvAdapter adapter;
    private RecyclerView attendanceRV;
    private DashboardViewModel viewModel;
    private ArrayAdapter spinnerAdapter;
    private Dialog loader;
    private Dialog addUnitDialog;
    private Dialog calenderDialog;
    private CalenderDialogBinding calenderDialogBinding;
    private AddUnitDialogBinding addUnitDialogBinding;
    private BottomNavigationView bottomNavigationView;

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








        navController = Navigation.findNavController(view);
        navController.addOnDestinationChangedListener(this);


        bottomNavigationView = requireActivity().findViewById(R.id.navigationBar);

        // Set the OnNavigationItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        PLANT_NAME = DashboardFragmentArgs.fromBundle(getArguments()).getPlant();






        try {
            initialize_loader();
            loader.show();
            loadDialog();
            checkUnitSize(PLANT_NAME);


            binding.dashboardTV.setText(PLANT_NAME);
            binding.addUnitImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUnitDialog.show();
                }
            });


            binding.noUnitAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUnitDialog.show();
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



        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


//


    }

    private void checkUnitSize(String plantName) {

        viewModel.noOfUnits(plantName).observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer size) {
                if(size>0){
                    binding.mainLayout.setVisibility(View.VISIBLE);
                    binding.noPlantsLayout.setVisibility(View.INVISIBLE);
                    load_blue_box();
                    loadAttendance(PLANT_NAME);


                }else{
                    binding.mainLayout.setVisibility(View.INVISIBLE);
                    binding.noPlantsLayout.setVisibility(View.VISIBLE);
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
            public void onPresentClick(Attendance attendance,int position) {
                loader.show();
                attendance.setAttendance(Constants.PRESENT);
                attendanceList.get(position).setAttendance(Constants.PRESENT);
                displayTotalAttendance(attendanceList);

                viewModel.addAttendance(attendance).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        loader.dismiss();
                        if(aBoolean){

                           // adapter.setAttendanceList(attendanceList);
                            Toast.makeText(getActivity(), "Attendance registered.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Attendance registration failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            @Override
            public void onAbsentClick(Attendance attendance,int postition) {
                attendanceList.get(postition).setAttendance(Constants.ABSENT);
                displayTotalAttendance(attendanceList);
                loader.show();
            attendance.setAttendance(Constants.ABSENT);
                viewModel.addAttendance(attendance).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    loader.dismiss();
                    if(aBoolean){

                        //adapter.setAttendanceList(attendanceList);
                        Toast.makeText(getActivity(), "Attendance registered.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "Attendance registration failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

            @Override
            public void onDeleteRequest(Attendance attendance) {


            }
        });

        viewModel.getAttendances(PLANT_NAME).observe(getViewLifecycleOwner(), new Observer<List<Attendance>>() {
            @Override
            public void onChanged(List<Attendance> attendances) {
                attendanceList = attendances;

                adapter.setResources(getResources());

                adapter.setAttendanceList(attendanceList);
               displayTotalAttendance(attendances);
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


                        binding.percentageTV.setText(todayRecords.size()+"/"+ noOfUits);
                        Map<String ,Integer> map = viewModel.getBlueBoxDetails(recordList);
                        binding.totalAmountTV.setText("₹ "+map.get("sum"));
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
                loader.show();
                unitName = addUnitDialogBinding.nameET.getText().toString().trim();
                String openingText = addUnitDialogBinding.openingET.getText().toString();
                if (openingText.isEmpty()) {
                    addUnitDialogBinding.openingET.setError("Enter opening reading/balance");
                    return;
                } else {
                    opening = Integer.parseInt(openingText);
                }
                String openingWater = addUnitDialogBinding.waterOpening.getText().toString();
                if (openingWater.isEmpty()) {
                    addUnitDialogBinding.waterOpening.setError("Enter opening reading");
                    return;
                } else {
                    waterOpening = Integer.parseInt(openingWater);
                }

                if (!unitType.equals(Constants.RECHARGE_UNIT)) {
                    waterOpening = Integer.parseInt(openingWater);
                } else {
                    waterOpening = 0;
                }
                if (unitName.isEmpty()) {
                    addUnitDialogBinding.nameET.setError("Enter unit name");
                } else {
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

            NavDirections action = DashboardFragmentDirections.actionDashboardFragmentToAddUserFragment(PLANT_NAME);
            navController.navigate(action);
            return true;
        }
        return false;
    }
}

