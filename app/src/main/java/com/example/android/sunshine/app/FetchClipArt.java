package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mgo983 on 4/30/17.
 */

public class FetchClipArt extends AsyncTask<String[], Void, ArrayList<ArrayList<String>>> {
    private ButtonTextAdapter adapter;


    public FetchClipArt(ButtonTextAdapter newAdapter){
        adapter = newAdapter;
    }

    private final String LOG_TAG = FetchClipArt.class.getSimpleName();

    @Override
    protected void onPostExecute(final ArrayList<ArrayList<String>> Result) {

        for (int i = 0; i < Result.size(); i++){
            ArrayList<String> currResult = Result.get(i);
            adapter.addItem(currResult.get(0) + "&&" + currResult.get(1));
            Log.v("OnPostExecuteResult: ", currResult.get(0));

        }

    }

    @Override
    protected /*String[]*/ ArrayList<ArrayList<String>> doInBackground(String[]...params) {

        ArrayList<ArrayList<String>> ClipArtJson = new ArrayList<ArrayList<String>>();


        if (params.length == 0)
            return null;

        for (int i = 0; i < params[0].length; i++){

            ClipArtJson.add(getJSONData("https://openclipart.org/search/json/","table",params[0][i]));
        }

        return ClipArtJson;
    }

    protected  ArrayList<String> getJSONData(String baseUrl, String apiKey, String queryParameter){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        ArrayList<String> ClipArtJsonStr = new ArrayList<String>();

        try{
            final String CLIPART_BASE_URL = baseUrl;
            final String API_KEY = apiKey;
            final String QUERY = "query";
            final String AMOUNT = "amount";

            Uri buildUri = null;

            buildUri = Uri.parse(CLIPART_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY, queryParameter)
                    .appendQueryParameter(AMOUNT,"1")
                    .build();

            URL url = new URL(buildUri.toString());
            Log.v(LOG_TAG,"The built Uri " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0){
                return null;
            }

            ClipArtJsonStr.add(queryParameter);
            ClipArtJsonStr.add(buffer.toString());
//                ClipArtJsonStr = buffer.toString();
            //Log.v(LOG_TAG, "Clip art JSON String " + ClipArtJsonStr.get(1));

            return ClipArtJsonStr;

        }catch (IOException e){
            Log.e(LOG_TAG, "Error", e);
            return null;
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try{
                    reader.close();
                }catch (final IOException e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }




    }
}
