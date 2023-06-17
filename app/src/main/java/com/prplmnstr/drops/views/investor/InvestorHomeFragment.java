package com.prplmnstr.drops.views.investor;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.RecyclerView;

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
import com.prplmnstr.drops.MainActivity;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.AttendanceRvAdapter;
import com.prplmnstr.drops.databinding.AddPlantReportDialogBinding;
import com.prplmnstr.drops.databinding.AddUnitDialogBinding;
import com.prplmnstr.drops.databinding.CalenderDialogBinding;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.databinding.FragmentInvestorHomeBinding;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.PlantReport;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;
import com.prplmnstr.drops.viewModel.DashboardViewModel;
import com.prplmnstr.drops.viewModel.investor.InvestorDashboardViewModel;
import com.prplmnstr.drops.views.admin.DashboardFragment;
import com.prplmnstr.drops.views.admin.DashboardFragmentArgs;
import com.prplmnstr.drops.views.admin.DashboardFragmentDirections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvestorHomeFragment extends Fragment implements NavController.OnDestinationChangedListener {

    private FragmentInvestorHomeBinding binding;
   
    
    private InvestorDashboardViewModel viewModel;
    private ArrayAdapter spinnerAdapter;
    private Dialog loader;


    private List<String> plants = new ArrayList<>();
   
   
   

    // adding unit variables
    private String unitType, unitName;
    private int opening, waterOpening;

    private NavController navController;
    private String PLANT_NAME;
    private List<Record> todayRecords = new ArrayList<>();
    private List<Attendance> attendanceList = new ArrayList<>();
    private int noOfUits;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInvestorHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        navController.addOnDestinationChangedListener(this);
        initialize_loader();
        loader.show();
        
        try{
            loadUI();



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

                            navController.navigate(R.id.action_investorHomeFragment_to_mainActivity2);
//                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
                            return true;
                        }
                    });
                }
            });


        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(InvestorDashboardViewModel.class);

//        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                // Navigate to the previous fragment
//               getActivity().finish();
//            }
//        });
        
    }


    private void loadUI() {
        viewModel.getPlants().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> plantList) {
                if(plantList!=null){
                    plants = plantList;
                    set_spinner(plants);
                }else{
                    plantList.add("Chikodi");
                    set_spinner(plants);

                }

            }
        });
    }
    private void loadMonthlyReport(String plantName) {

        float scale = getResources().getDisplayMetrics().scaledDensity;

        viewModel.getMonthlyData(plantName).observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                if(records!= null){
                    Map<String, String> map = new HashMap<>();
                    map =  viewModel.getMonthlySum(records);
                    float fontSizeInPixels= viewModel.calculateTextSize(map.get("waterSupply"));
                    binding.monthAmountNumber.setTextSize(fontSizeInPixels);
                    binding.monthWaterNumber.setTextSize(fontSizeInPixels);
                    binding.monthAmountNumber.setText("₹"+map.get("sum"));
                    binding.monthWaterNumber.setText(map.get("waterSupply"));
                }
            }
        });
    }

    private void loadPlantReport(String plantName) {
        viewModel.getPlantReport(plantName).observe(getViewLifecycleOwner(), new Observer<PlantReport>() {
            @Override
            public void onChanged(PlantReport plantReport) {
                if(plantReport!= null){
                    binding.flowNumber.setText(String.valueOf(plantReport.getFlow()));
                    binding.tdsNumber.setText(String.valueOf(plantReport.getTds()));
                    binding.pressureNumber.setText(plantReport.getPressure());
                    binding.plantReportDate.setText(Helper.getDateInStringFormat(
                            plantReport.getDay(), plantReport.getMonth(), plantReport.getYear()
                    ));
                }
            }
        });
    }


    private void checkUnitSize(String plantName) {

        viewModel.noOfUnits(plantName).observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer size) {
                if(size>0){
                    binding.mainLayout.setVisibility(View.VISIBLE);
                    binding.noPlantsLayout.setVisibility(View.INVISIBLE);
                    load_blue_box();
                  
                    loadPlantReport(PLANT_NAME);
                    loadMonthlyReport(PLANT_NAME);


                }else{
                    binding.mainLayout.setVisibility(View.INVISIBLE);
                    binding.noPlantsLayout.setVisibility(View.VISIBLE);
                }
                loader.dismiss();
            }
        });
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
                PLANT_NAME= plants.get(i);
                checkUnitSize(PLANT_NAME);
                Toast.makeText(getActivity(), "Loading please wait...", Toast.LENGTH_LONG).show();
            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {

    }
}