package com.example.android.sunshine.app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    private Context mContext;
    private int prevPosition = 0;

    private class CategoryViewHolder{
        public ImageView imageView;
        public TextView textView;
        public ImageButton imageButton;
        public LinearLayout linearLayout;
        public boolean IsGreen = true;

        public CategoryViewHolder(View view){
            imageView = view.findViewById(R.id.image_category);
            textView = view.findViewById(R.id.text_category);
            imageButton = view.findViewById(R.id.more_category);
            linearLayout = view.findViewById(R.id.l_grid_item_word);
        }
    }

    public SpeechCategoryAdapter(Context context, int resource, SpeechWordAdapter mSpeechWordAdapter){
        super(context, resource);
        inflater = LayoutInflater.from(context);
        speechWordAdapter = mSpeechWordAdapter;
        mContext = context;
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
        final CategoryViewHolder viewHolder = new CategoryViewHolder(view);
        String category = mCategory.get(position);
        String repCategory = category.replace("_"," ");
        String CapCategory = repCategory.substring(0,1).toUpperCase() + repCategory.substring(1);
        viewHolder.textView.setText(CapCategory);
        viewHolder.imageButton.setVisibility(View.INVISIBLE);

        imageViewOnClickListener(view, viewHolder, category, parent, position);


        return view;
    }

    private void imageViewOnClickListener(final View sview, final CategoryViewHolder viewHolder, final String category, final ViewGroup parent, final int position){

       sview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechWordAdapter.clearMWord();
                speechWordAdapter.setmCategory(category);
                populateSpeechWordAdapter(category, view);
                sview.setSelected(true);
                //((ListView) parent).setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                //((ListView) parent).setSelector(mContext.getResources().getDrawable(R.drawable.dialog_border));
                //sview.setFocusable(true);
                //sview.setPressed(true);


            }
        });

    }

    private void populateSpeechWordAdapter(final String category, final View view){
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
        view.setSelected(true);
    }

}
