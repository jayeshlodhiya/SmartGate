package com.payphi.visitorsregister;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.payphi.visitorsregister.Auth.AuthActivity;
//import com.payphi.visitorsregister.Beakon.BeaconActivity;
import com.payphi.visitorsregister.Beakon.BeaconActivity;
import com.payphi.visitorsregister.ChatAssistant.ChatAssitant;
import com.payphi.visitorsregister.FaceRecognition.AwsFaceDetection;
import com.payphi.visitorsregister.FaceRecognition.RecognizeFace;
import com.payphi.visitorsregister.FirebaseNotification.LocalService;
import com.payphi.visitorsregister.Vendor.VendorActivity;
import com.payphi.visitorsregister.adapters.ExpectedVisitorAdapter;
import com.payphi.visitorsregister.adapters.MyAdapter;
import com.payphi.visitorsregister.adapters.NewsBoardAdpter;
import com.payphi.visitorsregister.adapters.RecyclerViewAdapter;
import com.payphi.visitorsregister.invoice.InvoiceActivity;
import com.payphi.visitorsregister.model.Society;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Visitor;
import com.payphi.visitorsregister.utils.ClearData;
import com.payphi.visitorsregister.utils.DetectionActivity;
import com.payphi.visitorsregister.utils.SendMailTask;
import com.payphi.visitorsregister.utils.ShakeListener;
import com.payphi.visitorsregister.utils.SharedPrefManager;
import com.payphi.visitorsregister.utils.Utils;
import com.squareup.picasso.Picasso;
import com.white.progressview.HorizontalProgressView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.payphi.visitorsregister.FirebaseNotification.Config.NOTIFICATION_ID;

public class HomeDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private Toolbar toolbar;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    ImageView backhead;
    AlertDialog waitingvisitorDialog;
     int size;
    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    public final static String AUTH_KEY_FCM = "AIzaSyB28LSaN0BW-iUbQw27ek0fHb4g2yLlaAs";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    private ArrayList<String> mNamesexpected = new ArrayList<>();
    private ArrayList<String> mImageUrlsexpected = new ArrayList<>();//
    private  ArrayList<Visitor> visitrList = new ArrayList<>();
    private ArrayList<String> noticeText = new ArrayList<>();
    HashMap<String,String> wiatingVisitors =  new HashMap();
    Animation shake;
    public static  HomeDashboard homeDashboard;
    String deviceId = "fkXCYN5UVho:APA91bFnuoV4aD2zMaIzw5yVxESlFjDD88H-Rglwal0F9vyyvLugxojub_YIs8U_rBp8IXZTVqWsjHCD2PBB5-0wokQWIwmnB9qjzNCkWE8NS8HcWKYXnMHWjWmnkjtQu8QLNU5PsRpl";
    Date d1;
    Date d2;

    ArrayList<String> deviceIds = new ArrayList<String>();
    FirebaseFirestore db = null;
    CollectionReference docRef =null;
    //  SharedPreferences sharedPreferences ;
    String name = "";
    int count = 0;
    String sound;
    boolean shkonlyonce = false;
    TextView pendinCount, nameView, emailView,societyName;
    CardView cardView, bookinCard;
    String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;
    String socityCode = "";
    SharedPreferences sharedPreferences;
    SharedPrefManager sharedPrefManager;
    private CircleImageView mProfileImageView;
    Context mContext = this;
    private User user;
    LinearLayout visitornew;
    private ArrayList<Visitor> visitorsList = new ArrayList<>();
    private ArrayList<Visitor> expectedVisitorList =  new ArrayList<>();
    public static int docSize;
    LinearLayout linearLayout;
    RecyclerViewAdapter homeadapter ;
    ExpectedVisitorAdapter homeadapterexpected;
    NewsBoardAdpter newsBoardAdpter;
    ImageView notificationicon, sendalertimage;
    private TextView tv;
    ShakeListener mShaker;
    public static int shakecount = 0;
    String currentLoction = "";
    ImageView qrImage;
    private static ViewPager mPager;
    private  int currentPage = 0;
    private static  ArrayList<String> slideshow ;
    String alertMsg="";
    HorizontalProgressView horizontalProgressView;
    float totalVisitor=0;
    float totalIn=0,totalOut=0;
    //String svisitorpic="data:image/gif;base64,R0lGODlh9AH0AbMAAP///8z//8zM/8zMzJnMzGbMzGaZzGaZmTOZmTNmmQBmmQAAAAAAAAAAAAAAAAAAACH/C1hNUCBEYXRhWE1QPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNS4zLWMwMTEgNjYuMTQ1NjYxLCAyMDEyLzAyLzA2LTE0OjU2OjI3ICAgICAgICAiPiA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPiA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtbG5zOnhtcE1NPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvbW0vIiB4bWxuczpzdFJlZj0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL3NUeXBlL1Jlc291cmNlUmVmIyIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgQ1M2ICgxMy4wIDIwMTIwMzA1Lm0uNDE1IDIwMTIvMDMvMDU6MjE6MDA6MDApICAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDoxRDMzQUE3OTZGNjkxMUUxODAxN0UzQ0I1MkEzRTYyQiIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDoxRDMzQUE3QTZGNjkxMUUxODAxN0UzQ0I1MkEzRTYyQiI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjFEMzNBQTc3NkY2OTExRTE4MDE3RTNDQjUyQTNFNjJCIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjFEMzNBQTc4NkY2OTExRTE4MDE3RTNDQjUyQTNFNjJCIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+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";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_drawer);

        slideshow = new ArrayList<String>();
        slideshow.add("https://firebasestorage.googleapis.com/v0/b/test-184bf.appspot.com/o/reportsimage.png?alt=media&token=d829b066-0f0b-43d8-8408-1e8cbb2cd7f8");
        slideshow.add("https://firebasestorage.googleapis.com/v0/b/test-184bf.appspot.com/o/easyattendance.png?alt=media&token=6019c10d-e5b0-40cd-bd9b-3cfb97d74182");
        slideshow.add("https://firebasestorage.googleapis.com/v0/b/test-184bf.appspot.com/o/seamlessguestentry.png?alt=media&token=68a0fe3b-50eb-4f1e-ba16-bad7c2d05334");
        slideshow.add("https://firebasestorage.googleapis.com/v0/b/test-184bf.appspot.com/o/maidalerts.png?alt=media&token=0a5b1955-db17-4fa6-8ea0-a70a3d8a9056");
        homeDashboard =  this;
        CheckInternet();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        docRef =  db.collection("Bookings");
        mContext = this;
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(RECORD_AUDIO);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(CAMERA);
        permissions.add(VIBRATE);
        permissions.add(SEND_SMS);
        permissions.add(CALL_PHONE);
        permissions.add(READ_PHONE_STATE);
        permissions.add(READ_CONTACTS);
        permissions.add(STORAGE_SERVICE);

        horizontalProgressView = (HorizontalProgressView) findViewById(R.id.progress100);
        horizontalProgressView.setTextVisible(true);
      horizontalProgressView.setReachBarSize(6);
      horizontalProgressView.setProgressPosition(HorizontalProgressView.CENTRE);
      horizontalProgressView.setProgressInTime(0,2500);
