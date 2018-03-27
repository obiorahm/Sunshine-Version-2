package com.example.android.sunshine.app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sunshine.app.AccessorsAndSetters.Word;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.DatabaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by mgo983 on 3/22/18.
 */

public class SpeechWordAdapter extends ArrayAdapter {

    private ArrayList<Word> mWord = new ArrayList<>();
    private LayoutInflater inflater;
    private Context mContext;
    private String mCategory;

    public SpeechWordAdapter(Context context, int resource){
        super(context, resource);
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    public boolean compareWord(Word word){
        int i = mWord.size() - 1;
        if (i < 0) return false;
        Word prevWord = mWord.get(i);
        return (prevWord.getWord().equals(word.getWord()));
    }

    public void addItem(Word word){
        mWord.add(word);
        notifyDataSetChanged();
    }

    public void setmCategory(String category){
        mCategory = category;
    }

    @Override
    public int getCount(){
        return mWord.size();
    }

    @Override
    public Word getItem(int position){
        return mWord.get(position);
    }

    @Override

    public View getView(int position, View view, ViewGroup parent){
        if(null == view){
            view = inflater.inflate(R.layout.grid_item_word, null);
        }

        WordViewHolder wordViewHolder = new WordViewHolder(view);

        Word currWord = mWord.get(position);
        String word = currWord.getWord();
        String fileName = currWord.getFileName();
        wordViewHolder.mTextView.setText(word);

        loadImage(fileName, wordViewHolder);
        setOnClickListener(wordViewHolder);
        return view;
    }

    private static class WordViewHolder{
        TextView mTextView;
        ImageView mImageView;

        public WordViewHolder(View view){
            mTextView = (TextView) view.findViewById(R.id.text_word);
            mImageView = (ImageView) view.findViewById(R.id.image_word);
        }
    }

    private void setOnClickListener(final WordViewHolder wordViewHolder){
        wordViewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) ((Activity) mContext).getWindow().getDecorView().findViewById(R.id.compose_word);
                String  sentence = editText.getText().toString();
                int position = editText.getSelectionStart();
                Log.d("position", "" + position);
                String word = wordViewHolder.mTextView.getText().toString() + " ";
                editText.setText(sentence.substring(0, position) + word + sentence.substring(position));
                editText.setSelection(position + word.length());
            }
        });
    }

    private void loadImage(String fileName, final WordViewHolder wordViewHolder){
        String referencePath = DatabaseConstants.WORD_IMAGE_REFERENCE + "/" + mCategory + "/" + fileName;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(referencePath);
        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(mContext).load(uri).into(wordViewHolder.mImageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("image Error ", e.toString());
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

            }
        });
    }

}
