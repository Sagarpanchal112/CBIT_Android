package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemAllWaitingJtcktBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickJTicket;
import com.tfb.cbit.models.RedeemJTicket.Contest;

import java.util.List;


public class WaitingAllJTicktAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<Contest> packageList;
    private OnItemClickJTicket onItemClickJTicket;
    RequestOptions requestOptions;

    public WaitingAllJTicktAdapter(Context context, List<Contest> packageList) {
        this.context = context;
        this.packageList = packageList;
        requestOptions = new RequestOptions();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       ItemAllWaitingJtcktBinding binding = ItemAllWaitingJtcktBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new PackageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        //  if(viewHolder instanceof PackageHolder){
        PackageHolder packageHolder = (PackageHolder) viewHolder;
        packageHolder.binding.tvPurchasePrice.setText(packageList.get(i).getName());
        packageHolder.binding.tvInrCash.setText("Current Waiting : " + packageList.get(i).getWaiting());
        packageHolder.binding.tvMyTicket.setText("My j tickets : " + packageList.get(i).getApplyCount());

        Glide.with(context)
                .load(packageList.get(i).getImage())
                .apply(requestOptions)
                .into(packageHolder.binding.ivJTicket);

        packageHolder.binding.btnjwaitingroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickJTicket.onItemClick(String.valueOf(packageList.get(i).getPrice()), packageList.get(i).getId(), "WROOM",0);

            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public void setOnItemClickListener(OnItemClickJTicket onItemClickListener) {
        this.onItemClickJTicket = onItemClickListener;
    }

    class PackageHolder extends RecyclerView.ViewHolder {


        ItemAllWaitingJtcktBinding binding;

        public PackageHolder(@NonNull ItemAllWaitingJtcktBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
