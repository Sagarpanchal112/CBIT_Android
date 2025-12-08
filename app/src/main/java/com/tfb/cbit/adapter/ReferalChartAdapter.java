package com.tfb.cbit.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ItemReferalChartBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.models.ReferalCoupan;
import com.tfb.cbit.models.ReferalCriteriaChart;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ReferalChartAdapter extends RecyclerView.Adapter<ReferalChartAdapter.ViewHolder> {
    private Context context;
    private List<ReferalCriteriaChart.ReferralList> groupName;
    private SessionUtil sessionUtil;

    public ReferalChartAdapter(Context context, List<ReferalCriteriaChart.ReferralList> groupName) {
        this.context = context;
        this.groupName = groupName;
        sessionUtil = new SessionUtil(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemReferalChartBinding binding = ItemReferalChartBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvLevel.setText(groupName.get(i).getLevel());
        viewHolder.binding.tvRefferal.setText(groupName.get(i).getRefferel() + "");
        viewHolder.binding.tvEm.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(groupName.get(i).getEM()))));
        viewHolder.binding.tvRefcom.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(groupName.get(i).getRefComm()))));
        viewHolder.binding.tvTds.setText(Utils.getTwoDecimalFormat(Float.parseFloat(String.valueOf(groupName.get(i).getTDS()))));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReferralPopup(groupName.get(i).getLevel());

            }
        });
    }

    private void getReferralPopup(String level) {
        JSONObject jsonObject = new JSONObject();
        String request = "";
        try {
            byte[] data;
            jsonObject.put("Level", level);
            request = jsonObject.toString();
            Log.i("isLoadMore Request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance().getReferralPopup(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                ReferalCoupan nm = gson.fromJson(responseData, ReferalCoupan.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    openUserTicketDailog(level, nm.getContent());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemReferalChartBinding binding;;
        public ViewHolder(@NonNull ItemReferalChartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void openUserTicketDailog(String level, List<ReferalCoupan.Content> content) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_referal);
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
        RecyclerView rvLevelUserList = dialog.findViewById(R.id.rvLevelUserList);
        ReferalCoupanAdapter itemListDataAdapter =
                new ReferalCoupanAdapter(context, content);
        rvLevelUserList.setHasFixedSize(true);
        rvLevelUserList.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        rvLevelUserList.setAdapter(itemListDataAdapter);

        rvLevelUserList.setNestedScrollingEnabled(false);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView tv_level = dialog.findViewById(R.id.tv_level);
        tv_level.setText("Level " + level);

        dialog.show();
    }

}
