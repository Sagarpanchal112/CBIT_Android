package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListHostedContestBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.hostedcontest.Content;

import java.util.List;

public class HostedContestAdapter extends RecyclerView.Adapter<HostedContestAdapter.ViewHolder>{

    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Content> hostedList;
    public HostedContestAdapter(Context context, List<Content> hostedList) {
        this.context = context;
        this.hostedList = hostedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListHostedContestBinding binding = ListHostedContestBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvContestName.setText(hostedList.get(i).getName());
        viewHolder.binding.tvTime.setText(hostedList.get(i).getStartTime());
        viewHolder.binding.tvDate.setText(hostedList.get(i).getStartDate());
    }

    @Override
    public int getItemCount() {
        return hostedList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ListHostedContestBinding binding;
        public ViewHolder(@NonNull ListHostedContestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, pos);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, pos);
                    }
                }
            });
        }
    }
}
