package com.example.sociobuzz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Fragment3 extends Fragment implements View.OnClickListener {


    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        reference = database.getReference("All Users");

        recyclerView = getActivity().findViewById(R.id.rv_all_user);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }



    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<All_UserMmber> options =
                new FirebaseRecyclerOptions.Builder<All_UserMmber>()
                        .setQuery(reference,All_UserMmber.class).build();

        FirebaseRecyclerAdapter<All_UserMmber,AllUsersViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<All_UserMmber,AllUsersViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AllUsersViewholder holder, int position, @NonNull All_UserMmber model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentuserid = user.getUid();

                        final  String postkey = getRef(position).getKey();
                        holder.setAllUser(getActivity(),model.getName(),model.getUid(),model.getProf(),model.getUrl());



//
                        final String name = getItem(position).getName();
                        final String url = getItem(position).getUrl();
                        final String prof = getItem(position).getProf();

                        final String userid = getItem(position).getUid();

















                    }



                    @NonNull
                    @Override
                    public AllUsersViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.alluser_layout,parent,false);

                        return new AllUsersViewholder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }



    @Override
    public void onClick(View v) {

    }
}
