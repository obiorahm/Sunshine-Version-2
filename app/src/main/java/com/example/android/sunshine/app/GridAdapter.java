package com.example.android.sunshine.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;

/**
 * Created by mgo983 on 11/6/17.
 */

public class GridAdapter extends AphasiaAdapter {

    private Context context;

    private LayoutInflater inflater;

    private String[] imageUrls;

    private Color availableColor = new Color();

    //private boolean[] checked;

    //private CheckBox selectAllCheckBox;

    public GridAdapter(Context context, String[] imageUrls){
        super(context, R.layout.item_grid,imageUrls);

        this.context = context;

        this.imageUrls = imageUrls;

        //this.checked = new boolean[imageUrls.length];

        inflater = LayoutInflater.from(context);


    }
}


