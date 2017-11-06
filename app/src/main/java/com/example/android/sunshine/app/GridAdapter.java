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

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by mgo983 on 11/6/17.
 */

public class GridAdapter extends AphasiaAdapter {

    private Context context;

    private LayoutInflater inflater;

    private String[] imageUrls;

    private Color availableColor = new Color();



    public GridAdapter(Context context, String[] imageUrls){
        super(context, R.layout.item_grid,imageUrls);

        this.context = context;

        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);

    }

    public GridAdapter(Context context, int resource, String [] objects){
        super(context, resource, objects);

    }

    public void addItem(final String item){

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


