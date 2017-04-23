package com.example.android.sunshine.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

/**
 * Created by mgo983 on 4/18/17.
 */

public class SelectActionFragment extends android.support.v4.app.Fragment {

    public final static String EXTRA_TEXT = "com.example.android.sunshine.MESSAGE";

    public SelectActionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.newmain, container, false);


//click listener for image buttons

        ImageButton cameraButton = (ImageButton) rootView.findViewById(R.id.camera);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
               /* Intent cameraActivityIntent = new Intent(getActivity(), CameraActivity.class);
                startActivity(cameraActivityIntent);*/
                CameraFragment nextFrag= new CameraFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container,nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });


        ImageButton galleryButton = (ImageButton) rootView.findViewById(R.id.gallery);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Perform action on click
                Intent galleryActivityIntent = new Intent(getActivity(), GalleryActivity.class);
                startActivity(galleryActivityIntent);
            }
        });


        return rootView;

    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment,menu);
    }

}
