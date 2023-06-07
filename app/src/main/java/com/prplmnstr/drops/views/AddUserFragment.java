package com.prplmnstr.drops.views;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.prplmnstr.drops.R;
import com.prplmnstr.drops.databinding.FragmentAddUserBinding;
import com.prplmnstr.drops.databinding.FragmentSignInBinding;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.viewModel.AddUserViewModel;
import com.prplmnstr.drops.viewModel.AuthViewModel;

import java.util.ArrayList;
import java.util.List;


public class AddUserFragment extends Fragment {

    private FragmentAddUserBinding binding;
    private AddUserViewModel viewModel;
    private String outletName="";
    private ArrayAdapter spinnerAdapter;
    private List<String> outlets = new ArrayList<String>();
    private Dialog loader;


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
        showLoader();
        loadOutlets();



        binding.addNewOutletTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.addLayout.getVisibility()==View.GONE){
                    binding.addLayout.setVisibility(View.VISIBLE);
                    binding.addNewOutletTV.setText("Cancel");
                }else{
                    binding.addLayout.setVisibility(View.GONE);
                    binding.addNewOutletTV.setText("Add");
                }
            }
        });

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoader();
                outletName = binding.outletET.getText().toString();
                viewModel.addOutlet(outletName).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean result) {
                        if(result ==true){
                            loadOutlets();
                            Toast.makeText(getActivity(), "Outlet added", Toast.LENGTH_SHORT).show();
                            binding.addLayout.setVisibility(View.GONE);
                            binding.addNewOutletTV.setText("Add");
                            loader.dismiss();
                        }else
                        {
                            loader.dismiss();
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
               

            }
        });
    }

    private void showLoader() {
        loader = new Dialog(getActivity());
        loader.setContentView(R.layout.loader);
        loader.getWindow().setBackgroundDrawableResource(R.color.transparent);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loader.setCancelable(false);
        loader.getWindow().getAttributes().windowAnimations = R.style.animation;
        loader.show();
    }

    private void loadOutlets() {

        viewModel.getOutlets().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                outlets = strings;
                spinnerAdapter = new ArrayAdapter(getActivity(),
                        android.R.layout.simple_spinner_item,
                        outlets);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        if(outlets.size()<=0){
//            outlets.add("Empty");
//            binding.spinner.setEnabled(false);
//        }
                binding.spinner.setAdapter(spinnerAdapter);
                spinnerAdapter.notifyDataSetChanged();
               loader.dismiss();

            }
        });




        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              outletName = outlets.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AddUserViewModel.class);
    }
}