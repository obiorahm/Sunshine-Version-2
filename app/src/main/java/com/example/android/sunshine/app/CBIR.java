package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.util.Log;


import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;

/**
 * Created by mgo983 on 6/22/17.
 */

public class CBIR extends  AsyncTask<Void, Void, List<ClarifaiOutput<Concept>>>{
    final ClarifaiClient client = new ClarifaiBuilder("oOH6jbTgfsWll9_X55goV5uZTIgb8L8fdmoM4UQr", "4xmzUBx_N201JpiR6jVTrQPA7tgwi0GTqgmnbYI_").client(new OkHttpClient()).buildSync();

    public CBIR(){

    }

    @Override
    protected  List<ClarifaiOutput<Concept>> doInBackground(Void...Params){
        List<ClarifaiOutput<Concept>> predictionResults = client.getDefaultModels().generalModel()
                .predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of("https://static.pexels.com/photos/31242/pexels-photo-31242.jpg"))).executeSync().get();

        return predictionResults;
    }


    @Override
        protected void onPostExecute(List<ClarifaiOutput<Concept>> Result){
            Log.v("His raod stretches", Result.get(0).toString());
    }

}
