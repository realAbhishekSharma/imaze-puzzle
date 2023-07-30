package com.elementstore.imazepuzzle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.SoundAndVibration;

public class ControllerKeyboard {

    TextView clickUp, clickDown, clickLeft, clickRight;
    KeyPressedListener keyPressedListener;
    Context context;
    View root;
    private SoundAndVibration soundAndVibration;
    public ControllerKeyboard(Context context, View root, KeyPressedListener keyPressedListener){
        this.context = context;
        this.keyPressedListener = keyPressedListener;
        this.root = root;
        soundAndVibration = new SoundAndVibration(context);
        initializeView();
    }

    public void initializeView(){
        clickLeft = root.findViewById(R.id.clickLeftKey);
        clickRight = root.findViewById(R.id.clickRightKey);
        clickUp = root.findViewById(R.id.clickUpKey);
        clickDown = root.findViewById(R.id.clickDownKey);

        clickLeft.setOnClickListener(view -> {
            keyPressedListener.onLeftKey(view);
            soundAndVibration.playControllerClickSound();
        });

        clickRight.setOnClickListener(view -> {
            keyPressedListener.onRightKey(view);
            soundAndVibration.playControllerClickSound();
        });

        clickUp.setOnClickListener(view -> {
            keyPressedListener.onUpKey(view);
            soundAndVibration.playControllerClickSound();
        });

        clickDown.setOnClickListener(view -> {
            keyPressedListener.onDownKey(view);
            soundAndVibration.playControllerClickSound();
        });

    }


    public interface KeyPressedListener{
        void onLeftKey(View view);
        void onRightKey(View view);
        void onUpKey(View view);
        void onDownKey(View view);
    }
}
