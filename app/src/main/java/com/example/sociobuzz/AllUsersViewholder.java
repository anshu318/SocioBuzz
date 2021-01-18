package com.example.sociobuzz;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AllUsersViewholder extends RecyclerView.ViewHolder {

    ImageView imageViewProfile;
    TextView tv_prof,tv_name;


    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public AllUsersViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setAllUser(FragmentActivity activity, String name,String uid,String prof,String url) {
        imageViewProfile = itemView.findViewById(R.id.ivprofile_item_all_user);

        tv_prof = itemView.findViewById(R.id.tv_prof_all_user);
        tv_name = itemView.findViewById(R.id.tv_name_all_user);

        Picasso.get().load(url).into(imageViewProfile);
        tv_name.setText(name);
        tv_prof.setText(prof);


        }

    }









