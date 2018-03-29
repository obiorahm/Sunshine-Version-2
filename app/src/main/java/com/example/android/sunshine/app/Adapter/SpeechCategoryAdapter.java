package com.example.android.sunshine.app.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.AccessorsAndSetters.Word;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.DatabaseConstants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by mgo983 on 3/21/18.
 */

public class SpeechCategoryAdapter extends ArrayAdapter {

    private ArrayList<String> mCategory = new ArrayList<>();
    private LayoutInflater inflater;
    private SpeechWordAdapter speechWordAdapter;

    public SpeechCategoryAdapter(Context context, int resource, SpeechWordAdapter mSpeechWordAdapter){
        super(context, resource);
        inflater = LayoutInflater.from(context);
        speechWordAdapter = mSpeechWordAdapter;
    }

    public void addItem(String category){
        mCategory.add(category);
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return mCategory.size();
    }

    @Override
    public String getItem(int position){
        return mCategory.get(position);
    }



    @Override
    public View getView(int position, View view, final ViewGroup parent) {
        if (null == view){
            view = inflater.inflate(R.layout.item_category, null);
        }
        String category = mCategory.get(position);
        String repCategory = category.replace("_"," ");
        String CapCategory = repCategory.substring(0,1).toUpperCase() + repCategory.substring(1);
        TextView textView = (TextView) view.findViewById(R.id.text_category);
        textView.setText(CapCategory);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.more_category);
        imageButton.setVisibility(View.INVISIBLE);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_category);
        imageViewOnClickListener(imageView, category);

        return view;
    }

    private void imageViewOnClickListener(ImageView imageView, final String category){

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechWordAdapter.clearMWord();
                speechWordAdapter.setmCategory(category);
                populateSpeechWordAdapter(category);
            }
        });

    }

    private void populateSpeechWordAdapter(final String category){
        final DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference(DatabaseConstants.WORD_CATEGORY).child(category);
        categoryReference.orderByChild(category).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Word word = dataSnapshot.getValue(Word.class);
                        if (!speechWordAdapter.compareWord(word))
                            speechWordAdapter.addItem(word);
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
