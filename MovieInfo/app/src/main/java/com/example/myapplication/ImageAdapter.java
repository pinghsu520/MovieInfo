package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private int[] images = new int[] {R.drawable.endgame_poster, R.drawable.star_wars_poster, R.drawable.joker_poster, R.drawable.aladdin_poster};
    private ArrayList<String> posters;

    public ImageAdapter(Context context, ArrayList<String> urls){
        this.mContext = context;
        posters = urls;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        System.out.println(position);
        System.out.println(posters.size());
        ImageView image = new ImageView(mContext);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Bitmap bitmap = getBitmapFromURL("http://image.tmdb.org/t/p/w185/" + posters.get(position));
        image.setImageBitmap(bitmap);
        container.addView(image, 0);
        return image;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

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
