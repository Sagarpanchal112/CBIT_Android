package com.tfb.cbit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
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

import com.tfb.cbit.R;
import com.tfb.cbit.animations.Flip;
import com.tfb.cbit.animations.Render;
import com.tfb.cbit.databinding.ItemAnyTicketSelectionBinding;
import com.tfb.cbit.databinding.ItemTicketAnySelectionFixedBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class PaperChitTicketSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "TicketSelectionAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<Ticket> ticketList;
    private static int VIEW_TYPE_FLEXIBAR = 0;
    private static int VIEW_TYPE_FIXEDSLOT = 1;
    private int viewType;
    private int minAns, maxAns, noOfPlayer;
    Render render;

    public PaperChitTicketSelectionAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
        render = new Render(context);
        render.setDuration(2000);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == VIEW_TYPE_FLEXIBAR) {
            ItemAnyTicketSelectionBinding binding = ItemAnyTicketSelectionBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new FlexiHolder(binding);
        } else {
            ItemTicketAnySelectionFixedBinding binding = ItemTicketAnySelectionFixedBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new FixedHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof FlexiHolder) {
            FlexiHolder flexiHolder = (FlexiHolder) viewHolder;
            flexiHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
            flexiHolder.binding.tvTotalTicket.setText(Utils.getComaFormat(String.valueOf(noOfPlayer)));
            int pending = (noOfPlayer - ticketList.get(i).getTotalTickets());
           // flexiHolder.binding.tvP.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getTotalTickets())) + "Played /" + pending + " Pending");
            flexiHolder.binding.tvBracketSize.setText(String.valueOf(ticketList.get(i).getBracketSize()));
            if (ticketList.get(i).getTotalTickets() == 0 || ticketList.get(i).getTotalTickets() == 1) {
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
                flexiHolder.binding.chkSelect.setChecked(true);
            } else {
                flexiHolder.binding.chkSelect.setVisibility(View.VISIBLE);
                flexiHolder.binding.frmSelected.setVisibility(View.GONE);
                flexiHolder.binding.chkSelect.setChecked(false);
            }

        } else {
            FixedHolder fixedHolder = (FixedHolder) viewHolder;
            fixedHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
            fixedHolder.binding.tvjtcktwin.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getPerJTicket()))));
            fixedHolder.binding.tvjtcktholder.setText(ticketList.get(i).getTotalJTicketHolder());
            fixedHolder.binding.tvTotalTicket.setText(Utils.getComaFormat(String.valueOf(noOfPlayer)));
            fixedHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));
            int pending = (noOfPlayer - ticketList.get(i).getTotalTickets());
            fixedHolder.binding.tvPlayed.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getTotalTickets())) + "Played /" + pending + " Pending");

            if (ticketList.get(i).getSlotes().size() == 2 ||
                    ticketList.get(i).getSlotes().size() == 3) {
                if (ticketList.get(i).getSlotes().size() == 2) {
                    fixedHolder.binding.tvMinus.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                    fixedHolder.binding.tvZero.setText(ticketList.get(i).getSlotes().get(1).getDisplayValue());
                    fixedHolder.binding.tvPlus.setVisibility(View.GONE);
                } else {
                    fixedHolder.binding.linear3Options.setBackgroundColor(Color.parseColor("#E6E2E2"));
                    Log.d(TAG, "displayvalue: " + ticketList.get(i).getSlotes().get(0).getDisplayValue());
                    fixedHolder.binding.tvMinus.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                    fixedHolder.binding.tvZero.setText(ticketList.get(i).getSlotes().get(1).getDisplayValue());
                    fixedHolder.binding.tvPlus.setText(ticketList.get(i).getSlotes().get(2).getDisplayValue());
                    fixedHolder.binding.tvPlus.setVisibility(View.VISIBLE);
                    if (ticketList.get(i).getSlotes().get(0).getDisplayValue().equalsIgnoreCase("Red win")) {
                        fixedHolder.binding.tvMinus.setBackgroundResource(R.drawable.bg_red);
                        fixedHolder.binding.tvMinus.setTextColor(Color.parseColor("#ffffff"));
                    }
                    if (ticketList.get(i).getSlotes().get(2).getDisplayValue().equalsIgnoreCase("Blue win")) {
                        fixedHolder.binding.tvPlus.setBackgroundResource(R.drawable.bg_blue);
                        fixedHolder.binding.tvPlus.setTextColor(Color.parseColor("#ffffff"));
                    }
                }
                //    fixedHolder.tvOption.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                render.setAnimation(Flip.OutX(fixedHolder.binding.ivImage));
                render.start();
             /*   render.setAnimation(Zoom.Out(imageView));
                render.start();
            */
                CountDownTimer yourCountDownTimer = new CountDownTimer(1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        fixedHolder.binding.tvMinus.setVisibility(View.VISIBLE);
                    }
                }.start();
                fixedHolder.binding.ivVadapav.setVisibility(View.VISIBLE);
                render.setAnimation(Flip.InY(fixedHolder.binding.ivVadapav));
                render.start();
                fixedHolder.binding.ivImage.setVisibility(View.GONE);
                fixedHolder.binding.linear3Options.setVisibility(View.VISIBLE);
                fixedHolder.binding.rvOprions.setVisibility(View.GONE);
            } else {
                fixedHolder.binding.linear3Options.setVisibility(View.GONE);
                fixedHolder.binding.rvOprions.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                fixedHolder.binding.rvOprions.setLayoutManager(linearLayoutManager);
                fixedHolder.binding.rvOprions.setAdapter(new PaperChitOptionsAdapter(context, ticketList.get(i).getSlotes()));
            }

            if (ticketList.get(i).getTotalTickets() == 0 || ticketList.get(i).getTotalTickets() == 1) {
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

    public void setNoPlayer(int noOfPlayer) {
        this.noOfPlayer = noOfPlayer;
    }

    class FlexiHolder extends RecyclerView.ViewHolder {

        ItemAnyTicketSelectionBinding binding;

        public FlexiHolder(@NonNull ItemAnyTicketSelectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

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
        }
    }

    class FixedHolder extends RecyclerView.ViewHolder {
        ItemTicketAnySelectionFixedBinding binding;

        public FixedHolder(@NonNull ItemTicketAnySelectionFixedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
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

        }
    }
}
