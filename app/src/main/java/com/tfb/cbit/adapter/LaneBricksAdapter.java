/*
package com.tfb.cbit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.models.contestdetails.BoxJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaneBricksAdapter extends RecyclerView.Adapter<LaneBricksAdapter.ViewHolder> {
    private static final String TAG = "BricksAdapter";
    private Context context;
    private List<BoxJson> boxJsons;
    private boolean isRealData;
    private Integer[] bricksItems;
    //    private ArrayList<HashMap<String,Integer>> bricksColorModel;
    private ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();

    public LaneBricksAdapter(Context context, List<BoxJson> boxJsons, boolean isRealData) {
        this.context = context;
        this.boxJsons = boxJsons;
        this.isRealData = isRealData;*/
/**//*

    }

    public LaneBricksAdapter(Context context, Integer[] bricksItems) {
        this.context = context;
        this.bricksItems = bricksItems;
    }

    public LaneBricksAdapter(Context context, ArrayList<Integer> bricksItems, ArrayList<HashMap<String, Integer>> bricksColorModel) {
        this.context = context;
        this.bricksColorModel = bricksColorModel;
    }

    public LaneBricksAdapter(Context context, ArrayList<Integer> bricksItems, ArrayList<HashMap<String, Integer>> bricksColorModel, List<BoxJson> boxJsons) {
        this.context = context;

        this.boxJsons = boxJsons;
        this.bricksColorModel = bricksColorModel;
        this.isRealData = boxJsons.size() != 0;
        Log.d(TAG, "BricksAdapter: " + isRealData);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_lane_brickes, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.imgBrickes.setImageResource(bricksItems[i]);
    }

    @Override
    public int getItemCount() {
        return bricksItems.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgBrickes)
        ImageView imgBrickes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
*/
