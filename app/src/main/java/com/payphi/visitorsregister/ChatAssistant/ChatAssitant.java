package com.payphi.visitorsregister.ChatAssistant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.api.android.AIConfiguration;
import com.payphi.visitorsregister.api.android.AIDataService;
import com.payphi.visitorsregister.api.android.GsonFactory;
import com.payphi.visitorsregister.api.ui.AIButton;
import com.payphi.visitorsregister.model.ChatMessage;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.recyclerview.ChatAdapter;
import com.payphi.visitorsregister.utils.TTS;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAssitant extends AppCompatActivity implements AIButton.AIButtonListener {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_assitant);
        sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
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
        typingtext.setText("Online");
        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
        config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
        config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));

        aiButton.initialize(config);
        aiButton.setResultsListener(this);
        aiDataService = new AIDataService(this, config);
        TTS.init(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(this, chatMessageslist);

        recyclerView.setAdapter(chatAdapter);
     //   recyclerView.setLayoutManager(new SpeedyLinearLayoutManager(this, SpeedyLinearLayoutManager.VERTICAL, false));
        textbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ChatMessage chatMessage =  new ChatMessage();
                chatMessage.setMsgText(editText.getText().toString());
                chatMessage.setMsgUser("user");
              /*  AIRequest aiRequest = new AIRequest();
                aiRequest.setQuery(editText.getText().toString());
                try {
                    aiButton.textRequest(aiRequest);
                } catch (AIServiceException e) {
                    e.printStackTrace();
                }*/

                chatMessageslist.add(chatMessage);
                recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
                chatAdapter.notifyDataSetChanged();
                sendRequest();
                editText.setText("");

            }
        });


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
                    ImageViewAnimatedChange(ChatAssitant.this,fab_img,img);
                    flagFab=false;
                    textbutton.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)textboxlayout.getLayoutParams();
                    params.addRule(RelativeLayout.START_OF, R.id.assiaddBtn);
                    textboxlayout.setLayoutParams(params);

                }
                else if (s.toString().trim().length()==0){
                    ImageViewAnimatedChange(ChatAssitant.this,fab_img,img1);
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
        SampleTest();

    }

    private void SampleTest() {
        typingtext.setText("typing...");
        welcommsgTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }

                    SetWelcomeMessage();
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
        welcommsgTread.start();


    }
    private void SetWelcomeMessage(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                typingtext.setText("Online");
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMsgText(getTimeGreeting()+", "+user.getFullName()+", how can I help you?");
                chatMessage.setMsgUser("bot");
                chatMessageslist.add(chatMessage);
                chatAdapter.notifyDataSetChanged();
                TTS.speak(getTimeGreeting()+" "+user.getFullName()+", how can I help you?");
            }
        });

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
    private void GetUserData() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);

        CircleImageView mProfileImageView;
        mProfileImageView = (CircleImageView)findViewById(R.id.chatheaderprofileImage);
        Glide.with(this)
                .load(user.getPhoto())
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

    @Override
    public void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onResult");
                typingtext.setText("Online");
                //resultTextView.setText(gson.toJson(response));

                Log.i(TAG, "Received success response");

                // this is example how to get different parts of result object
                final Status status = response.getStatus();
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());

                final Result result = response.getResult();
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());
                if(reqType==null || reqType.equals("") ) {
                    ChatMessage userchatMessage = new ChatMessage();
                    userchatMessage.setMsgText(result.getResolvedQuery());
                    userchatMessage.setMsgUser("user");
                    chatMessageslist.add(userchatMessage);
                    recyclerView.smoothScrollToPosition(chatMessageslist.size());
                    chatAdapter.notifyDataSetChanged();

                }
                reqType="";
                Log.i(TAG, "Action: " + result.getAction());
                String speech = result.getFulfillment().getSpeech();
                Log.i(TAG, "Speech: " + speech);
                if(speech!=null && !speech.equals("")){
                    if(speech.contains(":")){
                        speecharr=speech.split(":");
                        speech = speecharr[0];
                        //CreatePassQr(speecharr[1]);
                    }
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMsgText(speech);
                    chatMessage.setMsgUser("bot");
                    if(speecharr!=null){
                        chatMessage.setData(speecharr[1]);
                    }

                    chatMessageslist.add(chatMessage);

                    TTS.speak(speech);


                    recyclerView.smoothScrollToPosition(chatMessageslist.size());
                    chatAdapter.notifyDataSetChanged();
                }else{
                    SetDefualtMessage("Sorry did not get that!!");
                    TTS.speak("Sorry did not get that!!");
                }

                //TTS.speak(speech);

                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }

                final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
            }

        });
    }

    private void CreatePassQr(String data) {
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
            paydialog.dismiss();
            CreateDailog();
            //    sucheader.setVisibility(View.VISIBLE);

            paydialog.dismiss();

        } catch (WriterException e) {
            e.printStackTrace();
        }


    }

    private void CreateDailog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.visitorpass, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(deleteDialogView);
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

    }
    private void Share() {

        String shareBody = "Please Show this QR code at Security Gate generated by " + user.getFullName() + " ,valid for 1 day only";
       /* Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image*//*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM,bmp);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        getActivity().startActivity(Intent.createChooser(sharingIntent, "Share via"));*/


        try {
            File file = new File(this.getExternalCacheDir(), "logicchip.png");
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
            this.startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                typingtext.setText("Online");
                System.out.println("Error=="+error.toString());
                SetDefualtMessage("Sorry did not get that!!");
                TTS.speak("Sorry did not get that!!");
                Log.d(TAG, "onError");
//                resultTextView.setText(error.toString());
            }
        });
    }
    public void SetDefualtMessage(String msg){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMsgText(msg);
        chatMessage.setMsgUser("bot");
        chatMessageslist.add(chatMessage);
        recyclerView.smoothScrollToPosition(chatMessageslist.size());
        chatAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                typingtext.setText("Online");
                Log.d(TAG, "onCancelled");
  //              resultTextView.setText("");
            }
        });
    }
    private void sendRequest() {
        reqType="T";
        typingtext.setText("typing...");
        final String queryString = editText.getText().toString();
        //final String eventString = eventSpinner.isEnabled() ? String.valueOf(String.valueOf(eventSpinner.getSelectedItem())) : null;
        //final String contextString = String.valueOf(contextEditText.getText());



        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];
//                String event = params[1];

                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);


                try {
                    return aiDataService.request(request);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };

        task.execute(queryString);
    }

}
