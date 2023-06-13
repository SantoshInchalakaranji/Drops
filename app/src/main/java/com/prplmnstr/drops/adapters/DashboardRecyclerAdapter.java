package com.prplmnstr.drops.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.prplmnstr.drops.R;
import com.prplmnstr.drops.databinding.DashboardListViewItemBinding;
import com.prplmnstr.drops.models.RecyclerModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardRecyclerAdapter extends RecyclerView.Adapter<DashboardRecyclerAdapter.ViewHolder> {
    private List<RecyclerModel> recyclerItems = new ArrayList<>();
    private OnItemClickListener listener;


    @NonNull
    @Override
    public DashboardRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DashboardListViewItemBinding listViewItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.dashboard_list_view_item,
                parent,
                false);

        return new ViewHolder(listViewItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardRecyclerAdapter.ViewHolder holder, int position) {
            RecyclerModel recyclerItem = recyclerItems.get(position);
            holder.listViewItemBinding.setRecyclerModel(recyclerItem);
    }

    @Override
    public int getItemCount() {
        return null!=recyclerItems? recyclerItems.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private DashboardListViewItemBinding listViewItemBinding;

        public ViewHolder(@NonNull DashboardListViewItemBinding listViewItemBinding) {
            super(listViewItemBinding.getRoot());
            this.listViewItemBinding = listViewItemBinding;

            listViewItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    if (listener!= null && clickedPosition != RecyclerView.NO_POSITION){
                        listener.onItemClick(recyclerItems.get(clickedPosition),clickedPosition);
                    }
                }
            });

            listViewItemBinding.dateTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    if (listener!= null && clickedPosition != RecyclerView.NO_POSITION){
                        listener.onItemClick(recyclerItems.get(clickedPosition),clickedPosition);
                    }
                }
            });
        }
    }

    public void setRecyclerItems(List<RecyclerModel> recyclerItems) {
        this.recyclerItems = recyclerItems;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(RecyclerModel recyclerModel,int clickPosition);

    }


    public void setListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
