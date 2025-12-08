package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ViewAutoJoinBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemSwitchClickListener;
import com.tfb.cbit.models.AutoRenewModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class AutoJoinAdapter extends RecyclerView.Adapter<AutoJoinAdapter.ViewHolder> {
    private Context context;
    private List<AutoRenewModel.AutorenewTable> groupName;
    private SessionUtil sessionUtil;
    private OnItemSwitchClickListener onItemSwitchClickListener;
    private OnItemClickListener onItemClickListener;
    public ArrayList<AutoRenewModel.Content> selctedArray = new ArrayList<>();

    public AutoJoinAdapter(Fragment context, List<AutoRenewModel.AutorenewTable> groupName) {
        this.context = context.getActivity();
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewAutoJoinBinding binding = ViewAutoJoinBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }


    public void setOnItemSwitchClickListener(OnItemSwitchClickListener onItemClickListener) {
        this.onItemSwitchClickListener = onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvTime.setText(groupName.get(i).getTwentyfourhourformat());
        viewHolder.binding.tvEntryFees.setText(groupName.get(i).getContest());
        if (groupName.get(i).getContest_status() == 1) {
            viewHolder.binding.switchAutoRenew.setChecked(true);
        } else {
            viewHolder.binding.switchAutoRenew.setChecked(false);
        }
        viewHolder.binding.switchAutoRenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupName.get(i).getContest_price().size() == 0) {
                    Utils.showToast(context, "Click on + to add contests");
                    viewHolder.binding.switchAutoRenew.setClickable(false);
                    viewHolder.binding.switchAutoRenew.setFocusable(false);
                    viewHolder.binding.switchAutoRenew.setChecked(false);

                } else {
                    if (viewHolder.binding.switchAutoRenew.isChecked()) {
                        onItemSwitchClickListener.onItemClick(v, i, 1);
                    } else {
                        onItemSwitchClickListener.onItemClick(v, i, 0);

                    }
                }
            }
        });

        viewHolder.binding.imgPlus.setOnClickListener(new View.OnClickListener() {
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

        ViewAutoJoinBinding binding;

        public ViewHolder(@NonNull ViewAutoJoinBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
