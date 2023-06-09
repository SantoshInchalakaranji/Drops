package com.prplmnstr.drops.views.admin;



import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.prplmnstr.drops.R;

import com.prplmnstr.drops.adapters.PlantsRecyclerAdapter;
import com.prplmnstr.drops.databinding.AddPlantDialogBinding;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.databinding.FragmentHomeBinding;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.viewModel.AddUserViewModel;
import com.prplmnstr.drops.viewModel.DashboardViewModel;
import com.prplmnstr.drops.viewModel.HomeFragmentViewModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private PlantsRecyclerAdapter adapter;

    private Dialog addPlantDialog;
    private AddPlantDialogBinding addPlantDialogBinding;
    private HomeFragmentViewModel viewModel;
    private String base64Image;
    Bitmap   bitmap;
    private Dialog loader;
    private List<Plant> plants;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize_loader();
        loader.show();
        load_plants();

        setRecyler();

        loadDialog();



        binding.addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlantDialog.show();
            }
        });
        

    }

    private void load_plants() {
        viewModel.getPlants().observe(getViewLifecycleOwner(), new Observer<List<Plant>>() {
            @Override
            public void onChanged(List<Plant> plantsList) {
                if(plantsList==null){
                    loader.dismiss();
                }else{
                    plants = plantsList;
                    setRecyler();
                    adapter.setPlants(plants);
                    loader.dismiss();
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



    private void setRecyler() {
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new PlantsRecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void loadDialog() {

            addPlantDialogBinding = AddPlantDialogBinding.inflate(LayoutInflater.from(getContext()));


            addPlantDialog = new Dialog(getActivity());
            addPlantDialog.setContentView(addPlantDialogBinding.getRoot());
            addPlantDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            addPlantDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            addPlantDialog.setCancelable(true);
            addPlantDialog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;


        addPlantDialogBinding.dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "ssdff", Toast.LENGTH_SHORT).show();
            }
        });

        addPlantDialogBinding.imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,Constants.PICK_IMAGE_REQUEST_CODE);
            }
        });
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(HomeFragmentViewModel.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

           Uri filepath1 = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(filepath1);
              bitmap = BitmapFactory.decodeStream(inputStream);
                addPlantDialogBinding.imagePicker.setImageBitmap(bitmap);
               // loadRecycler();
            } catch (Exception ex) {

            }

        }

    }
}