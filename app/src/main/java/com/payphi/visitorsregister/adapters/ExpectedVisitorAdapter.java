package com.payphi.visitorsregister.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Visitor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExpectedVisitorAdapter extends RecyclerView.Adapter<ExpectedVisitorAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    Visitor visitor = null;
    User user;
    private static final String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;
    //vars
    Bitmap bmp;
    private Context mContext;
    private ArrayList<Visitor> expectedVisitorList;

    public ExpectedVisitorAdapter(Context context, ArrayList<Visitor> expectedVisitorList) {
        this.expectedVisitorList = expectedVisitorList;
        mContext = context;
    }
    private void GetUserData() {
        SharedPreferences sharedPreferences;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        GetUserData();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Bitmap src;

        if (expectedVisitorList.get(position) != null) {
            holder.image.setImageResource(R.drawable.vcard);
            holder.name.setText(expectedVisitorList.get(position).getVistorName());
            holder.indicator.setVisibility(View.GONE);
            holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(user.getRole().equalsIgnoreCase("R")){
                        GenerateOutQrCode(position);
                    }
                    return false;
                }
            });
        }


       /* Glide.with(mContext)
                .load(mImageUrls.get(position))
                .into(holder.image);

        holder.name.setText(mNames.get(position));
*/

    }

    private void GenerateOutQrCode(int  pos) {
        Visitor visitor = expectedVisitorList.get(pos);
        String data="";
        String uniqueueNumber="";
        uniqueueNumber= visitor.getUniqueNumber();
        if(visitor.getEntryMethod()!=null){
            if(visitor.getEntryMethod().equalsIgnoreCase("QR")){
                data  = visitor.getVisitNumber()+"-"+visitor.getDocId();
            }else{
                data  = visitor.getVisitNumber()+"-"+visitor.getVistorName()+"-"+visitor.getDocId();
            }

            generateQRCode(data,uniqueueNumber);
            // GenerateTicketFragment.getInstance().generateQRCode(data);
            //GenerateTicketFragment generateTicketFragment= new GenerateTicketFragment();
            //generateTicketFragment.generateQRCode(data);


        }

    }

    private void generateQRCode(String data, String uniqueueNumber) {
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
            //((ImageView) findViewById(R.id.qrImage)).setImageBitmap(bmp);
            //     paydialog.dismiss();
            CreateDailog(uniqueueNumber);
            //    sucheader.setVisibility(View.VISIBLE);

            //       paydialog.dismiss();

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    public void CreateDailog(String uniqueueNumber) {
        try {
            ImageView imageView;
            LayoutInflater factory = LayoutInflater.from(mContext);
            final View deleteDialogView = factory.inflate(R.layout.visitorpass, null);
            final AlertDialog deleteDialog = new AlertDialog.Builder(mContext).create();
            deleteDialog.setView(deleteDialogView);
            TextView textView =(TextView) deleteDialogView.findViewById(R.id.uniqueId);
            textView.setText(uniqueueNumber);
            imageView = (ImageView) deleteDialogView.findViewById(R.id.timgId);
            imageView.setImageBitmap(bmp);
            deleteDialogView.findViewById(R.id.shareId).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //your business logic
                    Share();
                }
            });


            deleteDialog.show();
        }catch (Exception e){

        }

    }
    private void Share() {

        String shareBody = "Please Show this QR code at Security Gate generated by " + user.getFullName() ;
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



    @Override
    public int getItemCount() {
        return expectedVisitorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name;
        ImageView indicator;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (CircleImageView) itemView.findViewById(R.id.image_view);
            name = (TextView) itemView.findViewById(R.id.name);
            indicator = (ImageView) itemView.findViewById(R.id.inindicator);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeId);
        }
    }
}
