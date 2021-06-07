package com.example.sociobuzz;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MessageViewholder extends RecyclerView.ViewHolder {

    TextView sendertv,receivertv;
    ImageView iv_sender,iv_receiver;
    ImageButton playsender,playreceiver;
    public MessageViewholder(@NonNull View itemView) {
        super(itemView);

    }
    public void Setmessage(Application application ,  String message, String time, String date,String type,
                           String senderuid,String receiveruid, String sendername, String audio, String image){

        sendertv = itemView.findViewById(R.id.sender_tv);
        receivertv = itemView.findViewById(R.id.receiver_tv);

        playreceiver = itemView.findViewById(R.id.play_message_receiver);
        playsender = itemView.findViewById(R.id.play_message_sender);
        LinearLayout llsender = itemView.findViewById(R.id.llsender);
        LinearLayout llreceiver = itemView.findViewById(R.id.llreceiver);

        iv_receiver = itemView.findViewById(R.id.iv_receiver);
        iv_sender = itemView.findViewById(R.id.iv_sender);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();

        if(currentUid.equals(senderuid)){
            receivertv.setVisibility(View.GONE);
            sendertv.setText(message);

        }else if (currentUid.equals(receiveruid)){
            sendertv.setVisibility(View.GONE);
            receivertv.setText(message);
        }


    }



}
