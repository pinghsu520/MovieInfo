package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;


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
import java.net.URLConnection;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;



public class HomeFragment extends Fragment {
    private View myView;
    public static ArrayList<Movie> popularMovieArrayList=new ArrayList<Movie>();

    // These two arrays are for the poster urls and the poster bitmaps
    public static ArrayList<String> posterUrls = new ArrayList<String>();
    public static ArrayList<Bitmap> posters = new ArrayList<Bitmap>();
    // used to refernce the view when updating the UI with bitmaps
    static View pageView;

    public HomeFragment() {
        // Required empty public constructor
    }

    // test comment plz work 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        myView =  inflater.inflate(R.layout.fragment_home, container, false);
        ViewPager view = myView.findViewById(R.id.newMovies);
        Context context = view.getContext();
        ImageAdapter imageAdapter = new ImageAdapter(context);
        view.setAdapter(imageAdapter);

        pageView = myView;
        new DownloadTask().execute();

        //ViewPager view = myView.findViewById(R.id.newMovies);
        //Context context = view.getContext();
        //ImageAdapter imageAdapter = new ImageAdapter(context, posterUrls);
        //view.setAdapter(imageAdapter);

        return myView;
    }


    public void onSearchPressed(FragmentManager manager, SearchFragment search){
        EditText searchText = (EditText) myView.findViewById(R.id.searchText);
        String query = searchText.getText().toString();



        Bundle bundle = new Bundle();
        bundle.putString("search", query);

        search.setArguments(bundle);

        FragmentTransaction fTransaction = manager.beginTransaction();
        //adding it so that it will show
        fTransaction.replace(R.id.main_layout, search).addToBackStack(null);
        fTransaction.commit();
    }




    public static void parseJSONPopularMovies(JSONObject jsonObject){

        try {
            JSONArray resArray = jsonObject.getJSONArray("results"); //Getting the results object
            for (int i = 0; i < resArray.length()-1; i++) {
                JSONObject jsonObject1 = resArray.getJSONObject(i);
                if(jsonObject1!=null) {

                    String posterUrl = jsonObject1.getString("poster_path");
                    posterUrls.add(posterUrl);

                    Movie movie = new Movie(); //New Movie object
                    movie.setVoteAverage(jsonObject1.getInt("vote_average"));
                    movie.setOverview(jsonObject1.getString("overview"));
                    movie.setTitle(jsonObject1.getString("title"));
                    movie.setPopularity(jsonObject1.getDouble("popularity"));
                    movie.setReleaseDate(jsonObject1.getString("release_date"));
                    popularMovieArrayList.add(movie);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Erro occurred during JSON Parsing", e);
        }
        /*
        for(Movie a: popularMovieArrayList){
            System.out.println(a.getTitle());
            System.out.println(a.getOverview());
            System.out.println(a.getReleaseDate());

        }
         */
        System.out.println("poster urls");
        System.out.println(posterUrls);
    }

    //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=f3de492aa94182ea8b782ec30b1d6453

    // This private class gets the json information through the URL api.
    private class DownloadTask extends AsyncTask<Object, Void, JSONObject> {
        public String YOUR_API_KEY = "f3de492aa94182ea8b782ec30b1d6453";
        public String popularMoviesURL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + YOUR_API_KEY;

        public String topRatedMoviesURL = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=" + YOUR_API_KEY;

        public JSONObject jsonObject;

        @Override
        protected JSONObject doInBackground(Object[] objects) {

            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            URL url = null;
            try {
                url = new URL(popularMoviesURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {

                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                jsonObject = new JSONObject(stringBuffer.toString());
//                return jsonObject;


            } catch (Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            parseJSONPopularMovies(jsonObject);
            downloadMoviePosters();


            return null;
        }

        // The purpose of this function is to call and execute that grabs the information needed
        // from the JSON object. I parsed through the object to grab the meta data.
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            System.out.println("end");
            updatePosters();

        }

    }

    // This method uses the poster urls to download all poster bitmaps and then adds them to a list
    private void downloadMoviePosters(){
        for (int i = 0; i < posterUrls.size(); i++){
            Bitmap bitmap = getBitmapFromURL("http://image.tmdb.org/t/p/w185/" + posterUrls.get(i));
            posters.add(bitmap);
        }
    }

    // This method takes all poster bitmaps from the array list and updates the UI
    private void updatePosters(){
        for (int i=0; i<posters.size(); i++){
            ImageView image = new ImageView(pageView.getContext());
            Bitmap bitmap = posters.get(i);
            image.setImageBitmap(bitmap);
            LinearLayout movies = pageView.findViewById(R.id.movies);
            movies.addView(image);
        }

    }

    // Used to download poster images
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }





}
