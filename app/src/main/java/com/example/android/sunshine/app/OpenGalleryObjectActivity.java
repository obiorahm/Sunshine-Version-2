package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;
import opennlp.tools.stemmer.PorterStemmer;

/**
 * Created by mgo983 on 4/24/17.
 */

public class OpenGalleryObjectActivity extends ActionBarActivity implements TextToSpeech.OnInitListener{

    //public final static String ARG_OBJECT = "IMGFILENAME";

    public final static String IMGFILENAME = "com.example.android.sunshine.IMG_FILE_NAME";
    public final static String IMGFILEKEY = "com.example.android.sunshine.IMG_FILE_KEY";

    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    @Override
    public void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        setContentView(R.layout.fagment_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Prepare for text to speech
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    //checks whether the user has the TTS data installed. If it is not, the user will be prompted to install it.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

               ButtonTextAdapter adapter;

                //get preferred search engine
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                String prefSearchParam = sharedPref.getString(getString(R.string.pref_search_key),getString(R.string.pref_search_default_value));

                myTTS = new TextToSpeech(this, this);

                adapter = new ButtonTextAdapter(this, myTTS, prefSearchParam);

                Intent args = this.getIntent();


                if (args != null) {

                    SharedPreferences sharedPreferences;
                    String FileName;
                    sharedPreferences = getApplicationContext().getSharedPreferences(IMGFILENAME, Context.MODE_PRIVATE);
                    FileName = sharedPreferences.getString(IMGFILEKEY, null);

                    String[] FileNameArray = FileName.split("/");
                    File imgFile = new File(FileName);

                    String TxtFileName = FileNameArray[FileNameArray.length - 1].replace(".jpg", ".txt");
                    String TxtFileContent = readFromFile(this, TxtFileName);
                    String[] listOfWords = TxtFileContent.split(" ");

                    FetchClipArt fetchClipArt = new FetchClipArt(adapter, prefSearchParam);

                    adapter.addImage(imgFile.toString());
                    Log.v("Textfile content is ", TxtFileContent);


                    if (TxtFileContent != ""){
                        adapter.addItem(TxtFileContent + "&&" + imgFile.toString());
                        CheckInternetConnection checkInternetConnection = new CheckInternetConnection(this);
                        if (checkInternetConnection.isNetworkConnected())
                            fetchClipArt.execute(listOfWords);
                    }
                }
                ListView list = (ListView) this.findViewById(R.id.list_view_word);
                list.setAdapter(adapter);

                /*PorterStemmer stemmer = new PorterStemmer();
                String word = stemmer.stem("concussion");
                Log.v("The word stemmer", word);

                CBIR tryClarify = new CBIR();
                tryClarify.execute();*/

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
