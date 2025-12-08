package com.tfb.cbit.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.SelectedListPackagesBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contest_pkg.ContestPrice;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.Utils;

import java.util.List;

public class SelectedPackagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ContestPrice> packageList;
    private OnItemClickListener onItemClickListener;
    private boolean isMyPkg;
    public int Validity;

    public SelectedPackagesAdapter(Context context, List<ContestPrice> packageList, boolean isMyPkg,int Validity) {
        this.context = context;
        this.packageList = packageList;
        this.isMyPkg = isMyPkg;
        this.Validity = Validity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        SelectedListPackagesBinding binding = SelectedListPackagesBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new PackageHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        PackageHolder packageHolder = (PackageHolder) viewHolder;
        packageHolder.binding.tvPackagesDetail.setText("Entry fee" + Utils.getCurrencyFormat(String.valueOf(packageList.get(i).getAmount())) + " @ " + Utils.getCurrencyFormat(String.valueOf(packageList.get(i).getAmount()*Validity)) );

    }


    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class PackageHolder extends RecyclerView.ViewHolder {
        SelectedListPackagesBinding binding;
        public PackageHolder(@NonNull SelectedListPackagesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
