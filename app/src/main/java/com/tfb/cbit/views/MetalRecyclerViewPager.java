package com.tfb.cbit.views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.tfb.cbit.R;

public class MetalRecyclerViewPager extends RecyclerView {

    private int itemMargin;

    public MetalRecyclerViewPager(Context context) {
        super(context);
        init(context, null);
    }

    public MetalRecyclerViewPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MetalRecyclerViewPager(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MetalRecyclerViewPager, 0, 0);
            itemMargin = (int) typedArray.getDimension(R.styleable.MetalRecyclerViewPager_itemMargin, 0f);
            typedArray.recycle();
        }

        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);
    }

    public void setAdapter(Adapter adapter) {
        if (MetalAdapter.class.isInstance(adapter)) {
            MetalAdapter metalAdapter = (MetalAdapter) adapter;
            metalAdapter.setItemMargin(itemMargin);
            metalAdapter.updateDisplayMetrics();
        } else {
            throw new IllegalArgumentException("Only MetalAdapter is allowed here");
        }
        super.setAdapter(adapter);
    }

    public static abstract class MetalAdapter<VH extends MetalViewHolder> extends RecyclerView.Adapter<VH> {

        private DisplayMetrics metrics;
        private int itemMargin;
        private int itemWidth;

        public MetalAdapter(@NonNull DisplayMetrics metrics) {
            this.metrics = metrics;
        }

        void setItemMargin(int itemMargin) {
            this.itemMargin = itemMargin;
        }

        void updateDisplayMetrics() {
            itemWidth = metrics.widthPixels - itemMargin * 2;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            int currentItemWidth = itemWidth;

            if (position == 0) {
                currentItemWidth += itemMargin;
                holder.rootLayout.setPadding(itemMargin, 0, 0, 0);
            } else if (position == getItemCount() - 1) {
                currentItemWidth += itemMargin;
                holder.rootLayout.setPadding(0, 0, itemMargin, 0);
            }

            int height = holder.rootLayout.getLayoutParams().height;
            holder.rootLayout.setLayoutParams(new ViewGroup.LayoutParams(currentItemWidth, height));
        }
    }

    public static abstract class MetalViewHolder extends RecyclerView.ViewHolder {

        ViewGroup rootLayout;

        public MetalViewHolder(View itemView) {
            super(itemView);
            rootLayout = (ViewGroup) itemView.findViewById(R.id.root_layout);
        }
    }
}
