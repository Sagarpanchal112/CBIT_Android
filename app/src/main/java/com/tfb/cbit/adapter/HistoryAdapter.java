package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListHistoryBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.history.Content;

import java.util.ArrayList;
import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private OnItemClickListener onItemClickListener;
    public List<Content> historyList;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isMoreLoading = true;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;


    public HistoryAdapter(Context context) {
        this.context = context;
        this.historyList = new ArrayList<>();
        onLoadMoreListener = (OnLoadMoreListener)context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == VIEW_ITEM) {
            ListHistoryBinding binding = ListHistoryBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new ViewHolder(binding);
        } else {
            return new ProgressViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar, viewGroup, false));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 1:
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.binding.tvContestName.setText(historyList.get(position).getName());
                viewHolder.binding.tvPlayTime.setText(historyList.get(position).getContestTime());
                viewHolder.binding.tvPlayDate.setText(historyList.get(position).getContestDate());
                viewHolder.binding.tvResultDate.setText(historyList.get(position).getGame_date());
                viewHolder.binding.tvResultTime.setText(historyList.get(position).getGame_time());
                if(historyList.get(position).getGame().equalsIgnoreCase("Anytime Game")){
                    viewHolder.binding.tvGameType.setText("ATG Game No:"+historyList.get(position).getGame_no());
                    viewHolder.binding.tvGameLive.setVisibility(View.GONE);
                }else{
                    viewHolder.binding.tvGameLive.setVisibility(View.VISIBLE);
                    viewHolder.binding.tvGameType.setVisibility(View.GONE);
                   //  viewHolder.tvGameType.setText(historyList.get(position).getGame_type());

                }
                if(historyList.get(position).getGame_time().equals("-")){
                    viewHolder.binding.imgTag.setVisibility(View.GONE);

                }else{
                    if(historyList.get(position).getIs_watch().equals("0")){
                        viewHolder.binding.imgTag.setVisibility(View.VISIBLE);
                    }else{
                        viewHolder.binding.imgTag.setVisibility(View.GONE);

                    }
                }

               viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                           onItemClickListener.onItemClick(view, position);
                    }
                });
                 break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return historyList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        ListHistoryBinding binding;
        public ViewHolder(@NonNull ListHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public void addAllClass(List<Content> models) {
        historyList.clear();
        historyList.addAll(models);
    }

    public void addItemMore(List<Content> lst) {
        int sizeInit = historyList.size();
        historyList.addAll(lst);
        notifyItemRangeChanged(sizeInit, historyList.size());
    }

    public void showLoading() {
        if (isMoreLoading && historyList != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    historyList.add(null);
                    notifyItemInserted(historyList.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (historyList != null && historyList.size() > 0) {
            historyList.remove(historyList.size() - 1);
            notifyItemRemoved(historyList.size());
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
