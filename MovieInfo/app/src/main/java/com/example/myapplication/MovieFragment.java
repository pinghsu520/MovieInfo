package com.example.myapplication;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.myapplication.HomeFragment.popularMovieArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    Movie movie;


    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView =  inflater.inflate(R.layout.fragment_movie, container, false);
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        movie = popularMovieArrayList.get(position);
        System.out.println(movie.getOverview());
        System.out.println(movie.getTitle());

        return myView;
    }

}
