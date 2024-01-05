package com.elementstore.imazepuzzle.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.elementstore.imazepuzzle.ImageLevel;
import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.ShrinkViewEffect;

import java.util.List;

public class LevelGameSlider extends RecyclerView.Adapter<LevelGameSlider.SliderViewHolder> {
    private List<Integer> imageIdLevelList;
    private Context context;
    private LevelGameSliderItemClick levelGameSliderItemClick;
    private ImageLevel imageLevel;
    private int puzzleSize;
    public LevelGameSlider(Context context, int puzzleSize, List<Integer> imageIdLevelList, LevelGameSliderItemClick levelGameSliderItemClick){
        this.context = context;
        this.puzzleSize = puzzleSize;
        this.imageIdLevelList = imageIdLevelList;
        this.levelGameSliderItemClick = levelGameSliderItemClick;

        imageLevel = new ImageLevel(context);
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.level_view_grid_item, parent, false)
        );
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull LevelGameSlider.SliderViewHolder holder, int position) {

        holder.imageLevel.setBackground(context.getDrawable(imageIdLevelList.get(position)));

        if (!imageLevel.getActiveLevel(puzzleSize)[position]){
            holder.levelActiveView.setVisibility(View.VISIBLE);
            if (position != 0) {
                holder.levelNumberView.setText(position + "");
            }
        }

        new ShrinkViewEffect(holder.imageLevel);

        holder.imageLevel.setOnClickListener(view -> {
            levelGameSliderItemClick.onItemClicked(view, position);
        });

    }

    @Override
    public int getItemCount() {
        return imageIdLevelList.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageLevel;
        private TextView levelNumberView;

        private LinearLayout levelActiveView;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLevel = itemView.findViewById(R.id.gridItemImageView);
            levelNumberView = itemView.findViewById(R.id.levelNumberView);
            levelActiveView = itemView.findViewById(R.id.itemForegroundView);



        }
    }
    public interface LevelGameSliderItemClick{
        void onItemClicked(View view, int index);
    }
}