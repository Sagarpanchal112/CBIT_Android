package com.tfb.cbit.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListSeekbarDemoBinding;
import com.tfb.cbit.interfaces.OnRangeListener;

import java.util.HashMap;
import java.util.List;

public class SeekBarAdapter extends RecyclerView.Adapter<SeekBarAdapter.ViewHolder> {

    private Context context;
    private List<HashMap<String, String>> hashMapList;
    private OnRangeListener onRangeListener;

    public SeekBarAdapter(Context context, List<HashMap<String, String>> hashMapList) {
        this.context = context;
        this.hashMapList = hashMapList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListSeekbarDemoBinding binding = ListSeekbarDemoBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        HashMap<String, String> map = hashMapList.get(i);
        //  viewHolder.rangeSeekBar.setFixGap(Integer.parseInt(map.get("step")));
        //  viewHolder.rangeSeekBar.setMinValue(Float.parseFloat(map.get("min")));
        //  viewHolder.rangeSeekBar.setMaxValue(Float.parseFloat(map.get("max")));
        // viewHolder.rangeSeekBar.apply();
        viewHolder.binding.tvMin.setText(map.get("min"));
        viewHolder.binding.tvMax.setText(map.get("max"));
        viewHolder.binding.tvStep.setText(map.get("step"));

        if (!map.get("updatestep").isEmpty()) {
            //   viewHolder.rangeSeekBar.setFixGap(Integer.parseInt(map.get("updatestep")));
        } else {
            hashMapList.get(i).put("updatestep", "1");

        }
    }

    public void setOnRangeListener(OnRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        /*  @BindView(R.id.rangeSeekBar)
          CrystalRangeSeekbar rangeSeekBar;
        */
        ListSeekbarDemoBinding binding;

        public ViewHolder(@NonNull ListSeekbarDemoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
