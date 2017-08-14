package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by mgo983 on 5/4/17.
 */

public class ImageGridAdapter extends AphasiaAdapter {

    private Context context;

    private LayoutInflater inflater;

    private String[] imageUrls;

    private Color availableColor = new Color();



    public ImageGridAdapter(Context context, String[] imageUrls){
        super(context, R.layout.item_grid,imageUrls);

        this.context = context;

        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);

    }

    public void addItem(final String item){

    }

    public void unCheckAllItems(View view){
        for (int i = 0; i < ((GridView) view).getCount(); i++){
            FrameLayout gridItem  = (FrameLayout) ((GridView) view).getChildAt(i);
            CheckBox checkBox = (CheckBox) gridItem.findViewById(R.id.image_checkbox);
            checkBox.setChecked(false);
        }

    }

    public void visibleCheckboxes(View view){
        for (int i = 0; i < ((GridView) view).getCount(); i++){
            FrameLayout gridItem  = (FrameLayout) ((GridView) view).getChildAt(i);
            CheckBox checkBox = (CheckBox) gridItem.findViewById(R.id.image_checkbox);
            checkBox.setVisibility(view.VISIBLE);
        }

    }

    public void checkAllItems(View view){
        for (int i = 0; i < ((GridView) view).getCount(); i++){
            FrameLayout gridItem  = (FrameLayout) ((GridView) view).getChildAt(i);
            CheckBox checkBox = (CheckBox) gridItem.findViewById(R.id.image_checkbox);
            checkBox.setChecked(true);
        }

    }


    public void invisibleCheckboxes(View view){
        for (int i = 0; i < ((GridView) view).getCount(); i++){
            FrameLayout gridItem  = (FrameLayout) ((GridView) view).getChildAt(i);
            CheckBox checkBox = (CheckBox) gridItem.findViewById(R.id.image_checkbox);
            checkBox.setVisibility(view.INVISIBLE);
        }

    }
    public ArrayList<Integer> getCheckedboxes(View view){
        ArrayList<Integer> checkedPositions = new ArrayList<>();
        GridView gridView = ((GridView) view);
        for (int i = 0; i < gridView.getCount(); i++){
            FrameLayout gridItem = (FrameLayout) gridView.getChildAt(i);
            CheckBox checkBox = (CheckBox) gridItem.findViewById(R.id.image_checkbox);
            if (checkBox.isChecked())
                checkedPositions.add((Integer) i);
        }

        return checkedPositions;
    }



    public String getImageUrl(int position){
        return imageUrls[position];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView){
            convertView = inflater.inflate(R.layout.item_grid,parent,false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.film_fragment_image_view);

        String searchParam = imageUrls[position];

        isColor(searchParam, imageView);

        Glide
                .with(context)
                .load(imageUrls[position])
                .centerCrop()
                .into(imageView);

        return convertView;
    }



    private  void isColor(String searchParam, ImageView mImage){
        try {

            if (availableColor.searchColor(searchParam)){
                Class res = R.color.class;
                Field field = res.getField( searchParam );
                int colorId = field.getInt(null);
                if (availableColor.searchColor(searchParam)){
                    mImage.setBackgroundColor(context.getResources().getColor(colorId));

                }
            }

        }catch (NoSuchFieldException e){

        }catch (IllegalAccessException e){

        }


    }

}
