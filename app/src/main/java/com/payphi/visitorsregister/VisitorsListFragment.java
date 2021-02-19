package com.payphi.visitorsregister;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.payphi.visitorsregister.adapters.BookingAdapter;
import com.payphi.visitorsregister.adapters.RecyclerViewAdapter;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Visitor;
import com.payphi.visitorsregister.utils.Utils;

import org.json.JSONObject;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VisitorsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VisitorsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisitorsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private BookingAdapter adapter;
    private RecyclerView recyclerView;
    private Paint paint = new Paint();
    String flag = "";
    List<Visitor> temp;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference docRef;
    public static VisitorsListFragment visitorsListFragment;
    SharedPreferences sharedPreferences;
    Date d1;
    Date d2;
    ImageView download;
    public static int docSize;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Visitor> visitorsList;
    public final static String AUTH_KEY_FCM = "AIzaSyB28LSaN0BW-iUbQw27ek0fHb4g2yLlaAs";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    private OnFragmentInteractionListener mListener;
    String socityCode = "";
    User user;
    Dialog dialog;
    public ProgressDialog paydialog; // this = YourActivity
    ImageView filtericon;
    ArrayList<String> filterText;
    HashMap<String,String> mapfilters;
    String visitorName="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_visitors_list, container, false);
        visitorsList = new ArrayList<>();

        adapter = new BookingAdapter(getContext(), visitorsList, adapter);

        visitorsListFragment = this;
        flag = "P";
        paydialog = new ProgressDialog(getContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);

        //GetBookingsDetails();
        filtericon = (ImageView) view.findViewById(R.id.filterid);
        recyclerView = (RecyclerView) view.findViewById(R.id.crecycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.cswiperefresh);
        download = (ImageView) view.findViewById(R.id.reportid);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //    recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        sharedPreferences = getContext().getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        socityCode = sharedPreferences.getString("SOC_CODE", null);
        docRef = db.collection(socityCode).document("Visitors").collection("SVisitors");
        GetUserData();
        LoadVisitorsList();
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateReport();
            }
        });
        initSwipe();

        EditText searchField = (EditText) view.findViewById(R.id.searchboxid);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });
        filtericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFilter();
            }
        });
        return view;
    }

    private void GenerateReport() {
        Utils.DownloadVisitorList(getContext(),visitorsList);
    }


    private void ShowFilter() {
        temp = new ArrayList();
        ResetList();
        dialog = new Dialog(getContext(), R.style.DialogSlideAnim);
        // dialog.requestWindowFeature(set);
        // dialog.setTitle("Select Time");

        dialog.setContentView(R.layout.filtervisitor);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        RadioGroup genderradioGroup, glassesRadioGroup, headCoverRadioGroup, facialHairRadioGroup, baldRadioGroup,mushtacheGroup;

        ImageView imageView = (ImageView) dialog.findViewById(R.id.pbackid);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        filterText = new ArrayList<>();
        mapfilters = new HashMap<>();
        Button button = (Button) dialog.findViewById(R.id.applyButton);

        genderradioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpGenderId);
        genderradioGroup.clearCheck();

        glassesRadioGroup = (RadioGroup) dialog.findViewById(R.id.glassesRadioGrpId);
        glassesRadioGroup.clearCheck();

        headCoverRadioGroup = (RadioGroup) dialog.findViewById(R.id.headcovergrpid);
        headCoverRadioGroup.clearCheck();

        facialHairRadioGroup = (RadioGroup) dialog.findViewById(R.id.facialHairGrpId);
        facialHairRadioGroup.clearCheck();

        baldRadioGroup = (RadioGroup) dialog.findViewById(R.id.baldgrpid);
        baldRadioGroup.clearCheck();

        mushtacheGroup = (RadioGroup) dialog.findViewById(R.id.mushtacheGrpId);
        mushtacheGroup.clearCheck();

        genderradioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    // Toast.makeText(getContext(), String.valueOf(rb.getTag().toString()), Toast.LENGTH_SHORT).show();
                    if (mapfilters != null) {
                        /*if (String.valueOf(rb.getTag().toString()).equals("male|ladka|aadmi|man|boy")) {
                            filterText.remove("female|ladki|aurat|women|girl");
                        } else if (String.valueOf(rb.getTag().toString()).equals("female|ladki|aurat|women|girl")) {
                            filterText.remove("male|ladka|aadmi|man|boy");
                        }*/

                        filterText.add(String.valueOf(rb.getTag().toString()));
                        mapfilters.put("Gender",String.valueOf(rb.getTag().toString()));

                    }

                }
            }
        });

        glassesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    //     Toast.makeText(getContext(), String.valueOf(rb.getTag()), Toast.LENGTH_SHORT).show();
                    if (mapfilters != null) {
                        /*if (String.valueOf(rb.getTag().toString()).equals("male|ladka|aadmi|man|boy")) {
                            filterText.remove("female|ladki|aurat|women|girl");
                        } else if (String.valueOf(rb.getTag().toString()).equals("female|ladki|aurat|women|girl")) {
                            filterText.remove("male|ladka|aadmi|man|boy");
                        }*/

                        filterText.add(String.valueOf(rb.getTag().toString()));
                        mapfilters.put("Eyeglass",String.valueOf(rb.getTag().toString()));

                    }
                }
            }
        });

        headCoverRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    //   Toast.makeText(getContext(), String.valueOf(rb.getTag()), Toast.LENGTH_SHORT).show();
                    if (filterText != null) {
                        if (String.valueOf(rb.getTag().toString()).equals("cap|topi|gamcha|pagdi")) {
                            filterText.remove("");
                        } else if (String.valueOf(rb.getTag().toString()).equals("")) {
                            filterText.remove("cap|topi|gamcha|pagdi");
                        }
                    }
                    filterText.add(String.valueOf(rb.getTag()));
                }
            }
        });


        facialHairRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    //   Toast.makeText(getContext(), String.valueOf(rb.getTag()), Toast.LENGTH_SHORT).show();
                    if (mapfilters != null) {
                        /*if (String.valueOf(rb.getTag().toString()).equals("male|ladka|aadmi|man|boy")) {
                            filterText.remove("female|ladki|aurat|women|girl");
                        } else if (String.valueOf(rb.getTag().toString()).equals("female|ladki|aurat|women|girl")) {
                            filterText.remove("male|ladka|aadmi|man|boy");
                        }*/

                        filterText.add(String.valueOf(rb.getTag().toString()));
                        mapfilters.put("Beard",String.valueOf(rb.getTag().toString()));

                    }
                }
            }
        });

        mushtacheGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    //   Toast.makeText(getContext(), String.valueOf(rb.getTag()), Toast.LENGTH_SHORT).show();
                    if (mapfilters != null) {
                        /*if (String.valueOf(rb.getTag().toString()).equals("male|ladka|aadmi|man|boy")) {
                            filterText.remove("female|ladki|aurat|women|girl");
                        } else if (String.valueOf(rb.getTag().toString()).equals("female|ladki|aurat|women|girl")) {
                            filterText.remove("male|ladka|aadmi|man|boy");
                        }*/

                        filterText.add(String.valueOf(rb.getTag().toString()));
                        mapfilters.put("Mustache",String.valueOf(rb.getTag().toString()));

                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Final filter text=" + filterText);
                if (filterText.size() > 0) {
                    //AdvanceFilter(filterText);
                    AdvanceFilter(mapfilters);
                }

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void ResetList() {
        adapter.updateList(visitorsList);
        adapter.notifyDataSetChanged();
    }

    void filter(String text) {

        temp = new ArrayList();
        for (Visitor d : visitorsList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            String name = d.getVistorName().toLowerCase();
            String date[] = d.getVistorInTime().toLowerCase().split("\\s+");
            String feature = d.getFeatures();
            String flat = d.getFlatNo().toLowerCase();
            text = text.toLowerCase();
            if (name.contains(text) || date[0].contains(text) || flat.contains(text)) {
                // Toast.makeText(getContext(),d.getShopName(),Toast.LENGTH_SHORT).show();
                temp.add(d);

                adapter.updateList(temp);
                adapter.notifyDataSetChanged();
            }
        }
        //update recyclerview

    }

    private void AdvanceFilter(HashMap filterList) {
        List<Visitor> temp = new ArrayList();
        int containSize = 0;

        for (Visitor d : visitorsList) {
            containSize = 0;

            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(filterList.get("Gender")!=null){
                if (filterList.get("Gender").toString().equalsIgnoreCase(d.getGender())) {
                    containSize = containSize + 1;
                }
            }
            if(filterList.get("Eyeglass")!=null){
                if (filterList.get("Eyeglass").toString().equalsIgnoreCase(d.getEyeGlass())) {
                    containSize = containSize + 1;
                }
            }
            if(filterList.get("Mustache")!=null){
                if (filterList.get("Mustache").toString().equalsIgnoreCase(d.getMustache())) {
                    containSize = containSize + 1;
                }
            }
            if(filterList.get("Beard")!=null){
                if (filterList.get("Beard").toString().equalsIgnoreCase(d.getBeard())) {
                    containSize = containSize + 1;
                }
            }



            if (containSize == filterList.size()) {
                temp.add(d);
                adapter.updateList(temp);
                adapter.notifyDataSetChanged();
            }

        }

        if (temp.size() == 0) {
            Toast.makeText(getContext(), "No Record matched!!", Toast.LENGTH_LONG).show();
        }

    }


    private void AdvanceFilter(ArrayList filterList) {
        List<Visitor> temp = new ArrayList();
        int containSize = 0;
        boolean flag = false;
        Visitor ud = null;
        for (Visitor d : visitorsList) {
            containSize = 0;
            ud = d;
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            String name = d.getVistorName().toLowerCase();
            String date[] = d.getVistorInTime().toLowerCase().split("\\s+");
            String feature = d.getFeatures();
            String flat = d.getFlatNo().toLowerCase();
            // text = text.toLowerCase();

            if (feature != null && feature.contains(",")) {
                String arr[] = feature.split(",");
                for (int i = 0; i < arr.length; i++) {

                    if (filterList.contains(arr[i].trim())) {
                        containSize = containSize + 1;
                    }
                    }


            }

            if (containSize == filterList.size()) {
                temp.add(d);
                adapter.updateList(temp);
                adapter.notifyDataSetChanged();
            }

        }

        if (temp.size() == 0) {
            Toast.makeText(getContext(), "No Record matched!!", Toast.LENGTH_LONG).show();
        }

    }

    private void GetUserData() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                if (!flag.equals("P")) return 0;

                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();


                if (direction == ItemTouchHelper.LEFT) {
                    //adapter.removeItem(position);
                    //Toast.makeText(getApplicationContext(),"Left",Toast.LENGTH_LONG).show();
                    UpdateVisitorToOut(position);

                } else {
                    UpdateVisitorToOut(position);
                    //   removeView();
                    // edit_position = position;
                    //alertDialog.setTitle("Edit Country");
                    //et_country.setText(countries.get(position));
                    // alertDialog.show();


                    //Toast.makeText(getApplicationContext(),"Right",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        paint.setColor(Color.parseColor("#00897b"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.visitorout);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);
                    } else {
                        paint.setColor(Color.parseColor("#00897b"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.visitorout);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void SendNotification(Visitor visitor) {
        visitorName = visitor.getVistorName();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(socityCode).document("UserDoc").collection("Sousers");

        docRef.whereEqualTo("FlatNumber", visitor.getFlatNo()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
    private void sendPushNotification(DocumentSnapshot snapshot){
        String deviceId = snapshot.getString("DeviceId");

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
            sound = "android.resource://" + getActivity().getPackageName() + "/raw/jinglebellsms";
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
                String msg = visitorName+ " has just checked out from society";
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


    private void UpdateVisitorToOut(int position) {

     final   Visitor visitorObj;
        System.out.println("Pos==" + position);
        if(temp!=null && temp.size()>0){
            visitorObj = temp.get(position);
        }else{
            visitorObj = visitorsList.get(position);
        }




        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        if ((visitorObj.getVistorOutTime().equals("") && !visitorObj.getVistorInTime().equals("") ) && (user.getRole().equals("S"))) {

            DocumentReference bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(visitorObj.getDocId());
            bookingref.update("VistorOutTime", String.valueOf(dateFormat.format(date))).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("DeviceId Updated");
                    SendNotification(visitorObj);
                    RefreshMainActivity();
                    Refresh();
                }
            });

        }
        adapter.notifyDataSetChanged();
    }

    private void RefreshMainActivity() {
       HomeDashboard.getInstance().Refresh();
    }

    private void LoadVisitorsList() {

        if (user.getRole().equals("R")) {
            docRef.whereEqualTo("FlatNumber", user.getFlatNo()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    docSize = size;
                    System.out.println("Documents size====" + size);
                    //System.out.println("bookingsList size="+visitorsList.size());
                    if (visitorsList.size() > 0) {

                        visitorsList.removeAll(visitorsList);

                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                        adapter.notifyDataSetChanged();
*/


                    }
                    if (size == 0) {
                        visitorsList.removeAll(visitorsList);
                        adapter.notifyDataSetChanged();
                    }


                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //  System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        CreateVisitorsList(documentSnapshot);
                    }
                }
            });
        } else {
            docRef.orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    docSize = size;
                    System.out.println("Documents size====" + size);
                    //System.out.println("bookingsList size="+visitorsList.size());
                    if (visitorsList.size() > 0) {
                        visitorsList.removeAll(visitorsList);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                    }
                    if (size == 0) {
                        visitorsList.removeAll(visitorsList);
                        adapter.notifyDataSetChanged();
                    }


                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //  System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        CreateVisitorsList(documentSnapshot);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    Refresh();

                }
            });
        }
    }

    private void Refresh() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void CreateVisitorsList(DocumentSnapshot documentSnapshot) {
        Visitor visitor = new Visitor();
        visitor.setVistorName(documentSnapshot.getString("VistorName"));
        visitor.setVisitorPic(documentSnapshot.getString("VistorPic"));
        visitor.setFlatNo(documentSnapshot.getString("FlatNumber"));
        visitor.setWhomTomeet(documentSnapshot.getString("WhomTomeet"));
        visitor.setVistorMobile(documentSnapshot.getString("VistorMobile"));
        visitor.setVisitNumber(documentSnapshot.getString("VisitNumber"));
        visitor.setDocId(documentSnapshot.getString("DocId"));
        visitor.setVistorInTime(String.valueOf(documentSnapshot.get("VistorInTime")));
        visitor.setVistorOutTime(String.valueOf(documentSnapshot.get("VistorOutTime")));
        visitor.setMobileVeriy(documentSnapshot.getString("MobileVerify"));
        visitor.setUniqueNumber(documentSnapshot.getString("UniqueNumber"));
       // visitor.setVisitorsFaceFeatures((HashMap<String,String>) documentSnapshot.get("Features"));
        visitor.setVisitorApprove(documentSnapshot.getString("VisitorApprove"));
        visitor.setVisitPurpose(documentSnapshot.getString("Purpose"));
        if(documentSnapshot.getString("VisitorEntryMethod")!=null){
            visitor.setEntryMethod(documentSnapshot.getString("VisitorEntryMethod"));
        }
        if(documentSnapshot.getString("Gender")!=null){
            visitor.setGender(documentSnapshot.getString("Gender"));
        }
        if(documentSnapshot.getString("Mustache")!=null){
            visitor.setMustache(documentSnapshot.getString("Mustache"));
        }
        if(documentSnapshot.getString("Beard")!=null){
            visitor.setBeard(documentSnapshot.getString("Beard"));
        }
        if(documentSnapshot.getString("Eyeglass")!=null){
            visitor.setEyeGlass(documentSnapshot.getString("Eyeglass"));
        }
        if(documentSnapshot.getString("AgeL")!=null){
            visitor.setAgeL(documentSnapshot.getString("AgeL"));
        }
        if(documentSnapshot.getString("AgeU")!=null){
            visitor.setAgeH(documentSnapshot.getString("AgeU"));
        }

        //   visitor.setBookingTime(documentSnapshot.getString("Time"));
       /* bookings.setStatus(documentSnapshot.getString("Status"));
        bookings.setDocumentId(documentSnapshot.getString("DocId"));*/

        if(!visitor.getVistorInTime().equalsIgnoreCase("")){
            visitorsList.add(visitor);
        }/*else if(user.getRole().equalsIgnoreCase("R")){
            visitorsList.add(visitor);
        }*/


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

        adapter.notifyDataSetChanged();

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position

            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public VisitorsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitorsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VisitorsListFragment newInstance(String param1, String param2) {
        VisitorsListFragment fragment = new VisitorsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

}
