package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListWinnerAndParticipantContestBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.private_contest_detail.Ticket;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class ParticipantAndWinnerAdapter extends RecyclerView.Adapter<ParticipantAndWinnerAdapter.ViewHolder>{

    private Context context;
    private List<Ticket> ticketList;
    private OnItemClickListener onItemClickListener;
    public ParticipantAndWinnerAdapter(Context context, List<Ticket> ticketList){
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListWinnerAndParticipantContestBinding binding = ListWinnerAndParticipantContestBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvAmount.setText(String.valueOf(Utils.INDIAN_RUPEES+" "+ticketList.get(i).getAmount()));
        viewHolder.binding.tvUsers.setText(String.valueOf(ticketList.get(i).getTotalJoin()));
        viewHolder.binding.tvWinnerUsers.setText(String.valueOf(ticketList.get(i).getWinners()));

        if(ticketList.get(i).isIsCancel()){
            viewHolder.binding.tvCancelled.setVisibility(View.VISIBLE);
            viewHolder.binding.btnView.setVisibility(View.GONE);
        }else{
            viewHolder.binding.tvCancelled.setVisibility(View.GONE);
            viewHolder.binding.btnView.setVisibility(View.VISIBLE);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ListWinnerAndParticipantContestBinding binding;
        public ViewHolder(@NonNull ListWinnerAndParticipantContestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding. btnView.setOnClickListener(new View.OnClickListener() {
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
