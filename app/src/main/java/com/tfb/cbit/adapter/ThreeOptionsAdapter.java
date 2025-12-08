package com.tfb.cbit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemThreeOptionsBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contestdetails.Slote;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class ThreeOptionsAdapter extends RecyclerView.Adapter<ThreeOptionsAdapter.ViewHolder> {

    private static final String TAG = "OptionsAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener = null;
    private List<Slote> sloteList;
    private String gameStatus = "";
    public ThreeOptionsAdapter(Context context, List<Slote> sloteList) {
        this.context = context;
        this.sloteList = sloteList;
    }
    public ThreeOptionsAdapter(Context context, List<Slote> sloteList, String gameStatus) {
        this.context = context;
        this.sloteList = sloteList;
        this.gameStatus = gameStatus;
    }
    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemThreeOptionsBinding binding = ItemThreeOptionsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (gameStatus.equals(Utils.GAME_NOT_START)) {
            viewHolder.binding.tvOption.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
        } else {
            viewHolder.binding.tvOption.setBackground(ContextCompat.getDrawable(context,
                    sloteList.get(i).isIsSelected() ? R.drawable.bg_white_radius : R.drawable.bg_yellow_radius));

        }

     /*   if(sloteList.get(i).getDisplayValue().length()>1) {
            viewHolder.tvOption.setText(sloteList.get(i).getDisplayValue().replace(' ', '\n'));
        }else{
            viewHolder.tvOption.setText("\n"+sloteList.get(i).getDisplayValue()+"\n");
        }*/
        Log.d(TAG, "getDisplayValue: " + sloteList.get(i).getDisplayValue());

        viewHolder.binding.tvOption.setText(sloteList.get(i).getDisplayValue());
    }

    @Override
    public int getItemCount() {
        return sloteList.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemThreeOptionsBinding binding;
        public ViewHolder(@NonNull ItemThreeOptionsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.tvOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION ) {
                            onItemClickListener.onItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }
}
