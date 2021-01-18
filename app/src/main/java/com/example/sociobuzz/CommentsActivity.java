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

public class CommentsActivity extends AppCompatActivity {


    String uid,post_key,key;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;

    TextView addComment;
    RecyclerView recyclerView;
    ImageView  imageViewUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference voteRef,AllPost;

    Boolean votechecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);



        imageViewUser = findViewById(R.id.iv_comment_user);
        addComment = findViewById(R.id.comment_tv);

        recyclerView = findViewById(R.id.rv_comment);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));




        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            uid = extra.getString("uid");
            post_key = extra.getString("postkey");
           // question = extra.getString("q");
            //key = extra.getString("key");
        }else {
            Toast.makeText(this, "oops", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        voteRef = database.getReference("votes");
        AllPost = database.getReference("All posts").child(post_key).child("Comments");

       // reference1 = db.collection("user").document(uid);
        reference = db.collection("user").document(currentuid);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommentsActivity.this,AddCommentActivity.class);
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

        reference.get()
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
                            Toast.makeText(CommentsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        FirebaseRecyclerOptions<CommentMember> options =
                new FirebaseRecyclerOptions.Builder<CommentMember>()
                        .setQuery(AllPost,CommentMember.class).build();

        FirebaseRecyclerAdapter<CommentMember,CmntViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CommentMember, CmntViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CmntViewholder holder, int position, @NonNull CommentMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentuserid = user.getUid();
                        final String postkey = getRef(position).getKey();

                        holder.setComments(getApplication(),model.getName(),model.getComment()
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
                    public CmntViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.cmnt_layout,parent,false);

                        return new CmntViewholder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);




      }



}