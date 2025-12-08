package com.tfb.cbit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemTicketAnySelectionFixedBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.anytimegame.Content;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class AnyTicketSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "TicketSelectionAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<Content> ticketList;
    private static int VIEW_TYPE_FLEXIBAR = 0;
    private static int VIEW_TYPE_FIXEDSLOT = 1;
    private int viewType;
    private int minAns, maxAns;

    public AnyTicketSelectionAdapter(Context context, List<Content> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
         ItemTicketAnySelectionFixedBinding binding = ItemTicketAnySelectionFixedBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new FixedHolder(binding);

    }

    public void setNoPlayer(int noOfPlayer, int pending, int played) {
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        FixedHolder fixedHolder = (FixedHolder) viewHolder;
        fixedHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
        fixedHolder.binding.tvTotalTicket.setText(String.valueOf(ticketList.get(i).getNo_of_players()));
        fixedHolder.binding.tvMaxWinners.setText(ticketList.get(i).getNo_of_winners());
        fixedHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinningAmount())));

        if (ticketList.get(i).getGame_type().equals("0-9")) {
            if(ticketList.get(i).getSlotes().equals("3")){
                fixedHolder.binding.tvMinus.setText("0 to 3");
                fixedHolder.binding.tvZero.setText("4 to 6");
                fixedHolder.binding.tvPlus.setText("7 to 9");
                fixedHolder.binding.tvPlus.setVisibility(View.VISIBLE);

            }else if(ticketList.get(i).getSlotes().equals("2")){
                fixedHolder.binding.tvMinus.setText("0 to 4");
                fixedHolder.binding.tvZero.setText("5 to 9");
                fixedHolder.binding.tvPlus.setVisibility(View.GONE);

            }
         }else if(ticketList.get(i).getGame_type().equals("rdb")){
            fixedHolder.binding.linear3Options.setBackgroundColor(Color.parseColor("#E6E2E2"));
            fixedHolder.binding.tvMinus.setText("Red");
            fixedHolder.binding.tvMinus.setBackgroundResource(R.drawable.bg_red);
            fixedHolder.binding.tvMinus.setTextColor(Color.parseColor("#ffffff"));
            fixedHolder.binding.tvZero.setText("Draw");
            fixedHolder.binding.tvPlus.setText("Blue");
            fixedHolder.binding.tvPlus.setBackgroundResource(R.drawable.bg_blue);
            fixedHolder.binding.tvPlus.setTextColor(Color.parseColor("#ffffff"));

        }
        if(ticketList.get(i).isSelected()){
            fixedHolder.binding.chkSelect.setChecked(true);
        }else{
            fixedHolder.binding.chkSelect.setChecked(false);

        }


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

        ItemTicketAnySelectionFixedBinding  binding;

        public FixedHolder(@NonNull ItemTicketAnySelectionFixedBinding binding) {
            super(binding.getRoot());
            this.binding =binding;
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
                    //  if (ticketList.get(getAdapterPosition()).getIsPurchased() != 1) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, pos);
                    }
                    //   }
                }
            });

        }
    }
}
