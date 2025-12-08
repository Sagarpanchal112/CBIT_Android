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
import com.tfb.cbit.databinding.ItemAnySpinerTicketResultBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class AnySpinerTicketResultAdapter extends RecyclerView.Adapter<AnySpinerTicketResultAdapter.ViewHolder> {

    private static final String TAG = "TicketResultAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Ticket> ticketList;
    private String constest_type = "";
   // String SDCardPath = getFilesDir().getAbsolutePath() + "/";
    public boolean isPlayer = false;

    public AnySpinerTicketResultAdapter(Context context, List<Ticket> tickets, String constest_type) {
        this.context = context;
        this.ticketList = tickets;
        this.constest_type = constest_type;
    }

    public void setGameType(String constest_type) {
        this.constest_type = constest_type;
    }

    public void isPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemAnySpinerTicketResultBinding binding = ItemAnySpinerTicketResultBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
        viewHolder.binding.tvTotalTicket.setText(String.valueOf(ticketList.get(i).getNo_of_players()));
        viewHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
        viewHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));
        if (isPlayer) {
            viewHolder.binding.tvPlayed.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getPlayed())) + "Played /" + ticketList.get(i).getPending() + " Pending");

        } else {
            viewHolder.binding.tvPlayed.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getPlayed())) + "Played /" + ticketList.get(i).getPending() + " Pending");

        }
        if (ticketList.get(i).isIsLock() == false) {
            viewHolder.binding.tvAnsText.setVisibility(View.GONE);
        } else {
            viewHolder.binding.tvAnsText.setVisibility(View.VISIBLE);

        }
        viewHolder.binding.tvGaneNo.setText("Game No :  " + ticketList.get(i).getGame_no());
        if (ticketList.get(i).getPending() == 0) {
            viewHolder.binding.tvViewWinner.setVisibility(View.VISIBLE);
            viewHolder.binding.tvWinnings.setVisibility(View.VISIBLE);
            if (!ticketList.get(i).getTotalCCWinAmount().equals("0")) {
                viewHolder.binding.tvWinnings.setText("Points :  " + ticketList.get(i).getTotalCCWinAmount()+" Points");
            } else if (!ticketList.get(i).getNowin().equals("0")) {
                viewHolder.binding.tvWinnings.setText("Refund : " + ticketList.get(i).getNowin());
            }  else if (!ticketList.get(i).getWinAmount().equals("0")) {
                viewHolder.binding.tvWinnings.setText("Win : " + Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinAmount())));
            }else {
                viewHolder.binding.tvWinnings.setText("Win : " + Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinAmount())));
            }
        }else{

        } /*else {
            viewHolder.tvViewWinner.setVisibility(View.GONE);
            viewHolder.tvWinnings.setVisibility(View.GONE);
            if (ticketList.get(i).isWin()==false) {
                if(ticketList.get(i).getTotalCCWinAmount().equals("0.00")){
                    viewHolder.tvWinnings.setText("Refund : " + ticketList.get(i).getNowin());

                }else{
                    viewHolder.tvWinnings.setText("Reclamation : CC " + ticketList.get(i).getTotalCCWinAmount());

                }
                viewHolder.tvWinnings.setVisibility(View.VISIBLE);

             }else{
                viewHolder.tvWinnings.setVisibility(View.VISIBLE);
                viewHolder.tvWinnings.setText("Win : " + Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinAmount())));
            }
        }*/
        if (ticketList.get(i).getPlayed() == 1) {
            viewHolder.binding.tvWinnings.setVisibility(View.GONE);
            viewHolder.binding.tvViewWinner.setVisibility(View.GONE);

        } else {
            viewHolder.binding.tvWinnings.setVisibility(View.VISIBLE);
            viewHolder.binding.tvViewWinner.setVisibility(View.VISIBLE);

        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewHolder.binding.rvOprions.setLayoutManager(linearLayoutManager);
        viewHolder.binding.rvOprions.setAdapter(new SpiningOptionsAdapter(context, ticketList.get(i).getSlotes(), Utils.GAME_END));
        if (ticketList.get(i).isWin()) {
            viewHolder.binding.tvAnsText.setText("You chose right answer in " + ticketList.get(i).getLockTime() + "");
        } else {
            viewHolder.binding.tvAnsText.setText("You chose wrong answer in " + ticketList.get(i).getLockTime() + "");

        }
        if (ticketList.get(i).isCancel()) {
            viewHolder.binding.tvCancelled.setVisibility(View.VISIBLE);
            viewHolder.binding.tvAns.setVisibility(View.INVISIBLE);
            viewHolder.binding.tvLockTime.setVisibility(View.INVISIBLE);

        } else {
            viewHolder.binding.tvCancelled.setVisibility(View.GONE);
            viewHolder.binding.tvAns.setVisibility(View.VISIBLE);
            viewHolder.binding.tvLockTime.setVisibility(View.VISIBLE);
            if (ticketList.get(i).isIsLock()) {
                for (int j = 0; j < ticketList.get(i).getSlotes().size(); j++) {
                    if (ticketList.get(i).getUserSelect().getSelectValue().equalsIgnoreCase(ticketList.get(i).getSlotes().get(j).getDisplayValue())) {
                        Glide.with(context)
                                .load(context.getFilesDir().getAbsolutePath() + "/" + ticketList.get(i).getUserSelect().getSelectValue())
                                .into(viewHolder.binding.tvAns);
                    }
                }
                //   viewHolder.tvAns.setText("Your Selection " + ticketList.get(i).getUserSelect().getDisplayValue());
                viewHolder.binding.tvLockTime.setText("Locked at: " + ticketList.get(i).getLockTime());
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
        ItemAnySpinerTicketResultBinding binding;

        public ViewHolder(@NonNull ItemAnySpinerTicketResultBinding itemView) {
            super(itemView.getRoot());
            this.binding=itemView;
             binding.tvViewWinner.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(view, pos);
                }
            });
        }
    }
}
