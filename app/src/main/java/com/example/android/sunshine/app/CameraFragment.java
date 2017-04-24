package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by mgo983 on 4/22/17.
 */

public class CameraFragment extends Fragment {    public final static String EXTRA_IMAGE = "com.example.android.sunshine.IMAGE";
    public final static String EXTRA_TARGET = "com.example.android.sunshine.TARGET";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String applicationDirectory = "Aphasia";
    static final Uri mLocationForPhotos = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), applicationDirectory));

    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

    String targetFilename = "sun" + currentDateTimeString.replace(" ","") + ".jpg";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setCameraDisplayOrientation(this,);
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
            //Bitmap thumbnail = data.getExtras().getParcelable("data");
            // Do other work with full size photo saved in mLocationForPhotos

           /* Intent intent = new Intent(this, DetailFragment.class);
            intent.putExtra(EXTRA_IMAGE, (Parcelable) mLocationForPhotos);
            intent.putExtra(EXTRA_TARGET, targetFilename);

            startActivity(intent);*/

            DetailFragment nextFrag= new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_IMAGE,mLocationForPhotos);
            bundle.putString(EXTRA_TARGET, targetFilename);

            nextFrag.setArguments(bundle);
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.container,nextFrag)
                    .addToBackStack(null)
                    .commit();



        }
    }

}
