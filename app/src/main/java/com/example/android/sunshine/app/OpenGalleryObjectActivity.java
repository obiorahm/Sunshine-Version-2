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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;



/**
 * Created by mgo983 on 4/24/17.
 */

public class OpenGalleryObjectActivity extends CommonDetailOpen {

    //public final static String ARG_OBJECT = "IMGFILENAME";

    public final static String IMGFILENAME = "com.example.android.sunshine.IMG_FILE_NAME";
    public final static String IMGFILEKEY = "com.example.android.sunshine.IMG_FILE_KEY";

    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    private String TAG = "OpenGalleryObject";


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
                    _FileName = FileName = sharedPreferences.getString(IMGFILEKEY, null);
                    try{
                        String[] FileNameArray = FileName.split("/");
                        File imgFile = new File(FileName);

                        TxtFileName = FileNameArray[FileNameArray.length - 1].replace(".jpg", ".txt");
                        String TxtFileContent = readFromFile(this, TxtFileName);
                        listOfWords = TxtFileContent.split(" ");

                        FetchClipArt fetchClipArt = new FetchClipArt(adapter,this ,prefSearchParam);

                        adapter.addImage(imgFile.toString());
                        Log.v("Textfile content is ", TxtFileContent);
                        Log.v("Opengallery img fname ", _FileName);


                        if (! TxtFileContent.equals("") ){
                            adapter.addResult(TxtFileContent);
                            CheckInternetConnection checkInternetConnection = new CheckInternetConnection(this);
                            if (checkInternetConnection.isNetworkConnected())
                                fetchClipArt.execute(listOfWords);
                        }

                    }catch (NullPointerException e){
                        Log.e(TAG, ": " + e);
                    }

                }
                list = (ListView) this.findViewById(R.id.list_view_word);
                list.setAdapter(adapter);

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
                String receiveString;
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

/*    @Override
    public void onBackPressed(){
        if (ONLONGCLICKMODE){
            adapter.makeItemsEditDeleteInvisible();
            ONLONGCLICKMODE = false;
        }else if (ONEDITMODE){
            FrameLayout listItem =  (FrameLayout) list.getChildAt(adapter.EDITED_POSITION);
            adapter.hideEditElements(
                    (EditText) listItem.findViewById(R.id.list_item_word_editview),
                    (ImageButton) listItem.findViewById(R.id.list_reject_edit_button),
                    (ImageButton) listItem.findViewById(R.id.list_accept_edit_button),
                    this,
                    (TextView) listItem.findViewById(R.id.list_item_word_textview)
            );
            ONEDITMODE = false;

        }else
        {
            super.onBackPressed();
        }
    }*/

 /*   @Override
    public void okOrCancel(boolean okOrCancel, String menuID){
        if (okOrCancel){

            String newPhrase = "";
            String eachWord = listOfWords[0];
            int phraseLength = listOfWords.length;
            int beginAt;

            FrameLayout listItem =  (FrameLayout) list.getChildAt(adapter.EDITED_POSITION);

            switch (menuID){
                case "delete":
                    if(adapter.EDITED_POSITION - 2 == 0){
                      newPhrase = listOfWords[1];
                        beginAt = 2;
                    }else{
                       newPhrase = listOfWords[0];
                        beginAt = 1;
                    }

                    for(int i = beginAt; i < phraseLength; i++){
                        if (i != adapter.EDITED_POSITION - 2){
                            eachWord = listOfWords[i];
                            newPhrase = newPhrase + " " +eachWord;
                        }
                    }
                    break;
                case "edit":
                    EditText editedWordView = (EditText) listItem.findViewById(R.id.list_item_word_editview);
                    String editedWord = editedWordView.getText().toString();

                    //eliminate trailing spaces
                    if (adapter.EDITED_POSITION - 2 == 0){
                        newPhrase = editedWord;
                    } else{
                        newPhrase = eachWord;
                    }

                    for (int i = 1; i < listOfWords.length; i++){

                        if (i == adapter.EDITED_POSITION - 2){
                            eachWord = editedWordView.getText().toString();
                        }else {
                            eachWord = listOfWords[i];
                        }
                        newPhrase += " " + eachWord ;
                    }
                    break;
            }


            writeToFile(newPhrase, TxtFileName ,this);
            reloadActivity();

        }else{
            onBackPressed();
        }
    }
    private void reloadActivity(){
        finish();
        startActivity(getIntent());
    }*/

    /*private void writeToFile(String data, String fileName, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }*/

}
