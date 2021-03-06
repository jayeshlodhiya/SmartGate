package com.payphi.visitorsregister.adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.payphi.visitorsregister.GPSTracker;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Vendors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by swapnil.g on 3/22/2018.
 */
public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.MyViewHolder> implements View.OnLongClickListener {

    private Context mContext;
    VendorAdapter vendorAdapter;
    private List<Vendors> vendorList;
    Vendors vendors = null;
    String distance = "";
    List<Vendors> vendorsattendenceList;
    VendorAttendenceAdapter vendorAttendenceAdapter = null;
    int pos;
    int progress = 0;
    MyViewHolder myViewHolder;
    RatingBar ratingbar1;
    MyCountDownTimer myCountDownTimer = new MyCountDownTimer(10000, 1000);
    CollectionReference docRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    User user;
    String sdate;
    Vendors vendorObj = null;
    boolean flag = false;

    @Override
    public boolean onLongClick(View view) {

        return true;
    }


    //
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView title, count, dist, stat, datetime, visitorName, visitorId, whomtomeet, flatnumber, visitorMobile, vintimeId, visitorNumnber, visitorout, timer;
        public ImageView thumbnail, overflow, statusImage, cancelicon, callImage, visitorStatus;
        public ImageView mProfileImageView, verifyIcon;
        public CircleImageView circleImageView;
        public Button bookButton, cancelbutton, callbutton;
        public RelativeLayout relativeLayout;


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
            relativeLayout = (RelativeLayout) view.findViewById(R.id.vcardId);

        }

        @Override
        public boolean onLongClick(View view) {

            return true;
        }
    }


    public VendorAdapter(Context mContext, List<Vendors> cbookingsList, VendorAdapter adapter) {
        this.mContext = mContext;
        this.vendorList = cbookingsList;
        this.vendorAdapter = adapter;
    }

    public void updateList(List<Vendors> list) {
        vendorList = list;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vendor_card, parent, false);


        GetUserData();
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
        vendors = vendorList.get(position);
        setAnimation(holder.itemView, position);

        Bitmap src;
        holder.title.setText(vendors.getName());

        if (vendors.getPic() != null) {
            byte[] decodedString = Base64.decode(vendors.getPic(), Base64.DEFAULT);
            src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            //  myViewHolder.thumbnail.setImageBitmap(src);
            holder.circleImageView.setImageBitmap(src);
        } else {
            holder.circleImageView.setImageResource(R.drawable.vcard);

        }



       /* if (vendors.getVistorOutTime() != null && !visitor.getVistorOutTime().equals("")) {
            holder.visitorout.setText(visitor.getVistorOutTime());
            //myCountDownTimer.cancel();
        } else {
            holder.visitorout.setText("");
        }*/

        // myCountDownTimer.start();
        //holder.timer.setText(progress);
       /* holder.vintimeId.setText(visitor.getVistorInTime());*/
        //     holder.visitorNumnber.setText("Visitor #" + visitor.getVisitNumber());
        holder.visitorMobile.setText(vendors.getMobile());
//        holder.whomtomeet.setText(visitor.getWhomTomeet());
        holder.flatnumber.setText(vendors.getFlatNo());
        holder.callbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallVendor(position);
            }
        });
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                CreatePopup(position);
                return true;
            }
        });
        //bookingAdapter.notifyDataSetChanged();

    }

    private void CreatePopup(final int position) {

        CreateVendorAttendenceList(position);


        /*final Button inbutton = (Button)  optDialogView.findViewById(R.id.vinId);
        final Button outbutton = (Button)  optDialogView.findViewById(R.id.voutId);
        inbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckandInsertVisitorToIn(position);
                if(flag){
                    InsertIn(position);
                }

            }
        });

        outbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateVisitorToOut(position);

            }
        });
*/

    }

    private void CreateVendorAttendenceList(int position) {
        Vendors vendors = vendorList.get(position);
        RecyclerView recyclerView;
        LayoutInflater factory = LayoutInflater.from(mContext);
        View optDialogView = factory.inflate(R.layout.vendor_attendence_layout, null);
        AlertDialog optDialog = new AlertDialog.Builder(mContext).create();
        recyclerView = (RecyclerView) optDialogView.findViewById(R.id.vendor_attend_recycler_view);
        optDialog.setView(optDialogView);
        Paint paint = new Paint();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef;
        SharedPreferences sharedPreferences;
        vendorsattendenceList = new ArrayList<>();
        vendorAttendenceAdapter = new VendorAttendenceAdapter(mContext, vendorsattendenceList, vendorAttendenceAdapter);
        // swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.cswiperefresh);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        // recyclerView.addItemDecoration(new VisitorsListFragment.GridSpacingItemDecoration(4, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        vendorAttendenceAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(vendorAttendenceAdapter);
        sharedPreferences = mContext.getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String socityCode = sharedPreferences.getString("SOC_CODE", null);
        docRef = db.collection(socityCode).document("Vendors").collection("VendorsAttendence");

        docRef.whereEqualTo("Vid", vendors.getDocId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();

                System.out.println("Documents size====" + size);
                //System.out.println("bookingsList size="+visitorsList.size());
                if (vendorsattendenceList.size() > 0) {

                    vendorsattendenceList.removeAll(vendorsattendenceList);

                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                        adapter.notifyDataSetChanged();
*/


                }
                if (size == 0) {
                    vendorsattendenceList.removeAll(vendorsattendenceList);

                }


                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //  System.out.println("Documents data==="+documentSnapshot.getData().toString());
                    CreateVAttendenceList(documentSnapshot);
                }
            }
        });
