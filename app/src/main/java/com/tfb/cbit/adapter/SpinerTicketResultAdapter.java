package com.tfb.cbit.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemSpinerTicketResultBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.Utils;

import java.util.List;

public class SpinerTicketResultAdapter extends RecyclerView.Adapter<SpinerTicketResultAdapter.ViewHolder> {

    private static final String TAG = "TicketResultAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Ticket> ticketList;
    private String constest_type = "";
    String SDCardPath ;

    public SpinerTicketResultAdapter(Context context, List<Ticket> tickets, String constest_type) {
        this.context = context;
        this.ticketList = tickets;
        this.constest_type = constest_type;
        SDCardPath=context.getFilesDir().getAbsolutePath() + "/";
    }

    public void setGameType(String constest_type) {
        this.constest_type = constest_type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemSpinerTicketResultBinding binding = ItemSpinerTicketResultBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (ticketList.get(i).getTotalTickets() < ticketList.get(i).getMinJoin()) {
            viewHolder.binding.tvTotalWinnings.setText(String.valueOf("N/A"));
            viewHolder.binding.tvMaxWinners.setText(String.valueOf("N/A" + " (" + ticketList.get(i).getMaxWinnersPrc() + "%)"));
        } else {
            viewHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
            viewHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));
        }
        viewHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
        viewHolder.binding.tvTotalTicket.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getTotalTickets())));
        //  viewHolder.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
        //   viewHolder.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));
        viewHolder.binding.tvWinMoney.setText("Win : " + Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinAmount())));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewHolder.binding.rvOprions.setLayoutManager(linearLayoutManager);
        viewHolder.binding.rvOprions.setAdapter(new SpiningOptionsAdapter(context, ticketList.get(i).getSlotes(), Utils.GAME_END));

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

              /*  Glide.with(context)
                        .load(SDCardPath + ticketList.get(i).getUserSelect().getDisplayValue())
                        .into(viewHolder.tvAns);
*/
                //  viewHolder.tvAns.setText("Your Selection " + ticketList.get(i).getUserSelect().getDisplayValue());
                for (int j = 0; j < ticketList.get(i).getSlotes().size(); j++) {
                    if (ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase(ticketList.get(i).getSlotes().get(j).getDisplayValue())) {
                        Glide.with(context)
                                .load(SDCardPath + ticketList.get(i).getSlotes().get(j).getImage())
                                .into(viewHolder.binding.tvAns);
                    }
                }
                viewHolder.binding.tvLockTime.setText("Locked at: " + ticketList.get(i).getLockTime());
            } else {
                //  viewHolder.rdRdb.setAlpha(0.5f);
                //   viewHolder.tvAns.setText(String.valueOf(ticketList.get(i).getUserSelect().getDisplayValue()));
                viewHolder.binding.tvAns.setVisibility(View.GONE);
                viewHolder.binding.tvSelection.setText("Your Selection : \nNo Answer");
                for (int j = 0; j < ticketList.get(i).getSlotes().size(); j++) {
                    if (!ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase("No Answer")) {
                        if (ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase(ticketList.get(i).getSlotes().get(j).getDisplayValue())) {
                            Glide.with(context)
                                    .load(SDCardPath + ticketList.get(i).getSlotes().get(j).getImage())
                                    .into(viewHolder.binding.tvAns);

                        }
                    }
                }
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


        ItemSpinerTicketResultBinding binding;
        public ViewHolder(@NonNull ItemSpinerTicketResultBinding binding) {
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
