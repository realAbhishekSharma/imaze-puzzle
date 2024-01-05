package com.elementstore.imazepuzzle.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.elementstore.imazepuzzle.Adapter.MissionViewAdapter;
import com.elementstore.imazepuzzle.ImageLevel;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.dialogs.ConfirmDialog;
import com.elementstore.imazepuzzle.services.Coins;
import com.elementstore.imazepuzzle.services.Mission;
import com.elementstore.imazepuzzle.services.PlayerLife;
import com.elementstore.imazepuzzle.services.SoundAndVibration;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MissionActivity extends AppCompatActivity {

    TextView dailyMissionSelection, oneTimeMissionSelection, dailyTextView, oneTimeTextView;
    SoundAndVibration soundAndVibration;
    RecyclerView missionsRecyclerView;
    MissionViewAdapter missionViewAdapter;
    Mission mission;
    JSONArray dailyMissionArray;
    JSONArray oneTimeMissionArray;
    ImageLevel imageLevel;
    ConfirmDialog confirmDialog;
    Coins coins;
    PlayerLife playerLife;
    boolean isOneTime = false;
    AdRequest adRequest = new AdRequest.Builder().build();

    RewardedAd coinsRewardAds;

    ConstraintLayout loadingScreenView;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), GameHome.class));
        overridePendingTransition(0, 0);
        finish();
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.cloudCyan));
        setContentView(R.layout.activity_missions);

        mission = new Mission(this);
        playerLife = new PlayerLife(this);
        coins = new Coins(this);
        soundAndVibration = new SoundAndVibration(this);
        imageLevel = new ImageLevel(this);

        dailyMissionSelection = findViewById(R.id.dailyMissionsSelection);
        oneTimeMissionSelection = findViewById(R.id.oneTimeMissionsSelection);
        dailyTextView = findViewById(R.id.dailyTextView);
        oneTimeTextView = findViewById(R.id.oneTimeTextView);

        missionsRecyclerView = findViewById(R.id.missionRecyclerView);

        missionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        changeSelectionBackground(true);

        try {
            dailyMissionArray = mission.getActiveDailyMissions();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        oneTimeMissionArray = mission.getOneTimeMissions();

        changeMissionAdapter(dailyMissionArray, isOneTime);
        loadCoinsReward();
        dailyMissionSelection.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            changeSelectionBackground(true);
            isOneTime = false;
            changeMissionAdapter(dailyMissionArray, false);
        });

        oneTimeMissionSelection.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            changeSelectionBackground(false);
            isOneTime = true;
            changeMissionAdapter(oneTimeMissionArray, true);
        });

        confirmDialog = new ConfirmDialog(this, new ConfirmDialog.ConfirmBoxItemClick() {
            @Override
            public void onOkayClick(View view, int index) {
                soundAndVibration.doClickVibration();
                soundAndVibration.playNormalClickSound();
//                Toast.makeText(getApplicationContext(), "here"+index, Toast.LENGTH_SHORT).show();
                collectConfirm(index);
            }

            @Override
            public void onCancelClick(View view, int index) {
                soundAndVibration.doClickVibration();
                soundAndVibration.playNormalClickSound();
            }
        });
    }

    private void changeSelectionBackground(boolean type){
        if (type){
            dailyMissionSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkBlue)));
            oneTimeMissionSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.cloudCyan)));
            dailyTextView.setTextColor(getColor(R.color.white));
            oneTimeTextView.setTextColor(getColor(R.color.darkBlue));
        }else{
            dailyMissionSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.cloudCyan)));
            oneTimeMissionSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkBlue)));
            dailyTextView.setTextColor(getColor(R.color.darkBlue));
            oneTimeTextView.setTextColor(getColor(R.color.white));
        }
    }

    private void changeMissionAdapter(JSONArray data, boolean isOneTime){

        missionViewAdapter = new MissionViewAdapter(this,isOneTime, data, new MissionViewAdapter.MissionItemClick() {
            @Override
            public void onMissionItemClick(JSONObject object, int index) {
                soundAndVibration.doClickVibration();
                soundAndVibration.playNormalClickSound();
                if (!isOneTime && index == 0) {
                    collectConfirm(index);
                } else {
                    confirmDialog.openConfirmBox(index);
                }
            }
        });
        missionsRecyclerView.setAdapter(missionViewAdapter);
    }

    private void collectConfirm(int index){
        try {
            String name = oneTimeMissionArray.getJSONObject(index).getString("name");
            if (isOneTime) {
                int value = oneTimeMissionArray.getJSONObject(index).getInt("value");
                mission.setOneTimeMissionIsCollected(name);
                coins.addCoin(value);
                changeMissionAdapter(oneTimeMissionArray, true);
            }else {
                if (index == 0){
                    openCoinAds();
                }else {
                    String key = dailyMissionArray.getJSONObject(index).getString("key");
                    int value = dailyMissionArray.getJSONObject(index).getInt("value");
                    if (key.equals("spendTime")) {
                        playerLife.setActiveTimeLimitOfInfiniteTime(dailyMissionArray.getJSONObject(index).getInt("time"));
                    }else {
                        coins.addCoin(value);
                    }
                    mission.setDailyMissionIsCollected(key);
                    changeMissionAdapter(dailyMissionArray, false);
                }
            }
        }catch (JSONException e){
        }
    }

    private void openCoinAds(){
        if (coinsRewardAds != null) {
            coinsRewardAds.show(MissionActivity.this, rewardItem -> {

            });
        } else {
            Toast.makeText(getApplicationContext(), "Try Again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCoinsReward(){
        RewardedAd.load(this, getResources().getString(R.string.addLifeReward), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                Log.d(TAG, "here: "+loadAdError);
                coinsRewardAds = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                coinsRewardAds = rewardedAd;

                coinsRewardAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        coinsRewardAds = null;
                        loadCoinsReward();
                        addCoinsFromAds();

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        coinsRewardAds = null;
                    }

                    @Override
                    public void onAdImpression() {

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                    }
                });

            }
        });
    }

    private void addCoinsFromAds(){
        int random = coins.getRandomCoins(1000,2000);
        coins.addCoin(random);
        Toast.makeText(this, random+" Coin Increased.", Toast.LENGTH_SHORT).show();
    }
}