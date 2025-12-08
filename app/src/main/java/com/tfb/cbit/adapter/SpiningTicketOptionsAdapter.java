package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemOptionsSpinigBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contestdetails.Slote;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class SpiningTicketOptionsAdapter extends RecyclerView.Adapter<SpiningTicketOptionsAdapter.ViewHolder> {

    private static final String TAG = "OptionsAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener = null;
    private List<Slote> sloteList;
    private String gameStatus = "";
    public boolean isClick = false;
    public int row_index = -1;


    public SpiningTicketOptionsAdapter(Context context, List<Slote> sloteList) {
        this.context = context;
        this.sloteList = sloteList;
    }

    public SpiningTicketOptionsAdapter(Context context, List<Slote> sloteList, String gameStatus) {
        this.context = context;
        this.sloteList = sloteList;
        this.gameStatus = gameStatus;
    }

    public SpiningTicketOptionsAdapter(Context context, String gameStatus) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_options_spinig, viewGroup, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (gameStatus.equals(Utils.GAME_NOT_START)) {
            //   viewHolder.tvOption.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
        } else if (gameStatus.equals(Utils.GAME_END)) {
            viewHolder.binding.tvOption.setBackground(ContextCompat.getDrawable(context,
                    sloteList.get(i).isIsSelected() ? R.drawable.bg_white_border : android.R.color.transparent));
        } else {
            viewHolder.binding.tvOption.setBackground(ContextCompat.getDrawable(context,
                    sloteList.get(i).isIsSelected() ? R.drawable.bg_white_border : android.R.color.transparent));
        }
        if (sloteList.get(i).getDisplayValue().equalsIgnoreCase("draw")) {
            viewHolder.binding.tvOption.setImageResource(R.drawable.ic_draw);
        } else {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_logo)
                    .error(R.drawable.loading_logo)
                    .dontAnimate()
                    .dontTransform();
            Glide.with(context).load(context.getFilesDir().getAbsolutePath() + "/" + sloteList.get(i).getImage()).apply(options).into(viewHolder.binding.tvOption);

     /*       Glide.with(context)
                    .load(context.getFilesDir().getAbsolutePath() + "/" + sloteList.get(i).getImage())
                    .into(viewHolder.tvOption);
    */
        }
        viewHolder.binding.tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    if (gameStatus.equals(Utils.GAME_START)) {

                        if (!isClick) {
                          //  viewHolder.tvOption.setBackgroundResource(R.drawable.bg_white_border);
                            row_index = i;
                            LogHelper.d("TAG", "row_index : "+row_index);
                            onItemClickListener.onItemClick(view, i);
                            notifyDataSetChanged();

                            //   isClick = true;
                        }
                    }
                }

            }
        });

        if (row_index == i) {
            LogHelper.d("TAG", "row_index : "+row_index);
            viewHolder.binding.tvOption.setBackgroundResource(R.drawable.bg_white_border);
        } else {
            viewHolder.binding.tvOption.setBackground(null);
        }

        // viewHolder.tvOption.setText(sloteList.get(i).getDisplayValue());
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
