package com.example.android.sunshine.app.AccessorsAndSetters;

/**
 * Created by mgo983 on 3/22/18.
 */

public class Word {

    private String id;
    private String fileName;
    private String word;

    public Word(){}

    public Word(String mFileName, String mWord){
        fileName = mFileName;
        word = mWord;
    }

    public String getId(){ return  id;}
    public String getFileName(){return fileName;}
    public String getWord(){return word;}

    public void setId(String mId){
        id = mId;
    }

    public void setFileName(String mFileName){
        fileName = mFileName;
    }

    public void setWord(String mWord){
        word = mWord;
    }


}
