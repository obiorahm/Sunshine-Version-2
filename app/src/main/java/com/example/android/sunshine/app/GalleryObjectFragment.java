package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Created by mgo983 on 4/21/17.
 */

public class GalleryObjectFragment extends Fragment implements TextToSpeech.OnInitListener {
    public final String ARG_OBJECT = "object";
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fagment_detail, container, false);

        //Prepare for text to speech
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        Bundle args = getArguments();
        String[] FileNameArray = args.getString(ARG_OBJECT).replace("file://","").split("/");
        File imgFile = new  File(args.getString(ARG_OBJECT).replace("file://",""));

        ButtonTextAdapter adapter = new ButtonTextAdapter(getActivity(), myTTS);

        String TxtFileName = FileNameArray[FileNameArray.length - 1].replace(".jpg", ".txt");
        String TxtFileContent = readFromFile(getActivity(), TxtFileName);
        String[] listOfWords = TxtFileContent.split(" ");

        adapter.addImage(imgFile.toString());
        adapter.addItem(TxtFileContent);
        for (int i = 0; i < listOfWords.length; i++) {
            adapter.addItem(listOfWords[i]);
        }

        ListView list = (ListView) rootView.findViewById(R.id.list_view_word);
        list.setAdapter(adapter);

        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.search_complete);
        progressBar.setVisibility(View.INVISIBLE);

/*            View rootView = inflater.inflate(R.layout.page_item_image,container, false);
            Log.v("adapter file name ", imgFile.getAbsolutePath());

            ImageView imageView = (ImageView) rootView.findViewById(R.id.page_captured_image);
             Picasso.with(getActivity()).load(imgFile).resize(400,400).centerCrop().into(imageView);*/
            return rootView;

    }

    //checks whether the user has the TTS data installed. If it is not, the user will be prompted to install it.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(getActivity(), this);
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

}