// set progress from 20 to 100 with anim in 2500ms




        permissionsToRequest = findUnAskedPermissions(permissions);
        //getImages();
        //cardView =(CardView) findViewById(R.id.pendingReqcardId);
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);
//        bookinCard = (CardView) findViewById(R.id.bookingcardId);
        visitornew = (LinearLayout) findViewById(R.id.bookingcardId);
        notificationicon = (ImageView) findViewById(R.id.notificatioimageId);
        sendalertimage = (ImageView) findViewById(R.id.alertNotifyId);
        deviceId = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Shop device Id=" + deviceId);
        linearLayout = (LinearLayout) findViewById(R.id.homelayoutId);
        // pendinCount = (TextView) findViewById(R.id.pendingtextId);
        nameView = (TextView) findViewById(R.id.nameId);
        emailView = (TextView) findViewById(R.id.emailId);
        societyName = (TextView) findViewById(R.id.socnameId);
        sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        tv = (TextView) this.findViewById(R.id.badge_notification_4);
        tv.setSelected(true);  // Set focus to the textview
        qrImage = (ImageView) findViewById(R.id.scanId);
        sharedPrefManager = new SharedPrefManager(mContext);
    //    sharedPrefManager.saveSecurityCode(getApplicationContext(),"SPAS");
        socityCode = sharedPreferences.getString("SOC_CODE", null);

        name = sharedPreferences.getString("NAME", null);
      //  Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG).show();
        //  String uri = sharedPrefManager.getPhoto();
        emailView.setText(sharedPrefManager.getUserEmail());
       /* Uri mPhotoUri = Uri.parse(uri);
        Picasso.with(mContext)
                .load(mPhotoUri)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(R.drawable.customerpic)
                .into(mProfileImageView);*/
        System.out.println("Name===" + name);
//        nameView.setText(name);

        notificationicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeDashboard.this, ActivityDrawer.class);
                startActivity(intent);
            }
        });

        initNavigationDrawer();
        GetUserData();
        GetSocInfo();
        if(user!=null && user.getRole().equalsIgnoreCase("R")){
            CheckWaiting();
        }
       // CheckWaiting();
//        ShakeNotification();
        TempStoreDeviceId();
        SubscribeToTopic();
       // initilizeShake();
        GetCurrentLocation();
        init();
      //  GetCurrentLocationLT();
        //SendEMialTest();
        // Utils.SendEmail(this,"jglodhiya@gmail.com","Test from android","First emil from android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
