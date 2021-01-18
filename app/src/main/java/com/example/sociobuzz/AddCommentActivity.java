package com.example.sociobuzz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddCommentActivity extends AppCompatActivity {

    String uid,postkey;
    EditText editText;
    Button button;
    CommentMember member;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllPost;
    String name,url,time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        member = new CommentMember();
        editText = findViewById(R.id.comment_et);
        button = findViewById(R.id.btn_comment_submit);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uid = bundle.getString("user");
            postkey = bundle.getString("postkey");
        }else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        AllPost = database.getReference("All posts").child(postkey).child("Comments");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savecomment();
            }
        });

    }

    void savecomment() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        String comment = editText.getText().toString();
        if (comment != null) {
            Calendar cdate = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String savedate = currentdate.format(cdate.getTime());

            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
            final String savetime = currenttime.format(ctime.getTime());

            time = savedate +":"+ savetime;

            member.setComment(comment);
            member.setTime(time);
            member.setName(name);
            member.setUid(userid);
            member.setUrl(url);

            String id = AllPost.push().getKey();
            AllPost.child(id).setValue(member);
            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Please enter anything", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        FirebaseFirestore d = FirebaseFirestore.getInstance();
        DocumentReference reference;
        reference = d.collection("user").document(userid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists())
                        {
                            url = task.getResult().getString("url");
                            name = task.getResult().getString("name");


                        }
                        else
                        {
                            Toast.makeText(AddCommentActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });





    }
}