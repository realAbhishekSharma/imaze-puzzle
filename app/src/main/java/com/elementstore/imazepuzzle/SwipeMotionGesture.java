package com.elementstore.imazepuzzle;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class SwipeMotionGesture extends GestureDetector.SimpleOnGestureListener{

    int threshold = 100;
    int velocityThreshold = 100;

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
//                return super.onFling(e1, e2, velocityX, velocityY);
        float xDifference = e2.getX() - e1.getX();
        float yDifference = e2.getY() - e1.getY();

        try{

            if (Math.abs(xDifference)> Math.abs(yDifference)){
                if (Math.abs(xDifference)> threshold && Math.abs(velocityX)>velocityThreshold){
                    if (xDifference > 0){
                        onSwipeRight();
                        System.out.println("Right");
                    }else {
                        onSwipeLeft();
                        System.out.println("Left");
                    }
                    return true;

                }
            }else {
                if (Math.abs(yDifference)> threshold && Math.abs(velocityY)>velocityThreshold){
                    if (yDifference > 0){
                        onSwipeDown();
                        System.out.println("Down");
                    }else {
                        onSwipeUp();
                        System.out.println("Up");
                    }
                    return true;

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public void onSwipeUp(){

    }

    public void onSwipeDown(){

    }

    public void onSwipeRight(){

    }

    public void onSwipeLeft(){

    }
}
