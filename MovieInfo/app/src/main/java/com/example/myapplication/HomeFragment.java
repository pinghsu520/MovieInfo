/*
 * @author: Mario Verdugo. Ping Hsu, Nathon Smith
 * @description: This is the HomeFragment. It is the first fragment that is
 * displayed, and contains a few things. It contains a search bar, popular movie
 * images, and all the reviews the user has written.
 */
package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.solver.widgets.Helper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;



public class HomeFragment extends Fragment {

    private View myView;
    private  Context context;
    private HomeFragment itself = this;

    // These two arrays are for the poster urls and the poster bitmaps
    private ArrayList<String> posterUrls = new ArrayList<String>();
    private ArrayList<Bitmap> posters = new ArrayList<Bitmap>();
    private ArrayList<Integer> movieIds = new ArrayList<>();
    private ArrayList<String> reviews = new ArrayList<String>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        myView =  inflater.inflate(R.layout.fragment_home, container, false);
        //setting the onclick of the search button
        Button searchButton = myView.findViewById(R.id.search_button);
        Button helpButton = myView.findViewById(R.id.help);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchPressed();
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHelpPressed();
            }
        });


        context = myView.getContext();

        System.out.println("hello");
        //downloads the popular movie posters
        new DownloadTask().execute();

        //sets the reviews
        populateReviews();

        return myView;
    }

    /**
     * This function iterates through the review list, and adds a bunch of text views
     * with the text being the reviews to a linear layout.
     */
    public void populateReviews(){
        String revs = Helpers.readFromFile(context);
        String[] userRevs = revs.split("--");
        LinearLayout allRevs = myView.findViewById(R.id.reviewholder);
        //iterating over the reviews
        for (int i = 0; i < userRevs.length; i++){
            //creating a text view with the text in it.
            TextView rev = new TextView(context);
            rev.setText(userRevs[i]);
            //adding it to linear layout
            allRevs.addView(rev);
        }
    }

    /**
     * this is the function that is called from a seperate fragment that will
     * add a review to the review list, then it will add only that review to
     * the linear layout.
     * @param review
     */
    public void addReview(String review){
        //adding review to the arraylist
        reviews.add(review);

        //getting the linear layout
        LinearLayout allRevs = myView.findViewById(R.id.reviewholder);

        //setting the textview and adding to the LL
        TextView rev = new TextView(context);
        rev.setText(reviews.get(reviews.size() - 1));
        allRevs.addView(rev);
    }

    /**
     * this is the onclick function that is called when the search button is clicked.
     * it displays the search fragment and sends in the querey to the search fragment.
     */
    public void onSearchPressed(){
        //getting the query that was typed
        EditText searchText = (EditText) myView.findViewById(R.id.searchText);
        String query = searchText.getText().toString();

        //adding the querey to the bundle
        System.out.println("query:");
        System.out.println(query);
        if (query.length() > 0){
            Bundle bundle = new Bundle();
            bundle.putString("search", query);

            //creating a fragment and adding the bundle to it
            SearchFragment search = new SearchFragment();
            search.setArguments(bundle);

            FragmentTransaction fTransaction = MainActivity.manager.beginTransaction();
            //adding it so that it will show
            fTransaction.replace(R.id.main_layout, search).addToBackStack(null);
            fTransaction.commit();
        }

    }

    public void onHelpPressed(){
        HelpFragment help = new HelpFragment();
        FragmentTransaction fTransaction = MainActivity.manager.beginTransaction();
        Helpers.transition(this, search);

        //adding it so that it will show
        fTransaction.replace(R.id.main_layout, help).addToBackStack(null);
        fTransaction.commit();

    }

    /**
     * This function goes through a JSONObject, and gets all the posterUrls for the bitmap
     * and also gets the ids corresponding to the urls.
     * @param jsonObject : the jsonObject to iterate over
     */
    public void parseJSONPopularMovies(JSONObject jsonObject){

        try {
            //Getting the results object
            JSONArray resArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < resArray.length()-1; i++) {
                //getting the jsonObject at each array index
                JSONObject jsonObject1 = resArray.getJSONObject(i);
                if(jsonObject1!=null) {
                    //adding the url of the poster to the list
                    String posterUrl = jsonObject1.getString("poster_path");
                    posterUrls.add(posterUrl);

                    //adding the id of the movie to a list
                    Integer movieId = jsonObject1.getInt("id");
                    movieIds.add(movieId);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred during JSON Parsing", e);
        }
    }

    // This private class gets the json information through the URL api.
    private class DownloadTask extends AsyncTask<Object, Void, JSONObject> {
        public String YOUR_API_KEY = "f3de492aa94182ea8b782ec30b1d6453";
        public String popularMoviesURL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + YOUR_API_KEY;

        public JSONObject jsonObject;

        @Override
        protected JSONObject doInBackground(Object[] objects) {
            System.out.println("world");
            //getting the jsonObject
            jsonObject = Helpers.getJSONObjet(popularMoviesURL);
            System.out.println(jsonObject);
            parseJSONPopularMovies(jsonObject);

            //downloading the posters
            downloadMoviePosters();
            return null;
        }

        // The purpose of this function is to call and execute that grabs the information needed
        // from the JSON object. I parsed through the object to grab the meta data.
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            ViewPager view = myView.findViewById(R.id.newMovies);
            Context context = myView.getContext();
            ImageAdapter imageAdapter = new ImageAdapter(itself ,posters, movieIds, "movie");
            view.setAdapter(imageAdapter);

        }

    }

    /**
     * this function goes through all the posterUrls list, and adds the bitmaps to
     * a new arraylist that is a bitmap arraylist
     */
    private void downloadMoviePosters(){
        for (int i = 0; i < posterUrls.size(); i++){
            //downloading the posters and adding to new array
            Bitmap bitmap = Helpers.getBitmapFromURL("http://image.tmdb.org/t/p/w185/" + posterUrls.get(i));
            posters.add(bitmap);
        }
    }

}
