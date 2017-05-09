package com.example.android.sunshine.mma;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONException;

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
    private AphasiaAdapter adapter;
    private Context context;


    public FetchClipArt(ButtonTextAdapter newAdapter){
        adapter = newAdapter;
    }

    public FetchClipArt(ImageGridAdapter newAdapter, Context newContext)
    {
        adapter = newAdapter;
        context = newContext;
    }

    private final String LOG_TAG = FetchClipArt.class.getSimpleName();

    @Override
    protected void onPostExecute(final ArrayList<ArrayList<String>> Result) {
        if (adapter instanceof ButtonTextAdapter){
            for (int i = 0; i < Result.size(); i++){
                ArrayList<String> currResult = Result.get(i);
                adapter.addItem(currResult.get(0) + "&&" + currResult.get(1));
                Log.v("OnPostExecuteResult: ", currResult.get(0));

            }
        }else{
            ArrayList<String> ImageUrls = new ArrayList<>();
            JSONHandler jsonHandler = new JSONHandler();
            for (int i = 0; i < 10; i++){
                try{
                    ImageUrls.add(jsonHandler.getImageUrl(Result.get(0).get(1), i));

                }catch(JSONException e){}
            }

            String[] ImgStringArr = new String[ImageUrls.size()];
            ImgStringArr= ImageUrls.toArray(ImgStringArr);

            adapter = new ImageGridAdapter(context, ImgStringArr);
            GridView gridView = (GridView) ((ActionBarActivity) context).findViewById(R.id.image_gridview);
            gridView.setAdapter(adapter);

        }


    }

    @Override
    protected /*String[]*/ ArrayList<ArrayList<String>> doInBackground(String[]...params) {

        ArrayList<ArrayList<String>> ClipArtJson = new ArrayList<ArrayList<String>>();


        if (params.length == 0)
            return null;
        if (adapter instanceof  ButtonTextAdapter){
            for (int i = 0; i < params[0].length; i++){


                ClipArtJson.add(getJSONData("https://openclipart.org/search/json/","table",params[0][i], "1"));
            }
        }else{
                ClipArtJson.add(getJSONData("https://openclipart.org/search/json/","table",params[0][0], "10"));

        }



        return ClipArtJson;
    }

    protected  ArrayList<String> getJSONData(String baseUrl, String apiKey, String queryParameter, String amount){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        ArrayList<String> ClipArtJsonStr = new ArrayList<String>();

        try{
            final String CLIPART_BASE_URL = baseUrl;
            final String API_KEY = apiKey;
            final String QUERY = "query";
            final String AMOUNT = "amount";

            final String SORT = "sort";

            Uri buildUri = null;

            buildUri = Uri.parse(CLIPART_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY, queryParameter)
                    .appendQueryParameter(AMOUNT,amount)
                    .appendQueryParameter(SORT, "downloads")
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
