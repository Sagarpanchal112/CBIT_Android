package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tfb.cbit.databinding.ItemAllUserRequestBinding;
import com.tfb.cbit.models.GroupUserModel;
import java.util.List;

public class AllUserRequestAdapter extends RecyclerView.Adapter<AllUserRequestAdapter.ViewHolder> {
    private Context context;
    private List<GroupUserModel.Content> groupName;


    public AllUserRequestAdapter(Context context, List<GroupUserModel.Content> groupName) {
        this.context = context;
        this.groupName = groupName;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemAllUserRequestBinding binding = ItemAllUserRequestBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);



    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvUserName.setText(groupName.get(i).getFirstName() + " " + groupName.get(i).getLastName());
        viewHolder.binding.tvAccept.setVisibility(View.GONE);
        viewHolder.binding.tvDecline.setVisibility(View.GONE);


    }


    @Override
    public int getItemCount() {
        return groupName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemAllUserRequestBinding binding;


        public ViewHolder(@NonNull ItemAllUserRequestBinding binding) {
            super(binding.getRoot());
            this.binding =binding;
           // ButterKnife.bind(this, itemView);
        }
    }
}
