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

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,R.id.list_item_forecast_textview,forecastArray);

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
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected void onPostExecute(final String[] Result){

            String[] placeholder = {"mma", "nneoma"};

            adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,R.id.list_item_forecast_textview,placeholder);

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
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                /*final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";*/


                /*Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());*/

                //picture search URI
                /*final String CLOUDSIGHT_BASE_URL = "http://api.cloudsightapi.com/image_requests?";
                final String IMAGE_URL_PARAM = "image_request[remote_image_url]";
                final String LOCALE_PARAM = "image_request[locale]";

                Uri searchpicUri = Uri.parse(CLOUDSIGHT_BASE_URL).buildUpon()
                        .appendQueryParameter(IMAGE_URL_PARAM, "http://kingofwallpapers.com/chair/chair-005.jpg")
                        .appendQueryParameter(LOCALE_PARAM, "en")
                        .build();

                URL pictureUrl = new URL(searchpicUri.toString());
                Log.v(LOG_TAG, "picture URL " + searchpicUri.toString());*/

                // Create the request to OpenWeatherMap, and open the connection
                /*urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();*/

                /*urlConnection = (HttpURLConnection) pictureUrl.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Authorization", "CloudSight waWnmJu7yxqlJ_vKxcvoXg");
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(20000);
                urlConnection.connect();*/

                // Read the input stream into a String

                /*InputStream inputStream = null;
                StringBuffer buffer = new StringBuffer();



                inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(LOG_TAG, "input streams the culprit");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.v(LOG_TAG, "buffers the culprit");
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);

                WeatherDataParser ParsedForecastData = new WeatherDataParser();
                try{
                    //String[] ForecastData = ParsedForecastData.getWeatherDataFromJson(forecastJsonStr, 7);
                    JSONObject pictureData = new JSONObject(forecastJsonStr);
                    if (pictureData.getString("status").equals("completed")){

                    }
                    String token = pictureData.getString("token");

//use picture token for search URI
                    String CLOUDSIGHT_BASE_RESPONSE = "http://api.cloudsightapi.com/image_responses/:" + token;


                    Uri searchtokenUri = Uri.parse(CLOUDSIGHT_BASE_RESPONSE).buildUpon()
                            .build();

                    URL picturetokenUrl = new URL(searchtokenUri.toString());
                    Log.v(LOG_TAG, "picture URL " + searchtokenUri.toString());


                    urlConnection = (HttpURLConnection) picturetokenUrl.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.addRequestProperty("Authorization", "CloudSight waWnmJu7yxqlJ_vKxcvoXg");
                    urlConnection.setReadTimeout(15000);
                    urlConnection.setConnectTimeout(20000);
                    urlConnection.connect();

                    // Read the input stream into a String

                    InputStream inputStream1 = null;
                    StringBuffer buffer1 = new StringBuffer();



                    inputStream1 = urlConnection.getInputStream();

                    if (inputStream1 == null) {
                        // Nothing to do.
                        Log.v(LOG_TAG, "input streams the culprit");
                        return null;
                    }
                    reader1 = new BufferedReader(new InputStreamReader(inputStream1));

                    String line1;
                    while ((line1 = reader1.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer1.append(line + "\n");
                    }

                    if (buffer1.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        Log.v(LOG_TAG, "buffers the culprit");
                        return null;
                    }
                    tokenStr = buffer1.toString();

                    Log.v(LOG_TAG, "Forecast JSON String: " + tokenStr);

                }catch(JSONException jsonex){
                    Log.e(LOG_TAG, "Error", jsonex);
                }*/

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
