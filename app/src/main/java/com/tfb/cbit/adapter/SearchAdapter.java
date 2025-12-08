package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.private_group.PrivateGroupResponse;
import com.tfb.cbit.utility.Utils;

import java.util.ArrayList;
import java.util.List;


public class SearchAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PrivateGroupResponse.Content> mArrayList = new ArrayList<>();
    private LayoutInflater mInflater;
    public OnItemClickListener onItemClickListener;

    public SearchAdapter(Context mContext, ArrayList<PrivateGroupResponse.Content> mArrayList) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    public void addAll(List<PrivateGroupResponse.Content> mArrayList) {
        this.mArrayList.clear();
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HolderHorizontal(mInflater.inflate(R.layout.item_my_search_group_list, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HolderHorizontal holderHome = (HolderHorizontal) holder;
        bindHolderHorizontal(holderHome, position);
    }

    private void bindHolderHorizontal(HolderHorizontal holder, int position) {
        final PrivateGroupResponse.Content current = mArrayList.get(position);
        holder.tvGroupName.setText(current.getPrivate_group_name());
        holder.tv_create.setText("Created at :"+ Utils.getDate(current.getCreated_at()));

        holder.txtJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(holder.txtJoin, position);

            }
        });


    }


    class HolderHorizontal extends RecyclerView.ViewHolder {
        TextView tvGroupName, txtJoin,tv_create;

        public HolderHorizontal(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            txtJoin = itemView.findViewById(R.id.txtJoin);
            tv_create = itemView.findViewById(R.id.tv_create);
        }
    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
    }


}

