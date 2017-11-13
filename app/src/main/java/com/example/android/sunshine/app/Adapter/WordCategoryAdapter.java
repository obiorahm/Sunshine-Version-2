package com.example.android.sunshine.app.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.example.android.sunshine.app.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mgo983 on 11/13/17.
 */

public class WordCategoryAdapter extends GridAdapter {

    private LayoutInflater inflater;
    private ArrayList<Words> mData = new ArrayList<>();
    private Context context;

    public WordCategoryAdapter(Context _context, int resource){
        super(_context, resource);
        inflater = LayoutInflater.from(_context);
        context = _context;
    }

    public void addItem(final String category, final Words wordData){
        mData.add(wordData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return mData.size();
    }

    @Override
    public Words getItem(int position){
        return  mData.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) { convertView = inflater.inflate(R.layout.item_category,null);}

        Words word = mData.get(position);
        Log.d("The position ", position + " " + word.getCategory());

        TextView textView = (TextView) convertView.findViewById(R.id.text_category);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_category);

        textView.setText(word.getCategory());

        Glide
                .with(context)
                .load(word.getUrl())
                .into(imageView);

        return convertView;

    }
}
