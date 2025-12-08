package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.tfb.cbit.R;
import com.tfb.cbit.models.JticketFilter.Names;

import java.util.List;

public class JfilterNameAdapter extends RecyclerView.Adapter<JfilterNameAdapter.AttendanceAdapterViewHolder> {

    private Context mContext;
    private List<Names> mArrayList;
    private OnItemClickListener onItemClickListener;
    public int row_index = -1;

    public JfilterNameAdapter(Context mContext,
                              List<Names> mArrayList) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;

    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener myClickListener) {
        this.onItemClickListener = myClickListener;
    }
    @Override
    public AttendanceAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_filter_row, parent, false);
        return new AttendanceAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AttendanceAdapterViewHolder holder, final int position) {

        holder.txtCatName.setText(mArrayList.get(position).getName());
        holder.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
                row_index = position;
                notifyDataSetChanged();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
                row_index = position;
                notifyDataSetChanged();
            }
        });
        if (row_index == position) {
            holder.chkSelect.setChecked(true);
        } else {
            holder.chkSelect.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class AttendanceAdapterViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView txtCatName;
        CheckBox chkSelect;


        public AttendanceAdapterViewHolder(View itemView) {
            super(itemView);
            txtCatName = itemView.findViewById(R.id.txtName);
            chkSelect = itemView.findViewById(R.id.chkSelect);
        }
    }
}

