package com.example.android.sunshine.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

/**
 * Created by mgo983 on 5/4/17.
 */

public class AphasiaAdapter extends ArrayAdapter {

    public AphasiaAdapter(Context context, int resource){
        super(context, resource);

    }

    public AphasiaAdapter(Context context, int resource, String [] objects){
        super(context, resource, objects);

    }

    public void addItem(final String item){

    }
}
