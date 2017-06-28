package com.example.android.sunshine.app;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.api.request.input.SearchClause;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.SearchHit;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.input.image.ClarifaiURLImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;

/**
 * Created by mgo983 on 6/22/17.
 */
// Content Based Image Retrieval class
public class CBIR extends  AsyncTask<String[], Void, String[]>{
    final ClarifaiClient client = new ClarifaiBuilder("oOH6jbTgfsWll9_X55goV5uZTIgb8L8fdmoM4UQr", "4xmzUBx_N201JpiR6jVTrQPA7tgwi0GTqgmnbYI_")
                                    .client(new OkHttpClient())
                                    .buildSync();
    private String searchString;
    //private String [] clarifaiImageUrls;
    private ImageGridAdapter adapter;
    private Context context;


    public CBIR(String new_searchString, ImageGridAdapter newAdapter, Context newContext){

        searchString = new_searchString;
        //adapter = newAdapter;
        context = newContext;
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
    protected  String[] doInBackground(String []...Params){

            if (Params[0] == null)
                return null;

            client.deleteAllInputs().executeSync();

            for (int i = 0; i < Params[0].length; i++){
                //images.add(ClarifaiInput.forImage(ClarifaiImage.of(Params[0][i])));
                client.addInputs()
                        .plus(ClarifaiInput.forImage(ClarifaiImage.of(Params[0][i])))
                        .executeSync();
            }

        ClarifaiResponse containsConcept =  client.searchInputs(SearchClause.matchConcept(Concept.forName(searchString.toLowerCase()))).getPage(1).executeSync();
        ClarifaiResponse noConcepts = client.searchInputs(SearchClause.matchConcept(Concept.forName(searchString.toLowerCase()).withValue(false))).getPage(1).executeSync();

        /*List<ClarifaiOutput<Concept>> predictionResults = client.getDefaultModels().generalModel()
                    .predict()
                    .withInputs(images)
                    .withInputs()
                    .executeSync()
                    .get();*/
        String [] orderedImageUrl = {};
        if (containsConcept.isSuccessful() && noConcepts.isSuccessful()){
            orderedImageUrl = getOrderedImageUrl((ArrayList) containsConcept.get(), (ArrayList) noConcepts.get());
        }else{
            return Params[0];
        }
            if (containsConcept.isSuccessful()){
                Log.v("another Prediction: ", containsConcept.get() + " number");

            }else{
                Log.v("another Prediction: ", searchString + "not found");

            }


        //    return predictionResults;
        return orderedImageUrl;
    }

    private String[] getOrderedImageUrl(ArrayList containsConcepts, ArrayList noConcepts){
        int length = containsConcepts.size() + noConcepts.size();
        String[] OrderedImageUrl =  new String[length];

        Log.v("containsConcept: ", "size" + containsConcepts.size());
        Log.v("containsConcept: ", "size" + noConcepts.size());

            for (int i = 0; i < containsConcepts.size(); i++){
                SearchHit searchHit = (SearchHit) containsConcepts.get(i);
                OrderedImageUrl[i] = ((ClarifaiURLImage) searchHit.input().image()).url().toString();
                Log.v("orderedImageUrl: ", OrderedImageUrl[i]);
            }
            int containsConceptsLength =  containsConcepts.size();
            for (int j = containsConceptsLength; j < length && noConcepts.size() > 0; j++){
                SearchHit searchHit = (SearchHit) noConcepts.get(j - containsConceptsLength);
                OrderedImageUrl[j] = ((ClarifaiURLImage) searchHit.input().image()).url().toString();
                Log.v("unorderedImageUrl: ", OrderedImageUrl[j]);
            }

        return OrderedImageUrl;
    }


    @Override
        protected void onPostExecute(String[] Result){

        setGridViewAdapter(Result);
        ProgressBar progressBar = (ProgressBar) ((ActionBarActivity) context).findViewById(R.id.explainationProgress);
        progressBar.setVisibility(View.INVISIBLE);
        /*String conceptName = "";
        for (int j = 0; j < Result.size(); j++){
            ClarifaiOutput<Concept> AllConcepts = Result.get(j);
            for (int i = 0; i < AllConcepts.data().size(); i++){
                conceptName += " " + AllConcepts.data().get(i).name();
            }
            conceptName += "\n";
            AllConcepts.data().contains("leaf");
        }*/
     //       Log.v("His raod stretches", conceptName);
    //        Log.v("The number of results: ", " " + Result.size());


    }

    private void setGridViewAdapter(String[] ImageUrls){
        if (ImageUrls != null){
            adapter = new ImageGridAdapter(context, ImageUrls /*ImgStringArr*/);
            GridView gridView = (GridView) ((ActionBarActivity) context).findViewById(R.id.image_gridview);
            gridView.setAdapter(adapter);

        }
    }

}
