package com.tfb.cbit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemAnyTicketResultBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.Utils;
import java.util.List;

public class AnyTimeTicketResultAdapter extends RecyclerView.Adapter<AnyTimeTicketResultAdapter.ViewHolder> {

    private static final String TAG = "TicketResultAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Ticket> ticketList;
    private String constest_type = "";

    public AnyTimeTicketResultAdapter(Context context, List<Ticket> tickets, String constest_type) {
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
        ItemAnyTicketResultBinding binding = ItemAnyTicketResultBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
        viewHolder.binding.tvTotalTicket.setText(ticketList.get(i).getNo_of_players() + "");
        viewHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
        viewHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));
        viewHolder.binding.tvPlayed.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getPlayed())) + "Played /" + ticketList.get(i).getPending() + " Pending");
        viewHolder.binding.tvGaneNo.setText("Game No :  " + ticketList.get(i).getGame_no());
        if (ticketList.get(i).isWin()) {
            viewHolder.binding.tvAnsText.setText("You chose right answer in " + ticketList.get(i).getLockTime() + "");
        } else {
            viewHolder.binding.tvAnsText.setText("You chose wrong answer in " + ticketList.get(i).getLockTime() + "");

        }

        if (ticketList.get(i).getPending() == 0) {
            viewHolder.binding.tvViewWinner.setVisibility(View.VISIBLE);
            if (!ticketList.get(i).getTotalCCWinAmount().equals("0")) {
                viewHolder.binding.tvWinnings.setText("Points : " + ticketList.get(i).getTotalCCWinAmount()+" Points");
            } else if (!ticketList.get(i).getNowin().equals("0")) {
                viewHolder.binding.tvWinnings.setText("Refund : " + ticketList.get(i).getNowin());
            } else if (!ticketList.get(i).getWinAmount().equals("0")) {
                viewHolder.binding.tvWinnings.setText("Win : " + Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinAmount())));
            } else {
                viewHolder.binding.tvWinnings.setText("Win : " + Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinAmount())));
            }

           /* if (ticketList.get(i).isWin()) {
                viewHolder.tvWinnings.setVisibility(View.VISIBLE);
                viewHolder.tvWinnings.setText("Win : " + Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinAmount())));
            }else{
                if(ticketList.get(i).getTotalCCWinAmount().equals("0.00")){
                    viewHolder.tvWinnings.setText("Refund : " + ticketList.get(i).getNowin());

                }else{
                    viewHolder.tvWinnings.setText("Reclamation : CC " + ticketList.get(i).getTotalCCWinAmount());

                }
                viewHolder.tvWinnings.setVisibility(View.VISIBLE);

            }*/
        } else {
            viewHolder.binding.tvWinnings.setText("Result Pending");

           /* viewHolder.tvViewWinner.setVisibility(View.GONE);
            viewHolder.tvWinnings.setVisibility(View.GONE);

            if (ticketList.get(i).isWin()) {
                viewHolder.tvWinnings.setVisibility(View.VISIBLE);
                viewHolder.tvWinnings.setText("Win : " + Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinAmount())));
            }else{
                if(ticketList.get(i).getTotalCCWinAmount().equals("0.00")){
                    viewHolder.tvWinnings.setText("Refund : " + ticketList.get(i).getNowin());

                }else{
                    viewHolder.tvWinnings.setText("Reclamation : CC " + ticketList.get(i).getTotalCCWinAmount());

                }
                viewHolder.tvWinnings.setVisibility(View.VISIBLE);

            }*/
        }
        if (ticketList.get(i).isIsLock() == false) {
            viewHolder.binding.tvAnsText.setVisibility(View.GONE);
        } else {
            viewHolder.binding.tvAnsText.setVisibility(View.VISIBLE);

        }
        if (ticketList.get(i).getPlayed() == 1) {
            viewHolder.binding.tvWinnings.setVisibility(View.GONE);
            viewHolder.binding.tvViewWinner.setVisibility(View.GONE);

        } else {
            viewHolder.binding.tvWinnings.setVisibility(View.VISIBLE);
            viewHolder.binding.tvViewWinner.setVisibility(View.VISIBLE);

        }
        if (constest_type.equalsIgnoreCase("rdb")) {
            viewHolder.binding.rdRdb.setVisibility(View.VISIBLE);
            viewHolder.binding.linear3Options.setVisibility(View.GONE);
        } else {
            viewHolder.binding.rdRdb.setVisibility(View.GONE);
            viewHolder.binding.linear3Options.setVisibility(View.VISIBLE);
        }
        if (ticketList.get(i).isCancel()) {
            viewHolder.binding.tvAns.setVisibility(View.INVISIBLE);
            viewHolder.binding.tvLockTime.setVisibility(View.INVISIBLE);

        } else {
            viewHolder.binding.tvAns.setVisibility(View.VISIBLE);
            viewHolder.binding.tvLockTime.setVisibility(View.VISIBLE);
            if (ticketList.get(i).isIsLock()) {
                if (constest_type.equalsIgnoreCase("rdb")) {
                    if (ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase("Blue win")) {
                        viewHolder.binding.rdBlue.setBackground(context.getResources().getDrawable(R.drawable.bg_blue_border));
                    } else if (ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase("Red win")) {
                        viewHolder.binding.rdRed.setBackground(context.getResources().getDrawable(R.drawable.bg_red_border));
                    } else if (ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase("Draw win")) {
                        viewHolder.binding.rdDraw.setBackground(context.getResources().getDrawable(R.drawable.bg_yellow_border));
                        viewHolder.binding.rdDraw.setBackground(context.getResources().getDrawable(R.drawable.bg_white));
                        viewHolder.binding.rdDraw.setTextColor(context.getResources().getColor(R.color.color_yellow));
                    }
                } else {
                    if (ticketList.get(i).getSlotes().size() == 2) {

                        viewHolder.binding.tvMinus.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                        viewHolder.binding.tvZero.setText(ticketList.get(i).getSlotes().get(1).getDisplayValue());
                        viewHolder.binding.tvPlus.setVisibility(View.GONE);

                        viewHolder.binding.tvMinus.setBackgroundResource(R.drawable.bg_yellow_radius);
                        viewHolder.binding.tvMinus.setTextColor(Color.parseColor("#1a505d"));
                        viewHolder.binding.tvZero.setBackgroundResource(R.drawable.bg_yellow_radius);
                        viewHolder.binding.tvZero.setTextColor(Color.parseColor("#1a505d"));
                        if (Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue()) == ticketList.get(i).getSlotes().get(0).getStartValue()) {
                            viewHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                            viewHolder.binding.tvMinus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                            viewHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            viewHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                        } else if (Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue()) == ticketList.get(i).getSlotes().get(1).getStartValue()) {
                            viewHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            viewHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                            viewHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                            viewHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                        } else if (ticketList.get(i).getSlotes().size() == 3) {
                            if (Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue()) == ticketList.get(i).getSlotes().get(2).getStartValue()) {
                                viewHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                viewHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                viewHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                            }
                        }
                    } else {
                        viewHolder.binding.tvMinus.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                        viewHolder.binding.tvZero.setText(ticketList.get(i).getSlotes().get(1).getDisplayValue());
                        viewHolder.binding.tvPlus.setText(ticketList.get(i).getSlotes().get(2).getDisplayValue());

                        viewHolder.binding.tvMinus.setBackgroundResource(R.drawable.bg_yellow_radius);
                        viewHolder.binding.tvMinus.setTextColor(Color.parseColor("#1a505d"));
                        viewHolder.binding.tvZero.setBackgroundResource(R.drawable.bg_yellow_radius);
                        viewHolder.binding.tvZero.setTextColor(Color.parseColor("#1a505d"));
                        viewHolder.binding.tvPlus.setBackgroundResource(R.drawable.bg_yellow_radius);
                        viewHolder.binding.tvPlus.setTextColor(Color.parseColor("#1a505d"));
                        if (Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue()) == ticketList.get(i).getSlotes().get(0).getStartValue()) {
                            viewHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                            viewHolder.binding.tvMinus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                            viewHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            viewHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                        } else if (Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue()) == ticketList.get(i).getSlotes().get(1).getStartValue()) {
                           viewHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            viewHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                            viewHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                            viewHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                        } else if (ticketList.get(i).getSlotes().size() == 3) {
                            viewHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            viewHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            viewHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                            viewHolder.binding.tvPlus.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                        }
                    }
                }
                viewHolder.binding.tvAns.setText("Your Selection " + ticketList.get(i).getUserSelect().getSelectValue());
                viewHolder.binding.tvLockTime.setText("Locked at: " + ticketList.get(i).getLockTime());
            } else {
                viewHolder.binding.tvAns.setText(String.valueOf(ticketList.get(i).getUserSelect().getDisplayValue()));
            }
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

        ItemAnyTicketResultBinding binding;

        public ViewHolder(@NonNull ItemAnyTicketResultBinding binding) {
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
