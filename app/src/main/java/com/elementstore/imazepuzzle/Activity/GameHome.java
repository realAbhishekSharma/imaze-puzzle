package com.elementstore.imazepuzzle.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.elementstore.imazepuzzle.Adapter.GridViewAdapter;
import com.elementstore.imazepuzzle.Adapter.LevelGameSlider;
import com.elementstore.imazepuzzle.Adapter.SizeSelectionSliderAdapter;
import com.elementstore.imazepuzzle.ImageIdList;
import com.elementstore.imazepuzzle.ImageLevel;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.dialogs.UpdateRewardDialog;
import com.elementstore.imazepuzzle.fragments.SettingsFragment;
import com.elementstore.imazepuzzle.services.Coins;
import com.elementstore.imazepuzzle.services.GameTracker;
import com.elementstore.imazepuzzle.services.GyroMotionEffect;
import com.elementstore.imazepuzzle.services.PlayerLife;
import com.elementstore.imazepuzzle.services.ShrinkViewEffect;
import com.elementstore.imazepuzzle.services.SliderBoxTheme;
import com.elementstore.imazepuzzle.services.SoundAndVibration;
import com.github.drjacky.imagepicker.ImagePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameHome extends AppCompatActivity {

    int puzzleSize, selectedGameLevel;
    int entryFee;
    TextView coinsView;

    SoundAndVibration soundAndVibration;
    Coins coins;
    ImageLevel imageLevel;
    LinearLayout missionsButton, lifeButton;

    ConstraintLayout topContainerView, mainFrameLayoutView, loadingScreenView, levelMoveToStart;

    TextView mainBackground, settingButton, missionFloatingButton, lifeFloatingButton, startButton, darkTransparentView, closeDarkTransparentScreen;
    ImageView mainFrameImageViewHome;
    TextView lifeNotificationGameHome, missionNotificationGameHome;
    TextView sizeThreeSelection, sizeFourSelection, sizeFiveSelection;

    SharedPreferences ratePref;

    GridView levelGridView;
    GridViewAdapter gridViewAdapter;

    List<Integer> levelImageList;
    ImageIdList imageIdList;

    SliderBoxTheme sliderBoxTheme;

    SharedPreferences sharedPreferences;
    PlayerLife playerLife;

    GameTracker gameTracker;
    private UpdateRewardDialog updateRewardDialog;

    private ViewPager2 levelSelectSlider, sizeSelectionSlider;
    private LevelGameSlider levelGameSliderAdapter;

    private ShrinkViewEffect shrinkViewEffect;
    private FragmentTransaction fragmentTransaction;
    private View fragmentContainerView;
    ActivityResultLauncher<Intent> launcher;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.cloudCyan));
        setContentView(R.layout.activity_game_home);


        levelImageList = new ArrayList<>();
        imageIdList = new ImageIdList();
        levelImageList = imageIdList.getImageIdList();


        levelSelectSlider = findViewById(R.id.levelSelectViewGameHome);
        sizeSelectionSlider = findViewById(R.id.SizeSelectionSliderHome);


        ratePref = getSharedPreferences("rateData", MODE_PRIVATE);
        boolean rate = ratePref.getBoolean("rate", false);

        gameTracker = new GameTracker(this);
        soundAndVibration = new SoundAndVibration(this);
        coins = new Coins(this);
        imageLevel = new ImageLevel(this);
        sliderBoxTheme = new SliderBoxTheme(this);
        playerLife = new PlayerLife(this);
        updateRewardDialog = new UpdateRewardDialog(this);
        sharedPreferences = getSharedPreferences("SizeState", MODE_PRIVATE);


        coinsView = findViewById(R.id.topCoinTextHome);

        topContainerView = findViewById(R.id.topContainer);
        mainFrameLayoutView = findViewById(R.id.mainFrameLayout);

        mainBackground = findViewById(R.id.mainBackgroundHome);
        settingButton = findViewById(R.id.topSettingViewHome);
        missionFloatingButton = findViewById(R.id.missionFloatingButton);
        lifeFloatingButton = findViewById(R.id.lifeFloatingButton);
        missionNotificationGameHome = findViewById(R.id.missionNotificationGameHome);
        lifeNotificationGameHome = findViewById(R.id.lifeNotificationGameHome);
        mainFrameImageViewHome = findViewById(R.id.mainFrameImageViewHome);
        startButton = findViewById(R.id.startButtonHome);
        loadingScreenView = findViewById(R.id.loadingScreenGameHome);
        levelMoveToStart = findViewById(R.id.levelMoveToStartButton);

        darkTransparentView = findViewById(R.id.darkTransparentScreen);
        closeDarkTransparentScreen = findViewById(R.id.closeDarkTransparentScreen);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        fragmentContainerView.setVisibility(View.INVISIBLE);

        darkTransparentView.setOnClickListener(view ->{});
        loadingScreenView.setOnClickListener(view ->{});

        closeDarkTransparentScreen.setOnClickListener(view ->{
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            setTransparentScreenVisibility(View.INVISIBLE);
        });

        settingButton.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            setTransparentScreenVisibility(View.VISIBLE);
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, new SettingsFragment());
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.commitNow();
        });

        missionFloatingButton.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            startActivity(new Intent(this, MissionActivity.class));
            overridePendingTransition(R.anim.fading_in,R.anim.fading_out);
            finish();
        });

        lifeFloatingButton.setOnClickListener(view -> {
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
            startActivity(new Intent(this, LifeShopActivity.class));
            overridePendingTransition(R.anim.fading_in,R.anim.fading_out);
            finish();
        });

        startButton.setOnClickListener(view -> {
            onStartClick();

            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();


        });

        levelMoveToStart.setOnClickListener(item ->{
            levelSelectSlider.setCurrentItem(0);
            soundAndVibration.doClickVibration();
            soundAndVibration.playNormalClickSound();
        });




        shrinkViewEffect = new ShrinkViewEffect(settingButton);
        shrinkViewEffect = new ShrinkViewEffect(startButton);
        shrinkViewEffect = new ShrinkViewEffect(missionFloatingButton);
        shrinkViewEffect = new ShrinkViewEffect(lifeFloatingButton);
        shrinkViewEffect = new ShrinkViewEffect(closeDarkTransparentScreen);
        new GyroMotionEffect(this, startButton, mainBackground);




        puzzleSize = getSizeSelectionState();
        selectedGameLevel = imageLevel.getCompletedMaxLevel(puzzleSize);
        entryFee = getEntryFee();
        gridViewAdapter = new GridViewAdapter(this, levelImageList, puzzleSize);

        updateRewardDialog.openDialogBox();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateRewardDialog.openDialogBox();
            }
        }, 2000);

        if (!rate && imageLevel.getActiveLevel(4)[4]) {
            new Handler().postDelayed(this::openRateBox, 2000);

        }

        sizeSelectionSlider.setAdapter(new SizeSelectionSliderAdapter(this));

        sizeSelectionSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 0){
                    puzzleSize = 3;
                }else if (position == 1){
                    puzzleSize = 4;
                }else if (position == 2){
                    puzzleSize = 5;
                }
                if (selectedGameLevel != 0){
                    selectedGameLevel = imageLevel.getCompletedMaxLevel(puzzleSize);
                    setAdapterOnSizeSelectionChange();
                }
                setSizeSelectionState(puzzleSize);
            }
        });

        sizeSelectionSlider.setCurrentItem(puzzleSize-3, true);
        setAdapterOnSizeSelectionChange();


        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(10));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.2f);
                page.setScaleX(0.85f + r * 0.2f);
            }
        });
        levelSelectSlider.setPageTransformer(transformer);


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                    if(result.getResultCode()==RESULT_OK){
                        Uri uri=result.getData().getData();
                        try {
                            Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                            imageIdList.saveBitmap(getApplicationContext(), selectedBitmap);
                            mainFrameImageViewHome.setImageBitmap(imageIdList.getBitmap(getApplicationContext()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println(uri);
                    }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                        setAdapterOnSizeSelectionChange();
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    }else if (result.getResultCode()== RESULT_CANCELED){
                        setAdapterOnSizeSelectionChange();
                        Toast.makeText(getApplicationContext(), "No Image Selected.", Toast.LENGTH_SHORT).show();

                    }
                });

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            gameTracker = new GameTracker(this);
            updateLifeIcon();
            coinsView.setText(String.valueOf(coins.getTotalCoins()));
