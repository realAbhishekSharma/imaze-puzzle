package com.elementstore.imazepuzzle.services;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Mission {

    SharedPreferences sharedPreferences;
    private String DATABASE_STRING = "MISSIONS";
    private String DATE_STRING = "DATE";
    private String MISSION_TRACKER = "MISSION_TRACKER";
    private String COMPLETE = "COMPLETE";
    private String CURRENT_DATE = LocalDate.now().toString();
    private Context context;
    JSONObject missionData;
    JSONArray dailyMissions;
    JSONArray oneTimeMission;

    public Mission(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(DATABASE_STRING, Context.MODE_PRIVATE);

        try{
            InputStream inputStream = context.getAssets().open("missions.json");
            Scanner scanner = new Scanner(inputStream).useDelimiter("//A");
            String jsonString = scanner.hasNext()? scanner.next():"";
            scanner.close();
            missionData = new JSONObject(jsonString);
            dailyMissions = missionData.getJSONArray("dailyMissions");
            oneTimeMission = missionData.getJSONArray("oneTimeMissions");

        }catch (IOException | JSONException ioException){
            System.out.println(ioException.getMessage());
        }
    }

    private String getMissionDate() {
        return sharedPreferences.getString(DATE_STRING, CURRENT_DATE);
    }

    private void setMissionDateToday() {
        sharedPreferences.edit().putString(DATE_STRING, CURRENT_DATE).apply();
    }

    private boolean isMissionDateIsCurrentDate() {
        return (getMissionDate() == CURRENT_DATE);
    }

    private void assignDailyMissionsForToday() throws JSONException {
        Set<String> dailyMissionsSet = new HashSet<>();
        dailyMissionsSet.add("0");
        dailyMissionsSet.add("1");
        dailyMissionsSet.add("2");
        int randomNumber = (int) (Math.random()*(dailyMissions.length()-3))+3;
        System.out.println(dailyMissions.length());
        System.out.println(dailyMissions.getJSONObject(0).getString("key"));
        dailyMissionsSet.add(String.valueOf(randomNumber));
        sharedPreferences.edit().putStringSet(CURRENT_DATE, dailyMissionsSet).apply();
    }

    public JSONArray getActiveDailyMissions() throws JSONException {
        JSONArray data = new JSONArray();

        if (sharedPreferences.getStringSet(CURRENT_DATE, null) == null || !isMissionDateIsCurrentDate()){
            assignDailyMissionsForToday();
        }
        List selectedMission = new ArrayList(sharedPreferences.getStringSet(CURRENT_DATE, null));
        for (Object index: selectedMission){
            data.put(dailyMissions.getJSONObject(Integer.parseInt((String) index)));
        }
        return data;
    }

    public boolean isDailyMissionCollected(String missionKey) {
        return sharedPreferences.getBoolean(getMissionDate()+missionKey, false);
    }

    public void setDailyMissionIsCollected(String missionKey) {
        sharedPreferences.edit().putBoolean(getMissionDate()+missionKey, true).apply();
    }

    public boolean isDailyMissionCompleted(String missionKey) {
        return sharedPreferences.getBoolean(getMissionDate()+missionKey+COMPLETE, false);
    }

    public void setDailyMissionIsCompleted(String missionKey) {
        sharedPreferences.edit().putBoolean(getMissionDate()+missionKey+COMPLETE, true).apply();
    }

    public boolean isOneTimeMissionCollected(String missionKey) {
        return sharedPreferences.getBoolean(missionKey, false);
    }

    public void setOneTimeMissionIsCollected(String missionKey) {
        sharedPreferences.edit().putBoolean(missionKey, true).apply();
    }

    public boolean isOneTimeMissionCompleted(String missionKey) {
        return sharedPreferences.getBoolean(missionKey+COMPLETE, false);
    }

    public void setOneTimeMissionIsCompleted(String missionKey) {
        sharedPreferences.edit().putBoolean(missionKey+COMPLETE, true).apply();
    }

    public JSONArray getOneTimeMissions(){
        return oneTimeMission;
    }


    public void increaseTimeSpendInGame(int second){
        sharedPreferences.edit().putInt(getMissionDate()+MISSION_TRACKER, getTotalTimeSpendInGame()+second).apply();
    }

    public int getTotalTimeSpendInGame(){
        return sharedPreferences.getInt(getMissionDate()+MISSION_TRACKER, 0);
    }


    public int getPlayedCount(int size){
        return sharedPreferences.getInt(getMissionDate()+"COUNT"+size, 0);
    }
    public void increasePlayedCount(int size){
        sharedPreferences.edit().putInt(getMissionDate()+"COUNT"+size, getPlayedCount(size)+1).apply();
        sharedPreferences.edit().putInt(getMissionDate()+"COUNT0", getAllPlayedCount()+1).apply();
    }

    public int getAllPlayedCount(){
        return sharedPreferences.getInt(getMissionDate()+"COUNT0", 0);
    }


}
