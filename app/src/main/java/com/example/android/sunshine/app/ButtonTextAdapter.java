package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by mgo983 on 4/3/17.
 */



public class ButtonTextAdapter extends AphasiaAdapter {

    private final Context context;
    private final TextToSpeech myTTS;
    private String searchEngine;
    private Color availableColor = new Color();

    //private ArrayList mData = new ArrayList();
    public ArrayList mData = new ArrayList();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_MAX_COUNT= TYPE_IMAGE + 1;

    private TreeSet imageSet = new TreeSet();

    final static String SEARCH_PARAM = "SEARCH_PARAM";
    public final static String EXTRA_DIALOG_IMAGE = "com.example.android.sunshine.extraImage";




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

                    mHolder.mImage = (ImageView) view.findViewById(R.id.search_image);
                    //setImageGridColor(mHolder, newString[0]);
                    glideLoadImage(position, newString[1], newString[0] ,mHolder);

                    makeProgressBarInvisible(mHolder, view);
                    setImageOnClickListener(mHolder, newString[0]);
                    setTextOnClickListener(mHolder);

                    view.setTag(mHolder);
                    break;
                case TYPE_IMAGE:
                    mHolder = new ViewHolder();

                    final File imgFile = new  File(mData.get(position).toString().replace("file://",""));

                    mHolder.mImage = (ImageView) view.findViewById(R.id.list_captured_image);
                    Glide.with(context).load(imgFile).into(mHolder.mImage);

                    mHolder.mImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DialogFragment newFragment = new ImageDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_DIALOG_IMAGE, imgFile.toString());

                            newFragment.setArguments(bundle);
                            newFragment.show(((ActionBarActivity) context).getSupportFragmentManager(),"what?");

                        }
                    });

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

    private  void makeProgressBarInvisible(ViewHolder mHolder, View view){
        mHolder.mProgressBar = (ProgressBar) view.findViewById(R.id.image_load_complete);
        mHolder.mProgressBar.setVisibility(View.INVISIBLE);
    };
    private void setImageOnClickListener(ViewHolder mHolder, final String focusWord){
        mHolder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTTS.speak(focusWord, TextToSpeech.QUEUE_FLUSH, null);
                Intent ImageExplanationActivity = new Intent(getContext(), com.example.android.sunshine.app.ImageExplanationActivity.class);
                ImageExplanationActivity.putExtra(SEARCH_PARAM, focusWord);
                context.startActivity(ImageExplanationActivity);
            }
        });
    };

    private void setTextOnClickListener(ViewHolder mHolder){
        final String  speech = mHolder.mText.getText().toString();
        mHolder.mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

            }
        });
    }


    private String[] getImageUrl (String searchEngine, String JSONString, int position){

        String[] imageUrl = {};
        JSONHandler jsonHandler;
        if (JSONString != null){
            Log.v("In Pixabay section", this.searchEngine);
            try{
                switch (searchEngine){
                    case "1":
                        jsonHandler = new PixabayJSONHandler();
                        imageUrl = jsonHandler.getImageUrl(JSONString, position);
                        break;
                    default:
                        jsonHandler =new OpenClipArtJSONHandler();
                        imageUrl = jsonHandler.getImageUrl(JSONString, position);
                        break;
                }
            }catch (JSONException e){
                Log.e("JSONException ", e + "");
            }
        }
        return imageUrl;
    }

    private void glideLoadImage(int position, String JSONString, String searchString, ViewHolder mHolder){
        if (availableColor.searchColor(searchString.toLowerCase())){
            Log.v("search color content", JSONString + "and" + searchString);
            Glide.with(context).load(R.drawable.colorchart).centerCrop().into(mHolder.mImage);
            return;
        }

        if (position != 1) {
            String[] ImageUrl = { };

            ImageUrl = this.getImageUrl(this.searchEngine,JSONString,0);
            if (ImageUrl != null){
                Glide.with(context).load(Uri.parse(ImageUrl[0])).centerCrop().into(mHolder.mImage);
            }
        }else{
            File imgFile = new  File(JSONString.toString().replace("file://",""));

            Glide.with(context).load(imgFile).centerCrop().into(mHolder.mImage);
        }
    }

    private  void setImageGridColor(ViewHolder mHolder, String color){

        try{

            Class res = R.color.class;
            Field field = res.getField( color );
            int colorId = field.getInt(null);
            if (availableColor.searchColor(color)){
                mHolder.mImage.setBackgroundColor(context.getResources().getColor(colorId));

            }


        }catch(NoSuchFieldException e){

        }catch(IllegalAccessException e){

        }

        }


}
