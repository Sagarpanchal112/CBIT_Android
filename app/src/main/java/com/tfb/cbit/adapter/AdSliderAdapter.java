package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tfb.cbit.R;
import com.tfb.cbit.models.advertise.Content;

import java.util.List;


public class AdSliderAdapter extends PagerAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<Content> contentList;
    public AdSliderAdapter(Context context,List<Content> contentList) {
        this.context = context;
        this.contentList = contentList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View imageLayout = inflater.inflate(R.layout.sliding_images, container, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(context)
                .load(contentList.get(position).getImage())
                .apply(requestOptions)
                .into(imageView);

        // imageView.setImageResource(IMAGES.get(position));

        container.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
