package com.payphi.visitorsregister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.payphi.visitorsregister.model.Poll;
import com.payphi.visitorsregister.model.User;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PollResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PollResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PollResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "sosessionPref";
    User user;
    String securitycode = "";
    public ProgressDialog paydialog; // this = YourActivity
    Bundle bundle;
    Poll poll;
    CollectionReference docRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    BarChart barChart;
    PieChart pieChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> barEntryLabels;
    ArrayList<Entry> yvalues = new ArrayList<Entry>();
    int localcount=0;
    int docSize;
    ArrayList<String> xVals = new ArrayList<String>();
    ArrayList<String> optValues = new ArrayList<>();
    public PollResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PollResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PollResultFragment newInstance(String param1, String param2) {
        PollResultFragment fragment = new PollResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

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
        View view = inflater.inflate(R.layout.fragment_poll_result, container, false);
        bundle = this.getArguments();
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        securitycode = sharedPreferences.getString("SOC_CODE", null);
        paydialog = new ProgressDialog(getContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);
        TextView textView = (TextView)view.findViewById(R.id.qId);
        pieChart = (PieChart) view.findViewById(R.id.pollPieChart);
        ImageView refresImg = (ImageView) view.findViewById(R.id.refreshId);
        refresImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh();
            }
        });
        pieChart.setDescription("Sales by employee (In Thousands $) ");
        pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);

        pieChart.setCenterTextSize(10);
        pieChart.animateXY(1400, 1400);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {


            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;
                Log.i("VAL SELECTED",
                        "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                                + ", DataSet index: " + dataSetIndex);
            }

            @Override
            public void onNothingSelected() {
                Log.i("PieChart", "nothing selected");

            }
        });
        if (bundle != null) {
            if (bundle.getSerializable("PollObj") != null) {
                poll =  (Poll)bundle.getSerializable("PollObj");
                System.out.println("Options in  result fragment===="+poll.getOptions());
                textView.setText(poll.getQuestion());

            }
        }
        CheckForVotingStatus();
        return view;
    }

    private void Refresh() {
        System.out.println("In Refresh......");
        localcount=0;

        xVals.removeAll(xVals);
        yvalues.removeAll(yvalues);
        optValues.removeAll(optValues);
        CheckForVotingStatus();
        Fragment fragment =  getFragmentManager().findFragmentById(R.id.fragId);
        if (fragment instanceof PollResultFragment) {
            // do something with f
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(fragment);
            ft.attach(fragment);
            ft.commit();
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


    private void CheckForVotingStatus() {
        try {
    //        paydialog.show();
            System.out.println("DocId=="+poll.getDocId());
            docRef = db.collection(securitycode).document("Polls").collection("SPolls").document(poll.getDocId()).collection("voters");
            //.collection(societycode).document("Polls").collection("SPolls");
            docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    docSize = size;
                    System.out.println("Documents size====" + size);
                    //System.out.println("bookingsList size="+visitorsList.size());

                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        PrepareGraph(documentSnapshot);
                        localcount = localcount+1;
                    }
                    if(localcount==docSize){
                        addDataSet();
                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void addDataSet() {
try {
    System.out.println("Voted for="+optValues);
    xVals = new ArrayList<String>();
    for (int i = 0; i < optValues.size(); i++) {
        String val = optValues.get(i);
        int noofOcc = Collections.frequency(optValues, val);

           if(xVals!=null && !xVals.contains(val)){
       //do nothing
               yvalues.add(new Entry(noofOcc, i));
               xVals.add(val);
           }else{
               //xVals.remove(val);
                //xVals.add(val);
           }

     //   xVals.add(val);
    }

    //   yvalues.add(new Entry(1, 0));
    //  yvalues.add(new Entry(4, 1));
    // yvalues.add(new Entry(3, 2));
        /*yvalues.add(new Entry(25f, 3));
        yvalues.add(new Entry(23f, 4));
        yvalues.add(new Entry(17f, 5));*/

    PieDataSet dataSet = new PieDataSet(yvalues, "");




    //  xVals.add("1 BHK");
    // xVals.add("2 BHK");
    //xVals.add("3 BHK");
    //xVals.add("4 BHK");


    PieData data = new PieData(xVals, dataSet);

    // data.setValueFormatter(new DefaultValueFormatter(0));
    // Default value
    //data.setValueFormatter(new DefaultValueFormatter(0));
    pieChart.setData(data);
    pieChart.setCenterText("Total " + docSize + " Votings");
    pieChart.setDescription("");
    pieChart.setDrawHoleEnabled(true);
    pieChart.setTransparentCircleRadius(38f);

    pieChart.setHoleRadius(30f);
    dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

    data.setValueTextSize(13f);
    data.setValueTextColor(Color.DKGRAY);

//for bar chart

}catch (Exception e){
    e.printStackTrace();
}
    }

    private void PrepareGraph(DocumentSnapshot documentSnapshot) {
        optValues.add(documentSnapshot.getString("votedfor"));
    }
}
