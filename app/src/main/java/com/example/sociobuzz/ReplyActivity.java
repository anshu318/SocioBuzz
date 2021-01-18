package com.example.sociobuzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ReplyActivity extends AppCompatActivity {


    String uid,question,post_key,key;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference, reference2;

    TextView nameTv, questionTv, tvreply;
    RecyclerView recyclerView;
    ImageView imageViewQue, imageViewUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference voteRef,AllQuestions;

    Boolean votechecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        nameTv = findViewById(R.id.name_reply_tv);
        questionTv = findViewById(R.id.que_reply_tv);
        imageViewQue = findViewById(R.id.iv_que_user);
        imageViewUser = findViewById(R.id.iv_reply_user);
        tvreply = findViewById(R.id.answer_tv);

        recyclerView = findViewById(R.id.rv_ans);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReplyActivity.this));




        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            uid = extra.getString("uid");
            post_key = extra.getString("postkey");
            question = extra.getString("q");
            //key = extra.getString("key");
        }else {
            Toast.makeText(this, "oops", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        voteRef = database.getReference("votes");
        AllQuestions = database.getReference("All Questions").child(post_key).child("Answer");

        reference = db.collection("user").document(uid);
        reference2 = db.collection("user").document(currentuid);

        tvreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReplyActivity.this,AnswerActivity.class);
                intent.putExtra("user",uid);
                //intent.putExtra("question",question);
                intent.putExtra("postkey",post_key);
                //intent.putExtra("key",privacy);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Question user reference
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists())
                        {
                            String url = task.getResult().getString("url");
                            String name = task.getResult().getString("name");

                            Picasso.get().load(url).into(imageViewQue);
                            questionTv.setText(question);
                            nameTv.setText(name);
                        }
                        else
                        {
                            Toast.makeText(ReplyActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//reference for replying user
        reference2.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists())
                        {
                            String url = task.getResult().getString("url");
                            Picasso.get().load(url).into(imageViewUser);

                        }
                        else
                        {
                            Toast.makeText(ReplyActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        FirebaseRecyclerOptions<AnswerMember> options =
                new FirebaseRecyclerOptions.Builder<AnswerMember>()
                        .setQuery(AllQuestions,AnswerMember.class).build();

        FirebaseRecyclerAdapter<AnswerMember,AnsViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AnswerMember, AnsViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AnsViewholder holder, int position, @NonNull AnswerMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentuserid = user.getUid();
                        final String postkey = getRef(position).getKey();

                        holder.setAnswer(getApplication(),model.getName(),model.getAnswer()
                                ,model.getUid(),model.getTime(),model.getUrl());

                        holder.upvotechecker(postkey);
                        holder.upvoteTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                votechecker = true;
                                voteRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (votechecker.equals(true)) {
                                            if (snapshot.child(postkey).hasChild(currentuserid)) {
                                                voteRef.child(postkey).child(currentuserid).removeValue();
                                                votechecker = false;
                                            }else {
                                                voteRef.child(postkey).child(currentuserid).setValue(true);
                                                votechecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });





                    }

                    @NonNull
                    @Override
                    public AnsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.ans_layout,parent,false);

                        return new AnsViewholder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}