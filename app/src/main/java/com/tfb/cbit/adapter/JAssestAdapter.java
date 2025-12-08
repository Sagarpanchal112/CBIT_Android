package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemJassetsBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.models.JAssetsModel;

import java.util.List;

public class JAssestAdapter extends RecyclerView.Adapter<JAssestAdapter.ViewHolder> {

    private Context context;
    private List<JAssetsModel.RedemedList> contentList;

    public JAssestAdapter(Context context, List<JAssetsModel.RedemedList> sloteList) {
        this.context = context;
        this.contentList = sloteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemJassetsBinding binding = ItemJassetsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvName.setText(contentList.get(i).getName());
        viewHolder.binding.tvRedemedCc.setText("" + contentList.get(i).getRedemedCC());
        viewHolder.binding.tvAppliedCc.setText("" + contentList.get(i).getAppliedCC());
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemJassetsBinding binding;

        public ViewHolder(@NonNull ItemJassetsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
