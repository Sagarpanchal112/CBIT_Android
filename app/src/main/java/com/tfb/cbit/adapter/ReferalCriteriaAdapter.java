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

import com.bumptech.glide.Glide;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemIdsBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.models.ReferalCriteria;
import com.tfb.cbit.models.ReferalCriteriaChart;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class ReferalCriteriaAdapter extends RecyclerView.Adapter<ReferalCriteriaAdapter.ViewHolder> {
    private Context context;
    private List<ReferalCriteria.Content> groupName;

    public ReferalCriteriaAdapter(Context context, List<ReferalCriteria.Content> groupName) {
        this.context = context;
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemIdsBinding binding = ItemIdsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Glide.with(context).load(groupName.get(i).getImage()).apply(Utils.getUserAvatarReques()).into(viewHolder.binding.tvName);
       /* if (groupName.get(i).getReferalLevelName().equals("Master")) {
            Glide.with(context).load(groupName.get(i).getImage()).apply(Utils.getUserAvatarReques()).into(viewHolder.tv_name);
        } else if (groupName.get(i).getReferalLevelName().equals("Super Master")) {
            Glide.with(context).load(R.drawable.sm_new).apply(Utils.getUserAvatarReques()).into(viewHolder.tv_name);
        } else if (groupName.get(i).getReferalLevelName().equals("Top Master")) {
            Glide.with(context).load(R.drawable.tm_new).apply(Utils.getUserAvatarReques()).into(viewHolder.tv_name);
        } else if (groupName.get(i).getReferalLevelName().equals("VIP")) {
            Glide.with(context).load(R.drawable.vip_new).apply(Utils.getUserAvatarReques()).into(viewHolder.tv_name);
        } else if (groupName.get(i).getReferalLevelName().equals("RD")) {
            Glide.with(context).load(R.drawable.rd_new).apply(Utils.getUserAvatarReques()).into(viewHolder.tv_name);
        } else {
            Glide.with(context).load(R.drawable.ddd).apply(Utils.getUserAvatarReques()).into(viewHolder.tv_name);
        }*/
        viewHolder.binding.tvRefferal.setText(groupName.get(i).getTotalReferal()+" referrals");
        viewHolder.binding.tvCommision.setText("Random Commission Upto "+groupName.get(i).getCommissionLevel() +"Levels");
      }

    @Override
    public int getItemCount() {
        return groupName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemIdsBinding binding;
        public ViewHolder(@NonNull ItemIdsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
