package com.elementstore.imazepuzzle.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elementstore.imazepuzzle.ControllerKeyboard;
import com.elementstore.imazepuzzle.ImageBoxPosition;
import com.elementstore.imazepuzzle.ImageIdList;
import com.elementstore.imazepuzzle.ImageLevel;
import com.elementstore.imazepuzzle.ImageSlicer;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.SwipeMotionGesture;
import com.elementstore.imazepuzzle.dialogs.GameOverDialog;
import com.elementstore.imazepuzzle.services.Mission;
import com.elementstore.imazepuzzle.services.PlayerLife;
import com.elementstore.imazepuzzle.services.ShrinkViewEffect;
import com.elementstore.imazepuzzle.services.SliderBoxTheme;
import com.elementstore.imazepuzzle.services.SoundAndVibration;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class MainGameActivity extends AppCompatActivity {

    ConstraintLayout mainGameLoadingScreen, controllerSwitchButton;
    TextView largeImageView, movesCountView;
    ImageView largeImageViewHolderButton, mainLargeImageView;
    private ImageView emptyBox;

    private int[][] imageVariableList = {
            {R.id.oneOne, R.id.oneTwo, R.id.oneThree, R.id.oneFour, R.id.oneFive},
            {R.id.twoOne, R.id.twoTwo, R.id.twoThree, R.id.twoFour, R.id.twoFive},
            {R.id.threeOne, R.id.threeTwo, R.id.threeThree, R.id.threeFour, R.id.threeFive},
            {R.id.fourOne, R.id.fourTwo, R.id.fourThree, R.id.fourFour, R.id.fourFive},
            {R.id.fiveOne, R.id.fiveTwo, R.id.fiveThree, R.id.fiveFour, R.id.fiveFive}
    };

    private int correctCount = 0;
    private int movesCount = 0;
    private int PUZZLE_SIZE = 3;
    private boolean isGameStarted = false;
    private ImageIdList imageIdList;
    private Bitmap[][] puzzleImageList;
    private int[] emptyBoxPosition = {-1,-1};
    private ImageView[][] imageBoxPiece;
    private GestureDetector gestureDetector;
    private ImageBoxPosition[][] imageBoxPositions;
//    private Coins coins;
    private ImageLevel imageLevel;
    private boolean isAdsPlayed, won;
    private int selectedGameLevel;
    private int totalTimeSpend = 0;
    private boolean isKeyEnable = false;
    private View keyBoardLayout;
    private View mainBoardThree, mainBoardFour, mainBoardFive;
    private SoundAndVibration soundAndVibration;
    private SliderBoxTheme sliderBoxTheme;
    private GameOverDialog gameOverDialog;
    private PlayerLife playerLife;
    private Mission mission;
    private boolean backState = false;
    private AdRequest adRequest;
    private RewardedAd movesRewardedAdsHigh;
    private RewardedInterstitialAd moveRewardedAdsLow;
    private AdView bannerAdView;
    private boolean isCoinsDoubledWithAds = false;

    TextView darkScreen, closeDarkScreen, controllerTextView,exitButton;
    View gameOverViewLayout, largerImageViewLayout;


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        try {
            if (!backState) {
                backState = true;
                Toast.makeText(this, "Press again back to exit.", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backState = false;
                    }
                }, 3000);
            } else {
                goHome();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.cloudCyan));
        setContentView(R.layout.activity_main_game);

        mainGameLoadingScreen = findViewById(R.id.mainGameLoadingScreen);
        darkScreen = findViewById(R.id.darkTransparentScreenMainGame);
        closeDarkScreen = findViewById(R.id.closeDarkScreenMainGame);
        gameOverViewLayout = findViewById(R.id.gameOverViewContainer);
        largerImageViewLayout = findViewById(R.id.largeImageViewContainer);

        largeImageViewHolderButton = findViewById(R.id.topImageViewHolderButton);
        movesCountView = findViewById(R.id.topMovesCountViewText);


        mainLargeImageView = largerImageViewLayout.findViewById(R.id.largeImagePreviewMainGame);
        controllerSwitchButton = largerImageViewLayout.findViewById(R.id.controllerSwitchKeyMainGame);
        controllerTextView = largerImageViewLayout.findViewById(R.id.controllerSwitchTextViewMainGame);
        exitButton = largerImageViewLayout.findViewById(R.id.exitButtonMainGame);



        new ShrinkViewEffect(largeImageViewHolderButton);
        new ShrinkViewEffect(controllerSwitchButton);
        new ShrinkViewEffect(exitButton);
        new ShrinkViewEffect(closeDarkScreen);

        mainBoardThree = findViewById(R.id.mainGameSizeThree);
        mainBoardFour = findViewById(R.id.mainGameSizeFour);
        mainBoardFive = findViewById(R.id.mainGameSizeFive);
        keyBoardLayout = findViewById(R.id.keyboardLayoutMainGame);


        bannerAdView = findViewById(R.id.adViewMainGame);

        adRequest = new AdRequest.Builder().build();
        MobileAds.initialize(this, initializationStatus -> {
            loadGameBannerAds();
            loadMovesRewardHigh();
            loadMovesRewardLow();
        });



        Intent intent = getIntent();
        selectedGameLevel = intent.getIntExtra("level",1);
        PUZZLE_SIZE = intent.getIntExtra("puzzleSize",3);
        imageBoxPiece = new ImageView[PUZZLE_SIZE][PUZZLE_SIZE];
        imageIdList = new ImageIdList();

        imageBoxPositions = new ImageBoxPosition[PUZZLE_SIZE][PUZZLE_SIZE];
