package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListUserBinding;
import com.tfb.cbit.models.ReferralDetails.UserDetails;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class ReferralUserListAdapter extends RecyclerView.Adapter<ReferralUserListAdapter.ViewHolder> {

    private Context context;
    private List<UserDetails> userList;

    public ReferralUserListAdapter(Context context, List<UserDetails> userLists) {
        this.context = context;
        this.userList = userLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListUserBinding binding = ListUserBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvName.setText(userList.get(i).getFirstName() + " " + userList.get(i).getMiddelName() + " " + userList.get(i).getLastName());
        Glide.with(context)
                .load(userList.get(i).getImagePath())
                .apply(Utils.getUserAvatarRequestOption())
                .into(viewHolder.binding.ivProfilePic);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        ListUserBinding binding;
        public ViewHolder(@NonNull ListUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
