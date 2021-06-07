package com.example.sociobuzz;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Fragment1 extends Fragment implements View.OnClickListener{

    ImageView imageView;
    TextView nameEt,profEt,bioEt,emailEt,webEt,postTv;
    ImageButton imageButtonEdit,imageButtonMenu;
    Button btnsendmessage;
    DocumentReference reference;
    FirebaseFirestore firestore;
    String url;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("user").document(userid);




        imageView = getActivity().findViewById(R.id.iv_f1);
        nameEt = getActivity().findViewById(R.id.tv_name_f1);
        profEt = getActivity().findViewById(R.id.tv_prof_f1);
        bioEt = getActivity().findViewById(R.id.tv_bio_f1);
        emailEt = getActivity().findViewById(R.id.tv_email_f1);
        webEt = getActivity().findViewById(R.id.tv_web_f1);
        postTv = getActivity().findViewById(R.id.tv_post_f1);
        btnsendmessage = getActivity().findViewById(R.id.btn_sendmessage_f1);

        imageButtonEdit = getActivity().findViewById(R.id.ib_edit_f1);
        imageButtonMenu = getActivity().findViewById(R.id.ib_menu_f1);
        postTv.setOnClickListener(this);


        imageButtonMenu.setOnClickListener(this);
        imageButtonEdit.setOnClickListener(this);
        imageView.setOnClickListener(this);
        btnsendmessage.setOnClickListener(this);
        webEt.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_edit_f1:
                Intent intent = new Intent(getActivity(), updateProfile.class);
                startActivity(intent);
                break;
            case R.id.ib_menu_f1:
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(),"bottomsheet");
                break;
            case R.id.iv_f1:
                Intent intent1 = new Intent(getActivity(),ImageActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_post_f1:
                Intent intent5 = new Intent(getActivity(),IndividualPost.class);
                startActivity(intent5);
                break;
            case R.id.btn_sendmessage_f1:
                Intent intent6 = new Intent(getActivity(),ChatActivity.class);
                startActivity(intent6);
                break;
            case R.id.tv_web_f1:
                try {
                    String url = webEt.getText().toString();
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.setData(Uri.parse(url));
                    startActivity(intent2);
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
                break;



        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("user").document(userid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()) {

                                String nameResult = task.getResult().getString("name");
                                String bioResult = task.getResult().getString("bio");
                                String emailResult = task.getResult().getString("email");
                                String webResult = task.getResult().getString("web");
                                url = task.getResult().getString("url");
                                String profResult = task.getResult().getString("prof");

                                Picasso.get().load(url).into(imageView);
                                nameEt.setText(nameResult);
                                bioEt.setText(bioResult);
                                emailEt.setText(emailResult);
                                webEt.setText(webResult);
                                profEt.setText(profResult);


                            } else {
                                Intent intent = new Intent(getActivity(), CreateProfile.class);
                                startActivity(intent);
                            }
                    }
                });



    }


}