package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by mgo983 on 4/3/17.
 */



public class ButtonTextAdapter extends AphasiaAdapter {

    private final Context context;
    private final TextToSpeech myTTS;
    private String searchEngine;

    //private ArrayList mData = new ArrayList();
    public ArrayList mData = new ArrayList();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_MAX_COUNT= TYPE_IMAGE + 1;

    private TreeSet imageSet = new TreeSet();

    final static String SEARCH_PARAM = "SEARCH_PARAM";




    private LayoutInflater inflater;
    public ButtonTextAdapter(Context context,
                         TextToSpeech myTTS, String searchEngine) {
        super(context, R.layout.list_item_search);
        this.context = context;
        this.myTTS = myTTS;
        this.searchEngine = searchEngine;
        inflater = LayoutInflater.from(context);

    }

    @Override
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
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        int type = getItemViewType(position);
        final ViewHolder mHolder;
        if (null == view){
            switch (type){
                case TYPE_ITEM:
                    view = inflater.inflate(R.layout.list_item_search, null);
                    break;
                case TYPE_IMAGE:
                    view = inflater.inflate(R.layout.list_item_image, null);
            }
        }
            switch (type){
                case TYPE_ITEM:
                    mHolder = new ViewHolder();
                    mHolder.mText = (TextView) view.findViewById(R.id.list_item_word_textview);
                    final String[] newString = mData.get(position).toString().split("&&");

                    mHolder.mText.setText(newString[0].substring(0,1).toUpperCase() + newString[0].substring(1));

                    Log.v("GetView Function ", newString[0]);


                            mHolder.mImage = (ImageView) view.findViewById(R.id.search_image);
                            if (position != 1) {
                                String[] ImageUrl = { };

                                try {
                                    if (newString[1] != null){
                                        Log.v("In Pixabay section", this.searchEngine);

                                        if (this.searchEngine.equals("1")){

                                            PixabayJSONHandler jsonHandler = new PixabayJSONHandler();

                                            ImageUrl = jsonHandler.getImageUrl(newString[1], 0);
                                        }else{
                                            OpenClipArtJSONHandler jsonHandler =new OpenClipArtJSONHandler();
                                            ImageUrl = jsonHandler.getImageUrl(newString[1], 0);
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.e("JSONException", e + "");
                                }
                                if (ImageUrl != null){
                                    Glide.with(context).load(Uri.parse(ImageUrl[0])).centerCrop().into(mHolder.mImage);
                                }

                            }else{
                                File imgFile = new  File(newString[1].toString().replace("file://",""));

                                Glide.with(context).load(imgFile).centerCrop().into(mHolder.mImage);

                            }

                            mHolder.mProgressBar = (ProgressBar) view.findViewById(R.id.image_load_complete);
                            mHolder.mProgressBar.setVisibility(View.INVISIBLE);



                            mHolder.mImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent ImageExplanationActivity = new Intent(getContext(), com.example.android.sunshine.app.ImageExplanationActivity.class);
                                    ImageExplanationActivity.putExtra(SEARCH_PARAM, newString[0]);
                                    context.startActivity(ImageExplanationActivity);


                                }
                            });


                    mHolder.mText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String  speech = mHolder.mText.getText().toString();
                            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                        }
                    });

                    view.setTag(mHolder);
                    break;
                case TYPE_IMAGE:
                    mHolder = new ViewHolder();

                    final File imgFile = new  File(mData.get(position).toString().replace("file://",""));

                    mHolder.mImage = (ImageView) view.findViewById(R.id.list_captured_image);
                    Glide.with(context).load(imgFile).into(mHolder.mImage);

                    view.setTag(mHolder);
                    break;
            }


        return view;
    }


    private static class ImageViewHolder{
        public ImageView mImage;
    }

    private static class ViewHolder extends ImageViewHolder{
        private TextView mText;
        private ProgressBar mProgressBar;
    }



}
