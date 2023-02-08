package com.example.imazepuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameHome extends AppCompatActivity {

    TextView scoreView;
    TextView one, two, three;

    Score score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);

        score = new Score(this);

        scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText(score.getScore()+"");


        one = (TextView) findViewById(R.id.one);
        two = (TextView) findViewById(R.id.two);
        three = (TextView) findViewById(R.id.three);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "animal"));
//                finish();
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "car"));
//                finish();
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), PuzzleGame.class).putExtra("activity", "fruit"));
//                finish();
            }
        });


    }
}