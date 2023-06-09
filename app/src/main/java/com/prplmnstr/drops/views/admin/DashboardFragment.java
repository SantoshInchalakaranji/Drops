package com.prplmnstr.drops.views.admin;

import android.app.Dialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prplmnstr.drops.MainActivity;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.DashboardRecyclerAdapter;
import com.prplmnstr.drops.databinding.DashboardListViewItemBinding;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.databinding.FragmentSignInBinding;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.viewModel.AuthViewModel;
import com.prplmnstr.drops.viewModel.DashboardViewModel;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private DashboardViewModel viewModel;
    private List<RecyclerModel> recyclerItems;
    private Dialog loader;


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


            show_loader();


            viewModel.getRecyclerItems().observe(getViewLifecycleOwner(), new Observer<List<RecyclerModel>>() {
                @Override
                public void onChanged(List<RecyclerModel> recyclerModels) {
                    recyclerItems = recyclerModels;
                    loadRecyclerView();
                    binding.container.setVisibility(View.VISIBLE);
                    loader.dismiss();
                }
            });
            int progress = viewModel.getProgress();
            binding.linearProgressBar.setProgress(progress);
            binding.linearProgressBar.setMax(24);
            double percentage = (progress/24.0)*100;
            binding.percentageTV.setText((int) percentage+"%");



    }

    private void show_loader() {
        loader = new Dialog(getActivity());
        loader.setContentView(R.layout.loader);
        loader.getWindow().setBackgroundDrawableResource(R.color.transparent);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loader.setCancelable(false);
        loader.getWindow().getAttributes().windowAnimations = R.style.animation;
        loader.show();
    }

    private void loadRecyclerView() {



        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new DashboardRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        //For random image resourse
        final TypedArray imgs = getResources().obtainTypedArray(R.array.images);
        final Random rand = new Random();
        for(RecyclerModel item : recyclerItems){
             int rndInt = rand.nextInt(imgs.length());
            int resID = imgs.getResourceId(rndInt, R.drawable.statistics);
            item.setImageIndex(resID);

        }
        adapter.setRecyclerItems(recyclerItems);



    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(DashboardViewModel.class);
    }
}

