package com.prplmnstr.drops.views.admin;



import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prplmnstr.drops.R;

import com.prplmnstr.drops.adapters.PlantsRecyclerAdapter;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.databinding.FragmentHomeBinding;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.viewModel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private PlantsRecyclerAdapter adapter;
    private List<Plant>  plants = new ArrayList<>();

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
        //show_loader();
        loadRecycler();
    }

    private void loadRecycler() {
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new PlantsRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        Uri imageUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.add_image);
        plants.add(new Plant("Chikodi", imageUri.toString()));
        plants.add(new Plant("Nippani", imageUri.toString()));
        plants.add(new Plant("Nippani", imageUri.toString()));
        adapter.setPlants(plants);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}