package com.tfb.cbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.databinding.ListPassbookRawBinding;
import com.tfb.cbit.models.passbook.PassBookModel;

import java.util.ArrayList;
import java.util.List;
public class PassbookFilterAdapter extends RecyclerView.Adapter<PassbookFilterAdapter.ViewHolder> {

    private Context context;
     private List<PassBookModel.DisplayValuess> displayList;
    public int row_index = -1;
    public ArrayList<String> selctedArray = new ArrayList<>();

    public PassbookFilterAdapter(Context context, List<PassBookModel.DisplayValuess> displayList) {
        this.context = context;
        this.displayList=displayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListPassbookRawBinding binding = ListPassbookRawBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    public ArrayList<String> getSelected() {
        return selctedArray;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.binding.tvmyJticket.setText(displayList.get(position).getDisplay());

        viewHolder.binding.tvmyJticket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selctedArray.contains(displayList.get(position).getValue())) {
                        selctedArray.add(displayList.get(position).getValue());
                    }
                } else {
                    if (selctedArray.contains(displayList.get(position).getValue())) {
                        selctedArray.remove(displayList.get(position).getValue());
                    }
                  }
            }
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        ListPassbookRawBinding binding;
        public ViewHolder(@NonNull ListPassbookRawBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
