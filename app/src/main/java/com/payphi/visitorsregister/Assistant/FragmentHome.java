/*
package com.payphi.visitorsregister.Assistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.recyclerview.Chat;
import com.payphi.visitorsregister.recyclerview.ChatAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentHome extends Fragment implements ChatAdapter.ViewHolder.ClickListener {
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private TextView tv_selection;
    ArrayList<User> userArrayList;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    String PREF_NAME = "sosessionPref";
    String societycode = "";
    public FragmentHome() {
        setHasOptionsMenu(true);
    }

    public void onCreate(Bundle a) {
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null, false);

       */
/* getActivity().supportInvalidateOptionsMenu();
        ((MainActivitychat)getActivity()).changeTitle(R.id.toolbar, "Messages");*//*

        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        societycode = sharedPreferences.getString("SOC_CODE", null);
        tv_selection = (TextView) view.findViewById(R.id.tv_selection);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ChatAdapter(getContext(), setData(), this);
        mRecyclerView.setAdapter(mAdapter);
        //GetUserData();
        userArrayList = new ArrayList<>();
        GetAllMembers();
        return view;
    }

   */
/* private void GetUserData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }*//*


    private void GetAllMembers() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(societycode).document("UserDoc").collection("Sousers");

        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();
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
        userArrayList.add(user);

    }

    public List<Chat> setData() {
        List<Chat> data = new ArrayList<>();
        String name[] = {"Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris", "Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris"};
        String lastchat[] = {"Hi Laura Owens", "Hi there how are you", "Can we meet?", "Ow this awesome", "How are you?", "Ow this awesome", "How are you?", "Ow this awesome", "How are you?", "How are you?"};
        @DrawableRes int img[] = {R.drawable.userpic, R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4, R.drawable.userpic, R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4};
        boolean online[] = {true, false, true, false, true, true, true, false, false, true};

        for (int i = 0; i < 10; i++) {
            Chat chat = new Chat();
            chat.setmTime("5:04pm");
            chat.setName(name[i]);
            chat.setImage(img[i]);
            chat.setOnline(online[i]);
            chat.setLastChat(lastchat[i]);
            data.add(chat);
        }
        return data;
    }

    @Override
    public void onItemClicked(int position) {
        startActivity(new Intent(getActivity(), Conversation.class));
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        if (mAdapter.getSelectedItemCount() > 0) {
            tv_selection.setVisibility(View.VISIBLE);
        } else
            tv_selection.setVisibility(View.GONE);


        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                tv_selection.setText("Delete (" + mAdapter.getSelectedItemCount() + ")");
            }
        });

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit, menu);
    }
}

*/
