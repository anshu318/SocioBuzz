package com.example.sociobuzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Fragment3 extends Fragment {

    FirebaseDatabase database;
    DatabaseReference databaseReference,databaseReference1,profileRef;
    RecyclerView recyclerView,recyclerView_profile;
    RequestMember requestMember;
    TextView requesttv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment3,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Requests").child(currentUserId);
        profileRef = database.getReference("All Users");

        requestMember = new RequestMember();

        recyclerView_profile = getActivity().findViewById(R.id.recylerview_profile);



        recyclerView_profile.setHasFixedSize(true);

        recyclerView_profile.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView = getActivity().findViewById(R.id.recylerview_requestf3);
        requesttv = getActivity().findViewById(R.id.requeststv);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        //   MediaController mediaController;
        //  recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }


    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    requesttv.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                }else {
                    requesttv.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseRecyclerOptions<All_UserMmber> options1 =
                new FirebaseRecyclerOptions.Builder<All_UserMmber>()
                        .setQuery(profileRef,All_UserMmber.class)
                        .build();

        FirebaseRecyclerAdapter<All_UserMmber,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<All_UserMmber, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull All_UserMmber model) {


                        final String postkey = getRef(position).getKey();

                        holder.setProfile(getActivity(),model.getName(),model.getUid(),model.getProf(),model.getUrl());


                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();


                        holder.viewUserprofile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),ShowUser.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.profile,parent,false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();
//
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        recyclerView_profile.setLayoutManager(gridLayoutManager);
        recyclerView_profile.setAdapter(firebaseRecyclerAdapter1);


        FirebaseRecyclerOptions<RequestMember> options =
                new FirebaseRecyclerOptions.Builder<RequestMember>()
                        .setQuery(databaseReference,RequestMember.class)
                        .build();

        FirebaseRecyclerAdapter<RequestMember,RequestViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<RequestMember, RequestViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestViewholder holder, int position, @NonNull RequestMember model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = user.getUid();
                        final String postkey = getRef(position).getKey();

                        holder.setRequest(getActivity(),model.getName(),model.getUrl(),model.getProfession()
                                ,model.getBio(),model.getPrivacy(),model.getEmail(),model.getFollowers(),model.getWebsite(),model.getUserid());

                        String uid = getItem(position).getUserid();
                        String name = getItem(position).getName();
                        String bio = getItem(position).getBio();
                        String email = getItem(position).getEmail();
                        String privacy = getItem(position).getPrivacy();
                        String url = getItem(position).getUrl();
                        String website = getItem(position).getWebsite();
                        String age = getItem(position).getProfession();


                        holder.button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = getItem(position).getName();
                                decline(name);
                            }
                        });
                        holder.button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String uid = getItem(position).getUserid();
                                databaseReference1 = database.getReference("followers").child(uid);
                                requestMember.setName(name);

                                requestMember.setUserid(uid);
                                requestMember.setUrl(url);
                                requestMember.setProfession(age);
                                String id = databaseReference1.push().getKey();
                                databaseReference1.child(uid).setValue(requestMember);

                                databaseReference.child(currentUserId).child(uid).removeValue();

                                Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
                                decline(name);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.request_item,parent,false);

                        return new RequestViewholder(view);
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void decline(String name) {

        Query query = databaseReference.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                }
                //   Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///
            }
        });
    }


}