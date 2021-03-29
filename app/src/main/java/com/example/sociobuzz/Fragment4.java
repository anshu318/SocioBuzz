package com.example.sociobuzz;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class Fragment4 extends Fragment implements View.OnClickListener {

    Button button;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,likesref;
    Boolean likechecker = false;
    DatabaseReference db1,db2,db3;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment4,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        button = getActivity().findViewById(R.id.createpost_f4);
        reference = database.getReference("All posts");
        likesref = database.getReference("post likes");
        recyclerView = getActivity().findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All posts");

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createpost_f4:
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Postmember> options =
                new FirebaseRecyclerOptions.Builder<Postmember>()
                        .setQuery(reference,Postmember.class).build();

        FirebaseRecyclerAdapter<Postmember,PostViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Postmember,PostViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostViewholder holder, int position, @NonNull Postmember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentuserid = user.getUid();

                        final  String postkey = getRef(position).getKey();
                        holder.setPost(getActivity(),model.getName(),model.getUrl(),model.getPostUri(),
                                model.getTime(),model.getUid(),model.getType(),model.getDesc());


                        final String url = getItem(position).getPostUri();
                        final String name = getItem(position).getName();
                       //final String url = getItem(position).getUrl();
                        final String time = getItem(position).getTime();
                        final String type = getItem(position).getType();
                       final String userid = getItem(position).getUid();


                        holder.likeChecker(postkey);

                        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getActivity(),CommentsActivity.class);
                                intent.putExtra("uid",userid);

                                intent.putExtra("postkey",postkey);
                                startActivity(intent);



                            }
                        });

                        holder.commentbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getActivity(),CommentsActivity.class);
                                intent.putExtra("uid",userid);

                                intent.putExtra("postkey",postkey);
                                startActivity(intent);



                            }
                        });
                        holder.menuoptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog(name,url,time,userid,type);
                            }
                        });
                        holder.likebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                likechecker = true;

                                likesref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (likechecker.equals(true)) {
                                            if (snapshot.child(postkey).hasChild(currentuserid)) {
                                                likesref.child(postkey).child(currentuserid).removeValue();
                                               // delete(time);
                                                likechecker = false;
                                            }else {
                                                likesref.child(postkey).child(currentuserid).setValue(true);


                                                likechecker = false;



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
                    public PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout,parent,false);

                        return new PostViewholder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void showDialog(String name, String url, String time, String userid, String type) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.post_option,null);
        TextView download = view.findViewById(R.id.download_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);
        TextView copyurl = view.findViewById(R.id.copyurl_tv_post);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        alertDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid = user.getUid();

        if (userid.equals(currentuserid)) {
            delete.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.INVISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = db1.orderByChild("time").equalTo(time);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();

                            Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query query2 = db2.orderByChild("time").equalTo(time);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();

                            Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query query3 = db3.orderByChild("time").equalTo(time);
                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();

                            Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                reference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });

                alertDialog.dismiss();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                       if (type.equals("iv")) {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            request.setTitle("Download");
                            request.setDescription("Downloading Image....");
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+System.currentTimeMillis() + ".jpg");
                            DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);

                            Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();

                            alertDialog.dismiss();
                        }else {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            request.setTitle("Download");
                            request.setDescription("Downloading Video....");
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+System.currentTimeMillis() + ".mp4");
                            DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);

                            Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();

                            alertDialog.dismiss();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(getActivity(), "No Permissions", Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.with(getActivity())
                        .setPermissionListener(permissionListener)
                        .setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();


            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharetext = name +"\n" +"\n" + url;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_TEXT,sharetext);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent,"share via"));

                alertDialog.dismiss();
            }
        });

        copyurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager cp = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String",url);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(getActivity(), "Url Copied", Toast.LENGTH_SHORT).show();

                alertDialog.dismiss();

            }
        });
    }
}