//            setTransparentScreenVisibility(View.INVISIBLE);

            if (gameTracker.getMissionBallNotification()) {
                missionNotificationGameHome.setVisibility(View.VISIBLE);
            }else {
                missionNotificationGameHome.setVisibility(View.GONE);
            }
            if (loadingScreenView.getVisibility() == View.VISIBLE){
                new Handler().postDelayed(()->{
                    Animation loadOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
                    loadingScreenView.setAnimation(loadOutAnimation);
                    loadingScreenView.setVisibility(View.INVISIBLE);
                },10);
            }

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setAdapterOnSizeSelectionChange(){
        int maxLevel = imageLevel.getCompletedMaxLevel(puzzleSize);
        levelGameSliderAdapter = new LevelGameSlider(this, puzzleSize, levelImageList, new LevelGameSlider.LevelGameSliderItemClick() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onItemClicked(View view, int index) {
                if (index == 0) {
                    openCustomImageSelection();
                    selectedGameLevel = index;
                }else if (maxLevel >= index) {
                    selectedGameLevel = index;
                    mainFrameImageViewHome.setImageBitmap(null);
                    mainFrameImageViewHome.setBackground(getDrawable(levelImageList.get(index)));
                }
                soundAndVibration.doClickVibration();
                soundAndVibration.playNormalClickSound();
//                Toast.makeText(getApplicationContext(), "item click "+index, Toast.LENGTH_SHORT).show();
            }
        });

        levelSelectSlider.setClipToPadding(false);
        levelSelectSlider.setClipChildren(false);
        levelSelectSlider.setOffscreenPageLimit(3);
        levelSelectSlider.setCurrentItem(0);
        levelSelectSlider.setCurrentItem(maxLevel, true);
        mainFrameImageViewHome.setImageBitmap(null);
        mainFrameImageViewHome.setBackground(getDrawable(levelImageList.get(maxLevel)));
        levelSelectSlider.setAdapter(levelGameSliderAdapter);
    }

    private void setTransparentScreenVisibility(int isVisible){
        if (isVisible == View.VISIBLE) {
            closeDarkTransparentScreen.setVisibility(View.VISIBLE);
            darkTransparentView.setVisibility(View.VISIBLE);
            fragmentContainerView.setVisibility(View.VISIBLE);

            Animation darkScreenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_in);
            darkTransparentView.setAnimation(darkScreenAnimation);

            Animation fragmentContainerAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_spring);
            fragmentContainerView.setAnimation(fragmentContainerAnimation);

            topContainerView.setVisibility(View.INVISIBLE);
            mainFrameLayoutView.setVisibility(View.INVISIBLE);
            levelSelectSlider.setVisibility(View.INVISIBLE);
            missionFloatingButton.setVisibility(View.INVISIBLE);
            lifeFloatingButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.INVISIBLE);
            levelMoveToStart.setVisibility(View.INVISIBLE);


        }else if (isVisible == View.INVISIBLE){
            closeDarkTransparentScreen.setVisibility(View.GONE);
            darkTransparentView.setVisibility(View.GONE);

            Animation fragmentContainerAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
            fragmentContainerView.setAnimation(fragmentContainerAnimation);
            fragmentContainerView.setVisibility(View.GONE);

            Animation topContainerViewAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_spring);
            topContainerView.setAnimation(topContainerViewAnimation);
            topContainerView.setVisibility(View.VISIBLE);

            Animation levelSliderShowAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_spring);
            levelSelectSlider.setAnimation(levelSliderShowAnimation);
            levelSelectSlider.setVisibility(View.VISIBLE);

            Animation mainFrameViewAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_spring);
            mainFrameLayoutView.setAnimation(mainFrameViewAnimation);
            mainFrameLayoutView.setVisibility(View.VISIBLE);

            missionFloatingButton.setAnimation(mainFrameViewAnimation);
            missionFloatingButton.setVisibility(View.VISIBLE);
            if (missionNotificationGameHome.getVisibility() == View.VISIBLE) {
                missionNotificationGameHome.setAnimation(mainFrameViewAnimation);
            }

            Animation startButtonAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_spring);
            startButton.setAnimation(startButtonAnimation);
            startButton.setVisibility(View.VISIBLE);

            levelMoveToStart.setAnimation(startButtonAnimation);
            levelMoveToStart.setVisibility(View.VISIBLE);

            lifeFloatingButton.setAnimation(startButtonAnimation);
            lifeFloatingButton.setVisibility(View.VISIBLE);
            if (lifeNotificationGameHome.getVisibility() ==  View.VISIBLE){
                lifeNotificationGameHome.setAnimation(startButtonAnimation);
            }
        }
    }

    private void openRateBox(){
        try {
            AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameHome.this);
            View view = getLayoutInflater().inflate(R.layout.rate_dialog_box, null);

            TextView okay, notAsk;
            okay = view.findViewById(R.id.okay);
            notAsk = view.findViewById(R.id.notAsk);
            dialogBox.setView(view);

            final AlertDialog alertDialog = dialogBox.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
            alertDialog.setCancelable(true);


            okay.setOnClickListener(view1 -> {
                ratePref.edit().putBoolean("rate", true).apply();
                Uri link = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                System.out.println(link);
                startActivity(new Intent(Intent.ACTION_VIEW, link));
                alertDialog.cancel();
            });

            notAsk.setOnClickListener(views -> {
                ratePref.edit().putBoolean("rate", true).apply();
                alertDialog.cancel();
            });

        }catch (Exception e){
            System.out.println("Error in Rate Dialog Box");
        }
    }

    private void onStartClick(){
        if (imageLevel.getActiveLevel(puzzleSize)[selectedGameLevel]) {
            if (coins.getTotalCoins() >= entryFee) {
                if (playerLife.getTotalLife() > 0 || playerLife.isActiveInfiniteLife()) {

                    Animation loadingScreenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_in);
                    loadingScreenView.setVisibility(View.VISIBLE);
                    loadingScreenView.setAnimation(loadingScreenAnimation);

                    new Handler().postDelayed(()->{
                        startActivity(new Intent(getApplicationContext(), MainGameActivity.class).putExtra("level", selectedGameLevel).putExtra("puzzleSize", puzzleSize));
                        overridePendingTransition(0, 0);
                    },400);

                    coins.cutCoin(entryFee);
                    playerLife.decreaseLife();

                } else {
                    Toast.makeText(getApplicationContext(), "No Remaining Life.", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getApplicationContext(), "Not Enough coins.", Toast.LENGTH_SHORT).show();
            }
        }

        if (selectedGameLevel == 0 && !imageLevel.getActiveLevel(puzzleSize)[0]){
            if(puzzleSize == 3) {
                Toast.makeText(getApplicationContext(), getText(R.string.customImageTextForThree), Toast.LENGTH_SHORT).show();
            }else if(puzzleSize == 4) {
                Toast.makeText(getApplicationContext(), getText(R.string.customImageTextForFour), Toast.LENGTH_SHORT).show();
            }else if(puzzleSize == 5) {
                Toast.makeText(getApplicationContext(), String.valueOf(getText(R.string.customImageTextForFive)), Toast.LENGTH_SHORT).show();
            }
        }
//        Toast.makeText(getApplicationContext(), entryFee+" coins deducted.", Toast.LENGTH_SHORT).show();
    }

    private void setSizeSelectionState(int sizeSelection){
        sharedPreferences.edit().putInt("Size",sizeSelection).apply();
    }

    private int getSizeSelectionState(){
        return sharedPreferences.getInt("Size", 3);
    }

    private int getEntryFee(){
        if (getSizeSelectionState() == 3)
            return 1500;
        else if (getSizeSelectionState() == 4)
            return 3000;
        else
            return 5000;
    }

    public void openCustomImageSelection() {
        launcher.launch(
                ImagePicker.Companion.with(this)
                        .crop(1,1)
                        .maxResultSize(1200,1200, true)
                        .cropSquare()
                        .galleryOnly()
                        .createIntent());
    }

    private void changeGridViewOnSizeClick(int size){

        if (getSizeSelectionState() != size) {
            Animation leftSlideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_left);
            leftSlideAnimation.setDuration(300);
            levelGridView.setAnimation(leftSlideAnimation);

            setSizeSelectionState(size);
            gridViewAdapter = new GridViewAdapter(this, levelImageList, size);
//            levelGridView.setAdapter(gridViewAdapter);

//            changeSizeSelectionBackground(size);
        }
    }

    private void changeSizeSelectionBackground(int size){
        if (size == 3) {
            sizeThreeSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkCyan)));
            sizeFourSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
            sizeFiveSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
        }else if (size == 4){
            sizeThreeSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
            sizeFourSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkCyan)));
            sizeFiveSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));

        }else if (size == 5){
            sizeThreeSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
            sizeFourSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mainCyan)));
            sizeFiveSelection.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkCyan)));

        }
    }


    @SuppressLint("SetTextI18n")
    private void updateLifeIcon(){
        if (playerLife.isActiveInfiniteLife()){
            lifeNotificationGameHome.setVisibility(View.GONE);
            lifeFloatingButton.setText(getString(R.string.infinity));
            lifeFloatingButton.setTypeface(Typeface.DEFAULT_BOLD);
        }else {
            lifeNotificationGameHome.setVisibility(View.VISIBLE);
            lifeFloatingButton.setText(String.valueOf(playerLife.getTotalLife()));
        }
    }
}