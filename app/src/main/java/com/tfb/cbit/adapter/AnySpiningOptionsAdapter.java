package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemOptionsSpinigBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;

import java.util.List;


public class AnySpiningOptionsAdapter extends RecyclerView.Adapter<AnySpiningOptionsAdapter.ViewHolder> {

    private static final String TAG = "OptionsAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener = null;
    private List<String> sloteList;
    private String gameStatus = "";
    public boolean isClick = false;
    public int row_index = -1;


    public AnySpiningOptionsAdapter(Context context, List<String> sloteList) {
        this.context = context;
        this.sloteList = sloteList;
    }

    public AnySpiningOptionsAdapter(Context context, List<String> sloteList, String gameStatus) {
        this.context = context;
        this.sloteList = sloteList;
        this.gameStatus = gameStatus;
    }

    public AnySpiningOptionsAdapter(Context context, String gameStatus) {
        this.context = context;
        this.gameStatus = gameStatus;
    }

    public void setGameStatus(boolean isClick) {
        this.isClick = isClick;
    }

    public void setRawPosition(int row_index) {
        this.row_index = row_index;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemOptionsSpinigBinding binding = ItemOptionsSpinigBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.loading_logo)
                .error(R.drawable.loading_logo)
                .dontAnimate()
                .dontTransform();
        Glide.with(context).load(sloteList.get(i)).apply(options).into(viewHolder.binding.tvOption);


    }

    @Override
    public int getItemCount() {
        return sloteList.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
       ItemOptionsSpinigBinding binding;

        public ViewHolder(@NonNull ItemOptionsSpinigBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
