package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.app.LauncherActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgo983 on 4/21/17.
 */

public class GalleryActivity extends ActionBarActivity{

    public final static String IMGFILENAME = "com.example.android.sunshine.IMG_FILE_NAME";
    public final static String IMGFILEKEY = "com.example.android.sunshine.IMG_FILE_KEY";

    //GalleryPagerAdapter galleryPagerAdapter;

    ImageGridAdapter imageGridAdapter;

    ViewPager viewPager;

    boolean deleteMenuShow = false;

    boolean ONLONGCLICKMODE = false;

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
        final Long[] fileDateModified = new Long[fileLength];

        //enter value and index into hash map
        HashMap<Long, Integer> NameAndDateModified = new HashMap<>();


        for (int i = 0; i < fileLength; i++){
            fileNames[i] = files[i].getAbsolutePath();
            fileDateModified[i] = files[i].lastModified();
            NameAndDateModified.put(fileDateModified[i], i);
        }


       //order by date modified
        List<Long> fileDateModifiedList = Arrays.asList(fileDateModified);
        Collections.sort(fileDateModifiedList);

        final String[] NewFileNames = new String[fileLength];
        for (int i = 0; i < fileLength; i++){
            NewFileNames[i] = fileNames[NameAndDateModified.get(fileDateModifiedList.get(fileLength - i - 1))];
        }

        imageGridAdapter = new ImageGridAdapter(this, NewFileNames);
        final GridView gridView = (GridView) findViewById(R.id.image_gridview);
        gridView.setAdapter(imageGridAdapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        CheckBox imageButton = (CheckBox) findViewById(R.id.btn_slide);


        ImageView imageButton1 = (ImageView) gridView.findViewById(R.id.film_fragment_image_view);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                if (!ONLONGCLICKMODE){
                    Intent OpenGalleryActivityIntent = new Intent(getApplicationContext(), OpenGalleryObjectActivity.class);

                    //use sharedpreference to save data so that back button works
                    SharedPreferences sharedPreference;
                    SharedPreferences.Editor editor;
                    sharedPreference = getApplicationContext().getSharedPreferences(IMGFILENAME, getApplicationContext().MODE_PRIVATE);
                    editor = sharedPreference.edit();

                    editor.putString(IMGFILEKEY,NewFileNames[position]);
                    editor.commit();
                    startActivity(OpenGalleryActivityIntent);

                }else{
                    imageAnimation(gridView,position);
                    checkCurrentItem(view);
                }
            }

        });

        final int SELECT_PHOTO = 1;
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public  boolean onItemLongClick(AdapterView<?> adapterView, final View view, int position, long l){

                imageAnimation(gridView,position);

                if (!ONLONGCLICKMODE){
                    imageGridAdapter.unCheckAllItems(gridView);
                    imageGridAdapter.visibleCheckboxes(gridView);
                    invalidateOptionsMenu(); // this causes the onprepareOptionsMenu to be called
                    CheckBox imageButton = (CheckBox) findViewById(R.id.btn_slide); //select all button should be visible
                    imageButton.setVisibility(View.VISIBLE);
                    ONLONGCLICKMODE = true;

                }

                checkCurrentItem(view);
                return true;
            }


        });


    imageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CheckBox imageButton = (CheckBox) findViewById(R.id.btn_slide);
            if (imageButton.isChecked()){
                imageGridAdapter.checkAllItems(gridView);
            }else {
                imageGridAdapter.unCheckAllItems(gridView);
            }
        }
    });

    }

    public void imageAnimation(GridView gridView, int position){
        final FrameLayout child = (FrameLayout) gridView.getChildAt(position);
        child.animate().scaleX(0.8f);
        child.animate().scaleY(0.8f);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final View view1 = child;
                view1.animate().scaleX(1.0f);
                view1.animate().scaleY(1.0f);

            }
        }, 200);
    }

    public void checkCurrentItem(View view){
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.image_checkbox);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setChecked(true);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (ONLONGCLICKMODE){
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);



        }else {


            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);


        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        GridView gridView = (GridView) findViewById(R.id.image_gridview);
        CheckBox imageButton = (CheckBox) findViewById(R.id.btn_slide);

        if (ONLONGCLICKMODE){
            ONLONGCLICKMODE = false;
            invalidateOptionsMenu();
            imageGridAdapter.invisibleCheckboxes(gridView);
            imageButton.setVisibility(View.INVISIBLE);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowHomeEnabled(true);

        }else{
            super.onBackPressed();
        }
    }



}
