package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemEasyContestTimeBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.AutoRenewModel;
import com.tfb.cbit.models.EasyJoinModel;
import com.tfb.cbit.utility.Utils;

import java.util.List;

public class AutoJoinPriceAdapter extends RecyclerView.Adapter<AutoJoinPriceAdapter.ViewHolder> {
    private Context context;
    private List<AutoRenewModel.Price> groupName;
    private OnItemClickListener onItemClickListener;

    public AutoJoinPriceAdapter(Fragment context, List<AutoRenewModel.Price> groupName) {
        this.context = context.getActivity();
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemEasyContestTimeBinding binding = ItemEasyContestTimeBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvGamePrice.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(groupName.get(i).getPrice()))));
        if (groupName.get(i).isSelected()) {
            viewHolder.binding.chkSelect.setChecked(true);
        } else {
            viewHolder.binding.chkSelect.setChecked(false);
        }
        viewHolder.binding.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, i);
            }
        });

    }


    @Override
    public int getItemCount() {
        return groupName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemEasyContestTimeBinding binding;

        public ViewHolder(@NonNull ItemEasyContestTimeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
