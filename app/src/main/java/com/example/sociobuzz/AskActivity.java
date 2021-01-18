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

public class AskActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference AllQuestion,userQuestion;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    QuestionMember member;
    String name,url,privacy,uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid = user.getUid();
        editText = findViewById(R.id.ask_et_question);
        button = findViewById(R.id.btn_submit);
        documentReference = db.collection("user").document(currentuserid);

        AllQuestion = firebaseDatabase.getReference("All Questions");
        userQuestion = firebaseDatabase.getReference("User Questions").child(currentuserid);

        member = new QuestionMember();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = editText.getText().toString();

                Calendar cdate = Calendar.getInstance();
                SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                final String savedate = currentdate.format(cdate.getTime());

                Calendar ctime = Calendar.getInstance();
                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                final String savetime = currenttime.format(ctime.getTime());

                String time = savedate +":"+ savetime;

                if (question != null) {
                    member.setQuestion(question);
                    member.setName(name);
                    member.setUrl(url);
                    member.setPrivacy(privacy);
                    member.setUserid(uid);
                    member.setTime(time);
                    String id = userQuestion.push().getKey();
                    userQuestion.child(id).setValue(member);

                    String child = AllQuestion.push().getKey();
                    member.setKey(id);
                    AllQuestion.child(child).setValue(member);
                    Toast.makeText(AskActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AskActivity.this, "Please Ask a Question", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();



        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            name = task.getResult().getString("name");
                            url = task.getResult().getString("url");
                            privacy = task.getResult().getString("privacy");
                            uid = task.getResult().getString("uid");



                        }else {
                            Toast.makeText(AskActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}