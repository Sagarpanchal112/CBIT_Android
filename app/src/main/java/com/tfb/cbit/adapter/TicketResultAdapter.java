package com.tfb.cbit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemTicketResultBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class TicketResultAdapter extends RecyclerView.Adapter<TicketResultAdapter.ViewHolder> {

    private static final String TAG = "TicketResultAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Ticket> ticketList;
    private String constest_type = "";

    public TicketResultAdapter(Context context, List<Ticket> tickets, String constest_type) {
        this.context = context;
        this.ticketList = tickets;
        this.constest_type = constest_type;
    }

    public void setGameType(String constest_type) {
        this.constest_type = constest_type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemTicketResultBinding binding = ItemTicketResultBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        if (ticketList.get(i).getAmount() < 1){
//
//        }else {
//            viewHolder.tvEntryFees.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getAmount())));
//        }
        if (ticketList.get(i).getTotalTickets()< ticketList.get(i).getMinJoin()) {
            viewHolder.binding.tvTotalWinnings.setText(String.valueOf("N/A"));
            viewHolder.binding.tvMaxWinners.setText(String.valueOf("N/A" + " (" + ticketList.get(i).getMaxWinnersPrc() + "%)"));
        } else {
            viewHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
            viewHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));
        }


        viewHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
        viewHolder.binding.tvTotalTicket.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getTotalTickets())));
        viewHolder.binding.tvWinMoney.setText("Win : " + Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinAmount())));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewHolder.binding.rvOprions.setLayoutManager(linearLayoutManager);
         if (constest_type.equalsIgnoreCase("rdb")) {
            viewHolder.binding.rdRdb.setVisibility(View.VISIBLE);
            viewHolder.binding.frameContent.setVisibility(View.GONE);
        } else {
             viewHolder.binding.rdRdb.setVisibility(View.GONE);
             viewHolder.binding.frameContent.setVisibility(View.VISIBLE);

            // if(ticketList.get(i).getSlotes().size()==3){
                    ThreeOptionsAdapter optionsAdapter = new ThreeOptionsAdapter(context, ticketList.get(i).getSlotes(), "gameEnd");
                    viewHolder.binding.rvOprions.setAdapter(optionsAdapter);

              //  }else{
                //    OptionsAdapter optionsAdapter = new OptionsAdapter(context, ticketList.get(i).getSlotes(), "gameEnd");
                //    viewHolder.rvOprions.setAdapter(optionsAdapter);

              //  }

        }

        if (ticketList.get(i).isCancel()) {
            viewHolder.binding.tvCancelled.setVisibility(View.VISIBLE);
            viewHolder.binding.tvViewWinner.setVisibility(View.GONE);
            viewHolder.binding.tvAns.setVisibility(View.INVISIBLE);
            viewHolder.binding.tvLockTime.setVisibility(View.INVISIBLE);

        } else {
            viewHolder.binding.tvCancelled.setVisibility(View.GONE);
            viewHolder.binding.tvViewWinner.setVisibility(View.VISIBLE);
            viewHolder.binding.tvAns.setVisibility(View.VISIBLE);
            viewHolder.binding.tvLockTime.setVisibility(View.VISIBLE);
            if (ticketList.get(i).isIsLock()) {
                //  viewHolder.rdRdb.setAlpha(1.0f);
                if (ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase("Blue win")) {
                    viewHolder.binding.rdBlue.setBackground(context.getResources().getDrawable(R.drawable.bg_blue_border));
                    // viewHolder.rdRed.setAlpha(0.5f);
                    // viewHolder.rdDraw.setAlpha(0.5f);

                } else if (ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase("Red win")) {
                    viewHolder.binding.rdRed.setBackground(context.getResources().getDrawable(R.drawable.bg_red_border));
                    //  viewHolder.rdRed.setAlpha(1.0f);
                    // viewHolder.rdBlue.setAlpha(0.5f);
                    // viewHolder.rdDraw.setAlpha(0.5f);

                } else if (ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase("Draw win")) {
                    viewHolder.binding.rdDraw.setBackground(context.getResources().getDrawable(R.drawable.bg_yellow_border));
                    viewHolder.binding.rdDraw.setBackground(context.getResources().getDrawable(R.drawable.bg_white));
                    viewHolder.binding.rdDraw.setTextColor(context.getResources().getColor(R.color.color_yellow));
                    // viewHolder.rdDraw.setAlpha(1.0f);
                    //  viewHolder.rdBlue.setAlpha(0.5f);
                    //  viewHolder.rdRed.setAlpha(0.5f);

                }
                viewHolder.binding.tvAns.setText("Your Selection " + ticketList.get(i).getUserSelect().getDisplayValue());
                viewHolder.binding.tvLockTime.setText("Locked at: " + ticketList.get(i).getLockTime());
            } else {
                //  viewHolder.rdRdb.setAlpha(0.5f);
                viewHolder.binding.tvAns.setText(String.valueOf(ticketList.get(i).getUserSelect().getDisplayValue()));
            }


            Log.d(TAG, "Your Selection>>>>Displ>>>>: " + ticketList.get(i).getUserSelect().getDisplayValue());
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemTicketResultBinding binding;

        public ViewHolder(@NonNull ItemTicketResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.tvViewWinner.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(view, pos);
                }
            });

        }
    }
}
