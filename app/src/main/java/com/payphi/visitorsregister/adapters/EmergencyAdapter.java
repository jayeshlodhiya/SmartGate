package com.payphi.visitorsregister.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.payphi.visitorsregister.EmergencyActivity;
import com.payphi.visitorsregister.R;

import java.util.HashMap;

public class EmergencyAdapter extends BaseAdapter {
    String selectedOption="";
    private Context mContext;
    HashMap<Integer,ImageView> objects = new HashMap<>();
    public EmergencyAdapter(Context context){
    mContext = context;
    }
    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
   final     ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            if(objects.get(position)==null){
                objects.put(position,imageView);
            }


            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setTag(position);




        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);


      //  imageView.setAlpha(Float.parseFloat(""+4.15));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(mContext,imageView.getTag().toString(),Toast.LENGTH_LONG).show();
                selectedOption = imageView.getTag().toString();
                if(selectedOption.equalsIgnoreCase("0")){
                    imageView.setAlpha(Float.parseFloat(""+3.45));
                    ImageView imageView1 = objects.get(1);
                    imageView1.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView2 = objects.get(2);
                    imageView2.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView3 = objects.get(3);
                    imageView3.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView4 = objects.get(4);
                    imageView4.setAlpha(Float.parseFloat(""+0.45));
                    EmergencyActivity.selectedText = "Animal";

                }else if(selectedOption.equalsIgnoreCase("1")){
                    imageView.setAlpha(Float.parseFloat(""+3.45));
                    ImageView imageView0 = objects.get(0);
                    imageView0.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView2 = objects.get(2);
                    imageView2.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView3 = objects.get(3);
                    imageView3.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView4 = objects.get(4);
                    imageView4.setAlpha(Float.parseFloat(""+0.45));
                    EmergencyActivity.selectedText = "Fire";
                    return;
                }
                else if(selectedOption.equalsIgnoreCase("2")){
                    imageView.setAlpha(Float.parseFloat(""+3.45));
                    ImageView imageView1 = objects.get(0);
                    imageView1.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView2 = objects.get(1);
                    imageView2.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView3 = objects.get(3);
                    imageView3.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView4 = objects.get(4);
                    imageView4.setAlpha(Float.parseFloat(""+0.45));
                    EmergencyActivity.selectedText = "Medical";

                }
                else if(selectedOption.equalsIgnoreCase("3")){
                    imageView.setAlpha(Float.parseFloat(""+3.45));
                    ImageView imageView1 = objects.get(0);
                    imageView1.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView2 = objects.get(1);
                    imageView2.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView3 = objects.get(2);
                    imageView3.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView4 = objects.get(4);
                    imageView4.setAlpha(Float.parseFloat(""+0.45));
                    EmergencyActivity.selectedText = "Human";
                }
                else if(selectedOption.equalsIgnoreCase("4")){
                    imageView.setAlpha(Float.parseFloat(""+3.45));
                    ImageView imageView1 = objects.get(0);
                    imageView1.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView2 = objects.get(1);
                    imageView2.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView3 = objects.get(2);
                    imageView3.setAlpha(Float.parseFloat(""+0.45));
                    ImageView imageView4 = objects.get(3);
                    imageView4.setAlpha(Float.parseFloat(""+0.45));
                    EmergencyActivity.selectedText = "Lift";
                }
            }
        });
        imageView.setAlpha(Float.parseFloat(""+0.45));

        return imageView;
    }
    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.snake, R.drawable.fire,
            R.drawable.ambulance, R.drawable.thief,
            R.drawable.elevator1
    };
}
