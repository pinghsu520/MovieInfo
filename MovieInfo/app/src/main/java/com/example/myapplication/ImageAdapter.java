package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private int[] images = new int[] {R.drawable.endgame_poster, R.drawable.star_wars_poster, R.drawable.joker_poster, R.drawable.aladdin_poster};

    public ImageAdapter(Context context){
        this.mContext = context;
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
        ImageView image = new ImageView(mContext);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setImageResource(images[position]);
        container.addView(image, 0);
        return image;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
       container.removeView((ImageView) object);
    }

}
