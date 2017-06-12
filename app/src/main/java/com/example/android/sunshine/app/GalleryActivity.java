package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
        final GridView gridView = (GridView) findViewById(R.id.image_gridview);
        gridView.setAdapter(imageGridAdapter);

        //gridView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

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
        final int SELECT_PHOTO = 1;
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public  boolean onItemLongClick(AdapterView<?> adapterView, final View view, int position, long l){

                ImageView imageView = (ImageView) view.findViewById(R.id.film_fragment_image_view);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.image_checkbox);
                checkBox.setChecked(true);

                view.animate().scaleX(0.8f);
                view.animate().scaleY(0.8f);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final View view1 = view;
                        view1.animate().scaleX(1.0f);
                        view1.animate().scaleY(1.0f);

                    }
                }, 200);

                imageGridAdapter.uncheckAllItems(gridView);




                //view.animate().translationY(view.getHeight()).alpha(1.0f);
                //imageView.setBackgroundColor(getResources().getColor(R.color.colorMaroon));
                return true;
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
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
