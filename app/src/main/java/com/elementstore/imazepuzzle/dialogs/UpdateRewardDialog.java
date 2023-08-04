package com.elementstore.imazepuzzle.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.elementstore.imazepuzzle.BuildConfig;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.Coins;

public class UpdateRewardDialog {

    private AlertDialog.Builder dialogBox;
    private AlertDialog alertDialog;
    private View root;
    private Context context;
    private SharedPreferences sharedPreferences;

    private final String UPDATE_REWARD = "UPDATE_REWARD";
    private final String UPDATE = "REWARD "+BuildConfig.VERSION_NAME;

    private TextView okayButton;
    private Coins coins;
    private final int rewardCoins = 50000;

    public UpdateRewardDialog(Context context){
        this.context = context;
        this.coins = new Coins(context);
        this.sharedPreferences = context.getSharedPreferences( UPDATE_REWARD, Context.MODE_PRIVATE);
        initialize();
    }

    private void initialize(){

        dialogBox = new AlertDialog.Builder(this.context);
        root = View.inflate(context, R.layout.update_reward_dialog, null);
        dialogBox.setView(root);

        okayButton = root.findViewById(R.id.okayUpdateRewardBox);

        okayButton.setOnClickListener(view -> {
            setUpdateRewardCollected();
            coins.addCoin(rewardCoins);
            alertDialog.dismiss();
        });


        alertDialog = dialogBox.create();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    public void openDialogBox(){
        try {
            if (!isUpdateRewardCollected()) {
                alertDialog.show();
            }
        }catch (Exception e){

        }
    }

    private void setUpdateRewardCollected(){
        sharedPreferences.edit().putBoolean(UPDATE, true).apply();
    }

    private boolean isUpdateRewardCollected(){
        return sharedPreferences.getBoolean(UPDATE, false);
    }


}
