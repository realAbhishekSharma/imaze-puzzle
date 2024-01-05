package com.elementstore.imazepuzzle.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.elementstore.imazepuzzle.R;

public class MainActivity extends AppCompatActivity {

    int time = 1300;
    ImageView mainSplashLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.cloudCyan));
        setContentView(R.layout.activity_main);

        mainSplashLogo = findViewById(R.id.mainSplashLogo);

        ScaleAnimation scaleDown = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        scaleDown.setDuration(time+200);
        mainSplashLogo.setAnimation(scaleDown);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), GameHome.class));
            overridePendingTransition(R.anim.fading_in,R.anim.fading_out);
            finish();


        },time);
    }
}