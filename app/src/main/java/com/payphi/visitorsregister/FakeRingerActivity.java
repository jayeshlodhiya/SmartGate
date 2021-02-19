package com.payphi.visitorsregister;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.CallLog;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import android.telephony.PhoneNumberUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.manager.Lifecycle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.payphi.visitorsregister.adapters.RecyclerViewAdapter;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Visitor;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.payphi.visitorsregister.FirebaseNotification.Config.NOTIFICATION_ID;

public class FakeRingerActivity extends AppCompatActivity implements RecognitionListener {

    private static final int INCOMING_CALL_NOTIFICATION = 1001;
    private static final int MISSED_CALL_NOTIFICATION = 1002;
    public final static String AUTH_KEY_FCM = "AIzaSyB28LSaN0BW-iUbQw27ek0fHb4g2yLlaAs";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    private ImageButton callActionButton;
    private ImageButton answer;
    private ImageButton decline;
    private ImageButton text;
    private ImageButton endCall;
    DocumentReference bookingref;
    DocumentReference imageRef;
    DocumentReference visitorRef;
    public ProgressDialog paydialog; // this = YourActivity
    private CircleImageView contactPhoto;
    User user;
    String vstatus;
    private ImageView ring;
    TextView inputCommand;
    private SpeechRecognizer speech;
    private Intent recognizerIntent;
    private TextView callStatus;
    private TextView callDuration;
   static FakeRingerActivity fakeRingerActivity;
    private RelativeLayout main;
     Handler handler1 = new Handler();
     int delay = 2000; // 1000 milliseconds == 1 second
    private RelativeLayout callActionButtons;
   CircleImageView circleImageView;
    private AudioManager audioManager;

    private long secs;

    private int duration;

    private String number;

    private String name;

    private String voice;

    private Ringtone ringtone;

    private Vibrator vibrator;
    Thread splashTread;
    private PowerManager.WakeLock wakeLock;

    private NotificationManager notificationManager;

    private ContentResolver contentResolver;

    private MediaPlayer voicePlayer;

    private Resources resources;

    private int currentRingerMode;

    private int currentRingerVolume;

    private String contactImageString;

    private int currentMediaVolume;

     Handler handler = new Handler();

    private Runnable hangUP = new Runnable() {
        @Override
        public void run() {
          //  finish();
        }
    };

    Button approve,reject;

    String docId;
    String datetime="";
    ImageView loader;
    boolean isbackground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        fakeRingerActivity =  this;
        setContentView(R.layout.activity_fake_ringer);
        loader= (ImageView) findViewById(R.id.loaderId);
        paydialog = new ProgressDialog(getApplicationContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);

        Window window = getWindow();

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        if (getIntent().getSerializableExtra("docId") != null) {
            docId = getIntent().getSerializableExtra("docId").toString();
        }
       /* Glide.with(this)
                .load("https://i.pinimg.com/originals/d6/a4/db/d6a4db7983112c867f7ec4d71e754292.gif")
                .into(loader);*/
        //getSpeechInput();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);

        TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);

        TextView callerName = (TextView) findViewById(R.id.callerName);

        approve = (Button) findViewById(R.id.approveid);
        reject = (Button) findViewById(R.id.rejectId);
        StartTimer();
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApproveEntry();
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RejectEntry();
            }
        });

        final Animation ringExpandAnimation = AnimationUtils.loadAnimation(this, R.anim.ring_expand);

        final Animation ringShrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.ring_shrink);

        final Drawable bg2 = getDrawable(R.drawable.answered_bg);

        contactPhoto = (CircleImageView)findViewById(R.id.contactPhoto);

        contentResolver = getContentResolver();

        resources = getResources();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "Tag");

        currentRingerMode = audioManager.getRingerMode();

        currentRingerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        currentMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        callActionButtons = (RelativeLayout)findViewById(R.id.callActionButtons);

        callActionButton = (ImageButton) findViewById(R.id.callActionButton);

        answer = (ImageButton) findViewById(R.id.callActionAnswer);

        decline = (ImageButton) findViewById(R.id.callActionDecline);

        text = (ImageButton) findViewById(R.id.callActionText);

        endCall = (ImageButton) findViewById(R.id.endCall);

        callStatus = (TextView) findViewById(R.id.callStatus);

        callDuration = (TextView) findViewById(R.id.callDuration);

        main = (RelativeLayout) findViewById(R.id.main);

        ring = (ImageView) findViewById(R.id.ring);

        name = extras.getString("name");

        voice = extras.getString("voice", "");

        duration = extras.getInt("duration",50000);

        number = extras.getString("number","1234567");

        contactImageString = extras.getString("contactImage");

        int hangUpAfter = extras.getInt("hangUpAfter");

