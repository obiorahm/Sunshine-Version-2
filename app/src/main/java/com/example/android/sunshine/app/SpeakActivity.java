package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import com.example.android.sunshine.app.Adapter.SpeechCategoryAdapter;
import com.example.android.sunshine.app.data.DatabaseConstants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mgo983 on 3/21/18.
 */

public class SpeakActivity extends ActionBarActivity {

    SpeechCategoryAdapter speechCategoryAdapter;
    ListView categoryListView;

    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);

        speechCategoryAdapter = new SpeechCategoryAdapter(this, R.layout.item_category);
        categoryListView = (ListView) findViewById(R.id.category_list);
        getAllCategories();
        //categoryListView.setAdapter(speechCategoryAdapter);


    }


    private void getAllCategories(){
        DatabaseReference categoryDatabaseReference = FirebaseDatabase.getInstance().getReference(DatabaseConstants.WORD_CATEGORY);
        categoryDatabaseReference.orderByChild(DatabaseConstants.WORD_CATEGORY).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null){
                    String category = dataSnapshot.getKey();
                    Log.d("dataSnapshot values", s + "");
                    speechCategoryAdapter.addItem(category);
                    categoryListView.setAdapter(speechCategoryAdapter);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

