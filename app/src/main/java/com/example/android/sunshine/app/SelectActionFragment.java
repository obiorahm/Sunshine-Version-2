package com.example.android.sunshine.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by mgo983 on 4/18/17.
 */

public class SelectActionFragment extends android.support.v4.app.Fragment {

    public final static String EXTRA_TEXT = "com.example.android.sunshine.MESSAGE";

    //Camera Data
    public final static String EXTRA_IMAGE = "com.example.android.sunshine.IMAGE";
    public final static String EXTRA_TARGET = "com.example.android.sunshine.TARGET";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String applicationDirectory = "Aphasia";
    static final Uri mLocationForPhotos = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), applicationDirectory));

    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

    String targetFilename = "sun" + currentDateTimeString.replace(" ","") + ".jpg";

    public SelectActionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.newmain, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.app_name);
        Typeface face=Typeface.createFromAsset(getActivity().getAssets(),"fonts/shortname.ttf");
        textView.setTypeface(face);

        Button cameraButton = (Button) rootView.findViewById(R.id.camera);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

/*                CameraFragment nextFrag= new CameraFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container,nextFrag)
                        .addToBackStack(null)
                        .commit();*/
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.withAppendedPath(mLocationForPhotos, targetFilename));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


        Button galleryButton = (Button) rootView.findViewById(R.id.gallery);

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

        TextView textView = (TextView) getActivity().findViewById(R.id.app_name);
        Typeface face=Typeface.createFromAsset(getActivity().getAssets(),"fonts/shortname.ttf");
        //textView.setTypeface(face);

    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        //inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {


            DetailFragment nextFrag= new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_IMAGE,mLocationForPhotos);
            bundle.putString(EXTRA_TARGET, targetFilename);

            nextFrag.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container,nextFrag)
                    .addToBackStack(null)
                    .commit();


        }
    }

}
