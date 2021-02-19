package com.payphi.visitorsregister.adapters;

/**
 * Created by swapnil.g on 6/21/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.payphi.visitorsregister.R;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    // list of images
    public int[] lst_images = {

            R.drawable.seamlessguestentry,
            R.drawable.easyattendance,
            R.drawable.maidalerts,
            R.drawable.reportsimage
    };
    // list of titles
    public String[] lst_title = {

            "    ",
            "    ",
            "    ",
            "    "
    };
    // list of descriptions
    public String[] lst_description = {

            "Now make seamless visitor entry digitally with more security.. ",
            "Easy Attendence Authentication..",
            "Maid/Cab/Delivery Alerts",
            "Track visitor and download daily report.."

    };
    // list of background colors
    public int[] lst_backgroundcolor = {

            Color.rgb(239, 85, 85),
            Color.rgb(110, 49, 89),
            Color.rgb(1, 188, 212),
            Color.rgb(255, 160, 122)
    };


    public SlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return lst_title.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.introslide, container, false);
        RelativeLayout layoutslide = (RelativeLayout) view.findViewById(R.id.slidelinearlayout);
        ImageView imgslide = (ImageView) view.findViewById(R.id.slideimg);
        TextView txttitle = (TextView) view.findViewById(R.id.txttitle);
        TextView description = (TextView) view.findViewById(R.id.txtdescription);
        layoutslide.setBackgroundColor(lst_backgroundcolor[position]);
        imgslide.setImageResource(lst_images[position]);
        txttitle.setText(lst_title[position]);
        description.setText(lst_description[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}