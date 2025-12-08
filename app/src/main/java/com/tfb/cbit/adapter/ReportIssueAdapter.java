package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ViewReportsBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemSwitchClickListener;
import com.tfb.cbit.models.EasyJoinModel;
import com.tfb.cbit.models.ReportIssueModel;
import com.tfb.cbit.utility.SessionUtil;

import java.util.ArrayList;
import java.util.List;


public class ReportIssueAdapter extends RecyclerView.Adapter<ReportIssueAdapter.ViewHolder> {
    private Context context;
    private List<ReportIssueModel.Content> groupName;
    private SessionUtil sessionUtil;
    private OnItemClickListener onItemClickListener;
    private OnItemSwitchClickListener onItemSwitchClickListener;
    public ArrayList<EasyJoinModel.Contest> selctedArray = new ArrayList<>();

    public ReportIssueAdapter(Context context, List<ReportIssueModel.Content> groupName) {
        this.context = context;
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewReportsBinding binding = ViewReportsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
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
        viewHolder.binding.tvIssueName.setText(groupName.get(i).getReport_title());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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
        ViewReportsBinding binding;
        public ViewHolder(@NonNull ViewReportsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
