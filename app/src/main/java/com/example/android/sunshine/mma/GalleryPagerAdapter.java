package com.example.android.sunshine.mma;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by mgo983 on 4/21/17.
 */

public class GalleryPagerAdapter extends FragmentStatePagerAdapter{

    public GalleryPagerAdapter(FragmentManager fm){
        super(fm);
    }


    ArrayList<String> imagePaths = new ArrayList<String>();

    @Override
    public android.support.v4.app.Fragment getItem(int i){
        GalleryObjectFragment galleryFragment = new GalleryObjectFragment();
        Bundle args = new Bundle();
        args.putString(galleryFragment.ARG_OBJECT ,imagePaths.get(i));
        galleryFragment.setArguments(args);
        return galleryFragment;
    }

    @Override
    public int getCount(){
        return imagePaths.size();
    }

    public void addItem(final String item){
        imagePaths.add(item);
        notifyDataSetChanged();
    }

}



