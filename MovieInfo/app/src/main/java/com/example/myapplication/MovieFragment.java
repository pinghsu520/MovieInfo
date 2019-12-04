package com.example.myapplication;


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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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

import static com.example.myapplication.HomeFragment.popularMovieArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    private Movie movie;
    ImageView image;
    TextView title;
    TextView popularity;
    TextView release;
    TextView overview;

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
        String id = bundle.getString("id");

        OpenBrowserUrl="https://www.themoviedb.org/movie/"+id;
        System.out.println("THIS IS THE browser URL PING! "+OpenBrowserUrl);
        searchUrl = "https://api.themoviedb.org/3/movie/" + id + "?api_key=17e7d15a4fd879e7d97ec91084cc705b&language=en-US";
        videoUrl = "https://api.themoviedb.org/3/movie/" + id + "/videos?api_key=17e7d15a4fd879e7d97ec91084cc705b&language=en-US";
        System.out.println("HI PING!!!!!!!! "+searchUrl);
        System.out.println("HI PING!!!!!!!! "+videoUrl);

        new DownloadTask().execute();
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

    }



    public void onShare(FragmentManager manager, ContactFragment contact){
        Bundle bundle = new Bundle();
        bundle.putString("overview", movie.getOverview());
        bundle.putString("title", movie.getTitle());
        contact.setArguments(bundle);
        FragmentTransaction fTransaction = manager.beginTransaction();
        fTransaction.replace(R.id.main_layout, contact).addToBackStack(null);
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
        System.out.println(json);
        //creating the jsonObject
        JSONObject jsonObject1 = new JSONObject(json);
        System.out.println("TESTETSTSETST");
        JSONArray a=(jsonObject1.getJSONArray("results"));
        if(a.length()>2) {
            JSONObject b = (JSONObject) a.get(1);
            System.out.println(b);
            String videoID = (b.getString("key"));
            FinalVideoURL = "https://www.youtube.com/watch?v=" + videoID;
            System.out.println(FinalVideoURL);
        }

    }

    /*
This is the async task that actually downloads the flikr images.
It does this by parsing JSON from the flikr API.
*/
    private class DownloadTask extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object[] objects) {
            try {
                createJson(videoUrl);
                movie = new Movie(); //New Movie object

                String json = "";
                //getting the proper url
                URL url = new URL(searchUrl);

                String line;
                //reading the json
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                while ((line = in.readLine()) != null) {
                    json += line;
                }
                in.close();
                System.out.println(json);
                //creating the jsonObject
                JSONObject jsonObject = new JSONObject(json);

                Bitmap bitmap = getBitmapFromURL("http://image.tmdb.org/t/p/w185/" + jsonObject.getString("poster_path"));
                movie.setPoster(bitmap);
                return jsonObject;

            } catch (Exception e) { e.printStackTrace(); }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            //getting the approptiate information from the json
            try {

                movie.setVoteAverage(jsonObject.getInt("vote_average"));
                movie.setOverview(jsonObject.getString("overview"));
                movie.setTitle(jsonObject.getString("title"));
                movie.setPopularity(jsonObject.getDouble("popularity"));
                movie.setReleaseDate(jsonObject.getString("release_date"));

                image = myView.findViewById(R.id.poster);
                title = myView.findViewById(R.id.title);
                popularity = myView.findViewById(R.id.popularity);
                release = myView.findViewById(R.id.release);
                overview = myView.findViewById(R.id.overview);

                image.setImageBitmap(movie.getPoster());
                title.setText(movie.getTitle());
                popularity.setText(Double.toString(movie.getPopularity()));
                release.setText(movie.getReleaseDate());
                overview.setText(movie.getOverview());


            } catch (Exception e) { e.printStackTrace(); }
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
