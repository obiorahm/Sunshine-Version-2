package com.example.android.sunshine.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.sunshine.app.R;

import java.util.ArrayList;

/**
 * Created by mgo983 on 3/21/18.
 */

public class SpeechCategoryAdapter extends ArrayAdapter {

    private ArrayList<String> mCategory = new ArrayList<>();
    private LayoutInflater inflater;

    public SpeechCategoryAdapter(Context context, int resource){
        super(context, resource);
        inflater = LayoutInflater.from(context);
    }

    public void addItem(String category){
        mCategory.add(category);
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return mCategory.size();
    }

    @Override
    public String getItem(int position){
        return mCategory.get(position);
    }



    @Override
    public View getView(int position, View view, final ViewGroup parent) {
        if (null == view){
            view = inflater.inflate(R.layout.item_category, null);
        }
        String category = mCategory.get(position).replace("_"," ");
        String CapCategory = category.substring(0,1).toUpperCase() + category.substring(1);
        TextView textView = (TextView) view.findViewById(R.id.text_category);
        textView.setText(CapCategory);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.more_category);
        imageButton.setVisibility(View.INVISIBLE);

        return view;
    }

}
