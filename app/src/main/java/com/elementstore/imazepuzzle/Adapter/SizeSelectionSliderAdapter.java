package com.elementstore.imazepuzzle.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elementstore.imazepuzzle.R;

import java.util.ArrayList;
import java.util.List;

public class SizeSelectionSliderAdapter extends  RecyclerView.Adapter<SizeSelectionSliderAdapter.SliderViewHolder> {
    private List<Integer> imageIdList;
    private Context context;

    public SizeSelectionSliderAdapter(Context context) {
        this.context = context;
        this.imageIdList = new ArrayList<>();
        this.imageIdList.add(R.drawable.three_size_frame);
        this.imageIdList.add(R.drawable.four_size_frame);
        this.imageIdList.add(R.drawable.five_size_frame);
    }

    @NonNull
    @Override
    public SizeSelectionSliderAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SizeSelectionSliderAdapter.SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.size_selection_slider_single_item, parent, false)
        );
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull SizeSelectionSliderAdapter.SliderViewHolder holder, int position) {

        holder.sizeImage.setBackground(context.getDrawable(imageIdList.get(position)));

    }

    @Override
    public int getItemCount() {
        return imageIdList.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {

        private ImageView sizeImage;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            sizeImage = itemView.findViewById(R.id.sizeSelectionSliderItem);
        }
    }
}