//        coins = new Coins(this);
        imageLevel = new ImageLevel(this);
        soundAndVibration = new SoundAndVibration(this);
        sliderBoxTheme = new SliderBoxTheme(this);
//        largeImageDialog = new LargeImageDialog(this);
        playerLife = new PlayerLife(this);
        mission = new Mission(this);


        largeImageViewHolderButton.setOnClickListener(item -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            largeImageLayoutVisibility(View.VISIBLE);
        });
        darkScreen.setOnClickListener(item -> {
            if (largerImageViewLayout.getVisibility() == View.VISIBLE) {
                largeImageLayoutVisibility(View.GONE);
            }
//            if (gameOverViewLayout.getVisibility() == View.VISIBLE) {
//                gameOverLayoutVisibility(View.GONE);
//            }
        });

        largerImageViewLayout.setOnClickListener(item -> {});

        exitButton.setOnClickListener(item -> {
            saveAllStateOfGame();
            finish();
        });

        closeDarkScreen.setOnClickListener(item -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            if (largerImageViewLayout.getVisibility() == View.VISIBLE) {
                largeImageLayoutVisibility(View.GONE);
            }
        });


        controllerSwitchButton.setOnClickListener(item -> {
            soundAndVibration.changeControllerType();
            updateControllerSwitchView(soundAndVibration.getControllerType());
            soundAndVibration.playNormalClickSound();
            soundAndVibration.doClickVibration();
        });