//        TestViewPager();
//        getImages();
        //UpdateDeviceId();
        //    initilizeShake();

        sendalertimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sound = "android.resource://" + getPackageName() + "/raw/alertsound";
              //  openDialogBox();
                Intent intent =  new Intent(getApplicationContext(),EmergencyActivity.class);
                startActivity(intent);
            }
        });

        if(user!=null && user.getRole().equalsIgnoreCase("S")){

            qrImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecyclerViewAdapter.getInstance().proceedAfterPermission();
                }
            });
        }else{
            qrImage.setVisibility(View.GONE);
        }

        if(user!=null && user.getRole().equalsIgnoreCase("R")){
            horizontalProgressView.setVisibility(View.GONE);
        }
        /*if(user!=null){
            startService(new Intent(this, LocalService.class));
        }*/

    }

    private  void CheckInternet() {
        Utils utils = new Utils(getApplicationContext());
      if(  !utils.isNetworkAvailable()){
          Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_LONG).show();
      }
    }

    private void init() {






        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new MyAdapter(this,slideshow,""));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == slideshow.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
    }

    private void CheckWaiting() {
        DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String compdate = String.valueOf(onlydate.format(date));
        CollectionReference docRef = db.collection(socityCode).document("Visitors").collection("SVisitors");
        docRef.whereEqualTo("FlatNumber", String.valueOf(user.getFlatNo())).whereEqualTo("VisitorApprove","waiting").whereEqualTo("Date",compdate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        int size = task.getResult().size();
                        System.out.println("Queue size from sms="+size);


                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Log.d(TAG, document.getId() + " => " + document.getData());
                                Visitor visitor = new Visitor();
                                visitor.setVistorName(document.getString("VistorName"));
                                visitor.setVisitorPic(document.getString("VistorPic"));
                                visitor.setFlatNo(String.valueOf(document.get("FlatNumber")));
                                visitor.setWhomTomeet(document.getString("WhomTomeet"));
                                visitor.setVistorMobile(String.valueOf(document.get("VistorMobile")));
                                visitor.setVisitNumber(document.getString("VisitNumber"));
                                visitor.setDocId(document.getString("DocId"));
                                visitor.setVistorInTime(String.valueOf(document.get("VistorInTime")));
                                visitor.setVistorOutTime(String.valueOf(document.get("VistorOutTime")));
                                visitor.setMobileVeriy(document.getString("MobileVerify"));
                                visitor.setVisitorApprove(document.getString("VisitorApprove"));
                                ShowWaitingPopUp(visitor);
                            }



                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void SubscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(socityCode);
        }

    private void SendEMialTest() {
        List list = new ArrayList();
        list.add("jayesh.lodhiya@phicommerce.com");
        new SendMailTask(this).execute("jglodhiya@gmail.com",
                "mobile21", list, "Test..", "Test from Android   <br> https://firebasestorage.googleapis.com/v0/b/test-184bf.appspot.com/o/user.png?alt=media&token=6c12adc1-0d9c-47bd-afc2-8363197b1f6e");
    }
    public static HomeDashboard getInstance(){
        return homeDashboard;
    }



    private void MarkOnline() {
        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("UserDoc").collection("Sousers").document(user.getDocId());
        bookingref.update("Online", "Yes").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("User is online..");
            }
        });
    }

    public  void Refresh(){
        /*finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);*/
        mImageUrls.removeAll(mImageUrls);
        mNames.removeAll(mNames);
        mNamesexpected.removeAll(mNamesexpected);
        mImageUrlsexpected.removeAll(mImageUrlsexpected);
        expectedVisitorList.removeAll(expectedVisitorList);
        visitorsList.removeAll(visitorsList);
        totalIn = 0;
        LoadVisitorsList();
        homeadapter.notifyDataSetChanged();
        homeadapterexpected.notifyDataSetChanged();
        newsBoardAdpter.notifyDataSetChanged();

    }
    private void TempStoreDeviceId() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        deviceIds.add("fHKybXtc3Y8:APA91bHHpwv0k0ucl25SFitqteiI-e1540kBXkMED-a4WHfY18ymaSHI6bda6OU9kcC0qKaBIWjYWuteJkjEaGl71t6BjHBaAED3NQv-HbvHI9KPwzUu4OjNbNL0S7tga1l9Hj1itZjSA0PDsbf0IjqlA0ceu45Agw");
        //deviceIds.add("c43fQNJ-AGY:APA91bEvkJpGYrb-MCH0yfZ0YSs_OJAPnQyfsy2IAKvFoiva4cXXP1L5nXzjxV9sDAv5qUQgiXE6ieVckop0XCUu3VGpzHZNWU_OjxFhdN0qBUxwmIagemO7ekySOrBpmLcqKTdpsdO4");
        //deviceIds.add("cfiHyIcSTQM:APA91bGg36G7uHxP3UglApk3vJoKW49zG8P4KtleW_f6_8FhIBf9WFQg4_6CNEUwVY5xfz8sNDUwO4zA07qoxAJndaHIoUdw37gJOtTmKE5JNMwQgoyawC52u4LYV9k6BKxXRiKgYxFZ");
        String json = gson.toJson(deviceIds);
        editor.putString("PrefDeviceId", json);
        editor.commit();

    }

  /*  private void TestViewPager() {
        UltraViewPager ultraViewPager = (UltraViewPager)findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
//initialize UltraPagerAdapter，and add child view to UltraViewPager
        PagerAdapter adapter = new UltraViewPagerAdapter(homeadapter);
        ultraViewPager.setAdapter(adapter);

//initialize built-in indicator
        ultraViewPager.initIndicator();
//set style of indicators
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(Color.GREEN)
                .setNormalColor(Color.WHITE)
                .setRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//set the alignment
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//construct built-in indicator, and add it to  UltraViewPager
        ultraViewPager.getIndicator().build();

//set an infinite loop
        ultraViewPager.setInfiniteLoop(true);
//enable auto-scroll mode
        ultraViewPager.setAutoScroll(2000);

    }*/

    private void initilizeShake() {
        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mShaker = new ShakeListener(this);


        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                vibe.vibrate(200);
                new AlertDialog.Builder(HomeDashboard.this)
                        .setPositiveButton(android.R.string.ok, null)
                        .setMessage("Shooken! " + shakecount)
                        .show();
                shakecount = shakecount + 1;


                if (shakecount == 3) {
                    SendNotification();
                    shakecount = 0;
                }

            }
        });
    }

    public void initNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView navId = (ImageView) findViewById(R.id.opendraweerId);
        View hView = navigationView.getHeaderView(0);

        mProfileImageView = (CircleImageView) hView.findViewById(R.id.headerprofileImage);
        TextView headerName = (TextView) hView.findViewById(R.id.HeadernameId);
        backhead = (ImageView) hView.findViewById(R.id.headerfaintimageid);
        //headerfaintimageid

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = navigationView.getMenu().getItem(2);
        /*menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
               // Toast.makeText(getApplicationContext(), "on menu clck...", Toast.LENGTH_SHORT).show();
                logOut();
                return true;
            }
        });*/
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
        if (user == null) {
            headerName.setText(sharedPrefManager.getName());
        } else {
            headerName.setText(user.getFullName());
        }

        navId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT, true);
            }
        });

        //set up navigation drawer
        //drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
    private void openDialogBox() {

      /*  if (user.getRole().equals("S")) {
            TakeAllDeviceId();
        } else {
            //GetCurrentLocationLT();
            GetCurrentLocation();
            System.out.println("Current location in dashboard="+currentLoction);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("PrefDeviceId", null);
            //   Proxy.Type type = new TypeToken<ArrayList<ArrayObject>>() {}.getType();
            ArrayList<String> arrayList = gson.fromJson(json, ArrayList.class);
            for (int i = 0; i < arrayList.size(); i++) {
                String deviceId = arrayList.get(i);
                TakePrefDeviceId(deviceId);
            }


        }*/



    }

    private void GetCurrentLocation() {
        GPSTracker gps;
        gps = new GPSTracker(HomeDashboard.this);
        if (gps.canGetLocation()) {
            DatabaseReference childRef;
            DatabaseReference listRef;
            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();

            currentLoction = Double.toString(latitude)+","+Double.toString(longitude);
            System.out.println("My location=="+currentLoction);

        }
    }

    private void GetCurrentLocationLT() {
        locationTrack = new LocationTrack(HomeDashboard.this);
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            //     Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
            currentLoction = Double.toString(latitude)+","+Double.toString(longitude);
            System.out.println("My location=="+currentLoction);
        } else {
            locationTrack.showSettingsAlert();
        }
    }

    private void TakePrefDeviceId(String deviceId) {
        myAsyncTask myAsyncTask = (HomeDashboard.myAsyncTask) new myAsyncTask().execute(deviceId);
    }


   private void TakeAllDeviceId() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(socityCode).document("UserDoc").collection("Sousers");
        if(user.getRole().equalsIgnoreCase("R")){

            docRef.whereEqualTo("Role","S").addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    System.out.println("Documents notification size=" + size);
                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        SendNotification(documentSnapshot);
                    }
                }
            });
        }else {
            docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    System.out.println("Documents notification size=" + size);
                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        SendNotification(documentSnapshot);
                    }
                }
            });
        }



    }

    private void SendNotification() {

        myAsyncTask myAsyncTask = (HomeDashboard.myAsyncTask) new myAsyncTask().execute();
    }

    private void SendNotification(DocumentSnapshot snapshot) {
        deviceId = snapshot.getString("DeviceId");
        myAsyncTask myAsyncTask = (HomeDashboard.myAsyncTask) new myAsyncTask().execute(deviceId);
    }
    private void logOut(){
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        //editor.putBoolean("IS_LOGGED_IN",false);
        editor.clear();
        editor.commit();
        //clearAppData();

        drawerLayout.closeDrawers();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        //AuthActivity.getGoogleSignOut();
        //ClearData.getInstance().clearApplicationData();
       // finish();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.profileId:
                Intent intent1 = new Intent(this, User_Profile.class);
                startActivity(intent1);
            case R.id.HomeId:
                //  signOut();
                drawerLayout.closeDrawers();
                break;
            case R.id.logoutid:
                //Toast.makeText(HomeDashboard.this, "Log out..", Toast.LENGTH_SHORT).show();
                //  FirebaseAuth.getInstance().signOut();
                logOut();
                break;
              /*         case R.id.freebieId:
                           Toast.makeText(MapActivity.this,"Home",Toast.LENGTH_SHORT).show();
                           drawerLayout.closeDrawers();
                          *//* Intent intent = new Intent(MapActivity.this, MapActivity.class);
                           startActivity(intent);*//*
                           break;
                       case R.id.paymentId:
                           Toast.makeText(MapActivity.this,"Settings",Toast.LENGTH_SHORT).show();
                           drawerLayout.closeDrawers();
                          *//* Intent intent2 = new Intent(MapActivity.this, MapActivity.class);
                           startActivity(intent2);*//*
                           break;
                       case R.id.tripId:
                           Toast.makeText(MapActivity.this,"Trash",Toast.LENGTH_SHORT).show();
                           drawerLayout.closeDrawers();
                           break;

                       case R.id.tipsId:
                           Toast.makeText(MapActivity.this,"Trash",Toast.LENGTH_SHORT).show();
                           drawerLayout.closeDrawers();
                           break;*/
        }
        return true;
    }

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearPopup() {
            if(waitingvisitorDialog!=null && waitingvisitorDialog.isShowing()){
                waitingvisitorDialog.dismiss();
            }
    }

    public void RefreshData() {
        LoadVisitorsList();
        homeadapter.notifyDataSetChanged();
    }

    public void SendAlertToAll(String msg) {
        alertMsg = msg;
        TakeAllDeviceId();
    }


    private class myAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... pParams) {
          //  String deviceId = pParams[0];
           // System.out.println("Sending to DeviceId=" + deviceId);

            //
            String authKey = AUTH_KEY_FCM; // You FCM AUTH key
            String FMCurl = API_URL_FCM;
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
                json.put("to","/topics/"+socityCode);
               // json.put("to",deviceId);
                JSONObject payload = new JSONObject();
                JSONObject info = new JSONObject();
                info.put("title", "Alert "); // Notification title
                info.put("sound", sound);

                if (user.getRole().equals("S")) {
                    info.put("body", alertMsg); // Notification body
                } /*else if (user.getRole().equals("R")) {
                    info.put("body", "I am " + user.getFullName() + " from flat No " + user.getFlatNo() + " need help please tap to see my current location"); // Notification body
                    info.put("location", currentLoction);
                }*/

                //  info.put("docId","no");
                info.put("MobileNo",user.getMobileNumber());
                info.put("Scode", socityCode);
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

    private void getImages() {
      /*  Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        mNames.add("Havasu Falls");

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mNames.add("Trondheim");

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mNames.add("Portugal");

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mNames.add("Rocky Mountain National Park");


        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("Mahahual");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("Frozen Lake");


        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");
        mNames.add("White Sands Desert");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        mNames.add("Austrailia");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Washington");
*/
        initRecyclerView();


    }

    private void ShakeNotification() {
        shkonlyonce = true;


        new Thread() {
            public void run() {

                try {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                           /* if(chat_conversation.getParent()!=null)
                                ((ViewGroup)chat_conversation.getParent()).removeView(chat_conversation); // <- fix
                            chatlout.addView(chat_conversation);*/
                            tv.startAnimation(shake);
                            // notificationicon.startAnimation(shake);
                        }
                    });
                    // Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        homeadapter = new RecyclerViewAdapter(this, mNames, mImageUrls,visitorsList);
        recyclerView.setAdapter(homeadapter);

        LinearLayoutManager layoutManagerexpected = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewexpected = (RecyclerView) findViewById(R.id.recyclerViewexpected);
        recyclerViewexpected.setLayoutManager(layoutManagerexpected);
        homeadapterexpected = new ExpectedVisitorAdapter(this,expectedVisitorList);
        recyclerViewexpected.setAdapter(homeadapterexpected);

//recyclerViewboard
        LinearLayoutManager layoutManagerboard = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewboard = (RecyclerView) findViewById(R.id.recyclerViewboard);
        recyclerViewboard.setLayoutManager(layoutManagerboard);
        newsBoardAdpter = new NewsBoardAdpter(this, noticeText, mImageUrlsexpected);
        recyclerViewboard.setAdapter(newsBoardAdpter);


    }

    private void LoadVisitorsList() {
      //  socityCode="SPAS";
        /*totalVisitor = 0;
        totalIn = 0;
        totalOut=0;*/
        CollectionReference docRef = db.collection(socityCode).document("Visitors").collection("SVisitors");
        if (user.getRole().equals("R")) {
            docRef.whereEqualTo("FlatNumber", String.valueOf(user.getFlatNo())).addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    docSize = size;
                    System.out.println("Documents size====" + size);
                    //System.out.println("bookingsList size="+visitorsList.size());
                    //Refresh();
                    if (expectedVisitorList.size()>0 || visitorsList.size()>0) {
                        mImageUrls.removeAll(mImageUrls);
                        mNames.removeAll(mNames);
                        visitorsList.removeAll(visitorsList);
                        expectedVisitorList.removeAll(expectedVisitorList);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                    }
                    if (size == 0) {
                        mImageUrls.removeAll(mImageUrls);
                        mNames.removeAll(mNames);
                        visitorsList.removeAll(visitorsList);
                        expectedVisitorList.removeAll(expectedVisitorList);
                        homeadapter.notifyDataSetChanged();
                    }

                    if ( visitorsList.size()>0 || expectedVisitorList.size()>0) {
                        mImageUrlsexpected.removeAll(mImageUrlsexpected);
                        mNamesexpected.removeAll(mNamesexpected);
                        visitorsList.removeAll(visitorsList);
                        expectedVisitorList.removeAll(expectedVisitorList);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                    }
                    if (size == 0) {
                        mImageUrlsexpected.removeAll(mImageUrlsexpected);
                        mNamesexpected.removeAll(mNamesexpected);
                        visitorsList.removeAll(visitorsList);
                        expectedVisitorList.removeAll(expectedVisitorList);
                        homeadapterexpected.notifyDataSetChanged();

                    }

                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //  System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        CreateVisitorsList(documentSnapshot);
                    }
                }
            });
        } else if (user.getRole().equals("S")) {
            DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();

            String compdate = String.valueOf(onlydate.format(date));
            docRef.whereEqualTo("Date", compdate).addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    totalVisitor = 0;
                    totalIn = 0;
                    totalOut=0;
                    if(documentSnapshots!=null){
                        size = documentSnapshots.getDocuments().size();
                       // totalVisitor = size;
                    }

                    docSize = size;
                    System.out.println("Documents size====" + size);

                    if ( expectedVisitorList.size()>0 || visitorsList.size()>0) {
                        mImageUrls.removeAll(mImageUrls);
                        mNames.removeAll(mNames);
                        expectedVisitorList.removeAll(expectedVisitorList);
                        visitorsList.removeAll(visitorsList);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                    }
                    if (size == 0) {
                        mImageUrls.removeAll(mImageUrls);
                        mNames.removeAll(mNames);
                        expectedVisitorList.removeAll(expectedVisitorList);
                        visitorsList.removeAll(visitorsList);
                        homeadapter.notifyDataSetChanged();
                    }

                    if ( expectedVisitorList.size()>0 || visitorsList.size()>0) {
                        mImageUrlsexpected.removeAll(mImageUrlsexpected);
                        mNamesexpected.removeAll(mNamesexpected);
                        expectedVisitorList.removeAll(expectedVisitorList);
                        visitorsList.removeAll(visitorsList);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                    }
                    if (size == 0) {
                        mImageUrlsexpected.removeAll(mImageUrlsexpected);
                        mNamesexpected.removeAll(mNamesexpected);
                        homeadapterexpected.notifyDataSetChanged();
                        expectedVisitorList.removeAll(expectedVisitorList);
                        visitorsList.removeAll(visitorsList);
                    }
                    if(documentSnapshots!=null) {
                        for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                            //  System.out.println("Documents data==="+documentSnapshot.getData().toString());
                            CreateVisitorsList(documentSnapshot);
                        }
                        ShowProgress();
                    }
                }
            });
        }
    }

    private void ShowProgress() {

       // float per = Math.round((1/2));
     float fper =   Math.round((totalIn/totalVisitor)*100);
     int per = Integer.parseInt(String.format("%.0f", fper));
       // horizontalProgressView.setMax(Integer.parseInt(String.format("%.0f", totalVisitor)));
        horizontalProgressView.setTextSuffix("%("+String.format("%.0f", totalIn)+"/"+String.format("%.0f", totalVisitor)+")");
      //  horizontalProgressView.setReachBarColor(getResources().getColor(R.color.colorPrimary));
        horizontalProgressView.setProgressInTime(0,per,2500);
// reset current progress with anim in 2500ms
        horizontalProgressView.runProgressAnim(1000);
    }

    private void GetUserData() {

        socityCode = sharedPreferences.getString("SOC_CODE", null); //sharedPrefManager.getSocityCode();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference docRef = db.collection(socityCode).document("UserDoc").collection("Sousers");

            docRef.whereEqualTo("Email", sharedPrefManager.getUserEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    System.out.println("Documents size=" + size);
                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        SaveUserData(documentSnapshot);
                        UpdateDeviceId();
                        MarkOnline();
                    }
                }
            });


    }
    private void GetSocInfo(){
        socityCode = sharedPreferences.getString("SOC_CODE", null); //sharedPrefManager.getSocityCode();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference docRef = db.collection(socityCode).document("Info").collection("SInfo");

        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();
                System.out.println("Documents size=" + size);
                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                      SaveSocData(documentSnapshot);
                    }
            }
        });


    }

    private void SaveSocData(DocumentSnapshot documentSnapshot) {
        Society society = new Society();
        society.setName(documentSnapshot.getString("Name"));
        society.setCode(documentSnapshot.getString("Code"));
        society.setMid(documentSnapshot.getString("Mid"));
        society.setMainAmnt(documentSnapshot.getString("amt"));

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(society);
        prefsEditor.putString("SocObj", json);
        prefsEditor.commit();

        societyName.setText(society.getName());
    }


    private void UpdateDeviceId() {
        String deviceId = FirebaseInstanceId.getInstance().getToken();

        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("UserDoc").collection("Sousers").document(user.getDocId());
        bookingref.update("DeviceId", deviceId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("DeviceId Updated");
            }
        });

    }

    private void SaveUserData(DocumentSnapshot documentSnapshot) {
        user = new User();
        user.setFlatNo(documentSnapshot.getString("FlatNumber"));
        user.setMobileNumber(documentSnapshot.getString("MobileNumber"));
        user.setRole(documentSnapshot.getString("Role"));
        user.setFullName(documentSnapshot.getString("Name"));
        user.setDocId(documentSnapshot.getString("DocId"));
        user.setPhoto(documentSnapshot.getString("Photo"));
        user.setEmailId(documentSnapshot.getString("Email"));
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("UserObj", json);
        prefsEditor.commit();
        LoadVisitorsList();
        initRecyclerView();
        SetProfile();
       // startService(new Intent(this, LocalService.class));
    }

    private void SetProfile() {
        if (user.getPhoto().contains(".jpg")) {

            Picasso.with(mContext)
                    .load(user.getPhoto())
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .error(R.drawable.customerpic)
                    .into(mProfileImageView);

     /*       Picasso.with(mContext)
                    .load(user.getPhoto())
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .error(R.drawable.customerpic)
                    .into(backhead);

*/
        } else {
  /*          Bitmap src;
            byte[] decodedString = Base64.decode(user.getPhoto(), Base64.DEFAULT);
            src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);*/


            //mProfileImageView.setImageBitmap(src);
            try {
                Glide.with(this)
                        .load(user.getPhoto())
                        .into(mProfileImageView);
            }catch (Exception e){

            }

            //    Glide.with(this).load(bitmapToByte(src)).asBitmap().into(mProfileImageView);
//            Glide.with(this).load(bitmapToByte(src)).asBitmap().into(backhead);
           /* Picasso.with(mContext)
                    .load(src)
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .error(R.drawable.customerpic)
                    .into(mProfileImageView);*/
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public String RemoveSpecialSymbol(String email) {
        Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match = pt.matcher(email);
        while (match.find()) {
            String s = match.group();
         //   email = email.replaceAll("\\" + s, "");
        }
        return email;
    }

    private void UpdatePendingCount(int count) {

        //count=0;
    }

    public void OpenBookings(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);

        startActivity(intent);
        //Toast.makeText(this,"Coming soon!!",Toast.LENGTH_LONG).show();
    }
    public void societychat(View view){
       /* Intent intent = new Intent(this, SocietyChat.class);
        startActivity(intent);*/
        //Toast.makeText(this,"Coming soon!!",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ComingSoonActivity.class);
        startActivity(intent);

    }

    public void OpenReports(View view) {
        Intent intent = new Intent(this, CreatePoll.class);
        startActivity(intent);
        //Intent intent = new Intent(this, ComingSoonActivity.class);
        //startActivity(intent);
       // Toast.makeText(this,"Coming soon!!",Toast.LENGTH_LONG).show();

    }

    public void openassistant(View view) {
        /*Intent intent = new Intent(this, ChatAssitant.class);
        startActivity(intent);*/
        Intent intent = new Intent(this, ComingSoonActivity.class);
        startActivity(intent);
        //Toast.makeText(this,"Coming soon!!",Toast.LENGTH_LONG).show();
    }

    public void UserProfile(View view) {
       // System.out.println("Clicked......................");
        /*Intent intent = new Intent(this, User_Profile.class);
        startActivity(intent);*/
        //Toast.makeText(this,"Coming soon!!",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ComingSoonActivity.class);
        startActivity(intent);
    }

    public void PayInvoice(View view){
        Intent intent = new Intent(this, ComingSoonActivity.class);
        //startActivity(intent);
       // Intent intent = new Intent(this, AwsFaceDetection.class);
        startActivity(intent);
       // Toast.makeText(this,"Coming soon!!",Toast.LENGTH_LONG).show();
    }
    public void OpenRegiter(View view){
        Intent intent = new Intent(this, RegisterBook.class);
        startActivity(intent);
       // Toast.makeText(this,"Coming soon!!",Toast.LENGTH_LONG).show();
    }

    public void testbeakon(View view) {

      //  Intent intent = new Intent(this, BeaconActivity.class);
        //startActivity(intent);
    }

    public void manageVendor(View view) {
       // Toast.makeText(this,"Coming soon!!",Toast.LENGTH_LONG).show();
/*        Intent intent = new Intent(this, VendorActivity.class);
        startActivity(intent);*/
        Intent intent = new Intent(this, ComingSoonActivity.class);
        startActivity(intent);
    }

    public void opensettings(View view) {
        Intent intent = new Intent(this, DetectionActivity.class);
        startActivity(intent);
    }

    public void OpenMemberList(View view) {
        Intent intent = new Intent(this, MemberListActivity.class);
        startActivity(intent);
         /* Intent intent = new Intent(this, BeaconActivity.class);
        startActivity(intent);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        homeadapter.onActivityResult(requestCode, resultCode, data);
        //homeadapterexpected.notifyDataSetChanged();
        //homeadapter.notifyDataSetChanged();
    }

    private void CreateVisitorsList(DocumentSnapshot documentSnapshot) {
        Visitor visitor = new Visitor();
        visitor.setVistorName(documentSnapshot.getString("VistorName"));
        visitor.setVisitorPic(documentSnapshot.getString("VistorPic"));
        visitor.setFlatNo(String.valueOf(documentSnapshot.get("FlatNumber")));
        visitor.setWhomTomeet(documentSnapshot.getString("WhomTomeet"));
        visitor.setVistorMobile(String.valueOf(documentSnapshot.get("VistorMobile")));
        visitor.setVisitNumber(documentSnapshot.getString("VisitNumber"));
        visitor.setUniqueNumber(documentSnapshot.getString("UniqueNumber"));
        visitor.setDocId(documentSnapshot.getString("DocId"));
        visitor.setVistorInTime(String.valueOf(documentSnapshot.get("VistorInTime")));
        visitor.setVistorOutTime(String.valueOf(documentSnapshot.get("VistorOutTime")));
        visitor.setMobileVeriy(documentSnapshot.getString("MobileVerify"));
        visitor.setVisitorApprove(documentSnapshot.getString("VisitorApprove"));
        visitor.setVisitorExpected(documentSnapshot.getString("VisitorExpected"));
        if(documentSnapshot.getString("VisitorEntryMethod")!=null){
            visitor.setEntryMethod(documentSnapshot.getString("VisitorEntryMethod"));
        }

        String visitorPic = documentSnapshot.getString("VistorPic");
        String name = documentSnapshot.getString("VistorName");
        String stat="";

        if (!String.valueOf(documentSnapshot.get("VistorOutTime")).equals("")) {
            stat = "O";
            totalOut = totalOut+1;
        } else if(!String.valueOf(documentSnapshot.get("VistorInTime")).equals("")) {
            stat = "I";
            totalIn = totalIn+1;
        }
        totalVisitor = totalIn+totalOut;

        if(String.valueOf(documentSnapshot.get("VistorInTime")).equals("") && visitor.getVisitorExpected().equalsIgnoreCase("Y")){
            expectedVisitorList.add(visitor);
        }


        if (!String.valueOf(documentSnapshot.get("VistorInTime")).equals("") && String.valueOf(documentSnapshot.get("VistorOutTime")).equals("")) {
            mImageUrls.add(visitorPic);
            mNames.add(name + ":" + stat + ":" + documentSnapshot.getString("DocId"));
            visitorsList.add(visitor);
            Collections.sort(visitorsList, new Comparator<Visitor>() {
                public int compare(Visitor v1, Visitor v2) {
                    //  return s1.getDistance().compareToIgnoreCase(s2.getDistance());
                    //return Double.compare(Double.parseDouble(s1.getDistance()), Double.parseDouble(s2.getDistance()));

                    try {

                        if (v1.getVistorInTime() != null && v2.getVistorInTime() != null) {
                            d1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).parse(v1.getVistorInTime());
                            d2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).parse(v2.getVistorInTime());
                            return d2.compareTo(d1);
                        }


                    } catch (Exception e) {

                    }

                    // return Double.compare(), v1.getVistorInTime());

                    return 0;
                }
            });


        }





        homeadapter.notifyDataSetChanged();
        homeadapterexpected.notifyDataSetChanged();
        newsBoardAdpter.notifyDataSetChanged();

    }

    private void ShowWaitingPopUp(final Visitor visitor) {
        wiatingVisitors.put(visitor.getDocId(),visitor.getDocId());
    // Toast.makeText(this,visitor.getVistorName()+" is Waiting at gate",Toast.LENGTH_LONG).show();
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View visitorDialogView = factory.inflate(R.layout.wiating_visitor_pop_up, null);
        waitingvisitorDialog = new AlertDialog.Builder(mContext).create();
        waitingvisitorDialog.setView(visitorDialogView);
//        waitingvisitorDialog.setCancelable(false);
        waitingvisitorDialog.setCanceledOnTouchOutside(false);
        TextView name = (TextView) visitorDialogView.findViewById(R.id.visitorNameId);
        TextView mobile = (TextView) visitorDialogView.findViewById(R.id.visitorMobileId);
        TextView visiorFlat = (TextView) visitorDialogView.findViewById(R.id.visitorFlatId);
        TextView status = (TextView) visitorDialogView.findViewById(R.id.statusId);
        CircleImageView pic = (CircleImageView)  visitorDialogView.findViewById(R.id.visitorImage);
        Bitmap src;
        TextView inTime = (TextView) visitorDialogView.findViewById(R.id.vintimeId);
        TextView outTime = (TextView) visitorDialogView.findViewById(R.id.vouttimeId);
        Button approve = (Button) visitorDialogView.findViewById(R.id.papproveid);
        Button reject = (Button) visitorDialogView.findViewById(R.id.prejectId);
        Button okbutton = (Button)visitorDialogView.findViewById(R.id.okId);
        okbutton.setVisibility(View.GONE);
        name.setText(visitor.getVistorName());
        mobile.setText(visitor.getVistorMobile());
        visiorFlat.setText(visitor.getFlatNo());


        status.setText(visitor.getVisitorApprove());

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingvisitorDialog.dismiss();
                clearNotification();
                UpdateVisitorStatus("Approved",visitor.getDocId());
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingvisitorDialog.dismiss();
                clearNotification();
                UpdateVisitorStatus("Rejected",visitor.getDocId());
            }
        });

        byte[] decodedString = Base64.decode(visitor.getVisitorPic(), Base64.DEFAULT);
        src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        //  myViewHolder.thumbnail.setImageBitmap(src);
        pic.setImageBitmap(src);

        //inTime.setText(visitor.getVistorInTime());
        //outTime.setText(visitor.getVistorOutTime());

        waitingvisitorDialog.show();


    }
    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.cancelAll();
    }

    private void UpdateVisitorStatus(final String stat, String docId) {
       final DocumentReference bookingref;
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String  socityCode = sharedPreferences.getString("SOC_CODE", null);
        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final Date date = new Date();
        bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(docId);
        //  paydialog.show();
        bookingref.update("VisitorApprove", stat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                bookingref.update("VistorInTime", String.valueOf(dateFormat.format(date))).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Visitor "+stat,Toast.LENGTH_LONG).show();
  //                      waitingvisitorDialog.setCancelable(true);
                        waitingvisitorDialog.dismiss();


                    }
                });


            }
        });

        //finish();
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(HomeDashboard.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  Refresh();
        //Toast.makeText(this,"Resumed",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        String socityCode = sharedPreferences.getString("SOC_CODE", null);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(socityCode).document("UserDoc").collection("Sousers").document(user.getDocId());
        docRef.update("Online","No");
    }
}
