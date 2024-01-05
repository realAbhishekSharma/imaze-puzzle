package com.elementstore.imazepuzzle.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.elementstore.imazepuzzle.R;

public class SliderBoxTheme {

    private final SharedPreferences sharedPreferences;
    private final String COLOR = "color";

    private final Context context;
    public SliderBoxTheme(Context context){
        String SLIDER_BOX = "sliderBox";
        this.sharedPreferences = context.getSharedPreferences(SLIDER_BOX, Context.MODE_PRIVATE);
        this.context = context;
    }

    public int getActiveBoxColor() {
        return getColor(getColorIndex());
    }

    public void changeActiveBoxColor() {
        int index = getColorIndex();
        if (index == 3){
            index = 0;
        }else {
            index++;
        }
        sharedPreferences.edit().putInt(COLOR, index).apply();
    }
    private int getColorIndex(){
        return sharedPreferences.getInt(COLOR, 0);
    }

    private int getColor(int number){
        switch (number){
            case 1:
                return R.color.cyan;
            case 2:
                return R.color.red;
            case 3:
                return R.color.green;
            default:
                return R.color.darkBlue;
        }
    }
}
