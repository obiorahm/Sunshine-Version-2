package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by mgo983 on 4/21/17.
 */

public class GalleryActivity extends ActionBarActivity{

    public final static String IMGFILENAME = "com.example.android.sunshine.IMG_FILE_NAME";
    public final static String IMGFILEKEY = "com.example.android.sunshine.IMG_FILE_KEY";

    //GalleryPagerAdapter galleryPagerAdapter;

    ImageGridAdapter imageGridAdapter;

    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String ExternalStorageDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath();
        String targetPath = ExternalStorageDirectoryPath + "/Aphasia/";

        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
        File targetDirectory = new File(targetPath);

        //galleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());
        //viewPager = (ViewPager) findViewById(R.id.gallery_container);

        File[] files = targetDirectory.listFiles();
        int fileLength = files.length;
        final String[] fileNames = new String[fileLength];
        for (int i = 0; i < fileLength; i++){
            fileNames[i] = files[i].getAbsolutePath();
        }



        imageGridAdapter = new ImageGridAdapter(this,fileNames);
        GridView gridView = (GridView) findViewById(R.id.image_gridview);
        gridView.setAdapter(imageGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Intent OpenGalleryActivityIntent = new Intent(getApplicationContext(), OpenGalleryObjectActivity.class);

                //use sharedpreference to save data so that back button works
                SharedPreferences sharedPreference;
                SharedPreferences.Editor editor;
                sharedPreference = getApplicationContext().getSharedPreferences(IMGFILENAME, getApplicationContext().MODE_PRIVATE);
                editor = sharedPreference.edit();

                editor.putString(IMGFILEKEY,fileNames[position]);
                editor.commit();
                startActivity(OpenGalleryActivityIntent);
            }

        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
