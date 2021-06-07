package com.example.sociobuzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FollowerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String userid;
    DatabaseReference databaseReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        recyclerView = findViewById(R.id.rv_followers);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(FollowerActivity.this));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            userid = bundle.getString("u");

        }else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

        databaseReference = database.getReference("followers").child(userid);




    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<RequestMember> options1 =
                new FirebaseRecyclerOptions.Builder<RequestMember>()
                        .setQuery(databaseReference,RequestMember.class)
                        .build();

        FirebaseRecyclerAdapter<RequestMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<RequestMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull RequestMember model) {

                        final String postkey = getRef(position).getKey();



                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        String currentuserid = getItem(position).getUserid();


                        holder.setFollower(getApplication(),model.getName(),model.getUrl(),model.getProfession(),model.getBio(),
                                model.getPrivacy(),model.getEmail(),model.getFollowers(),model.getWebsite());

                        holder.vpfollower.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(FollowerActivity.this,ShowUser.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("uid",currentuserid);
                                startActivity(intent);
                            }
                        });



                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.follower_layout,parent,false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
//
    }
}