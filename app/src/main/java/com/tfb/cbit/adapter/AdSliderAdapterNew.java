/*
package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tfb.cbit.R;
import com.tfb.cbit.models.advertise.Content;
import com.tfb.cbit.views.MetalRecyclerViewPager;

import java.util.List;

public class AdSliderAdapterNew extends MetalRecyclerViewPager.MetalAdapter<AdSliderAdapterNew.ViewHolder> {

    private Context context;
    private List<Content> contentList;
    RequestOptions requestOptions;
    public AdSliderAdapterNew(Context context, DisplayMetrics metrics, List<Content> contentList){
        super(metrics);
        this.context = context;
        this.contentList = contentList;
        requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.sliding_images,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        Glide.with(context)
                .load(contentList.get(i).getImage())
                .apply(requestOptions)
                .into(viewHolder.image);

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    class ViewHolder extends MetalRecyclerViewPager.MetalViewHolder{
        @BindView(R.id.image)
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
*/
