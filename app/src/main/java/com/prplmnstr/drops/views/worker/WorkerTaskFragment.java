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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.Transaction;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.DashboardRecyclerAdapter;
import com.prplmnstr.drops.adapters.PlantsRecyclerAdapter;
import com.prplmnstr.drops.databinding.AddRecordDialogBinding;
import com.prplmnstr.drops.databinding.AddUnitDialogBinding;
import com.prplmnstr.drops.databinding.FragmentDashboardBinding;
import com.prplmnstr.drops.databinding.FragmentWorkerTaskBinding;
import com.prplmnstr.drops.models.Date;
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
    private Dialog editRecordDialog;
    private ArrayAdapter spinnerAdapter;
    private AddRecordDialogBinding addRecordDialogBinding;
    private int taskCount;

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





      loadUI();



    }

    private void initialize_recycler() {
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new DashboardRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setListener(new DashboardRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerModel recyclerModel, int clickPosition) {

                loadDialog(records.get(clickPosition));
            }
        });
    }
    public void initialize_loader() {
        loader = new Dialog(getActivity());
        loader.setContentView(R.layout.loader);
        loader.getWindow().setBackgroundDrawableResource(R.color.transparent);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loader.setCancelable(false);
        loader.getWindow().getAttributes().windowAnimations = R.style.animation;
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
                loader.show();
                String plantName = plants.get(i);
               load_recycler_items(plantName);

            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void loadDialog(Record record) {

        addRecordDialogBinding = AddRecordDialogBinding.inflate(LayoutInflater.from(getContext()));


        editRecordDialog = new Dialog(getActivity());
        editRecordDialog.setContentView(addRecordDialogBinding.getRoot());
        editRecordDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        editRecordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        editRecordDialog.setCancelable(true);
        editRecordDialog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;

       addRecordDialogBinding.unitNameTV.setText(record.getUnitName());
       addRecordDialogBinding.openingET.setText(String.valueOf(record.getClosing()));
        addRecordDialogBinding.waterOpening.setText(String.valueOf(record.getWaterClose()));
        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Integer closeInt;
                if(s!=null){
                    String closeString = s.toString();
                    if(!closeString.isEmpty()){
                      closeInt   = Integer.parseInt(closeString);
                    }else{
                        closeInt = 0;
                    }

                    Integer amount = Math.abs(closeInt-record.getClosing());
                    addRecordDialogBinding.totalAmountTV.setText(String.valueOf(amount.toString()));
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }


        };

        addRecordDialogBinding.closingET.addTextChangedListener(inputTextWatcher);
        if(record.getType().equals(Constants.RECHARGE_UNIT)){
            addRecordDialogBinding.waterClosing.setText("0");
            addRecordDialogBinding.waterOpening.setText("0");
            addRecordDialogBinding.wateLayout.setVisibility(View.GONE);

        }else {
            addRecordDialogBinding.wateLayout.setVisibility(View.VISIBLE);
        }

        addRecordDialogBinding.dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String open =  addRecordDialogBinding.openingET.getText().toString();
                String close =  addRecordDialogBinding.closingET.getText().toString();
                String waterOpen =  addRecordDialogBinding.waterOpening.getText().toString();
                String waterClose =  addRecordDialogBinding.waterClosing.getText().toString();
                
                if(open.isEmpty() || close.isEmpty()|| waterOpen.isEmpty()|| waterClose.isEmpty()){
                    Toast.makeText(getActivity(), "Enter All Data", Toast.LENGTH_SHORT).show();
                }else{
                    Date  today = Helper.getTodayDateObject();
                   // String recordDate = Helper.getDateInStringFormat(record.getDay(),record.getMonth(),record.getYear());
                    int amount = (Math.abs(Integer.parseInt(close)-Integer.parseInt(open)));
                    int waterSupply = (Math.abs(Integer.parseInt(waterClose)-Integer.parseInt(waterOpen)));
                    
                    
                    record.setAmount(amount);
                    record.setOpening(Integer.parseInt(open));
                    record.setClosing(Integer.parseInt(close));
                    record.setWaterSupply(waterSupply);
                    record.setWaterOpen(Integer.parseInt(waterOpen));
                    record.setWaterClose(Integer.parseInt(waterClose));
                    record.setDay(today.getDay());
                    record.setMonth(today.getMonth());
                    record.setYear(today.getYear());
                    loader.show();
                   viewModel.addRecord(record).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                       @Override
                       public void onChanged(Boolean result) {
                           loader.dismiss();
                           if(result){
                               Toast.makeText(getActivity(), "Record submit successful", Toast.LENGTH_SHORT).show();
                               load_recycler_items(record.getPlantName());
                           }else{
                               Toast.makeText(getActivity(), "Record submit failed", Toast.LENGTH_SHORT).show();
                           }
                           editRecordDialog.dismiss();
                       }
                   });
                }
            }
        });

        editRecordDialog.show();
    }



    private void load_recycler_items(String plantName) {
        Toast.makeText(getActivity(), "Loading please wait...", Toast.LENGTH_LONG).show();
        viewModel.getRecords(plantName)
                .observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
                    @Override
                    public void onChanged(List<Record> recordList) {


                        records = recordList;
                        String drawableName = "checkmark"; // Replace with the name of your drawable
                        int checkMarkResourseId = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
                        recyclerItems = viewModel.getRecycleItems(records,getResources(),checkMarkResourseId);

                        adapter.setRecyclerItems(recyclerItems);

                        binding.container.setVisibility(View.VISIBLE);
                        loader.dismiss();


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


    private void loadUI() {
        viewModel.getPlants().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> plantList) {
                plants = plantList;
                set_spinner(plants);
            }
        });
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(TaskFragmentViewModel.class);

    }


}