/*
 * @author: Mario Verdugo. Ping Hsu, Nathon Smith
 * @description: This is the Movie fragment. this is used to display information
 * about a single movie. it contains the title, poster, poster of similar movies,
 * posters of cast members, and buttons to got to websites, review the movie.
 */

package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 *  The purpose of this movie fragment is to display all movie information. This is used in
 *  fragments and displays all Movie object information including parsing through the JSON arrays
 *  that were fetched.
 */
public class MovieFragment extends Fragment {

    public Movie movie;
    private ImageView image;
    private TextView title;
    private TextView popularity;
    private TextView release;
    private TextView overview;
    private String id;
    private MovieFragment itself = this;

    private View myView;

    private Button browser;
    private String searchUrl = "";
    private String OpenBrowserUrl="";
    private String videoUrl="";
    private String FinalVideoURL="";

    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView =  inflater.inflate(R.layout.fragment_movie, container, false);
        //getting the id correspponding to the movie.
        Bundle bundle = getArguments();
        id = bundle.getString("id");

        //setting urls to open later
        OpenBrowserUrl="https://www.themoviedb.org/movie/"+id;
        searchUrl = "https://api.themoviedb.org/3/movie/" + id + "?api_key=17e7d15a4fd879e7d97ec91084cc705b&language=en-US";
        videoUrl = "https://api.themoviedb.org/3/movie/" + id + "/videos?api_key=17e7d15a4fd879e7d97ec91084cc705b&language=en-US";