//

        optDialog.show();
    }

    private void CreateVAttendenceList(DocumentSnapshot documentSnapshot) {
        Vendors vendors = new Vendors();
        vendors.setName(documentSnapshot.getString("Name"));
        //vendors.setPic(documentSnapshot.getString("Date"));
        vendors.setVin(documentSnapshot.getString("In"));
        vendors.setVout(documentSnapshot.getString("Out"));
        vendors.setDocId(documentSnapshot.getString("DocId"));

        vendorsattendenceList.add(vendors);
        vendorAttendenceAdapter.notifyDataSetChanged();
    }

    private void CheckandInsertVisitorToIn(final int position) {
        sharedPreferences = mContext.getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String socityCode = sharedPreferences.getString("SOC_CODE", null);
        docRef = db.collection(socityCode).document("Vendors").collection("VendorsAttendence");

        System.out.println("Pos==" + position);
        vendorObj = vendorList.get(position);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        //Toast.makeText(mContext,"IN",Toast.LENGTH_LONG).show();
        sdate = dateFormat.format(date);
        System.out.println("sdate=" + sdate);
        if ((user.getRole().equals("S"))) {
            docRef.whereEqualTo("Date", sdate).whereEqualTo("Vid", vendorObj.getDocId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    System.out.println("attendence size==" + size);

                    if (size != 0) {
                        flag = false;
                    } else if (size == 0) {
                        flag = true;
                    }


                }
            });

        }


        notifyDataSetChanged();
    }

    private void InsertIn(int position) {
        sharedPreferences = mContext.getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String socityCode = sharedPreferences.getString("SOC_CODE", null);
        DocumentReference attendref = FirebaseFirestore.getInstance().collection(socityCode).document("Vendors").collection("VendorsAttendence").document();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        Map data = new HashMap<>();
        GPSTracker gps;
        gps = new GPSTracker(mContext);
        String currentLoction = "";
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            currentLoction = String.valueOf(latitude + "," + longitude);
        }

        data.put("Date", sdate);
        data.put("In", dateFormat.format(date));
        data.put("Out", "");
        data.put("Vid", vendorObj.getDocId());
        data.put("DocId", docRef.getId());
        data.put("Location", currentLoction);

        attendref.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(mContext, "Registration done!!", Toast.LENGTH_LONG).show();


                //LocalNotification();
                // SendNotificationToShop();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error Occured.!", Toast.LENGTH_LONG).show();


            }
        });
    }

    private void GetUserData() {
        sharedPreferences = mContext.getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    private void CallVendor(int vistorMobilepos) {
        Vendors vendors = vendorList.get(vistorMobilepos);
        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + vendors.getMobile()));
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

    private void UpdateVisitorToOut(int position) {
        sharedPreferences = mContext.getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String socityCode = sharedPreferences.getString("SOC_CODE", null);
        Vendors vendorObj = null;
        System.out.println("Pos==" + position);
        vendorObj = vendorList.get(position);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        //  Toast.makeText(mContext,"Out",Toast.LENGTH_LONG).show();
        /*if((vendorObj.getVistorOutTime()==null || visitorObj.getVistorOutTime().equals("")) && (user.getRole().equals("S")) ) {

            DocumentReference bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(visitorObj.getDocId());
            bookingref.update("VistorOutTime", String.valueOf(dateFormat.format(date))).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("DeviceId Updated");
                }
            });

        }*/

        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Vendors").collection("VendorsAttendence").document(vendorObj.getDocId());
        bookingref.update("Out", String.valueOf(dateFormat.format(date))).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("DeviceId Updated");
            }
        });
        //vendorAdapter.notifyDataSetChanged();
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
        return vendorList.size();
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
