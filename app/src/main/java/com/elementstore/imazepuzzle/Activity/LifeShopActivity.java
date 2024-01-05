package com.elementstore.imazepuzzle.Activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elementstore.imazepuzzle.Adapter.LifeShopAdapter;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.dialogs.ConfirmDialog;
import com.elementstore.imazepuzzle.services.Coins;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class LifeShopActivity extends AppCompatActivity {

    JSONArray lifeShopData;
    LifeShopAdapter lifeShopAdapter;
    GridView lifeShopGridView;

    TextView lifeView, lifeTextView;
    PlayerLife playerLife;
    Coins coins;

    AdRequest adRequest = new AdRequest.Builder().build();

    RewardedAd lifeRewardAds;

    ConfirmDialog confirmDialog;
    SoundAndVibration soundAndVibration;

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
        setContentView(R.layout.activity_life_shop_activity);

        try {
            lifeShopData = getLifeShopData();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }


        lifeShopGridView = findViewById(R.id.lifeGridView);
        lifeView = findViewById(R.id.lifeViewLifeShop);
        lifeTextView = findViewById(R.id.lifeTextLifeShop);

        lifeShopAdapter = new LifeShopAdapter(this, lifeShopData);
        lifeShopGridView.setAdapter(lifeShopAdapter);

        soundAndVibration = new SoundAndVibration(this);
        playerLife = new PlayerLife(this);
        coins = new Coins(this);
        loadLifeReward();

        confirmDialog = new ConfirmDialog(this, new ConfirmDialog.ConfirmBoxItemClick() {
            @Override
            public void onOkayClick(View view, int index) {
                soundAndVibration.doClickVibration();
                soundAndVibration.playNormalClickSound();
                confirmPurchase(index);
//                Toast.makeText(getApplicationContext(), index+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClick(View view, int index) {
                soundAndVibration.doClickVibration();
                soundAndVibration.playNormalClickSound();

            }
        });

        lifeShopGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                soundAndVibration.doClickVibration();
                soundAndVibration.playNormalClickSound();
                if (i == 0){
                    confirmPurchase(i);
                }else {
                    confirmDialog.openConfirmBox(i);
                }
            }
        });

    }

    private void confirmPurchase(int i){
        if (i == 0){
            openLifeAds();
        }else{
            int price =0;
            try {
                price = lifeShopData.getJSONObject(i).getInt("value");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            if (price < coins.getTotalCoins()) {

                try {
                    if (lifeShopData.getJSONObject(i).has("timeInSec")){
                        playerLife.setActiveTimeLimitOfInfiniteTime(lifeShopData.getJSONObject(i).getInt("timeInSec"));
                        coins.cutCoin(price);
                    }else if (lifeShopData.getJSONObject(i).getInt("life") == 5){
                        playerLife.increaseToMaximumLife();
                        coins.cutCoin(price);
                    }else  if (lifeShopData.getJSONObject(i).getInt("life") == 1){
                        if (playerLife.getTotalLife()<5 || !playerLife.isActiveInfiniteLife()) {
                            playerLife.increaseLife();
                            updateLifeDetails();
                            coins.cutCoin(price);
                        }else {
                            Toast.makeText(getApplicationContext(), "Life is full.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }else {
                Toast.makeText(getApplicationContext(), "can't buy", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private JSONArray getLifeShopData() throws IOException, JSONException {
        JSONObject data;

        InputStream inputStream = getAssets().open("lives.json");
        Scanner scanner = new Scanner(inputStream).useDelimiter("//A");
        String jsonString = scanner.hasNext()? scanner.next():"";
        scanner.close();
        data = new JSONObject(jsonString);
        return data.getJSONArray("lifeOption");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            updateLifeDetails();
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateLifeDetails(){
        if (playerLife.isActiveInfiniteLife()){
            lifeView.setText(getString(R.string.infinity));
            lifeView.setTypeface(Typeface.DEFAULT_BOLD);
            if (playerLife.getRemainInfiniteTime()/60 == 0) {
                lifeTextView.setText("< 1min Remaining");
            }else {
                lifeTextView.setText(playerLife.getRemainInfiniteTime()/60+"min+ Remaining");
            }
        }else {
            lifeView.setText(playerLife.getTotalLife()+"");
            lifeTextView.setText("Life Remaining");
        }
    }

    private void openLifeAds(){
        if (lifeRewardAds != null) {
            lifeRewardAds.show(LifeShopActivity.this, rewardItem -> {
                addLifeFromAds();
            });
        } else {
            Toast.makeText(getApplicationContext(), "Try Again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLifeReward(){

        // Timer ads reload on every time over box
        RewardedAd.load(this, getResources().getString(R.string.addLifeReward), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                Log.d(TAG, "here: "+loadAdError);
                lifeRewardAds = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                lifeRewardAds = rewardedAd;

                lifeRewardAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
//                        lifeRewardAds = null;

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        lifeRewardAds = null;
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

    private void addLifeFromAds(){
        playerLife.increaseLife();
        Toast.makeText(this, "Life Increased.", Toast.LENGTH_SHORT).show();
    }
}