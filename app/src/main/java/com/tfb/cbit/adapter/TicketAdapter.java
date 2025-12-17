package com.tfb.cbit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemTicketFixedBinding;
import com.tfb.cbit.databinding.ItemTicketFlexibarBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnRangeListener;
import com.tfb.cbit.interfaces.OnSlotListener;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class TicketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnRangeListener onRangeListener;
    private OnSlotListener onSlotListener;
    private static int VIEW_TYPE_FLEXIBAR = 0;
    private static int VIEW_TYPE_FIXEDSLOT = 1;
    private int viewType;
    private int minAns, maxAns;
    private String gameStatus = "";
    private String constest_type = "";
    private List<Ticket> ticketList;
    public int count = 0;

    public TicketAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        if (i == VIEW_TYPE_FLEXIBAR) {
            ItemTicketFlexibarBinding binding = ItemTicketFlexibarBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
           return new FlexiHolder(binding);
        } else {
            ItemTicketFixedBinding binding = ItemTicketFixedBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new FixedHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof FlexiHolder) {
            Log.i("TAG", "in FlexiHolder=>");
            final FlexiHolder flexiHolder = (FlexiHolder) viewHolder;
//            if (ticketList.get(i).getAmount() < 1){
//                flexiHolder.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
//            }else {
//                flexiHolder.tvEntryFees.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getAmount())));
//            }

            flexiHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
            flexiHolder.binding.tvTotalTicket.setText(String.valueOf(ticketList.get(i).getNo_of_players()));
            flexiHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
            flexiHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));
            flexiHolder.binding.tvMinAns.setText(String.valueOf(minAns));
            flexiHolder.binding.tvMaxAns.setText(String.valueOf(maxAns));

            //  flexiHolder.rangeSeekBar.setMinValue(minAns);
            //  flexiHolder.rangeSeekBar.setMaxValue(maxAns);
            //  flexiHolder.rangeSeekBar.setFixGap(ticketList.get(i).getBracketSize() - 1);

            if (!ticketList.get(i).getMinValue().isEmpty()) {
                flexiHolder.binding.tvAnsSelection.setText(String.valueOf(ticketList.get(i).getDisplayView()).replace(' ', '\n'));
                //  flexiHolder.rangeSeekBar.setMinStartValue(Integer.parseInt(ticketList.get(i).getMinValue()));
                //  flexiHolder.rangeSeekBar.setMaxStartValue(Integer.parseInt(ticketList.get(i).getMaxValue()));
            }

            //  flexiHolder.rangeSeekBar.apply();
            /*flexiHolder.rangeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                @Override
                public void valueChanged(Number minValue, Number maxValue) {
                    onRangeListener.onRangeValue(minValue.intValue(), maxValue.intValue(), i);
                }
            });*/
          /*  flexiHolder.rangeSeekBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
                @Override
                public void finalValue(Number minValue, Number maxValue) {
                    onRangeListener.onRangeValue(minValue.intValue(), maxValue.intValue(), i);
                }
            });*/

            if (gameStatus.equals(Utils.GAME_NOT_START)) {
                //flexiHolder.linearNotSelection.setVisibility(View.VISIBLE);
                flexiHolder.binding.linearSelection.setVisibility(View.GONE);
                flexiHolder.binding.tvAnsSelection.setText("Empty");
                flexiHolder.binding.tvLockNow.setAlpha(.5f);
                flexiHolder.binding.tvLockNow.setEnabled(false);
                flexiHolder.binding.linearLock.setVisibility(View.GONE);
             /*   flexiHolder.rangeSeekBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                flexiHolder.rangeSeekBar.setVisibility(View.GONE);
             */
                flexiHolder.binding.frameBarDesign.setVisibility(View.VISIBLE);
                flexiHolder.binding.tvBracketSize.setText(String.valueOf(ticketList.get(i).getBracketSize()));
                //flexiHolder.tvBracketSize.setBackgroundResource(Utils.getImageForSlider(ticketList.get(i).getBracketSize(),maxAns));
                // flexiHolder.rangeSeekBarTemp.setMinValue(minAns);
                // flexiHolder.rangeSeekBarTemp.setMaxValue(maxAns);
                // flexiHolder.rangeSeekBarTemp.setFixGap(ticketList.get(i).getBracketSize());
                // flexiHolder.rangeSeekBarTemp.setEnabled(false);
                if ((minAns + maxAns) == 0) {
                    //  flexiHolder.rangeSeekBarTemp.setMinStartValue((-(float) ticketList.get(i).getBracketSize() / 2));
                    // flexiHolder.rangeSeekBarTemp.setMaxStartValue(((float) ticketList.get(i).getBracketSize() / 2));

                } else {
                    float m = 0, barWidht = 0;
                    m = ((float) maxAns / 2);
                    //Log.d("Bh",m+" m");
                    barWidht = ((float) ticketList.get(i).getBracketSize() / 2);
                    //Log.d("Bh",barWidht+" bar");
                    // flexiHolder.rangeSeekBarTemp.setMinStartValue(((float) m - barWidht));
                    // flexiHolder.rangeSeekBarTemp.setMaxStartValue(((float) m + barWidht));
                }
                // flexiHolder.rangeSeekBarTemp.apply();
            } else {
                // flexiHolder.rangeSeekBar.setVisibility(View.VISIBLE);
                flexiHolder.binding.frameBarDesign.setVisibility(View.GONE);
                flexiHolder.binding.tvLockNow.setAlpha(1f);
                flexiHolder.binding.tvLockNow.setEnabled(true);


                if (ticketList.get(i).getMinValue().isEmpty()) {
                    //  ticketList.get(i).setMinValue(String.valueOf(flexiHolder.rangeSeekBar.getSelectedMinValue()));
                    // ticketList.get(i).setMaxValue(String.valueOf(flexiHolder.rangeSeekBar.getSelectedMaxValue()));
                    // ticketList.get(i).setDisplayView(String.valueOf(flexiHolder.rangeSeekBar.getSelectedMinValue() + " To " + flexiHolder.rangeSeekBar.getSelectedMaxValue()));
                    flexiHolder.binding.tvAnsSelection.setText(String.valueOf(ticketList.get(i).getDisplayView()).replace(' ', '\n'));
                }
                if (ticketList.get(i).isIsLock()) {
                    //flexiHolder.linearNotSelection.setVisibility(View.GONE);
                    flexiHolder.binding.linearSelection.setVisibility(View.GONE);
                    flexiHolder.binding.linearLock.setVisibility(View.VISIBLE);
                    if (!ticketList.get(i).getDisplayView().isEmpty()) {
                        flexiHolder.binding.tvSelectedAns.setText(ticketList.get(i).getDisplayView());
                    } else {
                        /*flexiHolder.tvSelectedAns.setText(String.valueOf(ticketList.get(i).getUserSelect().getStartValue()+" To "+
                                ticketList.get(i).getUserSelect().getEndValue()));*/
                        flexiHolder.binding.tvSelectedAns.setText(String.valueOf(ticketList.get(i).getUserSelect().getDisplayValue()));
                        //  flexiHolder.rangeSeekBar.setMinStartValue(Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue()));
                        //  flexiHolder.rangeSeekBar.setMaxStartValue(Integer.parseInt(ticketList.get(i).getUserSelect().getEndValue()));
                        //  flexiHolder.rangeSeekBar.apply();
                    }
                    flexiHolder.binding.tvLockTime.setText(String.valueOf("Locked at: " + ticketList.get(i).getLockTime()));
                   /* flexiHolder.rangeSeekBar.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });*/
                } else {
                    // flexiHolder.linearNotSelection.setVisibility(View.GONE);
                    flexiHolder.binding.linearSelection.setVisibility(View.GONE);
                    flexiHolder.binding.linearLock.setVisibility(View.GONE);
                   /* flexiHolder.rangeSeekBar.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });*/
                }
            }

        } else {
            Log.i("TAG", "in FixedHolder=>");
            final FixedHolder fixedHolder = (FixedHolder) viewHolder;
//            if (ticketList.get(i).getAmount() < 1){
//                fixedHolder.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
//            }else {
//                fixedHolder.tvEntryFees.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getAmount())));
//            }
            fixedHolder.binding.tvEntryFees.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(ticketList.get(i).getAmount()))));
            fixedHolder.binding.tvTotalTicket.setText(Utils.getComaFormat(String.valueOf(ticketList.get(i).getNo_of_players())));
            fixedHolder.binding.tvTotalWinnings.setText(Utils.getCurrencyFormat(String.valueOf(ticketList.get(i).getTotalWinnings())));
            fixedHolder.binding.tvMaxWinners.setText(String.valueOf(ticketList.get(i).getMaxWinners()));

            if (ticketList.get(i).getSlotes().size() == 2 || ticketList.get(i).getSlotes().size() == 3) {
                if (ticketList.get(i).getSlotes().size() == 2) {
                    fixedHolder.binding.tvMinus.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                    fixedHolder.binding.tvZero.setText(ticketList.get(i).getSlotes().get(1).getDisplayValue());
                    fixedHolder.binding.tvPlus.setVisibility(View.GONE);

                    fixedHolder.binding.tvMinus.setBackgroundResource(R.drawable.bg_yellow_radius);
                    fixedHolder.binding.tvMinus.setTextColor(Color.parseColor("#1a505d"));
                    fixedHolder.binding.tvZero.setBackgroundResource(R.drawable.bg_yellow_radius);
                    fixedHolder.binding.tvZero.setTextColor(Color.parseColor("#1a505d"));

                } else {
                    if (constest_type.equalsIgnoreCase("rdb")) {
                        fixedHolder.binding.frameContent.setBackgroundColor(Color.parseColor("#E6E2E2"));
                        fixedHolder.binding.linear3Options.setBackgroundColor(Color.parseColor("#E6E2E2"));
                        fixedHolder.binding.tvMinus.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                        fixedHolder.binding.tvZero.setText(ticketList.get(i).getSlotes().get(1).getDisplayValue());
                        fixedHolder.binding.tvPlus.setText(ticketList.get(i).getSlotes().get(2).getDisplayValue());
                        fixedHolder.binding.tvPlus.setVisibility(View.VISIBLE);
                        if (ticketList.get(i).getSlotes().get(0).getDisplayValue().equalsIgnoreCase("Red win")) {
                            fixedHolder.binding.tvMinus.setBackgroundResource(R.drawable.bg_red);
                            fixedHolder.binding.tvMinus.setTextColor(Color.parseColor("#ffffff"));
                        }
                        if (ticketList.get(i).getSlotes().get(1).getDisplayValue().equalsIgnoreCase("Draw")) {
                            fixedHolder.binding.tvZero.setBackgroundResource(R.drawable.bg_yellow_radius);
                            fixedHolder.binding.tvZero.setTextColor(Color.parseColor("#1a505d"));
                        }
                        if (ticketList.get(i).getSlotes().get(2).getDisplayValue().equalsIgnoreCase("Blue win")) {
                            fixedHolder.binding.tvPlus.setBackgroundResource(R.drawable.bg_blue);
                            fixedHolder.binding.tvPlus.setTextColor(Color.parseColor("#ffffff"));
                        }
                    } else {
                        fixedHolder.binding.tvMinus.setText(ticketList.get(i).getSlotes().get(0).getDisplayValue());
                        fixedHolder.binding.tvZero.setText(ticketList.get(i).getSlotes().get(1).getDisplayValue());
                        fixedHolder.binding.tvPlus.setText(ticketList.get(i).getSlotes().get(2).getDisplayValue());

                        fixedHolder.binding.tvMinus.setBackgroundResource(R.drawable.bg_yellow_radius);
                        fixedHolder.binding.tvMinus.setTextColor(Color.parseColor("#1a505d"));
                        fixedHolder.binding.tvZero.setBackgroundResource(R.drawable.bg_yellow_radius);
                        fixedHolder.binding.tvZero.setTextColor(Color.parseColor("#1a505d"));
                        fixedHolder.binding.tvPlus.setBackgroundResource(R.drawable.bg_yellow_radius);
                        fixedHolder.binding.tvPlus.setTextColor(Color.parseColor("#1a505d"));

                    }

                }

                //System.err.println("tvAnsSelection In 0 "+ticketList.get(i).getMinValue().isEmpty());
                if (!ticketList.get(i).getMinValue().isEmpty()) {
                    if (ticketList.get(i).getSlotes().size() == 2) {
                        //System.err.println("tvAnsSelection In "+Integer.parseInt(ticketList.get(i).getMinValue())+" "+ticketList.get(i).getSlotes().get(0).getStartValue());
                        if (Integer.parseInt(ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(0).getStartValue()) {
                            fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                            fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                            fixedHolder.binding.tvMinus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                            fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                        } else if (Integer.parseInt(ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(1).getStartValue()) {
                            fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                            fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                            fixedHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                            fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                        } else if (ticketList.get(i).getSlotes().size() == 3) {
                            if (Integer.parseInt(ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(2).getStartValue()) {
                                fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                            }
                        }
                    } else {
                        if (constest_type.equalsIgnoreCase("rdb")) {
                            if (Integer.parseInt(ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(0).getStartValue()) {
                                fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                fixedHolder.binding.tvMinus.setTextColor(context.getResources().getColor(R.color.color_red));
                                fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                fixedHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_blue));
                            } else if (Integer.parseInt(ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(1).getStartValue()) {
                                fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_red));
                                fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                fixedHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_blue));
                            } else if (ticketList.get(i).getSlotes().size() == 3) {
                                if (Integer.parseInt(ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(2).getStartValue()) {
                                    fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                    fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_red));
                                    fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                    fixedHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                    fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                    fixedHolder.binding.tvPlus.setTextColor(context.getResources().getColor(R.color.color_blue));
                                }
                            }
                        } else {
                            if (Integer.parseInt(ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(0).getStartValue()) {
                                fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                fixedHolder.binding.tvMinus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            } else if (Integer.parseInt(ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(1).getStartValue()) {
                                fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                fixedHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                            } else if (ticketList.get(i).getSlotes().size() == 3) {
                                fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                fixedHolder.binding.tvPlus.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                            }
                        }

                    }
                }
                //set on 15-dec 2025
//                if (ticketList.get(i).getSlotes().size() == 3) {
//                    fixedHolder.binding.frameContent.setVisibility(View.GONE);
//                    fixedHolder.binding.linear3Options.setVisibility(View.GONE);
//                    fixedHolder.binding.rvOprions.setVisibility(View.GONE);
//                }else{
//                    fixedHolder.binding.frameContent.setVisibility(View.VISIBLE);
//                    fixedHolder.binding.linear3Options.setVisibility(View.VISIBLE);
//                    fixedHolder.binding.rvOprions.setVisibility(View.GONE);
//                }
                fixedHolder.binding.linear3Options.setVisibility(View.GONE);
                fixedHolder.binding.rvOprions.setVisibility(View.GONE);
            } else {
                fixedHolder.binding.linear3Options.setVisibility(View.GONE);
                fixedHolder.binding.rvOprions.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                fixedHolder.binding.rvOprions.setLayoutManager(linearLayoutManager);

              /*  if (fixedHolder.rvOprions.getItemAnimator() != null)
                    ((SimpleItemAnimator) fixedHolder.rvOprions.getItemAnimator()).setSupportsChangeAnimations(false);
            */
                fixedHolder.optionsAdapter = new OptionsAdapter(context, ticketList.get(i).getSlotes(), gameStatus);
                fixedHolder.optionsAdapter.setOnItemClickListener((view, position) -> {
                    if (!gameStatus.equals(Utils.GAME_NOT_START) && !ticketList.get(i).isIsLock()) {
                        onSlotListener.onSlotValue(fixedHolder.binding.rvOprions, i, position);
                    }
                });
                fixedHolder.binding.rvOprions.setAdapter(fixedHolder.optionsAdapter);
                final int speedScroll = 500;
                final Handler handler = new Handler();
                Runnable runnable = null;
                Runnable finalRunnable = runnable;
                runnable = new Runnable() {
                    int count = 0;

                    @Override
                    public void run() {
                        if (count < ticketList.get(i).getSlotes().size()) {
                            count = count + 2;
                            fixedHolder.binding.rvOprions.smoothScrollToPosition(count);
                            handler.postDelayed(this, speedScroll);
                        } else {
                            fixedHolder.binding.rvOprions.smoothScrollToPosition(1);
                            handler.removeCallbacks(finalRunnable);
                        }


                    }
                };
                handler.postDelayed(runnable, speedScroll);
                for (int pos = 0; pos < ticketList.get(i).getSlotes().size(); pos++) {
                    if (ticketList.get(i).getSlotes().get(pos).isIsSelected()) {
                        fixedHolder.binding.rvOprions.scrollToPosition(pos);
                        break;
                    }
                }

                if (!ticketList.get(i).getMinValue().isEmpty()) {
                    fixedHolder.binding.tvAnsSelection.setText(String.valueOf(ticketList.get(i).getDisplayView()));
                }
            }


            if (gameStatus.equals(Utils.GAME_NOT_START)) {
                // fixedHolder.linearNotSelection.setVisibility(View.VISIBLE);
                fixedHolder.binding.tvAnsSelection.setText("Empty");
                fixedHolder.binding.tvLockNow.setAlpha(.5f);
                fixedHolder.binding.tvLockNow.setEnabled(false);

                fixedHolder.binding.tvMinus.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                fixedHolder.binding.tvZero.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                if (ticketList.get(i).getSlotes().size() == 3)
                    fixedHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.white));
                fixedHolder.binding.tvPlus.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));

                fixedHolder.binding.linearSelection.setVisibility(View.GONE);
                fixedHolder.binding.linearLock.setVisibility(View.GONE);
            } else {
                fixedHolder.binding.tvLockNow.setAlpha(1f);
                fixedHolder.binding.tvLockNow.setEnabled(true);
            /*    fixedHolder.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                fixedHolder.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                fixedHolder.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
*/

                if (ticketList.get(i).isIsLock()) {
                    fixedHolder.binding.tvLockNow.setAlpha(0.5f);
                    fixedHolder.binding.tvLockNow.setEnabled(false);
                    // fixedHolder.linearNotSelection.setVisibility(View.GONE);
                    fixedHolder.binding.linearSelection.setVisibility(View.GONE);
                    fixedHolder.binding.linearLock.setVisibility(View.VISIBLE);
                    if (!ticketList.get(i).getDisplayView().isEmpty()) {
                        fixedHolder.binding.tvSelectedAns.setText(ticketList.get(i).getDisplayView());
                    } else {
                       /* fixedHolder.tvSelectedAns.setText(String.valueOf(ticketList.get(i).getUserSelect().getStartValue()+" To "+
                                ticketList.get(i).getUserSelect().getEndValue()));*/
                        fixedHolder.binding.tvSelectedAns.setText(String.valueOf(ticketList.get(i).getUserSelect().getDisplayValue()));
                        if (ticketList.get(i).getSlotes().size() == 2 || ticketList.get(i).getSlotes().size() == 3) {

                            if (ticketList.get(i).getSlotes().size() == 2) {
                                //System.err.println("tvAnsSelection here" + Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue())+" "+ticketList.get(i).getSlotes().get(0).getStartValue());
                                if (Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue()) == ticketList.get(i).getSlotes().get(0).getStartValue()) {
                                    fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                    fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                    fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                    fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                } else if (Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue()) == ticketList.get(i).getSlotes().get(1).getStartValue()) {
                                    fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                    fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                    fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                    fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                } else if (ticketList.get(i).getSlotes().size() == 3) {
                                    if (Integer.parseInt(ticketList.get(i).getUserSelect().getStartValue()) == ticketList.get(i).getSlotes().get(2).getStartValue()) {
                                        fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                        fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                        fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                        fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                    }
                                }
                            } else {
                                if (Integer.parseInt(TextUtils.isEmpty(ticketList.get(i).getMinValue()) ? String.valueOf(0) : ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(0).getStartValue()) {
                                    fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                    fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                    fixedHolder.binding.tvMinus.setTextColor(context.getResources().getColor(R.color.color_red));
                                    fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                    fixedHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                    fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_blue));
                                } else if (Integer.parseInt(TextUtils.isEmpty(ticketList.get(i).getMinValue()) ? String.valueOf(0) : ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(1).getStartValue()) {
                                    fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                    fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_red));
                                    fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                    fixedHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                    fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_blue));
                                } else if (ticketList.get(i).getSlotes().size() == 3) {
                                    if (Integer.parseInt(TextUtils.isEmpty(ticketList.get(i).getMinValue()) ? String.valueOf(0) : ticketList.get(i).getMinValue()) == ticketList.get(i).getSlotes().get(2).getStartValue()) {
                                        fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());
                                        fixedHolder.binding.tvMinus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_red));
                                        fixedHolder.binding.tvZero.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_yellow_radius));
                                        fixedHolder.binding.tvZero.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                        fixedHolder.binding.tvPlus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_radius));
                                        fixedHolder.binding.tvPlus.setTextColor(context.getResources().getColor(R.color.color_blue));
                                    }
                                }

                           /*     fixedHolder.tvMinus.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                                fixedHolder.tvZero.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                                if(ticketList.get(i).getSlotes().size() == 3)
                                    fixedHolder.tvZero.setTextColor(context.getResources().getColor(R.color.white));
                                fixedHolder.tvPlus.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));*/

                            }

                        }
                    }
                    fixedHolder.binding.tvLockTime.setText(String.valueOf("Locked at: " + ticketList.get(i).getLockTime()));
                    fixedHolder.binding.tvAnsSelection.setText(ticketList.get(i).getDisplayView());

                } else {
                    //fixedHolder.linearNotSelection.setVisibility(View.GONE);
                    fixedHolder.binding.linearSelection.setVisibility(View.GONE);
                    fixedHolder.binding.linearLock.setVisibility(View.GONE);
                    fixedHolder.binding.tvMinus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onItemClickListener.onItemClick(view, i);
                        }
                    });

                    fixedHolder.binding.tvZero.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onItemClickListener.onItemClick(view, i);
                        }
                    });

                    fixedHolder.binding.tvPlus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onItemClickListener.onItemClick(view, i);
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
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

    public void setGameType(String constest_type) {
        this.constest_type = constest_type;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnRangeListener(OnRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }

    public void setOnSlotListener(OnSlotListener onSlotListener) {
        this.onSlotListener = onSlotListener;
    }

    class FlexiHolder extends RecyclerView.ViewHolder {


        /*  @BindView(R.id.rangeSeekBar)
          CrystalRangeSeekbar rangeSeekBar;
          @BindView(R.id.rangeSeekBarTemp)
          CrystalRangeSeekbar rangeSeekBarTemp;
        */

        /*  @BindView(R.id.linearNotSelection)
          LinearLayout linearNotSelection;*/

        ItemTicketFlexibarBinding binding;

        public FlexiHolder(@NonNull ItemTicketFlexibarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.  tvLockNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, pos);
                    }
                }
            });

        }
    }

    class FixedHolder extends RecyclerView.ViewHolder {


        /*@BindView(R.id.linearNotSelection)
        LinearLayout linearNotSelection;*/

        OptionsAdapter optionsAdapter;
        ItemTicketFixedBinding binding;

        public FixedHolder(@NonNull ItemTicketFixedBinding binding) {
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
}