        //executing 3 async tasks that populate images
        new DownloadTask().execute();
        new castTask().execute();
        new relatedTask().execute();
        return myView;
    }

    //sets some onclicks to buttons programmatically
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //setting the onclick of the poster to show a youtube video of the trailer.
        ImageView view = (ImageView) myView.findViewById(R.id.poster);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("video url");
                System.out.println(FinalVideoURL);
                if (FinalVideoURL.length() > 5){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(FinalVideoURL));
                    getActivity().startActivity(i);
                }

            }
        });

        //setting the onclick of the browser button to open the website of the image.
        Button button = (Button) myView.findViewById(R.id.browserOpen);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(OpenBrowserUrl));
                getActivity().startActivity(i);
            }
        });


        //setting the review button to the onrate function
        Button reviewButton = (Button) myView.findViewById(R.id.review);
        reviewButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onRate(MainActivity.manager, new ReviewFragment());
            }
        });

        //setting the share button to call the onShare function
        Button shareButton = (Button) myView.findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onShare(MainActivity.manager, new ContactFragment());
            }
        });


    }


    /**
     * calls the contact fragment when the share button is clicked.
     * @param manager the fragment manager
     * @param contact the contact fragment
     */
    public void onShare(FragmentManager manager, ContactFragment contact){
        //settting the container activity.
        contact.setContainerActivity(getActivity());

        //setting the bundle to contain the overview and title.
        Bundle bundle = new Bundle();
        bundle.putString("overview", this.movie.getOverview());
        bundle.putString("title", this.movie.getTitle());

        //setting the fragment to contain the bundle and showing the fragment
        contact.setArguments(bundle);
        FragmentTransaction fTransaction = manager.beginTransaction();
        Helpers.transition(this, contact);
        fTransaction.replace(R.id.main_layout, contact).addToBackStack(null);
        fTransaction.commit();
    }

    /**
     * calls the review fragment when the review button movie is pressed.
     * @param manager the FragmentManager
     * @param reviews the review fragment
     */
    public void onRate(FragmentManager manager, ReviewFragment reviews){
        //setting the bundle to contain the id and title of the movie
        Bundle bundle = new Bundle();
        bundle.putString("title", this.movie.getTitle());
        bundle.putString("id", this.movie.getId());

        //settting the fragment to have the bundle and showing it
        reviews.setArguments(bundle);
        FragmentTransaction fTransaction = manager.beginTransaction();
        Helpers.transition(this, reviews);
        fTransaction.replace(R.id.main_layout, reviews).addToBackStack(null);
        fTransaction.commit();

    }

    /**
     * parses through the json and gets the url of the youtube video.
     * @param url the url to parse the json from
     * @throws IOException if the file is incorrect
     * @throws JSONException if the url is incorrect
     */
    public void createJson(String url) throws IOException, JSONException {
        //getting the json
        JSONObject jsonObject1 = Helpers.getJSONObjet(url);

        //going through the results
        JSONArray a=(jsonObject1.getJSONArray("results"));
        if(a.length()>2) {
            //setting the url
            JSONObject b = (JSONObject) a.get(1);
            String videoID = (b.getString("key"));
            FinalVideoURL = "https://www.youtube.com/watch?v=" + videoID;
            System.out.println("final video url");
            System.out.println(FinalVideoURL);
        }
    }

    /*
    This is the async task that actually downloads the poster images.
    It does this by parsing JSON from the MovieDatabase API.
    */
    private class DownloadTask extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Object[] objects) {
            movie = new Movie();

            //getting the url of the youtube video
            try{
                createJson(videoUrl);
            } catch (Exception e){
                e.printStackTrace();
            }

            //getting the movieObject
            JSONObject movieObject = Helpers.getJSONObjet(searchUrl);

            //setting the bitmap
            try{
                Bitmap bitmap = Helpers.getBitmapFromURL("http://image.tmdb.org/t/p/w185/" + movieObject.getString("poster_path"));
                movie.setPoster(bitmap);
            } catch (Exception e){
                e.printStackTrace();
            }
            return movieObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //setting the contents of the movie object given the json
            try {
                movie.setVoteAverage(jsonObject.getInt("vote_average"));
                movie.setOverview(jsonObject.getString("overview"));
                movie.setTitle(jsonObject.getString("title"));
                movie.setPopularity(jsonObject.getDouble("popularity"));
                movie.setReleaseDate(jsonObject.getString("release_date"));
            } catch (Exception e) { e.printStackTrace(); }

            //setting the views to have proper information
            image = myView.findViewById(R.id.poster);
            title = myView.findViewById(R.id.title);
            //popularity = myView.findViewById(R.id.popularity);
            release = myView.findViewById(R.id.release);
            overview = myView.findViewById(R.id.overview);

            image.setImageBitmap(movie.getPoster());
            title.setText(movie.getTitle());
            //popularity.setText(Double.toString(movie.getPopularity()));
            release.setText("Release Date: " + movie.getReleaseDate());
            overview.setText(movie.getOverview());

                /*
               // LinearLayout ll = (LinearLayout) myView.findViewById(R.id.related);
                for (int i = 0; i < relatedMoviePosters.size(); i++){
                    ImageView image =  new ImageView(myView.getContext());
                    image.setImageBitmap(relatedMoviePosters.get(i));

                    final int position = i;

                    image.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            MovieFragment movie = new MovieFragment();
                            Bundle bundle = new Bundle();
                            Movie newMovie = relatedMovies.get(position);

                            bundle.putString("id", newMovie.getId());
                            movie.setArguments(bundle);
                            FragmentTransaction fTransaction = MainActivity.manager.beginTransaction();
                            fTransaction.replace(R.id.main_layout, movie).addToBackStack(null);
                            fTransaction.commit();
                        }
                    });

                    //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    //lp.setMargins(10, 10, 10, 10);
                    //image.setLayoutParams(lp);
                    //ll.addView(image);
                }
                 */
        }
    }


    /*
   This is the async task that actually downloads the cast images.
   It does this by parsing JSON from the MovieDatabase API.
   */
    private class castTask extends AsyncTask<Object, Void, JSONObject> {

        private ArrayList<Bitmap> castPosters;
        private ArrayList<Integer> castIds;
        private String castUrl = "https://api.themoviedb.org/3/movie/" + id + "/credits?api_key=17e7d15a4fd879e7d97ec91084cc705b";

        @Override
        protected JSONObject doInBackground(Object[] objects) {
            castPosters = new ArrayList<>();
            castIds = new ArrayList<>();
            //getting the json object
            JSONObject castObject = Helpers.getJSONObjet(castUrl);

            //fills the information of the arrays
            fillCastInformation(castObject);
            return castObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
                //sets the image adapter
                ViewPager castView = myView.findViewById(R.id.cast);
                ImageAdapter castAdapter = new ImageAdapter(itself, castPosters, castIds, "cast");
                castView.setAdapter(castAdapter);
        }


        /**
         * goes through the json and dowloads all the poster images of the cast, and
         * adds them to an array as well as their ids associated with them.
         * @param jsonObject the jsonObject to iterate through
         */
        public void fillCastInformation(JSONObject jsonObject){
            try{
                //the cast json object
                JSONArray cast = jsonObject.getJSONArray("cast");
                //iterating through and downlaoding bitmaps
                for (int i = 0; i < cast.length(); i++){
                    JSONObject castMember = cast.getJSONObject(i);
                    if (castMember.getString("profile_path") != "null" ){
                        //downloading bitmaps and adding ids.
                        String pictureUrl = "http://image.tmdb.org/t/p/w185/" + castMember.getString("profile_path");
                        Bitmap bitmap = Helpers.getBitmapFromURL(pictureUrl);
                        castPosters.add(bitmap);
                        castIds.add(castMember.getInt("id"));
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /*
   This is the async task that actually downloads the related movie poster images.
   It does this by parsing JSON from the MovieDatatbase API. I have to have a whole differnt
   task becuase the fields of the json are the same
   */
    private class relatedTask extends AsyncTask<Object, Void, JSONObject> {

        private ArrayList<Bitmap> relatedMoviePosters;
        private ArrayList<Integer> relatedIds;
        private String similarUrl = "https://api.themoviedb.org/3/movie/" + id + "/similar?api_key=17e7d15a4fd879e7d97ec91084cc705b&language=en-US&page=1";


        @Override
        protected JSONObject doInBackground(Object[] objects) {
            relatedMoviePosters = new ArrayList<>();
            relatedIds = new ArrayList<>();

            //getting the json
            JSONObject jsonObject = Helpers.getJSONObjet(similarUrl);
            //filling the arrays
            parseJSONSimilarMovies(jsonObject);
            return jsonObject;
        }

        /**
         * iterates through the json object and downloads the images and sends the bitmaps to
         * the arraylist and the ids to the images to a diffrent array list.
         * @param jsonObject the jsonobject to iterate through
         */
        public void parseJSONSimilarMovies(JSONObject jsonObject){

            try {
                //Getting the results object
                JSONArray resArray = jsonObject.getJSONArray("results");
                //iterating through the results
                for (int i = 0; i < resArray.length(); i++) {
                    JSONObject jsonObject1 = resArray.getJSONObject(i);
                    if(jsonObject1!=null) {
                        //getting the bitmaps and adding to arraylist
                        Bitmap bitmap = Helpers.getBitmapFromURL("http://image.tmdb.org/t/p/w185/" + jsonObject1.getString("poster_path"));
                        relatedMoviePosters.add(bitmap);

                        //getting the ids and adding to arraylist
                        Integer movieId = jsonObject1.getInt("id");
                        relatedIds.add(movieId);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error occurred during JSON Parsing", e);
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
                //setting the image adapter
                ViewPager view = myView.findViewById(R.id.related);
                ImageAdapter imageAdapter = new ImageAdapter(itself ,relatedMoviePosters, relatedIds, "movie");
                view.setAdapter(imageAdapter);
        }
    }


}
