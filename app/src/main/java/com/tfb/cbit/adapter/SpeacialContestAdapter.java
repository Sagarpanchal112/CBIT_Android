package com.tfb.cbit.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemSpecialContestBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.gamelist.Contest;
import com.tfb.cbit.utility.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class SpeacialContestAdapter extends RecyclerView.Adapter<SpeacialContestAdapter.ViewHolder> {
    private static final String TAG = "GameContestAdapter";
    private Context context;
    private List<Contest> contest_list;
    private String currentTime = "";
    private OnItemClickListener onItemClickListener;
    private Date date;
    private SimpleDateFormat simpleDateFormat;

    public SpeacialContestAdapter(Context context, List<Contest> contest_list) {
        this.context = context;
        this.contest_list = contest_list;
        date = new Date();
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemSpecialContestBinding binding = ItemSpecialContestBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
         return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.binding.ivGameType.setVisibility(View.GONE);
        viewHolder.binding.tvContestName.setText(contest_list.get(i).getName());
        viewHolder.binding.tvStartDate.setText("Date : " + Utils.getddMMyyyyformat(contest_list.get(i).getStartDate()));
        viewHolder.binding.tvGameTime.setText(Utils.getHHMM(contest_list.get(i).getStartDate()));
        if(contest_list.get(i).getGame_type().equalsIgnoreCase("spinning-machine")){
            viewHolder.binding.ivGameLevel.setImageResource(R.drawable.slot_machine);
        }else{
            viewHolder.binding.ivGameLevel.setImageResource(R.drawable.classic_grid);

        }  /*switch (contest_list.get(i).getLevel()) {
            case Utils.EASY:
                viewHolder.ivGameLevel.setImageResource(R.drawable.ic_easy);
                break;
            case Utils.MODERATE:
                viewHolder.ivGameLevel.setImageResource(R.drawable.ic_medium);
                break;
            case Utils.PRO:
                viewHolder.ivGameLevel.setImageResource(R.drawable.ic_pro);
                break;
        }
*/
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
              /*  viewHolder.tvRemainigTime.setText(
                        String.valueOf(
                                String.format("%02d",(TimeUnit.MILLISECONDS.toHours(l) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(l))))+":"+
                                        String.format("%02d",(TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l))))+":"+
                                        String.format("%02d",(TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))))
                        )
                );*/
                viewHolder.binding.tvRemainigTime.setText(String.format("%02d : %02d : %02d",
                        TimeUnit.MILLISECONDS.toHours(l),
                        TimeUnit.MILLISECONDS.toMinutes(l) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));
            }

            @Override
            public void onFinish() {
                viewHolder.binding.tvRemainigTime.setText("00 : 00 : 00");
            }
        }.start();


        viewHolder.entryClosing = new CountDownTimer(cmill, 1000) {
            @Override
            public void onTick(long l) {
                viewHolder.binding.tvEntryClosingTime.setText(
                        String.format("%02d : %02d : %02d",
                                TimeUnit.MILLISECONDS.toHours(l),
                                TimeUnit.MILLISECONDS.toMinutes(l) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)), // The change is in this line
                                TimeUnit.MILLISECONDS.toSeconds(l) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)))
                );
            }

            @Override
            public void onFinish() {
                viewHolder.binding.tvEntryClosingTime.setText("00 : 00 : 00");
                onItemClickListener.onItemClick(viewHolder.binding.tvEntryClosingTime, i);
            }
        }.start();

    }

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

        /* @BindView(R.id.linearContent)
         LinearLayout linearContent;*/
        CountDownTimer startGameRemaining, entryClosing;
        ItemSpecialContestBinding binding;
        public ViewHolder(@NonNull ItemSpecialContestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.btnPayNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Log.d(TAG, "Range: " + contest_list.get(pos).getAnsRangeMin() + ">>>>" + contest_list.get(pos).getAnsRangeMax());
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, pos);
                    }
                }
            });

          /*  tvPayNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view,getAdapterPosition());
                }
            });*/
        }
    }
}
