package com.prplmnstr.drops.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.prplmnstr.drops.MainActivity;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.databinding.FragmentSignInBinding;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.viewModel.AuthViewModel;

import java.util.List;


public class SignInFragment extends Fragment {

    private AuthViewModel viewModel;
    private NavController navController;
    private FragmentSignInBinding binding;
    private ClickHandler clickHandler;
    private ArrayAdapter spinnerAdapter;
    private String userType;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentSignInBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        clickHandler = new ClickHandler(getActivity());



        // Get SharedPreferences instance
        sharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);

// Create an editor
        editor = sharedPreferences.edit();




        // set spinner
        spinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item,
                Constants.USER_TYPE_SIGN_IN);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(spinnerAdapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userType = Constants.USER_TYPE_SIGN_IN.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // binding.loginProgress.setVisibility(View.VISIBLE);


                String email = binding.emailET.getText().toString();
                String password = binding.passwordET.getText().toString();
                if(email.isEmpty()){


                    binding.emailET.setError("Please enter E-mail address");

                    Toast.makeText(getActivity(), "Please enter E-mail address", Toast.LENGTH_SHORT).show();
                }else if(password.isEmpty()){


                    binding.passwordET.setError("Please enter password");

                    Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
                }else{
                    binding.loginBtn.setEnabled(false);
                    binding.loginBtn.setText("");
                    binding.loginProgress.setVisibility(View.VISIBLE);
                    clickHandler.checkUserExistance(email,password,userType);
                }

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthViewModel.class);


    }

    private class ClickHandler{
        Context context;
        public ClickHandler(Context context){
            this.context = context;
        }



        public void checkUserExistance(String email,String password,String userType){
            if(userType.equals(Constants.ADMIN)){
                signInUser(email, password);
            }else if(userType.equals(Constants.WORKER)){
                viewModel.isUserExist(userType).observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> users) {

                        if(users!=null){

                            if(users.contains(email)){
                                signInUser(email,password);
                        }else{
                                binding.loginBtn.setText("Login");
                                binding.loginBtn.setEnabled(true);
                                binding.loginProgress.setVisibility(View.GONE);
                                Toast.makeText(context, "Worker account does not exist", Toast.LENGTH_SHORT).show();
                            }

                        }else{

                            Toast.makeText(context, "Worker account does not exist/Check your network", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else if(userType.equals(Constants.INVESTOR)){
                viewModel.isUserExist(userType).observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> users) {
                        if(users!=null){
                            if(users.contains(email)){
                                signInUser(email,password);
                            }else{
                                binding.loginBtn.setText("Login");
                                binding.loginBtn.setEnabled(true);
                                binding.loginProgress.setVisibility(View.GONE);
                                Toast.makeText(context, "Investor account does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                       else{

                            Toast.makeText(context, "Investor account does not exist/Check your network", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }

        public void signInUser(String email,String password){
          {
                 viewModel.signIn(email,password);

                    viewModel.getFirebaseUserMutableLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
                        @Override
                        public void onChanged(FirebaseUser firebaseUser) {
                            if(firebaseUser!=null){

                                //((MainActivity)getActivity()).checkForAdmin(firebaseUser);
                                if(userType==Constants.ADMIN){
                                    navController.navigate(R.id.action_signInFragment_to_homeFragment);
                                }else{
                                    navController.navigate(R.id.action_signInFragment_to_workerActivity);
                                }

                                // Store a string value

                                editor.putString(Constants.SAVED_USER_TYPE, userType);

                                // Apply the changes
                                editor.apply();
                                Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                binding.loginProgress.setVisibility(View.INVISIBLE);
                                binding.loginBtn.setText("Login");
                                binding.loginBtn.setEnabled(true);
                                Toast.makeText(getActivity(), "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




            }
        }

    }

}