package com.tfb.cbit.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

//import com.github.florent37.tutoshowcase.TutoShowcase;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemMyContestBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.gamelist.Contest;
import com.tfb.cbit.utility.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class MyContestAdapter extends RecyclerView.Adapter<MyContestAdapter.ViewHolder> {
    private Context context;
    private List<Contest> contest_list;
    private String currentTime = "";
    private OnItemClickListener onItemClickListener;
    private long mLastClickTime = 0;

    public MyContestAdapter(Fragment context, List<Contest> contest_list) {
        this.context = context.getActivity();
        this.contest_list = contest_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemMyContestBinding binding = ItemMyContestBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.binding.btnEnter.setVisibility(View.GONE);
        viewHolder.binding.btnMyTickets.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_colorprimary_radius_left_side));
        viewHolder.binding.tvContestName.setText(contest_list.get(i).getName());
        viewHolder.binding.tvStartDate.setText("Date : " + Utils.getddMMyyyyformat(contest_list.get(i).getStartDate()));
        viewHolder.binding.tvGameTime.setText(Utils.getHHMM(contest_list.get(i).getStartDate()));
        if (contest_list.get(i).getGame_type().equalsIgnoreCase("spinning-machine")) {
            viewHolder.binding.ivGameLevel.setImageResource(R.drawable.slot_machine);

        } else {
            viewHolder.binding.ivGameLevel.setImageResource(R.drawable.classic_grid);

        }
      /*  switch (contest_list.get(i).getLevel()) {
            case Utils.EASY:
                viewHolder.ivGameLevel.setImageResource(R.drawable.ic_easy);
                break;
            case Utils.MODERATE:
                viewHolder.ivGameLevel.setImageResource(R.drawable.ic_medium);
                break;
            case Utils.PRO:
                viewHolder.ivGameLevel.setImageResource(R.drawable.ic_pro);
                break;
        }*/

        switch (contest_list.get(i).getType()) {
            case Utils.FLEXIBAR:
                viewHolder.binding.ivGameType.setImageResource(R.drawable.flexi);
                break;
            case Utils.FIXEDSLOT:
                viewHolder.binding.ivGameType.setImageResource(R.drawable.fix);
                break;
        }

        long mill = Utils.convertMillSeconds(contest_list.get(i).getStartDate(), currentTime);
        long cmill = Utils.convertMillSeconds(contest_list.get(i).getCloseDate(), currentTime);
        if (viewHolder.startGameRemaining != null) {
            viewHolder.startGameRemaining.cancel();
        }

        if (viewHolder.entryClosing != null) {
            viewHolder.entryClosing.cancel();
        }


        viewHolder.startGameRemaining = new CountDownTimer(mill, 1000) {
            @Override
            public void onTick(long l) {
                viewHolder.binding.tvRemainigTime.setText(
                        String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(l),
                                TimeUnit.MILLISECONDS.toMinutes(l) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)), // The change is in this line
                                TimeUnit.MILLISECONDS.toSeconds(l) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                );
            }

            @Override
            public void onFinish() {
                viewHolder.binding.tvRemainigTime.setText("00:00:00");
            }
        }.start();


        viewHolder.entryClosing = new CountDownTimer(cmill, 1000) {
            @Override
            public void onTick(long l) {
                viewHolder.binding.tvEntryClosingTime.setText(
                        String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(l),
                                TimeUnit.MILLISECONDS.toMinutes(l) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)), // The change is in this line
                                TimeUnit.MILLISECONDS.toSeconds(l) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                );
            }

            @Override
            public void onFinish() {
                viewHolder.binding.tvEntryClosingTime.setText("00:00:00");
               /* try {
                    contest_list.remove(i);
                    notifyItemRemoved(i);
                }catch (Exception e){
                    e.printStackTrace();
                }*/
                viewHolder.binding.btnEnter.setVisibility(View.VISIBLE);
                //   showTuto();
                viewHolder.binding.btnMyTickets.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_colorprimary_radius_left_side));
            }
        }.start();
    }

//    private void showTuto() {
//        TutoShowcase.from((Activity) context)
//                .setListener(new TutoShowcase.Listener() {
//                    @Override
//                    public void onDismissed() {
//                        Toast.makeText(context, "Tutorial dismissed", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setContentView(R.layout.tuto_enter_game)
//                .setFitsSystemWindows(true)
//                .on(R.id.tvPayTitle)
//                .addCircle()
//                .withBorder()
//                .onClick(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                })
//                .show();
//    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return contest_list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CountDownTimer startGameRemaining, entryClosing;
        ItemMyContestBinding binding;

        public ViewHolder(@NonNull ItemMyContestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.btnMyTickets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, pos);
                    }
                }
            });

            binding.btnEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, pos);
                    }
                }
            });

        }
    }
}
