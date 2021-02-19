package com.payphi.visitorsregister;

import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RegisterBook extends AppCompatActivity {
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_book);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Fragment fragment = new VisitorsListFragment();
        //  fragment.setArguments(bundle);
        attachPaymentOptionFrgment(fragment);
    }
    private void attachPaymentOptionFrgment(Fragment fragment) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragId, fragment);
        mFragmentTransaction.commit();
    }
    public void FinishGoBack(View view){
        finish();
    }


}
