package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListBricksBinding;
import com.tfb.cbit.models.contestdetails.BoxJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BricksAdapter extends RecyclerView.Adapter<BricksAdapter.ViewHolder>{
    private static final String TAG = "BricksAdapter";
    private Context context;
    private List<BoxJson> boxJsons;
    private boolean isRealData;
    private ArrayList<Integer> bricksItems;
//    private ArrayList<HashMap<String,Integer>> bricksColorModel;
    private ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    public BricksAdapter(Context context,List<BoxJson> boxJsons,boolean isRealData){
        this.context = context;
        this.boxJsons = boxJsons;
        this.isRealData= isRealData;
    }

    public BricksAdapter(Context context, ArrayList<Integer> bricksItems, ArrayList<HashMap<String,Integer>> bricksColorModel) {
        this.context = context;
        this.bricksItems = bricksItems;
        this.bricksColorModel = bricksColorModel;
    }

    public BricksAdapter(Context context, ArrayList<Integer> bricksItems, ArrayList<HashMap<String,Integer>> bricksColorModel,List<BoxJson> boxJsons) {
        this.context = context;
        this.bricksItems = bricksItems;
        this.boxJsons = boxJsons;
        this.bricksColorModel = bricksColorModel;
        this.isRealData= boxJsons.size()!= 0;
        Log.d(TAG, "BricksAdapter: "+isRealData);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListBricksBinding binding = ListBricksBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
         return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(isRealData){
            viewHolder.binding.tvBricks.setText(String.valueOf(boxJsons.get(i).getNumber()));
            if(boxJsons.get(i).getColor().equals("blue")){
                viewHolder.binding.tvBricks.setBackgroundColor(ContextCompat.getColor(context, R.color.color_blue));
            }else if(boxJsons.get(i).getColor().equals("green")){
                viewHolder.binding.tvBricks.setBackgroundColor(ContextCompat.getColor(context, R.color.color_green));
            }else{
                viewHolder.binding.tvBricks.setBackgroundColor(ContextCompat.getColor(context, R.color.color_red));
            }
        }else{
            //viewHolder.tvBricks.setText(String.valueOf(bricksModels.get(i).get("count")));
           // viewHolder.tvBricks.setBackgroundColor(ContextCompat.getColor(context, bricksModels.get(i).get("color")));
            viewHolder.binding.tvBricks.setText(String.valueOf(bricksItems.get(i)));
            viewHolder.binding.tvBricks.setBackgroundColor(ContextCompat.getColor(context, bricksColorModel.get(i).get("color")));
        }
    }

    @Override
    public int getItemCount() {
        if(isRealData){
            return boxJsons.size();
        }else{
            return bricksItems.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{



        ListBricksBinding binding;
        public ViewHolder(@NonNull ListBricksBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
