package com.elementstore.imazepuzzle;

import android.content.Context;
import android.content.SharedPreferences;

public class ImageLevel {
    private final String DATABASE = "corruptedFile";
    private final String LEVEL = "Level";
    boolean[] activeLevel = new boolean[5];
    private final SharedPreferences sharedPreferences;


    public ImageLevel(Context context) {
        sharedPreferences = context.getSharedPreferences(DATABASE,Context.MODE_PRIVATE);
        for (int i =0; i<5; i++){
            activeLevel[i] = false;
        }
    }

    public void setActiveLevel(int levelIndex){
        activeLevel[levelIndex] = true;
        sharedPreferences.edit().putBoolean(LEVEL+""+levelIndex, activeLevel[levelIndex]).apply();
    }

    public boolean[] getActiveLevel(){
        for (int i =0; i<5; i++){
            activeLevel[i] = sharedPreferences.getBoolean(LEVEL+""+i, false);
        }
        return activeLevel;
    }
}
