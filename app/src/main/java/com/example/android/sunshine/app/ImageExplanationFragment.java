package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;


/**
 * Created by mgo983 on 5/4/17.
 */

public class ImageExplanationFragment extends ActionBarActivity implements TextToSpeech.OnInitListener{

    public ImageExplanationFragment(){}
    public ImageGridAdapter adapter;

    //FetchClipArt fetchClipArt;

    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explainfragment);

        Intent intent = this.getIntent();
        String searchParam = intent.getStringExtra(ButtonTextAdapter.SEARCH_PARAM);
        String [] searchParams = {""};
        final String searchParamToUpperCase = searchParam.substring(0,1).toUpperCase() + searchParam.substring(1);
        searchParams[0] = searchParamToUpperCase;

        CheckInternetConnection checkInternetConnection = new CheckInternetConnection(this);

        if (checkInternetConnection.isNetworkConnected()){

            FetchClipArt fetchClipArt = new FetchClipArt(adapter,this);
            fetchClipArt.execute(searchParams);
        }


        final TextView textView = (TextView) findViewById(R.id.grid_text);
        textView.setText(searchParamToUpperCase);

        ImageButton textView1 = (ImageButton) findViewById(R.id.share_text);

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Is this a picture of the word \"" + searchParamToUpperCase + "\". Find more pictures of this word here.";
                String shareSubject = "I need help identifying the object in the picture";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });


        //Prepare for text to speech
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);

                Intent args = this.getIntent();

                if (args != null) {

                    final ImageButton textView = (ImageButton) findViewById(R.id.speak_text);
                    final TextView textView1 = (TextView) findViewById(R.id.grid_text);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String  speech = textView1.getText().toString();
                            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                        }
                    });

                }

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
