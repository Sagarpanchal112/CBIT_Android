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
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListReferalCoupanBinding;
import com.tfb.cbit.models.ReferalCoupan;
import com.tfb.cbit.models.ReferalCriteriaChart;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ReferalCoupanAdapter extends RecyclerView.Adapter<ReferalCoupanAdapter.ViewHolder> {
    private Context context;
    private List<ReferalCoupan.Content> groupName;
    private SessionUtil sessionUtil;

    public ReferalCoupanAdapter(Context context, List<ReferalCoupan.Content> groupName) {
        this.context = context;
        this.groupName = groupName;
        sessionUtil = new SessionUtil(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListReferalCoupanBinding binding = ListReferalCoupanBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.binding.tvName.setText( groupName.get(i).getUser());
        if(!groupName.get(i).getReffralList().isEmpty()){
            viewHolder.binding.tvGroup.setText("("+groupName.get(i).getReffralList()+")");

        }

    }


    @Override
    public int getItemCount() {
        return groupName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ListReferalCoupanBinding binding;
        public ViewHolder(@NonNull ListReferalCoupanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
