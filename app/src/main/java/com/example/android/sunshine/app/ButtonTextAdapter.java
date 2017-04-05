package com.example.android.sunshine.app;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by mgo983 on 4/3/17.
 */



public class ButtonTextAdapter extends ArrayAdapter {

    private final Context context;
    private final String[] web;
    private final TextToSpeech myTTS;
    //private final String[] imageUrls;

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
    @Override
    public View getView(int position, View view, ViewGroup parent) {


        if (null == view){
            view = inflater.inflate(R.layout.list_item_search,parent,false);

        }

        view = inflater.inflate(R.layout.list_item_search, null,true);
        final TextView txtTitle = (TextView) view.findViewById(R.id.list_item_word_textview);

        txtTitle.setText(web[position]);

        ImageButton button = (ImageButton) view.findViewById(R.id.imagebutton_area);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                //speakWords();

                String  speech = txtTitle.getText().toString();
                myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

            }
        });

        return view;
    }


}
