package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.databinding.ItemAllUserRequestBinding;
import com.tfb.cbit.models.AllUserRequestModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AllRequestAdapter extends RecyclerView.Adapter<AllRequestAdapter.ViewHolder> {
    private Context context;
    private List<AllUserRequestModel.AllRequest> groupName;
    private ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    private OnAcceptOrDescline onItemClickListener;


    public AllRequestAdapter(Context context, List<AllUserRequestModel.AllRequest> groupName) {
        this.context = context;
        this.groupName = groupName;
    }

    public interface OnAcceptOrDescline {
        public void onClick(int type, int pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemAllUserRequestBinding binding = ItemAllUserRequestBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);

        //  View view = LayoutInflater.from(context).inflate(R.layout.item_all_user_request, viewGroup, false);
        // return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvUserName.setText(groupName.get(i).getFirstName() + " " + groupName.get(i).getLastName());
        viewHolder.binding.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(1, i);
            }
        });
        viewHolder.binding.tvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(0, i);
            }
        });
    }

    public void setOnItemClickListener(OnAcceptOrDescline onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return groupName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemAllUserRequestBinding binding;

        public ViewHolder(@NonNull ItemAllUserRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            //  ButterKnife.bind(this, itemView);
        }
    }
}
