package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Created by mgo983 on 4/21/17.
 */

public class GalleryObjectFragment extends Fragment {
    public final static String ARG_OBJECT = "object";
    public final static String IMGFILENAME = "com.example.android.sunshine.IMG_FILE_NAME";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Bundle args = getArguments();
        final String ImgFileName = args.getString(ARG_OBJECT).replace("file://","");
        File imgFile = new  File(ImgFileName);


           View rootView = inflater.inflate(R.layout.page_item_image,container, false);
            Log.v("adapter file name ", imgFile.getAbsolutePath());

            ImageView imageView = (ImageView) rootView.findViewById(R.id.page_captured_image);
             Picasso.with(getActivity()).load(imgFile).resize(400,400).centerCrop().into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent OpenGalleryActivityIntent = new Intent(getActivity(), OpenGalleryObjectFragment.class);

                    Bundle args = new Bundle();
                    args.putString(IMGFILENAME, ImgFileName);
                    OpenGalleryActivityIntent.putExtras(args);

                    startActivity(OpenGalleryActivityIntent);
                }
            });
            return rootView;

    }


}
