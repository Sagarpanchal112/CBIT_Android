package com.tfb.cbit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListPassbookBinding;
import com.tfb.cbit.models.passbook.Content;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class PassbookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Content> passBookList;
    private boolean isMoreLoading = true;
    private OnLoadMoreListener onLoadMoreListener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public PassbookAdapter(Context context, List<Content> passBookList) {
        this.context = context;
        this.passBookList = new ArrayList<>();
        onLoadMoreListener = (OnLoadMoreListener) context;
    }

    public void addAllClass(List<Content> models) {
        passBookList.clear();
        passBookList.addAll(models);
    }

    @Override
    public int getItemViewType(int position) {
        return passBookList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == VIEW_ITEM) {
            ListPassbookBinding binding = ListPassbookBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
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
                if (passBookList.get(i).getType().equals(Utils.SUBSTRACT)) {
                    viewHolder.binding.ivArrow.setRotation(180);
                } else {
                    viewHolder.binding.ivArrow.setRotation(0);
                }

                viewHolder.binding.tvTransactionTitle.setText(passBookList.get(i).getTitle());
                viewHolder.binding.tvTransactionDate.setText(String.valueOf(passBookList.get(i).getDate()));
                viewHolder.binding.tvTransactionTime.setText(String.valueOf(passBookList.get(i).getTime()));

                String lastAmount = passBookList.get(i).getAmount().substring(passBookList.get(i).getAmount().length() - 2, passBookList.get(i).getAmount().length());
                String firstAmount = passBookList.get(i).getAmount().substring(0, passBookList.get(i).getAmount().length() - 2);
                viewHolder.binding.tvAmount.setText(Html.fromHtml(firstAmount + "<font color=#525252>" + lastAmount + "</font>"));


                if (passBookList.get(i).getType().equalsIgnoreCase("add")) {
                    viewHolder.binding.tvbeforebalance.setBackgroundColor(Color.parseColor("#6abe45"));
                    viewHolder.binding.tvbeforebalance.setTextColor(Color.parseColor("#ffffff"));
                    viewHolder.binding.tvbeforebalance.setText(passBookList.get(i).getBeforebalance());

                } else {
                    viewHolder.binding.tvbeforebalance.setBackgroundColor(Color.parseColor("#fb0102"));
                    viewHolder.binding.tvbeforebalance.setTextColor(Color.parseColor("#ffffff"));
                    viewHolder.binding.tvbeforebalance.setText(passBookList.get(i).getBeforebalance());
                }

                if (passBookList.get(i).getTds().equals("0")) {
                    viewHolder.binding.tvTDS.setVisibility(View.GONE);
                } else {
                    viewHolder.binding.tvTDS.setVisibility(View.VISIBLE);
                    viewHolder.binding.tvTDS.setText(String.valueOf("TDS: " + passBookList.get(i).getTds()));
                }
                if (passBookList.get(i).getRedeemFlag() == 1) {
                    viewHolder.binding.linApproved.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.binding.linApproved.setVisibility(View.GONE);

                }
                break;
            default:
                break;
        }

    }


    @Override
    public int getItemCount() {
        return passBookList.size();
    }

    class PackageHolder extends RecyclerView.ViewHolder {

        ListPassbookBinding binding;

        public PackageHolder(@NonNull ListPassbookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void addItemMore(List<Content> lst) {
        int sizeInit = passBookList.size();
        passBookList.addAll(lst);

        notifyItemRangeChanged(sizeInit, passBookList.size());
    }

    public void showLoading() {
        if (isMoreLoading && passBookList != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    passBookList.add(null);
                    notifyItemInserted(passBookList.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (passBookList != null && passBookList.size() > 0) {
            passBookList.remove(passBookList.size() - 1);
            notifyItemRemoved(passBookList.size());
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
