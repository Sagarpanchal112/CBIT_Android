package com.tfb.cbit.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemRedeemJtcktBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnClickGraph;
import com.tfb.cbit.interfaces.OnItemClickJTicket;
import com.tfb.cbit.models.RedeemJTicket.Contest;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class RedeemJTicktAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<Contest> packageList;
    private OnItemClickJTicket onItemClickJTicket;
    RequestOptions requestOptions;
    OnClickGraph clickGraph;

    public RedeemJTicktAdapter(Context context, List<Contest> packageList) {
        this.context = context;
        this.packageList = packageList;
        requestOptions = new RequestOptions();
      //  this.clickGraph = (OnClickGraph) context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        ItemRedeemJtcktBinding binding = ItemRedeemJtcktBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new PackageHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        //  if(viewHolder instanceof PackageHolder){
        PackageHolder packageHolder = (PackageHolder) viewHolder;
        packageHolder.binding.tvPurchasePrice.setText("Redeem @ : " + packageList.get(i).getPrice()+" Points");
        packageHolder.binding.tvTicktName.setText(packageList.get(i).getName());
        packageHolder.binding.tvInrCash.setText("Get cashback up-to: " + Utils.INDIAN_RUPEES + " " + packageList.get(i).getRedenptionto());

        Glide.with(context)
                .load(packageList.get(i).getImage())
                .apply(requestOptions)
                .into(packageHolder.binding.ivJTicket);
        packageHolder.binding.btnJRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickJTicket.onItemClick(String.valueOf(packageList.get(i).getPrice()),
                        packageList.get(i).getId(), "REDEEM",packageList.get(i).getWaiting());

            }
        });
        packageHolder.binding.btnjwaitingroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickJTicket.onItemClick(String.valueOf(packageList.get(i).getPrice()), packageList.get(i).getId(), "WROOM",packageList.get(i).getWaiting());

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

        ItemRedeemJtcktBinding binding;

        public PackageHolder(@NonNull ItemRedeemJtcktBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
