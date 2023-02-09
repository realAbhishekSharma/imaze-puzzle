package com.elementstore.imazepuzzle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameHome extends AppCompatActivity {

    TextView scoreView;
    TextView one, two, three;

    Score score;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);

        score = new Score(this);

        scoreView = findViewById(R.id.scoreView);
        scoreView.setText(score.getScore()+"");


        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);

        one.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "animal"));
                finish();
        });

        two.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "car"));
                finish();
        });

        three.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "fruit"));
                finish();
        });


    }
}