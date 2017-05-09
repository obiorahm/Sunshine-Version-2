package com.example.android.sunshine.mma;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;


/**
 * Created by mgo983 on 5/4/17.
 */

public class ImageExplanationFragment extends ActionBarActivity implements TextToSpeech.OnInitListener{

    public ImageExplanationFragment(){}
    public ImageGridAdapter adapter;

    FetchClipArt fetchClipArt;

    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explainfragment);

        Intent intent = this.getIntent();
        String searchParam = intent.getStringExtra(ButtonTextAdapter.SEARCH_PARAM);
        String [] searchParams = {""};
        searchParams[0] = searchParam;


        fetchClipArt = new FetchClipArt(adapter,this);
        fetchClipArt.execute(searchParams);

        TextView textView = (TextView) findViewById(R.id.grid_text);
        textView.setText(searchParam);

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

                    final TextView textView = (TextView) findViewById(R.id.grid_text);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String  speech = textView.getText().toString();
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
