package com.prplmnstr.drops.views.admin;

import android.app.Dialog;
import android.content.Context;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.DashboardRecyclerAdapter;
import com.prplmnstr.drops.databinding.AddPlantDialogBinding;
import com.prplmnstr.drops.databinding.AddUserDialogBinding;
import com.prplmnstr.drops.databinding.FragmentAddUserBinding;
import com.prplmnstr.drops.databinding.FragmentSignInBinding;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.models.User;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.viewModel.AddUserViewModel;
import com.prplmnstr.drops.viewModel.AuthViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class AddUserFragment extends Fragment implements NavController.OnDestinationChangedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentAddUserBinding binding;
    private AddUserViewModel viewModel;
    private ArrayAdapter spinnerAdapter;
    private boolean newUser = true;
    private Dialog loader;
    private Dialog addUserDialog;
    private RecyclerView investorRV,workerRV;
    private DashboardRecyclerAdapter workerAdapter,investorAdapter;
    private String plantName;
    private AddUserDialogBinding addUserDialogBinding;
    //add user variables
    private String userType,userName, userPassword, userEmail;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private List<RecyclerModel> workerList = new ArrayList<>();
    private List<RecyclerModel> investorList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =FragmentAddUserBinding .inflate(inflater,container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize_utils(view);
        try{

            initialize_loader();
            loadDialog();
            loader.show();
            relaod_investors();
            reload_workers();
            Context context;





            binding.addUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUserDialog.show();
                }
            });



        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }







    }

    private void reload_workers() {
        viewModel.getWorkers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> worker) {
                if (worker != null) {
                    userList.addAll(worker);
                    workerList = viewModel.mapUsersToRecyclerItem(worker, plantName);
                }
                if (workerList != null && !workerList.isEmpty()){
                    loadWorkers();
                    binding.workerEmptyLayout.setVisibility(View.GONE);
                    binding.workersRV.setVisibility(View.VISIBLE);

                }else{
                    binding.workersRV.setVisibility(View.GONE);
                    binding.workerEmptyLayout.setVisibility(View.VISIBLE);

                }
                loader.dismiss();
            }
        });


    }

    private void relaod_investors() {
        viewModel.getInvestors().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> investor) {
                if(investor!=null) {
                    userList.addAll(investor);
                    investorList = viewModel.mapUsersToRecyclerItem(investor, plantName);
                }
                if(investorList!= null && !investorList.isEmpty()){
                    loadInvestors();
                    binding.investorEmptyLayout.setVisibility(View.GONE);
                    binding.inverstorRV.setVisibility(View.VISIBLE);
                }else{
                    binding.inverstorRV.setVisibility(View.GONE);
                    binding.investorEmptyLayout.setVisibility(View.VISIBLE);

                }

            }
        });
    }

    private void initialize_utils(View view) {
        navController = Navigation.findNavController(view);
        navController.addOnDestinationChangedListener(this);
        bottomNavigationView = requireActivity().findViewById(R.id.navigationBar);

        // Set the OnNavigationItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        plantName = AddUserFragmentArgs.fromBundle(getArguments()).getPlant();
    }

    private void loadDialog() {


        addUserDialogBinding = AddUserDialogBinding.inflate(LayoutInflater.from(getContext()));


        addUserDialog = new Dialog(getActivity());
        addUserDialog.setContentView(addUserDialogBinding.getRoot());
        addUserDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        addUserDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addUserDialog.setCancelable(true);
        addUserDialog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;

        // set spinner
        spinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item,
                Constants.USER_TYPE);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addUserDialogBinding.spinner.setAdapter(spinnerAdapter);

        addUserDialogBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userType = Constants.USER_TYPE.get(i);



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addUserDialogBinding.dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userEmail = addUserDialogBinding.emailET.getText().toString().trim();


                User oldUser = viewModel.isEmailExists(userList,userEmail);
              if(oldUser==null){
                  userName = addUserDialogBinding.nameET.getText().toString().trim();
                  userPassword = addUserDialogBinding.passwordET.getText().toString();
                  newUser  = true;
              }
              else{
                  newUser = false;
                  userPassword = oldUser.getPassword();
                  userName = oldUser.getUserName();
              }


                if(!userEmail.endsWith("@gmail.com")){
                    addUserDialogBinding.emailET.setError("Enter valid Email Adress");
                }


                else if(userName.isEmpty()){
                    addUserDialogBinding.nameET.setError("Enter User Name");
                }else if(userPassword.length()<8){
                    addUserDialogBinding.passwordET.setError("Password should be of atleast 8 characters");
                }else

                {
                    loader.show();
                    User user = new User(plantName,userType,userName,userEmail, userPassword);
                    addUser(user);

                    if(user.getUserType().equals(Constants.WORKER)){

                        reload_workers();
                    }else{
                        relaod_investors();
                    }

                }
            }
        });
    }

    private void addUser(User user) {
        viewModel.addUser(user,newUser).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean result) {
                if(result){
                    addUserDialog.dismiss();
                    Toast.makeText(getActivity(), "User Added", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                loader.dismiss();
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







    private void loadWorkers() {



        workerRV = binding.workersRV;
        workerRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        workerRV.setHasFixedSize(true);
        workerAdapter = new DashboardRecyclerAdapter();
        workerRV.setAdapter(workerAdapter);
        //For random image resourse
        final TypedArray imgs = getResources().obtainTypedArray(R.array.workers);
        final Random rand = new Random();
        for(RecyclerModel item : workerList){
            int rndInt = rand.nextInt(imgs.length());
            int resID = imgs.getResourceId(rndInt, R.drawable.employee_1);
            item.setImageIndex(resID);

        }
        workerAdapter.setRecyclerItems(workerList);



    }
    private void loadInvestors() {



        investorRV = binding.inverstorRV;
        investorRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        investorRV.setHasFixedSize(true);
        investorAdapter = new DashboardRecyclerAdapter();
        investorRV.setAdapter(investorAdapter);
        //For random image resourse
        final TypedArray imgs = getResources().obtainTypedArray(R.array.investor);
        final Random rand = new Random();
        for(RecyclerModel item : investorList){
            int rndInt = rand.nextInt(imgs.length());
            int resID = imgs.getResourceId(rndInt, R.drawable.investor_1);
            item.setImageIndex(resID);

        }
        investorAdapter.setRecyclerItems(investorList);



    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AddUserViewModel.class);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to the previous fragment
                NavDirections action = AddUserFragmentDirections.actionAddUserFragmentToDashboardFragment(plantName);
                navController.navigate(action);
            }
        });
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.dashboardFragment){

            NavDirections action = AddUserFragmentDirections.actionAddUserFragmentToDashboardFragment(plantName);
            navController.navigate(action);
            return true;
        }
        return false;
    }
}