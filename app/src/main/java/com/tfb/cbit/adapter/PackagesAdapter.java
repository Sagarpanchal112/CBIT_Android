package com.tfb.cbit.adapter;

import android.content.Context;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListMyPackagesBinding;
import com.tfb.cbit.databinding.ListPackagesBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.PackageInterface;
import com.tfb.cbit.models.contest_pkg.Content;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class PackagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Content> packageList;
    private PackageInterface onItemClickListener;
    private boolean isMyPkg;
    public PackagesTicketAdapter subServicesAdapter;

    public PackagesAdapter(Context context, List<Content> packageList, boolean isMyPkg) {
        this.context = context;
        this.packageList = packageList;
        this.isMyPkg = isMyPkg;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        if (isMyPkg) {
            ListMyPackagesBinding binding = ListMyPackagesBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new MyPackageHolder(binding);
        } else {
            ListPackagesBinding binding = ListPackagesBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new PackageHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof PackageHolder) {
            PackageHolder packageHolder = (PackageHolder) viewHolder;
            //   packageHolder.tvRate.setText(String.valueOf(packageList.get(i).getCommission()+"% commission on winning amount"));
            packageHolder.binding.tvValidity.setText(packageList.get(i).getPackages().getValidity() + " days");
            packageHolder.binding.tvPackageNmae.setText(packageList.get(i).getPackages().getName());
            if (packageList.get(i).getPackages().isSelected()) {
                packageHolder.binding.chkSelect.setVisibility(View.VISIBLE);
                //flexiHolder.frmSelected.setVisibility(View.VISIBLE);
                // flexiHolder.frmSelected.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent_50));
                packageHolder.binding.chkSelect.setChecked(true);
            } else {
                packageHolder.binding.chkSelect.setVisibility(View.VISIBLE);
                packageHolder.binding.chkSelect.setChecked(false);
            }
            packageHolder.binding.chkSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view, i, false, -1);
                }
            });
            packageHolder.binding.rvTickets.setVisibility(View.VISIBLE);   // holder.txtStyListName.setTextColor();
            packageHolder.binding.rvTickets.setLayoutManager(new LinearLayoutManager(context));
            final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
            packageHolder.binding.rvTickets.setLayoutAnimation(controller);
            subServicesAdapter = new PackagesTicketAdapter(context, packageList.get(i).getContestPriceList(), packageList.get(i).getPackages().getValidity());
            packageHolder.binding.rvTickets.setAdapter(subServicesAdapter);

            subServicesAdapter.setOnItemClickListener(new PackagesTicketAdapter.OnPackagItemClickListener() {
                @Override
                public void onSubItemClick(View view, int position) {
                    onItemClickListener.onItemClick(view, i, true, position);
                }
            });
        } else {
            final MyPackageHolder myPackageHolder = (MyPackageHolder) viewHolder;
            myPackageHolder.binding.tvPackagesDetail.setText(Utils.getCurrencyFormat(String.valueOf(packageList.get(i).getTicketPrice())) + " @ " + Utils.getCurrencyFormat(String.valueOf(packageList.get(i).getAmount())) + ", " + packageList.get(i).getValidity() + "days");
            myPackageHolder.binding.tvExpDate.setText(Utils.getddMMyyyyhhmmaformat(packageList.get(i).getExpirationDate()));

            long mill = Utils.convertMillSeconds(packageList.get(i).getExpirationDate(), packageList.get(i).getCurrentDate());

            PrintLog.d("mill", "Mill Sec " + mill);

            if (myPackageHolder.expiryRemaining != null) {
                myPackageHolder.expiryRemaining.cancel();
            }


            myPackageHolder.expiryRemaining = new CountDownTimer(mill, 1000) {
                @Override
                public void onTick(long l) {
                    myPackageHolder.binding.tvRemaining.setText(
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
                    myPackageHolder.binding.tvRemaining.setText("00:00:00");
                }
            }.start();
        }
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public void setOnItemClickListener(PackageInterface onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class PackageHolder extends RecyclerView.ViewHolder {


      /*
        @BindView(R.id.btnBuy)
        Button btnBuy;
        @BindView(R.id.tvValidity)
        TextView tvValidity;*/

        ListPackagesBinding binding;
        public PackageHolder(@NonNull ListPackagesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

           /* btnBuy.setOnClickListener(new View.OnClickListener() {
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


    class MyPackageHolder extends RecyclerView.ViewHolder {


        CountDownTimer expiryRemaining;

        ListMyPackagesBinding binding;

        public MyPackageHolder(@NonNull ListMyPackagesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
