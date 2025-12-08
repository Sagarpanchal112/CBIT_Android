/*
package com.tfb.cbit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnRangeListener;
import com.tfb.cbit.interfaces.OnSlotListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnytimeTicketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnRangeListener onRangeListener;
    private OnSlotListener onSlotListener;
   private int viewType;
    private int minAns, maxAns;
    private String gameStatus = "";
    private List<Ticket> ticketList;
    public int count = 0;

    public AnytimeTicketAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;

            view = LayoutInflater.from(context)
                    .inflate(R.layout.view_any_time_tickets, viewGroup, false);
            return new FixedHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final FixedHolder fixedHolder = (FixedHolder) viewHolder;

        if (i == 0) {
            fixedHolder.tvEntryFees.setText("₹ 1");
            fixedHolder.tvTotalTicket.setText("500");
            fixedHolder.tvTotalWinnings.setText("400");
            fixedHolder.tvMaxWinners.setText("200");

        }else  if (i == 1) {
            fixedHolder.tvEntryFees.setText("₹ 5");
            fixedHolder.tvTotalTicket.setText("500");
            fixedHolder.tvTotalWinnings.setText("2000");
            fixedHolder.tvMaxWinners.setText("200");

        }else  if (i == 2) {
            fixedHolder.tvEntryFees.setText("₹ 11");
            fixedHolder.tvTotalTicket.setText("500");
            fixedHolder.tvTotalWinnings.setText("4400");
            fixedHolder.tvMaxWinners.setText("200");

        }else  if (i == 3) {
            fixedHolder.tvEntryFees.setText("₹ 25");
            fixedHolder.tvTotalTicket.setText("500");
            fixedHolder.tvTotalWinnings.setText("10000");
            fixedHolder.tvMaxWinners.setText("200");

        } }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
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

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnRangeListener(OnRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }

    public void setOnSlotListener(OnSlotListener onSlotListener) {
        this.onSlotListener = onSlotListener;
    }

    class FixedHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvEntryFees)
        TextView tvEntryFees;
        @BindView(R.id.tvTotalTicket)
        TextView tvTotalTicket;
        @BindView(R.id.tvTotalWinnings)
        TextView tvTotalWinnings;
        @BindView(R.id.tvMaxWinners)
        TextView tvMaxWinners;


        public FixedHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }
    }
}
*/
