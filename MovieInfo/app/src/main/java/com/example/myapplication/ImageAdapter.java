/*
 * @author: Mario Verdugo. Ping Hsu, Nathon Smith
 * @description: This is the imageAdapter class which allows for
 * the sliding of the images of the cast, popular movies, and related movies.
 */

package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<Bitmap> images;
    private ArrayList<Integer> ids;
    private String type;

    public ImageAdapter(Context context, ArrayList<Bitmap> images, ArrayList<Integer> ids, String type){
        this.mContext = context;
        this.ids = ids;
        this.images = images;
        this.type = type;

    }

    //gets the size of the array
    @Override
    public int getCount() {
        return images.size();
    }

    //checks if the object is from the view
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //used when the user slides the imageadapter to pull up a new image
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView image = new ImageView(mContext);
        //if we are looking at images and not cast members.
        if (type.equals("movie")){
            //setting the onclick to transfer to a mmovie fragment
            image.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view){
                    //getting the id of the movie
                    Bundle bundle = new Bundle();
                    Integer id = ids.get(position);

                    bundle.putString("id", id.toString());

                    //creating a new movie fragment
                    MovieFragment movie = new MovieFragment();
                    movie.setArguments(bundle);
                    FragmentTransaction fTransaction = MainActivity.manager.beginTransaction();
                    fTransaction.replace(R.id.main_layout, movie).addToBackStack(null);
                    fTransaction.commit();
                }
            });
        }

        //setting the image to fit
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //setting the image itself.
        image.setImageBitmap(images.get(position));
        container.addView(image, 0);
        return image;
    }

    //destroys when the user swipes away.s
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }


}
