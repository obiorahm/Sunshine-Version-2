package com.example.android.sunshine.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by mgo983 on 5/4/17.
 */

public class ImageGridAdapter extends AphasiaAdapter {

    private Context context;

    private LayoutInflater inflater;

    private String[] imageUrls;

    public ImageGridAdapter(Context context, String[] imageUrls){
        super(context, R.layout.item_grid,imageUrls);

        this.context = context;

        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);

    }

    public void addItem(final String item){

    }

    public void uncheckAllItems(View view){
        for (int i = 0; i < ((GridView) view).getCount(); i++){
            FrameLayout gridItem  = (FrameLayout) ((GridView) view).getChildAt(i);
            CheckBox checkBox = (CheckBox) gridItem.findViewById(R.id.image_checkbox);
            checkBox.setVisibility(view.VISIBLE);
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView){
            convertView = inflater.inflate(R.layout.item_grid,parent,false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.film_fragment_image_view);

        Glide
                .with(context)
                .load(imageUrls[position])
                .centerCrop()
                .into(imageView);


        return convertView;
    }

}
