package com.elementstore.imazepuzzle.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.Mission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MissionViewAdapter extends RecyclerView.Adapter<MissionViewAdapter.ViewHolder> {

    Context context;
    JSONArray missionListArray;
    MissionItemClick missionItemClick;
    Mission mission;
    boolean isOneTimeMission;
    public MissionViewAdapter(Context context,boolean isOneTimeMission, JSONArray missionListArray, MissionItemClick missionItemClick){
        this.context = context;
        this.isOneTimeMission = isOneTimeMission;
        this.missionListArray = missionListArray;
        this.missionItemClick = missionItemClick;
        mission = new Mission(context);
    }

    @NonNull
    @Override
    public MissionViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_mission_view, parent, false));
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull MissionViewAdapter.ViewHolder holder, int position) {
        try {
            String name = missionListArray.getJSONObject(position).getString("name");
            holder.rewardCoinView.setText(missionListArray.getJSONObject(position).getString("value"));

            if (isOneTimeMission) {
                holder.description.setText(name);
                setButtonState(holder.collectButton, mission.isOneTimeMissionCompleted(name), mission.isOneTimeMissionCollected(name));
            }else {
                String key = missionListArray.getJSONObject(position).getString("key");
                String desc = name;
                if (!key.equals("spendTime")) {
                    if (key.equals("threeTimeCI1")) {
                        desc += " (" + mission.getPlayedCount(1) + ")";
                    } else if (key.equals("anyFiveTime0")) {
                        desc += " (" + mission.getPlayedCount(0) + ")";
                    } else if (key.equals("threeSize")) {
                        desc += " (" + mission.getPlayedCount(3) + ")";
                    } else if (key.equals("fourSize")) {
                        desc += " (" + mission.getPlayedCount(4) + ")";
                    } else if (key.equals("fiveSize")) {
                        desc += " (" + mission.getPlayedCount(5) + ")";
                    }
                }else {
                    desc += " (" + mission.getTotalTimeSpendInGame() / 60 + ")";
                    holder.missionImageview.setText(context.getString(R.string.infinity));
                    holder.rewardCoinView.setText("60 Min");
                    holder.missionImageview.setBackground(context.getDrawable(R.drawable.red_heart));
                }
                holder.description.setText(desc);
                setButtonState(holder.collectButton, mission.isDailyMissionCompleted(key), mission.isDailyMissionCollected(key));

                if (position == 0) {
                    holder.collectButton.setText("Get Coins");
                    holder.rewardCoinView.setVisibility(View.GONE);
                    holder.collectButton.setVisibility(View.GONE);
                    holder.adsIconView.setVisibility(View.VISIBLE);
                }

            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        holder.collectButton.setOnClickListener(view -> {
            try {
                missionItemClick.onMissionItemClick(missionListArray.getJSONObject(position), position);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        holder.adsIconView.setOnClickListener(view -> {
            try {
                missionItemClick.onMissionItemClick(missionListArray.getJSONObject(position), position);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public int getItemCount() {
        return missionListArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView missionImageview,description, rewardCoinView, collectButton, adsIconView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            missionImageview = itemView.findViewById(R.id.missionImageView);
            description = itemView.findViewById(R.id.missionDescription);
            rewardCoinView = itemView.findViewById(R.id.rewardCoinView);
            collectButton = itemView.findViewById(R.id.collectButton);
            adsIconView = itemView.findViewById(R.id.adsIconView);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setButtonState(TextView button, boolean completedState, boolean collectedState){
        if (completedState) {
            if (collectedState) {
                button.setBackgroundTintList(context.getColorStateList(R.color.mainCyan));
                button.setText("Collected");
                button.setEnabled(false);
            } else {
                button.setBackgroundTintList(context.getColorStateList(R.color.golden));
                button.setText("Collect");
                button.setEnabled(true);
            }
        }else {
            button.setBackgroundTintList(context.getColorStateList(R.color.lightCyan));
            button.setEnabled(false);
        }
    }

    public interface MissionItemClick{
        void onMissionItemClick(JSONObject object, int index);
    }
}