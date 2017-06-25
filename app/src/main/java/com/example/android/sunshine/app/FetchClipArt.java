package com.example.android.sunshine.app;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

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
    private String chooseEngine;
    Color availableColors = new Color();


    public FetchClipArt(ButtonTextAdapter newAdapter, String newChooseEngine ){
        adapter = newAdapter;
        chooseEngine = newChooseEngine;
    }

    public FetchClipArt(ImageGridAdapter newAdapter, Context newContext, String newChooseEngine)
    {
        adapter = newAdapter;
        context = newContext;
        chooseEngine = newChooseEngine;
    }

    private final String LOG_TAG = FetchClipArt.class.getSimpleName();

    @Override
    protected void onPostExecute(final ArrayList<ArrayList<String>> Result) {
        if (Result == null)
            return;
        if (adapter instanceof ButtonTextAdapter){
            addSearchResultToAdapter(Result);

        }else{
            String [] ImageUrls = new String [1];
            ArrayList<String> currResult = Result.get(0);
            String searchString = currResult.get(0);
            if (!availableColors.searchColor(searchString.toLowerCase())) {
                ImageUrls = parseJSONString(Result);}
            else{
                //ImageUrls = new String[1];
                ImageUrls[0] = searchString ;
            }
            setGridViewAdapter(ImageUrls);
        }
    }

    private void setGridViewAdapter(String[] ImageUrls){
        if (ImageUrls != null){
            adapter = new ImageGridAdapter(context, ImageUrls /*ImgStringArr*/);
            GridView gridView = (GridView) ((ActionBarActivity) context).findViewById(R.id.image_gridview);
            gridView.setAdapter(adapter);

        }
    }
    private  void addSearchResultToAdapter(final ArrayList<ArrayList<String>>Result){
        for (int i = 0; i < Result.size(); i++){
            ArrayList<String> currResult = Result.get(i);
            if (currResult.get(0) != null){
                adapter.addItem(currResult.get(0) + "&&" + currResult.get(1));
            }
        }
    };

    private String[] parseJSONString(ArrayList<ArrayList<String>> Result){
        try{
            if (chooseEngine .equals("1")){
                PixabayJSONHandler jsonHandler = new PixabayJSONHandler();

                return jsonHandler.getImageUrl(Result.get(0).get(1), 0);
            }else{
                OpenClipArtJSONHandler jsonHandler = new OpenClipArtJSONHandler();
                return jsonHandler.getImageUrl(Result.get(0).get(1), 0);
            }

        }catch(JSONException e){}
        return null;
    }

    @Override
    protected /*String[]*/ ArrayList<ArrayList<String>> doInBackground(String[]...params) {

        ArrayList<ArrayList<String>> ClipArtJson = new ArrayList<ArrayList<String>>();
        ArrayList<String> color = new ArrayList<>();


        if (params.length == 0)
            return null;
        switch (chooseEngine){
            case "1":
                if (adapter instanceof  ButtonTextAdapter){
                    for (int i = 0; i < params[0].length; i++){

                        if (!addColorDataIfColor(ClipArtJson, params[0][i])){
                            ClipArtJson.add(getJSONData(buildPixaBayUri("https://pixabay.com/api/","5321405-e3d51a927066916f670cf60c0",params[0][i], "1"),params[0][i]));
                        }
                    }
                }else{
                    if (!addColorDataIfColor(ClipArtJson, params[0][0].toLowerCase())){
                        ClipArtJson.add(getJSONData(buildPixaBayUri("https://pixabay.com/api/","5321405-e3d51a927066916f670cf60c0",params[0][0], "10"),params[0][0]));
                    }
                }
                break;
            case "2":
                if (adapter instanceof  ButtonTextAdapter){
                    for (int i = 0; i < params[0].length; i++){
                        if (!addColorDataIfColor(ClipArtJson, params[0][i])){
                            ClipArtJson.add(getJSONData(buildOpenClipArtUri("https://openclipart.org/search/json/","table",params[0][i], "1"),params[0][i]));
                        }
                    }
                }else{
                    if (!addColorDataIfColor(ClipArtJson, params[0][0].toLowerCase())){
                        ClipArtJson.add(getJSONData(buildOpenClipArtUri("https://openclipart.org/search/json/","table",params[0][0], "10"), params[0][0]));
                    }
                }
                break;

        }

        return ClipArtJson;
    }

    public ImageGridAdapter getAdapter(){
        return (ImageGridAdapter) adapter;
    }

    private boolean addColorDataIfColor(ArrayList<ArrayList<String>> ClipArtJSON, String searchValue) {
        ArrayList<String> color = new ArrayList<>();

        if (availableColors.searchColor(searchValue)){
            color.add(searchValue);
            color.add(searchValue);
            ClipArtJSON.add(color);
            return true;
        }else{
            return false;
        }
    }

    private Uri buildOpenClipArtUri(String baseUrl, String apiKey, String queryParameter, String amount){
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
        return buildUri;


    }


    private Uri buildPixaBayUri(String baseUrl, String apiKey, String queryParameter, String amount){
        final String CLIPART_BASE_URL = baseUrl;
        final String API_KEY = "key";
        final String QUERY = "q";

        Uri buildUri;

        buildUri = Uri.parse(CLIPART_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY,apiKey)
                .appendQueryParameter(QUERY, queryParameter)

                .build();
        return buildUri;
    }


    protected  ArrayList<String> getJSONData(Uri SearchUri, String queryParameter /*String baseUrl, String apiKey, String queryParameter, String amount*/){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        ArrayList<String> ClipArtJsonStr = new ArrayList<String>();

        try{

            Uri buildUri = null;

            buildUri = SearchUri; //buildOpenClipArtUri(baseUrl, apiKey, queryParameter, amount);

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
