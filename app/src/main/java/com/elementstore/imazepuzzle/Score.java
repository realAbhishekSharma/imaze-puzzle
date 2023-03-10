package com.elementstore.imazepuzzle;

import android.content.Context;
import android.content.SharedPreferences;

public class Score {
    private final String DATABASE = "corruptedFile";
    private final String SCORE = "Score";
    private final SharedPreferences sharedPreferences;

    public Score(Context context) {
        sharedPreferences = context.getSharedPreferences(DATABASE,Context.MODE_PRIVATE);
    }

    public int getScore() {
        return sharedPreferences.getInt(SCORE,0);
    }

    public void setScore(int score) {
        sharedPreferences.edit().putInt(SCORE, score).apply();

    }

    public void saveHighScore(int score) {
        if (getScore()<score) {
            sharedPreferences.edit().putInt(SCORE, score).apply();
        }
    }

    public int generateScore(int correct){

        return correct*100;
    }
}
