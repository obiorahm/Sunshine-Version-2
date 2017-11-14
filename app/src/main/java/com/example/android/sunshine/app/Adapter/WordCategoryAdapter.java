package com.example.android.sunshine.app.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.example.android.sunshine.app.CheckInternetConnection;
import com.example.android.sunshine.app.FetchClipArt;
import com.example.android.sunshine.app.JSONHandler;
import com.example.android.sunshine.app.OpenClipArtJSONHandler;
import com.example.android.sunshine.app.OpenGalleryObjectActivity;
import com.example.android.sunshine.app.PixabayJSONHandler;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.WordCategoriesActivity;
import com.example.android.sunshine.app.data.AddWord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mgo983 on 11/13/17.
 */

public class WordCategoryAdapter extends GridAdapter {

    private LayoutInflater inflater;
    private ArrayList<Words> mData = new ArrayList<>();
    private Context context;
    private String searchEngine;

    public WordCategoryAdapter(Context _context, int resource, String _searchEngine){
        super(_context, resource);
        inflater = LayoutInflater.from(_context);
        context = _context;
        searchEngine = _searchEngine;
    }

    public void addItem(final String category, final Words wordData){
        mData.add(wordData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return mData.size();
    }

    @Override
    public Words getItem(int position){
        return  mData.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) { convertView = inflater.inflate(R.layout.item_category,null);}

        final Words word = mData.get(position);
        Log.d("The position ", position + " " + word.getCategory());

        TextView textView = (TextView) convertView.findViewById(R.id.text_category);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_category);

        final String category = word.getCategory();

        textView.setText(category);

        String url = word.getUrl();
        String[] jsonUrl ={};
        JSONHandler jsonHandler = new JSONHandler();
        final int FIRST_POSITION = 0;
        try{
            switch (searchEngine){
                case FetchClipArt.ENGINE_PIXABAY:
                    jsonHandler = new PixabayJSONHandler();
                    jsonUrl = jsonHandler.getImageUrl(url);
                    break;
                case FetchClipArt.ENGINE_OPENCLIPART:
                    jsonHandler = new OpenClipArtJSONHandler();
                    jsonUrl = jsonHandler.getImageUrl(url);
                    break;
            }

        }catch (JSONException e){

            Log.e("JSON Error: ", e.toString());

        }

        setImageOnClickListener(imageView, category, word);

        if (jsonUrl != null) url = jsonUrl[FIRST_POSITION];

        Glide
            .with(context)
            .load(url)
            .into(imageView);

        return convertView;

    }
    private void setImageOnClickListener(ImageView imageView, final String category, final Words word){
        final String[] searchWord = {""};
        searchWord[0] = word.getWord();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInternetConnection checkInternetConnection = new CheckInternetConnection(context);

                GridAdapter adapter = new GridAdapter(context, R.id.item_grid);

                if (category.equals("INTERNET")){
                    if (checkInternetConnection.isNetworkConnected()){

                        FetchClipArt fetchClipArt = new FetchClipArt(adapter,context, searchEngine);
                        fetchClipArt.execute(searchWord);
                    }
                }else{
                    localSearch(searchWord[0],category, adapter);
                }

            }
        });
    }

    private void localSearch(final String searchString,  final String category, final GridAdapter adapter){


            FirebaseUser firebaseUser = OpenGalleryObjectActivity.firebaseAuth.getCurrentUser();
            if (firebaseUser == null){
                ((OpenGalleryObjectActivity) context).signInAnonymously();
            }
            final Query mDatabaseQuery = FirebaseDatabase.getInstance().getReference(AddWord.WORD_REFERENCE).child(searchString.toLowerCase());
            final String WORD_IMAGE_REFERENCE  = "symbols";
            Log.d("the Query", mDatabaseQuery.toString());

            mDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        long count = dataSnapshot.getChildrenCount();
                        final String [] urls = new String[(int)count];
                        final ArrayList lUrls = new ArrayList();
                        int i = 0;
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            String wordEntries = (String) child.getValue();
                            String[] getFileName = wordEntries.split("/");
                            Log.d("The entries: ", wordEntries);
                            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

                            String currCategory = getFileName[0];
                            String fileName = getFileName[2];
                            if (category.equals(currCategory)){
                                firebaseStorage.child( WORD_IMAGE_REFERENCE + "/" + currCategory + "/" + fileName).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                lUrls.add(uri.toString());
                                                urls[lUrls.size() - 1] = uri.toString();
                                                setImageAdapter(adapter,lUrls);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Download error ", e.toString());
                                    }
                                });
                            }


                        }


                    }else{
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void setImageAdapter(GridAdapter adapter, ArrayList<String> lUrls){
            String [] url = new String[lUrls.size()];
            int count = 0;
            for (String urls : lUrls){
                    url[count] = urls;
                count++;
            }
            adapter = new GridAdapter(context, url);
            GridView gridView = (GridView) ((ActionBarActivity) context).findViewById(R.id.image_gridview);
            gridView.setAdapter(adapter);

            ProgressBar progressBar = (ProgressBar) ((ActionBarActivity) context).findViewById(R.id.explanationProgress);
            progressBar.setVisibility(View.INVISIBLE);



        }
}
