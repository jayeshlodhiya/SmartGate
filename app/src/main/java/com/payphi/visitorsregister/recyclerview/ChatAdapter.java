package com.payphi.visitorsregister.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.model.ChatMessage;
import com.payphi.visitorsregister.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

/**
 * Created by Dytstudio.
 */

public class ChatAdapter extends SelectableAdapter<ChatAdapter.ViewHolder>  {

    private List<ChatMessage> mArrayList;
    private Context mContext;
    private ViewHolder.ClickListener clickListener;
    ChatMessage chatMessage;
    Bitmap bmp;
    ViewHolder viewHolder;
    User user;
    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;

    public ChatAdapter(Context context, List<ChatMessage> arrayList) {
        this.mArrayList = arrayList;
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.clickListener = clickListener;
        GetUserData();
    }

    private void GetUserData() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    // Create new views
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.msglist, null);

        viewHolder = new ViewHolder(itemLayoutView, clickListener);

        return viewHolder;
    }
    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder1, int position) {
        this.viewHolder = viewHolder1;
        chatMessage = mArrayList.get(position);
       // setAnimation(viewHolder.itemView, position);
        if (chatMessage.getMsgUser().equals("user")) {
            viewHolder.rightText.setText(chatMessage.getMsgText());
            viewHolder.rightText.setVisibility(View.VISIBLE);
            viewHolder.leftText.setVisibility(View.GONE);
            viewHolder.cardView.setVisibility(View.GONE);
            viewHolder.shareid.setVisibility(View.GONE);
            viewHolder.userNameId.setVisibility(View.GONE);
            viewHolder.view.setVisibility(View.GONE);
        } else {
            viewHolder.userNameId.setVisibility(View.VISIBLE);
            viewHolder.view.setVisibility(View.GONE);
            // setAnimation(viewHolder.itemView, position);
            viewHolder.leftText.setText(chatMessage.getMsgText());
            viewHolder.userNameId.setText(chatMessage.getUserName());
            viewHolder.leftText.setVisibility(View.VISIBLE);
            viewHolder.rightText.setVisibility(View.GONE);
            viewHolder.cardView.setVisibility(View.GONE);
            viewHolder.shareid.setVisibility(View.GONE);
            viewHolder.cardView.setVisibility(View.GONE);
            if (chatMessage.getData() != null && !chatMessage.getData().equals("")) {
                viewHolder.cardView.setVisibility(View.VISIBLE);
                viewHolder.shareid.setVisibility(View.VISIBLE);

                CreatePassQr(chatMessage.getData());
                viewHolder.shareid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Share();
                    }
                });
            }

        }
    }

    private void Share() {

        String shareBody = "Please Show this QR code at Security Gate generated by " + user.getFullName() + " ,valid for 1 day only";
       /* Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image*//*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM,bmp);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        getActivity().startActivity(Intent.createChooser(sharingIntent, "Share via"));*/


        try {
            File file = new File(mContext.getExternalCacheDir(), "logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            mContext.startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void CreatePassQr(String data) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            viewHolder.botimge.setImageBitmap(bmp);

            //((ImageView) findViewById(R.id.qrImage)).setImageBitmap(bmp);
            //paydialog.dismiss();
            //CreateDailog();
            //    sucheader.setVisibility(View.VISIBLE);

            //paydialog.dismiss();

        } catch (WriterException e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView leftText, rightText, shareid,userNameId;
        private ClickListener listener;
        ImageView botimge;
        CardView cardView;
        View view;
        //private final View selectedOverlay;


        public ViewHolder(View itemLayoutView, ClickListener listener) {
            super(itemLayoutView);
            leftText = (TextView) itemView.findViewById(R.id.leftText);
            rightText = (TextView) itemView.findViewById(R.id.rightText);
            botimge = (ImageView) itemView.findViewById(R.id.botimageid);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            shareid = (TextView) itemView.findViewById(R.id.shareqrid);
            userNameId = (TextView) itemView.findViewById(R.id.userNameId);
            view = (View) itemView.findViewById(R.id.lineid);
            //this.listener = listener;


        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition());
            }
            return false;
        }

        public interface ClickListener {
            public void onItemClicked(int position);

            public boolean onItemLongClicked(int position);

            boolean onCreateOptionsMenu(Menu menu);
        }



    }
}
