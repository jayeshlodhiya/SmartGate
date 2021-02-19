package com.payphi.visitorsregister.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.payphi.visitorsregister.R;

import java.util.ArrayList;



public class MyAdapter extends PagerAdapter {

    private ArrayList<Integer> images;
    private ArrayList<String> strimages;
    private LayoutInflater inflater;
    private Context context;

    public MyAdapter(Context context, ArrayList<Integer> images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }

    public MyAdapter(Context context, ArrayList<String> strimages, String temp) {
        this.context = context;
        this.strimages=strimages;
        inflater = LayoutInflater.from(context);
}
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if(images!=null){
            return images.size();
        }
        if(strimages!=null){
            return strimages.size();
        }
        return 0 ;
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.slideimageId);
        if(strimages!=null) {
            //myImage.setImageResource();
            Glide.with(context).load(strimages.get(position)).into(myImage);
        }






        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}