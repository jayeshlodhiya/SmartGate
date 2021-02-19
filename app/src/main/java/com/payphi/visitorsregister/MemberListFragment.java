package com.payphi.visitorsregister;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.payphi.visitorsregister.adapters.MemberListAdapter;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.recyclerview.ChatAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemberListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemberListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private MemberListAdapter memberListAdapter;
    private TextView tv_selection;
    ArrayList<User> userArrayList;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    String PREF_NAME = "sosessionPref";
    String societycode = "";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MemberListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemberListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberListFragment newInstance(String param1, String param2) {
        MemberListFragment fragment = new MemberListFragment();
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
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);

        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        societycode = sharedPreferences.getString("SOC_CODE", null);
        tv_selection = (TextView) view.findViewById(R.id.member_selection);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.memberListrecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //  mAdapter = new ChatAdapter(getContext(), setData(), this);
        userArrayList = new ArrayList<>();
        memberListAdapter = new MemberListAdapter(getContext(), userArrayList, memberListAdapter);
        mRecyclerView.setAdapter(memberListAdapter);
        //GetUserData();
        EditText searchField = (EditText) view.findViewById(R.id.searchmebers);
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
        GetAllMembers();
        return view;
    }

    void filter(String text) {

        ArrayList<User> temp = new ArrayList();
        for (User d : userArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            String name = d.getFullName().toLowerCase();
            //     String date[] = d.getVistorInTime().toLowerCase().split("\\s+");

            String flat = d.getFlatNo().toLowerCase();
            text = text.toLowerCase();
            if (name.startsWith(text)) {
                // Toast.makeText(getContext(),d.getShopName(),Toast.LENGTH_SHORT).show();
                temp.add(d);

                memberListAdapter.updateList(temp);
                memberListAdapter.notifyDataSetChanged();
            }
        }
        //update recyclerview

    }

    private void GetAllMembers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(societycode).document("UserDoc").collection("Sousers");
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();
                if (userArrayList.size() > 0) {
                    userArrayList.removeAll(userArrayList);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                }
                if (size == 0) {
                    userArrayList.removeAll(userArrayList);
                    memberListAdapter.notifyDataSetChanged();
                }


                System.out.println("Documents notification size=" + size);


                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                    CreateMemberList(documentSnapshot);
                }
            }
        });
    }

    private void CreateMemberList(DocumentSnapshot documentSnapshot) {
        User user = new User();
        user.setDocId(String.valueOf(documentSnapshot.get("DocId")));
        user.setFlatNo(String.valueOf(documentSnapshot.get("FlatNumber")));
        user.setPhoto(String.valueOf(documentSnapshot.get("Photo")));

        user.setFullName(String.valueOf(documentSnapshot.get("Name")).toUpperCase());
        user.setWork(String.valueOf(documentSnapshot.get("Work")));
        user.setOnline(String.valueOf(documentSnapshot.get("Online")));
        user.setrType(String.valueOf(documentSnapshot.get("RType")));
        userArrayList.add(user);
        if (userArrayList.size() > 0) {
            Collections.sort(userArrayList, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.getFullName().compareTo(o2.getFullName());
                }


            });
        }

        memberListAdapter.notifyDataSetChanged();

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
