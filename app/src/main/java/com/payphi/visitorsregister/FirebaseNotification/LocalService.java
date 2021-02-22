package com.payphi.visitorsregister.FirebaseNotification;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseApp;
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
import com.payphi.visitorsregister.FakeRingerActivity;
import com.payphi.visitorsregister.model.User;

import java.util.HashMap;

public class LocalService extends Service {
    String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;
    User user;
    SharedPreferences sharedPreferences;
    String socityCode = "";
    public LocalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        //FirebaseApp.initializeApp(this);
       /* final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
               // Toast.makeText(getApplicationContext(),"Created",Toast.LENGTH_LONG).show();
                handler.postDelayed(this, 6000);
            }
        };
        handler.post(run);*/
            GetUser();
            ListenToUserDoc();
    }
    private void GetUser() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    private void ListenToUserDoc() {


        socityCode = sharedPreferences.getString("SOC_CODE", null); //sharedPrefManager.getSocityCode();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            DocumentReference docRef = db.collection(socityCode).document("UserDoc").collection("Sousers").document(user.getDocId()).collection("NotificationUpdateId").document(user.getEmailId());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        //          Toast.makeText(getApplicationContext(),"Doc Updated=="+documentSnapshot.getString("DocId"),Toast.LENGTH_LONG).show();
                        //CheckAndCreateNotification(documentSnapshot);
                    } else {
                        //Toast.makeText(getApplicationContext(),"No Document"+documentSnapshot.getString("DocId"),Toast.LENGTH_LONG).show();
                        HashMap map = new HashMap();
                        map.put("DocId","");
                        DocumentReference notificationDocs = FirebaseFirestore.getInstance().collection(socityCode).document("UserDoc").collection("Sousers").document(user.getDocId()).collection("NotificationUpdateId").document(user.getEmailId());
                        notificationDocs.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }

                }
            });
        }catch (Exception e){
            HashMap map = new HashMap();
            map.put("DocId","");
            DocumentReference notificationDocs = FirebaseFirestore.getInstance().collection(socityCode).document("UserDoc").collection("Sousers").document(user.getDocId()).collection("NotificationUpdateId").document(user.getEmailId());
            notificationDocs.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    }

    private void CheckAndCreateNotification(DocumentSnapshot documentSnapshot) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String socityCode = "";

        socityCode = sharedPreferences.getString("SOC_CODE", null); //sharedPrefManager.getSocityCode();
if(documentSnapshot.getString("DocId")!=null && !documentSnapshot.getString("DocId").equalsIgnoreCase("")) {
    DocumentReference docRef = db.collection(socityCode).document("Visitors").collection("SVisitors").document(documentSnapshot.getString("DocId"));
    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            DocumentSnapshot documentSnapshot1 = task.getResult();
            if (documentSnapshot1.getString("VisitorApprove").equalsIgnoreCase("waiting")) {
                //CreateNotification(documentSnapshot1);
            }
        }
    });
}
    }
    public Bitmap getBitmapFromBase64(String img) {
        Bitmap src;
        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
        src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return src;
    }

    public void CreateNotification(DocumentSnapshot documentSnapshot1) {
        String sound;
        sound =  "android.resource://" + getApplicationContext().getPackageName() + "/raw/bell_ring";
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        Bitmap bitmap = getBitmapFromBase64(documentSnapshot1.getString("VistorPic"));
        String msg = user.getFullName() + ", " + documentSnapshot1.getString("VistorName") + " is at security gate for " + documentSnapshot1.getString("Purpose"); // Notification body
        notificationUtils.showSmallNotification(getPackageName(),msg,bitmap,null,sound,"Reg","",documentSnapshot1.getString("DocId"),"");

        Intent intent = new Intent(this, FakeRingerActivity.class);
        intent.putExtra("name", msg);
        //intent.putExtra("number", "Mobile " + "9370348860");
        intent.putExtra("docId", documentSnapshot1.getString("DocId"));



        //intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
//        startActivity(intent);
        //broadcastMessage(context, message);

        //  String duration = "200";
        //   intent.putExtra("contactImage", contactImage);

        //    intent.putExtra("duration", Integer.parseInt(duration));

        //    intent.putExtra("hangUpAfter", Integer.parseInt(hangUpAfter));

        //intent.putExtra("voice", voice);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        startActivity(intent);

    }
}
