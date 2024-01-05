package com.elementstore.imazepuzzle.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.ShrinkViewEffect;
import com.elementstore.imazepuzzle.services.SliderBoxTheme;
import com.elementstore.imazepuzzle.services.SoundAndVibration;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ConstraintLayout controllerSwitch, sliderBoxSwitch, soundSwitch, vibrationSwitch, youtubeButton;
    TextView controllerTextView, sliderColorView, soundIconView, vibrationIconView;
    SoundAndVibration soundAndVibration;
    SliderBoxTheme sliderBoxTheme;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_settings, container, false);
        Context context = getContext();

        controllerSwitch = root.findViewById(R.id.controllerSwitchKeySetting);
        sliderBoxSwitch = root.findViewById(R.id.sliderBoxSwitchKeySetting);
        soundSwitch = root.findViewById(R.id.soundSwitchKeySetting);
        vibrationSwitch = root.findViewById(R.id.vibrationSwitchKeySetting);
        youtubeButton = root.findViewById(R.id.youtubeKeySetting);

        controllerTextView = root.findViewById(R.id.controllerSwitchTextView);
        sliderColorView = root.findViewById(R.id.sliderBoxColorView);
        soundIconView = root.findViewById(R.id.soundSwitchIconView);
        vibrationIconView = root.findViewById(R.id.vibrationSwitchIconView);


        new ShrinkViewEffect(controllerSwitch);
        new ShrinkViewEffect(sliderBoxSwitch);
        new ShrinkViewEffect(soundSwitch);
        new ShrinkViewEffect(vibrationSwitch);
        new ShrinkViewEffect(youtubeButton);
        soundAndVibration = new SoundAndVibration(context);
        sliderBoxTheme = new SliderBoxTheme(context);

        updateControllerSwitchView(soundAndVibration.getControllerType());
        controllerSwitch.setOnClickListener(item -> {
            soundAndVibration.changeControllerType();
            updateControllerSwitchView(soundAndVibration.getControllerType());
            soundAndVibration.playNormalClickSound();
            soundAndVibration.doClickVibration();
        });


        sliderColorView.setBackgroundTintList(context.getColorStateList(sliderBoxTheme.getActiveBoxColor()));
        sliderBoxSwitch.setOnClickListener(item -> {
            sliderBoxTheme.changeActiveBoxColor();
            sliderColorView.setBackgroundTintList(context.getColorStateList(sliderBoxTheme.getActiveBoxColor()));
            soundAndVibration.playNormalClickSound();
            soundAndVibration.doClickVibration();
        });

        updateSoundSwitchView(context, soundAndVibration.isCanSound());
        soundSwitch.setOnClickListener(item -> {
            if (soundAndVibration.isCanSound()){
                soundAndVibration.setCanSound(false);
                updateSoundSwitchView(context, false);
            }else {
                soundAndVibration.setCanSound(true);
                updateSoundSwitchView(context, true);
            }
            soundAndVibration.playNormalClickSound();
            soundAndVibration.doClickVibration();
        });

        updateVibrationSwitchView(context, soundAndVibration.isCanVibrate());
        vibrationSwitch.setOnClickListener(item -> {
            if (soundAndVibration.isCanVibrate()){
                soundAndVibration.setCanVibrate(false);
                updateVibrationSwitchView(context, false);
            }else {
                soundAndVibration.setCanVibrate(true);
                updateVibrationSwitchView(context, true);
            }
            soundAndVibration.playNormalClickSound();
            soundAndVibration.doClickVibration();
        });

        youtubeButton.setOnClickListener(view1 -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            Uri link = Uri.parse("https://www.youtube.com/channel/UCdwrABK9efyOeAYYX71mtJg");
            startActivity(new Intent(Intent.ACTION_VIEW, link));
        });

        return root;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateSoundSwitchView(Context context, boolean state){
        if (state){
            soundIconView.setBackground(context.getDrawable(R.drawable.sound_max));
        }else {
            soundIconView.setBackground(context.getDrawable(R.drawable.sound_mute));
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateVibrationSwitchView(Context context, boolean state){
        if (state){
            vibrationIconView.setBackground(context.getDrawable(R.drawable.vibrate_on));
        }else {
            vibrationIconView.setBackground(context.getDrawable(R.drawable.vibrate_off));
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateControllerSwitchView(boolean type){
        if (type){
            controllerTextView.setText("Key");
        }else {
            controllerTextView.setText("Swipe");
        }
    }
}