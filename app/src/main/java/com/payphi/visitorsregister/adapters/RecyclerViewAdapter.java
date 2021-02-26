package com.payphi.visitorsregister.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.payphi.visitorsregister.HomeDashboard;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.VisitorTicketScannerActivity;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Visitor;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by User on 2/12/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    public final static String AUTH_KEY_FCM = "AIzaSyB28LSaN0BW-iUbQw27ek0fHb4g2yLlaAs";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    Visitor visitor = null;
    public static RecyclerViewAdapter recyclerViewAdapter;
    String svisitorpic = "data:image/gif;base64,R0lGODlh9AH0AbMAAP///8z//8zM/8zMzJnMzGbMzGaZzGaZmTOZmTNmmQBmmQAAAAAAAAAAAAAAAAAAACH/C1hNUCBEYXRhWE1QPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNS4zLWMwMTEgNjYuMTQ1NjYxLCAyMDEyLzAyLzA2LTE0OjU2OjI3ICAgICAgICAiPiA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPiA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtbG5zOnhtcE1NPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvbW0vIiB4bWxuczpzdFJlZj0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL3NUeXBlL1Jlc291cmNlUmVmIyIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgQ1M2ICgxMy4wIDIwMTIwMzA1Lm0uNDE1IDIwMTIvMDMvMDU6MjE6MDA6MDApICAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDoxRDMzQUE3OTZGNjkxMUUxODAxN0UzQ0I1MkEzRTYyQiIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDoxRDMzQUE3QTZGNjkxMUUxODAxN0UzQ0I1MkEzRTYyQiI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjFEMzNBQTc3NkY2OTExRTE4MDE3RTNDQjUyQTNFNjJCIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjFEMzNBQTc4NkY2OTExRTE4MDE3RTNDQjUyQTNFNjJCIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+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";
    //vars
    private ArrayList<String> mNames = new ArrayList<>();

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;
    private String docid;
    String vDoc="";
    int pos;
    Visitor vvisitor;
    String namearr[] = null;
    ImageView imageView;
      String flag="";
    String socityCode = "";
    Bitmap imageBitmap;
    Dialog dialog;
    Button checkinbutton;
    String imageEncoded;
    public ProgressDialog paydialog; // this = YourActivity
    DocumentReference bookingref;
    String visitornumber="";
    String flatNo="";
    String name="";
    User user;
    private static final String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;
    String docId="";
    ArrayList<Visitor> visitorList= new ArrayList();
  public   String notifMsg="";
    private void GetUserData() {
        SharedPreferences sharedPreferences;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }
    public RecyclerViewAdapter(Context context, ArrayList<String> names, ArrayList<String> imageUrls) {
        mNames = names;
        mImageUrls = imageUrls;
        mContext = context;
        recyclerViewAdapter = this;
        GetUserData();
    }
    public RecyclerViewAdapter(Context context, ArrayList<String> names, ArrayList<String> imageUrls,ArrayList<Visitor> visitors) {
        mNames = names;
        mImageUrls = imageUrls;
        mContext = context;
        recyclerViewAdapter = this;
        visitorList = visitors;
    }

    public static RecyclerViewAdapter getInstance() {
        return recyclerViewAdapter;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        Bitmap src;
        if(visitorList.size()>0){
            byte[] decodedString = Base64.decode(visitorList.get(position).getVisitorPic(), Base64.DEFAULT);
            src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(src);
            holder.name.setText(visitorList.get(position).getVistorName());
           /* if (visitorList.get(position).getVisitorPic().equals("")) {
                holder.image.setImageResource(R.drawable.customerpic);
            }*/
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Visitor visitor = visitorList.get(position);
                    ShowVisitorDetails(position);

                }
            });

            holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    GetUserData();
                    Visitor visitor;
                    if(visitorList.size()>0){
                        visitor =visitorList.get(position);
                        vvisitor = visitor;
                        if(visitor.getVistorOutTime().equalsIgnoreCase("") && user.getRole().equalsIgnoreCase("S")){

                            RegisterOut(visitor);
                        }else if(visitor.getVistorOutTime().equalsIgnoreCase("") && user.getRole().equalsIgnoreCase("R")){
                            //do nothing
                        }

                    }




                    return false;
                }
            });
        }
    }

  /*  @Override
    public void onBindViewHolder1(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Bitmap src;
        String vtype = "";
        Visitor visitor = visitorList.get(position);
        if (visitor!= null && mNames.get(position)!=null) {
            byte[] decodedString = Base64.decode(visitor.getVisitorPic(), Base64.DEFAULT);
            src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(src);
            namearr = mNames.get(position).split(":");
            holder.name.setText(visitor.getVistorName());

            if (visitor.getVisitorPic().equals("")) {
                holder.image.setImageResource(R.drawable.customerpic);
            }

            if (visitor.getVistorOutTime().equalsIgnoreCase("")) {
                flag = "I";
                vDoc = visitor.getDocId();
                holder.indicator.setVisibility(View.VISIBLE);
            } else if (!visitor.getVistorOutTime().equalsIgnoreCase("")) {
                flag = "O";
                holder.indicator.setVisibility(View.INVISIBLE);
            }else if (namearr[1].equals("Y")){
                flag = "E";
                holder.indicator.setVisibility(View.INVISIBLE);
            }
        }

       *//* Glide.with(mContext)
                .load(mImageUrls.get(position))
                .into(holder.image);

        holder.name.setText(mNames.get(position));
*//*
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Visitor visitor = visitorList.get(position);
                if (visitor.getVisitorExpected().equals("Y")) {
                    //ShowDialog(position);
                }else{
                    ShowVisitorDetails(position);
                }
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               GetUserData();
                Visitor visitor;
               if(visitorList.size()>0){
                    visitor =visitorList.get(position);
                    vvisitor = visitor;
                   if(!flag.equalsIgnoreCase("E") && visitor.getVistorOutTime().equalsIgnoreCase("") && user.getRole().equalsIgnoreCase("S")){

                       RegisterOut(visitor);
                   }
               }




                return false;
            }
        });
    }*/

    private void ShowVisitorDetails(int position) {
        Visitor visitor = visitorList.get(position);
      //  Toast.makeText(mContext,visitor.getVistorInTime(),Toast.LENGTH_LONG).show();
        OpenVerficationOptions(visitor);
    }

    private void OpenVerficationOptions(Visitor visitor) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View visitorDialogView = factory.inflate(R.layout.visitor_pass_card, null);
        final AlertDialog visitorDialog = new AlertDialog.Builder(mContext).create();
        visitorDialog.setView(visitorDialogView);
        ImageView imageView = (ImageView) visitorDialogView.findViewById(R.id.outqrid);
        TextView name = (TextView) visitorDialogView.findViewById(R.id.visitorNameId);
        TextView mobile = (TextView) visitorDialogView.findViewById(R.id.visitorMobileId);
        TextView visiorFlat = (TextView) visitorDialogView.findViewById(R.id.visitorFlatId);
        TextView status = (TextView) visitorDialogView.findViewById(R.id.statusId);
        CircleImageView pic = (CircleImageView)  visitorDialogView.findViewById(R.id.visitorImage);
        Bitmap src;
        TextView inTime = (TextView) visitorDialogView.findViewById(R.id.vintimeId);
        TextView outTime = (TextView) visitorDialogView.findViewById(R.id.vouttimeId);
        imageView.setVisibility(View.GONE);
        name.setText(visitor.getVistorName());
        mobile.setText(visitor.getVistorMobile());
        visiorFlat.setText(visitor.getFlatNo());
        status.setText(visitor.getVisitorApprove());

        byte[] decodedString = Base64.decode(visitor.getVisitorPic(), Base64.DEFAULT);
        src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        //  myViewHolder.thumbnail.setImageBitmap(src);
        pic.setImageBitmap(src);

        inTime.setText(visitor.getVistorInTime());
        outTime.setText(visitor.getVistorOutTime());

        visitorDialog.show();

    }

    private void ShowDialog(final int position) {

        paydialog = new ProgressDialog(mContext);
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);

        dialog = new Dialog(mContext);
        // dialog.requestWindowFeature(set);
        // dialog.setTitle("Select Time");
        dialog.setContentView(R.layout.check_in_evisitor);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCanceledOnTouchOutside(false);
        /*Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);*/
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        RadioGroup genderradioGroup, glassesRadioGroup, headCoverRadioGroup, facialHairRadioGroup, baldRadioGroup;

        imageView = (ImageView) dialog.findViewById(R.id.cardimageid);
        checkinbutton = (Button) dialog.findViewById(R.id.checkinvisitor);
        checkinbutton.setBackgroundResource(R.color.gray);
        checkinbutton.setEnabled(false);
        CardView card = (CardView) dialog.findViewById(R.id.scanqrcodeid);
       /* card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
                String arr[] = mNames.get(position).split(":");
                visitornumber =arr[3];
                docid = arr[2];

            }
        });
*/
      /*  checkinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // CheckValidity();

            }
        });*/
        dialog.show();
    }

    public void CheckValidity(final String data) {
        flatNo = data.split("-")[1];
        name = data.split("-")[2];
        docid = data.split("-")[3];
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        socityCode = sharedPreferences.getString("SOC_CODE", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(socityCode).document("Visitors").collection("SVisitors").document(docid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            DocumentSnapshot documentSnapshot = task.getResult();
            if(documentSnapshot!=null){
                String stat = documentSnapshot.getString("VisitorApprove");
                String in = documentSnapshot.getString("VistorInTime");
                if( in.equalsIgnoreCase("")){
                   // UpdateExpectedVisitor();
                   VerifyVisitor(data);
                }else{
                    Toast.makeText(mContext,"Used QR code!!",Toast.LENGTH_LONG).show();
                }
            }

            }
        });





    }

    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, android.Manifest.permission.CAMERA)) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                builder.setTitle("Need Camera Access Permission");
                builder.setMessage("This app needs camera access permission");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 100);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.CAMERA}, 100);
            }
        } else {
            proceedAfterPermission();

        }
    }

    public void RegisterOut(Visitor visitor){
        vvisitor = visitor;
        Intent intent = new Intent(mContext, VisitorTicketScannerActivity.class);
        intent.putExtra("ReqFrom", "out");
        intent.putExtra("id", vvisitor.getDocId());
        intent.putExtra("inTime", vvisitor.getVistorInTime());
        mContext.startActivity(intent);
    }

    public void proceedAfterPermission() {
        //WE GOT THE PERMISSIONify
        //Toast.makeText(getApplicationContext(), "We Got The Camera Acceass Permission", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(mContext, VisitorTicketScannerActivity.class);
        intent.putExtra("ReqFrom", "scanadapter");

        mContext.startActivity(intent);
        // intent.putExtra("awbno", awbNo);
        // mContext.startActivityForResult(intent, 1);
        // Toast.makeText(this.getActivity(), "  IFSC code : " + IINid, Toast.LENGTH_SHORT).show();
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void VerifyVisitor(String data) {
       // System.out.println("Visitor Docid=" + docid + "Scanned docId=" + data);
        flatNo = data.split("-")[1];
        name = data.split("-")[2];
        docid = data.split("-")[3];
        if (visitornumber.equals(data)) {
            //Toast.makeText(mContext,"Matched",Toast.LENGTH_LONG).show();
               /*Glide.with(mContext)
                       .load(R.drawable.verified)
                       .into(imageView);*/
            TakePic();


        } else {
            //Toast.makeText(mContext, "Not Matched", Toast.LENGTH_LONG).show();
        }
        TakePic();
    }

    public void TakePic() {
        Toast.makeText(mContext,"Scan is Successful, Please take Pic of visitor!",Toast.LENGTH_LONG).show();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
            ((Activity) mContext).startActivityForResult(cameraIntent, 20);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");

        if (requestCode == 20) {
            try {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
//                    imageView.setImageBitmap(imageBitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
  //                  checkinbutton.setBackgroundResource(R.color.gradEnd);
    //                checkinbutton.setEnabled(true);
                    UpdateExpectedVisitor();
                   // CheckValidity();
                }

                //encodeBitmapAndSaveToFirebase(imageBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void UpdateExpectedVisitor() {
        try {
            notifMsg = name+" has just checked in the society";
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
            socityCode = sharedPreferences.getString("SOC_CODE", null);
            final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final Date date = new Date();
            bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(docid);
            // paydialog.show();
            bookingref.update("VisitorExpected", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    bookingref.update("VistorInTime", String.valueOf(dateFormat.format(date))).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            encodeBitmapAndSaveToFirebase(imageBitmap);
                            SendNotification();
                        }
                    });


                }
            });
            recyclerViewAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void SendNotification() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(socityCode).document("UserDoc").collection("Sousers");

        docRef.whereEqualTo("FlatNumber", flatNo).addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();

                System.out.println("Documents notification size=" + size);


                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                    sendPushNotification(documentSnapshot);
                }
            }
        });


    }
    public void sendPushNotification(DocumentSnapshot snapshot){
      String deviceId = snapshot.getString("DeviceId");

        myAsyncTask myAsyncTask = (myAsyncTask) new myAsyncTask().execute(deviceId);
        HomeDashboard.getInstance().Refresh();
    }

    public void OutTimeUpdate(String rawdata, String id, String inTime) {
     String flatNo =  rawdata.split("-")[1];
     String  name = rawdata.split("-")[2];
     String docid = rawdata.split("-")[3];
     // vDoc = vvisitor.getDocId();
     if(docid.equalsIgnoreCase(id)){
         UpdateVisitorToOut(docid,name,flatNo,inTime);
     }else{
         Toast.makeText(mContext,"Please scan valid visitor QR Code!",Toast.LENGTH_LONG).show();
     }


    }

    public void UpdateVisitorToOut(String docid, String name, final String flatNo, String inTime) {
try {
    final Visitor visitorObj;
    HashMap map = new HashMap();
    notifMsg = name + " has just checked out from society";
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Date date = new Date();
    Date inDate = new Date();
    inDate = dateFormat.parse(inTime);
    long diff = date.getTime() - inDate.getTime();
    long diffSeconds = diff / 1000 % 60;
    long diffMinutes = diff / (60 * 1000) % 60;
    long diffHours = diff / (60 * 60 * 1000);
    String totalStay = String.valueOf(diffHours) + ":" + String.valueOf(diffMinutes) + ":" + String.valueOf(diffSeconds);
    map.put("VistorOutTime", String.valueOf(dateFormat.format(date)));
    map.put("Staytime", totalStay);
    System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43

    SharedPreferences sharedPreferences = mContext.getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
    socityCode = sharedPreferences.getString("SOC_CODE", null);


    DocumentReference bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(docid);
    bookingref.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            SendNotificationOut(flatNo);
        }
    });


}catch (Exception e){
    e.printStackTrace();
}
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void SendNotificationOut(String flatNo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(socityCode).document("UserDoc").collection("Sousers");

        docRef.whereEqualTo("FlatNumber", flatNo).addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();

                System.out.println("Documents notification size=" + size);


                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                    sendPushNotification(documentSnapshot);
                }
            }
        });

    }

    private class myAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... pParams) {
            String deviceId = pParams[0];
            //      System.out.println("DeviceId="+deviceId);
            //
            String authKey = AUTH_KEY_FCM; // You FCM AUTH key
            String FMCurl = API_URL_FCM;
            String sound;
            sound = "android.resource://" + mContext.getPackageName() + "/raw/jinglebellsms";
            try {
                URL url = new URL(FMCurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + authKey);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();
                json.put("to", deviceId.trim());
                JSONObject payload = new JSONObject();
                JSONObject info = new JSONObject();
                info.put("title", "Booking "); // Notification title
                info.put("sound", "default");
                info.put("type", "checkin");
                String msg = notifMsg;
                info.put("body", msg); // Notification body


              //  info.put("docId", docId);
                info.put("sound", sound);
              //  info.put("Scode", securitycode);
                //payload.put("payloaddata",info);
                json.put("data", info);
                // json.put("notification", info);


                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    public void encodeBitmapAndSaveToFirebase(Bitmap imageBitmap) {


        bookingref.update("VistorPic", imageEncoded).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                paydialog.dismiss();
              //  dialog.dismiss();


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
