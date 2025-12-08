package com.tfb.cbit.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemTicketSelectionBinding;
import com.tfb.cbit.databinding.ItemTicketSelectionFixedBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class TicketSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "TicketSelectionAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<Ticket> ticketList;
    private static int VIEW_TYPE_FLEXIBAR = 0;
    private static int VIEW_TYPE_FIXEDSLOT = 1;
    private int viewType;
    private int minAns, maxAns;

    public TicketSelectionAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == VIEW_TYPE_FLEXIBAR) {
            ItemTicketSelectionBinding binding = ItemTicketSelectionBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_ticket_selection, viewGroup, false);
            return new FlexiHolder(binding);
        } else {
            ItemTicketSelectionFixedBinding binding = ItemTicketSelectionFixedBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_ticket_selection_fixed, viewGroup, false);
            return new FixedHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof FlexiHolder) {
            FlexiHolder flexiHolder = (FlexiHolder) viewHolder;
//            if (ticketList.get(i).getAmount() < 1){
//                flexiHolder.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
//            }else {
//                flexiHolder.tvEntryFees.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getAmount())));
//            }
            flexiHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
            flexiHolder.binding.tvTotalTicket.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getTotalTickets())));
            flexiHolder.binding.tvBracketSize.setText(String.valueOf(ticketList.get(i).getBracketSize()));
            //flexiHolder.tvBracketSize.setBackgroundResource(Utils.getImageForSlider(ticketList.get(i).getBracketSize(),maxAns));
          //  flexiHolder.rangeSeekBar.setMinValue(minAns);
           // flexiHolder.rangeSeekBar.setMaxValue(maxAns);
           // flexiHolder.rangeSeekBar.setFixGap(ticketList.get(i).getBracketSize());
            if ((minAns + maxAns) == 0) {
             //   flexiHolder.rangeSeekBar.setMinStartValue((-(float) ticketList.get(i).getBracketSize() / 2));
             //   flexiHolder.rangeSeekBar.setMaxStartValue(((float) ticketList.get(i).getBracketSize() / 2));

            } else {
                float m = 0, barWidht = 0;
                m = ((float) maxAns / 2);
                //Log.d("Bh",m+" m");
                barWidht = ((float) ticketList.get(i).getBracketSize() / 2);
                //Log.d("Bh",barWidht+" bar");
               // flexiHolder.rangeSeekBar.setMinStartValue(((float) m - barWidht));
             //   flexiHolder.rangeSeekBar.setMaxStartValue(((float) m + barWidht));
            }
          //  flexiHolder.rangeSeekBar.setEnabled(false);
          //  flexiHolder.rangeSeekBar.apply();
            // Log.d("Bhavik",flexiHolder.rangeSeekBar.getSelectedMinValue()+" "+ticketList.get(i).getBracketSize()+"  "+((float)m - barWidht));
            // Log.d("Bhavik",flexiHolder.rangeSeekBar.getSelectedMaxValue()+" "+ticketList.get(i).getBracketSize()+"  "+ ((float)m + barWidht));

            if (ticketList.get(i).getTotalTickets() < ticketList.get(i).getMinJoin()) {
                flexiHolder.binding.tvTotalWinnings.setText(String.valueOf("N/A"));
                flexiHolder.binding.tvMaxWinners.setText(String.valueOf("N/A" + " (" + ticketList.get(i).getMaxWinnersPrc() + "%)"));
            } else {
                flexiHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
                flexiHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners() + " (" + ticketList.get(i).getMaxWinnersPrc() + "%)"));
            }
            flexiHolder.binding.tvMinAns.setText(String.valueOf(minAns));
            flexiHolder.binding.tvMaxAns.setText(String.valueOf(maxAns));

            if (ticketList.get(i).getIsPurchased() == 1) {
                flexiHolder.binding.frmSelected.setVisibility(View.VISIBLE);
                flexiHolder.binding.frmSelected.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent_white));
                flexiHolder.binding.chkSelect.setVisibility(View.GONE);
            } else if (ticketList.get(i).isSelected()) {
                flexiHolder.binding.chkSelect.setVisibility(View.VISIBLE);
                //flexiHolder.frmSelected.setVisibility(View.VISIBLE);
                // flexiHolder.frmSelected.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent_50));
                flexiHolder.binding.chkSelect.setChecked(true);
            } else {
                flexiHolder.binding.chkSelect.setVisibility(View.VISIBLE);
                flexiHolder.binding.frmSelected.setVisibility(View.GONE);
                flexiHolder.binding.chkSelect.setChecked(false);
            }
        } else {
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

            if (ticketList.get(i).getSlotes().size() == 2 ||
                    ticketList.get(i).getSlotes().size() == 3) {

                if (ticketList.get(i).getSlotes().size() == 2) {
                    fixedHolder.binding.tvMinus.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                    fixedHolder.binding.tvZero.setText(ticketList.get(i).getSlotes().get(1).getDisplayValue());
                    fixedHolder.binding.tvPlus.setVisibility(View.GONE);
                } else {
                    fixedHolder.binding.linear3Options.setBackgroundColor(Color.parseColor("#E6E2E2"));
                    Log.d(TAG, "displayvalue: "+ticketList.get(i).getSlotes().get(0).getDisplayValue());

                    fixedHolder.binding.tvMinus.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                    fixedHolder.binding.tvZero.setText(ticketList.get(i).getSlotes().get(1).getDisplayValue());
                    fixedHolder.binding.tvPlus.setText(ticketList.get(i).getSlotes().get(2).getDisplayValue());
                    fixedHolder.binding.tvPlus.setVisibility(View.VISIBLE);
                    if(ticketList.get(i).getSlotes().get(0).getDisplayValue().equalsIgnoreCase("Red win")){
                      //  fixedHolder.tvMinus.setBackgroundColor(Color.parseColor("#fb0102"));
                        fixedHolder.binding.tvMinus.setBackgroundResource(R.drawable.bg_red);
                        fixedHolder.binding.tvMinus.setTextColor(Color.parseColor("#ffffff"));
                    }
                    if(ticketList.get(i).getSlotes().get(2).getDisplayValue().equalsIgnoreCase("Blue win")){
                        //fixedHolder.tvPlus.setBackgroundColor(Color.parseColor("#0433ff"));
                        fixedHolder.binding.tvPlus.setBackgroundResource(R.drawable.bg_blue);
                        fixedHolder.binding.tvPlus.setTextColor(Color.parseColor("#ffffff"));
                    }
                }

                fixedHolder.binding.linear3Options.setVisibility(View.VISIBLE);
                fixedHolder.binding.rvOprions.setVisibility(View.GONE);
            } else {
                fixedHolder.binding.linear3Options.setVisibility(View.GONE);
                fixedHolder.binding.rvOprions.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                fixedHolder.binding.rvOprions.setLayoutManager(linearLayoutManager);
                fixedHolder.binding.rvOprions.setAdapter(new OptionsAdapter(context, ticketList.get(i).getSlotes()));
            }

            if (ticketList.get(i).getTotalTickets() < ticketList.get(i).getMinJoin()) {
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

    class FlexiHolder extends RecyclerView.ViewHolder {


       /* @BindView(R.id.rangeSeekBar)
        CrystalRangeSeekbar rangeSeekBar;
*/ItemTicketSelectionBinding binding;
        public FlexiHolder(@NonNull ItemTicketSelectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

           /* itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(ticketList.get(getAdapterPosition()).getIsPurchased()!=1){
                        onItemLongClickListener.onItemLongClick(view,getAdapterPosition());
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
                    if (ticketList.get(getAdapterPosition()).getIsPurchased() != 1) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }

    class FixedHolder extends RecyclerView.ViewHolder {

        ItemTicketSelectionFixedBinding binding;
        public FixedHolder(@NonNull ItemTicketSelectionFixedBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

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