//        getSupportActionBar().hide();

        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        wakeLock.setReferenceCounted(false);

        nBuilder.setSmallIcon(R.drawable.ic_call);

        nBuilder.setOngoing(true);

        nBuilder.setContentTitle(name);

        nBuilder.setColor(Color.rgb(4, 137, 209));

        nBuilder.setContentText(resources.getString(R.string.incoming_call));

        //notificationManager.notify(INCOMING_CALL_NOTIFICATION, nBuilder.build());

        handler.postDelayed(hangUP, hangUpAfter * 1000);
        GetUserData();

        muteAll();

        setContactImage(true);

        callActionButton.setOnTouchListener(new View.OnTouchListener() {

            float x1 = 0, x2 = 0, y1 = 0, y2 = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int a = event.getAction();

                if (a == MotionEvent.ACTION_DOWN) {

                    x1 = event.getX();

                    y1 = event.getY();

                    ring.startAnimation(ringExpandAnimation);

                    answer.setVisibility(View.VISIBLE);

                    decline.setVisibility(View.VISIBLE);

                    text.setVisibility(View.VISIBLE);

                    callActionButton.setVisibility(View.INVISIBLE);

                } else if (a == MotionEvent.ACTION_MOVE) {

                    x2 = event.getX();

                    y2 = event.getY();

                    if ((x2 - 200) > x1) {

                        callActionButtons.removeView(callActionButton);

                        callActionButtons.removeView(ring);

                        callActionButtons.removeView(answer);

                        callActionButtons.removeView(decline);

                        callActionButtons.removeView(text);

                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                        handler.removeCallbacks(hangUP);

                        callStatus.setText("");

                        setContactImage(false);

                        stopRinging();

                        main.setBackground(bg2);

                        endCall.setVisibility(View.VISIBLE);

                        wakeLock.acquire();

                        playVoice();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                long min = (secs % 3600) / 60;

                                long seconds = secs % 60;

                                String dur = String.format(Locale.US, "%02d:%02d", min, seconds);

                                secs++;

                                callDuration.setText(dur);

                                handler.postDelayed(this, 1000);

                            }
                        }, 10);

                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                              //  finish();
                            }

                        }, duration * 1000);


                    } else if ((x2 + 200) < x1) {

//                        finish();

                    } else if ((y2 + 200) < y1) {

//                        finish();

                    } else if ((y2 - 200) > y1) {

  //                      finish();

                    }

                } else if (a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_CANCEL) {

                    answer.setVisibility(View.INVISIBLE);

                    decline.setVisibility(View.INVISIBLE);

                    text.setVisibility(View.INVISIBLE);

                    ring.startAnimation(ringShrinkAnimation);

                    callActionButton.setVisibility(View.VISIBLE);

                }

                return false;

            }
        });

        Animation animCallStatusPulse = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.call_status_pulse);

        callStatus.startAnimation(animCallStatusPulse);

        number = PhoneNumberUtils.formatNumber(number, "ET");

        phoneNumber.setText("");

        callerName.setText(name);

        Uri ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneURI);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //ringtone.play();

        long[] pattern = {1000, 1000, 1000, 1000, 1000};

      //  vibrator.vibrate(pattern, 0);

    }

    private void GetUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);;
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }
    private void ApproveEntry() {
       // Toast.makeText(getApplicationContext(),docId,Toast.LENGTH_LONG).show();
        UpdateVisitorStatus("Approved");
    }
    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        notificationManager.cancelAll();
    }
        public static FakeRingerActivity getInstance(){
               return fakeRingerActivity;
        }
        public void finishActivity(){

                Intent intent = new Intent(this,HomeDashboard.class);
                startActivity(intent);
                finish();
        }
    private void ClearData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String socityCode = sharedPreferences.getString("SOC_CODE", null);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(socityCode).document("UserDoc").collection("Sousers").document(user.getDocId()).collection("NotificationUpdateId").document(user.getEmailId());
        docRef.update("DocId","");
    }
    private void RejectEntry(){
        UpdateVisitorStatus("Rejected");
    }
    private void UpdateVisitorStatus(final String stat) {
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
       String  socityCode = sharedPreferences.getString("SOC_CODE", null);
        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final Date date = new Date();

        if(stat.equalsIgnoreCase("Approved")){
            datetime = String.valueOf(dateFormat.format(date));
        }

        bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(docId);
      //  paydialog.show();
        bookingref.update("VisitorApprove", stat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                bookingref.update("VistorInTime",datetime).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       Toast.makeText(getApplicationContext(),"Visitor "+stat,Toast.LENGTH_LONG).show();
                        stopVoice();
                        vibrator.cancel();
                        //ringtone.stop();
                        //finish();
                        clearNotification();
                        SendAcknowlegment(stat);
                        ClearPopup();
                        finishAndRemoveTask();
                        ClearData();

                    }
                });


            }
        });

        finish();
    }

    private void SendAcknowlegment(String stat) {
        vstatus = stat;
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String  socityCode = sharedPreferences.getString("SOC_CODE", null);

        visitorRef = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(docId);
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

    @Override
    public void onReadyForSpeech(Bundle params) {


    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        //StartTimer();
        //getSpeechInput();
    }

    @Override
    public void onError(int error) {
        //getSpeechInput();
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches) {
            text += result + "\n";
        }
    //    Toast.makeText(this,matches.get(0),Toast.LENGTH_LONG).show();
        String command = matches.get(0);
        command =command.toLowerCase();
        if(command.contains("approve") || command.contains("yes")){
            ApproveEntry();
        }else if(command.contains("reject") || command.contains("no") || command.contains("ject")){
            RejectEntry();
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

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
                String msg = user.getFullName()+ " has just "+vstatus+" the request";
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


    private void ClearPopup() {
        if(HomeDashboard.getInstance()!=null){
            HomeDashboard.getInstance().clearPopup();
        }

    }


    private void setContactImage(boolean tint) {

       /* if (!(contactImageString == null)) {

            Uri contactImageUri = Uri.parse(contactImageString);

            try {

                InputStream contactImageStream = contentResolver.openInputStream(contactImageUri);

                Drawable contactImage = Drawable.createFromStream(contactImageStream, contactImageUri.toString());

                if(tint) {
                    contactImage.setTint(getResources().getColor(R.color.contact_photo_tint));
                    contactImage.setTintMode(PorterDuff.Mode.DARKEN);
                }

                contactPhoto.setImageDrawable(contactImage);

            } catch (Exception e) {

            }


        }*/
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String  socityCode = sharedPreferences.getString("SOC_CODE", null);

        imageRef = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(docId);
        //  paydialog.show();
        imageRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                String img  = documentSnapshot.getString("VistorPic");
                Bitmap bitmap = getBitmapFromBase64(img);
                contactPhoto.setImageBitmap(bitmap);
            }
        });






    }
    public Bitmap getBitmapFromBase64(String img) {
        Bitmap src;
        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
        src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return src;
    }

    private void playVoice() {

        if (!voice.equals("")) {

            Uri voiceURI = Uri.parse(voice);

            voicePlayer = new MediaPlayer();

            try {
                voicePlayer.setDataSource(this, voiceURI);
            } catch (Exception e) {
                e.printStackTrace();
            }

            voicePlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

            voicePlayer.prepareAsync();

            voicePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

        }

    }

    private void muteAll() {

        audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);

        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

    }

    private void unMuteAll() {

        audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);

        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

    }

    public void onClickEndCall(View view) {

        stopVoice();

        finish();

    }

    private void stopVoice() {

        if (voicePlayer != null && voicePlayer.isPlaying()) {
            voicePlayer.stop();
        }

    }

    private void stopRinging() {

        vibrator.cancel();

        //ringtone.stop();

    }

    // adds a missed call to the log and shows a notification
    private void missedCall() {

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);

        nBuilder.setSmallIcon(android.R.drawable.stat_notify_missed_call);

        nBuilder.setContentTitle(name);

        nBuilder.setContentText(resources.getString(R.string.missed_call));

        nBuilder.setColor(Color.rgb(4, 137, 209));

        nBuilder.setAutoCancel(true);

        Intent showCallLog = new Intent(Intent.ACTION_VIEW);

        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, showCallLog, PendingIntent.FLAG_CANCEL_CURRENT);

        nBuilder.setContentIntent(pendingIntent);

        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);

        notificationManager.notify(MISSED_CALL_NOTIFICATION, nBuilder.build());

        //CallLogUtilities.addCallToLog(contentResolver, number, 0, CallLog.Calls.MISSED_TYPE, System.currentTimeMillis());

    }

    private void incomingCall() {

        //CallLogUtilities.addCallToLog(contentResolver, number, secs, CallLog.Calls.INCOMING_TYPE, System.currentTimeMillis());

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        stopVoice();

        notificationManager.cancel(INCOMING_CALL_NOTIFICATION);

        if (secs > 0) {

            incomingCall();

        } else {

//            incomingCall();

        }

        wakeLock.release();

        audioManager.setRingerMode(currentRingerMode);

        audioManager.setStreamVolume(AudioManager.STREAM_RING, currentRingerVolume, 0);

        stopRinging();

        unMuteAll();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentMediaVolume, 0);

    }

    @Override
    public void onBackPressed() {

    }
    public void getSpeechInput() {
try {
    speech = SpeechRecognizer.createSpeechRecognizer(this);
    speech.setRecognitionListener(this);
    recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
    //recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,this.getPackageName());
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    //startActivity(recognizerIntent);
    speech.startListening(recognizerIntent);

}catch (Exception e){

}

    }
    public void StartTimer() {

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                        if(!isbackground){
                            //
                        }else {
//                            clearNotification();
                        }

                    //               startRevealAnimation();

                    //      Intent intent = new Intent(Splashscreen.this, AuthActivity.class);
                    //    startActivity(intent);
                    //  Splashscreen.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // do nothing
                } finally {
                    //Splashscreen.this.finish();
                }

            }
        };
        splashTread.start();


    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this,"Background",Toast.LENGTH_LONG).show();
        isbackground =true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
