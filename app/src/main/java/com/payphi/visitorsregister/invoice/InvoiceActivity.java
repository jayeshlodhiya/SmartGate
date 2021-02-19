package com.payphi.visitorsregister.invoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.payphi.visitorsregister.CreatePoll;
import com.payphi.visitorsregister.PollListFragment;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.VotePollFragment;
import com.payphi.visitorsregister.createpollfrgament;
import com.payphi.visitorsregister.model.Society;
import com.payphi.visitorsregister.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    RelativeLayout tablayout;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    User user;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "sosessionPref";
    InvoiceFragment invoiceFragment;
    InvoiceListFragment invoiceListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        tabLayout = (TabLayout) findViewById(R.id.regtabsId);
        viewPager = (ViewPager) findViewById(R.id.regviewpager);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        GetUserData();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        invoiceFragment = new InvoiceFragment();
        invoiceListFragment = new InvoiceListFragment();
        //vendorListFragment = new VendorListFragment();
        final Bundle args = new Bundle();
        args.putString("TAG", "createPoll");
        invoiceFragment.setArguments(args);

        /*if (user.getRole().equals("S")) {

            adapter.addFrag(vendorFragment, "Add Vendors");
            adapter.addFrag(vendorListFragment, "Vendors");

        } else if (user.getRole().equals("R")) {
            adapter.addFrag(vendorFragment, "Add Vendors");
            adapter.addFrag(vendorFragment, "Vendors");

        } else if (user.getRole().equals("A")) {
            adapter.addFrag(vendorFragment, "Add Vendors");
            //adapter.addFrag(registerFragment, "New Visitor");
            adapter.addFrag(vendorFragment, "Vendors");

        }*/
        adapter.addFrag(invoiceFragment, "Invoice");
        adapter.addFrag(invoiceListFragment, "Invoice List");
        //    adapter.addFrag(vendorListFragment, "Vendors");


    /*    visitorsListFragment = new VisitorsListFragment();
        adapter.addFrag(visitorsListFragment, "Register");
*/
        //adapter.addFrag(new TransactionListFragment(), "Transactions");
        viewPager.setAdapter(adapter);
    }

    private void GetUserData() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
try {
    if (resultCode == RESULT_CANCELED) {

        showNotificationForActivityResult(resultCode);
    } else if (resultCode == 2 || resultCode == 4) {

        showNotificationForActivityResult(resultCode);
    } else if (resultCode == 3) {

        showNotificationForActivityResult(resultCode);
    }else {
        if(data!=null){
            Bundle bundle = data.getExtras();
            bundle.putString("UserName", user.getFullName());
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            bundle.putString("TransDate", String.valueOf(dateFormat.format(date)));

            invoiceFragment.SaveTrans(bundle);

        }
            }
}catch (Exception e){
    e.printStackTrace();
}
      }

    private void showNotificationForActivityResult(int resultCode) {
        if(resultCode==0){
            Toast.makeText(this,"Transaction cancelled!!",Toast.LENGTH_LONG).show();
        }

    }
}
