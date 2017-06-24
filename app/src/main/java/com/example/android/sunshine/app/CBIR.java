package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;


import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
    final ClarifaiClient client = new ClarifaiBuilder("oOH6jbTgfsWll9_X55goV5uZTIgb8L8fdmoM4UQr", "4xmzUBx_N201JpiR6jVTrQPA7tgwi0GTqgmnbYI_")
                                    .client(new OkHttpClient())
                                    .buildSync();

    public CBIR(){

    }

    Collection<ClarifaiInput> images = new Collection<ClarifaiInput>() {
        List<ClarifaiInput> clarifaiInputs = new ArrayList<>();

        @Override
        public boolean add(ClarifaiInput clarifaiInput) {
            return clarifaiInputs.add(clarifaiInput);
        }

        @Override
        public boolean addAll(Collection<? extends ClarifaiInput> collection) {

            return clarifaiInputs.addAll(collection);
        }

        @Override
        public void clear() {
            clarifaiInputs.clear();

        }

        @Override
        public boolean contains(Object o) {
            return clarifaiInputs.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return clarifaiInputs.containsAll(collection);
        }

        @Override
        public boolean isEmpty() {
            return clarifaiInputs.isEmpty();
        }

        @NonNull
        @Override
        public Iterator<ClarifaiInput> iterator() {
            return clarifaiInputs.iterator();

        }

        @Override
        public boolean remove(Object o) {
            return clarifaiInputs.remove(o);

        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return clarifaiInputs.removeAll(collection);
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return clarifaiInputs.retainAll(collection);
        }

        @Override
        public int size() {
            return clarifaiInputs.size();
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return clarifaiInputs.toArray();
        }

        @NonNull
        @Override
        public <T> T[] toArray(T[] ts) {
            return clarifaiInputs.toArray(ts);
        }
    };

    @Override
    protected  List<ClarifaiOutput<Concept>> doInBackground(Void...Params){

            images.add(ClarifaiInput.forImage(ClarifaiImage.of("https://static.pexels.com/photos/31242/pexels-photo-31242.jpg")));
            images.add(ClarifaiInput.forImage(ClarifaiImage.of("http://www.ikea.com/gb/en/images/products/norrn%C3%A4s-chair-oak-isunda-grey__0105948_pe253720_s5.jpg")));
            images.add(ClarifaiInput.forImage(ClarifaiImage.of("https://fthmb.tqn.com/aG_csasiSllxQJt2CmM011UBbCE=/768x0/filters:no_upscale()/about/hp-computer-on-off-56a6f9e85f9b58b7d0e5cc8b.jpg")));

            List<ClarifaiOutput<Concept>> predictionResults = client.getDefaultModels().generalModel()
                    .predict()
                    .withInputs(images)
                    .withInputs()
                    .executeSync()
                    .get();


            return predictionResults;

    }


    @Override
        protected void onPostExecute(List<ClarifaiOutput<Concept>> Result){

        String conceptName = "";
        for (int j = 0; j < Result.size(); j++){
            ClarifaiOutput<Concept> AllConcepts = Result.get(j);
            for (int i = 0; i < AllConcepts.data().size(); i++){
                conceptName += " " + AllConcepts.data().get(i).name();
            }
            conceptName += "\n";
        }





            Log.v("His raod stretches", conceptName);
            Log.v("The number of results: ", " " + Result.size());
    }

}
