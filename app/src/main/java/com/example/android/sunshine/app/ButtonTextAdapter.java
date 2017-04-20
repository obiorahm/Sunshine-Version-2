package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by mgo983 on 4/3/17.
 */



public class ButtonTextAdapter extends ArrayAdapter {

    private final Context context;
    private final String[] web;
    private final TextToSpeech myTTS;

    private ArrayList mData = new ArrayList();
    //private final String[] imageUrls;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_MAX_COUNT= TYPE_IMAGE + 1;

    private TreeSet imageSet = new TreeSet();




    private LayoutInflater inflater;
    public ButtonTextAdapter(Context context,
                         String[] web, TextToSpeech myTTS) {
        super(context, R.layout.list_item_search, web);
        this.context = context;
        this.web = web;
        this.myTTS = myTTS;
        //this.imageUrls = imageUrls;
        inflater = LayoutInflater.from(context);

    }

    public void addItem(final String item){
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addImage(final String imageUrl){
        mData.add(imageUrl);
        imageSet.add(mData.size() - 1);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType (int position){
        if (imageSet.contains(position)){
            return TYPE_IMAGE;
        }else{
            return TYPE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount(){
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount(){
        return mData.size();
    }

    @Override
    public String getItem(int position){
        return (String) mData.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        int type = getItemViewType(position);
        if (null == view){
            switch (type){
                case TYPE_ITEM:
                    // view = inflater.inflate(R.layout.list_item_search,parent,false);
                    view = inflater.inflate(R.layout.list_item_search, null, false);
                    final TextView txtTitle = (TextView) view.findViewById(R.id.list_item_word_textview);
                    txtTitle.setText((String) mData.get(position));
                    ImageButton button = (ImageButton) view.findViewById(R.id.imagebutton_area);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Perform action on click
                            String  speech = txtTitle.getText().toString().toUpperCase();
                            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    });
                    break;
                case TYPE_IMAGE:
                    view = inflater.inflate(R.layout.list_item_image, null,true);

                    File imgFile = new  File(mData.get(position).toString().replace("file://",""));

                    final ImageView imageView = (ImageView) view.findViewById(R.id.list_captured_image);
                    Picasso.with(context).load(imgFile).into(imageView);
                    break;
            }
        }
        return view;
    }

    public void setupTextandSpeech(View view, String word ){
        final TextView txtTitle = (TextView) view.findViewById(R.id.list_item_word_textview);
        txtTitle.setText(word);
        ImageButton button = (ImageButton) view.findViewById(R.id.imagebutton_area);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                String  speech = txtTitle.getText().toString().toUpperCase();
                myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }


}
