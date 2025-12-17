package com.tfb.cbit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListWinnerBinding;
import com.tfb.cbit.models.contestwinner.Content;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class WinnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public List<Content> winnerList;
    private OnLoadMoreListener onLoadMoreListener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean isMoreLoading = true;

    String SDCardPath = Environment.getExternalStorageDirectory() + "/.cbit/";

    public WinnerAdapter(Context context, List<Content> winnerList) {
        this.context = context;
        this.winnerList = new ArrayList<>();
        onLoadMoreListener = (OnLoadMoreListener) context;
    }

    @Override
    public int getItemViewType(int position) {
        return winnerList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == VIEW_ITEM) {
            ListWinnerBinding binding = ListWinnerBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
           return new PackageHolder(binding);
        } else {
            return new ProgressViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar, viewGroup, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        switch (holder.getItemViewType()) {
            case 1:
                PackageHolder viewHolder = (PackageHolder) holder;
                if (winnerList.get(i).getWinStatus().equalsIgnoreCase("1")) {
                    viewHolder.binding.tvName.setTextColor(Color.parseColor("#6abe45"));
                } else {
                    viewHolder.binding.tvName.setTextColor(Color.parseColor("#ffffff"));
                }
                viewHolder.binding.tvName.setText(winnerList.get(i).getName());

                viewHolder.binding.tvdisplayvalue.setText(winnerList.get(i).getDisplayValue());
                viewHolder.binding.tvLockTime.setText(winnerList.get(i).getLockTime());
                Glide.with(context)
                        .load(winnerList.get(i).getReferral_image())
                        .apply(Utils.getUserAvatarReques())
                        .into(viewHolder.binding.ivProfilePic);

                if (winnerList.get(i).getWinStatus().equals("1")) {
                    viewHolder.binding.imgRight.setVisibility(View.VISIBLE);
                    viewHolder.binding.imgRemove.setVisibility(View.GONE);
                    viewHolder.binding.imgRightWrong.setVisibility(View.GONE);
                } else if (winnerList.get(i).getWinStatus().equals("2")) {
                    viewHolder.binding.imgRight.setVisibility(View.GONE);
                    viewHolder.binding.imgRemove.setVisibility(View.GONE);
                    viewHolder.binding.imgRightWrong.setVisibility(View.VISIBLE);
                } else if (winnerList.get(i).getWinStatus().equals("3")) {
                    viewHolder.binding.imgRight.setVisibility(View.GONE);
                    viewHolder.binding.imgRemove.setVisibility(View.VISIBLE);
                    viewHolder.binding.imgRightWrong.setVisibility(View.GONE);

                } else if (winnerList.get(i).getWinStatus().equals("0")) {
                    viewHolder.binding.imgRight.setVisibility(View.GONE);
                    viewHolder.binding.imgRemove.setVisibility(View.VISIBLE);
                    viewHolder.binding.imgRightWrong.setVisibility(View.GONE);

                }

                if (winnerList.get(i).getImage().equalsIgnoreCase("-")) {
                    viewHolder.binding.tvdisplayvalue.setVisibility(View.VISIBLE);
                    viewHolder.binding.imgDisplayvalue.setVisibility(View.GONE);
                } else {
                    viewHolder.binding.imgDisplayvalue.setVisibility(View.VISIBLE);
                    viewHolder.binding.tvdisplayvalue.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(context.getFilesDir().getAbsolutePath() + "/" + winnerList.get(i).getImage())
                            .apply(Utils.getUserAvatarReques())
                            .into(viewHolder.binding.imgDisplayvalue);
                }


                break;
            default:
                break;
        }
    }

    public void addAllClass(List<Content> models) {
        winnerList.clear();
        winnerList.addAll(models);
    }


    public void addItemMore(List<Content> lst) {
        int sizeInit = winnerList.size();
        winnerList.addAll(lst);
        Log.i("TAG", "winnerList +" + winnerList.size());
        notifyItemRangeChanged(sizeInit, winnerList.size());
    }

    public void showLoading() {
        if (isMoreLoading && winnerList != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    winnerList.add(null);
                    notifyItemInserted(winnerList.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (winnerList != null && winnerList.size() > 0) {
            winnerList.remove(winnerList.size() - 1);
            notifyItemRemoved(winnerList.size());
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    @Override
    public int getItemCount() {
        return winnerList.size();
    }

    public class PackageHolder extends RecyclerView.ViewHolder {

        ListWinnerBinding binding;

        public PackageHolder(@NonNull ListWinnerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
