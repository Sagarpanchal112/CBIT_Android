package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListPackagesTicketBinding;
import com.tfb.cbit.models.contest_pkg.ContestPrice;
import com.tfb.cbit.utility.Utils;

import java.util.List;

public class PackagesTicketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ContestPrice> packageList;
    public int Validity = 0;
    public OnPackagItemClickListener onSubtemClickListener;

    public PackagesTicketAdapter(Context context, List<ContestPrice> packageList, int Validity) {
        this.context = context;
        this.packageList = packageList;
        this.Validity = Validity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        ListPackagesTicketBinding binding = ListPackagesTicketBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new PackageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        PackageHolder packageHolder = (PackageHolder) viewHolder;
        packageHolder.binding.tvEntryFees.setText(Utils.getCurrencyFormat(String.valueOf(packageList.get(i).getAmount())));
        int packagePriceTotal = (packageList.get(i).getAmount() * Validity);
        packageHolder.binding.tvPackagePrice.setText(Utils.getCurrencyFormat(String.valueOf(packagePriceTotal)));
        if (packageList.get(i).isSelected()) {
            packageHolder.binding.chkSelect.setChecked(true);
        } else {
            packageHolder.binding.chkSelect.setChecked(false);
        }

        packageHolder.binding.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = i;
                if (pos != RecyclerView.NO_POSITION) {
                    onSubtemClickListener.onSubItemClick(view, pos);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public void setOnItemClickListener(OnPackagItemClickListener onItemClickListener) {
        this.onSubtemClickListener = onItemClickListener;
    }
    public interface OnPackagItemClickListener {
        public void onSubItemClick(View view, int position);
    }
    class PackageHolder extends RecyclerView.ViewHolder {

        ListPackagesTicketBinding binding;

        public PackageHolder(@NonNull ListPackagesTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }
    }


}
