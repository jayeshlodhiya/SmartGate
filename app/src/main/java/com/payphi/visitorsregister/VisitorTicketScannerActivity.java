package com.payphi.visitorsregister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.google.zxing.Result;
import com.payphi.visitorsregister.adapters.RecyclerViewAdapter;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.payphi.visitorsregister.adapters.RecyclerViewAdapter.recyclerViewAdapter;

public class VisitorTicketScannerActivity extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    boolean status = false;
    ImageButton flashimg;
    String reqfrom = "";
    EditText number;
    Button button;
    Bitmap imageBitmap;
    String imageEncoded;
    String docId="";
    String notifMsg="";
    String name ="";
    String flatNo="";
    DocumentReference bookingref;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_visitor_ticket_scanner);
        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        flashimg = (ImageButton) findViewById(R.id.flashId);
       /* uidno = (TextView)findViewById(R.id.textView5);*/
        scannerView = new ZXingScannerView(this);
        //scannerView.getBackground().s;
        number = (EditText) findViewById(R.id.codeId);
        button = (Button) findViewById(R.id.validatebutton);
        contentFrame.addView(scannerView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();
            }
        });

        if (getIntent().getSerializableExtra("ReqFrom") != null) {
            reqfrom = getIntent().getSerializableExtra("ReqFrom").toString();
        }


    }

    private void Validate() {
            String val = number.getText().toString();
            if(val.equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(),"Please enter the Code!",Toast.LENGTH_LONG).show();
            }else{
                CheckInVisitor(val);
            }
    }

    private void CheckInVisitor(String val) {
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
       String  socityCode = sharedPreferences.getString("SOC_CODE", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(socityCode).document("Visitors").collection("SVisitors");
        docRef.whereEqualTo("UniqueNumber",val).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentSnapshots  = task.getResult();
                int size = documentSnapshots.getDocuments().size();
                if(size==0){
                    Toast.makeText(getApplicationContext(),"Number not found!!",Toast.LENGTH_LONG).show();
                }
                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                    //CreateMemberList(documentSnapshot);
                    String in = documentSnapshot.getString("VistorInTime");
                    String out = documentSnapshot.getString("VistorOutTime");
                    docId = documentSnapshot.getString("DocId");
                    flatNo = documentSnapshot.getString("FlatNumber");
                    name = documentSnapshot.getString("VistorName");
                    if(in.equalsIgnoreCase("")){
                        // UpdateExpectedVisitor();
                        TakePic();
                    }else if(!in.equalsIgnoreCase("") && out.equalsIgnoreCase("")){
                        RecyclerViewAdapter.getInstance().UpdateVisitorToOut(docId,name,flatNo,in);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"Used Number !!",Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }


    public void TakePic() {
        Toast.makeText(this,"Please take Pic of visitor!",Toast.LENGTH_LONG).show();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            ((Activity) this).startActivityForResult(cameraIntent, 20);
        }
    }
    public void encodeBitmapAndSaveToFirebase() {


        bookingref.update("VistorPic", imageEncoded).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                paydialog.dismiss();
                //  dialog.dismiss();


            }
        });


    }
    public void UpdateExpectedVisitor() {
        try {

            notifMsg = name+" has just checked in the society";
            SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
            String  socityCode = "";
            socityCode = sharedPreferences.getString("SOC_CODE", null);
            final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final Date date = new Date();
            bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(docId);
            // paydialog.show();
            bookingref.update("VisitorExpected", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    bookingref.update("VistorInTime", String.valueOf(dateFormat.format(date))).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            encodeBitmapAndSaveToFirebase();
                            SendNotification();
                            finish();
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

        RecyclerViewAdapter.getInstance().notifMsg = name+" has just checked in the society";
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String  socityCode = "";
        socityCode = sharedPreferences.getString("SOC_CODE", null);
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
                   RecyclerViewAdapter.getInstance().sendPushNotification(documentSnapshot);
                }
            }
        });


    }
    @Override
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
                finish();
                //encodeBitmapAndSaveToFirebase(imageBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();

    }


    public void toggleflash(View view) {
        if (!status) {
            status = true;
            flash(true);
            flashimg.setBackgroundResource(R.drawable.flashon);
        } else {
            flash(false);
            status = false;
            flashimg.setBackgroundResource(R.drawable.flashoff);
        }
    }

    public void flash(boolean option) {
        scannerView.setFlash(option);
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        try {

            setResult(1, intent);
            final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            if (reqfrom != null && reqfrom.equals("PC")) {
                ProfieCompleteActivity.getInstace().getSocityCode(rawResult.toString());
            } else if (reqfrom != null && reqfrom.equals("scanadapter")) {
                RecyclerViewAdapter.getInstance().CheckValidity(rawResult.toString());
            }else if(reqfrom != null && reqfrom.equals("out")){
                if (getIntent().getSerializableExtra("id") != null) {
                    RecyclerViewAdapter.getInstance().OutTimeUpdate(rawResult.toString(),getIntent().getSerializableExtra("id").toString(),getIntent().getSerializableExtra("inTime").toString());
                }

            } else {
                RegisterFragment.getInstance().setData(rawResult.toString());
            }

            finish();

        } catch (Exception e) {

            e.printStackTrace();
            finish();
            //startActivity(getIntent());
        }
    }


}
