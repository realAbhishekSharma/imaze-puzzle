package com.elementstore.imazepuzzle;

import android.content.Context;
import android.content.SharedPreferences;

public class ImageLevel {
    private final String DATABASE = "corruptedFile";
    private final String LEVEL = "Level";
    boolean[] activeLevel;
    private int level;
    private final SharedPreferences sharedPreferences;

    private ImageIdList idList;

    public ImageLevel(Context context) {
        sharedPreferences = context.getSharedPreferences(DATABASE,Context.MODE_PRIVATE);
        this.idList = new ImageIdList();
        this.level = this.idList.getImageIdCount();
        this.activeLevel = new boolean[level];
        for (int i =0; i<this.level; i++){
            activeLevel[i] = false;
        }
    }

    public void setActiveLevel(int size, int levelIndex){
        if (levelIndex<level) {
            if (size == 3 && levelIndex == 11){
                activeLevel[0] = true;
                sharedPreferences.edit().putBoolean(LEVEL +""+size+ "" + 0, activeLevel[0]).apply();
            }else if (size == 4 && levelIndex == 6){
                activeLevel[0] = true;
                sharedPreferences.edit().putBoolean(LEVEL +""+size+ "" + 0, activeLevel[0]).apply();
            }else if (size == 5 && levelIndex == 4){
                activeLevel[0] = true;
                sharedPreferences.edit().putBoolean(LEVEL +""+size+ "" + 0, activeLevel[0]).apply();
            }

            activeLevel[levelIndex] = true;
            sharedPreferences.edit().putBoolean(LEVEL +""+size+ "" + levelIndex, activeLevel[levelIndex]).apply();
        }
    }


    public boolean[] getActiveLevel(int size){
        for (int i =0; i<this.level; i++){
            activeLevel[i] = sharedPreferences.getBoolean(LEVEL +""+size+""+i, false);
        }
        activeLevel[1] = true;
        return activeLevel;
    }

    public int getCompletedMaxLevel(int size){
        int count = 1;
        for (int i =1; i<this.level; i++){
            activeLevel[i] = sharedPreferences.getBoolean(LEVEL +""+size+""+i, false);
            if (activeLevel[i]){
                count++;
            }
        }
        return count;
    }
}
