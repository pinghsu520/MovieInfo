/*
 * @author: Mario Verdugo. Ping Hsu, Nathon Smith
 * @description: This is the searchFragment. It is a listview of all the
 * relevant searches to whatever the user searched.
 */
package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class SearchFragment extends Fragment {

    private String searchQuery;
    private String searchUrl = "https://api.themoviedb.org/3/search/multi?" +
            "api_key=17e7d15a4fd879e7d97ec91084cc705b&language=en-US&page=1&include_adult=false" +
            "&query=";

    private HashMap<String, JSONObject> movies;

    private View myView;

    private FragmentManager manager;
    private SearchFragment itself = this;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_search, container, false);

        //getting the search querey
        Bundle bundle = getArguments();
        searchQuery = bundle.getString("search");
        movies = new HashMap<>();
        this.manager = MainActivity.manager;
        new DownloadTask().execute();
        return myView;
    }




    /*
    This is the async task that actually downloads the search results .
    It does this by parsing JSON from the MovieDatabase API.
    */
    private class DownloadTask extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object[] objects) {
            //getting the jsonObject
            return Helpers.getJSONObjet(searchUrl + searchQuery);
        }

        /**
         * Helper function used to get a json field.
         * @param json the object to get from
         * @param field the field to get
         * @return the string of the object
         */
        private String getJsonString(JSONObject json, String field){
            try{
                return json.getString(field);
            } catch (Exception e){
                return "";
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            //getting the approptiate information from the json
            try {
                //getting the articles array
                JSONArray jsonArray = json.getJSONArray("results");

                //getting the listView
                ListView listView = myView.findViewById(R.id.listView);

                final ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++){
                    //creating the hashmap
                    HashMap<String, String> hm = new HashMap<>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    //String image = jsonObject.getString("poster_path");

                    String title = getJsonString(jsonObject, "title");
                    String overview = getJsonString(jsonObject, "overview");
                    if (!title.equals("") && !overview.equals("")){
                        //hm.put("image", jsonObject.getString("poster_path"));
                        hm.put("title", title);
                        hm.put("overview", overview);

                        //putting the url as a key with the jsonObject as a value
                        movies.put(title, jsonObject);
                        arrayList.add(hm);
                    }
                }

                //the ids in string form
                String[] from={"title","overview"};
                //String[] from={"poster", "title","author"};
                //the actual ids
                int[] to={R.id.title, R.id.overview};
                //int[] to={R.id.poster, R.id.title, R.id.overview};
                //creating the simple adapter
                SimpleAdapter simpleAdapter=new SimpleAdapter(myView.getContext(),arrayList,R.layout.movie_layout,from,to);

                //creating the onclick function for the listview
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        JSONObject clicked = movies.get(arrayList.get(i).get("title"));
                        Bundle bundle = new Bundle();
                        Integer movieId = 0;
                        try{
                            movieId = clicked.getInt("id");
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        bundle.putString("id", movieId.toString());
                        MovieFragment movieFrag = new MovieFragment();
                        movieFrag.setArguments(bundle);
                        //starting the new fragment
                        FragmentTransaction newTransaction =  manager.beginTransaction();
                        Helpers.transition(itself, movieFrag);
                        newTransaction.replace(R.id.main_layout, movieFrag).addToBackStack(null);
                        newTransaction.commit();
                    }
                });

                //setting it
                listView.setAdapter(simpleAdapter);

            } catch (Exception e) { e.printStackTrace(); }
        }
    }

}
