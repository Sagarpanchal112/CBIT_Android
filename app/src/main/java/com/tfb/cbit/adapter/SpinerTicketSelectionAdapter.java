package com.tfb.cbit.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemTicketSelectionBinding;
import com.tfb.cbit.databinding.ItemTicketSelectionSpinerBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.Utils;
import com.tfb.cbit.views.CustomLinearLayoutManager;

import java.util.List;

public class SpinerTicketSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "TicketSelectionAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<Ticket> ticketList;
    private static int VIEW_TYPE_FLEXIBAR = 0;
    private static int VIEW_TYPE_FIXEDSLOT = 1;
    private int viewType;
    private int minAns, maxAns;
    final int speedScroll = 1000;
    final Handler handler1 = new Handler();

    public SpinerTicketSelectionAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        ItemTicketSelectionSpinerBinding binding = ItemTicketSelectionSpinerBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);

        return new FixedHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        FixedHolder fixedHolder = (FixedHolder) viewHolder;
//            if (ticketList.get(i).getAmount() < 1){
//                fixedHolder.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
//            }else {
//                fixedHolder.tvEntryFees.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getAmount())));
//            }
        fixedHolder.binding.tvEntryFees.setText(Utils.INDIAN_RUPEES +ticketList.get(i).getAmount());
        fixedHolder.binding.tvjtcktwin.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getPerJTicket()))));
        fixedHolder.binding.tvjtcktholder.setText(ticketList.get(i).getTotalJTicketHolder());
        fixedHolder.binding.tvTotalTicket.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getTotalTickets())));
        fixedHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));
        fixedHolder.binding.linear3Options.setVisibility(View.GONE);
        fixedHolder.binding.rvOprions.setVisibility(View.VISIBLE);
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fixedHolder.binding.rvOprions.setLayoutManager(linearLayoutManager);
        SpiningOptionsAdapter adapter = new SpiningOptionsAdapter(context, ticketList.get(i).getSlotes(), Utils.GAME_NOT_START);
        fixedHolder.binding.rvOprions.setAdapter(adapter);
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;

            @Override
            public void run() {
                if (count < adapter.getItemCount()) {
                    if (count == adapter.getItemCount() - 1) {
                        flag = false;
                    } else if (count == 0) {
                        flag = true;
                    }
                    if (flag) count++;
                    else count--;

                    fixedHolder.binding.rvOprions.smoothScrollToPosition(count);
                    handler1.postDelayed(this, speedScroll);
                }
            }
        };

        handler1.postDelayed(runnable, speedScroll);
        if (ticketList.get(i).getTotalTickets()< ticketList.get(i).getMinJoin()) {
            fixedHolder.binding.tvTotalWinnings.setText(String.valueOf("N/A"));
            fixedHolder.binding.tvMaxWinners.setText(String.valueOf("N/A" + " (" + ticketList.get(i).getMaxWinnersPrc() + "%)"));
        } else {
            fixedHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
            fixedHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners() + " (" + ticketList.get(i).getMaxWinnersPrc() + "%)"));
        }

        if (ticketList.get(i).getIsPurchased() == 1) {
            fixedHolder.binding.frmSelected.setVisibility(View.VISIBLE);
            fixedHolder.binding.frmSelected.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent_white));
            fixedHolder.binding.chkSelect.setVisibility(View.GONE);

        } else if (ticketList.get(i).isSelected()) {
            //fixedHolder.frmSelected.setVisibility(View.VISIBLE);
            // fixedHolder.frmSelected.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent_50));
            fixedHolder.binding.chkSelect.setChecked(true);
            fixedHolder.binding.chkSelect.setVisibility(View.VISIBLE);
        } else {
            fixedHolder.binding.chkSelect.setVisibility(View.VISIBLE);
            fixedHolder.binding.frmSelected.setVisibility(View.GONE);
            fixedHolder.binding.chkSelect.setChecked(false);
        }

    }

    public void autoScroll() {
        final int speedScroll = 0;
        final Handler handler = new Handler();

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


        ItemTicketSelectionSpinerBinding binding;
        public FixedHolder(@NonNull ItemTicketSelectionSpinerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            /*itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(ticketList.get(getAdapterPosition()).getIsPurchased()!=1){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(view, pos);
                        }
                    }
                    return true;
                }
            });*/

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(v, pos);
                    }
                }
            });

            binding.chkSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ticketList.get(getAdapterPosition()).getIsPurchased() != 1) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(view, pos);
                        }
                    }
                }
            });

            /*rvOprions.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (ticketList.get(getAdapterPosition()).getIsPurchased() != 1) {
                            onItemClickListener.onItemClick(recyclerView, getAdapterPosition());
                        }
                    }
                    return true;
                }

                @Override
                public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean b) {

                }
            });*/
        }
    }

}
