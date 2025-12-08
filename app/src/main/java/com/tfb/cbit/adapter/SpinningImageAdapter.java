package com.tfb.cbit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.databinding.ItemSpinningImageBinding;
import com.tfb.cbit.databinding.ListAccountBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.SelectedImage;
import com.tfb.cbit.models.SpinningImagesModel;

import java.util.List;


public class SpinningImageAdapter extends RecyclerView.Adapter<SpinningImageAdapter.ViewHolder> {

    private static final String TAG = "OptionsAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener = null;
    private List<SpinningImagesModel.Content> winningOptionsList;
    private String gameStatus = "";
    public int row_index = -1;

    public SpinningImageAdapter(Context context, List<SpinningImagesModel.Content> sloteList) {
        this.context = context;
        this.winningOptionsList = sloteList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemSpinningImageBinding binding = ItemSpinningImageBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        SpinningImagesModel.Content winningOptions = winningOptionsList.get(position);
        Glide.with(context)
                .load(winningOptions.getImage())
                .into(viewHolder.binding.tvOption);
        SelectedImage cObj = new SelectedImage();
        cObj.setCategoryId(winningOptions.getCategoryID());
        cObj.setItemId(winningOptions.getId());
        Gson gson = new Gson();
        String jObj = gson.toJson(cObj);
        Log.i("cObj", "==>" + jObj);
        for (int j = 0; j < CBit.selectedImageArrayList.size(); j++) {
            if(CBit.selectedImageArrayList.get(j).getItemId() == winningOptions.getId()){

                Log.i(" IN IF cObj", "==>" + jObj);
                viewHolder.binding.chkSelect.setChecked(true);
            }else{
                Log.i(" IN else cObj", "==>" + jObj);
                viewHolder.binding.chkSelect.setChecked(false);
            }
        }

        viewHolder.binding.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                if (CBit.selectedImageArrayList.size() > 0) {
                    if (viewHolder.binding.chkSelect.isChecked()) {
                        for (int j = 0; j < CBit.selectedImageArrayList.size(); j++) {
                            if (winningOptions.getCategoryID() == CBit.selectedImageArrayList.get(j).getCategoryId()) {
                                if (winningOptions.getId() != CBit.selectedImageArrayList.get(j).getItemId()) {
                                    SelectedImage obj = new SelectedImage();
                                    obj.setCategoryId(winningOptions.getCategoryID());
                                    obj.setItemId(winningOptions.getId());
                                    CBit.selectedImageArrayList.add(obj);
                                    break;
                                }
                            }else {
                                CBit.selectedImageArrayList.clear();
                                SelectedImage obj = new SelectedImage();
                                obj.setCategoryId(winningOptions.getCategoryID());
                                obj.setItemId(winningOptions.getId());
                                CBit.selectedImageArrayList.add(obj);
                                break;
                            }
                        }
                    } else {
                        for (int j = 0; j < CBit.selectedImageArrayList.size(); j++) {
                            if (winningOptions.getCategoryID() == CBit.selectedImageArrayList.get(j).getCategoryId()) {
                                if (winningOptions.getId() == CBit.selectedImageArrayList.get(j).getItemId()) {
                                    CBit.selectedImageArrayList.remove(j);
                                    break;
                                }
                            }
                        }
                    }

                /* Gson gson = new Gson();
                SelectedImage cObj = new SelectedImage();
                cObj.setCategoryId(winningOptions.getCategoryID());
                cObj.setItemId(winningOptions.getId());
                String jObj = gson.toJson(cObj);
                Log.i("cObj", "==>" + jObj);
                if (CBit.selectedImageArrayList.contains(cObj)) {
                    CBit.selectedImageArrayList.remove(cObj);
                    Log.i(" IN IF cObj", "==>" + jObj);
                    return;
                }


                   for (int j = 0; j < CBit.selectedImageArrayList.size(); j++) {
                        if (winningOptions.getCategoryID() == CBit.selectedImageArrayList.get(j).getCategoryId()) {
                            cObj = new SelectedImage();
                            cObj.setCategoryId(winningOptions.getCategoryID());
                            cObj.setItemId(winningOptions.getId());
                            if (CBit.selectedImageArrayList.contains(cObj)) {
                                CBit.selectedImageArrayList.remove(j);
                                break;
                            } else {
                                if (viewHolder.chkSelect.isChecked()) {
                                    SelectedImage obj = new SelectedImage();
                                    obj.setCategoryId(winningOptions.getCategoryID());
                                    obj.setItemId(winningOptions.getId());
                                    CBit.selectedImageArrayList.add(obj);
                                    break;
                                }
                            }
                            *//*if (winningOptions.getId() == CBit.selectedImageArrayList.get(j).getItemId()) {


                            } else {
                                SelectedImage obj = new SelectedImage();
                                obj.setCategoryId(winningOptions.getCategoryID());
                                obj.setItemId(winningOptions.getId());
                                CBit.selectedImageArrayList.add(obj);
                                break;
                            }*//*
                        } else {
                            CBit.selectedImageArrayList.clear();
                            SelectedImage obj = new SelectedImage();
                            obj.setCategoryId(winningOptions.getCategoryID());
                            obj.setItemId(winningOptions.getId());
                            CBit.selectedImageArrayList.add(obj);
                            break;
                        }
                    }*/
                }
                else {
                    SelectedImage obj = new SelectedImage();
                    obj.setCategoryId(winningOptions.getCategoryID());
                    obj.setItemId(winningOptions.getId());
                    CBit.selectedImageArrayList.add(obj);
                }

              String  jObj = gson.toJson(CBit.selectedImageArrayList);
                Log.i("selectedImageArrayList", "==>" + jObj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return winningOptionsList.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemSpinningImageBinding binding;
        public ViewHolder(@NonNull ItemSpinningImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
