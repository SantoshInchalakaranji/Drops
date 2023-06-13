package com.prplmnstr.drops.views.worker;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.DashboardRecyclerAdapter;
import com.prplmnstr.drops.adapters.PlantsRecyclerAdapter;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.databinding.FragmentWorkerTaskBinding;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;
import com.prplmnstr.drops.viewModel.DashboardViewModel;
import com.prplmnstr.drops.viewModel.TaskFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class WorkerTaskFragment extends Fragment {

    private FragmentWorkerTaskBinding binding;
    private TaskFragmentViewModel viewModel;
    private List<Record> records = new ArrayList<>();
    private List<RecyclerModel> recyclerItems = new ArrayList<>();
    private List<String> plants = new ArrayList<>();
    private RecyclerView recyclerView;
    private DashboardRecyclerAdapter adapter;
    private Dialog loader;
    private Dialog addPlantDialog;
    private ArrayAdapter spinnerAdapter;
    private int amountCollection,waterSupply,taskCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkerTaskBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize_loader();
        loader.show();
        initialize_recycler();





        viewModel.getRecords()
                .observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> recordList) {
                records = recordList;
                String drawableName = "checkmark"; // Replace with the name of your drawable
                int checkMarkResourseId = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
                recyclerItems = viewModel.getRecycleItems(records,getResources(),checkMarkResourseId);

                adapter.setRecyclerItems(recyclerItems);
                adapter.notifyDataSetChanged();
                loader.dismiss();
                viewModel.getPlants().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> plantList) {
                        plants = plantList;
                        set_spinner(plants);
                    }
                });

                viewModel.getTaskCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        taskCount = integer;
                        binding.percentageTV.setText(taskCount+"/"+recyclerItems.size());
                        binding.linearProgressBar.setProgress(taskCount);
                        binding.linearProgressBar.setMax(recordList.size());

                    }
                });

                viewModel.getCollection().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        binding.totalAmountTV.setText("₹ "+integer);
                    }
                });
                viewModel.getWaterSupply().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        binding.waterDispenseTV.setText("Water dispense(L) : "+integer);
                    }
                });

                binding.dayTV.setText(Helper.getTodayDateObject().getDateInStringFormat());
            }
        });

    }

    private void initialize_recycler() {
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new DashboardRecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void set_spinner(List<String> plants) {
        // set spinner
        spinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item,
                plants);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(spinnerAdapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(TaskFragmentViewModel.class);

    }
}