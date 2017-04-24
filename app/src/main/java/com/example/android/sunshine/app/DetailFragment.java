package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by mgo983 on 4/21/17.
 */

public class DetailFragment extends Fragment implements TextToSpeech.OnInitListener{
    private ShareActionProvider mShareActionProvider;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    private Uri imageFile = null;

    public ArrayAdapter<String> adapter;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fagment_detail, container, false);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

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
        captureAndSearchImage();
    }

    private void writeToFile(String data, String fileName, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private Uri getImage() {
        Intent intent = getActivity().getIntent();
        //Uri ImgDirectory = intent.getExtras().getParcelable(CameraActivity.EXTRA_IMAGE);
        //String FileName = intent.getStringExtra(CameraActivity.EXTRA_TARGET);
        //Uri FullFilePath = Uri.withAppendedPath(ImgDirectory, FileName);

        Bundle bundle = this.getArguments();
        if (bundle != null){
            Uri ImgDirectory = bundle.getParcelable(CameraFragment.EXTRA_IMAGE);
            String FileName = bundle.getString(CameraFragment.EXTRA_TARGET);
            Uri FullFilePath = Uri.withAppendedPath(ImgDirectory, FileName);
            return FullFilePath;
        }
        return null;

    }


    private void captureAndSearchImage() {

        Uri fullFilePath = imageFile = getImage();
        //Log.v("PRINTING", fullFilePath.toString());
        File imgFile = new File(fullFilePath.toString().replace("file://", ""));

        if (imgFile.exists()) {
            //run CloudSight Search
            DetailFragment.FetchImageDescription fetchImageDescription = new DetailFragment.FetchImageDescription();
            fetchImageDescription.execute(imgFile);
        }
    }




    public class FetchImageDescription extends AsyncTask<File, Void, /*String[]*/ CSGetResult> {

        private final String LOG_TAG = DetailFragment.FetchImageDescription.class.getSimpleName();

        @Override
        protected void onPostExecute(final CSGetResult Result /* String[] Result*/) {

            String[] placeholder = {"mma", "nneoma"};

            System.out.print("the result of image processing" + Result);


            ArrayList<String> mylist = new ArrayList<String>();
            String searchResult = Result.getName();
            //mylist.add(searchResult);
            String[] listofWords = searchResult.split(" ");

            //Save the retrieved text
            writeToFile(searchResult,imageFile.toString(),getActivity());


            //list
            ButtonTextAdapter adapter = new ButtonTextAdapter(getActivity(), myTTS);

            adapter.addImage(imageFile.toString());
            adapter.addItem(Result.getName());
            for (int i = 0; i < listofWords.length; i++) {
                adapter.addItem(listofWords[i]);
            }

            ListView list = (ListView) getActivity().findViewById(R.id.list_view_word);
            list.setAdapter(adapter);

            ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.search_complete);
            progressBar.setVisibility(View.INVISIBLE);


        }

        @Override
        protected /*String[]*/ CSGetResult doInBackground(File... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            BufferedReader reader1 = null;

            if (params.length == 0){
                return null;
            }


            String API_KEY = "waWnmJu7yxqlJ_vKxcvoXg";

            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JsonFactory JSON_FACTORY = new JacksonFactory();

            try {

                CSApi api = new CSApi(
                        HTTP_TRANSPORT,
                        JSON_FACTORY,
                        API_KEY
                );

                CSPostConfig imageToPost = CSPostConfig.newBuilder()
                        .withImage(params[0]).build();

                CSPostResult portResult = api.postImage(imageToPost);



                System.out.println("Post result: " + portResult);

                try {
                    Thread.sleep(30000);
                }catch (InterruptedException e){
                    Log.e(LOG_TAG,"Error",e);
                }


                CSGetResult scoredResult = api.getImage(portResult);

                System.out.println(scoredResult);


                //String[] placeholder = {"mma","obi"};
                return scoredResult;


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            //return null;

            //String[] placeholder = {"school boy", "nneoma"};
            //return placeholder;
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
