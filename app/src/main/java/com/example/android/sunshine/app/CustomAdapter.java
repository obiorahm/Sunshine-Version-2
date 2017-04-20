package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by mgo983 on 1/31/17.
 */

public class CustomAdapter extends ArrayAdapter {

    private final Context context;
    private final String[] web;
    private final String[] imageUrls;

    private LayoutInflater inflater;
    public CustomAdapter(Context context,
                      String[] web, String[] imageUrls) {
        super(context, R.layout.list_item_forecast, web);
        this.context = context;
        this.web = web;
        this.imageUrls = imageUrls;
        inflater = LayoutInflater.from(context);

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {


        if (null == view){
            view = inflater.inflate(R.layout.list_item_forecast,parent,false);

        }

        view = inflater.inflate(R.layout.list_item_forecast, null,true);
        TextView txtTitle = (TextView) view.findViewById(R.id.list_item_forecast_textview);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_area);

        Picasso.with(context).load(imageUrls[position]).into(imageView);

        txtTitle.setText(web[position].toUpperCase());

        return view;
    }

    

}
