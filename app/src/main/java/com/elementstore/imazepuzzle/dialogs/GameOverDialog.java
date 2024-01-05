package com.elementstore.imazepuzzle.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.Coins;
import com.elementstore.imazepuzzle.services.ShrinkViewEffect;
import com.elementstore.imazepuzzle.services.SoundAndVibration;

public class GameOverDialog {
    View root;
    Context context;
    AlertDialog.Builder dialogBox;
    AlertDialog alertDialog;

    TextView titleView, earnedCoinView, goHomeButton, addTime;
    TextView textForLow, textForHigh, coinForLow, coinForHigh, adsForLow, adsForHigh;
    private SoundAndVibration soundAndVibration;
    private BoxItemClick boxItemClick;
    private int totalEarnedCoin;
    private Coins coins;
    private boolean isWon;

    public GameOverDialog(Context context, View root, BoxItemClick boxItemClick){
        this.context = context;
        this.boxItemClick = boxItemClick;
        this.totalEarnedCoin = 0;
        soundAndVibration = new SoundAndVibration(context);
        this.root = root;
        coins = new Coins(context);
        initializeBox();

    }

    private void initializeBox(){
        titleView = root.findViewById(R.id.topTitleViewGameOver);
        earnedCoinView = root.findViewById(R.id.earnedScoreViewGameOver);

        textForLow = root.findViewById(R.id.textForLow);
        textForHigh = root.findViewById(R.id.textForHigh);

        coinForLow = root.findViewById(R.id.coinForLow);
        adsForLow = root.findViewById(R.id.adsForLow);
        coinForHigh = root.findViewById(R.id.coinForHigh);
        adsForHigh = root.findViewById(R.id.adsForHigh);

        goHomeButton = root.findViewById(R.id.goHomeGameOver);
        new ShrinkViewEffect(coinForLow);
        new ShrinkViewEffect(coinForHigh);
        new ShrinkViewEffect(adsForLow);
        new ShrinkViewEffect(adsForHigh);
        new ShrinkViewEffect(goHomeButton);

        coinForLow.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            coins.cutCoin(1000);
            boxItemClick.addLowMoves(view);
        });
        coinForHigh.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            coins.cutCoin(2000);
            boxItemClick.addHighMoves(view);
        });

        adsForLow.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            boxItemClick.addLowMovesWithAds(view);
        });

        adsForHigh.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            boxItemClick.addHighMovesWithAds(view);
        });

        goHomeButton.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            totalEarnedCoin = Integer.parseInt(earnedCoinView.getText().toString());
            coins.addCoin(totalEarnedCoin);
            boxItemClick.onGoHome(view);
        });

    }


    public interface BoxItemClick{
        void onGoHome(View view);
        void addLowMoves(View view);
        void addHighMoves(View view);
        void addLowMovesWithAds(View view);
        void addHighMovesWithAds(View view);
    }


    @SuppressLint("SetTextI18n")
    private void setWinningContent(){
        titleView.setText("Congratulations");
        earnedCoinView.setText(totalEarnedCoin+"");
        coinForLow.setVisibility(View.GONE);
        coinForHigh.setVisibility(View.GONE);
        textForLow.setText("1.5X Coins");
        textForHigh.setText("2X Coins");
    }


    @SuppressLint("SetTextI18n")
    private void setOutOfMovesContent(){
        titleView.setText("Out Of Moves");
        totalEarnedCoin = 0;
        earnedCoinView.setText(totalEarnedCoin+"");
        textForLow.setText("+30 Moves");
        textForHigh.setText("+60 Moves");
    }

    public void setDialogStatus( boolean isWon, int puzzleSize){
        this.isWon = isWon;
        if (isWon){
            totalEarnedCoin = coins.generateCoins(puzzleSize*puzzleSize);
            setWinningContent();
        }else {
            setOutOfMovesContent();
        }
    }
}
