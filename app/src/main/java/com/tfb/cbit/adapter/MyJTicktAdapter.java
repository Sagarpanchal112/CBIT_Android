package com.tfb.cbit.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.animation.AnimationFactory;
import com.tfb.cbit.databinding.ItemMyJtcktNewBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickJTicket;
import com.tfb.cbit.models.MyJTicket.ApproachList;
import com.tfb.cbit.models.MyJTicket.Contest;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;


public class MyJTicktAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<Contest> myjTcktlist= new ArrayList<>();
    private OnItemClickJTicket onItemClickJTicket;
    private OnApprochItemClickListener onApprochItemClickListener;
    private OnApprochListClickListener onApprochListClickListener;
    RequestOptions requestOptions;
    public SuperRecyclerView recyclerView;
    private boolean isFlipped = false;
    public int lastPosition = 0;
    private boolean isMoreLoading = true;
    private OnLoadMoreListener onLoadMoreListener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    public Fragment fragment;

    public MyJTicktAdapter(Context context, Fragment fragment, SuperRecyclerView recyclerView) {
        this.context = context;
        this.fragment = fragment;
        onLoadMoreListener = (OnLoadMoreListener) fragment;
        myjTcktlist = new ArrayList<>();
        requestOptions = new RequestOptions();
        this.recyclerView = recyclerView;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == VIEW_ITEM) {
            ItemMyJtcktNewBinding binding = ItemMyJtcktNewBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
             return new PackageHolder(binding);
        } else {
            return new ProgressViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar, viewGroup, false));
        }

    }

    public interface OnApprochItemClickListener {
        public void onItemClick(int position, String value);

    }
    public interface OnApprochListClickListener {
        public void onItemClick(List<ApproachList> approachList);

    }
    public void setOnItemApprochClickListener(OnApprochItemClickListener onItemClickListener) {
        this.onApprochItemClickListener = onItemClickListener;
    }
    public void setOnItemApprochListClickListener(OnApprochListClickListener onItemClickListener) {
        this.onApprochListClickListener = onItemClickListener;
    }
    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //   holder.itemView.clearAnimation();
    }

    @Override
    public int getItemViewType(int position) {
        return myjTcktlist.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        switch (viewHolder.getItemViewType()) {
            case 1:

                PackageHolder packageHolder = (PackageHolder) viewHolder;
                packageHolder.binding.tvCcprice.setText("Redeemed @ " + myjTcktlist.get(i).getPrice()+" Points");
                packageHolder.binding.tvJTicketName.setText(myjTcktlist.get(i).getName());
                packageHolder.binding.tvDate.setText(Utils.getOnlyDate(myjTcktlist.get(i).getApplyDate()));
                packageHolder.binding.tvJtcktNo.setText(myjTcktlist.get(i).getTicket_number());

                Glide.with(context)
                        .load(myjTcktlist.get(i).getImage())
                        .apply(requestOptions)
                        .into(packageHolder.binding.ivJTicket);
                if (myjTcktlist.get(i).getStatus().equalsIgnoreCase("0")) {
                    packageHolder.binding.imgHit.setVisibility(View.GONE);
                    packageHolder.binding.tvDateHit.setVisibility(View.GONE);
                    packageHolder.binding.btnApplyNow.setText("APPLY NOW");
                    packageHolder.binding.btnApplyNow.setClickable(true);
                    packageHolder.binding.tvFlipApplied.setText((myjTcktlist.get(i).getApplyDate()));
                    packageHolder.binding.tvFlipRedeem.setText((myjTcktlist.get(i).getCreated_at()));
                    packageHolder.binding.tvFlipHit.setText((myjTcktlist.get(i).getHitDate()));
                    packageHolder.binding.tvFlipJTicket.setText(myjTcktlist.get(i).getTicket_number());
                    packageHolder.binding.linApplied
                            .setVisibility(View.GONE);
                    packageHolder.binding.btnApplyNow.setEnabled(true);
                    packageHolder.binding.btnApplyNow.setBackgroundColor(Color.parseColor("#1a505d"));

                } else if (myjTcktlist.get(i).getStatus().equalsIgnoreCase("1")) {
                    packageHolder.binding.imgHit.setVisibility(View.GONE);
                    packageHolder.binding.tvDateHit.setVisibility(View.GONE);
                    packageHolder.binding.linApplied.setVisibility(View.VISIBLE);
                    packageHolder.binding.btnApplyNow.setVisibility(View.GONE);
                    packageHolder.binding.tvCurrentWaiting.setVisibility(View.GONE);
                    packageHolder.binding.btnEO.setVisibility(View.GONE);
                    packageHolder.binding.tvCashback.setVisibility(View.GONE);
                    packageHolder.binding.btnApplyNow.setVisibility(View.VISIBLE);
                    packageHolder.binding.btnApplyNow.setText("Current Waiting \nNo : " + myjTcktlist.get(i).getWaiting());
                    packageHolder.binding.tvCurrentWaiting.setText("Current Waiting \nNo : " + myjTcktlist.get(i).getWaiting());
                    packageHolder.binding.tvCashback.setText("Cashback Upto \nNo : " + myjTcktlist.get(i).getWinningAmount());
                    packageHolder.binding.btnEO.setText("Exchange\nOffers : " + myjTcktlist.get(i).getApproachList().size());
                    packageHolder.binding.btnApplyNow.setClickable(false);
                    packageHolder.binding.tvFlipApplied.setText((myjTcktlist.get(i).getApplyDate()));
                    packageHolder.binding.tvFlipRedeem.setText((myjTcktlist.get(i).getCreated_at()));
                    packageHolder.binding.tvFlipHit.setText((myjTcktlist.get(i).getHitDate()));
                    packageHolder.binding.tvFlipJTicket.setText(myjTcktlist.get(i).getTicket_number());
                    packageHolder.binding.btnApplyNow.setEnabled(false);
                    packageHolder.binding.btnApplyNow.setBackgroundColor(Color.parseColor("#f2b502"));
                } else if (myjTcktlist.get(i).getStatus().equalsIgnoreCase("2")) {
                    packageHolder.binding.imgHit.setVisibility(View.VISIBLE);
                    packageHolder.binding.tvDateHit.setText((Utils.getDateTime(myjTcktlist.get(i).getHitDate())));
                    packageHolder.binding.linApplied.setVisibility(View.GONE);
                    packageHolder.binding.tvFlipApplied.setText((myjTcktlist.get(i).getApplyDate()));
                    packageHolder.binding.tvFlipRedeem.setText((myjTcktlist.get(i).getCreated_at()));
                    packageHolder.binding.tvFlipHit.setText((myjTcktlist.get(i).getHitDate()));
                    packageHolder.binding.tvFlipJTicket.setText(myjTcktlist.get(i).getTicket_number());
                    packageHolder.binding.tvDateHit.setVisibility(View.VISIBLE);
                    packageHolder.binding.btnApplyNow.setText("Cashback Received " + Utils.INDIAN_RUPEES + " " + myjTcktlist.get(i).getWinningAmount());
                    packageHolder.binding.btnApplyNow.setClickable(false);
                    packageHolder.binding.btnApplyNow.setEnabled(false);
                    packageHolder.binding.btnApplyNow.setBackgroundColor(Color.parseColor("#26b34b"));
                }
                packageHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                packageHolder.binding.rlFront.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isFlipped) {
                            foldAllFlipper();
                        }
                        isFlipped = true;
                        flipViewFlipper(packageHolder.binding.myEasyFlipView);
                    }
                });

                packageHolder.binding.rlBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isFlipped = false;
                        flipViewFlipper(packageHolder.binding.myEasyFlipView);
                    }
                });


                packageHolder.binding.btnApplyNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickJTicket.onItemClick("", myjTcktlist.get(i).getId(), "", 0);
                    }
                });

                packageHolder.binding.btnEO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(myjTcktlist.get(i).getApproachList().size()>0){
                            onApprochListClickListener.onItemClick(myjTcktlist.get(i).getApproachList());
                        }else{
                            Utils.showToast(context, "This jticket have not any offer");
                        }
                      }
                });
                break;
            default:
                break;
        }
    }


    @Override
    public int getItemCount() {
        return myjTcktlist.size();
    }

    public void setOnItemClickListener(OnItemClickJTicket onItemClickListener) {
        this.onItemClickJTicket = onItemClickListener;
    }

    class PackageHolder extends RecyclerView.ViewHolder {



/*        @BindView(R.id.ivJTicket)
        ImageView ivJTicket;
        @BindView(R.id.tvWaiting_no)
        TextView tvWaitingNo;
        @BindView(R.id.tvTckt_no)
        TextView tvTcktNo;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvCount)
        TextView tvCount;
        @BindView(R.id.btnApplyNow)
        Button btnApplyNow;*/
        ItemMyJtcktNewBinding binding;

        public PackageHolder(@NonNull ItemMyJtcktNewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

       /*     btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, pos);
                    }
                }
            });*/
        }
    }

    public void foldAllFlipper() {

        for (int i = 0; i < getItemCount(); i++) {
            try {

                PackageHolder v = (PackageHolder) recyclerView.getRecyclerView().findViewHolderForAdapterPosition(i);

                if (v != null) {
                    if (v.binding.myEasyFlipView.getDisplayedChild() != 0) {
                        AnimationFactory.flipTransition(v.binding.myEasyFlipView, AnimationFactory.FlipDirection.RIGHT_LEFT);
                        v.binding.myEasyFlipView.setDisplayedChild(0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void flipViewFlipper(ViewFlipper flipper) {

        if (flipper.getDisplayedChild() == 0) {
            AnimationFactory.flipTransition(flipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
            flipper.setDisplayedChild(1);
        } else {
            AnimationFactory.flipTransition(flipper, AnimationFactory.FlipDirection.RIGHT_LEFT);
            flipper.setDisplayedChild(0);
        }

    }

    public void addAllClass(List<Contest> models) {
        myjTcktlist.clear();
        myjTcktlist.addAll(models);
    }

    public void addItemMore(List<Contest> lst) {
        int sizeInit = myjTcktlist.size();
        myjTcktlist.addAll(lst);

        notifyItemRangeChanged(sizeInit, myjTcktlist.size());
    }

    public void showLoading() {
        if (isMoreLoading && myjTcktlist != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    myjTcktlist.add(null);
                    notifyItemInserted(myjTcktlist.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (myjTcktlist != null && myjTcktlist.size() > 0) {
            myjTcktlist.remove(myjTcktlist.size() - 1);
            notifyItemRemoved(myjTcktlist.size());
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
