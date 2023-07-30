package com.elementstore.imazepuzzle.dialogs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.SliderBoxTheme;
import com.elementstore.imazepuzzle.services.SoundAndVibration;

public class SliderColorBoxDialog {
    Context context;
    AlertDialog.Builder dialogBox;
    AlertDialog alertDialog;

    ConstraintLayout colorOne, colorTwo, colorThree;
    TextView okayButton, cancelButton;
    SliderBoxTheme sliderBoxTheme;

    View view;
    SoundAndVibration soundAndVibration;
    public SliderColorBoxDialog(Context context){
        this.context = context;
        sliderBoxTheme = new SliderBoxTheme(context);
        soundAndVibration = new SoundAndVibration(context);
        dialogBox = new AlertDialog.Builder(context);
        view = View.inflate(context, R.layout.slider_box_color_dialog, null);
        initializeDialog();
    }

    private void initializeDialog(){
        colorOne = view.findViewById(R.id.colorChoiceOne);
        colorTwo = view.findViewById(R.id.colorChoiceTwo);
        colorThree = view.findViewById(R.id.colorChoiceThree);

        okayButton = view.findViewById(R.id.okaySettingDialog);
        cancelButton = view.findViewById(R.id.cancelSettingDialog);
        dialogBox.setView(view);


        if (sliderBoxTheme.getActiveThemeColor() == context.getColor(R.color.cyan)){
            colorOne.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.cyan)));
            colorTwo.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            colorThree.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
        }else if (sliderBoxTheme.getActiveThemeColor() == context.getColor(R.color.green)){
            colorOne.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            colorTwo.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.green)));
            colorThree.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
        }else if (sliderBoxTheme.getActiveThemeColor() == context.getColor(R.color.red)){
            colorOne.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            colorTwo.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            colorThree.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
        }

        final int[] color = {sliderBoxTheme.getActiveThemeColor()};
        colorOne.setOnClickListener(item ->{
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            colorOne.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.cyan)));
            colorTwo.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            colorThree.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            color[0] = context.getColor(R.color.cyan);
        });

        colorTwo.setOnClickListener(item ->{
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            colorOne.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            colorTwo.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.green)));
            colorThree.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            color[0] = context.getColor(R.color.green);
        });

        colorThree.setOnClickListener(item ->{
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            colorOne.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            colorTwo.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
            colorThree.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
            color[0] = context.getColor(R.color.red);
        });


        okayButton.setOnClickListener(item ->{
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            sliderBoxTheme.setThemeColor(color[0]);
            alertDialog.cancel();
        });

        cancelButton.setOnClickListener(item -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            alertDialog.dismiss();
        });

        alertDialog = dialogBox.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
    }

    public void openDialog(){
        alertDialog.show();

    }
}
