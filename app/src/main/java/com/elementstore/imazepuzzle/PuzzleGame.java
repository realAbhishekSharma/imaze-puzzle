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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class PuzzleGame extends AppCompatActivity {

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

    RewardedAd timeRewardAds;

    Score score;
    boolean isAdsPlayed, won;

    long timeInSec=100;
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
        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        MobileAds.initialize(this, initializationStatus -> {
        });

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

                    setImageBackground(x,y);

                    ImageBoxPosition i = new ImageBoxPosition();
                    i.setX(x);
                    i.setY(y);
                    imageBoxPositions[x][y] = i;
//
//                    TextView temp = imageBox[x][y];
//
//                    imageBox[x][y].setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Toast.makeText(getApplicationContext(), temp.getText().toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            }
        }catch (Exception exception){
            System.out.println(exception);
        }

        new Handler().postDelayed(() -> {
            setImageBox();
            startTimer();
        },200);




        clickDown.setOnClickListener(view -> swipeUp());

        clickUp.setOnClickListener(view -> swipeDown());

        clickLeft.setOnClickListener(view -> swipeRight());

        clickRight.setOnClickListener(view -> swipeLeft());


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

            swapBox( xx+1,yy);

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
            timeOver();

        }else if (emptyBoxPosition[0] == 1 || emptyBoxPosition[0] == 2){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapBox( xx-1,yy);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeLeft(){
        if (emptyBoxPosition[1] == 0 || emptyBoxPosition[1] == 1){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapBox( xx,yy+1);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeRight(){
        if (emptyBoxPosition[1] == 1 || emptyBoxPosition[1] == 2){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapBox( xx,yy-1);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);

        }
    }

    private void swapBox(int i, int j){
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

    private void checkImage(){
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

                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImageBackground(int x, int y){
        switch (activity){
            case "animal":{
                originalImage.setBackground(getDrawable(R.drawable.animal));
                imageBox[x][y].setBackground(getDrawable(animalImage[x][y]));
                imageBox[x][y].setText("");
            }
            break;
            case "car":{
                originalImage.setBackground(getDrawable(R.drawable.car));
                imageBox[x][y].setBackground(getDrawable(carImage[x][y]));
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

    private void setImageBox(){
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
        for (int p = 0; p<10;p++){
            int random = (int)( Math.random()*10)%4;

            switch (random){
                case 0:{
                    swipeRight();
                    swipeDown();
                    swipeLeft();
                    swipeUp();
                }
                break;
                case 1:{
                    swipeDown();
                    swipeLeft();
                    swipeUp();
                    swipeRight();
                }
                break;
                case 2:{
                    swipeUp();
                    swipeRight();
                    swipeDown();
                    swipeLeft();

                }
                break;
                case 3:{
                    swipeUp();
                    swipeLeft();
                    swipeDown();
                    swipeRight();

                }
                break;
            }

            swipeLeft();
            swipeLeft();
            swipeDown();
            swipeDown();
            swipeDown();

        }
    }

    //Opening the time over box to show ads or collect the coin earned by user
    @SuppressLint("SetTextI18n")
    private void openTimeOverBox(){

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
            addTime.setVisibility(View.INVISIBLE);
            goHomeButton.setText("Go Home");
            titleShow.setText("You Won");
        }

        dialogBox.setView(view);

        final AlertDialog alertDialog = dialogBox.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.setCancelable(false);


        AdRequest adRequest = new AdRequest.Builder().build();

        // Timer ads reload on every time over box
        RewardedAd.load(this, getResources().getString(R.string.addTimeRewardAds), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                Log.d(TAG, loadAdError.toString());
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
                        timeIncrease();
//                                Toast.makeText(getApplicationContext(), "done dishmissed ads", Toast.LENGTH_SHORT).show();
                        timeRewardAds = null;
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
                });
            } else {
                Toast.makeText(getApplicationContext(), "Time Reward is Not Ready", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "The rewarded ad wasn't ready yet.");
            }


        });

        goHomeButton.setOnClickListener(view12 -> {
            if (won){
                score.saveHighScore(totalScore);
                countDownTimer.cancel();
                goHome();
            }else {
//                timeInSec =5;
//                countDownTimer.start();
                goHome();

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
                timeOver();
                openTimeOverBox();
            }
        }.start();
    }

    //Time off function to start the dialog box and stoping the timer
    private void timeOver(){
        checkImage();
        totalScore = score.generateScore(correctCount);
        score.saveHighScore(totalScore);
        if (won){
            openTimeOverBox();
            countDownTimer.cancel();
        }
    }


    private void timeIncrease(){
        if (isAdsPlayed){
            timeInSec = 60;
            startTimer();
        }
    }


    private void goHome(){
        startActivity(new Intent(getApplicationContext(), GameHome.class));
        finish();
    }
}