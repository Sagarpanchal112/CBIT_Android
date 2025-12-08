package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListPrivateRoomTicketsBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.AllRequestModel;
import com.tfb.cbit.models.contestdetails.BoxJson;
import com.tfb.cbit.models.private_group.PrivateGroupResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrivateRoomTicketsAdapter extends RecyclerView.Adapter<PrivateRoomTicketsAdapter.ViewHolder> {
    private Context context;
    private List<AllRequestModel.AllRequest> groupName;
    private ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    private OnItemClickListener onItemClickListener;


    public PrivateRoomTicketsAdapter(Context context, List<AllRequestModel.AllRequest> groupName) {
        this.context = context;
        this.groupName = groupName;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListPrivateRoomTicketsBinding binding = ListPrivateRoomTicketsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
         return new ViewHolder(binding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvGroupName.setText("Group Name:" + groupName.get(i).getGroup_name()+"("+groupName.get(i).getTicketSold()+" Tickets Sold)");
        viewHolder.binding.tvGroupTicket.setText("Game Type:" + groupName.get(i).getGameType());
        viewHolder.binding.tvLockStyle.setText("Lock Style:" + groupName.get(i).getLock_style());
        if (groupName.get(i).getGame_type().equals("spinning_machine")) {
            viewHolder.binding.imgGameType.setImageDrawable(context.getDrawable(R.drawable.slot_machine));
            viewHolder.binding.imgLockStyle.setImageDrawable(context.getDrawable(R.drawable.ic_paper_chit));
         } else {
            viewHolder.binding.imgGameType.setImageDrawable(context.getDrawable(R.drawable.classic_grid));
            viewHolder.binding.imgLockStyle.setImageDrawable(context.getDrawable(R.drawable.classic_grid));
        }
        viewHolder.binding.tvJoinContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, i);
            }
        });
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public int getItemCount() {
        return groupName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ListPrivateRoomTicketsBinding binding;

        public ViewHolder(@NonNull ListPrivateRoomTicketsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
