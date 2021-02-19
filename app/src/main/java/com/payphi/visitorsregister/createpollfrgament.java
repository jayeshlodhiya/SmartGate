package com.payphi.visitorsregister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.payphi.visitorsregister.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link createpollfrgament.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link createpollfrgament#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createpollfrgament extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public int numberOfLines = 1;
    List<EditText> allEds = new ArrayList<EditText>();
    String[] options;
    View view;
    LinearLayout ll;
    private OnFragmentInteractionListener mListener;
    EditText queeditText;
    private static final String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;
    SharedPreferences sharedPreferences;
    public ProgressDialog paydialog; // this = YourActivity
    User user;
    String securitycode = "";
    public createpollfrgament() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment createpollfrgament.
     */
    // TODO: Rename and change types and number of parameters
    public static createpollfrgament newInstance(String param1, String param2) {
        createpollfrgament fragment = new createpollfrgament();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         View view = inflater.inflate(R.layout.fragment_createpollfrgament, container, false);
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        securitycode = sharedPreferences.getString("SOC_CODE", null);
        paydialog = new ProgressDialog(getContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);
        final Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getAnswer();
            }
        });
         ll = (LinearLayout) view.findViewById(R.id.plinearLayoutDecisions);
        final FloatingActionButton Add_button = (FloatingActionButton) view.findViewById(R.id.fab);
        queeditText = (EditText) view.findViewById(R.id.queEditId);
        Add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Line();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size=allEds.size();
                options = new String[size];

                String que = queeditText.getText().toString();
                if(que==null || que.equals("")){
                    Toast.makeText(getContext(),"Please enter Question!!",Toast.LENGTH_LONG).show();
                    return;
                }else{
                    SavePoll();
                }

            }
        });
        GetUserData();
        return view;
    }
    private void GetUserData() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    private void SavePoll() {
        Map<String, Object> pollMap;
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        Random rnd = new Random();
        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(securitycode).document("Polls").collection("SPolls").document();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        pollMap = new HashMap<String, Object>();
        String vendorNumber = String.valueOf(100000 + rnd.nextInt(900000));

       String opts="";
        GPSTracker gps;
        gps = new GPSTracker(getContext());
        String currentLoction = "";
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            currentLoction = String.valueOf(latitude + "," + longitude);
        }
        pollMap.put("CreateBy",user.getFullName());
        pollMap.put("FlatNo", user.getFlatNo());
        pollMap.put("Date", String.valueOf(onlydate.format(date)));
        pollMap.put("Que", queeditText.getText().toString());
        pollMap.put("pic", user.getPhoto());
        for(int i=0; i < allEds.size(); i++){
            options[i] = allEds.get(i).getText().toString();
            System.out.println(options[i]);
            pollMap.put(options[i],0);
            opts = opts+options[i]+",";
        }

        opts = opts.substring(0, opts.length() - 1);
        String docId = bookingref.getId();
        pollMap.put("options", opts);
        pollMap.put("DocId", docId);

        paydialog.show();

        bookingref.set(pollMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Poll Created!!", Toast.LENGTH_LONG).show();

                RefreshFragment();

               // ClearData();
                // TakeDeviceIdByFlatNoOrName();
                //GetVisitorFeatures();
                //getImageStringFromVisitNumber(docId);
                paydialog.dismiss();
                //LocalNotification();
                // SendNotificationToShop();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error Occured.!", Toast.LENGTH_LONG).show();
                paydialog.dismiss();

            }
        });






    }
    public void RefreshFragment() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    public void Add_Line() {
        try {

            // add edittext
            EditText et = new EditText(getContext());
            et.setBackgroundResource(R.drawable.rounded_edittext);
            View view = new RelativeLayout(getContext());
            allEds.add(et);
            LinearLayout.LayoutParams vi = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 5);

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setLayoutParams(p);
            // et.setText("Text");
            et.setHint("Option " + numberOfLines);
            et.setId(numberOfLines);
            ll.addView(et);
            view.setLayoutParams(vi);
            ll.addView(view);
            numberOfLines++;
        }catch (Exception e){
            e.printStackTrace();
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
