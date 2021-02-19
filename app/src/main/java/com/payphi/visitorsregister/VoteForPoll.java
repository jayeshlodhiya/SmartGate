package com.payphi.visitorsregister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.payphi.visitorsregister.model.Poll;
import com.payphi.visitorsregister.model.User;

public class VoteForPoll extends AppCompatActivity {
    Bundle bundle;
    public Fragment currentFragment;
    private FragmentTransaction mFragmentTransaction;
    public FragmentManager mFragmentManager;
    CollectionReference docRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    String socityCode;
    User user;
    Poll poll;
    public ProgressDialog paydialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_for_poll);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        sharedPreferences = getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        socityCode = sharedPreferences.getString("SOC_CODE", null);
         bundle = new Bundle();
        if(getIntent().getExtras().get("PollObj")!=null){
             poll = (Poll) getIntent().getSerializableExtra("PollObj");;
            System.out.println("Options in activity="+poll.getOptions());
            bundle.putSerializable("PollObj", poll);
        }
        paydialog = new ProgressDialog(this);
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);
        GetUser();
        AttachFragment();
        CheckForVotingStatus();



    }

    private void GetUser() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    private void CheckForVotingStatus() {
        try {
            paydialog.show();
            docRef = db.collection(socityCode).document("Polls").collection("SPolls").document(poll.getDocId()).collection("voters");
            //.collection(societycode).document("Polls").collection("SPolls");
            docRef.whereEqualTo("Name", user.getFullName()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    System.out.println("Document size=="+size);
                    paydialog.dismiss();
                    if (size > 0) {
                        Fragment fragment =  getSupportFragmentManager().findFragmentById(R.id.fragId);
                        if (fragment instanceof VotePollFragment) {
                            // do something with f
                            ((VotePollFragment) fragment).Votingstatus("true");
                        }

                    } else {
                        Fragment fragment =  getSupportFragmentManager().findFragmentById(R.id.fragId);
                        if (fragment instanceof VotePollFragment) {
                            // do something with f
                            ((VotePollFragment) fragment).Votingstatus("false");
                        }

                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void AttachResultFragment() {
        ResultFragment resultFragment = new ResultFragment();
        resultFragment.setArguments(bundle);
        attachFrgment(resultFragment);
    }

    private void AttachFragment() {

        VotePollFragment fragment = new VotePollFragment();
        fragment.setArguments(bundle);
        attachFrgment(fragment);
        }

    private void attachFrgment(ResultFragment fragment) {

        try {
            this.currentFragment = fragment;
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragId, fragment);
            mFragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void attachFrgment(VotePollFragment fragment) {

        this.currentFragment=fragment;
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragId, fragment);
        mFragmentTransaction.commit();
    }

}
