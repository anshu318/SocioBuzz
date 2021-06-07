package com.example.sociobuzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Button btnEdit,btnDel;
    DocumentReference reference;
    String url;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        btnDel = findViewById(R.id.btn_del_iv);
        btnEdit = findViewById(R.id.btn_edit_iv);
        imageView = findViewById(R.id.iv_expand);
        textView = findViewById(R.id.tv_name_image);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();

        reference = db.collection("user").document(currentid);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageActivity.this,UpdatePhoto.class);
                startActivity(intent);
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(ImageActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()){
                    String name = task.getResult().getString("name");
                    url = task.getResult().getString("url");

                    Picasso.get().load(url).into(imageView);
                    textView.setText(name);
                }else {
                    Toast.makeText(ImageActivity.this, "No profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}