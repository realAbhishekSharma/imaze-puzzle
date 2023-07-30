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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elementstore.imazepuzzle.ControllerKeyboard;
import com.elementstore.imazepuzzle.ImageBoxPosition;
import com.elementstore.imazepuzzle.ImageIdList;
import com.elementstore.imazepuzzle.ImageLevel;
import com.elementstore.imazepuzzle.ImageSlicer;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.dialogs.LargeImageDialog;
import com.elementstore.imazepuzzle.dialogs.TImeOverDialog;
import com.elementstore.imazepuzzle.services.Coins;
import com.elementstore.imazepuzzle.services.Mission;
import com.elementstore.imazepuzzle.services.PlayerLife;
import com.elementstore.imazepuzzle.services.SliderBoxTheme;
import com.elementstore.imazepuzzle.SwipeMotionGesture;
import com.elementstore.imazepuzzle.services.SoundAndVibration;
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

public class FivePuzzleGame extends AppCompatActivity {


    private long timeInSec=241;
    private int totalTimeSpend = 0;
    private int correctCount = 0;
    private int totalEarnedCoins = 0;
    private final int PUZZLE_SIZE = 5;
    private ImageIdList imageIdList;
    private CountDownTimer countDownTimer;
    private ProgressBar timerProgress;
    private int[][] imagePosition = {
            {R.id.oneOne, R.id.oneTwo, R.id.oneThree, R.id.oneFour, R.id.oneFive},
            {R.id.twoOne, R.id.twoTwo, R.id.twoThree, R.id.twoFour, R.id.twoFive},
            {R.id.threeOne, R.id.threeTwo, R.id.threeThree, R.id.threeFour, R.id.threeFive},
            {R.id.fourOne, R.id.fourTwo, R.id.fourThree, R.id.fourFour, R.id.fourFive},
            {R.id.fiveOne, R.id.fiveTwo, R.id.fiveThree, R.id.fiveFour, R.id.fiveFive}
    };

    private Bitmap [][] puzzleImageList;

    private int[] emptyBoxPosition = {-1,-1};

    private ImageView[][] imageBox;
    private ImageView originalImage;
    private TextView timer, levelView;
    private ImageView emptyBox;

    private GestureDetector gestureDetector;

    private ImageBoxPosition[][] imageBoxPositions = new ImageBoxPosition[PUZZLE_SIZE][PUZZLE_SIZE];

    private AdRequest adRequest = new AdRequest.Builder().build();

    private RewardedAd timeRewardAds;

    private AdView mAdView;

    private Coins coins;
    private ImageLevel imageLevel;
    private boolean isAdsPlayed, won;

    private int selectedGameLevel;
    private boolean isKeyEnable = false;
    private View keyBoardLayout;

    private ControllerKeyboard controllerKeyboard;
    private SoundAndVibration soundAndVibration;

    private SliderBoxTheme sliderBoxTheme;
    private TImeOverDialog tImeOverDialog;
    private LargeImageDialog largeImageDialog;
    private PlayerLife playerLife;
    private Mission mission;
    private boolean isCustomImage = false;
    private boolean backState = false;
    @Override
    public void onBackPressed() {
        try {
            if (!backState){
                backState = true;
                Toast.makeText(this, "Press again back to exit.", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backState = false;
                    }
                },3000);
            }else {
                goHome();
                countDownTimer.cancel();
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_puzzle_game);
        getWindow().setStatusBarColor(getColor(R.color.deepCyan));


        coins = new Coins(this);
        imageLevel = new ImageLevel(this);
        soundAndVibration = new SoundAndVibration(this);
        sliderBoxTheme = new SliderBoxTheme(this);
        largeImageDialog = new LargeImageDialog(this);
        playerLife = new PlayerLife(this);
        mission = new Mission(this);

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


