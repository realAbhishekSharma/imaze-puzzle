package com.elementstore.imazepuzzle.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.elementstore.imazepuzzle.ImageLevel;
import com.elementstore.imazepuzzle.R;

import org.w3c.dom.Text;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    List<Integer> imageIdLevelList;
    private Context context;
    private int puzzleSize;
    public GridViewAdapter(Context context, List<Integer> imageIdLevelList, int puzzleSize){
        this.context = context;
        this.imageIdLevelList = imageIdLevelList;
        this.puzzleSize = puzzleSize;
    }


    @Override
    public int getCount() {
        return imageIdLevelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    LinearLayout levelItemView;
    TextView levelTextView;
    ImageView levelImageView;
    ImageLevel imageLevel;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View root = inflater.inflate(R.layout.level_view_grid_item,null);

//        levelItemView = root.findViewById(R.id.itemForegroundView);
//        levelTextView = root.findViewById(R.id.levelTextGridItem);
        levelImageView = root.findViewById(R.id.gridItemImageView);

        levelImageView.setBackground(context.getDrawable(imageIdLevelList.get(position)));

        imageLevel = new ImageLevel(context);

        if (!imageLevel.getActiveLevel(puzzleSize)[position]){
            levelItemView.setVisibility(View.VISIBLE);
        }

        return root;
    }
}