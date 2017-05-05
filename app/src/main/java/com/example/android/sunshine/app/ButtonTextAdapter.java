package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by mgo983 on 4/3/17.
 */



public class ButtonTextAdapter extends AphasiaAdapter {

    private final Context context;
    private final TextToSpeech myTTS;

    private ArrayList mData = new ArrayList();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_MAX_COUNT= TYPE_IMAGE + 1;

    private TreeSet imageSet = new TreeSet();

    final static String SEARCH_PARAM = "SEARCH_PARAM";




    private LayoutInflater inflater;
    public ButtonTextAdapter(Context context,
                         TextToSpeech myTTS) {
        super(context, R.layout.list_item_search);
        this.context = context;
        this.myTTS = myTTS;
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

                    mHolder.mText.setText(newString[0]);

                    Log.v("GetView Function ", newString[0]);
                        String ImageUrl = "";
                        if (newString.length > 1){
                            try{
                                ImageUrl = getImageUrl(newString[1]);
                            }catch (JSONException e){
                                Log.e("JSONException", e + "");
                            }

                            mHolder.mImage = (ImageView) view.findViewById(R.id.search_image);
                            Glide.with(context).load(Uri.parse(ImageUrl)).into(mHolder.mImage);

                            mHolder.mImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent ImageExplanationActivity = new Intent(getContext(), ImageExplanationFragment.class);
                                    ImageExplanationActivity.putExtra(SEARCH_PARAM, newString[0]);
                                    context.startActivity(ImageExplanationActivity);


                                }
                            });
                        }

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

                    File imgFile = new  File(mData.get(position).toString().replace("file://",""));

                    mHolder.mImage = (ImageView) view.findViewById(R.id.list_captured_image);
                    Glide.with(context).load(imgFile).into(mHolder.mImage);
                    view.setTag(mHolder);
                    break;
            }

        return view;
    }

    private String getImageUrl(String JSONString) throws JSONException {

            final JSONObject obj = new JSONObject(JSONString);
            final JSONArray payLoad = obj.getJSONArray("payload");
            final JSONObject firstElement = payLoad.getJSONObject(0);
            final String ImageUrl = firstElement.getJSONObject("svg").getString("png_thumb");
            Log.v("JSON Url", ImageUrl);

            return ImageUrl;

    }

    private static class ImageViewHolder{
        public ImageView mImage;
    }

    private static class ViewHolder extends ImageViewHolder{
        private TextView mText;
    }



}
