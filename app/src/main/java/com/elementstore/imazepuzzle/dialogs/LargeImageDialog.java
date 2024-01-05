package com.elementstore.imazepuzzle.dialogs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.SoundAndVibration;
public class LargeImageDialog {
    Context context;
    AlertDialog.Builder dialogBox;
    AlertDialog alertDialog;
    View root;

    TextView keyboardStatusView;
    ConstraintLayout keyboardSwitch;
    ImageView imagePreview;

    SoundAndVibration soundAndVibration;
    boolean keyState = false;
    public LargeImageDialog(Context context){
        this.context = context;
        soundAndVibration = new SoundAndVibration(context);
        initializeDialog();
    }

    private void initializeDialog(){

        dialogBox = new AlertDialog.Builder(this.context);
        root = View.inflate(context, R.layout.large_image_dialog, null);
        dialogBox.setView(root);

        keyboardSwitch = root.findViewById(R.id.controllerSwitchKeyMainGame);
        keyboardStatusView = root.findViewById(R.id.controllerSwitchTextViewMainGame);
        imagePreview = root.findViewById(R.id.largeImagePreviewMainGame);

        keyboardSwitch.setOnClickListener(view -> {
            soundAndVibration.changeControllerType();
            soundAndVibration.playNormalClickSound();
            if (keyState) {
                updateKeyboard();
                soundAndVibration.doClickVibration();
            }else {
                soundAndVibration.doErrorVibration();
                Toast.makeText(context, "Not Supported.", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog = dialogBox.create();
        alertDialog.setCanceledOnTouchOutside(true);


    }
    public void openLargeImage(){
        alertDialog.show();
    }

    public void setImagePreviewImage(Bitmap bitmap){
        imagePreview.setImageBitmap(bitmap);
    }
    public void setImagePreviewImage(Drawable drawable){
        imagePreview.setBackground(drawable);
    }

    public void setKeyboardStatusText(boolean isEnable){
        keyState = isEnable;
        if (isEnable){
            updateKeyboard();
        }else {
            keyboardStatusView.setText("Swipe");
        }

    }

    private void updateKeyboard(){
        if (soundAndVibration.getControllerType()){
            keyboardStatusView.setText("Button");
        }else {
            keyboardStatusView.setText("Swipe");
        }
    }

}
