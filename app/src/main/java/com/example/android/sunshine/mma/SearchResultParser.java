package com.example.android.sunshine.mma;

/**
 * Created by mgo983 on 4/3/17.
 */

public class SearchResultParser {

    public String [] PhraseToArray(String SearchResult){

        String[] returnstatement =  SearchResult.split(" ");

        return returnstatement;
    }
}


