package com.elementstore.imazepuzzle.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elementstore.imazepuzzle.R;

import org.json.JSONArray;
import org.json.JSONException;

public class LifeShopAdapter extends BaseAdapter {
    JSONArray lifeShopData;
    private Context context;

    public LifeShopAdapter(Context context, JSONArray lifeShopData){
        this.context = context;
        this.lifeShopData = lifeShopData;

    }

    @Override
    public int getCount() {
        return lifeShopData.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    TextView lifeImageView,rewardSizeView, lifeCountView, priceView;

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.life_shop_single_item, null);

        lifeImageView = root.findViewById(R.id.lifeImageView);
        rewardSizeView = root.findViewById(R.id.rewardSizeView);
        lifeCountView = root.findViewById(R.id.lifeCountView);
        priceView = root.findViewById(R.id.priceView);

        try {
            if (i == 0){
                rewardSizeView.setText("+");
                lifeImageView.setBackground(context.getDrawable(R.drawable.ads_icon));
                priceView.setVisibility(View.INVISIBLE);
            }else {
                rewardSizeView.setText(lifeShopData.getJSONObject(i).getString("name"));
                priceView.setText(lifeShopData.getJSONObject(i).getInt("value")+"");
            }
            if (lifeShopData.getJSONObject(i).getInt("life") == 0) {
                lifeCountView.setText(context.getString(R.string.infinity));
                lifeCountView.setTypeface(Typeface.DEFAULT_BOLD);
            }else{
                lifeCountView.setText(lifeShopData.getJSONObject(i).getInt("life")+"");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return root;
    }
}
