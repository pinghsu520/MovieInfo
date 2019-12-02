package com.example.myapplication;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.myapplication.HomeFragment.popularMovieArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    Movie movie;
    ImageView image;
    TextView title;
    TextView popularity;
    TextView release;
    TextView overview;


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


        System.out.println(movie.getOverview());
        System.out.println(movie.getTitle());


//        String url_image=UTL OF THE MOVIE??
//        System.out.println(url_image);
//        Intent browserIntent=new Intent(Intent.ACTION_VIEW,  Uri.parse(url_image));
//        startActivity(browserIntent);

        return myView;
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

}
