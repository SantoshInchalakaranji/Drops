package com.prplmnstr.drops.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.prplmnstr.drops.R;
import com.prplmnstr.drops.databinding.PlantItemBinding;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.views.admin.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class PlantsRecyclerAdapter extends RecyclerView.Adapter<PlantsRecyclerAdapter.ViewHolder> {
    Context context;
    PopupMenu popupMenu;
    ChangeImageLister changeImageLister;
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
//        Glide.with(context)
//                .load(Uri.parse(plant.getImageUri()))
//                .into(holder.plantItemBinding.plantImage);
        Bitmap bitmap = Constants.stringToBitmap(plant.getImage());
        holder.plantItemBinding.plantImage.setImageBitmap(bitmap);
        Log.i("TAG", "list size "+String.valueOf(plants.size())+ plant.getPlantName());
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
                    popupMenu = new PopupMenu(context,view);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.plant_popup_menu, popupMenu.getMenu());
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Plant newPlant = changeImageLister.changeImageRequest(plantItemBinding.getPlant());
                            plantItemBinding.setPlant(newPlant);

                            return true;
                        }
                    });
                }
            });

            plantItemBinding.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   changeImageLister.plantItemClicked(plantItemBinding.getPlant().getPlantName());
                }
            });

        }
    }
    public void setPlants(List<Plant> plants) {
        this.plants = plants;
        notifyDataSetChanged();
    }

    public void setChangeImageListener(ChangeImageLister listener){
        this.changeImageLister = listener;
    }

    public interface ChangeImageLister{
        Plant changeImageRequest(Plant plant);
        void plantItemClicked(String plantName);
    }
}
