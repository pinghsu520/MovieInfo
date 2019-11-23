package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.content.ContentValues.TAG;


public class HomeFragment extends Fragment {

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
        View myView =  inflater.inflate(R.layout.fragment_home, container, false);
        ViewPager view = myView.findViewById(R.id.newMovies);
        Context context = view.getContext();
        ImageAdapter imageAdapter = new ImageAdapter(context);
        view.setAdapter(imageAdapter);
        new DownloadTask().execute();

        return myView;
    }



    public static void parseJSON(JSONObject jsonObject){

        try {

            System.out.println("ASDFASDASDFSADF");
            System.out.println(jsonObject);
            JSONArray resArray = jsonObject.getJSONArray("results"); //Getting the results object
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonObject1 = resArray.getJSONObject(i);
                System.out.println(jsonObject1.get("title"));
//                    Movie movie = new Movie(); //New Movie object
//                movie.setId(jsonObject.getInt("id"));
//                movie.setVoteAverage(jsonObject.getInt("vote_average"));
//                movie.setVoteCount(jsonObject.getInt("vote_count"));
//                movie.setOriginalTitle(jsonObject.getString("original_title"));
//                movie.setTitle(jsonObject.getString("title"));
//                movie.setPopularity(jsonObject.getDouble("popularity"));
//                movie.setBackdropPath(jsonObject.getString("backdrop_path"));
//                movie.setOverview(jsonObject.getString("overview"));
//                movie.setReleaseDate(jsonObject.getString("release_date"));
//                movie.setPosterPath(jsonObject.getString("poster_path"));
                //Adding a new movie object into ArrayList
//                    list.add(movie);


            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Erro occurred during JSON Parsing", e);
        }

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
//                System.out.println("HIHIIHIHIHI");
//
//                String json = "";
//                String line;
//
//                URL url = new URL(popularMoviesURL);
//
//                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//                while ((line = in.readLine()) != null) {
//                    System.out.println("JSON LINE " + line);
//                    json += line;
//                }
//
//                System.out.println(json);
//                in.close();
//
//                JSONObject jsonObject = new JSONObject(json);
//                return jsonObject;

                System.out.println(popularMoviesURL+" THIS IS TI!!!!!!!!!");
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                jsonObject = new JSONObject(stringBuffer.toString());
//                return jsonObject;


            }
            catch(Exception ex)
            {
                Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println ("HIHHIIH");
            System.out.println(jsonObject);
            parseJSON(jsonObject);




            return null;
        }


        // The purpose of this function is to call and execute that grabs the information needed
        // from the JSON object. I parsed through the object to grab the meta data.
        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            //
//                System.out.println("ASDFASDASDFSADF");
//                System.out.println(jsonObject);
//                JSONArray resArray = jsonObject.getJSONArray("results"); //Getting the results object
//                for (int i = 0; i < resArray.length(); i++) {
//                    JSONObject jsonObject1 = resArray.getJSONObject(i);
//                    System.out.println(jsonObject1.get("title"));
//                    Movie movie = new Movie(); //New Movie object
//                movie.setId(jsonObject.getInt("id"));
//                movie.setVoteAverage(jsonObject.getInt("vote_average"));
//                movie.setVoteCount(jsonObject.getInt("vote_count"));
//                movie.setOriginalTitle(jsonObject.getString("original_title"));
//                movie.setTitle(jsonObject.getString("title"));
//                movie.setPopularity(jsonObject.getDouble("popularity"));
//                movie.setBackdropPath(jsonObject.getString("backdrop_path"));
//                movie.setOverview(jsonObject.getString("overview"));
//                movie.setReleaseDate(jsonObject.getString("release_date"));
//                movie.setPosterPath(jsonObject.getString("poster_path"));
            //Adding a new movie object into ArrayList
//                    list.add(movie);
            System.out.println("end");


        }

    }


}
