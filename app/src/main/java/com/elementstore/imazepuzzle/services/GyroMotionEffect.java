package com.elementstore.imazepuzzle.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;

public class GyroMotionEffect  implements SensorEventListener {

    private View item, background;
    private final int SHIFT_FACTOR = 10;
    float[] itemInitialValue = {0,0};
    float[] bgInitialValue = {0,0};
    private SensorManager sensorManager;
    @SuppressLint("ClickableViewAccessibility")
    public GyroMotionEffect(Context context, View item, View background){
        this.item = item;
        this.background = background;
        itemInitialValue[0] = item.getX();
        itemInitialValue[1] = item.getY();

        bgInitialValue[0] = background.getX();
        bgInitialValue[1] = background.getY();
/*

        background.setOnTouchListener((view, motionEvent) -> {
//            System.out.println(motionEvent.getX());
            if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL){

            }
            System.out.println(motionEvent);
//            System.out.println(motionEvent.getX() + background.getX());
//            System.out.println(background.getX());
            return true;
        });

//        System.out.println(bgInitialValue[0] +"  " + bgInitialValue[1]);
*/

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            item.setTranslationX(itemInitialValue[0]+sensorEvent.values[0]*SHIFT_FACTOR);
//            item.setTranslationY(itemInitialValue[1]+sensorEvent.values[1]*SHIFT_FACTOR);


            background.setTranslationX(bgInitialValue[0]-sensorEvent.values[0]*SHIFT_FACTOR);
//            background.setTranslationY(bgInitialValue[1]-sensorEvent.values[1]*SHIFT_FACTOR);


//            System.out.println("value x:" + sensorEvent.values[0] + "    value y:" + sensorEvent.values[1]);
//            System.out.println("value x:" + (bgInitialValue[0]+sensorEvent.values[0]*SHIFT_FACTOR) + "    value y:" + (bgInitialValue[1]+sensorEvent.values[1]*SHIFT_FACTOR));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
