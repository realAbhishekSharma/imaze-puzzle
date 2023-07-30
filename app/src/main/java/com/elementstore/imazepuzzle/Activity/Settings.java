package com.elementstore.imazepuzzle.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.dialogs.SliderColorBoxDialog;
import com.elementstore.imazepuzzle.services.SliderBoxTheme;
import com.elementstore.imazepuzzle.services.SoundAndVibration;

public class Settings extends AppCompatActivity {

    ConstraintLayout vibrationSwitch, soundSwitch, controllerSwitch, sliderColorSwitch;
    TextView vibrationText, soundText, controllerText, sliderColorView;
    TextView moreGameButton, youtubeLinkButton, rateThisGameButton;

    SoundAndVibration soundAndVibration;
    SliderBoxTheme sliderBoxTheme;
    SliderColorBoxDialog sliderColorBoxDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setStatusBarColor(getColor(R.color.deepCyan));

        vibrationSwitch = findViewById(R.id.vibrationSwitchSetting);
        soundSwitch = findViewById(R.id.soundSwitchSetting);
        controllerSwitch = findViewById(R.id.controllerSwitchSetting);
        sliderColorSwitch = findViewById(R.id.sliderColorSwitchSetting);
        vibrationText = findViewById(R.id.vibrationTextSetting);
        soundText = findViewById(R.id.soundTextSetting);
        controllerText = findViewById(R.id.controllerTextSetting);
        sliderColorView = findViewById(R.id.sliderColorViewSetting);

        moreGameButton = findViewById(R.id.moreGamesSetting);
        youtubeLinkButton = findViewById(R.id.youtubeLinkSetting);
        rateThisGameButton = findViewById(R.id.rateThisGameSetting);

        soundAndVibration = new SoundAndVibration(this);
        sliderBoxTheme = new SliderBoxTheme(this);
        sliderColorBoxDialog = new SliderColorBoxDialog(this);

        changeVibrationText(soundAndVibration.isCanVibrate());
        changeSoundText(soundAndVibration.isCanSound());
        changeControllerText(soundAndVibration.getControllerType());

        vibrationSwitch.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            if (soundAndVibration.isCanVibrate()){
                soundAndVibration.setCanVibrate(false);
            }else {
                soundAndVibration.setCanVibrate(true);
            }
            changeVibrationText(soundAndVibration.isCanVibrate());
        });

        soundSwitch.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            if (soundAndVibration.isCanSound()){
                soundAndVibration.setCanSound(false);
            }else {
                soundAndVibration.setCanSound(true);
            }
            changeSoundText(soundAndVibration.isCanSound());
        });

        controllerSwitch.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            soundAndVibration.changeControllerType();
            changeControllerText(soundAndVibration.getControllerType());

        });

        sliderColorSwitch.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            sliderColorBoxDialog.openDialog();
        });


        rateThisGameButton.setOnClickListener(view1 -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            Uri link = Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
            System.out.println(link);
            startActivity(new Intent(Intent.ACTION_VIEW, link));
        });

        moreGameButton.setOnClickListener(view1 -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            Uri link = Uri.parse("https://play.google.com/store/apps/dev?id=7918701498462053188");
            System.out.println(link);
            startActivity(new Intent(Intent.ACTION_VIEW, link));
        });

        youtubeLinkButton.setOnClickListener(view1 -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            Uri link = Uri.parse("https://www.youtube.com/channel/UCdwrABK9efyOeAYYX71mtJg");
            System.out.println(link);
            startActivity(new Intent(Intent.ACTION_VIEW, link));
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            sliderColorView.setBackgroundTintList(ColorStateList.valueOf(sliderBoxTheme.getActiveThemeColor()));
        }

    }

    @SuppressLint("SetTextI18n")
    private void changeVibrationText(boolean state){
        if (state){
            vibrationText.setText("ON");
            vibrationText.setBackgroundTintList(getColorStateList(R.color.mainCyan));
        }else {
            vibrationText.setText("off");
            vibrationText.setBackgroundTintList(getColorStateList(R.color.darkCyan));
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeSoundText(boolean state){
        if (state){
            soundText.setText("ON");
            soundText.setBackgroundTintList(getColorStateList(R.color.mainCyan));
        }else {
            soundText.setText("off");
            soundText.setBackgroundTintList(getColorStateList(R.color.darkCyan));
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeControllerText(boolean state){
        if (state){
            controllerText.setText("Button");
        }else {
            controllerText.setText("swipe");
        }
    }
}