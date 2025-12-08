package com.tfb.cbit.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ViewDefaultJoinBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.DefaultJoinTicket;
import com.tfb.cbit.utility.Utils;

import java.util.List;

public class DefaultJoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<DefaultJoinTicket.Contest> ticketList;
    private int viewType;

    public DefaultJoinAdapter(Context context, List<DefaultJoinTicket.Contest> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        ViewDefaultJoinBinding binding = ViewDefaultJoinBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
         return new FixedHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        FixedHolder fixedHolder = (FixedHolder) viewHolder;
        fixedHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getPrice()))));

        if (ticketList.get(i).isSelected()) {
            fixedHolder.binding.chkSelect.setChecked(true);
            fixedHolder.binding.chkSelect.setVisibility(View.VISIBLE);
        } else {
            fixedHolder.binding.chkSelect.setVisibility(View.VISIBLE);
            fixedHolder.binding.chkSelect.setChecked(false);
        }
        fixedHolder.binding.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, i);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }




    class FixedHolder extends RecyclerView.ViewHolder {


        ViewDefaultJoinBinding binding;

        public FixedHolder(@NonNull ViewDefaultJoinBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }
    }

}
