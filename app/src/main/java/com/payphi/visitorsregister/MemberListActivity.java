package com.payphi.visitorsregister;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MemberListActivity extends AppCompatActivity {
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;
    public static MemberListActivity memberListActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        memberListActivity =  this;
        Fragment fragment = new MemberListFragment();
        //  fragment.setArguments(bundle);
        attachPaymentOptionFrgment(fragment);

    }

    private void attachPaymentOptionFrgment(Fragment fragment) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragId, fragment);
        mFragmentTransaction.commit();
    }
    public static MemberListActivity getInstance(){
        return memberListActivity;
    }
    public void FinishGoBack(View view){
        finish();
    }


}
