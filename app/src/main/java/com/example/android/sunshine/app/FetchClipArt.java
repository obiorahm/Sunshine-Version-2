package com.example.android.sunshine.app;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.android.sunshine.app.data.AddWord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import opennlp.tools.cmdline.CLI;
import opennlp.tools.stemmer.PorterStemmer;

/**
 * Created by mgo983 on 4/30/17.
 */

public class FetchClipArt extends AsyncTask<String[], Void, ArrayList<ArrayList<String>>> {
    private AphasiaAdapter adapter;
    private Context context;
    private String chooseEngine;
    private String[] listOfWords;
    Color availableColors = new Color();

    public static final String ENGINE_PIXABAY = "1";
    public static final String ENGINE_OPENCLIPART = "2";

    ArrayList<ArrayList<String>> ClipArtJson = new ArrayList<ArrayList<String>>();



    public FetchClipArt(ButtonTextAdapter newAdapter, Context newContext ,String newChooseEngine, String[] _listOfWords ){
        adapter = newAdapter;
        chooseEngine = newChooseEngine;
        context = newContext;
        listOfWords = _listOfWords;
    }

    public FetchClipArt(GridAdapter newAdapter, Context newContext, String newChooseEngine)
    {
        adapter = newAdapter;
        context = newContext;
        chooseEngine = newChooseEngine;
    }

    private final String LOG_TAG = FetchClipArt.class.getSimpleName();

    @Override
    protected void onPostExecute(ArrayList<ArrayList<String>> Result) {
        Log.d("I got here: ", "Post Execute");
        if (Result == null){
            Log.d("no results", "absolutely no results");
            return;

        }
        if (adapter instanceof ButtonTextAdapter){
            //addSearchResultToAdapter(Result);
            localSearch(listOfWords, Result, 0, listOfWords.length);
/*            for(int i = 0; i < listOfWords.length; i++){
                Log.d("word order ", listOfWords[i]);
                //localSearch(listOfWords[i], Result, i);

            }*/

            hideProgressBar((ProgressBar) ((ActionBarActivity) context).findViewById(R.id.search_complete));

        }else{
            String [] ImageUrls = new String [1];
            ArrayList<String> currResult = Result.get(0);
            String searchString = currResult.get(0);
            if (!availableColors.searchColor(searchString.toLowerCase())) {
                ImageUrls = parseJSONString(Result);
                orderImages(ImageUrls, searchString);

            }
            else{
                ImageUrls[0] = searchString ;
                setGridViewAdapter(ImageUrls);
                hideProgressBar((ProgressBar) ((ActionBarActivity) context).findViewById(R.id.explanationProgress));

            }
        }
    }

    private void orderImages(String[] ImageUrls, String searchString){

        PorterStemmer stemmer = new PorterStemmer();
        String word = stemmer.stem(searchString);
        setGridViewAdapter(ImageUrls);
        ProgressBar progressBar = (ProgressBar) ((ActionBarActivity) context).findViewById(R.id.explanationProgress);
        hideProgressBar(progressBar);

        //CBIR tryClarify = new CBIR(word, (ImageGridAdapter) adapter, context);
        //tryClarify.execute(ImageUrls);

        //String[] orderedImageUrls = ;
        //return orderedImageUrls;
    }

    private void setGridViewAdapter(String[] ImageUrls){
        if (ImageUrls != null){
            adapter = new GridAdapter(context, ImageUrls /*ImgStringArr*/);
            GridView gridView = (GridView) ((ActionBarActivity) context).findViewById(R.id.image_gridview);
            gridView.setAdapter(adapter);

        }
    }
    private  void addSearchResultToAdapter(final ArrayList<ArrayList<String>>Result){
        final int QUERY_PARAMETER = 0;
        final int URL_JSON = 1;
        for (int i = 0; i < Result.size(); i++){
            ArrayList<String> currResult = Result.get(i);
            Log.d("addSearchResult ", Result.get(i).toString());
            if (currResult.get(QUERY_PARAMETER) != null){
                adapter.addItem(currResult.get(QUERY_PARAMETER) + "&&" + currResult.get(URL_JSON));
            }
        }
    };

    private void hideProgressBar(ProgressBar currProgressBar){
        ProgressBar progressBar = (ProgressBar) currProgressBar;
        progressBar.setVisibility(View.INVISIBLE);
    }

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
            case ENGINE_PIXABAY:
                if (adapter instanceof  ButtonTextAdapter){
                    for (int i = 0; i < params[0].length; i++){

                        if (!addColorDataIfColor(ClipArtJson, params[0][i])){
                            //localSearch(params[0][i], ClipArtJson);
                            if (isCancelled()) break;
                            ClipArtJson.add(getJSONData(buildPixaBayUri("https://pixabay.com/api/","5321405-e3d51a927066916f670cf60c0",params[0][i], "1"),params[0][i]));
                        }
                    }
                }else{
                    if (!addColorDataIfColor(ClipArtJson, params[0][0].toLowerCase())){
                        ClipArtJson.add(getJSONData(buildPixaBayUri("https://pixabay.com/api/","5321405-e3d51a927066916f670cf60c0",params[0][0], "10"),params[0][0]));
                    }
                }
                break;
            case ENGINE_OPENCLIPART:
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

    public GridAdapter getAdapter(){
        return (GridAdapter) adapter;
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



    private void localSearch(final String[] listOfWords, final ArrayList<ArrayList<String>> Result, final int position, final int lengthOfResult ){

        if (position == lengthOfResult){
            return;
        }else{
            FirebaseUser firebaseUser = OpenGalleryObjectActivity.firebaseAuth.getCurrentUser();
            if (firebaseUser != null){

            }else{
                ((OpenGalleryObjectActivity) context).signInAnonymously();
            }
            final String searchString = listOfWords[position];
            final Query mDatabaseQuery = FirebaseDatabase.getInstance().getReference(AddWord.WORD_REFERENCE).child(searchString.toLowerCase()).limitToFirst(1);
            final String WORD_IMAGE_REFERENCE  = "symbols";
            Log.d("the Query", mDatabaseQuery.toString());

            mDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            String wordEntries = (String) child.getValue();
                            String[] getFileName = wordEntries.split("/");
                            Log.d("The entries: ", wordEntries.toString());
                            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

                            firebaseStorage.child( WORD_IMAGE_REFERENCE + "/" + getFileName[0] + "/" + getFileName[2]).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            adapter.addItem(searchString + "&&" + uri.toString());
                                            int newPosition = position + 1;
                                            localSearch(listOfWords, Result, position + 1, lengthOfResult);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    final int QUERY_PARAMETER = 0;
                                    final int URL_JSON = 1;
                                    ArrayList<String> currResult = Result.get(position);
                                    adapter.addItem(currResult.get(QUERY_PARAMETER) + "&&" + currResult.get(URL_JSON));
                                    localSearch(listOfWords, Result, position + 1, lengthOfResult);
                                    Log.d("Download error ", e.toString());
                                }
                            });
                        }

                    }else{
                        final int QUERY_PARAMETER = 0;
                        final int URL_JSON = 1;
                        ArrayList<String> currResult = Result.get(position);
                        adapter.addItem(currResult.get(QUERY_PARAMETER) + "&&" + currResult.get(URL_JSON));
                        localSearch(listOfWords, Result, position + 1, lengthOfResult);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
