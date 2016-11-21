package com.example.android.sunshine.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by mgo983 on 11/21/16.
 */

public class CameraActivity extends ActionBarActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final Uri mLocationForPhotos = Uri.fromFile(Environment.getExternalStorageDirectory());

    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

    String targetFilename = "sun" + currentDateTimeString.replace(" ","") + ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.withAppendedPath(mLocationForPhotos, targetFilename));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bitmap thumbnail = data.getExtras().getParcelable("data");
            // Do other work with full size photo saved in mLocationForPhotos

        }
    }
}


