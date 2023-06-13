package com.prplmnstr.drops;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.prplmnstr.drops.databinding.ActivityMainBinding;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.viewModel.AuthViewModel;
import com.prplmnstr.drops.viewModel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private MainActivityViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel =  new ViewModelProvider(this).get(MainActivityViewModel.class);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.navigationBar,navController);


        // To hide navigation bar in splash fragment
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if(navDestination.getId() == R.id.splashFragment || navDestination.getId() == R.id.signInFragment
                        || navDestination.getId() == R.id.homeFragment){

                    binding.navigationBar.setVisibility(View.GONE);

                }else{
                    binding.navigationBar.setVisibility(View.VISIBLE);

                }
            }
        });

        //show add user fragment for admin
        //else show account details fragment for user
//        FirebaseUser firebaseUser = viewModel.getCurrentUser();
//        if(firebaseUser!=null) {
//            checkForAdmin(firebaseUser);
//        }

    }

//    public void checkForAdmin(FirebaseUser firebaseUser) {
//
//            if (firebaseUser.getEmail().equals(Constants.ADMIN_MAIL)) {
//                binding.navigationBar.getMenu().removeItem(R.id.userDetailsFragment);
//                binding.navigationBar.getMenu().removeItem(R.id.uploadDataFragment);
//            } else {
//                binding.navigationBar.getMenu().removeItem(R.id.addUserFragment);
//            }
//        }

}

