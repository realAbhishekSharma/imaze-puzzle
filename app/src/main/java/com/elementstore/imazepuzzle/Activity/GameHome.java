package com.elementstore.imazepuzzle.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.elementstore.imazepuzzle.Adapter.GridViewAdapter;
import com.elementstore.imazepuzzle.ImageIdList;
import com.elementstore.imazepuzzle.ImageLevel;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.Coins;
import com.elementstore.imazepuzzle.services.GameTracker;
import com.elementstore.imazepuzzle.services.PlayerLife;
import com.elementstore.imazepuzzle.services.SliderBoxTheme;
import com.elementstore.imazepuzzle.services.SoundAndVibration;

import java.util.ArrayList;
import java.util.List;

public class GameHome extends AppCompatActivity {

    int puzzleSize;
    int entryFee;
    TextView coinsView;

    SoundAndVibration soundAndVibration;
    Coins coins;
    ImageLevel imageLevel;
    LinearLayout settingButton, missionsButton, lifeButton;
    TextView lifeTextGameHome, lifeNotificationGameHome, missionNotificationGameHome;
    TextView sizeThreeSelection, sizeFourSelection, sizeFiveSelection;

    SharedPreferences ratePref;

    GridView levelGridView;
    GridViewAdapter gridViewAdapter;

    List<Integer> levelImageList;

    SliderBoxTheme sliderBoxTheme;

    SharedPreferences sharedPreferences;
    PlayerLife playerLife;

    GameTracker gameTracker;
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);
        getWindow().setStatusBarColor(getColor(R.color.deepCyan));


        ratePref = getSharedPreferences("rateData", MODE_PRIVATE);
        boolean rate = ratePref.getBoolean("rate", false);

        gameTracker = new GameTracker(this);
        soundAndVibration = new SoundAndVibration(this);
        coins = new Coins(this);
        imageLevel = new ImageLevel(this);
        sliderBoxTheme = new SliderBoxTheme(this);
        playerLife = new PlayerLife(this);
        sharedPreferences = getSharedPreferences("SizeState", MODE_PRIVATE);


        coinsView = findViewById(R.id.coinViewGameHome);

        levelGridView = findViewById(R.id.levelListView);
        sizeThreeSelection = findViewById(R.id.sizeThreeSelection);
        sizeFourSelection = findViewById(R.id.sizeFourSelection);
        sizeFiveSelection = findViewById(R.id.sizeFiveSelection);
        settingButton =  findViewById(R.id.settingButtonGameHome);
        missionsButton =  findViewById(R.id.missionsGameHome);
        lifeButton =  findViewById(R.id.lifeGameHome);
        lifeTextGameHome =  findViewById(R.id.lifeTextGameHome);
        lifeNotificationGameHome =  findViewById(R.id.lifeNotificationGameHome);
        missionNotificationGameHome =  findViewById(R.id.missionNotificationGameHome);

        levelImageList = new ArrayList<>();
        ImageIdList imageList = new ImageIdList();
        levelImageList = imageList.getImageIdList();


        puzzleSize = getSizeSelectionState();
        entryFee = getEntryFee();
        gridViewAdapter = new GridViewAdapter(this, levelImageList, puzzleSize);
        levelGridView.setAdapter(gridViewAdapter);

        changeSizeSelectionBackground(puzzleSize);

        sizeThreeSelection.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            puzzleSize = 3;
            changeGridViewOnSizeClick(puzzleSize);
            entryFee = getEntryFee();
        });

        sizeFourSelection.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            puzzleSize = 4;
            changeGridViewOnSizeClick(puzzleSize);
            entryFee = getEntryFee();
        });

        sizeFiveSelection.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            puzzleSize = 5;
            changeGridViewOnSizeClick(puzzleSize);
            entryFee = getEntryFee();
        });

        settingButton.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            startActivity(new Intent(this, Settings.class));
