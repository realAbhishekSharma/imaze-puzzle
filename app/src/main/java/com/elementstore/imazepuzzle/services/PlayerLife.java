package com.elementstore.imazepuzzle.services;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class PlayerLife {
    SharedPreferences lifeDatabase;
    private final String LIFE_DATABASE = "LIFE_HERE";
    private final String LIFE = "LIFE";
    private final String INFINITE_LIFE = "INFINITE_LIFE";
    private final String START_TIME = "START_TIME";
    private final String ACTIVE_TIME_LIMIT = "ACTIVE_TIME_LIMIT";
    private final String CURRENT_TIME = new Date().toString();
    private final Date TIME = new Date();
    private final int MAX_LIFE = 5;

    public PlayerLife(Context context){
        lifeDatabase = context.getSharedPreferences(LIFE_DATABASE, Context.MODE_PRIVATE);
    }

    public void increaseLife(){
        if (getTotalLife()<MAX_LIFE){
            lifeDatabase.edit().putInt(LIFE, getTotalLife()+1).apply();
        }
    }

    public void increaseToMaximumLife(){
        lifeDatabase.edit().putInt(LIFE, MAX_LIFE).apply();

    }

    public void decreaseLife(){
        if (getTotalLife()>0 && !isActiveInfiniteLife()){
            lifeDatabase.edit().putInt(LIFE, getTotalLife()-1).apply();
        }
    }

    public int getTotalLife() {
        return lifeDatabase.getInt(LIFE, MAX_LIFE);
    }

    private int getActiveTimeLimitOfInfiniteTime(){
        return lifeDatabase.getInt(ACTIVE_TIME_LIMIT, 0);
    }

    public void setActiveTimeLimitOfInfiniteTime(int timeInSecond){
        lifeDatabase.edit().putInt(ACTIVE_TIME_LIMIT, timeInSecond).apply();
        setStartTimeOfInfiniteLife();
    }

    private void setStartTimeOfInfiniteLife(){
        lifeDatabase.edit().putString(START_TIME, new Date().toString()).apply();
    }

    public String getStartTimeOfInfiniteLife(){
        return lifeDatabase.getString(START_TIME, null);
    }

    public boolean isActiveInfiniteLife(){
        if (getStartTimeOfInfiniteLife() == null){
            return false;
        }
        long difference = getRemainInfiniteTime();
        System.out.println("difference "+difference);
        if (difference>0 && difference<getActiveTimeLimitOfInfiniteTime()){
            return true;
        }
        return false;
    }
    public int getRemainInfiniteTime(){
        System.out.println(getActiveTimeLimitOfInfiniteTime());
        System.out.println(new Date().getTime() - new Date(getStartTimeOfInfiniteLife()).getTime());
        return (int) (getActiveTimeLimitOfInfiniteTime()-(new Date().getTime() - new Date(getStartTimeOfInfiniteLife()).getTime())/1000);
    }

}
