package com.example.android.sunshine.app;

import android.content.Context;
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



public class ButtonTextAdapter extends ArrayAdapter {

    private final Context context;
    //private final String[] web;
    private final TextToSpeech myTTS;

    private ArrayList mData = new ArrayList();
    //private final String[] imageUrls;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_MAX_COUNT= TYPE_IMAGE + 1;

    private TreeSet imageSet = new TreeSet();




    private LayoutInflater inflater;
    public ButtonTextAdapter(Context context,
                         TextToSpeech myTTS) {
        super(context, R.layout.list_item_search);
        this.context = context;
        //this.web = web;
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
                    String[] newString = mData.get(position).toString().split("&&");
                    txtTitle.setText(newString[0]);

                        String ImageUrl = "";
                        if (newString.length > 1){
                            try{
                                ImageUrl = getImageUrl(newString[1]);
                            }catch (JSONException e){
                                Log.e("JSONException", e + "");
                            }

                            ImageView imageView = (ImageView) view.findViewById(R.id.search_image);
                            Picasso.with(context).load(Uri.parse(ImageUrl)).into(imageView);
                        }




                    txtTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String  speech = txtTitle.getText().toString();
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

    private String getImageUrl(String JSONString) throws JSONException {

            final JSONObject obj = new JSONObject(JSONString);
            final JSONArray payLoad = obj.getJSONArray("payload");
            final JSONObject firstElement = payLoad.getJSONObject(0);
            final String ImageUrl = firstElement.getJSONObject("svg").getString("png_thumb");
            Log.v("JSON Url", ImageUrl);

            return ImageUrl;

    }

}
