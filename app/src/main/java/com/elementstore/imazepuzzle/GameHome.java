package com.elementstore.imazepuzzle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameHome extends AppCompatActivity {

    TextView scoreView;
    TextView one, two, three;
    TextView[] levelButton = new TextView[5];

    Score score;
    ImageLevel imageLevel;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);

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
}