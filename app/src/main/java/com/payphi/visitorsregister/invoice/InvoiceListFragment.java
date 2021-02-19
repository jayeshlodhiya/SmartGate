package com.payphi.visitorsregister.invoice;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.payphi.visitorsregister.PollListFragment;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.adapters.MemberListAdapter;
import com.payphi.visitorsregister.adapters.PollListAdapter;
import com.payphi.visitorsregister.adapters.TransactionAdapter;
import com.payphi.visitorsregister.model.ChatMessage;
import com.payphi.visitorsregister.model.Poll;
import com.payphi.visitorsregister.model.Transaction;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.recyclerview.ChatAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvoiceListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvoiceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvoiceListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private TransactionAdapter transactionAdapter;
    ArrayList<Transaction> transactionArrayList;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    String PREF_NAME = "sosessionPref";
    String societycode = "";
    User user;



    public InvoiceListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InvoiceListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InvoiceListFragment newInstance(String param1, String param2) {
        InvoiceListFragment fragment = new InvoiceListFragment();
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
        View view = inflater.inflate(R.layout.fragment_invoice_list, container, false);

        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        societycode = sharedPreferences.getString("SOC_CODE", null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.memberListrecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //  mAdapter = new ChatAdapter(getContext(), setData(), this);
        transactionArrayList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(getContext(), transactionArrayList, transactionAdapter);
        mRecyclerView.setAdapter(transactionAdapter);
        GetUserData();
        GetAllTransaction();


        return view;
    }
    private void GetUserData() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    private void GetAllTransaction() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(societycode).document("Tranlog").collection("STranlog");
        docRef.whereEqualTo("UserName",user.getFullName()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();
                if (transactionArrayList.size() > 0) {
                    transactionArrayList.removeAll(transactionArrayList);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                }
                if (size == 0) {
                    transactionArrayList.removeAll(transactionArrayList);
                    transactionAdapter.notifyDataSetChanged();
                }


                System.out.println("Documents notification size=" + size);


                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                    CreateTranList(documentSnapshot);
                }
            }
        });

    }

    private void CreateTranList(DocumentSnapshot documentSnapshot) {
        try {
            Transaction transaction = new Transaction();
            transaction.setAmt(documentSnapshot.getString("Amount"));
            transaction.setDate(documentSnapshot.getString("TransDate"));
            transaction.setRespCode(documentSnapshot.getString("responseCode"));
            transaction.setName(documentSnapshot.getString("UserName"));
            transaction.setTransId(documentSnapshot.getString("txnID"));
            transactionArrayList.add(transaction);
            Collections.sort(transactionArrayList, new Comparator<Transaction>() {
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                public int compare(Transaction o1, Transaction o2) {
                    //return o1.getDateTime().compareTo(o2.getDateTime());
                    try {
                        return df.parse(o2.getDate()).compareTo(df.parse(o1.getDate()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });

            transactionAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
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
