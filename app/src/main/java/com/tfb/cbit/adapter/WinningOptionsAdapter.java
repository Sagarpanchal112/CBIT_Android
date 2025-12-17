package com.tfb.cbit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfb.cbit.databinding.ItemWinningOptionBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.contestdetails.WinningOptions;

import java.util.List;

public class WinningOptionsAdapter extends RecyclerView.Adapter<WinningOptionsAdapter.ViewHolder> {

    private static final String TAG = "OptionsAdapter";
    private Context context;
    private OnItemClickListener onItemClickListener = null;
    private List<WinningOptions> winningOptionsList;
    private String gameStatus = "";
   // final Animation animation;
    public int answer;

    public WinningOptionsAdapter(Context context, List<WinningOptions> sloteList, String answer) {
        this.context = context;
        this.winningOptionsList = sloteList;
      //  animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
      //  animation.setRepeatCount(Animation.INFINITE);
        this.answer= Integer.parseInt(answer);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemWinningOptionBinding binding = ItemWinningOptionBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WinningOptions winningOptions=winningOptionsList.get(i);
        Glide.with(context)
                .load(context.getFilesDir().getAbsolutePath()+"/" + winningOptions.getImage())
                .into(viewHolder.binding.tvOption);
        viewHolder.binding.tvCount.setText("Total:"+winningOptions.getCount()+"");
        int max =winningOptionsList.get(0).getCount();

        for (int j = 1; j < winningOptionsList.size(); j++) {
            if (winningOptionsList.get(j).getCount() > max) {
                max = winningOptionsList.get(j).getCount();
            }
        }


        for(int k=0;k<winningOptionsList.size();k++){
            Log.i("answer","==>"+answer);
            Log.i("answer id","==>"+winningOptionsList.get(k).getId());
            if(answer==winningOptionsList.get(k).getId()){
              //  viewHolder.tvOption.setBackground(ContextCompat.getDrawable(context, R.drawable.win ));
            }else{
                viewHolder.binding.tvOption.setBackground(null);
            }
        }
      /*  if(max==winningOptionsList.get(i).getCount()){
            viewHolder.itemView.startAnimation(animation);
       //     viewHolder.tvOption.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_green_border ));
        }*/
    }

    @Override
    public int getItemCount() {
        return winningOptionsList.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemWinningOptionBinding binding;

        public ViewHolder(@NonNull ItemWinningOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
