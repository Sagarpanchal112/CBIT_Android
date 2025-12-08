package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemOwnTicketBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.models.approch.UserContest;

import java.util.List;

public class UserTicketsAdapter extends RecyclerView.Adapter<UserTicketsAdapter.ViewHolder> {

    private Context context;
    private List<UserContest> contentList;
    public int row_index = -1;

    public UserTicketsAdapter(Context context, List<UserContest> sloteList) {
        this.context = context;
        this.contentList = sloteList;
    }

    public UserContest getSelected() {
        return contentList.get(row_index);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemOwnTicketBinding binding = ItemOwnTicketBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvCashback.setText(contentList.get(i).getRedenption_to());
        viewHolder.binding.tvJticketNo.setText(contentList.get(i).getTicket_number());
        viewHolder.binding.tvTicketNo.setText(contentList.get(i).getWaiting());
        viewHolder.binding.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = i;
                notifyDataSetChanged();
            }
        });
        if (row_index == i) {
            viewHolder.binding.chkSelect.setChecked(true);
         } else {
            viewHolder.binding.chkSelect.setChecked(false);
         }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ItemOwnTicketBinding binding;

        public ViewHolder(@NonNull ItemOwnTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
