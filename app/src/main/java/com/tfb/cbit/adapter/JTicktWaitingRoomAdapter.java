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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemJWaitingRoomBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemOfferApproch;
import com.tfb.cbit.models.JWaitingRoom.Contest;
import com.tfb.cbit.models.MyJTicket.ApproachList;
import com.tfb.cbit.models.approch.UserContest;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class JTicktWaitingRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "JTicktWaitingRoomAdapte";
    private Context context;
    public List<Contest> packageList;
    private List<UserContest> userContestList = new ArrayList<>();
    private OnItemOfferApproch onItemClickListener;
    private boolean isMoreLoading = true;
    private OnLoadMoreListener onLoadMoreListener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private SessionUtil sessionUtil;

    public JTicktWaitingRoomAdapter(Context context) {
        this.context = context;
        onLoadMoreListener = (OnLoadMoreListener) context;
        packageList = new ArrayList<>();
        onItemClickListener = (OnItemOfferApproch) context;
        sessionUtil = new SessionUtil(context);
    }

    @Override
    public int getItemViewType(int position) {
        return packageList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == VIEW_ITEM) {
            ItemJWaitingRoomBinding binding = ItemJWaitingRoomBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new PackageHolder(binding);
        } else {
            return new ProgressViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar, viewGroup, false));
        }


    }

    public int posApprochList;

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case 1:
                PackageHolder packageHolder = (PackageHolder) viewHolder;
              /*  if (packageList.get(i).getApproachList().size() > 0) {
                    if (sessionUtil.getId().equals(packageList.get(i).getUser_id())) {
                        packageHolder.lin_approch.setVisibility(View.GONE);
                        packageHolder.lin_offer.setVisibility(View.GONE);
                        packageHolder.lin_eo.setVisibility(View.VISIBLE);
                        packageHolder.tvEo.setText("Exchange\nOffers : " + packageList.get(i).getApproachList().size());
                    } else {
                        for (int j = 0; j < packageList.get(i).getApproachList().size(); j++) {
                            if (sessionUtil.getId().equals(String.valueOf(packageList.get(i).getApproachList().get(j).getUser_to()))) {
                                packageHolder.lin_approch.setVisibility(View.VISIBLE);
                                packageHolder.lin_eo.setVisibility(View.GONE);
                                packageHolder.lin_offer.setVisibility(View.GONE);
                                if (packageList.get(i).getApproachList().get(j).getAccept() == 1) {
                                    packageHolder.lin_approch.setVisibility(View.GONE);
                                    packageHolder.lin_eo.setVisibility(View.GONE);
                                    packageHolder.lin_offer.setVisibility(View.VISIBLE);
                                } else if (packageList.get(i).getApproachList().get(j).getNegotiate() == 0) {
                                    packageHolder.tv_negotite.setText("Approched");
                                } else {
                                    packageHolder.tv_negotite.setText(packageList.get(i).getApproachList().get(j).getNegotiate() + " %");
                                    posApprochList = j;
                                }
                            } else {
                                packageHolder.lin_eo.setVisibility(View.GONE);
                                packageHolder.lin_approch.setVisibility(View.GONE);
                                packageHolder.lin_offer.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else if (sessionUtil.getId().equals(packageList.get(i).getUser_id())) {
                    packageHolder.lin_approch.setVisibility(View.GONE);
                    packageHolder.lin_offer.setVisibility(View.GONE);
                    packageHolder.lin_eo.setVisibility(View.VISIBLE);
                    packageHolder.tvEo.setText("Exchange\nOffers : " + packageList.get(i).getApproachList().size());
                } else {
                    packageHolder.lin_approch.setVisibility(View.GONE);
                    packageHolder.lin_offer.setVisibility(View.VISIBLE);
                    packageHolder.lin_eo.setVisibility(View.GONE);

                }


                if (packageList.get(i).getIsApproach().equalsIgnoreCase("Done")) {
                    packageHolder.lin_approch.setVisibility(View.VISIBLE);
                    packageHolder.tv_negotite.setText("Approched");
                    packageHolder.lin_eo.setVisibility(View.GONE);
                    packageHolder.lin_offer.setVisibility(View.GONE);
                }
                if (packageList.get(i).getUp().equals("1")) {
                    packageHolder.tvUp.setTextColor(context.getColor(R.color.color_red));
                    packageHolder.tvUp.setText(Utils.INDIAN_RUPEES + packageList.get(i).getApproachCashback() + "(" + packageList.get(i).getApproachCashbackPer() + "%)");
                } else if (packageList.get(i).getDown().equals("1")) {
                    packageHolder.tvUp.setTextColor(context.getColor(R.color.color_green));
                    packageHolder.tvUp.setText(Utils.INDIAN_RUPEES + packageList.get(i).getApproachCashback() + "(" + packageList.get(i).getApproachCashbackPer() + "%)");

                } else {
                    packageHolder.tvUp.setVisibility(View.GONE);
                }*/
                packageHolder.binding.tvRate.setText("CWN : " + (i + 1) + "");
                packageHolder.binding.tvUsername.setText(packageList.get(i).getUserName());
                packageHolder.binding.tvTicketNo.setText(packageList.get(i).getTicket_number());
                packageHolder.binding.tvInrCash.setText("Cashback up-to: " + Utils.INDIAN_RUPEES + packageList.get(i).getCashbackUpto());
                packageHolder.binding.tvDate.setText("Applied on " + Utils.convertNodeFormat(packageList.get(i).getApplyDate()));
                //   packageHolder.tvInrCash.setText("Cashback up-to: " + Utils.INDIAN_RUPEES + " 400");
                packageHolder.binding.tvE.setVisibility(View.GONE);  // number is odd
                packageHolder.binding.linOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openUserTicketDailog(i);
                    }
                });
                packageHolder.binding.linEo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (packageList.get(i).getApproachList().size() > 0) {
                            onApprochListClickListener.onItemClick(packageList.get(i).getApproachList());
                        } else {
                            Utils.showToast(context, "Not any exchange offer.");

                        }
                    }
                });
                packageHolder.binding.linApproch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  openUserTicketDailog(i);
                        if (packageHolder.binding.tvNegotite.getText().toString().equals("Approched")) {
                            Utils.showToast(context, "You have already approched.");
                        } else {
                            onItemClickListener.onItemClick(posApprochList, i, packageList.get(i).getApproachList().get(posApprochList).getJ_ticket_user_approach_id(), "Approch");

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
        return packageList.size();
    }

    public interface OnApprochListClickListener {
        public void onItemClick(List<ApproachList> approachList);

    }

    private OnApprochListClickListener onApprochListClickListener;

    public void setOnItemApprochListClickListener(OnApprochListClickListener onItemClickListener) {
        this.onApprochListClickListener = onItemClickListener;
    }

    public void setOnItemClickListener(OnItemOfferApproch onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class PackageHolder extends RecyclerView.ViewHolder {
        ItemJWaitingRoomBinding binding;

        public PackageHolder(@NonNull ItemJWaitingRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public void addClass(Contest model) {
        packageList.add(model);
    }

    public void addAllClass(List<Contest> models) {
        packageList.clear();
        packageList.addAll(models);
    }

    public void addUserWating(List<UserContest> models) {
        userContestList.clear();
        userContestList.addAll(models);
    }

    public void addItemMore(List<Contest> lst) {
        int sizeInit = packageList.size();
        packageList.addAll(lst);
        notifyItemRangeChanged(sizeInit, packageList.size());
    }

    public void showLoading() {
        if (isMoreLoading && packageList != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    packageList.add(null);
                    notifyItemInserted(packageList.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (packageList != null && packageList.size() > 0) {
            packageList.remove(packageList.size() - 1);
            notifyItemRemoved(packageList.size());
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private void openUserTicketDailog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_approch_list);
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            lp.width = (int) (metrics.widthPixels * 0.90);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
        ImageView img_close = dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        RecyclerView rvUserList = dialog.findViewById(R.id.rvUserList);
        UserTicketsAdapter itemListDataAdapter =
                new UserTicketsAdapter(context, userContestList);
        rvUserList.setHasFixedSize(true);
        rvUserList.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        rvUserList.setAdapter(itemListDataAdapter);

        rvUserList.setNestedScrollingEnabled(false);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnApproach = dialog.findViewById(R.id.btnApproach);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnApproach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onItemClickListener.onItemClick(posApprochList, position, 1, itemListDataAdapter.getSelected().getId());
            }
        });
        dialog.show();
    }

}
