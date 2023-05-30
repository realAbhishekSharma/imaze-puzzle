package com.elementstore.imazepuzzle.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elementstore.imazepuzzle.Adapter.GridViewAdapter;
import com.elementstore.imazepuzzle.ImageIdList;
import com.elementstore.imazepuzzle.ImageLevel;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.Score;
import com.elementstore.imazepuzzle.SliderBoxTheme;

import java.util.ArrayList;
import java.util.List;

public class GameHome extends AppCompatActivity {

    int puzzleSize;
    TextView scoreView;

    Score score;
    ImageLevel imageLevel;
    LinearLayout settingButton;
    TextView sizeSelection;

    SharedPreferences ratePref;

    GridView levelGridView;
    GridViewAdapter gridViewAdapter;

    List<Integer> levelImageList;

    SliderBoxTheme sliderBoxTheme;

    SharedPreferences sharedPreferences;
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);
        getWindow().setStatusBarColor(getColor(R.color.deepCyan));

        ratePref = getSharedPreferences("rateData", MODE_PRIVATE);
        boolean rate = ratePref.getBoolean("rate", false);

        score = new Score(this);
        imageLevel = new ImageLevel(this);
        sliderBoxTheme = new SliderBoxTheme(this);
        sharedPreferences = getSharedPreferences("SizeState", MODE_PRIVATE);

        scoreView = findViewById(R.id.scoreView);
        scoreView.setText(score.getScore()+"");


        levelGridView = findViewById(R.id.levelListView);
        sizeSelection = findViewById(R.id.sizeSelectionContainer);
        scoreView = findViewById(R.id.scoreView);
        settingButton =  findViewById(R.id.settingButtonGameHome);

        levelImageList = new ArrayList<>();
        ImageIdList imageList = new ImageIdList();
        levelImageList = imageList.getImageIdList();


        puzzleSize = getSizeSelectionState();
        gridViewAdapter = new GridViewAdapter(this, levelImageList, puzzleSize);
        levelGridView.setAdapter(gridViewAdapter);

        sizeSelection.setText(puzzleSize+"x"+puzzleSize);
        sizeSelection.setOnClickListener(view -> {

            Animation leftSlideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_left);
            leftSlideAnimation.setDuration(300);
            levelGridView.setAnimation(leftSlideAnimation);

            if (puzzleSize ==3){
                puzzleSize =4;
                setSizeSelectionState(puzzleSize);
                gridViewAdapter = new GridViewAdapter(this, levelImageList, puzzleSize);
                levelGridView.setAdapter(gridViewAdapter);


            }else if (puzzleSize == 4){
                puzzleSize = 5;
                setSizeSelectionState(puzzleSize);
                gridViewAdapter = new GridViewAdapter(this, levelImageList, puzzleSize);
                levelGridView.setAdapter(gridViewAdapter);
            }else if (puzzleSize == 5){
                puzzleSize = 3;
                setSizeSelectionState(puzzleSize);
                gridViewAdapter = new GridViewAdapter(this, levelImageList, puzzleSize);
                levelGridView.setAdapter(gridViewAdapter);
            }
            sizeSelection.setText(puzzleSize+"x"+puzzleSize);
        });




        settingButton.setOnClickListener(view -> openSettingBox());

        levelGridView.setOnItemClickListener((adapterView, view, i, l) -> {

            if (imageLevel.getActiveLevel(puzzleSize)[i]) {
                openSelectedLevelActivity(i);
//                    Toast.makeText(getApplicationContext(), i + "", Toast.LENGTH_SHORT).show();
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
    }


    private void openSettingBox(){
        try {

            ConstraintLayout colorOne, colorTwo, colorThree;
            TextView okayButton, cancelButton;
            TextView rateThisGame, moreGames, youtubeLink;


            AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameHome.this);
            View view = getLayoutInflater().inflate(R.layout.settings_dialog_box, null);

            colorOne = view.findViewById(R.id.colorChoiceOne);
            colorTwo = view.findViewById(R.id.colorChoiceTwo);
            colorThree = view.findViewById(R.id.colorChoiceThree);


            okayButton = view.findViewById(R.id.okaySettingDialog);
            cancelButton = view.findViewById(R.id.cancelSettingDialog);
            rateThisGame = view.findViewById(R.id.rateThisGameSettingDialog);
            moreGames = view.findViewById(R.id.moreGames);
            youtubeLink = view.findViewById(R.id.youtubeLink);
            dialogBox.setView(view);

            final AlertDialog alertDialog = dialogBox.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            alertDialog.setCancelable(false);


            if (sliderBoxTheme.getColor() == getColor(R.color.cyan)){
                colorOne.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.cyan)));
                colorTwo.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                colorThree.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            }else if (sliderBoxTheme.getColor() == getColor(R.color.green)){
                colorOne.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                colorTwo.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.green)));
                colorThree.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            }else if (sliderBoxTheme.getColor() == getColor(R.color.red)){
                colorOne.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                colorTwo.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                colorThree.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red)));
            }

            final int[] color = {0};
            colorOne.setOnClickListener(item ->{
                colorOne.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.cyan)));
                colorTwo.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                colorThree.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                color[0] = getColor(R.color.cyan);
            });

            colorTwo.setOnClickListener(item ->{
                colorOne.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                colorTwo.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.green)));
                colorThree.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                color[0] = getColor(R.color.green);
            });

            colorThree.setOnClickListener(item ->{
                colorOne.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                colorTwo.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                colorThree.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red)));
                color[0] = getColor(R.color.red);
            });

            rateThisGame.setOnClickListener(view1 -> {
                Uri link = Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
                System.out.println(link);
                startActivity(new Intent(Intent.ACTION_VIEW, link));
                alertDialog.cancel();
            });

            moreGames.setOnClickListener(view1 -> {
                Uri link = Uri.parse("https://play.google.com/store/apps/dev?id=7918701498462053188");
                System.out.println(link);
                startActivity(new Intent(Intent.ACTION_VIEW, link));
                alertDialog.cancel();
            });

            youtubeLink.setOnClickListener(view1 -> {
                Uri link = Uri.parse("https://www.youtube.com/channel/UCdwrABK9efyOeAYYX71mtJg");
                System.out.println(link);
                startActivity(new Intent(Intent.ACTION_VIEW, link));
                alertDialog.cancel();
            });

            okayButton.setOnClickListener(item ->{
                sliderBoxTheme.setColor(color[0]);
                alertDialog.cancel();
            });

            cancelButton.setOnClickListener(item -> alertDialog.dismiss());

        }catch (Exception e){
            System.out.println("Error occur in opening setting box.");
        }

    }

    private void setSizeSelectionState(int sizeSelection){
        sharedPreferences.edit().putInt("Size",sizeSelection).apply();
    }

    private int getSizeSelectionState(){
        return sharedPreferences.getInt("Size", 3);
    }

}