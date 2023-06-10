package com.prplmnstr.drops.views;

import android.content.Context;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.prplmnstr.drops.MainActivity;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.databinding.FragmentSignInBinding;
import com.prplmnstr.drops.viewModel.AuthViewModel;


public class SignInFragment extends Fragment {

    private AuthViewModel viewModel;
    private NavController navController;
    private FragmentSignInBinding binding;
    private ClickHandler clickHandler;

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



        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.loginBtn.setEnabled(false);
                binding.loginBtn.setText("");
                binding.loginProgress.setVisibility(View.VISIBLE);


                String email = binding.emailET.getText().toString();
                String password = binding.passwordET.getText().toString();
                clickHandler.signInUser(email,password);
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
        public void signInUser(String email,String password){
            if(email.isEmpty()){
                binding.emailET.setError("Please enter E-mail address");
                binding.loginBtn.setText("Login");
                binding.loginBtn.setEnabled(true);
                Toast.makeText(getActivity(), "Please enter E-mail address", Toast.LENGTH_SHORT).show();
            }else if(password.isEmpty()){
                binding.passwordET.setError("Please enter password");
                binding.loginBtn.setText("Login");
                binding.loginBtn.setEnabled(true);
                Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
            }else{
                 viewModel.signIn(email,password);

                    viewModel.getFirebaseUserMutableLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
                        @Override
                        public void onChanged(FirebaseUser firebaseUser) {
                            if(firebaseUser!=null){
                                ((MainActivity)getActivity()).checkForAdmin(firebaseUser);
                                Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                                navController.navigate(R.id.action_splashFragment_to_homeFragment);
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