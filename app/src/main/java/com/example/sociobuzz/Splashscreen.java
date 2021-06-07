package com.example.sociobuzz;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Splashscreen extends AppCompatActivity {

    ImageView imageView,imageViewText;
    TextView nameTv,name2Tv;
    Animation top, bottom, lefttoright;
    long animTime = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);


        imageView = findViewById(R.id.iv_logo_splash);
        //imageViewText = findViewById(R.id.iv_logo_splash_text);
        //name2Tv = findViewById(R.id.tv_splash_name2);
        //nameTv = findViewById(R.id.tv_splash_name);

        lefttoright = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
        top = AnimationUtils.loadAnimation(this, R.anim.top);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);

        imageView.setAnimation(top);
        //imageViewText.setAnimation(bottom);
        //name2Tv.setAnimation(bottom);
        //nameTv.setAnimation(bottom);

        //ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView,"y",350f);
        //ObjectAnimator animatorname = ObjectAnimator.ofFloat(nameTv,"z",300f);

        //animatorY.setDuration(animTime);
        //animatorname.setDuration(animTime);
        AnimatorSet animatorSet = new AnimatorSet();
        //animatorSet.playTogether(animatorY,animatorname);
        //animatorSet.play(animatorY);
        animatorSet.start();

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splashscreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },4000);*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user!=null)
                {
                    Intent intent = new Intent(Splashscreen.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(Splashscreen.this,LoginActivity.class);
                    startActivity(intent);
                }

            }
        },4000);
    }
}