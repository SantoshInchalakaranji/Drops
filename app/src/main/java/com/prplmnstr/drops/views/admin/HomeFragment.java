package com.prplmnstr.drops.views.admin;



import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.prplmnstr.drops.R;

import com.prplmnstr.drops.adapters.PlantsRecyclerAdapter;
import com.prplmnstr.drops.databinding.AddPlantDialogBinding;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.databinding.FragmentHomeBinding;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;
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
    private List<Plant> plants= new ArrayList<>();
    private OnActivityResultListener onActivityResultListener;
    private NavController navController;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null){
            binding = FragmentHomeBinding.inflate(inflater,container,false);
             view = binding.getRoot();

        }


        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        try{
            initialize_loader();
            loader.show();
            load_plants();

            setRecyler();

            loadDialog();



        adapter.setChangeImageListener(new PlantsRecyclerAdapter.ChangeImageLister() {
            @Override
            public Plant changeImageRequest(Plant plant) {
                imagePickerIntent(Constants.CHANGE_IMAGE_REQUEST_CODE);
                loader.show();

                setOnActivityResultListener(new OnActivityResultListener() {
                    @Override
                    public void onActivityResult() {

                        plant.setImage(viewModel.convertImageToBase64(bitmap));
                        updatePlant(plant);
                        loader.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                });

                return plant;
            }

            @Override
            public void plantItemClicked(String plantName) {
               // Toast.makeText(getActivity(), plantName, Toast.LENGTH_SHORT).show();
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToDashboardFragment(plantName);
               navController.navigate(action);


              //  navController.navigate(R.id.action_homeFragment_to_dashboardFragment);

            }
        });



        binding.noPlantAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlantDialog.show();
            }
        });
        binding.addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlantDialog.show();
            }
        });
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void updatePlant(Plant plant) {
        viewModel.updatePlant(plant).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean result) {
                if(result){

                    Toast.makeText(getActivity(), "Image Updated", Toast.LENGTH_SHORT).show();
                }else {

                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void load_plants() {
        viewModel.getPlants().observe(getViewLifecycleOwner(), new Observer<List<Plant>>() {
            @Override
            public void onChanged(List<Plant> plantsList) {
                if(plantsList==null){
                    loader.dismiss();
                    layoutVisibility(View.VISIBLE, View.GONE,View.GONE);
                }else{
                   plants = plantsList;

                   adapter.setPlants(plants);
                    layoutVisibility(View.GONE, View.VISIBLE,View.VISIBLE);
                    loader.dismiss();
                }
            }
        });
    }

    private void layoutVisibility(int noPlantVisibility, int homeVisibility,int actionButtonVisibility) {
        binding.noPlantsLayout.setVisibility(noPlantVisibility);
        binding.scroller.setVisibility(homeVisibility);
        binding.addPlantButton.setVisibility(actionButtonVisibility);
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

        Drawable drawable = getResources().getDrawable(R.drawable.add_image);
       bitmap = ((BitmapDrawable) drawable).getBitmap();

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
               String plantName = addPlantDialogBinding.dialogEditText.getText().toString().trim();
               if(!Helper.containsOnlyAlphabets(plantName) || plantName.isEmpty()){
                   addPlantDialogBinding.dialogEditText.setError("Plant name should contain only alphabets");
               }else{
                   loader.show();
                   Plant plant = new Plant();
                   plant.setPlantName(plantName);
                   plant.setImage(viewModel.convertImageToBase64(bitmap));
                   addNewPlant(plant);
                   plants.add(plant);
                   adapter.setPlants(plants);
                   layoutVisibility(View.GONE,View.VISIBLE,View.VISIBLE);
                   loader.dismiss();
                   addPlantDialog.dismiss();
                   adapter.notifyDataSetChanged();
               }
            }
        });

        addPlantDialogBinding.imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickerIntent(Constants.PICK_IMAGE_REQUEST_CODE);
            }
        });
    }

    private void imagePickerIntent(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,requestCode);
    }

    private void addNewPlant(Plant plant) {
        viewModel.addNewPlant(plant).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean result) {
                if(result){
                    Toast.makeText(getActivity(), "New Plant Added.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

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

            } catch (Exception ex) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

       }
        else if(requestCode == Constants.CHANGE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            Uri filepath1 = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(filepath1);
                bitmap = BitmapFactory.decodeStream(inputStream);
                if (onActivityResultListener != null) {
                    onActivityResultListener.onActivityResult();
                }

            } catch (Exception ex) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else {
            loader.dismiss();
        }

    }

    public interface OnActivityResultListener {
        void onActivityResult();
    }

    public void setOnActivityResultListener(OnActivityResultListener listener) {
        this.onActivityResultListener = listener;
    }
}