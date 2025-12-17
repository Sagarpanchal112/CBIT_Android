package com.tfb.cbit.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemGameContestBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.gamelist.Contest;
import com.tfb.cbit.utility.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class GameContestAdapter extends RecyclerView.Adapter<GameContestAdapter.ViewHolder> {
    private static final String TAG = "GameContestAdapter";
    private Context context;
    private List<Contest> contest_list;
    private String currentTime = "";
    private OnItemClickListener onItemClickListener;
    private Date date;
    private SimpleDateFormat simpleDateFormat;
    private boolean isMoreLoading = true;
    private OnLoadMoreListener onLoadMoreListener;

    public GameContestAdapter(Fragment context, List<Contest> contest_list) {
        this.context = context.getActivity();
        this.contest_list = contest_list;
        date = new Date();
        onLoadMoreListener = (OnLoadMoreListener) context;
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    }
    public void showLoading() {
        if (isMoreLoading && contest_list != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    contest_list.add(null);
                    notifyItemInserted(contest_list.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }
    public void addAllClass(List<Contest> models) {
        contest_list.addAll(models);
    }
    public void dismissLoading() {
        if (contest_list != null && contest_list.size() > 0) {
            contest_list.remove(contest_list.size() - 1);
            notifyItemRemoved(contest_list.size());
        }
    }
    public void addItemMore(List<Contest> lst) {
        int sizeInit = contest_list.size();
        contest_list.addAll(lst);
        notifyItemRangeChanged(sizeInit, contest_list.size());
    }
    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemGameContestBinding binding = ItemGameContestBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.binding.ivGameType.setVisibility(View.GONE);
        viewHolder.binding.tvContestName.setText(contest_list.get(i).getName());
        viewHolder.binding.tvStartDate.setText("Date : " + Utils.getddMMyyyyformat(contest_list.get(i).getStartDate()));
        viewHolder.binding.tvGameTime.setText(Utils.getHHMM(contest_list.get(i).getStartDate()));
   /*     switch (contest_list.get(i).getLevel()) {
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
        if(contest_list.get(i).getGame_type().equalsIgnoreCase("spinning-machine")){
            viewHolder.binding.ivGameLevel.setImageResource(R.drawable.whatsmost);

        }else{
            viewHolder.binding.ivGameLevel.setImageResource(R.drawable.classic_grid);

        }
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

        ItemGameContestBinding binding;

        public ViewHolder(@NonNull ItemGameContestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding. btnPayNow.setOnClickListener(new View.OnClickListener() {
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
    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
