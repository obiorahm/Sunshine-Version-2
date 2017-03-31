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
        import android.os.AsyncTask;
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
        import android.widget.ProgressBar;
        import android.widget.TextView;

        import com.google.api.client.http.HttpTransport;
        import com.google.api.client.http.javanet.NetHttpTransport;
        import com.google.api.client.json.JsonFactory;
        import com.google.api.client.json.jackson2.JacksonFactory;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.IOException;
        import java.net.HttpURLConnection;
        import java.net.URL;

public class DetailActivity extends ActionBarActivity {

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*String weatherData = getWeatherText();
        TextView textView = new TextView(this);
        textView.setText(weatherData);


        ViewGroup layout = (ViewGroup) findViewById(R.id.detail_container);
        layout.addView(textView);*/

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
        //Log.v("PRINTING", fullFilePath.toString());
        File imgFile = new  File(fullFilePath.toString().replace("file://",""));

        if(imgFile.exists()){

            //run CloudSight Search
            FetchImageDescription fetchImageDescription = new FetchImageDescription();
            fetchImageDescription.execute(imgFile);

            Log.v("PRINTING", "File exists");
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ViewGroup layout = (ViewGroup) findViewById(R.id.detail_container);

            ImageView myImage = (ImageView) layout.findViewById(R.id.captured_image);
            //ImageView myImage = new ImageView(this);

            myImage.setImageBitmap(myBitmap);

            //layout.addView(myImage);

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

            View rootView = inflater.inflate(R.layout.fagment_detail, container, false);

            return rootView;
        }
    }

    public class FetchImageDescription extends AsyncTask<File, Void, /*String[]*/ CSGetResult> {

        private final String LOG_TAG = DetailActivity.FetchImageDescription.class.getSimpleName();

        @Override
        protected void onPostExecute(final CSGetResult Result /* String[] Result*/ ) {

            String[] placeholder = {"mma", "nneoma"};

            System.out.print("the result of image processing"+ Result);

            //ViewGroup layout = (ViewGroup) findViewById(R.id.detail_container);

            //TextView textView = new TextView(DetailActivity.this);
            TextView textView = (TextView) findViewById(R.id.search_result);
            //textView.setText(Result[0]);
            textView.setText(Result.getName().toUpperCase());




            ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_complete);
            progressBar.setVisibility(View.INVISIBLE);

            //layout.addView(textView);

        }

        @Override
        protected /*String[]*/ CSGetResult doInBackground(File... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.



            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            BufferedReader reader1 = null;

            if (params.length == 0){
                return null;
            }


            String API_KEY = "waWnmJu7yxqlJ_vKxcvoXg";

            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JsonFactory JSON_FACTORY = new JacksonFactory();

            try {

                CSApi api = new CSApi(
                        HTTP_TRANSPORT,
                        JSON_FACTORY,
                        API_KEY
                );

                CSPostConfig imageToPost = CSPostConfig.newBuilder()
                        .withImage(params[0]).build();

                CSPostResult portResult = api.postImage(imageToPost);



                System.out.println("Post result: " + portResult);

                try {
                    Thread.sleep(30000);
                }catch (InterruptedException e){
                    Log.e(LOG_TAG,"Error",e);
                }


                CSGetResult scoredResult = api.getImage(portResult);

                System.out.println(scoredResult);


                //String[] placeholder = {"mma","obi"};
                return scoredResult;


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            //return null;

            //String[] placeholder = {"mma", "nneoma"};
            //return placeholder;
        }
    }
}
