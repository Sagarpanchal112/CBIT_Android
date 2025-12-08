package com.tfb.cbit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemTicketSelectionAnySpinerBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemClickStringListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.anytimegame.Content;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class AnySpinerTicketSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "TicketSelectionAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemClickStringListener onItemClickStringListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<Content> ticketList;
    private ArrayList<String> seletedItem;
    private static int VIEW_TYPE_FLEXIBAR = 0;
    private static int VIEW_TYPE_FIXEDSLOT = 1;
    private int viewType;
    private int minAns, maxAns;

    public AnySpinerTicketSelectionAdapter(Context context, List<Content> ticketList, ArrayList<String> seletedItem) {
        this.context = context;
        this.ticketList = ticketList;
        this.seletedItem = seletedItem;
    }
    public void setArrayData( ArrayList<String> seletedItem) {
        this.seletedItem = seletedItem;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        ItemTicketSelectionAnySpinerBinding binding = ItemTicketSelectionAnySpinerBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new FixedHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        FixedHolder fixedHolder = (FixedHolder) viewHolder;
        fixedHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
        fixedHolder.binding.tvGaneNo.setText("Game No :  "+ticketList.get(i).getGame_played());
       // fixedHolder.tvjtcktwin.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getPerJTicket()))));
      //  fixedHolder.tvjtcktholder.setText(ticketList.get(i).getTotalJTicketHolder());
        fixedHolder.binding.tvTotalTicket.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getNo_of_players())));
      //  fixedHolder.tvMaxWinners.setText(ticketList.get(i).getMaxWinners()+"("+ticketList.get(i).getMaxWinnersPrc()+"%)");

        fixedHolder.binding.tvMaxWinners.setText(ticketList.get(i).getNo_of_winners());
        fixedHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getWinningAmount())));

        fixedHolder.binding.linear3Options.setVisibility(View.GONE);
        if(ticketList.get(i).isSelected()){
            fixedHolder.binding.chkSelect.setChecked(true);
        }else{
            fixedHolder.binding.chkSelect.setChecked(false);

        }
       //  fixedHolder.tv_played.setText(ticketList.get(i).getPlayed() + " Played /" + ticketList.get(i).getPending() + " Pending");


        /*if (ticketList.get(i).getTotalTickets() == 0 || ticketList.get(i).getTotalTickets() == 1) {
            fixedHolder.tvTotalWinnings.setText(String.valueOf("N/A"));
          //  fixedHolder.tvMaxWinners.setText(String.valueOf("N/A" + " (" + ticketList.get(i).getMaxWinnersPrc() + "%)"));
            fixedHolder.tvMaxWinners.setText(ticketList.get(i).getMaxWinners()+"("+ticketList.get(i).getMaxWinnersPrc()+"%)");
        } else {
            fixedHolder.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
          //  fixedHolder.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners() + " (" + ticketList.get(i).getMaxWinnersPrc() + "%)"));
            fixedHolder.tvMaxWinners.setText(ticketList.get(i).getMaxWinners()+"("+ticketList.get(i).getMaxWinnersPrc()+"%)");
        }
        fixedHolder.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
*/
        if (ticketList.get(i).isSelected()) {
            //fixedHolder.frmSelected.setVisibility(View.VISIBLE);
            // fixedHolder.frmSelected.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent_50));
            fixedHolder.binding.chkSelect.setChecked(true);
            fixedHolder.binding.chkSelect.setVisibility(View.VISIBLE);
        } else {
            fixedHolder.binding.chkSelect.setVisibility(View.VISIBLE);
            fixedHolder.binding.frmSelected.setVisibility(View.GONE);
            fixedHolder.binding.chkSelect.setChecked(false);
        }
Log.i("seletedItem Size","=>"+seletedItem.size());
        if(seletedItem.size()==2){
            fixedHolder.binding.linear3Options.setVisibility(View.VISIBLE);
            fixedHolder.binding.rvOprions.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_logo)
                    .error(R.drawable.loading_logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform();
            Glide.with(context).load(seletedItem.get(0)).apply(options).into(fixedHolder.binding.imgOne);
            Glide.with(context).load(seletedItem.get(1)).apply(options).into(fixedHolder.binding.imgTwo);
        }else{
            fixedHolder.binding.rvOprions.setVisibility(View.VISIBLE);
            fixedHolder.binding.linear3Options.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            fixedHolder.binding.rvOprions.setLayoutManager(linearLayoutManager);
            fixedHolder.binding.rvOprions.setAdapter(new AnySpiningOptionsAdapter(context, seletedItem, Utils.GAME_NOT_START));

        }

        fixedHolder.binding.imgSuffles.setVisibility(View.GONE);
        fixedHolder.binding.imgSuffles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickStringListener.onItemStringClick(v, i);

            }
        });

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
    public void setOnItemClickStringListener(OnItemClickStringListener onItemClickListener) {
        this.onItemClickStringListener = onItemClickListener;
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

        ItemTicketSelectionAnySpinerBinding binding;

        public FixedHolder(@NonNull ItemTicketSelectionAnySpinerBinding binding) {
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

            binding. chkSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 //   if (ticketList.get(getAdapterPosition()).getIsPurchased() != 1) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(view, pos);
                        }
                  //  }
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
