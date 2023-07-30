package com.elementstore.imazepuzzle.dialogs;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.elementstore.imazepuzzle.Activity.ThreePuzzleGame;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.SoundAndVibration;

public class TImeOverDialog {

    BoxItemClick boxItemClick;
    View root;
    Context context;
    AlertDialog.Builder dialogBox;
    AlertDialog alertDialog;

    TextView titleView, earnedCoinView, goHomeButton, addTime;
    private SoundAndVibration soundAndVibration;

    public TImeOverDialog(Context context, BoxItemClick boxItemClick){
        this.context = context;
        this.boxItemClick = boxItemClick;
        soundAndVibration = new SoundAndVibration(context);
        initializeBox();

    }

    private void initializeBox(){

        dialogBox = new AlertDialog.Builder(this.context);
        root = View.inflate(context, R.layout.time_over_dialog, null);
        dialogBox.setView(root);


        titleView = root.findViewById(R.id.titleShow);
        earnedCoinView = root.findViewById(R.id.earnedScore);
        goHomeButton = root.findViewById(R.id.goHomeButton);
        addTime = root.findViewById(R.id.timeIncrease);


        addTime.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            boxItemClick.onAddTimeFromAds(view);
        });

        goHomeButton.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            boxItemClick.onGoHome(view);
        });

        alertDialog = dialogBox.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);


    }

    public void openBox(){
        try {
            alertDialog.show();
        }catch (Exception e){

        }
    }
    public void closeBox(){
        alertDialog.dismiss();
    }

    public interface BoxItemClick{
        void onAddTimeFromAds(View view);
        void onAddTimeFromCoins(View view);
        void onGoHome(View view);
    }

    public void setTitleView(String title){
        titleView.setText(title);
    }

    @SuppressLint("SetTextI18n")
    public void setEarnedCoinView(int earnedCoin){
        earnedCoinView.setText(earnedCoin+"");
    }
    public void setWinningContent(int earnedCoins){
        addTime.setVisibility(View.GONE);
        goHomeButton.setText("Go Home");
        titleView.setText("You Won");
        earnedCoinView.setText(earnedCoins+"");
        titleView.setTextColor(context.getColor(R.color.cyan));
    }
}
