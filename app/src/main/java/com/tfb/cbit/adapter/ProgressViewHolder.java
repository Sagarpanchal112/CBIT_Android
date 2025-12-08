package com.tfb.cbit.adapter;

import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;

public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar pBar;

    public ProgressViewHolder(View v) {
        super(v);
        pBar = (ProgressBar) v.findViewById(R.id.progressBar1);
    }
}