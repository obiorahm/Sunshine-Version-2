/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        package com.example.android.sunshine.app;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.ShareCompat;
        import android.support.v4.view.MenuItemCompat;
        import android.support.v7.app.ActionBarActivity;
        import android.support.v7.widget.ShareActionProvider;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.io.File;
        import java.net.URL;

public class DetailActivity extends ActionBarActivity {

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String weatherData = getWeatherText();
        TextView textView = new TextView(this);
        textView.setText(weatherData);


        ViewGroup layout = (ViewGroup) findViewById(R.id.detail_container);
        layout.addView(textView);

        if (savedInstanceState == null) {


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new PlaceholderFragment())
                    .commit();
        }


    }

    private String getWeatherText(){
        Intent intent = getIntent();
        String weatherData = intent.getStringExtra(ForecastFragment.EXTRA_TEXT);
        return weatherData;
    }

    private Uri getImage(){
        Intent intent = getIntent();
        Uri ImgDirectory = intent.getExtras().getParcelable(CameraActivity.EXTRA_IMAGE);
        String FileName = intent.getStringExtra(CameraActivity.EXTRA_TARGET);
        Uri FullFilePath = Uri.withAppendedPath(ImgDirectory,FileName);
        return FullFilePath;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Uri fullFilePath = getImage();
        Log.v("PRINTING", fullFilePath.toString());
        File imgFile = new  File(fullFilePath.toString().replace("file://",""));

        if(imgFile.exists()){

            Log.v("PRINTING", "File exists");
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = new ImageView(this);

            myImage.setImageBitmap(myBitmap);

            ViewGroup layout = (ViewGroup) findViewById(R.id.detail_container);
            layout.addView(myImage);
        }




        //Uri screenshotUri = Uri.parse(url);
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("image/jpeg").getIntent();
        shareIntent.putExtra(Intent.EXTRA_STREAM, fullFilePath);


        // Set the share Intent
        mShareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_detail, container, false);
            return rootView;
        }
    }
}
