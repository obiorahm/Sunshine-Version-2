package com.example.android.sunshine.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by mgo983 on 4/21/17.
 */

public class GalleryActivity extends FragmentActivity {

    GalleryPagerAdapter galleryPagerAdapter;
    ViewPager viewPager;
    @Override
    public void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        String ExternalStorageDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath();
        String targetPath = ExternalStorageDirectoryPath + "/Aphasia/";

        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
        File targetDirectory = new File(targetPath);

        galleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewer_image_gallery);

        File[] files = targetDirectory.listFiles();
        for (File file : files){
                galleryPagerAdapter.addItem(file.getAbsolutePath());

        }
        viewPager.setAdapter(galleryPagerAdapter);
    }

}
