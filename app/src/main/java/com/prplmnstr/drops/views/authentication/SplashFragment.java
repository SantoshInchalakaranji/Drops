package com.prplmnstr.drops.views.authentication;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import androidx.lifecycle.ViewModelProvider;
        import androidx.lifecycle.ViewModelStoreOwner;
        import androidx.navigation.NavController;
        import androidx.navigation.Navigation;

        import android.os.Handler;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Toast;

        import com.prplmnstr.drops.R;
        import com.prplmnstr.drops.utils.Constants;
        import com.prplmnstr.drops.viewModel.authentication.AuthViewModel;

public class SplashFragment extends Fragment {


    private AuthViewModel viewModel;
    private NavController navController;
    private SharedPreferences sharedPreferences;
    private String userType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthViewModel.class);
        navController = Navigation.findNavController(view);
        try {

            sharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);

            boolean key = sharedPreferences.contains(Constants.SAVED_USER_TYPE);
            if(key){
                userType = sharedPreferences.getString(Constants.SAVED_USER_TYPE,null);
            }else{
                userType = null;
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "Please reload the app", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    if (viewModel.getCurrentUser() != null) {
                        if(userType!=null) {
                            if (userType.equals(Constants.ADMIN)) {
                                navController.navigate(R.id.action_splashFragment_to_homeFragment);
                            } else if (userType.equals(Constants.WORKER)) {

                                navController.navigate(R.id.action_splashFragment_to_workerActivity);
                                getActivity().finish();
                            } else if (userType.equals(Constants.INVESTOR)) {
                                // navController.popBackStack();
                                navController.navigate(R.id.action_splashFragment_to_investorActivity);
                                getActivity().finish();

                            }
                        }

                    } else {
                        navController.navigate(R.id.action_splashFragment_to_signInFragment);
                    }
                }catch (Exception e){
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        },3000);
    }
}






















