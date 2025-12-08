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
import com.tfb.cbit.models.private_contest_detail.User;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>{

    private Context context;
    private List<User> userList;
    public UserListAdapter(Context context, List<User> userList){
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListUserBinding binding = ListUserBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvName.setText(userList.get(i).getName());
        Glide.with(context)
                .load(userList.get(i).getReferral_image())
                .apply(Utils.getUserAvatarReques())
                .into(viewHolder.binding.ivProfilePic);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ListUserBinding binding;

        public ViewHolder(@NonNull ListUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
