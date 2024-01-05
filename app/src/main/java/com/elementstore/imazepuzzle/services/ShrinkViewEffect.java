package com.elementstore.imazepuzzle.services;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

public class ShrinkViewEffect {

    private final float SHRINK_VALUE = 0.85F;
    private final long DURATION_ANIMATION = 80L;

    @SuppressLint("ClickableViewAccessibility")
    public ShrinkViewEffect(View view){
        if (!view.hasOnClickListeners()){
            view.setOnClickListener(view1 -> {});
        }
        view.setOnTouchListener((view1, motionEvent) -> {
            System.out.println(motionEvent);
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                buildShrinkAnimator(view1).start();
            }else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                buildGrowAnimator(view1).start();
            }
            return false;
        });
    }

    private Animator buildShrinkAnimator(View view){
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, SHRINK_VALUE);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, SHRINK_VALUE);

        Animator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        animator.setDuration(DURATION_ANIMATION);

        return animator;
    }

    private Animator buildGrowAnimator(View view){
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, SHRINK_VALUE, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, SHRINK_VALUE, 1f);
        Animator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        animator.setDuration(DURATION_ANIMATION);
        return animator;
    }

    public static View applyClick(View view){
        new ShrinkViewEffect(view);
        return view;
    }
}
