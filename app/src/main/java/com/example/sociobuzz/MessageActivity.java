package com.example.sociobuzz;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView imageView;
    ImageButton sendbtn,cambtn,micbtn;
    TextView username;
    EditText messageEt;
    UploadTask uploadTask;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootref1,rootref2;
    MessageMember messageMember;
    Boolean typingchecker = false;
    String  receiver_name,receiver_uid,sender_uid,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            url = bundle.getString("u");
            receiver_name = bundle.getString("n");
            receiver_uid = bundle.getString("uid");
        }else {
            Toast.makeText(this, "user missing", Toast.LENGTH_SHORT).show();
        }

        messageMember = new MessageMember();
        cambtn = findViewById(R.id.cam_sendmessage);
        imageView = findViewById(R.id.iv_message);
        messageEt = findViewById(R.id.messageet);
        sendbtn = findViewById(R.id.imageButtonsend);
        username = findViewById(R.id.username_messageTv);
        micbtn  = findViewById(R.id.btn_mic);

        recyclerView = findViewById(R.id.rv_message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));

        Picasso.get().load(url).into(imageView);
        username.setText(receiver_name);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sender_uid = user.getUid();

        rootref1 = database.getReference("Message").child(sender_uid).child(receiver_uid);
        rootref2 = database.getReference("Message").child(receiver_uid).child(sender_uid);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<MessageMember> options1 =
                new FirebaseRecyclerOptions.Builder<MessageMember>()
                        .setQuery(rootref1,MessageMember.class)
                        .build();

        FirebaseRecyclerAdapter<MessageMember,MessageViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<MessageMember, MessageViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull MessageViewholder holder, int position, @NonNull MessageMember model) {

                        holder.Setmessage(getApplication(),model.getMessage(),model.getTime(),model.getDate(),model.getType(),
                                model.getSenderuid(),model.getReceiveruid(),model.getSendername(),model.getAudio(),model.getImage());







                    }

                    @NonNull
                    @Override
                    public MessageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.message_layout,parent,false);

                        return new MessageViewholder(view);
                    }
                };

        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);

    }


    private void SendMessage() {
        String message = messageEt.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final  String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate +":"+ savetime;

        if (message.isEmpty()){
            Toast.makeText(this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
        }else {
            messageMember.setDate(savedate);
            messageMember.setTime(savetime);
            messageMember.setMessage(message);
            messageMember.setReceiveruid(receiver_uid);
            messageMember.setSenderuid(sender_uid);
            messageMember.setType("text");

            String id = rootref1.push().getKey();
            rootref1.child(id).setValue(messageMember);

            String id1 = rootref2.push().getKey();
            rootref2.child(id1).setValue(messageMember);

            messageEt.setText("");


        }
    }

}