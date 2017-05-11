package com.example.android.sunshine.app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mgo983 on 5/4/17.
 */

public class JSONHandler {

    public String getImageUrl(String JSONString, int position) throws JSONException {

        try{
            final String ImageUrl;
            final JSONObject obj = new JSONObject(JSONString);
            final JSONArray payLoad = obj.getJSONArray("payload");
            final JSONObject firstElement = payLoad.getJSONObject(position);
            ImageUrl = firstElement.getJSONObject("svg").getString("png_thumb");
            Log.v("JSON Url", ImageUrl);

            return ImageUrl;
        }catch (JSONException e){
            Log.e("A JSON Exception: ", "the error: " + e);
        }

        return null;

    }
}
