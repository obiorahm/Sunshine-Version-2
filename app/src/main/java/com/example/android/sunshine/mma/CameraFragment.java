package com.example.android.sunshine.mma;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by mgo983 on 4/22/17.
 */

public class CameraFragment extends android.support.v4.app.Fragment {
    public final static String EXTRA_IMAGE = "com.example.android.sunshine.IMAGE";
    public final static String EXTRA_TARGET = "com.example.android.sunshine.TARGET";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String applicationDirectory = "Aphasia";
    static final Uri mLocationForPhotos = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), applicationDirectory));

    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

    String targetFilename = "sun" + currentDateTimeString.replace(" ","") + ".jpg";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.withAppendedPath(mLocationForPhotos, targetFilename));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
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
