package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListHistoryBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.history.Content;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnLoadMoreListener onLoadMoreListener;

    public List<Content> historyList;

    private boolean isMoreLoading = true;

    private static final int VIEW_ITEM = 1;
    private static final int VIEW_PROG = 0;

    public HistoryAdapter(Context context) {
        this.context = context;
        this.historyList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_ITEM) {
            ListHistoryBinding binding = ListHistoryBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            );
            return new ViewHolder(binding);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progressbar, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == VIEW_ITEM) {

            ViewHolder viewHolder = (ViewHolder) holder;

            Content item = historyList.get(position);

            viewHolder.binding.tvContestName.setText(item.getName());
            viewHolder.binding.tvPlayTime.setText(item.getContestTime());
            viewHolder.binding.tvPlayDate.setText(item.getContestDate());
            viewHolder.binding.tvResultDate.setText(item.getGame_date());
            viewHolder.binding.tvResultTime.setText(item.getGame_time());

            if ("Anytime Game".equalsIgnoreCase(item.getGame())) {

                viewHolder.binding.tvGameType.setVisibility(View.VISIBLE);
                viewHolder.binding.tvGameLive.setVisibility(View.GONE);

                viewHolder.binding.tvGameType.setText(
                        "ATG Game No: " + item.getGame_no());

            } else {

                viewHolder.binding.tvGameLive.setVisibility(View.VISIBLE);
                viewHolder.binding.tvGameType.setVisibility(View.GONE);
            }

            if ("-".equals(item.getGame_time())) {

                viewHolder.binding.imgTag.setVisibility(View.GONE);

            } else {

                if ("0".equals(item.getIs_watch())) {
                    viewHolder.binding.imgTag.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.binding.imgTag.setVisibility(View.GONE);
                }
            }

            viewHolder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return historyList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ListHistoryBinding binding;

        public ViewHolder(ListHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void addAllClass(List<Content> list) {
        historyList.clear();
        historyList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItemMore(List<Content> list) {

        int start = historyList.size();

        historyList.addAll(list);

        notifyItemRangeInserted(start, list.size());
    }

    public void showLoading() {

        if (isMoreLoading && onLoadMoreListener != null) {

            isMoreLoading = false;

            new Handler().post(() -> {

                historyList.add(null);

                notifyItemInserted(historyList.size() - 1);

                onLoadMoreListener.onLoadMore();
            });
        }
    }

    public void dismissLoading() {

        if (historyList != null && historyList.size() > 0) {

            int index = historyList.size() - 1;

            if (historyList.get(index) == null) {

                historyList.remove(index);

                notifyItemRemoved(index);
            }
        }
    }

    public void setMore(boolean more) {
        isMoreLoading = more;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}