//        movesCountView.setOnClickListener(item -> {
//            gameOverLayoutVisibility(View.VISIBLE);
//        });


        new Handler().postDelayed(()->{
            initializeGame();
            setImageBoxRandomPosition();
            setSmallImageBoxBackground();
            System.out.println(movesCount);
            if (PUZZLE_SIZE == 3){
                movesCount = (int) (1.5*movesCount);
            }else if (PUZZLE_SIZE == 4){
                movesCount = 3*movesCount;
            }else if (PUZZLE_SIZE == 5){
                movesCount = 4*movesCount;
            }
            updateMovesCount();
            startGame();
        },500);


        new ControllerKeyboard(this, keyBoardLayout, new ControllerKeyboard.KeyPressedListener() {
            @Override
            public void onLeftKey(View view) {
                if (isGameStarted) {
                    soundAndVibration.doClickVibration();
                    swipeRight();
                }
            }

            @Override
            public void onRightKey(View view) {
                if (isGameStarted) {
                    soundAndVibration.doClickVibration();
                    swipeLeft();
                }
            }

            @Override
            public void onUpKey(View view) {
                if (isGameStarted) {
                    soundAndVibration.doClickVibration();
                    swipeDown();
                }
            }

            @Override
            public void onDownKey(View view) {
                if (isGameStarted) {
                    soundAndVibration.doClickVibration();
                    swipeUp();
                }
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


        gameOverDialog = new GameOverDialog(getApplicationContext(), gameOverViewLayout, new GameOverDialog.BoxItemClick() {
            @Override
            public void onGoHome(View view) {
                saveAllStateOfGame();
                goHome();
            }

            @Override
            public void addLowMoves(View view) {
                movesCount = 31;
                updateMovesCount();
                isGameStarted = true;
                gameOverLayoutVisibility(View.GONE);
            }

            @Override
            public void addHighMoves(View view) {
                movesCount = 61;
                updateMovesCount();
                isGameStarted = true;
                gameOverLayoutVisibility(View.GONE);
            }

            @Override
            public void addLowMovesWithAds(View view) {
                showAddMovesRewardLow();
            }

            @Override
            public void addHighMovesWithAds(View view) {
                showAddMovesRewardHigh();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isKeyEnable && isGameStarted) {
            return gestureDetector.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            visibilityForKeyBoardLayout();
            updateControllerSwitchView(soundAndVibration.getControllerType());
        }
    }

    private void setLoadingScreenVisibility(boolean visibility){
        if (visibility){

            Animation loadingScreenFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_in);
            mainGameLoadingScreen.setAnimation(loadingScreenFadeIn);
            mainGameLoadingScreen.setVisibility(View.VISIBLE);
        }else {
            Animation loadingScreenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
            mainGameLoadingScreen.setAnimation(loadingScreenAnimation);
            mainGameLoadingScreen.setVisibility(View.GONE);
            visibilityForKeyBoardLayout();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initializeGame(){

        if (PUZZLE_SIZE == 3){
            mainBoardThree.setVisibility(View.VISIBLE);
            emptyBox = mainBoardThree.findViewById(R.id.emptyBox);

            for (int x = 0; x < PUZZLE_SIZE; x++) {
                for (int y = 0; y < PUZZLE_SIZE; y++) {
                    imageBoxPiece[x][y] = mainBoardThree.findViewById(imageVariableList[x][y]);
                    ImageBoxPosition i = new ImageBoxPosition();
                    i.setXPosition(x);
                    i.setYPosition(y);
                    imageBoxPositions[x][y] = i;
                }
            }

        }else if (PUZZLE_SIZE == 4){
            mainBoardFour.setVisibility(View.VISIBLE);
            emptyBox = mainBoardFour.findViewById(R.id.emptyBox);

            for (int x = 0; x < PUZZLE_SIZE; x++) {
                for (int y = 0; y < PUZZLE_SIZE; y++) {
                    imageBoxPiece[x][y] = mainBoardFour.findViewById(imageVariableList[x][y]);
                    ImageBoxPosition i = new ImageBoxPosition();
                    i.setXPosition(x);
                    i.setYPosition(y);
                    imageBoxPositions[x][y] = i;
                }
            }
        }else if (PUZZLE_SIZE == 5){
            mainBoardFive.setVisibility(View.VISIBLE);
            emptyBox = mainBoardFive.findViewById(R.id.emptyBox);

            for (int x = 0; x < PUZZLE_SIZE; x++) {
                for (int y = 0; y < PUZZLE_SIZE; y++) {
                    imageBoxPiece[x][y] = mainBoardFive.findViewById(imageVariableList[x][y]);
                    ImageBoxPosition i = new ImageBoxPosition();
                    i.setXPosition(x);
                    i.setYPosition(y);
                    imageBoxPositions[x][y] = i;
                }
            }
        }



        Bitmap selectedResourceBitmap;
        int cropBy = 50;
        int lineStroke, roundRadius;
        if (selectedGameLevel == 0){
            selectedResourceBitmap = imageIdList.getBitmap(getApplicationContext());
            lineStroke = 4;
            roundRadius = 15;
        }else {
            selectedResourceBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), imageIdList.getImageId(selectedGameLevel)));
            lineStroke = 13;
            roundRadius = 40;
        }
        selectedResourceBitmap = Bitmap.createBitmap(selectedResourceBitmap, cropBy,cropBy,selectedResourceBitmap.getWidth()-2*cropBy, selectedResourceBitmap.getHeight()-2*cropBy);
        ImageSlicer imageSlicer = new ImageSlicer(selectedResourceBitmap,PUZZLE_SIZE);
        puzzleImageList = imageSlicer.getSlicedStrokedImage(lineStroke,roundRadius, getColor(R.color.white));
        emptyBox.setImageBitmap(imageSlicer.getEmptyBoxImage(puzzleImageList[0][0],lineStroke,roundRadius, getColor(sliderBoxTheme.getActiveBoxColor()), Color.parseColor("#FFFFFF")));

//        levelView.setText(selectedGameLevel+"");
        if (selectedGameLevel == 0){
            largeImageViewHolderButton.setImageBitmap(imageIdList.getBitmap(getApplicationContext()));
            mainLargeImageView.setImageBitmap(imageIdList.getBitmap(getApplicationContext()));
        }else {
            largeImageViewHolderButton.setBackground(getDrawable(imageIdList.getImageId(selectedGameLevel)));
            mainLargeImageView.setBackground(getDrawable(imageIdList.getImageId(selectedGameLevel)));
        }
//        largeImageDialog.setImagePreviewImage(getDrawable(imageIdList.getImageId(selectedGameLevel)));

    }

    private void startGame(){
        System.out.println("game Started");
        setLoadingScreenVisibility(false);
        isGameStarted = true;
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


    @SuppressLint("UseCompatLoadingForDrawables")
    private void setSmallImageBoxBackground(){
        for (int x = 0; x < PUZZLE_SIZE; x++) {
            for (int y = 0; y < PUZZLE_SIZE; y++) {
                imageBoxPiece[x][y].setImageBitmap(puzzleImageList[x][y]);
            }
        }
    }

    private void swipeUp(){
        if (emptyBoxPosition[0] == -1 && emptyBoxPosition[1]==-1){


            float x = emptyBox.getX();
            float y = emptyBox.getY();
            int imageX = 0;
            int imageY = PUZZLE_SIZE -1;

            System.out.println(x+" "+y);
            emptyBox.setX(imageBoxPiece[imageX][imageY].getX());
            emptyBox.setY(imageBoxPiece[imageX][imageY].getY());

            imageBoxPiece[imageX][imageY].setX(x);
            imageBoxPiece[imageX][imageY].setY(y);

            emptyBoxPosition[0] = imageX;
            emptyBoxPosition[1] = imageY;

            imageBoxPositions[imageX][imageY].setXPosition(imageX);
            imageBoxPositions[imageX][imageY].setYPosition(imageY);

            if (totalTimeSpend>1) {
                soundAndVibration.playBoxSlideSound();
            }
            updateMovesCount();

        }else if (emptyBoxPosition[0] >= 0 && emptyBoxPosition[0] <= PUZZLE_SIZE-2){
            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];

            swapImageAndEmptyBox( xx+1,yy);

            imageBoxPositions[xx][yy].setXPosition(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getXPosition());
            imageBoxPositions[xx][yy].setYPosition(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getYPosition());
//            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]);
        }

    }

    private void swipeDown(){
        if (emptyBoxPosition[0] == 0 && emptyBoxPosition[1]==PUZZLE_SIZE-1){

            float x = emptyBox.getX();
            float y = emptyBox.getY();
            int imageX = 0;
            int imageY = PUZZLE_SIZE -1;

            System.out.println(x+" "+y);
            emptyBox.setX(imageBoxPiece[imageX][imageY].getX());
            emptyBox.setY(imageBoxPiece[imageX][imageY].getY());

            imageBoxPiece[imageX][imageY].setX(x);
            imageBoxPiece[imageX][imageY].setY(y);

            emptyBoxPosition[0] = -1;
            emptyBoxPosition[1] = -1;

            imageBoxPositions[imageX][imageY].setXPosition(imageX);
            imageBoxPositions[imageX][imageY].setYPosition(imageY);


            if (totalTimeSpend>1) {
                soundAndVibration.playBoxSlideSound();
            }
            checkGameWon();
            updateMovesCount();

        }else if (emptyBoxPosition[0] >= 1 && emptyBoxPosition[0] <= PUZZLE_SIZE-1){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx-1,yy);

            imageBoxPositions[xx][yy].setXPosition(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getXPosition());
            imageBoxPositions[xx][yy].setYPosition(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getYPosition());
            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeLeft(){
        if (emptyBoxPosition[1] >= 0 && emptyBoxPosition[1] <= PUZZLE_SIZE-2){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx,yy+1);

            imageBoxPositions[xx][yy].setXPosition(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getXPosition());
            imageBoxPositions[xx][yy].setYPosition(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getYPosition());
//            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);
        }
    }

    private void swipeRight(){
        if (emptyBoxPosition[1] >= 1 && emptyBoxPosition[1] <= PUZZLE_SIZE-1){

            int xx = emptyBoxPosition[0];
            int yy = emptyBoxPosition[1];
            swapImageAndEmptyBox( xx,yy-1);

            imageBoxPositions[xx][yy].setXPosition(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getXPosition());
            imageBoxPositions[xx][yy].setYPosition(imageBoxPositions[emptyBoxPosition[0]][emptyBoxPosition[1]].getYPosition());
//            System.out.println("value "+xx+" "+yy+" "+emptyBoxPosition[0]+" "+emptyBoxPosition[1]);

        }
    }

    private void swapImageAndEmptyBox(int i, int j){
        float x = emptyBox.getX();
        float y = emptyBox.getY();

//        System.out.println(x+" "+y);
        emptyBox.setX(imageBoxPiece[imageBoxPositions[i][j].getXPosition()][imageBoxPositions[i][j].getYPosition()].getX());
        emptyBox.setY(imageBoxPiece[imageBoxPositions[i][j].getXPosition()][imageBoxPositions[i][j].getYPosition()].getY());


        imageBoxPiece[imageBoxPositions[i][j].getXPosition()][imageBoxPositions[i][j].getYPosition()].setX(x);
        imageBoxPiece[imageBoxPositions[i][j].getXPosition()][imageBoxPositions[i][j].getYPosition()].setY(y);

        emptyBoxPosition[0] = i;
        emptyBoxPosition[1] = j;

        if (totalTimeSpend>1) {
            soundAndVibration.playBoxSlideSound();
        }
        updateMovesCount();
    }

    private void checkGameWon(){
        checkImageAnswerPosition();
        if (won){
            isGameStarted = false;
            new Handler().postDelayed(() -> {
                gameOverLayoutVisibility(View.VISIBLE);
            },500);
        }
    }

    private void checkImageAnswerPosition(){
        correctCount =0;
        for (int x =0; x<PUZZLE_SIZE; x++){
            for (int y = 0; y<PUZZLE_SIZE; y++){
                if (imageBoxPositions[x][y].getXPosition() == x && imageBoxPositions[x][y].getYPosition() == y){
                    correctCount++;
//                    System.out.println(correctCount);
                    if (correctCount == PUZZLE_SIZE*PUZZLE_SIZE){
//                        Toast.makeText(getApplicationContext(), "You won.",Toast.LENGTH_SHORT).show();
                        won = true;
                    }
//                    totalEarnedCoins = coins.generateCoins(correctCount);
                }
            }
        }
    }

    private void visibilityForKeyBoardLayout(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        Animation topSlideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_spring);
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
        topSlideAnimation.setDuration(500);
        fadeOutAnimation.setDuration(500);

        if (height - keyBoardLayout.getY()-120>keyBoardLayout.getHeight()){
            isKeyEnable = soundAndVibration.getControllerType();
//            largeImageDialog.setKeyboardStatusText(true);
            if (isKeyEnable){
                keyBoardLayout.setAnimation(topSlideAnimation);
                keyBoardLayout.setVisibility(View.VISIBLE);
            }else {
                if (keyBoardLayout.getVisibility() == View.VISIBLE) {
                    keyBoardLayout.setAnimation(fadeOutAnimation);
                }
                keyBoardLayout.setVisibility(View.GONE);
            }
        }else {
            isKeyEnable = false;
//            largeImageDialog.setKeyboardStatusText(false);
            keyBoardLayout.setVisibility(View.GONE);
//            Toast.makeText(getApplicationContext(), "Key Doesn't Support.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateMovesCount(){
        if (isGameStarted){
            movesCount--;
        }else {
            movesCount++;
        }
        if (movesCount < 2) {
            movesCountView.setText("Move: " + movesCount);
            if (movesCount == 0){
                gameOverLayoutVisibility(View.VISIBLE);
            }
        }else {
            movesCountView.setText("Moves: "+movesCount);
        }

    }

    public void gameOverLayoutVisibility(int visibility){
        gameOverDialog.setDialogStatus(won, PUZZLE_SIZE);

        if (visibility == View.GONE){

            Animation darkScreenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
            darkScreen.setAnimation(darkScreenAnimation);
            darkScreen.setVisibility(View.GONE);


            Animation gameOverAnimationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
            gameOverViewLayout.setAnimation(gameOverAnimationOut);
            gameOverViewLayout.setVisibility(View.GONE);
        }else {
            Animation darkScreenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_in);
            darkScreen.setAnimation(darkScreenAnimation);
            darkScreen.setVisibility(View.VISIBLE);

            Animation gameOverAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_spring);
            gameOverAnimation.setDuration(300);
            gameOverViewLayout.setAnimation(gameOverAnimation);
            gameOverViewLayout.setVisibility(View.VISIBLE);
        }
    }

    public void largeImageLayoutVisibility(int visibility){
        if (visibility == View.GONE){
            visibilityForKeyBoardLayout();

            Animation darkScreenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
            darkScreen.setAnimation(darkScreenAnimation);
            darkScreen.setVisibility(View.GONE);


            Animation gameOverAnimationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
            largerImageViewLayout.setAnimation(gameOverAnimationOut);
            largerImageViewLayout.setVisibility(View.GONE);

            closeDarkScreen.setVisibility(View.GONE);
            closeDarkScreen.setAnimation(darkScreenAnimation);
        }else {
            Animation darkScreenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_in);
            darkScreen.setAnimation(darkScreenAnimation);
            closeDarkScreen.setAnimation(darkScreenAnimation);
            darkScreen.setVisibility(View.VISIBLE);
            closeDarkScreen.setVisibility(View.VISIBLE);

            Animation gameOverAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_spring);
            gameOverAnimation.setDuration(300);
            largerImageViewLayout.setAnimation(gameOverAnimation);
            largerImageViewLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadGameBannerAds(){

        bannerAdView.loadAd(adRequest);
        bannerAdView.setAdListener(new AdListener() {
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
    }

    @SuppressLint("SetTextI18n")
    private void showAddMovesRewardHigh(){
        if (movesRewardedAdsHigh != null) {
            Activity activityContext = MainGameActivity.this;
            movesRewardedAdsHigh.show(activityContext, rewardItem -> {
                // Handle the reward.
                Log.d(TAG, "The user earned the reward.");
                if (won){
                    if (!isCoinsDoubledWithAds) {
                        isCoinsDoubledWithAds = true;
                        TextView coinTempView = gameOverViewLayout.findViewById(R.id.earnedScoreViewGameOver);
                        coinTempView.setText(String.valueOf(Integer.parseInt(coinTempView.getText().toString()) * 2));
                    }else {
                        Toast.makeText(getApplicationContext(), "Coins already increased.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    movesCount = 61;
                    updateMovesCount();
                    isGameStarted = true;
                    gameOverLayoutVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Reward is Not Ready", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }

    }


    private void loadMovesRewardHigh(){

        // Video Ads for moves 60
        RewardedAd.load(this, getResources().getString(R.string.moveVideoReward), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                Log.d(TAG, "here: "+loadAdError);
                movesRewardedAdsHigh = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                movesRewardedAdsHigh = rewardedAd;

                movesRewardedAdsHigh.setFullScreenContentCallback(new FullScreenContentCallback() {
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

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when ad fails to show.
                        Log.e(TAG, "Ad failed to show fullscreen content.");
                        movesRewardedAdsHigh = null;
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
    @SuppressLint("SetTextI18n")
    private void showAddMovesRewardLow(){
        if (moveRewardedAdsLow != null) {
            Activity activityContext = MainGameActivity.this;
            moveRewardedAdsLow.show(activityContext, rewardItem -> {
                // Handle the reward.
                Log.d(TAG, "The user earned the reward.");
                if (won){if (!isCoinsDoubledWithAds) {
                    isCoinsDoubledWithAds = true;
                    TextView coinTempView = gameOverViewLayout.findViewById(R.id.earnedScoreViewGameOver);
                    coinTempView.setText(String.valueOf((int) (Integer.parseInt(coinTempView.getText().toString()) * 1.5)));
                }else {
                    Toast.makeText(getApplicationContext(), "Coins already increased.", Toast.LENGTH_SHORT).show();
                }
                }else {
                    movesCount = 61;
                    updateMovesCount();
                    isGameStarted = true;
                    gameOverLayoutVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Reward is Not Ready", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }

    }


    private void loadMovesRewardLow(){

        // Interstitial Ads for moves 30

        RewardedInterstitialAd.load(MainGameActivity.this, getResources().getString(R.string.moveRewardInterstitial),
                new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        Log.d(TAG, "Ad was loaded.");
                        moveRewardedAdsLow = ad;
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        moveRewardedAdsLow = null;
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void updateControllerSwitchView(boolean type){
        if (type){
            controllerTextView.setText("Key");
        }else {
            controllerTextView.setText("Swipe");
        }
    }


    private void saveAllStateOfGame() {
        if (selectedGameLevel == 0){
            mission.increasePlayedCount(1);
        }else {
            mission.increasePlayedCount(PUZZLE_SIZE);
            if (won) {
                imageLevel.setActiveLevel(PUZZLE_SIZE,selectedGameLevel+1);
            }
        }
        mission.increaseTimeSpendInGame(totalTimeSpend-1);

        if (won) {
            playerLife.increaseLife();
        }

    }

    private void goHome(){
        Animation loadInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_in);
        mainGameLoadingScreen.setAnimation(loadInAnimation);
        mainGameLoadingScreen.setVisibility(View.VISIBLE);
        new Handler().postDelayed(()->{
            startActivity(new Intent(getApplicationContext(), GameHome.class));
            overridePendingTransition(0, 0);
            finish();
        },350);
    }


}