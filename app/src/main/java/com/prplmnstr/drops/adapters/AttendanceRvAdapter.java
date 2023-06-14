package com.prplmnstr.drops.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.prplmnstr.drops.R;
import com.prplmnstr.drops.databinding.AttendanceItemBinding;
import com.prplmnstr.drops.databinding.DashboardListViewItemBinding;
import com.prplmnstr.drops.databinding.PlantItemBinding;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.RecyclerModel;
import com.prplmnstr.drops.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AttendanceRvAdapter extends RecyclerView.Adapter<AttendanceRvAdapter.ViewHolder>{
    private List<Attendance> attendanceList = new ArrayList<>();
    private OnItemClickListener listener;
    private Resources resources;
    Context context;
    PopupMenu popupMenu;
    @NonNull
    @Override
    public AttendanceRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        AttendanceItemBinding attendanceItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.attendance_item,
                parent,
                false
        );

        return new ViewHolder(attendanceItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceRvAdapter.ViewHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);
        holder.attendanceItemBinding.setAttendance(attendance);
        Drawable background = resources.getDrawable(R.drawable.attendace_gray_box);
        if(attendance.getAttendance()== Constants.NO_ATTENDANCE){

            holder.attendanceItemBinding.absentLayout.setBackground(background);
            holder.attendanceItemBinding.presentLayout.setBackground(background);
        }else if(attendance.getAttendance()== Constants.PRESENT){

            Drawable present = resources.getDrawable(R.drawable.present_bg);
            holder.attendanceItemBinding.absentLayout.setBackground(background);
            holder.attendanceItemBinding.presentLayout.setBackground(present);
            holder.attendanceItemBinding.P.setTextColor((resources.getColor(R.color.white)));
        }else if(attendance.getAttendance()== Constants.ABSENT){
            Drawable absent = resources.getDrawable(R.drawable.absent_bg);
            holder.attendanceItemBinding.absentLayout.setBackground(absent);
            holder.attendanceItemBinding.presentLayout.setBackground(background);
            holder.attendanceItemBinding.A.setTextColor((resources.getColor(R.color.white)));
        }
    }

    @Override
    public int getItemCount() {
        return null!=attendanceList? attendanceList.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AttendanceItemBinding attendanceItemBinding;


        public ViewHolder(@NonNull AttendanceItemBinding attendanceItemBinding) {
            super(attendanceItemBinding.getRoot());
            this.attendanceItemBinding = attendanceItemBinding;


            attendanceItemBinding.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    if (listener!= null && clickedPosition != RecyclerView.NO_POSITION){
                        listener.onItemClick(attendanceList.get(clickedPosition),clickedPosition);
                    }
                }
            });

            attendanceItemBinding.presentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    Drawable background = resources.getDrawable(R.drawable.attendace_gray_box);
                    Drawable present = resources.getDrawable(R.drawable.present_bg);
                    attendanceItemBinding.absentLayout.setBackground(background);
                   attendanceItemBinding.presentLayout.setBackground(present);
                    attendanceItemBinding.P.setTextColor((resources.getColor(R.color.white)));
                    attendanceItemBinding.A.setTextColor((resources.getColor(R.color.black)));
                    listener.onPresentClick(attendanceList.get(clickedPosition),clickedPosition);
                }
            });

            attendanceItemBinding.threeDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupMenu = new PopupMenu(context,view);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.delete_worker_menu, popupMenu.getMenu());
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                           listener.onDeleteRequest(attendanceItemBinding.getAttendance());


                            return true;
                        }
                    });
                }
            });



            attendanceItemBinding.absentLayout.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    Drawable background = resources.getDrawable(R.drawable.attendace_gray_box);
                    Drawable absent = resources.getDrawable(R.drawable.absent_bg);
                    attendanceItemBinding.absentLayout.setBackground(absent);
                    attendanceItemBinding.presentLayout.setBackground(background);
                    attendanceItemBinding.P.setTextColor((resources.getColor(R.color.black)));
                    attendanceItemBinding.A.setTextColor((resources.getColor(R.color.white)));
                    listener.onAbsentClick(attendanceList.get(clickedPosition),clickedPosition);
                }
            });
        }
    }

    public void setAttendanceList(List<Attendance> list) {
        this.attendanceList = list;
        notifyDataSetChanged();
    }

    public void setResources(Resources resources) {
        this.resources = resources;
        notifyDataSetChanged();
    }



    public interface OnItemClickListener{
        void onItemClick(Attendance attendance,int clickPosition);
        void onPresentClick(Attendance attendance, int position);
        void onAbsentClick(Attendance attendance,int position);

        void onDeleteRequest(Attendance attendance);
    }

    public void setListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