        keyBoardLayout = findViewById(R.id.keyLayoutFive);
        originalImage.setEnabled(false);
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
                                puzzleImageList = imageSlicer.getSlicedStrokedImage(5,15, getColor(R.color.white));
                                emptyBox.setImageBitmap(imageSlicer.getEmptyBoxImage(puzzleImageList[0][0],5,15, sliderBoxTheme.getActiveThemeColor(),Color.parseColor("#FFFFFF")));


                                startGame();
                                levelView.setText("CI");
                                isCustomImage = true;
                                originalImage.setImageBitmap(selectedBitmap);
                                largeImageDialog.setImagePreviewImage(selectedBitmap);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println(uri);
                        }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                            goHome();
                            // Use ImagePicker.Companion.getError(result.getData()) to show an error
                        }else if (result.getResultCode()== RESULT_CANCELED){
                            goHome();
                            Toast.makeText(getApplicationContext(), "No Image Selected.", Toast.LENGTH_SHORT).show();
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
            emptyBox.setImageBitmap(imageSlicer.getEmptyBoxImage(puzzleImageList[0][0],13,40, sliderBoxTheme.getActiveThemeColor(),Color.parseColor("#FFFFFF")));

            startGame();
            levelView.setText(selectedGameLevel+"");
            originalImage.setBackground(getDrawable(imageIdList.getImageId(selectedGameLevel)));
            largeImageDialog.setImagePreviewImage(getDrawable(imageIdList.getImageId(selectedGameLevel)));
        }

        originalImage.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            largeImageDialog.openLargeImage();
        });


        controllerKeyboard = new ControllerKeyboard(this, keyBoardLayout, new ControllerKeyboard.KeyPressedListener() {
            @Override
            public void onLeftKey(View view) {
                soundAndVibration.doClickVibration();
                swipeRight();
            }

            @Override
            public void onRightKey(View view) {
                soundAndVibration.doClickVibration();
                swipeLeft();
            }

            @Override
            public void onUpKey(View view) {
                soundAndVibration.doClickVibration();
                swipeDown();
            }

            @Override
            public void onDownKey(View view) {
                soundAndVibration.doClickVibration();
                swipeUp();
            }
        });

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


        tImeOverDialog = new TImeOverDialog(this, new TImeOverDialog.BoxItemClick() {
            @Override
            public void onAddTimeFromAds(View view) {
                addTimeFromAds();
            }

            @Override
            public void onAddTimeFromCoins(View view) {

            }

            @Override
            public void onGoHome(View view) {
                if (won) {
                    coins.addCoin(totalEarnedCoins);
                    playerLife.increaseLife();
                    countDownTimer.cancel();
                    updateImageActiveLevel();
                    updatePlayedCount();
                    goHome();
                } else {
//                timeInSec =5;
//                countDownTimer.start();
                    goHome();
                    countDownTimer.cancel();

                }
                tImeOverDialog.closeBox();

            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isKeyEnable) {
            return gestureDetector.onTouchEvent(event);
        }
        return false;
    }


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
            originalImage.setEnabled(true);
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

            if (totalTimeSpend>1) {
                soundAndVibration.playBoxSlideSound();
            }

        }else if (emptyBoxPosition[0] == 0 || emptyBoxPosition[0] == 1 || emptyBoxPosition[0] == 2 || emptyBoxPosition[0] == 3){
            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];

            swapImageAndEmptyBox( xx+1,yy);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
//            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]);
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


            if (totalTimeSpend>1) {
                soundAndVibration.playBoxSlideSound();
            }
            checkGameWon();

        }else if (emptyBoxPosition[0] == 1 || emptyBoxPosition[0] == 2 || emptyBoxPosition[0] == 3 || emptyBoxPosition[0] == 4){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx-1,yy);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
//            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeLeft(){
        if (emptyBoxPosition[1] == 0 || emptyBoxPosition[1] == 1 || emptyBoxPosition[1] == 2 || emptyBoxPosition[1] == 3){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx,yy+1);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
