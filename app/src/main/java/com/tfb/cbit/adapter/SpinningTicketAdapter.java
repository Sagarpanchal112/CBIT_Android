package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListSpinningTicketsBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemClickStringListener;
import com.tfb.cbit.interfaces.OnSlotListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class SpinningTicketAdapter extends RecyclerView.Adapter<SpinningTicketAdapter.ViewHolder> {
    private static final String TAG = "BricksAdapter";
    private Context context;
    private boolean isRealData;
    private List<Ticket> ticketList;
    private String gameStatus = "";
    private OnItemClickListener onItemClickListener;
    private OnItemClickStringListener onStringItemClickStringListener;
    private OnSlotListener onSlotListener;

    public SpinningTicketAdapter(Context context, List<Ticket> ticketList, String gameStatus) {
        this.context = context;
        this.ticketList = ticketList;
        this.gameStatus = gameStatus;
    }

    public SpinningTicketAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListSpinningTicketsBinding binding = ListSpinningTicketsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
        viewHolder.binding.tvTotalTicket.setText(String.valueOf(ticketList.get(i).getNo_of_players()));
        viewHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
        viewHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewHolder.binding.rvOprions.setLayoutManager(linearLayoutManager);
        viewHolder.optionsAdapter = new SpiningTicketOptionsAdapter(context, ticketList.get(i).getSlotes(), gameStatus);
        viewHolder.optionsAdapter.setOnItemClickListener((view, position) -> {
            if (!gameStatus.equals(Utils.GAME_NOT_START) && !ticketList.get(i).isIsLock()) {
               /* viewHolder.optionsAdapter.setRawPosition(position);
                ticketList.get(i).getSlotes().get(position).setIsSelected(true);
                ticketList.get(i).setMinValue(String.valueOf(ticketList.get(i).getSlotes().get(position).getStartValue()));
                ticketList.get(i).setMaxValue(String.valueOf(ticketList.get(i).getSlotes().get(position).getEndValue()));
                ticketList.get(i).setDisplayView(String.valueOf(ticketList.get(i).getSlotes().get(position).getDisplayValue()));
                viewHolder.optionsAdapter.notifyDataSetChanged();
*/
               onSlotListener.onSlotValue(viewHolder.binding.rvOprions, i, position);
             }
        });

        viewHolder.binding.rvOprions.setAdapter(viewHolder.optionsAdapter);
        viewHolder.optionsAdapter.setGameStatus(false);
        if (!ticketList.get(i).getMinValue().isEmpty()) {
            viewHolder.binding.tvAnsSelection.setText(String.valueOf(ticketList.get(i).getDisplayView()));
            viewHolder.binding.image.setVisibility(View.VISIBLE);
            viewHolder.binding.tvAnsSelection.setVisibility(View.GONE);
            for (int j = 0; j < ticketList.get(i).getSlotes().size(); j++) {
                if (ticketList.get(i).getUserSelect().getDisplayValue().equalsIgnoreCase(ticketList.get(i).getSlotes().get(j).getDisplayValue())) {
                    Glide.with(context)
                            .load(context.getFilesDir().getAbsolutePath() + "/" + ticketList.get(i).getSlotes().get(j).getImage())
                            .into(viewHolder.binding.image);
                }
            }
        }

        if (gameStatus.equals(Utils.GAME_NOT_START)) {
            // fixedHolder.linearNotSelection.setVisibility(View.VISIBLE);
            viewHolder.binding.tvAnsSelection.setText("Empty");
            viewHolder.binding.tvLockNow.setAlpha(.5f);
            viewHolder.binding.tvLockNow.setEnabled(false);
        } else {
            viewHolder.binding.tvLockNow.setAlpha(1f);
            viewHolder.binding.tvLockNow.setEnabled(true);
            if (ticketList.get(i).isIsLock()) {
                viewHolder.binding.tvLockNow.setAlpha(0.5f);
                viewHolder.binding.tvLockNow.setEnabled(false);
                // fixedHolder.linearNotSelection.setVisibility(View.GONE);
                viewHolder.binding.linearSelection.setVisibility(View.GONE);
                viewHolder.binding.linearLock.setVisibility(View.GONE);
                viewHolder.optionsAdapter.setGameStatus(true);
                if (!ticketList.get(i).getDisplayView().isEmpty()) {
                    viewHolder.binding.tvSelectedAns.setVisibility(View.GONE);
                    viewHolder.binding.image.setVisibility(View.VISIBLE);
                    for (int j = 0; j < ticketList.get(i).getSlotes().size(); j++) {
                        if (ticketList.get(i).getDisplayView().equalsIgnoreCase(ticketList.get(i).getSlotes().get(j).getDisplayValue())) {
                            Glide.with(context)
                                    .load(context.getFilesDir().getAbsolutePath() + "/" + ticketList.get(i).getSlotes().get(j).getImage())
                                    .into(viewHolder.binding.image);
                           viewHolder.optionsAdapter.setRawPosition(j);

                        }
                    }
                    viewHolder.binding.tvSelectedAns.setText(ticketList.get(i).getDisplayView());
                } else {
                    viewHolder.binding.image.setVisibility(View.VISIBLE);
                    viewHolder.binding.tvSelectedAns.setVisibility(View.GONE);
                    for (int j = 0; j < ticketList.get(i).getSlotes().size(); j++) {
                        if (ticketList.get(i).getDisplayView().equalsIgnoreCase(ticketList.get(i).getSlotes().get(j).getDisplayValue())) {
                            Glide.with(context)
                                    .load(context.getFilesDir().getAbsolutePath() + "/" + ticketList.get(i).getSlotes().get(j).getImage())
                                    .into(viewHolder.binding.image);
                            viewHolder.optionsAdapter.setRawPosition(j);

                        }
                    }
                    viewHolder.binding.tvSelectedAns.setText(String.valueOf(ticketList.get(i).getUserSelect().getDisplayValue()));
                }
                viewHolder.binding.tvLockTime.setText(String.valueOf("Locked at: " + ticketList.get(i).getLockTime()));
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.tvAnsSelection.setVisibility(View.GONE);
                for (int j = 0; j < ticketList.get(i).getSlotes().size(); j++) {
                    if (ticketList.get(i).getDisplayView().equalsIgnoreCase(ticketList.get(i).getSlotes().get(j).getDisplayValue())) {
                        Glide.with(context)
                                .load(context.getFilesDir().getAbsolutePath() + "/" + ticketList.get(i).getSlotes().get(j).getImage())
                                .into(viewHolder.binding.image);
                        viewHolder.optionsAdapter.setRawPosition(j);

                    }
                }
                viewHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());

            } else {
                viewHolder.binding.linearSelection.setVisibility(View.GONE);
                viewHolder.binding.linearLock.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {



        SpiningTicketOptionsAdapter optionsAdapter;
        ListSpinningTicketsBinding binding;
        public ViewHolder(@NonNull ListSpinningTicketsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding. tvLockNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    PrintLog.e("ADAPTER", "tvLockNow click " + pos + "");
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, pos);
                    }
                }
            });



        }
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnSlotListener(OnSlotListener onSlotListener) {
        this.onSlotListener = onSlotListener;
    }

}
