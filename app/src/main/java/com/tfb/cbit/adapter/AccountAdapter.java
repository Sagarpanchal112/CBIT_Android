package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.models.profile.BankAccount;

import java.util.List;


public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{

    private Context context;
    private List<BankAccount> bankAccount;
    public AccountAdapter(Context context, List<BankAccount> bankAccount){
        this.context = context;
        this.bankAccount = bankAccount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListAccountBinding binding = ListAccountBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvBankName.setText(bankAccount.get(i).getBankName());
        viewHolder.binding.tvBankAc.setText(String.valueOf("A/C: "+bankAccount.get(i).getAccountNo()));
        viewHolder.binding.tvBankIFSC.setText(String.valueOf("IFSC Code: "+bankAccount.get(i).getIfscCode()));
       /* if(bankAccount.get(i).getVerifyBank()==0){
            viewHolder.tvStatus.setVisibility(View.VISIBLE);
            viewHolder.tvBankName.setAlpha(.5f);
            viewHolder.tvBankAc.setAlpha(.5f);
            viewHolder.tvBankIFSC.setAlpha(.5f);
        }else{*/
            viewHolder.binding.tvStatus.setVisibility(View.GONE);
            viewHolder.binding.tvBankName.setAlpha(1f);
            viewHolder.binding.tvBankAc.setAlpha(1f);
            viewHolder.binding.tvBankIFSC.setAlpha(1f);
       // }

    }

    @Override
    public int getItemCount() {
        return bankAccount.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ListAccountBinding binding;
        public ViewHolder(@NonNull ListAccountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
