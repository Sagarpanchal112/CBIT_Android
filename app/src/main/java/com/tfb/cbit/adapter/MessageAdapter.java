package com.tfb.cbit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfb.cbit.R;
import com.tfb.cbit.models.UserReportModel;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
//import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<UserReportModel.Content> messageList;
    private SessionUtil sessionUtil;

    private Context context;

    public MessageAdapter(Context context, List<UserReportModel.Content> messageList) {
        this.messageList = messageList;
        this.context = context;
        sessionUtil = new SessionUtil(context);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        UserReportModel.Content message = messageList.get(position);
        if (message.getSenderID() == 0) {
            if (!message.getTitle().equals("")) {
                holder.senderMessageImage.setVisibility(View.GONE);
                holder.receiverMessageImage.setVisibility(View.GONE);
              //  holder.receiverPlayerView.setVisibility(View.GONE);
              //  holder.senderPlayerView.setVisibility(View.GONE);
                holder.sender_text_message.setVisibility(View.INVISIBLE);
                holder.receiver_text_message.setVisibility(View.VISIBLE);
                holder.user_profile_image.setVisibility(View.VISIBLE);
                holder.receiver_text_message.setBackgroundResource(R.drawable.single_message_text_background);
                holder.receiver_text_message.setTextColor(Color.WHITE);
                holder.receiver_text_message.setGravity(Gravity.LEFT);
                holder.receiver_text_message.setText(message.getTitle());
            } else if (!message.getImageUrl().equals("")) {
                holder.sender_text_message.setVisibility(View.GONE);
                holder.receiver_text_message.setVisibility(View.GONE);
               // holder.receiverPlayerView.setVisibility(View.GONE);
               // holder.senderPlayerView.setVisibility(View.GONE);
                holder.receiverMessageImage.setVisibility(View.VISIBLE);
                holder.senderMessageImage.setVisibility(View.INVISIBLE);
                holder.user_profile_image.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUrl()).apply(Utils.getUserAvatarReques()).into(holder.receiverMessageImage);

            } else if (!message.getVoiceUrl().equals("")) {
                holder.sender_text_message.setVisibility(View.GONE);
                holder.receiver_text_message.setVisibility(View.GONE);
                holder.receiverMessageImage.setVisibility(View.GONE);
                holder.senderMessageImage.setVisibility(View.GONE);
               // holder.receiverPlayerView.setVisibility(View.VISIBLE);
               // holder.senderPlayerView.setVisibility(View.INVISIBLE);
                holder.user_profile_image.setVisibility(View.VISIBLE);
               // holder.receiverPlayerView.setAudio(message.getVoiceUrl());

            }
        } else {
            if (!message.getTitle().equals("")) {
                holder.senderMessageImage.setVisibility(View.GONE);
                holder.receiverMessageImage.setVisibility(View.GONE);
               // holder.receiverPlayerView.setVisibility(View.GONE);
               // holder.senderPlayerView.setVisibility(View.GONE);
                holder.sender_text_message.setBackgroundResource(R.drawable.single_message_text_another_background);
                holder.sender_text_message.setTextColor(Color.BLACK);
                holder.sender_text_message.setGravity(Gravity.LEFT);
                holder.sender_text_message.setText(message.getTitle());
                holder.receiver_text_message.setVisibility(View.INVISIBLE);
                holder.user_profile_image.setVisibility(View.INVISIBLE);
                holder.sender_text_message.setVisibility(View.VISIBLE);
            } else if (!message.getImageUrl().equals("")) {
                holder.sender_text_message.setVisibility(View.GONE);
                holder.receiver_text_message.setVisibility(View.GONE);
               // holder.receiverPlayerView.setVisibility(View.GONE);
               // holder.senderPlayerView.setVisibility(View.GONE);
                holder.senderMessageImage.setVisibility(View.VISIBLE);
                holder.receiverMessageImage.setVisibility(View.INVISIBLE);
                holder.user_profile_image.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUrl()).apply(Utils.getUserAvatarReques()).into(holder.senderMessageImage);

            } else if (!message.getVoiceUrl().equals("")) {
                holder.sender_text_message.setVisibility(View.GONE);
                holder.receiver_text_message.setVisibility(View.GONE);
                holder.receiverMessageImage.setVisibility(View.GONE);
                holder.senderMessageImage.setVisibility(View.GONE);
               // holder.receiverPlayerView.setVisibility(View.INVISIBLE);
               // holder.senderPlayerView.setVisibility(View.VISIBLE);
                holder.user_profile_image.setVisibility(View.GONE);

                //holder.senderPlayerView.setAudio(message.getVoiceUrl());

            }
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView sender_text_message, receiver_text_message;
        CircleImageView user_profile_image;
        ImageView receiverMessageImage, senderMessageImage;
       // VoicePlayerView receiverPlayerView, senderPlayerView;

        MessageViewHolder(View view) {
            super(view);
            sender_text_message = view.findViewById(R.id.senderMessageText);
            receiver_text_message = view.findViewById(R.id.receiverMessageText);
            user_profile_image = view.findViewById(R.id.messageUserImage);
            receiverMessageImage = view.findViewById(R.id.receiverMessageImage);
            senderMessageImage = view.findViewById(R.id.senderMessageImage);
          //  receiverPlayerView = view.findViewById(R.id.receiverPlayerView);
          //  senderPlayerView = view.findViewById(R.id.senderPlayerView);

        }

    }
}
