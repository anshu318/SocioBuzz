package com.example.sociobuzz;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CmntViewholder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameTv, timeTv, cmntTv, upvoteTv, votesNoTv;
    int votesCount;
    DatabaseReference reference;
    FirebaseDatabase database;

    public CmntViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setComments(Application application, String name, String answer, String uid, String time, String url) {
        imageView = itemView.findViewById(R.id.imageView_cmnt);
        nameTv = itemView.findViewById(R.id.tv_name_cmnt);
        timeTv = itemView.findViewById(R.id.tv_time_cmnt);
        cmntTv = itemView.findViewById(R.id.tv_cmnt);

        nameTv.setText(name);
        timeTv.setText(time);
        cmntTv.setText(answer);
        Picasso.get().load(url).into(imageView);
    }

    public void upvotechecker(String postkey) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("votes");

        upvoteTv = itemView.findViewById(R.id.tv_vote_cmnt);
        votesNoTv = itemView.findViewById(R.id.tv_vote_no_cmnt);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postkey).hasChild(currentuid)) {
                    upvoteTv.setText("VOTED");
                    votesCount = (int)snapshot.child(postkey).getChildrenCount();
                    votesNoTv.setText(Integer.toString(votesCount)+"-VOTES");
                }else {
                    upvoteTv.setText("UPVOTE");
                    votesCount = (int)snapshot.child(postkey).getChildrenCount();
                    votesNoTv.setText(Integer.toString(votesCount)+"-VOTES");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
