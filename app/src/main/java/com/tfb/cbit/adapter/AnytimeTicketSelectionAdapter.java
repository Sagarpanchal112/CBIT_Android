/*
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
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.anytimegame.Content;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnytimeTicketSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "TicketSelectionAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;*/
/**//*

    private List<Content> ticketList=new ArrayList<>();
    private int viewType;
    private int minAns, maxAns;

    public AnytimeTicketSelectionAdapter(Context context, List<Content> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
        onItemClickListener = (OnItemClickListener) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(context)
                .inflate(R.layout.item_ticket_any_selection_fixed, viewGroup, false);
        return new FixedHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        final FixedHolder fixedHolder = (FixedHolder) viewHolder;
        Content content = ticketList.get(i);
        // Log.i("Prize", "==>" + ticketList.get(i).getAmount());
        fixedHolder.tvEntryFees.setText("₹ " + content.getAmount());
        fixedHolder.tvTotalTicket.setText(content.getNo_of_players()+"");
        fixedHolder.tvTotalWinnings.setText("400");
        fixedHolder.tvMaxWinners.setText("200");
        fixedHolder.tv_lock_style.setText(" Basic");
        if (ticketList.get(i).isSelected()) {
            //fixedHolder.frmSelected.setVisibility(View.VISIBLE);
            // fixedHolder.frmSelected.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent_50));
            fixedHolder.chkSelect.setChecked(true);
            fixedHolder.chkSelect.setVisibility(View.VISIBLE);
        } else {
            fixedHolder.chkSelect.setVisibility(View.VISIBLE);
              fixedHolder.chkSelect.setChecked(false);
        }
        */
/* else if (i == 1) {
            fixedHolder.tvEntryFees.setText("₹ 5");
            fixedHolder.tvTotalTicket.setText("500");
            fixedHolder.tvTotalWinnings.setText("2000");
            fixedHolder.tvMaxWinners.setText("200");
            fixedHolder.tv_lock_style.setText(" Basic");

        } else if (i == 2) {
            fixedHolder.tvEntryFees.setText("₹ 11");
            fixedHolder.tvTotalTicket.setText("500");
            fixedHolder.tvTotalWinnings.setText("4400");
            fixedHolder.tvMaxWinners.setText("200");
            fixedHolder.tv_lock_style.setText(" Basic");

        } else if (i == 3) {
            fixedHolder.tvEntryFees.setText("₹ 25");
            fixedHolder.tvTotalTicket.setText("500");
            fixedHolder.tvTotalWinnings.setText("10000");
            fixedHolder.tvMaxWinners.setText("200");
            fixedHolder.tv_lock_style.setText(" Basic");

        }*//*


    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setMinAns(int minAns) {
        this.minAns = minAns;
    }

    public void setMaxAns(int maxAns) {
        this.maxAns = maxAns;
    }


    class FixedHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chkSelect)
        CheckBox chkSelect;
        @BindView(R.id.tvEntryFees)
        TextView tvEntryFees;
        @BindView(R.id.tvTotalTicket)
        TextView tvTotalTicket;
        @BindView(R.id.tvTotalWinnings)
        TextView tvTotalWinnings;
        @BindView(R.id.tvMaxWinners)
        TextView tvMaxWinners;
        @BindView(R.id.tv_lock_style)
        TextView tv_lock_style;

        public FixedHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            chkSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if (ticketList.get(getAdapterPosition()).getIsPurchased() != 1) {
                    int pos = getAdapterPosition();
                    onItemClickListener.onItemClick(view, pos);

                    // }
                }
            });


        }
    }
}
*/
