package com.payphi.visitorsregister.adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.payphi.visitorsregister.GenerateTicketFragment;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.RegisterActivity;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Visitor;
import com.payphi.visitorsregister.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by swapnil.g on 10/16/2017.
 */
public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> {

    private Context mContext;
    BookingAdapter bookingAdapter;
    private List<Visitor> visitorsList;
    Visitor visitor = null;
    String distance = "";
    int pos;
    Bitmap bmp;
    int progress = 0;
    MyViewHolder myViewHolder;
    User user;
    private static final String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;
    MyCountDownTimer myCountDownTimer = new MyCountDownTimer(10000, 1000);


    //
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, dist, stat, datetime, visitorName, visitorId, whomtomeet, flatnumber, visitorMobile, vintimeId, visitorNumnber, visitorout, timer,visitorStat;

        public ImageView thumbnail, overflow, statusImage, cancelicon, callImage, visitorStatus;
        public ImageView mProfileImageView, verifyIcon,outqr;
        public CircleImageView circleImageView;
        public Button bookButton, cancelbutton, callbutton;
        public CardView cardView;



        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.visitorNameId);
            //   thumbnail = (ImageView) view.findViewById(R.id.imageView);
            // callImage = (ImageView) view.findViewById(R.id.callImageId);
            visitorMobile = (TextView) view.findViewById(R.id.visitorMobileId);
            visitorId = (TextView) view.findViewById(R.id.visitorNumberId);
            flatnumber = (TextView) view.findViewById(R.id.visitorFlatId);
            whomtomeet = (TextView) view.findViewById(R.id.whomtomeetId);
            visitorNumnber = (TextView) view.findViewById(R.id.visitorNumnber);
            circleImageView = (CircleImageView) view.findViewById(R.id.visitorImage);
            visitorStatus = (ImageView) view.findViewById(R.id.visitorstatusId);
            vintimeId = (TextView) view.findViewById(R.id.vintimeId);
            callbutton = (Button) view.findViewById(R.id.visitorcallid);
            visitorout = (TextView) view.findViewById(R.id.vouttimeId);
            verifyIcon = (ImageView) view.findViewById(R.id.verifyIconListId);
            visitorStat = (TextView) view.findViewById(R.id.statusId);
            cardView = (CardView) view.findViewById(R.id.visitorview);
            outqr = (ImageView) view.findViewById(R.id.outqrid);
            GetUserData();
        }
    }

    private void GetUserData() {
        SharedPreferences sharedPreferences;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    public BookingAdapter(Context mContext, List<Visitor> cbookingsList, BookingAdapter adapter) {
        this.mContext = mContext;
        this.visitorsList = cbookingsList;
        this.bookingAdapter = adapter;
    }

    public void updateList(List<Visitor> list) {
        visitorsList = list;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visitor_pass_card, parent, false);

        return new MyViewHolder(itemView);
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        myViewHolder = holder;
        visitor = visitorsList.get(position);
        setAnimation(holder.itemView, position);

        Bitmap src;
        holder.title.setText(visitor.getVistorName());

        if (visitor.getVisitorPic() != null) {
            byte[] decodedString = Base64.decode(visitor.getVisitorPic(), Base64.DEFAULT);
            src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            //  myViewHolder.thumbnail.setImageBitmap(src);
            holder.circleImageView.setImageBitmap(src);
        } else {
            holder.circleImageView.setImageResource(R.drawable.vcard);

        }

        if(user.getRole().equalsIgnoreCase("R")){
            holder.outqr.setVisibility(View.VISIBLE);
            holder.outqr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GenerateOutQrCode(position);
                }
            });
        }else if(user.getRole().equalsIgnoreCase("S")){
                 holder.outqr.setVisibility(View.GONE);
        }

        if (visitor.getMobileVeriy() != null && visitor.getMobileVeriy().equals("V")) {
            holder.verifyIcon.setVisibility(View.VISIBLE);
        } else {
            holder.verifyIcon.setVisibility(View.INVISIBLE);
        }

        if (visitor.getVistorOutTime() != null && !visitor.getVistorOutTime().equals("")) {
            holder.visitorout.setText(visitor.getVistorOutTime());
            //myCountDownTimer.cancel();
        } else {
            holder.visitorout.setText("");
        }

        // myCountDownTimer.start();
        //holder.timer.setText(progress);
        holder.vintimeId.setText(visitor.getVistorInTime());
        holder.visitorNumnber.setText("Visitor #" + visitor.getVisitNumber());
        holder.visitorMobile.setText(visitor.getVistorMobile());
        holder.visitorStat.setText(visitor.getVisitorApprove());
