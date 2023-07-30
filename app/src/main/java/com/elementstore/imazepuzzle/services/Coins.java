package com.elementstore.imazepuzzle.services;

import android.content.Context;
import android.content.SharedPreferences;

public class Coins {
    SharedPreferences coinDatabase;
    private SoundAndVibration soundAndVibration;
    private final String COIN_DATABASE = "COIN_HERE";
    private final String COINS = "COINS";

    public Coins(Context context){
        coinDatabase = context.getSharedPreferences(COIN_DATABASE, Context.MODE_PRIVATE);
        soundAndVibration = new SoundAndVibration(context);
    }

    public void addCoin(int coin){
        soundAndVibration.playAddCoinSound();
        coinDatabase.edit().putInt(COINS, getTotalCoins()+coin).apply();
    }
    public void cutCoin(int coin){
        if (getTotalCoins()>coin) {
            soundAndVibration.playDeductCoinSound();
            coinDatabase.edit().putInt(COINS, getTotalCoins() - coin).apply();
        }
    }

    public int getTotalCoins(){
        return coinDatabase.getInt(COINS, 5000);
    }

    public int getRandomCoins(int from, int to){
        return (int) (Math.random()*(to-from))+from;
    }


    public int generateCoins(int correct){
        return correct*500;
    }
}
