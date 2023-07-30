package com.elementstore.imazepuzzle.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.elementstore.imazepuzzle.R;

public class SoundAndVibration {
    private String SOUND_VIBRATION = "SOUND_VIBRATION";
    private String SOUND = "SOUND";
    private String VIBRATION = "VIBRATION";
    private String CONTROLLER = "CONTROLLER";
    SharedPreferences soundAndVibrationDB;
    Context context;
    Vibrator vibrator;

    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayer1;
    SoundPool soundPool;
    AudioAttributes attrs;
    int addCoin, boxSlide, controllerClick, deductCoin, normalClick;

    public SoundAndVibration(Context context){
        this.context = context;
        vibrator = (Vibrator) this.context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        soundAndVibrationDB = context.getSharedPreferences(SOUND_VIBRATION, Context.MODE_PRIVATE);
        initializeSound();
    }

    public boolean isCanVibrate() {
        return soundAndVibrationDB.getBoolean(VIBRATION, true);
    }

    public void setCanVibrate(boolean canVibrate) {
        soundAndVibrationDB.edit().putBoolean(VIBRATION, canVibrate).apply();
    }

    public boolean isCanSound() {
        return soundAndVibrationDB.getBoolean(SOUND, true);
    }

    public void setCanSound(boolean canSound) {
        soundAndVibrationDB.edit().putBoolean(SOUND, canSound).apply();
    }

    public void doClickVibration(){
        if (isCanVibrate()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            }else {
                vibrator.vibrate(50);
            }
        }
    }

    public void doErrorVibration(){
        if (isCanVibrate()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }else {
                vibrator.vibrate(200);
            }
        }
    }

    public void changeControllerType(){
        if (getControllerType())
            soundAndVibrationDB.edit().putBoolean(CONTROLLER, false).apply();
        else
            soundAndVibrationDB.edit().putBoolean(CONTROLLER, true).apply();
    }
    public boolean getControllerType(){
        return soundAndVibrationDB.getBoolean(CONTROLLER, true);
    }

    private void initializeSound(){
         mediaPlayer = MediaPlayer.create(context, R.raw.add_coins);
         mediaPlayer1 = MediaPlayer.create(context, R.raw.controller_click);

        attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attrs)
                .build();
        addCoin = soundPool.load(context, R.raw.add_coins, 1);
        boxSlide = soundPool.load(context, R.raw.box_sliding, 1);
        controllerClick = soundPool.load(context, R.raw.controller_click, 1);
        deductCoin = soundPool.load(context, R.raw.deduct_coins, 1);
        normalClick = soundPool.load(context, R.raw.normal_click, 1);
        soundPool.autoPause();
    }

    public void playAddCoinSound(){
        if (isCanSound()) {
            soundPool.play(addCoin, 1, 1, 0, 0, 1);
        }
    }

    public void playBoxSlideSound(){
        if (isCanSound()) {
            soundPool.play(boxSlide, 0.1F, 0.1F, 0, 0, 1.2F);
        }
    }

    public void playControllerClickSound(){
        if (isCanSound()) {
            soundPool.play(controllerClick, 1, 1, 0, 0, 1);
        }
    }

    public void playDeductCoinSound(){
        if (isCanSound()) {
            soundPool.play(deductCoin, 1, 1, 0, 0, 1);
        }
    }

    public void playNormalClickSound(){
        if (isCanSound()) {
            soundPool.play(normalClick, 1, 1, 0, 0, 1);
        }
    }
}