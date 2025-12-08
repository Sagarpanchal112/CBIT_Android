package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.activities.AllUserRequestListActivity;
import com.tfb.cbit.databinding.ItemMyGroupListBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.models.private_group.PrivateGroupResponse;

import java.util.List;

public class MyPrivateRoomTicketsAdapter extends RecyclerView.Adapter<MyPrivateRoomTicketsAdapter.ViewHolder> {
    private Context context;
    private List<PrivateGroupResponse.Content> groupName;

    public MyPrivateRoomTicketsAdapter(Context context, List<PrivateGroupResponse.Content> groupName) {
        this.context = context;
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemMyGroupListBinding binding = ItemMyGroupListBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvGroupName.setText(groupName.get(i).getPrivate_group_name());
        viewHolder.binding.tvCreate.setText("Total User :" + (groupName.get(i).getCount()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AllUserRequestListActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemMyGroupListBinding binding;

        public ViewHolder(@NonNull ItemMyGroupListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
