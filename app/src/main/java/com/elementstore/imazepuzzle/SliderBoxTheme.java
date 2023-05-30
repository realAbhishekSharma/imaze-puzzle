package com.elementstore.imazepuzzle;

import android.content.Context;
import android.content.SharedPreferences;

public class SliderBoxTheme {

    private final SharedPreferences sharedPreferences;
    private final String COLOR = "color";

    private final Context context;
    public SliderBoxTheme(Context context){
        String SLIDER_BOX = "sliderBox";
        this.sharedPreferences = context.getSharedPreferences(SLIDER_BOX, Context.MODE_PRIVATE);
        this.context = context;

    }

    public int getColor() {
        return sharedPreferences.getInt(COLOR, context.getColor(R.color.cyan));
    }

    public void setColor(int color) {
        sharedPreferences.edit().putInt(COLOR, color).apply();
    }

}
