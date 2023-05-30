package com.elementstore.imazepuzzle.Activity;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elementstore.imazepuzzle.ImageBoxPosition;
import com.elementstore.imazepuzzle.ImageIdList;
import com.elementstore.imazepuzzle.ImageLevel;
import com.elementstore.imazepuzzle.ImageSlicer;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.Score;
import com.elementstore.imazepuzzle.SliderBoxTheme;
import com.elementstore.imazepuzzle.SwipeMotionGesture;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.io.IOException;
import java.util.List;

public class FivePuzzleGame extends AppCompatActivity {


    long timeInSec=241;
    int correctCount = 0;
    int totalScore = 0;
    final int PUZZLE_SIZE = 5;

    ImageIdList imageIdList;
    CountDownTimer countDownTimer;
    ProgressBar timerProgress;
    int[][] imagePosition = {
            {R.id.oneOne, R.id.oneTwo, R.id.oneThree, R.id.oneFour, R.id.oneFive},
            {R.id.twoOne, R.id.twoTwo, R.id.twoThree, R.id.twoFour, R.id.twoFive},
            {R.id.threeOne, R.id.threeTwo, R.id.threeThree, R.id.threeFour, R.id.threeFive},
            {R.id.fourOne, R.id.fourTwo, R.id.fourThree, R.id.fourFour, R.id.fourFive},
            {R.id.fiveOne, R.id.fiveTwo, R.id.fiveThree, R.id.fiveFour, R.id.fiveFive}
    };

    Bitmap[][] puzzleImageList;

    int[] emptyBoxPosition = {-1,-1};

    ImageView[][] imageBox;
    ImageView originalImage;
    TextView timer, levelView, emptyBox;
    TextView clickUp, clickDown, clickLeft, clickRight;

    GestureDetector gestureDetector;

    ImageBoxPosition[][] imageBoxPositions = new ImageBoxPosition[PUZZLE_SIZE][PUZZLE_SIZE];

    AdRequest adRequest = new AdRequest.Builder().build();

    RewardedAd timeRewardAds;

    AdView mAdView;

    Score score;
    ImageLevel imageLevel;
    boolean isAdsPlayed, won;

    int selectedGameLevel;

    ImageView keypadSwitch;
    boolean isKeyEnable = false;
    LinearLayout keyBoardLayout;

    SliderBoxTheme sliderBoxTheme;

