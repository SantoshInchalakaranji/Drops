package com.prplmnstr.drops.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.databinding.DashboardListViewItemBinding;
import com.prplmnstr.drops.databinding.PlantItemBinding;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.RecyclerModel;

import java.util.ArrayList;
import java.util.List;

public class PlantsRecyclerAdapter extends RecyclerView.Adapter<PlantsRecyclerAdapter.ViewHolder> {
    Context context;
    private List<Plant> plants = new ArrayList<>();
    @NonNull
    @Override
    public PlantsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        PlantItemBinding plantItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.plant_item,
                parent,
                false);

        return new ViewHolder(plantItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantsRecyclerAdapter.ViewHolder holder, int position) {
        Plant plant = plants.get(position);
        holder.plantItemBinding.setPlant(plant);
        Glide.with(context)
                .load(Uri.parse(plant.getImageUri()))
                .into(holder.plantItemBinding.plantImage);
    }

    @Override
    public int getItemCount() {
        return  null!=plants? plants.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private PlantItemBinding plantItemBinding;

        public ViewHolder(@NonNull PlantItemBinding plantItemBinding) {
            super(plantItemBinding.getRoot());
            this.plantItemBinding = plantItemBinding;

            plantItemBinding.menuImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context,view);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.plant_popup_menu, popupMenu.getMenu());
                    popupMenu.show();
                }
            });

        }
    }
    public void setPlants(List<Plant> plants) {
        this.plants = plants;
        notifyDataSetChanged();
    }
}