//            openSettingBox();
        });

        missionsButton.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            startActivity(new Intent(this, MissionActivity.class));
        });

        lifeButton.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            startActivity(new Intent(this, LifeShopActivity.class));
        });

        levelGridView.setOnItemClickListener((adapterView, view, i, l) -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();

            if (imageLevel.getActiveLevel(puzzleSize)[i]) {
                if (coins.getTotalCoins() >= entryFee) {
                    if (playerLife.getTotalLife() > 0 || playerLife.isActiveInfiniteLife()) {
                        openSelectedLevelActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Remaining Life.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Not Enough coins.", Toast.LENGTH_SHORT).show();
                }
            }

            if (i == 0 && !imageLevel.getActiveLevel(puzzleSize)[0]){
                if(puzzleSize == 3) {
                    Toast.makeText(getApplicationContext(), getText(R.string.customImageTextForThree)+"", Toast.LENGTH_SHORT).show();
                }else if(puzzleSize == 4) {
                    Toast.makeText(getApplicationContext(), getText(R.string.customImageTextForFour)+"", Toast.LENGTH_SHORT).show();
                }else if(puzzleSize == 5) {
                    Toast.makeText(getApplicationContext(), getText(R.string.customImageTextForFive)+"", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!rate && imageLevel.getActiveLevel(4)[4]){
            new Handler().postDelayed(this::openRateBox,2000);

        }
    }

    private void openRateBox(){
        try {
            AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameHome.this);
            View view = getLayoutInflater().inflate(R.layout.rate_dialog_box, null);

            TextView okay, notAsk;
            okay = view.findViewById(R.id.okay);
            notAsk = view.findViewById(R.id.notAsk);
            dialogBox.setView(view);

            final AlertDialog alertDialog = dialogBox.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
            alertDialog.setCancelable(true);


            okay.setOnClickListener(view1 -> {
                ratePref.edit().putBoolean("rate", true).apply();
                Uri link = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                System.out.println(link);
                startActivity(new Intent(Intent.ACTION_VIEW, link));
                alertDialog.cancel();
            });

            notAsk.setOnClickListener(views -> {
                ratePref.edit().putBoolean("rate", true).apply();
                alertDialog.cancel();
            });

        }catch (Exception e){
            System.out.println("Error in Rate Dialog Box");
        }
    }

    private void openSelectedLevelActivity(int level){

        if (puzzleSize == 3) {
            startActivity(new Intent(getApplicationContext(), ThreePuzzleGame.class).putExtra("level", level));
            finish();
        }else if (puzzleSize == 4) {
            startActivity(new Intent(getApplicationContext(), FourPuzzleGame.class).putExtra("level", level));
            finish();
        }else if (puzzleSize == 5) {
            startActivity(new Intent(getApplicationContext(), FivePuzzleGame.class).putExtra("level", level));
            finish();
        }
        coins.cutCoin(entryFee);
        Toast.makeText(getApplicationContext(), entryFee+" coins deducted.", Toast.LENGTH_SHORT).show();
        playerLife.decreaseLife();
    }

    private void setSizeSelectionState(int sizeSelection){
        sharedPreferences.edit().putInt("Size",sizeSelection).apply();
    }

    private int getSizeSelectionState(){
        return sharedPreferences.getInt("Size", 3);
    }

    private int getEntryFee(){
        if (getSizeSelectionState() == 3)
            return 1500;
        else if (getSizeSelectionState() == 4)
            return 3000;
        else
            return 5000;
    }

    private void changeGridViewOnSizeClick(int size){

        if (getSizeSelectionState() != size) {
            Animation leftSlideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_left);
            leftSlideAnimation.setDuration(300);
            levelGridView.setAnimation(leftSlideAnimation);

            setSizeSelectionState(size);
            gridViewAdapter = new GridViewAdapter(this, levelImageList, size);
            levelGridView.setAdapter(gridViewAdapter);

            changeSizeSelectionBackground(size);
        }
    }

    private void changeSizeSelectionBackground(int size){
        if (size == 3) {
            sizeThreeSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkCyan)));
            sizeFourSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
            sizeFiveSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
        }else if (size == 4){
            sizeThreeSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
            sizeFourSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkCyan)));
            sizeFiveSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));

        }else if (size == 5){
            sizeThreeSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
            sizeFourSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
            sizeFiveSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkCyan)));

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            gameTracker = new GameTracker(this);
            coinsView.setText(coins.getTotalCoins()+"");
            updateLifeIcon();
            if (gameTracker.getMissionBallNotification()) {
                missionNotificationGameHome.setVisibility(View.VISIBLE);
            }else {
                missionNotificationGameHome.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateLifeIcon(){
        if (playerLife.isActiveInfiniteLife()){
            lifeNotificationGameHome.setVisibility(View.GONE);
            lifeTextGameHome.setText(getString(R.string.infinity));
            lifeTextGameHome.setTypeface(Typeface.DEFAULT_BOLD);
        }else {
            lifeNotificationGameHome.setVisibility(View.VISIBLE);
            lifeTextGameHome.setText(playerLife.getTotalLife()+"");
        }
    }
}