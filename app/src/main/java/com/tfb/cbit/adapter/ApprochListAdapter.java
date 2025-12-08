package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemOfferApprochBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.models.MyJTicket.ApproachList;

import java.util.List;

public class ApprochListAdapter extends RecyclerView.Adapter<ApprochListAdapter.ViewHolder> {

    private Context context;
    private OnApprochItemClickListener onItemClickListener;
    private List<ApproachList> historyList;

    public ApprochListAdapter(Context context, List<ApproachList> historyList) {
        this.context = context;
        this.historyList = historyList;
      //  onItemClickListener=(OnApprochItemClickListener)context;
    }

    public interface OnApprochItemClickListener {
        public void onItemClick(int position,String value);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemOfferApprochBinding binding = ItemOfferApprochBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvUser.setText(historyList.get(i).getUserName());
        viewHolder.binding.tvOffer.setText(historyList.get(i).getOffer() + " %");
        viewHolder.binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewHolder.binding.etNego.getText().toString().isEmpty()){
                    onItemClickListener.onItemClick(historyList.get(i).getJ_ticket_user_approach_id(), viewHolder.binding.etNego.getText().toString());

                }else{

                }

            }
        });

    }

    public void setOnItemClickListener(OnApprochItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemOfferApprochBinding binding;
        public ViewHolder(@NonNull ItemOfferApprochBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
