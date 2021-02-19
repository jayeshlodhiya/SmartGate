package com.payphi.visitorsregister.FirebaseNotification;

/**
 * Created by swapnil.g on 8/17/2017.
 */

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.payphi.visitorsregister.Calling;
import com.payphi.visitorsregister.FakeRingerActivity;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.RejectVisitorNotification;
import com.payphi.visitorsregister.Splashscreen;
import com.payphi.visitorsregister.model.Visitor;
import com.payphi.visitorsregister.utils.TTS;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.payphi.visitorsregister.FirebaseNotification.Config.NOTIFICATION_ID;


/**
 * Created by Ravi on 31/03/15.
 */
public class NotificationUtils extends NotificationListenerService {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;
    Ringtone ringtone;
    Intent notifyIntent;
    PendingIntent notifyPendingIntent;
    ListenerRegistration registration;
    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent, String packageName, Context applicationContext) {
        showNotificationMessage(title, message, timeStamp, intent, null, packageName, applicationContext);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl, String packagName, Context context) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;


        // notification icon
        final int icon = R.mipmap.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, packagName);
                    //showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound,packagName);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, packagName);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, packagName);
            playNotificationSound();
        }
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound, String packageName) {

       /* NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        mBuilder.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)

                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)

                 .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)

                .build();


        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews notificationView = new RemoteViews(packageName, R.layout.mynotification);

        Intent switchIntent = new Intent(mContext, Calling.class);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(mContext, 0,
                switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.callId,
                pendingSwitchIntent);
        Intent notificationIntent = new Intent(mContext, Calling.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, 0);
        notification.contentView = notificationView;
        notification.contentIntent = pendingNotificationIntent;
         notification.flags |= Notification.FLAG_NO_CLEAR;



        notificationManager.notify(Config.NOTIFICATION_ID, notification);
*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ringtone.stop();

    }
    public void showSmallNotification(String packageName, String message, Bitmap img, String location, String notificationsound,String type,String name,String docId,String code) {
      //  TTS.init(getApplicationContext());
        //MonitorInput(docId,code);
        RemoteViews expandedView = null;
        String sound;
        sound = "android.resource://" + mContext.getPackageName() + "/raw/bell_ring";
        if(type.equals("Chat")){

            expandedView = new RemoteViews(packageName, R.layout.notify_chat);
            expandedView.setTextViewText(R.id.notification_message,  message);
            expandedView.setTextViewText(R.id.nameId,name);
            expandedView.setTextViewText(R.id.content_text,  message);


        }else{
           // ringtone.play();
            expandedView = new RemoteViews(packageName, R.layout.view_expanded_notification);
            expandedView.setTextViewText(R.id.notification_message, getTimeGreeting() + message);
        //    TTS.speak(getTimeGreeting() + " " + message);
            expandedView.setTextViewText(R.id.content_text, getTimeGreeting() + message);

        }




        expandedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(mContext, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));

               //expandedView.setBitmap(R.id.notification_img,"test",img);

        //


        expandedView.setImageViewBitmap(R.id.notification_img, img);
        // adding action to left button
        Intent leftIntent = new Intent(mContext, Calling.class);
        leftIntent.putExtra("docId",docId);
        leftIntent.setAction("left");
        expandedView.setOnClickPendingIntent(R.id.left_button, PendingIntent.getService(mContext, 0, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        // adding action to right button
        Intent rightIntent = new Intent(mContext, Calling.class);
        rightIntent.setAction("right");
        rightIntent.putExtra("docId",docId);

        expandedView.setOnClickPendingIntent(R.id.right_button, PendingIntent.getService(mContext, 1, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent reject = new Intent(mContext, RejectVisitorNotification.class);
        reject.setAction("reject");
        reject.putExtra("docId",docId);

        expandedView.setOnClickPendingIntent(R.id.reject_button, PendingIntent.getService(mContext, 1, reject, PendingIntent.FLAG_UPDATE_CURRENT));



        if(location!=null){
            String destlocaarray[]=location.split(",");
           /* Intent mapnotifyIntent = new Intent(mContext, UserLocation.class);
            mapnotifyIntent.putExtra("location",location);
// Set the Activity to start in a new, empty task
            mapnotifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Create the PendingIntent
             notifyPendingIntent = PendingIntent.getActivity(
                    mContext, 0, mapnotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );*/

            Uri gmmIntentUri = Uri.parse("google.navigation:q="+destlocaarray[0]+","+destlocaarray[1]);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Create the PendingIntent
            notifyPendingIntent = PendingIntent.getActivity(
                    mContext, 0, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );

             System.out.println("location in notification="+location);

        }else{
              notifyIntent = new Intent(mContext, Splashscreen.class);
// Set the Activity to start in a new, empty task
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Create the PendingIntent
             notifyPendingIntent = PendingIntent.getActivity(
                    mContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );

        }


        RemoteViews collapsedView = new RemoteViews(packageName, R.layout.view_collapsed_notification);
        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(mContext, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        collapsedView.setTextViewText(R.id.content_textId, getTimeGreeting() + message);

        Intent rightIntent1 = new Intent(mContext, Calling.class);
        rightIntent1.setAction("right");
        rightIntent1.putExtra("docId",docId);

        collapsedView.setOnClickPendingIntent(R.id.a_left_button, PendingIntent.getService(mContext, 1, rightIntent1, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent reject1 = new Intent(mContext, RejectVisitorNotification.class);
        reject1.setAction("reject");
        reject1.putExtra("docId",docId);

        collapsedView.setOnClickPendingIntent(R.id.r_reject_button, PendingIntent.getService(mContext, 1, reject1, PendingIntent.FLAG_UPDATE_CURRENT));



        Uri sounduri = Uri.parse(notificationsound);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)

                // these are the three things a NotificationCompat.Builder object requires at a minimum
                .setSmallIcon(R.drawable.socityicon)
                .setContentTitle("Alert")
                .setContentText(getTimeGreeting() + message)
                // notification will be dismissed when tapped
                .setAutoCancel(true)
                // tapping notification will open MainActivity
                .setContentIntent(PendingIntent.getActivity(mContext, 0, new Intent(mContext, Calling.class), 0))
                // setting the custom collapsed and expanded views
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                // setting style to DecoratedCustomViewStyle() is necessary for custom views to display
                .setSound(sounduri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                .setStyle(new android.support.v7.app.NotificationCompat.DecoratedCustomViewStyle());

        builder.setContentIntent(notifyPendingIntent);

        // retrieves android.app.NotificationManager
        NotificationManager notificationManager = (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());



    }

    private void MonitorInput(String docId, String code) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(code).document("Visitors").collection("SVisitors").document(docId);



        registration =   docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

              String stat =  documentSnapshot.getString("VisitorApprove");
                if(!stat.equalsIgnoreCase("waiting")){
                    clearNotification();
                    CloseIntent();
                    registration.remove();
                }
               // CreteateWaitingPopUp(visitor);
            }
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {


        });


    }

    private void CloseIntent() {
        ArrayList<String> runningactivities = new ArrayList<String>();

        ActivityManager activityManager = (ActivityManager)getBaseContext().getSystemService (Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i1 = 0; i1 < services.size(); i1++) {
            runningactivities.add(0,services.get(i1).topActivity.toString());
        }

        if(runningactivities.contains("ComponentInfo{com.payphi.visitorsregister/com.payphi.visitorsregister.FakeRingerActivity}")==true){
//            Toast.makeText(getBaseContext(),"Activity is in foreground, active",Toast.LENGTH_LONG).show();
            FakeRingerActivity.getInstance().finishActivity();
            //alert.show()
        }
    }

    public void clearNotification() {
       /* NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);*/
        //    Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //   sendBroadcast(closeIntent);



        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.cancelAll();


    }

    private String getTimeGreeting() {
        String timeofday = "";
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            timeofday = "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            timeofday = "Good Afternoon";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            timeofday = "Good Evening";
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            timeofday = " ";
        }
        return timeofday;

    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound, String packageName) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public Bitmap getBitmapFromBase64(String img) {
        Bitmap src;
        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
        src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return src;
    }
}
