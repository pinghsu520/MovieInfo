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
import static com.example.myapplication.HomeFragment.popularMovieArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    public Movie movie;
    ImageView image;
    TextView title;
    TextView popularity;
    TextView release;
    TextView overview;
    private String id;

    private View myView;

    Button browser;
    private String searchUrl = "";
    String OpenBrowserUrl="";
    String videoUrl="";
    String FinalVideoURL="";




    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView =  inflater.inflate(R.layout.fragment_movie, container, false);
        Bundle bundle = getArguments();
        id = bundle.getString("id");

        OpenBrowserUrl="https://www.themoviedb.org/movie/"+id;

        searchUrl = "https://api.themoviedb.org/3/movie/" + id + "?api_key=17e7d15a4fd879e7d97ec91084cc705b&language=en-US";
        videoUrl = "https://api.themoviedb.org/3/movie/" + id + "/videos?api_key=17e7d15a4fd879e7d97ec91084cc705b&language=en-US";
        new DownloadTask().execute();

        System.out.println("I finished the task");
        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView view = (ImageView) myView.findViewById(R.id.poster);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(FinalVideoURL));
                getActivity().startActivity(i);
            }
        });


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


        Button reviewButton = (Button) myView.findViewById(R.id.review);
        reviewButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onRate(MainActivity.manager, new ReviewFragment());
            }
        });


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



    public void onShare(FragmentManager manager, ContactFragment contact){
        contact.setContainerActivity(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString("overview", this.movie.getOverview());
        bundle.putString("title", this.movie.getTitle());
        contact.setArguments(bundle);
        FragmentTransaction fTransaction = manager.beginTransaction();
        fTransaction.replace(R.id.main_layout, contact).addToBackStack(null);
        fTransaction.commit();
    }

    public void onRate(FragmentManager manager, ReviewFragment reviews){
        Bundle bundle = new Bundle();

        bundle.putString("title", this.movie.getTitle());
        bundle.putString("id", this.movie.getId());
        reviews.setArguments(bundle);
        FragmentTransaction fTransaction = manager.beginTransaction();
        fTransaction.replace(R.id.main_layout, reviews).addToBackStack(null);
        fTransaction.commit();

    }

    public void createJson(String url) throws IOException, JSONException {
        String json = "";
        //getting the proper url
        URL url1 = new URL(url);

        String line;
        //reading the json
        BufferedReader in = new BufferedReader(new InputStreamReader(url1.openStream()));
        while ((line = in.readLine()) != null) {
            json += line;
        }
        in.close();

        //creating the jsonObject
        JSONObject jsonObject1 = new JSONObject(json);

        JSONArray a=(jsonObject1.getJSONArray("results"));
        if(a.length()>2) {
            JSONObject b = (JSONObject) a.get(1);
            String videoID = (b.getString("key"));
            FinalVideoURL = "https://www.youtube.com/watch?v=" + videoID;

        }

    }

    /*
    This is the async task that actually downloads the flikr images.
    It does this by parsing JSON from the flikr API.
    */
    private class DownloadTask extends AsyncTask<Object, Void, JSONObject> {

        private ArrayList<Bitmap> relatedMoviePosters;
        private ArrayList<Integer> relatedIds;

        private ArrayList<Bitmap> castPosters;
        private ArrayList<Integer> castIds;


        @Override
        protected JSONObject doInBackground(Object[] objects) {
            movie = new Movie();
            relatedMoviePosters = new ArrayList<>();
            relatedIds = new ArrayList<>();

            castPosters = new ArrayList<>();
            castIds = new ArrayList<>();


            String similarUrl = "https://api.themoviedb.org/3/movie/" + id + "/similar?api_key=17e7d15a4fd879e7d97ec91084cc705b&language=en-US&page=1";
            String castUrl = "https://api.themoviedb.org/3/movie/" + id + "/credits?api_key=17e7d15a4fd879e7d97ec91084cc705b";

            JSONObject jsonObject = getObject(similarUrl);
            parseJSONSimilarMovies(jsonObject);

            JSONObject castObject = getObject(castUrl);
            fillCastInformation(castObject);

            try{
                createJson(videoUrl);
            } catch (Exception e){
                e.printStackTrace();
            }

            JSONObject movieObject = getObject(searchUrl);

            try{
                Bitmap bitmap = getBitmapFromURL("http://image.tmdb.org/t/p/w185/" + movieObject.getString("poster_path"));
                movie.setPoster(bitmap);
            } catch (Exception e){
                e.printStackTrace();
            }


            return movieObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            System.out.println(jsonObject);
            //myView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            //getting the approptiate information from the json
            try {
                movie.setVoteAverage(jsonObject.getInt("vote_average"));
                movie.setOverview(jsonObject.getString("overview"));
                movie.setTitle(jsonObject.getString("title"));
                movie.setPopularity(jsonObject.getDouble("popularity"));
                movie.setReleaseDate(jsonObject.getString("release_date"));

                image = myView.findViewById(R.id.poster);
                title = myView.findViewById(R.id.title);
                //popularity = myView.findViewById(R.id.popularity);
                //release = myView.findViewById(R.id.release);
                overview = myView.findViewById(R.id.overview);

                image.setImageBitmap(movie.getPoster());
                title.setText(movie.getTitle());
                //popularity.setText(Double.toString(movie.getPopularity()));
                //release.setText(movie.getReleaseDate());
                overview.setText(movie.getOverview());

                ViewPager view = myView.findViewById(R.id.related);
                Context context = myView.getContext();

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
                ImageAdapter imageAdapter = new ImageAdapter(context ,relatedMoviePosters, relatedIds, "movie");
                view.setAdapter(imageAdapter);

                ViewPager castView = myView.findViewById(R.id.cast);
                ImageAdapter castAdapter = new ImageAdapter(context, castPosters, castIds, "cast");
                castView.setAdapter(castAdapter);

            } catch (Exception e) { e.printStackTrace(); }
        }

        public void parseJSONSimilarMovies(JSONObject jsonObject){

            try {
                JSONArray resArray = jsonObject.getJSONArray("results"); //Getting the results object
                for (int i = 0; i < resArray.length(); i++) {
                    JSONObject jsonObject1 = resArray.getJSONObject(i);
                    if(jsonObject1!=null) {

                        Bitmap bitmap = getBitmapFromURL("http://image.tmdb.org/t/p/w185/" + jsonObject1.getString("poster_path"));
                        relatedMoviePosters.add(bitmap);

                        Integer movieId = jsonObject1.getInt("id");
                        relatedIds.add(movieId);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error occurred during JSON Parsing", e);
            }
        }

        public void fillCastInformation(JSONObject jsonObject){
            try{
                JSONArray cast = jsonObject.getJSONArray("cast");
                for (int i = 0; i < cast.length(); i++){
                    JSONObject castMember = cast.getJSONObject(i);
                    if (castMember.getString("profile_path") != "null" ){
                        String pictureUrl = "http://image.tmdb.org/t/p/w185/" + castMember.getString("profile_path");
                        Bitmap bitmap = getBitmapFromURL(pictureUrl);
                        castPosters.add(bitmap);
                        castIds.add(castMember.getInt("id"));
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }


        }

        public JSONObject getObject(String baseurl){
            JSONObject jsonObject = null;
            try {
                URL url = new URL(baseurl);
                String line;
                String json = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                while ((line = in.readLine()) != null) {
                    json += line;
                }
                in.close();

                jsonObject = new JSONObject(json);

            } catch (Exception e){
                e.printStackTrace();
            }

            return jsonObject;
        }


        // Used to download poster images
        public Bitmap getBitmapFromURL(String src) {
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


}
