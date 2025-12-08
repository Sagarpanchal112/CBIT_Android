package com.tfb.cbit.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tfb.cbit.R;
import com.tfb.cbit.models.HowtoPlayModel;
import com.tfb.cbit.utility.Utils;

import java.util.List;


public class TutorialSliderAdapter extends PagerAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<HowtoPlayModel.Contest> imageList;
    public TutorialSliderAdapter(Context context, List<HowtoPlayModel.Contest> imageList) {
        this.context = context;
        this.imageList = imageList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View imageLayout = inflater.inflate(R.layout.sliding_images, container, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);
        Glide.with(context).load(imageList.get(position).getImage()).apply(Utils.getUserAvatarReques()).into(imageView);

    //    imageView.setImageResource(imageList.get(position));

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
