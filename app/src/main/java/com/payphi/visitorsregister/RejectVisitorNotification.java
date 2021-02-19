package com.payphi.visitorsregister;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.payphi.visitorsregister.model.User;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.payphi.visitorsregister.FirebaseNotification.Config.NOTIFICATION_ID;

public class RejectVisitorNotification extends IntentService {
    //author jayesh lodhiya  nilkesh...
    String snumber;
    String docId;
    DocumentReference bookingref;
    String stat;
    User user;
    public RejectVisitorNotification() {
        super("RejectVisitorNotification");
    }
    public final static String AUTH_KEY_FCM = "AIzaSyB28LSaN0BW-iUbQw27ek0fHb4g2yLlaAs";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        //Toast.makeText(this,"In service",Toast.LENGTH_LONG).show();
        //return super.onStartCommand(intent, flags, startId);
        try {
            if (intent!=null && intent.getSerializableExtra("docId") != null) {
                docId = intent.getSerializableExtra("docId").toString();
                GetUserData();
            }
            Reject();
        }catch (Exception  e){
            e.printStackTrace();
        }
        return START_STICKY;
    }
    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.cancelAll();
    }
    private void GetUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }
    private void CallSecurity() {
        snumber = "73503488660";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
       /* Toast.makeText(
                MainActivity.this, "999999999999999999     video.....", Toast.LENGTH_SHORT
        ).show();*/

        String name = getContactName(snumber, RejectVisitorNotification.this);
        intent.setDataAndType(Uri.parse(
                "content://com.android.contacts/data/" +
                        getContactIdForWhatsAppVideoCall(name, RejectVisitorNotification.this)
        ), "vnd.android.cursor.item/vnd.com.whatsapp.video.call");

        intent.setPackage("com.whatsapp");
        startActivity(intent);

    }

    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public int getContactIdForWhatsAppVideoCall(String name, Context context) {
        //contactNumber = Uri.encode(contactNumber);

        Cursor cursor = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data._ID},
                ContactsContract.Data.DISPLAY_NAME + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{name, "vnd.android.cursor.item/vnd.com.whatsapp.video.call"},
                ContactsContract.Contacts.DISPLAY_NAME);
        //     int phoneContactID = new Random().nextInt();
//        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.Data.CONTENT_URI,contactNumber),new String[] {ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data._ID}, null, null, null);
//        while(contactLookupCursor.moveToNext())
//        {
//            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.Data._ID));
//        }
//        contactLookupCursor.close();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
//            do
//            {
            int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data._ID));
            System.out.println("9999999999999999          name  " + name + "      id    " + phoneContactID);


            //}while (cursor.moveToNext());


            return phoneContactID;
        } else {
            System.out.println("8888888888888888888");
            return 0;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //CallSecurity();
        switch (intent.getAction()) {
            case "left":
              //  CallSecurity();
                break;
            case "right":
                //  CallSecurity();
                Reject();
                break;
            case "approve":
                //  CallSecurity();
                Reject();
                break;
        }
    }
    private void SendAknoledgement(String stat) {


        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String  socityCode = sharedPreferences.getString("SOC_CODE", null);

        DocumentReference visitorRef = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(docId);
        visitorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                //NotifyGuard(documentSnapshot.getString("SecurityDeviceId"));
                sendPushNotification(documentSnapshot);
            }
        });
    }
    private void sendPushNotification(DocumentSnapshot snapshot){
        String deviceId = snapshot.getString("SecurityDeviceId");

       myAsyncTask myAsyncTask = (myAsyncTask) new myAsyncTask().execute(deviceId);
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
            sound = "android.resource://" + getPackageName() + "/raw/jinglebellsms";
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
                String msg = user.getFullName()+ " has just "+stat+" the request";
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


    private void Reject() {
        clearNotification();
        stat = "Rejected";
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String  socityCode = sharedPreferences.getString("SOC_CODE", null);
        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final Date date = new Date();
        if(docId!=null) {
            bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(docId);
            //  paydialog.show();
            bookingref.update("VisitorApprove", stat).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    bookingref.update("VistorInTime", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Visitor " + stat, Toast.LENGTH_LONG).show();
                            SendAknoledgement(stat);
                            CloseCallingActivity();
                            // clearNotification();
                            //ClearPopup();
                            //finish();
                            ClearData();
                        }
                    });


                }
            });

        }
    }
    private void ClearData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String socityCode = sharedPreferences.getString("SOC_CODE", null);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(socityCode).document("UserDoc").collection("Sousers").document(user.getDocId()).collection("NotificationUpdateId").document(user.getEmailId());
        docRef.update("DocId","");
    }

    private void CloseCallingActivity() {
        try {


            ArrayList<String> runningactivities = new ArrayList<String>();

            ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

            for (int i1 = 0; i1 < services.size(); i1++) {
                runningactivities.add(0, services.get(i1).topActivity.toString());
            }

            if (runningactivities.contains("ComponentInfo{com.payphi.visitorsregister/com.payphi.visitorsregister.FakeRingerActivity}") == true) {
//            Toast.makeText(getBaseContext(),"Activity is in foreground, active",Toast.LENGTH_LONG).show();
                FakeRingerActivity.getInstance().finishActivity();
                //alert.show()
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void ClearPopup() {
        if(HomeDashboard.getInstance()!=null) {
        //    HomeDashboard.getInstance().clearPopup();
        }
    }

}