//            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeRight(){
        if (emptyBoxPosition[1] == 1 || emptyBoxPosition[1] == 2 || emptyBoxPosition[1] == 3 || emptyBoxPosition[1] == 4){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx,yy-1);

            imageBoxPositions[xx][yy].setX(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getX());
            imageBoxPositions[xx][yy].setY(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getY());
//            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);

        }
    }

    private void swapImageAndEmptyBox(int i, int j){
        float x = emptyBox.getX();
        float y = emptyBox.getY();

//        System.out.println(x+" "+y);
        emptyBox.setX(imageBox[imageBoxPositions[i][j].getX()][imageBoxPositions[i][j].getY()].getX());
        emptyBox.setY(imageBox[imageBoxPositions[i][j].getX()][imageBoxPositions[i][j].getY()].getY());


        imageBox[imageBoxPositions[i][j].getX()][imageBoxPositions[i][j].getY()].setX(x);
        imageBox[imageBoxPositions[i][j].getX()][imageBoxPositions[i][j].getY()].setY(y);

        emptyBoxPosition[0] = i;
        emptyBoxPosition[1] = j;

        if (totalTimeSpend>1) {
            soundAndVibration.playBoxSlideSound();
        }
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
                    totalEarnedCoins = coins.generateCoins(correctCount);

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
//            System.out.println(".                   random "+random);
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

    private void startTimer(){
        countDownTimer = new CountDownTimer(timeInSec*1000,1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                timer.setText((int)l/1000+"");
                timerProgress.setProgress((int) (1000-(l/timeInSec))/10, true);
                totalTimeSpend++;
            }

            @Override
            public void onFinish() {
                timer.setText("0");
                timerProgress.setProgress(100);
                onTimeOver();
            }
        }.start();
        loadTimeRewardAds();
    }

    private void addTimeFromAds(){
        if (timeRewardAds != null) {
            Activity activityContext = FivePuzzleGame.this;
            timeRewardAds.show(activityContext, rewardItem -> {
                // Handle the reward.
                Log.d(TAG, "The user earned the reward.");
//                    int rewardAmount = rewardItem.getAmount();
//                    String rewardType = rewardItem.getType();
                tImeOverDialog.closeBox();
            });
        } else {
            Toast.makeText(getApplicationContext(), "Time Reward is Not Ready", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }

    }

    private void onTimeOver(){
        checkImageAnswerPosition();
        tImeOverDialog.openBox();
        countDownTimer.cancel();

    }

    private void checkGameWon(){
        checkImageAnswerPosition();
        if (won){
            tImeOverDialog.setWinningContent(totalEarnedCoins);
            tImeOverDialog.openBox();
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
        mission.increaseTimeSpendInGame(totalTimeSpend-1);
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

        Animation topSlideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_top);
        Animation bottomSlideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_bottom);
        topSlideAnimation.setDuration(150);
        bottomSlideAnimation.setDuration(150);

        if (height - keyBoardLayout.getY()-120>keyBoardLayout.getHeight()){
            isKeyEnable = soundAndVibration.getControllerType();
            largeImageDialog.setKeyboardStatusText(true);
            if (isKeyEnable){
                keyBoardLayout.setAnimation(topSlideAnimation);
                keyBoardLayout.setVisibility(View.VISIBLE);
            }else {
                keyBoardLayout.setVisibility(View.GONE);
                if (totalTimeSpend>2) {
                    keyBoardLayout.setAnimation(bottomSlideAnimation);
                }
            }
        }else {
            isKeyEnable = false;
            largeImageDialog.setKeyboardStatusText(false);
            keyBoardLayout.setVisibility(View.GONE);
//            Toast.makeText(getApplicationContext(), "Key Doesn't Support.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            visibilityForKeyBoardLayout();

        }
    }
    public void updatePlayedCount(){
        if (isCustomImage){
            mission.increasePlayedCount(1);
        }else {
            mission.increasePlayedCount(5);
        }
    }

}