package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by mgo983 on 4/24/17.
 */

public class OpenGalleryObjectFragment extends ActionBarActivity implements TextToSpeech.OnInitListener{

    public final static String ARG_OBJECT = "IMGFILENAME";
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    private ButtonTextAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        setContentView(R.layout.fagment_detail);


        //Prepare for text to speech
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);


    }


    //checks whether the user has the TTS data installed. If it is not, the user will be prompted to install it.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);

                adapter = new ButtonTextAdapter(this, myTTS);

                Intent args = this.getIntent();


                if (args != null) {
                    String[] FileNameArray = args.getStringExtra(GalleryObjectFragment.IMGFILENAME).split("/");
                    File imgFile = new File(args.getStringExtra(GalleryObjectFragment.IMGFILENAME));
                    Log.v("I got here! ", "I got here!");

                    String TxtFileName = FileNameArray[FileNameArray.length - 1].replace(".jpg", ".txt");
                    String TxtFileContent = readFromFile(this, TxtFileName);
                    String[] listOfWords = TxtFileContent.split(" ");

                    //FetchClipArt fetchClipArt = new FetchClipArt(adapter);
                    FetchClipArt fetchClipArt = new FetchClipArt(adapter);

                    adapter.addImage(imgFile.toString());
                    Log.v("Textfile content is ", TxtFileContent);


                    if (TxtFileContent != ""){
                        adapter.addItem(TxtFileContent + "&&");
                        fetchClipArt.execute(listOfWords);

                        /*for (int i = 0; i < listOfWords.length; i++) {

                            //adapter.addItem(listOfWords[i] + "&&" + "imageurl" );
                        }*/

                    }
                }
                ListView list = (ListView) this.findViewById(R.id.list_view_word);
                list.setAdapter(adapter);

                ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.search_complete);
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }
    @Override
    public void onInit(int initStatus) {

        if (initStatus == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(Locale.US);
            myTTS.setSpeechRate(0.5f);
        }


    }


    /*public class FetchClipArt extends AsyncTask<String[], Void, ArrayList<ArrayList<String>>> {

        private final String LOG_TAG = OpenGalleryObjectFragment.FetchClipArt.class.getSimpleName();

        @Override
        protected void onPostExecute(final ArrayList<ArrayList<String>> Result) {

                for (int i = 0; i < Result.size(); i++){
                    ArrayList<String> currResult = Result.get(i);
                    adapter.addItem(currResult.get(0) + "&&" + currResult.get(1));

                }


        }

        @Override
        protected ArrayList<ArrayList<String>> doInBackground(String[]...params) {

            ArrayList<ArrayList<String>> ClipArtJson = new ArrayList<ArrayList<String>>();


            if (params.length == 0)
                return null;


            //ClipArtJson = getJSONData("https://openclipart.org/search/json/","table",params[0]);
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
        }*/





    private String readFromFile(Context context, String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (myTTS != null) {
            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
    }


}
