package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tfb.cbit.R;
import com.tfb.cbit.models.contestdetails.BoxJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewFliperItemAdapter extends RecyclerView.Adapter<ViewFliperItemAdapter.ViewHolder> {
    private static final String TAG = "BricksAdapter";
    private Context context;
    private boolean isRealData;
    private ArrayList<String> bricksItems;
    public boolean isStatus = false;
    //    private ArrayList<HashMap<String,Integer>> bricksColorModel;
    private ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();

    public ViewFliperItemAdapter(Context context, List<BoxJson> boxJsons, boolean isRealData) {
        this.context = context;
        this.isRealData = isRealData;
    }

    public ViewFliperItemAdapter(Context context) {
        this.context = context;
    }

    public void setStatus(boolean isStatus) {
        this.isStatus = isStatus;
        notifyDataSetChanged();
    }

    public ViewFliperItemAdapter(Context context, ArrayList<String> bricksItems) {
        this.context = context;
        this.bricksItems = bricksItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.slot_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if(bricksItems.get(i).equals("draw.jpg")) {
            viewHolder.imgSlot.setImageResource(R.drawable.ic_draw);

        }else{
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_logo)
                    .error(R.drawable.loading_logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform();
            Glide.with(context).load(bricksItems.get(i)).apply(options).into(viewHolder.imgSlot);

        }

        if (!isStatus) {
            Animation aniFade = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            viewHolder.imgSlot.startAnimation(aniFade);
          } else {
            viewHolder.imgSlot.setAnimation(null);
        }
        }

    @Override
    public int getItemCount() {
        return bricksItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSlot;
        ProgressBar spin_kit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSlot = itemView.findViewById(R.id.imgSlot);
            spin_kit = itemView.findViewById(R.id.spin_kit);
            //     ButterKnife.bind(this, itemView);
        }
    }

}
