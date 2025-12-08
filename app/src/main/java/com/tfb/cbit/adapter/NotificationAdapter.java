package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListNotificationBinding;
import com.tfb.cbit.models.notification.Content;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context context;
    private List<Content> notificationList;
    public NotificationAdapter(Context context, List<Content> notificationList){
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListNotificationBinding binding = ListNotificationBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvNotificationTitle.setText(notificationList.get(i).getMessage());
        viewHolder.binding.tvNotificationTime.setText(notificationList.get(i).getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ListNotificationBinding binding;
        public ViewHolder(@NonNull ListNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
