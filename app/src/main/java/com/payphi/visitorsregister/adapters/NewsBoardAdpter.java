package com.payphi.visitorsregister.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.model.Visitor;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by swapnil.g on 5/22/2018.
 */
public class NewsBoardAdpter extends RecyclerView.Adapter<NewsBoardAdpter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    Visitor visitor = null;
    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;

    public NewsBoardAdpter(Context context, ArrayList<String> names, ArrayList<String> imageUrls) {
        mNames = names;
        mImageUrls = imageUrls;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_board_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Bitmap src;
        if (mImageUrls.get(position) != null) {
            byte[] decodedString = Base64.decode(mImageUrls.get(position), Base64.DEFAULT);
            src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(src);
            String namearr[] = mNames.get(position).split(":");
            holder.name.setText(namearr[0]);
            if (namearr[1].equals("I")) {
                holder.indicator.setVisibility(View.VISIBLE);
            } else if (namearr[1].equals("O")) {
                holder.indicator.setVisibility(View.INVISIBLE);
            }


        } else {
            holder.image.setImageResource(R.drawable.vcard);

        }


       /* Glide.with(mContext)
                .load(mImageUrls.get(position))
                .into(holder.image);

        holder.name.setText(mNames.get(position));
*/
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on an image: " + mNames.get(position));
                Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name;
        ImageView indicator;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (CircleImageView) itemView.findViewById(R.id.image_view);
            name = (TextView) itemView.findViewById(R.id.name);
            indicator = (ImageView) itemView.findViewById(R.id.inindicator);
        }
    }
}
