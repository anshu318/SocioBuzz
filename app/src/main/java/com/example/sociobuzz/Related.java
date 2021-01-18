package com.example.sociobuzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Related extends AppCompatActivity {


    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related);

        recyclerView = findViewById(R.id.rv_related);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid = user.getUid();

        reference = database.getReference("favouriteList").child(currentuserid);

        FirebaseRecyclerOptions<QuestionMember> options =
                new FirebaseRecyclerOptions.Builder<QuestionMember>()
                        .setQuery(reference,QuestionMember.class).build();

        FirebaseRecyclerAdapter<QuestionMember,Viewholder_Question> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Question holder, int position, @NonNull QuestionMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentuserid = user.getUid();

                        final  String postkey = getRef(position).getKey();


                        holder.setitemrelated(getApplication(),model.getName(),model.getUrl(),model.getUserid(),model.getKey(),
                                model.getQuestion(),model.getPrivacy(),model.getTime());

                       final String que = getItem(position).getQuestion();
                        //String name = getItem(position).getName();
                        //String url = getItem(position).getUrl();
                        //final String time = getItem(position).getTime();
                        //String privacy = getItem(position).getPrivacy();
                        final String userid = getItem(position).getUserid();

                        holder.reply_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Related.this,ReplyActivity.class);
                                intent.putExtra("uid",userid);
                                intent.putExtra("q",que);
                                intent.putExtra("postkey",postkey);

                                startActivity(intent);
                            }
                        });



                    }

                    @NonNull
                    @Override
                    public Viewholder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.related_item,parent,false);

                        return new Viewholder_Question(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }
}