    @Override
    public void onBackPressed() {
        try {
            goHome();
            countDownTimer.cancel();
        }catch (Exception e){
            System.out.println(e.getMessage());
            Toast.makeText(getApplicationContext(), "Problem.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_puzzle_game);
        getWindow().setStatusBarColor(getColor(R.color.deepCyan));



        score = new Score(this);
        imageLevel = new ImageLevel(this);
        sliderBoxTheme = new SliderBoxTheme(this);

        Intent intent = getIntent();
        selectedGameLevel = intent.getIntExtra("level",1);
        imageBox = new ImageView[PUZZLE_SIZE][PUZZLE_SIZE];

        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.adView);
        loadGameBannerAds();

        originalImage = findViewById(R.id.originalImageShowFive);

        timer = findViewById(R.id.timer);
        levelView = findViewById(R.id.levelViewFive);
        timerProgress = findViewById(R.id.timerProgressBar);
        emptyBox = findViewById(R.id.emptyBox);
        emptyBox.setBackgroundTintList(ColorStateList.valueOf(sliderBoxTheme.getColor()));

        clickUp = findViewById(R.id.clickUp);
        clickDown = findViewById(R.id.clickDown);
        clickLeft = findViewById(R.id.clickLeft);
        clickRight = findViewById(R.id.clickRight);


        keypadSwitch = findViewById(R.id.keySwitchFive);
        keyBoardLayout = findViewById(R.id.keyLayoutFive);

        keypadSwitch.setEnabled(false);

        keypadSwitch.setOnClickListener(view -> {
            Animation topSlideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_top);
            Animation bottomSlideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_bottom);
            topSlideAnimation.setDuration(150);
            bottomSlideAnimation.setDuration(150);


            if (isKeyEnable){
                isKeyEnable = false;
                keypadSwitch.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lightCyan)));
                keyBoardLayout.setAnimation(bottomSlideAnimation);
                keyBoardLayout.setVisibility(View.GONE);
            }else {
                isKeyEnable = true;
                keypadSwitch.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
                keyBoardLayout.setVisibility(View.VISIBLE);
                keyBoardLayout.setAnimation(topSlideAnimation);
            }
        });


        imageIdList = new ImageIdList();

        Bitmap selectedResourceBitmap;
        if(selectedGameLevel == 0){

            ActivityResultLauncher<Intent> launcher=
                    registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                        if(result.getResultCode()==RESULT_OK){
                            Uri uri=result.getData().getData();
                            try {

                                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                                ImageSlicer imageSlicer = new ImageSlicer(selectedBitmap,PUZZLE_SIZE);
                                puzzleImageList = imageSlicer.getSlicedStrokedImage(5,15, Color.parseColor("#FFFFFF"));

                                startGame();
                                levelView.setText("CI");
                                originalImage.setImageBitmap(selectedBitmap);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println(uri);
                        }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                            goHome();
                            // Use ImagePicker.Companion.getError(result.getData()) to show an error
                        }else if (result.getResultCode()== RESULT_CANCELED){
                            Toast.makeText(getApplicationContext(), "No Image Selected.", Toast.LENGTH_SHORT).show();
                            goHome();
                        }
                    });

            launcher.launch(
                    ImagePicker.with(this)
                            .crop(1,1)
                            .maxResultSize(1200,1200, true)
                            .cropSquare()
                            .galleryOnly() // or galleryOnly()
                            .createIntent());

        }else {
            int cropBy = 50;
            selectedResourceBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), imageIdList.getImageId(selectedGameLevel)));
            selectedResourceBitmap = Bitmap.createBitmap(selectedResourceBitmap, cropBy,cropBy,selectedResourceBitmap.getWidth()-2*cropBy, selectedResourceBitmap.getHeight()-2*cropBy);
            ImageSlicer imageSlicer = new ImageSlicer(selectedResourceBitmap,PUZZLE_SIZE);
            puzzleImageList = imageSlicer.getSlicedStrokedImage(13,40, getColor(R.color.white));
            startGame();
            levelView.setText(selectedGameLevel+"");
            originalImage.setImageBitmap(selectedResourceBitmap);
        }





        clickDown.setOnClickListener(view -> swipeUp());

        clickUp.setOnClickListener(view -> swipeDown());

        clickLeft.setOnClickListener(view -> swipeRight());

        clickRight.setOnClickListener(view -> swipeLeft());

        gestureDetector = new GestureDetector(this, new SwipeMotionGesture(){
            @Override
            public void onSwipeDown() {
                swipeUp();
            }

            @Override
            public void onSwipeUp() {
                swipeDown();
            }

            @Override
            public void onSwipeLeft() {
                swipeRight();
            }

            @Override
            public void onSwipeRight() {
                swipeLeft();
            }
        });


        loadTimeRewardAds();

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isKeyEnable) {
            return gestureDetector.onTouchEvent(event);
        }
        return false;    }


    public void startGame(){

        try {
            for (int x = 0; x < PUZZLE_SIZE; x++) {
                for (int y = 0; y < PUZZLE_SIZE; y++) {
                    imageBox[x][y] = findViewById(imagePosition[x][y]);



                    ImageBoxPosition i = new ImageBoxPosition();
                    i.setX(x);
                    i.setY(y);
                    imageBoxPositions[x][y] = i;
                }
            }


        }catch (Exception exception){
            System.out.println(exception.getMessage());
        }

        new Handler().postDelayed(() -> {
            setImageBoxRandomPosition();
            setSmallImageBoxBackground();
            startTimer();
            keypadSwitch.setEnabled(true);
            visibilityForKeyBoardLayout();
        },10);

    }


    private void swipeUp(){
        if (emptyBoxPosition[0] == -1 && emptyBoxPosition[1]==-1){


            float x = emptyBox.getX();
            float y = emptyBox.getY();
            int imageX = 0;
            int imageY = PUZZLE_SIZE -1;

            System.out.println(x+" "+y);
            emptyBox.setX(imageBox[imageX][imageY].getX());
            emptyBox.setY(imageBox[imageX][imageY].getY());

            imageBox[imageX][imageY].setX(x);
            imageBox[imageX][imageY].setY(y);

            emptyBoxPosition[0] = imageX;
            emptyBoxPosition[1] = imageY;

            imageBoxPositions[imageX][imageY].setX(imageX);
            imageBoxPositions[imageX][imageY].setY(imageY);
//            checkImage();

        }else if (emptyBoxPosition[0] == 0 || emptyBoxPosition[0] == 1 || emptyBoxPosition[0] == 2 || emptyBoxPosition[0] == 3){
            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];

            swapImageAndEmptyBox( xx+1,yy);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]);
        }

    }

    private void swipeDown(){
        if (emptyBoxPosition[0] == 0 && emptyBoxPosition[1]==4){

            float x = emptyBox.getX();
            float y = emptyBox.getY();
            int imageX = 0;
            int imageY = PUZZLE_SIZE -1;

            System.out.println(x+" "+y);
            emptyBox.setX(imageBox[imageX][imageY].getX());
            emptyBox.setY(imageBox[imageX][imageY].getY());

            imageBox[imageX][imageY].setX(x);
            imageBox[imageX][imageY].setY(y);

            emptyBoxPosition[0] = -1;
            emptyBoxPosition[1] = -1;

            imageBoxPositions[imageX][imageY].setX(imageX);
            imageBoxPositions[imageX][imageY].setY(imageY);
//            checkImage();
            checkGameWon();

        }else if (emptyBoxPosition[0] == 1 || emptyBoxPosition[0] == 2 || emptyBoxPosition[0] == 3 || emptyBoxPosition[0] == 4){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx-1,yy);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeLeft(){
        if (emptyBoxPosition[1] == 0 || emptyBoxPosition[1] == 1 || emptyBoxPosition[1] == 2 || emptyBoxPosition[1] == 3){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx,yy+1);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeRight(){
        if (emptyBoxPosition[1] == 1 || emptyBoxPosition[1] == 2 || emptyBoxPosition[1] == 3 || emptyBoxPosition[1] == 4){

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

    }

    private void checkImageAnswerPosition(){
        correctCount =0;
        for (int x =0; x<PUZZLE_SIZE; x++){
            for (int y = 0; y<PUZZLE_SIZE; y++){
                if (imageBoxPositions[x][y].getX() == x && imageBoxPositions[x][y].getY() == y){
                    correctCount++;
//                    System.out.println(correctCount);
                    if (correctCount == PUZZLE_SIZE*PUZZLE_SIZE){
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
    private void setSmallImageBoxBackground(){
        for (int x = 0; x < PUZZLE_SIZE; x++) {
            for (int y = 0; y < PUZZLE_SIZE; y++) {
                imageBox[x][y].setImageBitmap(puzzleImageList[x][y]);
            }
        }
    }

    private void setImageBoxRandomPosition(){
        swipeUp();
        for (int x =0; x<4; x++){
            for (int i = 0; i <PUZZLE_SIZE-1; i++) {
                swipeUp();
            }
            for (int i = 0; i <PUZZLE_SIZE-1; i++) {
                swipeRight();
            }
            for (int i = 0; i <PUZZLE_SIZE-1; i++) {
                swipeDown();
            }
            for (int i = 0; i <PUZZLE_SIZE-1; i++) {
                swipeLeft();
            }

        }

        for (int p = 0; p<30;p++){
            int random = (int)( Math.random()*4);
            System.out.println(".                   random "+random);
            if (random == 0) {
                swipeRight();
                swipeUp();
                swipeLeft();

            }else if (random == 1){
                swipeLeft();
                swipeUp();
                swipeRight();
            }else if (random == 2){
                swipeUp();
                swipeRight();
                swipeDown();

            }else if (random == 3){
                swipeUp();
                swipeLeft();
                swipeDown();

            }
        }
        for (int i = 0; i <4; i++) {
            swipeRight();
        }
        for (int i = 0; i <4; i++) {
            swipeUp();
        }
        for (int i = 0; i <4; i++) {
            swipeLeft();
            swipeDown();
        }
        swipeDown();
    }

    //Opening the time over box to show ads or collect the coin earned by user
    @SuppressLint("SetTextI18n")
    private void openDialogBox(){
        try {

            AlertDialog.Builder dialogBox = new AlertDialog.Builder(FivePuzzleGame.this);
            View view = getLayoutInflater().inflate(R.layout.time_over_dialog, null);

            TextView titleShow, earnedScore, goHomeButton, addTime;

            titleShow = view.findViewById(R.id.titleShow);
            earnedScore = view.findViewById(R.id.earnedScore);
            goHomeButton = view.findViewById(R.id.goHomeButton);
            addTime = view.findViewById(R.id.timeIncrease);

            if (won) {
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


            earnedScore.setText(totalScore + "");

            addTime.setOnClickListener(view1 -> {
                if (timeRewardAds != null) {
                    Activity activityContext = FivePuzzleGame.this;
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
                if (won) {
                    score.setScore(totalScore);
                    countDownTimer.cancel();
                    updateImageActiveLevel();
                    goHome();
                } else {
//                timeInSec =5;
//                countDownTimer.start();
                    goHome();
                    countDownTimer.cancel();

                }
                alertDialog.dismiss();


            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error in Time Box.", Toast.LENGTH_SHORT).show();
        }

    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(timeInSec*1000,1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                timer.setText((int)l/1000+"");
                timerProgress.setProgress((int) (1000-(l/timeInSec))/10, true);
            }

            @Override
            public void onFinish() {
                timer.setText("0");
                timerProgress.setProgress(100);
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
        imageLevel.setActiveLevel(PUZZLE_SIZE,selectedGameLevel+1);
    }

    private void loadTimeRewardAds(){

        // Timer ads reload on every time over box
        RewardedAd.load(this, getResources().getString(R.string.addTimeRewardAds), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                Log.d(TAG, "here: "+loadAdError);
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
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
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

    private void visibilityForKeyBoardLayout(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        if (height - keyBoardLayout.getY()-120>keyBoardLayout.getHeight()){
            if (isKeyEnable){

                keypadSwitch.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
                keyBoardLayout.setVisibility(View.VISIBLE);
            }else {
                keypadSwitch.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lightCyan)));
                keyBoardLayout.setVisibility(View.GONE);
            }
        }else {
            keypadSwitch.setVisibility(View.GONE);
            keyBoardLayout.setVisibility(View.GONE);
        }
    }
}