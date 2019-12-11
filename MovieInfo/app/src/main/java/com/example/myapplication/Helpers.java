/*
 * @author: Mario Verdugo
 * @description: This is a class that holds a lot of similar functions across
 * multiple classes. I added this to reduce and be able to reuse code across
 * the entire app.
 */


package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Helpers {


    /**
     * This function will get an image from a url. It does this using a url and connection.
     * @param src : The url of the image.
     * @return bitmap. the bitmap of the image.
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            //opening the connection
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            //reading the connection
            InputStream input = connection.getInputStream();
            //getting the bitmap
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * given a url it will get the response from the url. It will return
     * a jsonObject that is the response of the get from the URL.
     * @param baseurl : the url of the website to get the request from.
     * @return jsonObject. the jsonObject of the response.
     */
    public static JSONObject getJSONObjet(String baseurl){
        JSONObject jsonObject = null;
        try {
            URL url = new URL(baseurl);
            String line;
            String json = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            //reading through the JSON
            while ((line = in.readLine()) != null) {
                json += line;
            }
            in.close();

            jsonObject = new JSONObject(json);

        } catch (Exception e){
            e.printStackTrace();
        }

        //returning the new object. Null if anything bad happens.
        return jsonObject;
    }



    public static void transition(Fragment fromView, Fragment toView){

        int FADE_DEFAULT_TIME = 500;
        int MOVE_DEFAULT_TIME = 500;

        //fadeing current fragment out
        Fade exitFade = new Fade();
        exitFade.setDuration(FADE_DEFAULT_TIME);
        fromView.setExitTransition(exitFade);

        //fading together
        TransitionSet enterTransitionSet = new TransitionSet();
        enterTransitionSet.addTransition(TransitionInflater.from(fromView.getContext()).inflateTransition(android.R.transition.move));
        enterTransitionSet.setDuration(MOVE_DEFAULT_TIME);
        enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);
        toView.setSharedElementEnterTransition(enterTransitionSet);

        //fading new in
        Fade enterFade = new Fade();
        enterFade.setStartDelay(MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME);
        enterFade.setDuration(FADE_DEFAULT_TIME);
        toView.setEnterTransition(enterFade);
    }


    /**
     * goes through all the reviews from the file and adds them to a string seperated by
     * --.
     * @param context the context of the fragment
     * @return a string that is all the reviews.
     */
    public static String readFromFile(Context context) {
        String ret = "";

        try {
            //opening the file
            InputStream inputStream = context.openFileInput("reviews.txt");

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                //reading through the file
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    if(receiveString != ""){
                        //adding the line to the string
                        stringBuilder.append(receiveString + "--");
                    }
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

}
