package com.payphi.visitorsregister;

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
import com.payphi.visitorsregister.adapters.MemberListAdapter;
import com.payphi.visitorsregister.adapters.PollListAdapter;
import com.payphi.visitorsregister.model.Poll;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.recyclerview.ChatAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PollListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PollListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PollListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private MemberListAdapter memberListAdapter;
    PollListAdapter pollListAdapter;
    private TextView tv_selection;
    ArrayList<Poll> pollArrayList;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    String PREF_NAME = "sosessionPref";
    String societycode = "";
    private OnFragmentInteractionListener mListener;

    public PollListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PollListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PollListFragment newInstance(String param1, String param2) {
        PollListFragment fragment = new PollListFragment();
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
        View view = inflater.inflate(R.layout.fragment_poll_list, container, false);
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        societycode = sharedPreferences.getString("SOC_CODE", null);
        tv_selection = (TextView) view.findViewById(R.id.member_selection);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.memberListrecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //  mAdapter = new ChatAdapter(getContext(), setData(), this);
        pollArrayList = new ArrayList<>();
        pollListAdapter = new PollListAdapter(getContext(), pollArrayList, pollListAdapter);
        mRecyclerView.setAdapter(pollListAdapter);
        GetAllPolls();
        return view;
    }

    private void GetAllPolls() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(societycode).document("Polls").collection("SPolls");
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();
                if (pollArrayList.size() > 0) {
                    pollArrayList.removeAll(pollArrayList);
                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                       adapter.notifyDataSetChanged();
*/
                }
                if (size == 0) {
                    pollArrayList.removeAll(pollArrayList);
                    pollListAdapter.notifyDataSetChanged();
                }


                System.out.println("Documents notification size=" + size);


                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                    CreatePollList(documentSnapshot);
                }
            }
        });

    }

    private void CreatePollList(DocumentSnapshot documentSnapshot) {
    Poll poll= new Poll();
    poll.setQuestion(documentSnapshot.getString("Que"));
    poll.setName(documentSnapshot.getString("CreateBy"));
    poll.setOptions(documentSnapshot.getString("options"));
    poll.setDocId(documentSnapshot.getString("DocId"));
    poll.setDate(documentSnapshot.getString("Date"));
    System.out.println("User Pic===="+documentSnapshot.getString("pic"));
    poll.setUserPic(documentSnapshot.getString("pic"));

    pollArrayList.add(poll);
    pollListAdapter.notifyDataSetChanged();

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
