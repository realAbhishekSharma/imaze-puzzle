package com.elementstore.imazepuzzle.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.elementstore.imazepuzzle.ImageLevel;

import org.json.JSONArray;
import org.json.JSONException;

public class GameTracker {
    private String MISSION_TRACKER = "MISSION_TRACKER";
    private String TOTAL_TIME = "TOTAL_TIME";
    private SharedPreferences missionTracker;
    private Mission mission;
    private ImageLevel imageLevel;
    private Context context;
    private JSONArray oneTimeMission;
    private JSONArray dailyMission;
    public GameTracker(Context context){
        this.context = context;
        imageLevel = new ImageLevel(context);
        mission = new Mission(context);
        oneTimeMission = mission.getOneTimeMissions();
        missionTracker = context.getSharedPreferences(MISSION_TRACKER, Context.MODE_PRIVATE);
        checkAndUpdateDailyMissions();
        checkAndUpdateOneTimeMissions();
    }
    private void checkAndUpdateOneTimeMissions(){
        try {
            int index = 0;
            if (imageLevel.getCompletedMaxLevel(3) >= 2) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(4) >= 2) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(5) >= 2) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(3) >= 11) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(4) >= 6) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(5) >= 4) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(3) >= 10) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(4) >= 10) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(5) >= 10) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(3) >= 20) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(4) >= 20) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(5) >= 20) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(3) >= 35) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(4) >= 35) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(5) >= 35) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(3) >= 50) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(4) >= 50) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
            index++;
            if (imageLevel.getCompletedMaxLevel(5) >= 50) {
                mission.setOneTimeMissionIsCompleted(oneTimeMission.getJSONObject(index).getString("name"));
            }
        }catch (JSONException e){

        }
    }

    private void checkAndUpdateDailyMissions(){
        try {
            dailyMission = mission.getActiveDailyMissions();
            int index = 1;
            mission.setDailyMissionIsCompleted(dailyMission.getJSONObject(index).getString("key"));
            index++;
            if (mission.getTotalTimeSpendInGame() >= 30*60) {
                mission.setDailyMissionIsCompleted(dailyMission.getJSONObject(index).getString("key"));
            }
            index++;
            String key = dailyMission.getJSONObject(index).getString("key");

            if (key.equals("threeTimeCI1") && mission.getPlayedCount(1) >= 3) {
                mission.setDailyMissionIsCompleted(key);
            }else if (key.equals("anyFiveTime0") && mission.getPlayedCount(0) >= 5) {
                mission.setDailyMissionIsCompleted(key);
            }else if (key.equals("threeSize") && mission.getPlayedCount(3) >= 3) {
                mission.setDailyMissionIsCompleted(key);
            }else if (key.equals("fourSize") && mission.getPlayedCount(4) >= 2) {
                mission.setDailyMissionIsCompleted(key);
            }else if (key.equals("fiveSize") && mission.getPlayedCount(5) >= 1) {
                mission.setDailyMissionIsCompleted(key);
            }

        }catch (JSONException e){

        }
    }

    public boolean getMissionBallNotification() {
        try {
            for (int i = 0; i < oneTimeMission.length(); i++) {
                String name = oneTimeMission.getJSONObject(i).getString("name");
                if (mission.isOneTimeMissionCompleted(name)) {
                    if (!mission.isOneTimeMissionCollected(name)) {
                        return true;
                    }
                }
            }
            for (int i = 0; i < dailyMission.length(); i++) {
                String key = dailyMission.getJSONObject(i).getString("key");
                if (mission.isDailyMissionCompleted(key)) {
                    if (!mission.isDailyMissionCollected(key)) {
                        return true;
                    }
                }
            }
        }catch (JSONException e){
        }
        return false;
    }

}
