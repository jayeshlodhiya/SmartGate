package com.payphi.visitorsregister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.payphi.visitorsregister.model.Poll;
import com.payphi.visitorsregister.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.payphi.visitorsregister.ProfieCompleteActivity.paydialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VotePollFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VotePollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VotePollFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Poll poll;
    TextView questextView;
    private OnFragmentInteractionListener mListener;
    private Spinner  opitions;
    Button button;
    private int selected_position;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "sosessionPref";
    User user;
    String securitycode = "";
    String selectedOpt;
    public Fragment currentFragment;
    private FragmentTransaction mFragmentTransaction;
    public FragmentManager mFragmentManager;
    public ProgressDialog paydialog; // this = YourActivity
    Bundle bundle;
    TextView weltextView;
    Button resButton;
    LinearLayout my_layout;
    public VotePollFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VotePollFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VotePollFragment newInstance(String param1, String param2) {
        VotePollFragment fragment = new VotePollFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void Votingstatus(String stat){
        System.out.println("Voting status="+stat);
        if(stat!=null && stat.equals("true")){
           // AttachResultFragment();
            EnableResult();
        }

    }

    private void EnableResult() {
        weltextView.setVisibility(View.VISIBLE);
        resButton.setVisibility(View.VISIBLE);
        my_layout.setVisibility(View.GONE);
        button.setVisibility(View.GONE);

    }

    private void AttachResultFragment() {
        PollResultFragment resultFragment = new PollResultFragment();

        resultFragment.setArguments(bundle);
        attachFrgment(resultFragment);
    }

    private void attachFrgment(PollResultFragment fragment) {

        try {
            this.currentFragment = fragment;
            mFragmentManager = getFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragId, fragment);
            mFragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void GetUserData() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
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
        View view =inflater.inflate(R.layout.fragment_vote_poll, container, false);
         bundle = this.getArguments();
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        securitycode = sharedPreferences.getString("SOC_CODE", null);
        paydialog = new ProgressDialog(getContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);
        if (bundle != null) {

            if (bundle.getSerializable("PollObj") != null) {
                poll =  (Poll)bundle.getSerializable("PollObj");
                System.out.println("Options in fragment="+poll.getOptions());
            }
        }

        questextView = (TextView)view.findViewById(R.id.pollqueId);
        questextView.setText(poll.getQuestion());
        opitions = (Spinner) view.findViewById(R.id.spinner2);
        button = (Button) view.findViewById(R.id.subButtonId);
        weltextView = (TextView) view.findViewById(R.id.textId);
        weltextView.setVisibility(View.INVISIBLE);

        resButton = (Button) view.findViewById(R.id.resButtonId);
        resButton.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveVote();
            }
        });

        resButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowResult();
            }
        });
        List<String> list = new ArrayList<String>();
        if(poll.getOptions().contains(",")){
            String arr[] = poll.getOptions().split(",");
            for(int i=0;i<arr.length;i++){
                list.add(arr[i]);
            }
        }

       /* list.add("list 1");
        list.add("list 2");
        list.add("list 3");*/
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        opitions.setAdapter(dataAdapter);
        opitions.setOnItemSelectedListener(new CustomOnItemSelectedListener());

         my_layout = (LinearLayout)view.findViewById(R.id.my_layout);
      final  ArrayList<CheckBox> mCheckBoxes = new ArrayList<CheckBox>();
        for (int i = 0; i < list.size(); i++)
        {
            TableRow row =new TableRow(getContext());
            row.setId(i);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

            int leftMargin=10;
            int topMargin=2;
            int rightMargin=10;
            int bottomMargin=2;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            row.setLayoutParams(tableRowParams);
            CheckBox checkBox = new CheckBox(getContext());
            //checkBox.setOnCheckedChangeListener();
            checkBox.setId(i);
            checkBox.setText(list.get(i));
            row.addView(checkBox);
            mCheckBoxes.add(checkBox);
            checkBox.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick (View view) {

                    if (((CheckBox) view).isChecked())
                    {
                        for (int i = 0; i < mCheckBoxes.size(); i++) {
                            if (mCheckBoxes.get(i) == view){
                                selected_position = i;
                                System.out.println("Selected item="+mCheckBoxes.get(i).getText().toString());
                                selectedOpt = mCheckBoxes.get(i).getText().toString();
                            }
                            else {
                                mCheckBoxes.get(i).setChecked(false);
                            }

                        }

                    }
                    else
                    {
                        selected_position=-1;
                    }



                }

            });
            my_layout.addView(row);
        }
        GetUserData();
        return view;
    }

    private void ShowResult() {
        AttachResultFragment();
        }

    private void SaveVote() {
        try {
            Map<String, Object> pollMap;
            sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            Random rnd = new Random();
            DocumentReference bookingref = FirebaseFirestore.getInstance().collection(securitycode).document("Polls").collection("SPolls").document(poll.getDocId()).collection("voters").document();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            pollMap = new HashMap<String, Object>();

            String docId = bookingref.getId();
            pollMap.put("Name", user.getFullName());
            pollMap.put("DocId", docId);
            pollMap.put("votedfor", selectedOpt);
            pollMap.put("Date", String.valueOf(onlydate.format(date)));


            paydialog.show();

            bookingref.set(pollMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Voting done!!", Toast.LENGTH_LONG).show();

                    // ClearData();
                    // TakeDeviceIdByFlatNoOrName();
                    //GetVisitorFeatures();
                    //getImageStringFromVisitNumber(docId);
                    paydialog.dismiss();
                    getActivity().finish();
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
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }
}
