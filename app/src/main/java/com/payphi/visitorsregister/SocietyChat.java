package com.payphi.visitorsregister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.payphi.visitorsregister.ChatAssistant.ChatAssitant;
import com.payphi.visitorsregister.api.android.AIDataService;
import com.payphi.visitorsregister.api.android.GsonFactory;
import com.payphi.visitorsregister.api.ui.AIButton;
import com.payphi.visitorsregister.model.ChatMessage;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.recyclerview.ChatAdapter;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocietyChat extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText editText;
    RelativeLayout addBtn,textbutton,textboxlayout;
    DatabaseReference mDataBaseReference;
    ArrayList<ChatMessage> chatMessageslist=new ArrayList<>();
    ChatAdapter chatAdapter;
    // FirebaseRecyclerAdapter<ChatMessage,chat_rec> adapter;
    Boolean flagFab = true;
    public static final String TAG = ChatAssitant.class.getName();
    private static final String PREF_NAME = "sosessionPref";
    public final static String AUTH_KEY_FCM = "AIzaSyB28LSaN0BW-iUbQw27ek0fHb4g2yLlaAs";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    int PRIVATE_MODE = 0;
    SharedPreferences sharedPreferences;
    private AIButton aiButton;
    private TextView resultTextView,typingtext;
    private AIDataService aiDataService;
    private Gson gson = GsonFactory.getGson();
    String reqType;
    User user;
    Thread welcommsgTread;
    String speecharr[];
    Bitmap bmp;
    ImageView imageView;
    public ProgressDialog paydialog; // this = YourActivity
    String securitycode = "";
     String queryString="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_assitant);
        sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        securitycode = sharedPreferences.getString("SOC_CODE", null);
        GetUserData();

        paydialog = new ProgressDialog(this);
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);
        recyclerView = (RecyclerView) findViewById(R.id.assirecyclerView);
        editText = (EditText) findViewById(R.id.assieditText);
        addBtn = (RelativeLayout)findViewById(R.id.bottom_container);
        textbutton =(RelativeLayout)findViewById(R.id.assiaddBtn);
        textboxlayout = (RelativeLayout)findViewById(R.id.textboxlayoutid);
        textbutton.setVisibility(View.GONE);
        aiButton = (AIButton) findViewById(R.id.micButton);
        typingtext = (TextView)findViewById(R.id.typingtextid);
       // typingtext.setText("Online");

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(this, chatMessageslist);

        recyclerView.setAdapter(chatAdapter);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(chatMessageslist.size());
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView fab_img = (ImageView)findViewById(R.id.fab_img);
                Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.ic_send_white_24dp);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_mic_white_24dp);



                if (s.toString().trim().length()!=0 && flagFab){
                    ImageViewAnimatedChange(SocietyChat.this,fab_img,img);
                    flagFab=false;
                    textbutton.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)textboxlayout.getLayoutParams();
                    params.addRule(RelativeLayout.START_OF, R.id.assiaddBtn);
                    textboxlayout.setLayoutParams(params);

                }
                else if (s.toString().trim().length()==0){
                    ImageViewAnimatedChange(SocietyChat.this,fab_img,img1);
                    flagFab=true;
                    textbutton.setVisibility(View.GONE);
                    addBtn.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)textboxlayout.getLayoutParams();
                    params.addRule(RelativeLayout.START_OF, R.id.bottom_container);
                    textboxlayout.setLayoutParams(params);
                }


                //chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMsgText(editText.getText().toString());
                    chatMessage.setMsgUser("user");
              /*  AIRequest aiRequest = new AIRequest();
                aiRequest.setQuery(editText.getText().toString());
                try {
                    aiButton.textRequest(aiRequest);
                } catch (AIServiceException e) {
                    e.printStackTrace();
                }*/
                    sendMsg();
                    chatMessageslist.add(chatMessage);
                    recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    chatAdapter.notifyDataSetChanged();

                    editText.setText("");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        GetChatMessages();

    }

    private void GetChatMessages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(securitycode).document("Chat").collection("SChat");
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();
                if (chatMessageslist.size() > 0) {
                    chatMessageslist.removeAll(chatMessageslist);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                }
                if (size == 0) {
                    chatMessageslist.removeAll(chatMessageslist);
                    chatAdapter.notifyDataSetChanged();
                }


                System.out.println("Documents notification size=" + size);


                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                    LoadChats(documentSnapshot);
                }
            }
        });

    }

    private void LoadChats(DocumentSnapshot documentSnapshot) {
        ChatMessage userchatMessage = new ChatMessage();
        userchatMessage.setMsgText(documentSnapshot.getString("Msg"));
        userchatMessage.setDateTime(documentSnapshot.getString("Date"));
        userchatMessage.setUserName(documentSnapshot.getString("Name"));
        if(documentSnapshot.getString("Name").equals(user.getFullName())){
            userchatMessage.setMsgUser("user");
        }else {
            userchatMessage.setMsgUser("bot");
        }
        chatMessageslist.add(userchatMessage);
        Collections.sort(chatMessageslist, new Comparator<ChatMessage>() {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            public int compare(ChatMessage o1, ChatMessage o2) {
                //return o1.getDateTime().compareTo(o2.getDateTime());
                try {
                    return df.parse(o1.getDateTime()).compareTo(df.parse(o2.getDateTime()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        /*Collections.sort(datestring, new Comparator<String>() {
            DateFormat df = new SimpleDateFormat("your format");
            @Override
            public int compare(String s1, String s2) {
                try {
                    return df.parse(s1).compareTo(df.parse(s2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
*/


        recyclerView.smoothScrollToPosition(chatMessageslist.size());
        chatAdapter.notifyDataSetChanged();

    }

    private void sendMsg() {
        try {
             queryString = editText.getText().toString();
            Map<String, Object> pollMap;
            sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            Random rnd = new Random();
            DocumentReference bookingref = FirebaseFirestore.getInstance().collection(securitycode).document("Chat").collection("SChat").document();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            pollMap = new HashMap<String, Object>();

            String docId = bookingref.getId();
            pollMap.put("Name", user.getFullName());
            pollMap.put("DocId", docId);
            pollMap.put("Date", String.valueOf(dateFormat.format(date)));
            pollMap.put("Msg", queryString);


//            paydialog.show();

            bookingref.set(pollMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Toast.makeText(this, "Voting done!!", Toast.LENGTH_LONG).show();

                    // ClearData();
                    // TakeDeviceIdByFlatNoOrName();
                    //GetVisitorFeatures();
                    //getImageStringFromVisitNumber(docId);
                    // paydialog.dismiss();
                    // getActivity().finish();
                    //LocalNotification();
                     SendNotificationToAll();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(getContext(), "Error Occured.!", Toast.LENGTH_LONG).show();
                    //paydialog.dismiss();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void SendNotificationToAll() {
        myAsyncTask myAsyncTask = (myAsyncTask) new myAsyncTask().execute(securitycode);


    }
    private class myAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... pParams) {
            String deviceId = pParams[0];
            //      System.out.println("DeviceId="+deviceId);
            //
            String authKey = AUTH_KEY_FCM; // You FCM AUTH key
            String FMCurl = API_URL_FCM;
            String sound = "android.resource://" + getPackageName() + "/raw/jinglebellsms";
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
                json.put("to","/topics/"+securitycode);
                JSONObject payload = new JSONObject();
                JSONObject info = new JSONObject();
                info.put("title",queryString ); // Notification title
                info.put("sound", sound);
                info.put("body", queryString); // Notification body
                info.put("type", "Chat"); // Notification body
                info.put("name", user.getFullName()); // Notification body
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

    private void GetUserData() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);

        CircleImageView mProfileImageView;
        mProfileImageView = (CircleImageView)findViewById(R.id.chatheaderprofileImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/test-184bf.appspot.com/o/soicon.png?alt=media&token=ba2f75a8-1421-4198-b535-a4ec9f26505a")
                .into(mProfileImageView);
    }
    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }


}
