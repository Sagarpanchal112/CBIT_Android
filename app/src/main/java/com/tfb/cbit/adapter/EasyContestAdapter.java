package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemEasyContestBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemSwitchClickListener;
import com.tfb.cbit.models.EasyJoinModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;


public class EasyContestAdapter extends RecyclerView.Adapter<EasyContestAdapter.ViewHolder> {
    private Context context;
    private List<EasyJoinModel.Contest> groupName;
    private SessionUtil sessionUtil;
    private OnItemClickListener onItemClickListener;
    private OnItemSwitchClickListener onItemSwitchClickListener;
    public ArrayList<EasyJoinModel.Contest> selctedArray = new ArrayList<>();

    public EasyContestAdapter(Fragment context, List<EasyJoinModel.Contest> groupName) {
        this.context = context.getActivity();
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemEasyContestBinding binding = ItemEasyContestBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSwitchClickListener(OnItemSwitchClickListener onItemClickListener) {
        this.onItemSwitchClickListener = onItemClickListener;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvGameName.setText(groupName.get(i).getName());
        viewHolder.binding.tvGameTime.setText(Utils.getHHMM(groupName.get(i).getStartDate()));
        if (groupName.get(i).isSelected()) {
            viewHolder.binding.chkSelect.setChecked(true);
            viewHolder.binding.chkSelect.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.chkSelect.setVisibility(View.VISIBLE);
            viewHolder.binding.chkSelect.setChecked(false);
        }
        viewHolder.binding.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, i);
            }
        });
        if (groupName.get(i).isSwitchSelected()) {
            viewHolder.binding.switchAutoRenew.setChecked(true);
        } else {
            viewHolder.binding.switchAutoRenew.setChecked(false);
        }
        viewHolder.binding.switchAutoRenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.binding.switchAutoRenew.isChecked()) {
                    onItemSwitchClickListener.onItemClick(v, i, 1);
                } else {
                    onItemSwitchClickListener.onItemClick(v, i, 0);

                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return groupName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemEasyContestBinding binding;
        public ViewHolder(@NonNull ItemEasyContestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