//        holder.whomtomeet.setText(visitor.getWhomTomeet());
        holder.flatnumber.setText(visitor.getFlatNo());
        holder.callbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallVisitor(position);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(user.getRole().equalsIgnoreCase("R")){
                    GenerateOutQrCode(position);
                }

                return false;
            }
        });

        //bookingAdapter.notifyDataSetChanged();

    }

    private void GenerateOutQrCode(int  pos) {
        Visitor visitor = visitorsList.get(pos);
        String data="";
        String unqiueNumber = visitor.getUniqueNumber();
        String number = visitor.getVisitNumber();
        if(visitor.getEntryMethod()!=null){
                if(visitor.getEntryMethod().equalsIgnoreCase("QR")){
                   data  = visitor.getVisitNumber()+"-"+visitor.getDocId();
                }else{
                    data  = visitor.getVisitNumber()+"-"+visitor.getVistorName()+"-"+visitor.getDocId();
                }

                generateQRCode(data,unqiueNumber,number);
               // GenerateTicketFragment.getInstance().generateQRCode(data);
                //GenerateTicketFragment generateTicketFragment= new GenerateTicketFragment();
                //generateTicketFragment.generateQRCode(data);


        }

    }

    public void generateQRCode(String url, String unqiueNumber, String number) {

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 512, 512);
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
            CreateDailog(unqiueNumber,number);
            //    sucheader.setVisibility(View.VISIBLE);

     //       paydialog.dismiss();

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
    public void CreateDailog(String unqiueNumber,final String number) {
        final   String msgBody= unqiueNumber + " is your verification code from Smart Gate";
        try {
            ImageView imageView;
            ImageView msg;
            LayoutInflater factory = LayoutInflater.from(mContext);
            final View deleteDialogView = factory.inflate(R.layout.visitorpass, null);
            final AlertDialog deleteDialog = new AlertDialog.Builder(mContext).create();
            TextView textView =(TextView) deleteDialogView.findViewById(R.id.uniqueId);
            textView.setText(unqiueNumber);
            deleteDialog.setView(deleteDialogView);
            imageView = (ImageView) deleteDialogView.findViewById(R.id.timgId);
            imageView.setImageBitmap(bmp);
            msg = (ImageView) deleteDialogView.findViewById(R.id.msgCodeId);
            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.sendSMS(mContext, number, msgBody);
                }
            });
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



    private void CallVisitor(int vistorMobilepos) {
        Visitor visitor = visitorsList.get(vistorMobilepos);
        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + visitor.getVistorMobile()));
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mContext.startActivity(intent);
    }




    private void OpenVisitorInfo(int pos) {
       /* Bookings bookings= bookingsList.get(pos);
        Toast.makeText(mContext,"Shop=="+bookings.getShopName(), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(mContext, Shop_Profile.class);
        //System.out.println("Email="+bookings.getShopAddress()Email());
        i.putExtra("ShopName",bookings.getShopAddress());
        i.putExtra("ShopObj", (Serializable) bookings);
        mContext.startActivity(i);*/
    }


    @Override
    public int getItemCount() {
        return visitorsList.size();
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            progress = (int) (millisUntilFinished / 1000);


        }

        @Override
        public void onFinish() {

        }
    }

}

