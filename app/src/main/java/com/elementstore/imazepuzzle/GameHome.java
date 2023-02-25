package com.elementstore.imazepuzzle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;

public class GameHome extends AppCompatActivity {

    TextView scoreView;
    TextView one, two, three;
    TextView[] levelButton = new TextView[5];

    Score score;
    ImageLevel imageLevel;

    SharedPreferences ratePref;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);

        ratePref = getSharedPreferences("rateData", MODE_PRIVATE);
        boolean rate = ratePref.getBoolean("rate", false);

        score = new Score(this);
        imageLevel = new ImageLevel(this);

        scoreView = findViewById(R.id.scoreView);
        scoreView.setText(score.getScore()+"");


        levelButton[0] = findViewById(R.id.one);
        levelButton[1] = findViewById(R.id.two);
        levelButton[2] = findViewById(R.id.three);
        levelButton[3] = findViewById(R.id.four);
        levelButton[4] = findViewById(R.id.five);

        imageLevel.setActiveLevel(0);
        for (int i = 0; i<5; i++){

            if (!imageLevel.getActiveLevel()[i]){
                levelButton[i].setEnabled(false);
                levelButton[i].setForeground(getDrawable(R.drawable.lock));
                levelButton[i].setAlpha(0.6f);
            }

        }

        if (!rate && imageLevel.getActiveLevel()[4]){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openRateBox();
                }
            },2000);

        }


        levelButton[0].setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "car"));
                finish();
        });

        levelButton[1].setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "animal"));
                finish();
        });

        levelButton[2].setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "enemy"));
                finish();
        });

        levelButton[3].setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "space"));
                finish();
        });

        levelButton[4].setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "fruit"));
                finish();
        });


    }

    private void openRateBox(){

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameHome.this);
        View view = getLayoutInflater().inflate(R.layout.rate_dialog_box, null);

        TextView okay,notAsk;

        okay = view.findViewById(R.id.okay);
        notAsk = view.findViewById(R.id.notAsk);

        dialogBox.setView(view);

        final AlertDialog alertDialog = dialogBox.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.setCancelable(false);

        okay.setOnClickListener(view1 -> {
            ratePref.edit().putBoolean("rate",true).apply();
            Uri link = Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
            System.out.println(link);
            startActivity(new Intent(Intent.ACTION_VIEW, link));
            alertDialog.cancel();
        });

        notAsk.setOnClickListener(views -> {
            ratePref.edit().putBoolean("rate",true).apply();
            alertDialog.cancel();
        });


    }
}