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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.prplmnstr.drops.MainActivity;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.DashboardRecyclerAdapter;
import com.prplmnstr.drops.databinding.AddPlantDialogBinding;
import com.prplmnstr.drops.databinding.AddUnitDialogBinding;
import com.prplmnstr.drops.databinding.DashboardListViewItemBinding;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.databinding.FragmentSignInBinding;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.viewModel.AuthViewModel;
import com.prplmnstr.drops.viewModel.DashboardViewModel;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DashboardFragment extends Fragment implements NavController.OnDestinationChangedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentDashboardBinding binding;
    private DashboardRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private DashboardViewModel viewModel;
    private ArrayAdapter spinnerAdapter;
    private Dialog loader;
    private Dialog addUnitDialog;
    private AddUnitDialogBinding addUnitDialogBinding;
    private BottomNavigationView bottomNavigationView;

    // adding unit variables
    private String unitType ,unitName;
    private int opening, waterOpening;

    private NavController navController;
    public static String PLANT_NAME ;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater,container,false);
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
        PLANT_NAME= DashboardFragmentArgs.fromBundle(getArguments()).getPlant();



        binding.dashboardDummyTV.setText(PLANT_NAME);
        binding.dashboardTV.setText(PLANT_NAME);

        try {
            initialize_loader();
            //loader.show();
            loadDialog();

            binding.noUnitAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUnitDialog.show();
                }
            });







        }catch (Exception e){

        }





//




    }

    private void loadDialog() {

        addUnitDialogBinding = AddUnitDialogBinding.inflate(LayoutInflater.from(getContext()));


        addUnitDialog = new Dialog(getActivity());
        addUnitDialog.setContentView(addUnitDialogBinding.getRoot());
        addUnitDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        addUnitDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

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
                if(unitType.equals(Constants.RECHARGE_UNIT)){
                    addUnitDialogBinding.waterOpening.setVisibility(View.GONE);
                    addUnitDialogBinding.openingET.setHint("Enter Opening Balance");
                }else{
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
                if(openingText.isEmpty()){
                    addUnitDialogBinding.openingET.setError("Enter opening reading/balance");
                    return;
                }else{
                    opening = Integer.parseInt(openingText);
                }
                String openingWater = addUnitDialogBinding.waterOpening.getText().toString();
                if(openingWater.isEmpty()){
                    addUnitDialogBinding.waterOpening.setError("Enter opening reading");
                    return;
                }else{
                    waterOpening = Integer.parseInt(openingWater);
                }

                if(!unitType.equals(Constants.RECHARGE_UNIT) ){
                    waterOpening = Integer.parseInt(openingWater);
                }else{
                    waterOpening = 0;
                }
                if(unitName.isEmpty()){
                    addUnitDialogBinding.nameET.setError("Enter unit name");
                }
                else {
                    viewModel.addNewUnit(PLANT_NAME,unitName,unitType,opening,waterOpening).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean result) {
                            loader.dismiss();
                            if(result){
                                Toast.makeText(getActivity(), "Unit Added", Toast.LENGTH_SHORT).show();
                            }else{
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
        loader.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loader.setCancelable(false);
        loader.getWindow().getAttributes().windowAnimations = R.style.animation;

    }

//    private void loadRecyclerView() {
//
//
//
//        recyclerView = binding.recyclerView;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setHasFixedSize(true);
//        adapter = new DashboardRecyclerAdapter();
//        recyclerView.setAdapter(adapter);
//        //For random image resourse
//        final TypedArray imgs = getResources().obtainTypedArray(R.array.images);
//        final Random rand = new Random();
//        for(RecyclerModel item : recyclerItems){
//             int rndInt = rand.nextInt(imgs.length());
//            int resID = imgs.getResourceId(rndInt, R.drawable.statistics);
//            item.setImageIndex(resID);
//
//        }
//        adapter.setRecyclerItems(recyclerItems);
//
//
//
//    }

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
        if(item.getItemId() == R.id.addUserFragment){

            NavDirections action = DashboardFragmentDirections.actionDashboardFragmentToAddUserFragment(PLANT_NAME);
            navController.navigate(action);
            return true;
        }
        return false;
    }
}

