package com.prplmnstr.drops.views.admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.prplmnstr.drops.MainActivity;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.adapters.DashboardRecyclerAdapter;
import com.prplmnstr.drops.databinding.AddRecordDialogBinding;
import com.prplmnstr.drops.databinding.FragmentDailyInfoBinding;
import com.prplmnstr.drops.databinding.FragmentWorkerTaskBinding;
import com.prplmnstr.drops.databinding.ItemDetailsDialogBinding;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Record;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.repository.admin.DailyInfoRepository;
import com.prplmnstr.drops.utils.CreatePdfReport;
import com.prplmnstr.drops.utils.Helper;
import com.prplmnstr.drops.viewModel.DailyInfoViewModel;
import com.prplmnstr.drops.viewModel.TaskFragmentViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DailyInfoFragment extends Fragment implements NavController.OnDestinationChangedListener{

    private FragmentDailyInfoBinding binding;
    private DailyInfoViewModel viewModel;
    private List<Record> records = new ArrayList<>();
    private List<RecyclerModel> recyclerItems = new ArrayList<>();
    private Dialog recordInfoDialog;
    private RecyclerView recyclerView;
    private ItemDetailsDialogBinding itemDetailsDialogBinding;
    private DashboardRecyclerAdapter adapter;
    private Dialog loader;
    private final String  plantName = DashboardFragment.PLANT_NAME;
    private int taskCount;
    private NavController navController;
    private Date recordDate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(DailyInfoViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDailyInfoBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize_loader();
        loader.show();
        initialize_recycler();
        navController = Navigation.findNavController(view);
        navController.addOnDestinationChangedListener(this);
        recordDate = Helper.getTodayDateObject();
        load_recycler_items(plantName,recordDate);


        binding.backButtonDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigateUp();
            }
        });



        binding.spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        binding.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                CreatePdfReport createPdfReport = new CreatePdfReport(getContext(),getResources(),getActivity());

                createPdfReport.downloadableData(plantName,recordDate);
            }
        });
    }

    private void load_recycler_items(String plantName, Date date) {
        Toast.makeText(getActivity(), "Loading please wait...", Toast.LENGTH_LONG).show();
        viewModel.getRecords(plantName,date)
                .observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
                    @Override
                    public void onChanged(List<Record> recordList) {


                        records = recordList;
                        String drawableName = "checkmark"; // Replace with the name of your drawable
                        int checkMarkResourseId = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
                        if(date.getDateInStringFormat().equals(Helper.getTodayDateObject().getDateInStringFormat())){
                            recyclerItems = viewModel.getRecycleItems(records,getResources(),checkMarkResourseId);
                            binding.dayTV.setText("Today");

                        }else{
                            recyclerItems = viewModel.getPreviousDate(records,getResources(),checkMarkResourseId);
                            binding.dayTV.setText(date.getDateInStringFormat());
                        }


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



                    }
                });
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

            @Override
            public void onItemLongClick(RecyclerModel recyclerModel, int clickPosition) {

            }
        });
    }



    private void loadDialog(Record record) {
        itemDetailsDialogBinding = ItemDetailsDialogBinding.inflate(LayoutInflater.from(getContext()));


        recordInfoDialog= new Dialog(getActivity());
        recordInfoDialog.setContentView(itemDetailsDialogBinding.getRoot());
        recordInfoDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        recordInfoDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        recordInfoDialog.setCancelable(true);
        recordInfoDialog.getWindow().getAttributes().windowAnimations = R.style.animation_popup;

        itemDetailsDialogBinding.cashTV.setText("₹ "+String.valueOf(record.getAmount()));
        itemDetailsDialogBinding.openingTV.setText(String.valueOf(record.getOpening()));
        itemDetailsDialogBinding.closingTV.setText(String.valueOf(record.getClosing()));
        itemDetailsDialogBinding.dailySaleTV.setText("+ "+String.valueOf(record.getWaterSupply()));
        itemDetailsDialogBinding.dispOpenTV.setText(String.valueOf(record.getWaterOpen()));
        itemDetailsDialogBinding.dispCloseTv.setText(String.valueOf(record.getWaterClose()));
        itemDetailsDialogBinding.unitNameTV.setText(String.valueOf(record.getUnitName()));

        itemDetailsDialogBinding.dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordInfoDialog.dismiss();
            }
        });
        recordInfoDialog.show();
    }

    public void initialize_loader() {
        loader = new Dialog(getActivity());
        loader.setContentView(R.layout.loader);
        loader.getWindow().setBackgroundDrawableResource(R.color.transparent);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loader.setCancelable(false);
        loader.getWindow().getAttributes().windowAnimations = R.style.animation;
    }

    public void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);


        // Create a DatePickerDialog and set the listener
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Update the selected date in your UI
                        recordDate = new Date();
                        recordDate.setDay(day);
                        recordDate.setMonth(month+1);
                        recordDate.setYear(year);
                        recordDate.setDateInStringFormat(Helper.getDateInStringFormat(day,month,year));
                        binding.spinner.setText(recordDate.getDateInStringFormat());


                        try {
                            loader.show();
                            load_recycler_items(plantName, recordDate);
                        }catch (Exception e){
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, year, month, dayOfMonth);

        // Show the date picker dialog
        datePickerDialog.show();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {

    }
}