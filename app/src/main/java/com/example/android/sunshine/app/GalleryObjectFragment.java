package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by mgo983 on 4/21/17.
 */

public class GalleryObjectFragment extends Fragment {
    public final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        Bundle args = getArguments();
        File imgFile = new  File(args.getString(ARG_OBJECT).replace("file://",""));
        if (imgFile.exists()){
            View rootView = inflater.inflate(R.layout.page_item_image,container, false);
            Log.v("adapter file name ", imgFile.getAbsolutePath());

            ImageView imageView = (ImageView) rootView.findViewById(R.id.page_captured_image);
             Picasso.with(getActivity()).load(imgFile).into(imageView);
            return rootView;
        }
        return null;
    }


}
