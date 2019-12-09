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

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView image = new ImageView(mContext);
        if (type.equals("movie")){
            image.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view){

                    Bundle bundle = new Bundle();
                    Integer id = ids.get(position);

                    bundle.putString("id", id.toString());

                    MovieFragment movie = new MovieFragment();
                    movie.setArguments(bundle);
                    FragmentTransaction fTransaction = MainActivity.manager.beginTransaction();
                    fTransaction.replace(R.id.main_layout, movie).addToBackStack(null);
                    fTransaction.commit();


                }
            });
        }
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setImageBitmap(images.get(position));
        container.addView(image, 0);



        return image;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }


}
