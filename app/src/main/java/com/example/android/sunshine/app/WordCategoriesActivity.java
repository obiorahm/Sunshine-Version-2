package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import com.example.android.sunshine.app.Adapter.ButtonTextAdapter;
import com.example.android.sunshine.app.Adapter.GridAdapter;
import com.example.android.sunshine.app.Adapter.WordCategoryAdapter;
import com.example.android.sunshine.app.Adapter.Words;
import com.example.android.sunshine.app.data.AddWord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mgo983 on 11/13/17.
 */

public class WordCategoriesActivity extends ActionBarActivity {

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private GridAdapter adapter;

    private HashMap adapterItem = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_sense_disambiguation);

        adapter = new WordCategoryAdapter(this, R.layout.item_category);

        Intent intent = this.getIntent();
        String[] searchParam = {""};
        searchParam[0] = intent.getStringExtra(ButtonTextAdapter.SEARCH_PARAM).toLowerCase();

        //get preferred search engine
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String prefSearchParam = sharedPref.getString(getString(R.string.pref_search_key),getString(R.string.pref_search_default_value));

        CheckInternetConnection checkInternetConnection = new CheckInternetConnection(this);

        if (checkInternetConnection.isNetworkConnected()){

            FetchClipArt fetchClipArt = new FetchClipArt(adapter,this, prefSearchParam);
            fetchClipArt.execute(searchParam);
            adapter = fetchClipArt.getAdapter();
        }

        ListView listView = (ListView) findViewById(R.id.word_categories);
        listView.setAdapter(adapter);

    }


    public void signInAnonymously() {
        firebaseAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

            }


        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}
