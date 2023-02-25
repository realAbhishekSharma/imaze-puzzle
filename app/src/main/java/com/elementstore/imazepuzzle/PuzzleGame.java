package com.elementstore.imazepuzzle;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class PuzzleGame extends AppCompatActivity {


    long timeInSec=100;
    int correctCount = 0;
    int totalScore = 0;
    CountDownTimer countDownTimer;
    int[][] imagePosition = {
            {R.id.oneOne, R.id.oneTwo, R.id.oneThree},
            {R.id.twoOne, R.id.twoTwo, R.id.twoThree},
            {R.id.threeOne, R.id.threeTwo, R.id.threeThree},
    };

    int[][] carImage = {
            {R.drawable.car1, R.drawable.car2, R.drawable.car3},
            {R.drawable.car4, R.drawable.car5, R.drawable.car6},
            {R.drawable.car7, R.drawable.car8, R.drawable.car9},
    };



    int [][] animalImage = {
            {R.drawable.animal1, R.drawable.animal2, R.drawable.animal3},
            {R.drawable.animal4, R.drawable.animal5, R.drawable.animal6},
            {R.drawable.animal7, R.drawable.animal8, R.drawable.animal9},
    };


    int[][] enemyImage = {
            {R.drawable.enemy1, R.drawable.enemy2, R.drawable.enemy3},
            {R.drawable.enemy4, R.drawable.enemy5, R.drawable.enemy6},
            {R.drawable.enemy7, R.drawable.enemy8, R.drawable.enemy9},
    };

    int[][] spaceImage = {
            {R.drawable.space1, R.drawable.space2, R.drawable.space3},
            {R.drawable.space4, R.drawable.space5, R.drawable.space6},
            {R.drawable.space7, R.drawable.space8, R.drawable.space9},
    };
    int [][] fruitImage = {
            {R.drawable.fruit1, R.drawable.fruit2, R.drawable.fruit3},
            {R.drawable.fruit4, R.drawable.fruit5, R.drawable.fruit6},
            {R.drawable.fruit7, R.drawable.fruit8, R.drawable.fruit9},
    };

    int[] emptyBoxPosition = {-1,-1};

    TextView[][] imageBox = new TextView[3][3];
    TextView originalImage,timer, emptyBox;
    TextView clickUp, clickDown, clickLeft, clickRight;

    ImageBoxPosition[][] imageBoxPositions = new ImageBoxPosition[3][3];


    AdRequest adRequest = new AdRequest.Builder().build();

    RewardedAd timeRewardAds;

    AdView mAdView;

    Score score;
    ImageLevel imageLevel;
    boolean isAdsPlayed, won;

    String activity;

    @Override
    public void onBackPressed() {
        goHome();
        countDownTimer.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_game);

        score = new Score(this);
        imageLevel = new ImageLevel(this);

        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.adView);
        loadGameBannerAds();

        originalImage = findViewById(R.id.originalImageShow);

        timer = findViewById(R.id.timer);
        emptyBox = findViewById(R.id.emptyBox);

        clickUp = findViewById(R.id.clickUp);
        clickDown = findViewById(R.id.clickDown);
        clickLeft = findViewById(R.id.clickLeft);
        clickRight = findViewById(R.id.clickRight);

        try {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    imageBox[x][y] = findViewById(imagePosition[x][y]);

                    setSmallImageBoxBackground(x,y);

                    ImageBoxPosition i = new ImageBoxPosition();
                    i.setX(x);
                    i.setY(y);
                    imageBoxPositions[x][y] = i;
/*
                    TextView temp = imageBox[x][y];

                    imageBox[x][y].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), temp.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
            }
        }catch (Exception exception){
            System.out.println(exception);
        }

        new Handler().postDelayed(() -> {
            setImageBoxRandomPosition();
            startTimer();
        },200);



        clickDown.setOnClickListener(view -> swipeUp());

        clickUp.setOnClickListener(view -> swipeDown());

        clickLeft.setOnClickListener(view -> swipeRight());

        clickRight.setOnClickListener(view -> swipeLeft());


        loadTimeRewardAds();


    }


    private void swipeUp(){
        if (emptyBoxPosition[0] == -1 && emptyBoxPosition[1]==-1){

            float x = emptyBox.getX();
            float y = emptyBox.getY();

            System.out.println(x+" "+y);
            emptyBox.setX(imageBox[0][2].getX());
            emptyBox.setY(imageBox[0][2].getY());

            imageBox[0][2].setX(x);
            imageBox[0][2].setY(y);

            emptyBoxPosition[0] = 0;
            emptyBoxPosition[1] = 2;

            imageBoxPositions[0][2].setX(0);
            imageBoxPositions[0][2].setY(2);
//            checkImage();

        }else if (emptyBoxPosition[0] == 0 || emptyBoxPosition[0] == 1){
            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];

            swapImageAndEmptyBox( xx+1,yy);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]);
        }

    }

    private void swipeDown(){
        if (emptyBoxPosition[0] == 0 && emptyBoxPosition[1]==2){

            float x = emptyBox.getX();
            float y = emptyBox.getY();

            emptyBox.setX(imageBox[0][2].getX());
            emptyBox.setY(imageBox[0][2].getY());

            imageBox[0][2].setX(x);
            imageBox[0][2].setY(y);

            emptyBoxPosition[0] = -1;
            emptyBoxPosition[1] = -1;


            imageBoxPositions[0][2].setX(0);
            imageBoxPositions[0][2].setY(2);
//            checkImage();
            checkGameWon();

        }else if (emptyBoxPosition[0] == 1 || emptyBoxPosition[0] == 2){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx-1,yy);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeLeft(){
        if (emptyBoxPosition[1] == 0 || emptyBoxPosition[1] == 1){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx,yy+1);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeRight(){
        if (emptyBoxPosition[1] == 1 || emptyBoxPosition[1] == 2){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx,yy-1);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);

        }
    }

    private void swapImageAndEmptyBox(int i, int j){
        float x = emptyBox.getX();
        float y = emptyBox.getY();

        System.out.println(x+" "+y);
        emptyBox.setX(imageBox[imageBoxPositions[i][j].getX()][imageBoxPositions[i][j].getY()].getX());
        emptyBox.setY(imageBox[imageBoxPositions[i][j].getX()][imageBoxPositions[i][j].getY()].getY());


        imageBox[imageBoxPositions[i][j].getX()][imageBoxPositions[i][j].getY()].setX(x);
        imageBox[imageBoxPositions[i][j].getX()][imageBoxPositions[i][j].getY()].setY(y);

        emptyBoxPosition[0] = i;
        emptyBoxPosition[1] = j;



//        System.out.println("image Box "+imageBoxPositions[i][j].getX()+" "+imageBoxPositions[i][j].getY());
//        System.out.println("empty Box "+emptyBox.getX()+" "+emptyBox.getY());
//        System.out.println("image "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
//        checkImage();

    }

    private void checkImageAnswerPosition(){
        correctCount =0;
        for (int x =0; x<3; x++){
            for (int y = 0; y<3; y++){
                if (imageBoxPositions[x][y].getX() == x && imageBoxPositions[x][y].getY() == y){
                    correctCount++;
//                    System.out.println(correctCount);
                    if (correctCount == 9){
                        Toast.makeText(getApplicationContext(), "You won.",Toast.LENGTH_SHORT).show();
                        won = true;
                    }
                    totalScore = score.generateScore(correctCount);
                    score.setScore(totalScore);

                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setSmallImageBoxBackground(int x, int y){
        switch (activity){
            case "car":{
                originalImage.setBackground(getDrawable(R.drawable.car));
                imageBox[x][y].setBackground(getDrawable(carImage[x][y]));
                imageBox[x][y].setText("");
            }
            break;
            case "animal":{
                originalImage.setBackground(getDrawable(R.drawable.animal));
                imageBox[x][y].setBackground(getDrawable(animalImage[x][y]));
                imageBox[x][y].setText("");
            }
            break;
            case "enemy":{
                originalImage.setBackground(getDrawable(R.drawable.enemy));
                imageBox[x][y].setBackground(getDrawable(enemyImage[x][y]));
                imageBox[x][y].setText("");
            }
            break;
            case "space":{
                originalImage.setBackground(getDrawable(R.drawable.space));
                imageBox[x][y].setBackground(getDrawable(spaceImage[x][y]));
                imageBox[x][y].setText("");
            }
            break;
            case "fruit":{
                originalImage.setBackground(getDrawable(R.drawable.fruit));
                imageBox[x][y].setBackground(getDrawable(fruitImage[x][y]));
                imageBox[x][y].setText("");
            }
            break;
        }
    }

    private void setImageBoxRandomPosition(){
        swipeUp();
        for (int x =0; x<3; x++){
            swipeUp();
            swipeUp();
            swipeRight();
            swipeRight();
            swipeDown();
            swipeDown();
            swipeLeft();
            swipeLeft();

        }
        swipeUp();
        swipeRight();
        for (int p = 0; p<20;p++){
            int random = (int)( Math.random()*10)%4;
            System.out.println(".                   random "+random);
            if (random == 0) {
                swipeRight();
                swipeDown();
                swipeLeft();
                swipeUp();
            }else if (random == 1){
                swipeDown();
                swipeLeft();
                swipeUp();
                swipeRight();
            }else if (random == 2){
                swipeUp();
                swipeRight();
                swipeDown();
                swipeLeft();

            }else if (random == 3){
                swipeUp();
                swipeLeft();
                swipeDown();
                swipeRight();

            }
        }
        swipeLeft();
        swipeDown();
        swipeDown();
    }

    //Opening the time over box to show ads or collect the coin earned by user
    @SuppressLint("SetTextI18n")
    private void openDialogBox(){

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(PuzzleGame.this);
        View view = getLayoutInflater().inflate(R.layout.time_over_dialog, null);

        TextView titleShow,earnedScore,goHomeButton, addTime;

        titleShow = view.findViewById(R.id.titleShow);
        earnedScore = view.findViewById(R.id.earnedScore);
        goHomeButton = view.findViewById(R.id.goHomeButton);
        addTime = view.findViewById(R.id.timeIncrease);

//        GradientDrawable drawable = (GradientDrawable)goHomeButton.getBackground();
//        drawable.mutate(); // only change this instance of the xml, not all components using this xml
//        drawable.setStroke(7, getResources().getColor(R.color.red));


        if (won){
            addTime.setVisibility(View.GONE);
            goHomeButton.setText("Go Home");
            titleShow.setText("You Won");
            titleShow.setTextColor(getColor(R.color.cyan));
        }

        dialogBox.setView(view);

        final AlertDialog alertDialog = dialogBox.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.setCancelable(false);


        earnedScore.setText(totalScore+"");

        addTime.setOnClickListener(view1 -> {
            if (timeRewardAds != null) {
                Activity activityContext = PuzzleGame.this;
                timeRewardAds.show(activityContext, rewardItem -> {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
//                    int rewardAmount = rewardItem.getAmount();
//                    String rewardType = rewardItem.getType();
                    alertDialog.dismiss();
                    loadTimeRewardAds();
                });
            } else {
                Toast.makeText(getApplicationContext(), "Time Reward is Not Ready", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "The rewarded ad wasn't ready yet.");
            }


        });

        goHomeButton.setOnClickListener(view12 -> {
            if (won){
                score.setScore(totalScore);
                countDownTimer.cancel();
                updateImageActiveLevel();
                goHome();
            }else {
//                timeInSec =5;
//                countDownTimer.start();
                goHome();
                countDownTimer.cancel();

            }
            alertDialog.dismiss();



        });

    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(timeInSec*1000,1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                timer.setText((int)l/1000+"");
            }

            @Override
            public void onFinish() {
                timer.setText("0");
                onTimeOver();
            }
        }.start();
    }

    private void onTimeOver(){
        checkImageAnswerPosition();
        openDialogBox();
        countDownTimer.cancel();

    }

    private void checkGameWon(){
        checkImageAnswerPosition();
        if (won){
            openDialogBox();
            countDownTimer.cancel();
        }
    }

    private void timeIncrease(){
        if (isAdsPlayed){
            loadTimeRewardAds();
            timeInSec = 60;
            startTimer();
        }
    }

    private void goHome(){
        startActivity(new Intent(getApplicationContext(), GameHome.class));
        finish();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateImageActiveLevel(){
        switch (activity){

            case "car":{
                imageLevel.setActiveLevel(1);
            }
            break;
            case "animal":{
                imageLevel.setActiveLevel(2);
            }
            break;
            case "enemy":{
                imageLevel.setActiveLevel(3);
            }
            break;
            case "space":{
                imageLevel.setActiveLevel(4);
            }
            break;
        }
    }

    private void loadTimeRewardAds(){

        // Timer ads reload on every time over box
        RewardedAd.load(this, getResources().getString(R.string.addTimeRewardAds), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                Log.d(TAG, "here: "+loadAdError.toString());
                timeRewardAds = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                timeRewardAds = rewardedAd;

                timeRewardAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d(TAG, "Ad was clicked.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d(TAG, "Ad dismissed fullscreen content.");
                        isAdsPlayed = true;
                        timeRewardAds = null;
                        timeIncrease();
                        loadGameBannerAds();
//                                Toast.makeText(getApplicationContext(), "done dishmissed ads", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when ad fails to show.
                        Log.e(TAG, "Ad failed to show fullscreen content.");
                        timeRewardAds = null;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d(TAG, "Ad recorded an impression.");
//                        Toast.makeText(getApplicationContext(), "impression full ads", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad showed fullscreen content.");
//                        Toast.makeText(getApplicationContext(), "ads showed full ads", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void loadGameBannerAds(){
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                System.out.println("here failed.");
//                loadGameBannerAds();
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });
        mAdView.loadAd(adRequest);
    }
}