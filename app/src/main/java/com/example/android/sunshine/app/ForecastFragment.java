package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.Inflater;
import com.squareup.picasso.Picasso;

/**
 * Created by mmaob_000 on 10/26/2016.
 */

public class ForecastFragment extends Fragment {
    public final static String EXTRA_TEXT = "com.example.android.sunshine.MESSAGE";
    public ForecastFragment() {
    }
    public ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecastArray = {"Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/46",
                "Weds - Cloudy - 72/63",
                "Thurs - Rainy - 64/51",
                "Fri - Foggy - 70/46",
                "Sat - Sunny - 76/68"};

        String[] imageUrls = {"http://ab.pbimgs.com/pbimgs/ab/images/dp/wcm/201640/0079/trieste-side-chair-o.jpg",
                "https://i.imgur.com/tGbaZCY.jpg", "http://ab.pbimgs.com/pbimgs/ab/images/dp/wcm/201640/0079/trieste-side-chair-o.jpg",
                "https://i.imgur.com/tGbaZCY.jpg", "http://ab.pbimgs.com/pbimgs/ab/images/dp/wcm/201640/0079/trieste-side-chair-o.jpg",
                "https://i.imgur.com/tGbaZCY.jpg"};

        adapter = new CustomAdapter(getActivity(), forecastArray, imageUrls );
        //adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,R.id.list_item_forecast_textview,forecastArray);

        ListView list = (ListView) rootView.findViewById(R.id.listview_forecast);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Activity context = getActivity();
                String Forecast = adapter.getItem(position);


                Intent detailActivityIntent = new Intent(context, DetailActivity.class);
                detailActivityIntent.putExtra(EXTRA_TEXT, Forecast);
                startActivity(detailActivityIntent);

            }
        });

        return rootView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
            fetchWeatherTask.execute("94043");
            return true;
        }else if(id == R.id.action_camera){
            Intent cameraActivityIntent = new Intent(getActivity(), CameraActivity.class);
            startActivity(cameraActivityIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected void onPostExecute(final String[] Result){

            String[] placeholder = {"mma", "nneoma"};

            String[] imageUrls = {"https://i.imgur.com/tGbaZCY.jpg",
                                    "https://i.imgur.com/tGbaZCY.jpg"};


            adapter = new CustomAdapter(getActivity(), Result, imageUrls);
            //adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast,R.id.list_item_forecast_textview,Result);

            ListView list = (ListView) getActivity().findViewById(R.id.listview_forecast);
            list.setAdapter(adapter);


        }

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            BufferedReader reader1 = null;

            if (params.length == 0){
                return null;
            }

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String tokenStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

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
                        .withRemoteImageUrl("http://kingofwallpapers.com/chair/chair-005.jpg")
                        .build();

                CSPostResult portResult = api.postImage(imageToPost);



                System.out.println("Post result: " + portResult);

                try {
                    Thread.sleep(30000);
                }catch (InterruptedException e){
                    Log.e(LOG_TAG,"Error",e);
                }


                CSGetResult scoredResult = api.getImage(portResult);

                System.out.println(scoredResult);


                String[] placeholder = {"mma","obi"};
                return placeholder;


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
        }
    }


}
