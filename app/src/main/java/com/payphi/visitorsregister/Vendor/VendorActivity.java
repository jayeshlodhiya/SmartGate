package com.payphi.visitorsregister.Vendor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.payphi.visitorsregister.GenerateTicketFragment;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.VisitorsListFragment;
import com.payphi.visitorsregister.model.User;

import java.util.ArrayList;
import java.util.List;

public class VendorActivity extends AppCompatActivity {
    RelativeLayout tablayout;
    private ViewPager viewPager;
    private VendorAddFragment vendorFragment;
    private VendorListFragment vendorListFragment;
    private GenerateTicketFragment generateTicketFragment;
    private VisitorsListFragment visitorsListFragment;
    private TabLayout tabLayout;
    ImageView scanView;
    User user;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "sosessionPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);
        tabLayout = (TabLayout) findViewById(R.id.vendortabsId);
        viewPager = (ViewPager) findViewById(R.id.vendorviewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.vendortabsId);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        GetUserData();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        vendorFragment = new VendorAddFragment();
        vendorListFragment = new VendorListFragment();
        final Bundle args = new Bundle();
        args.putString("TAG", "registerFragment");
        vendorFragment.setArguments(args);

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
        adapter.addFrag(vendorFragment, "Add Vendors");
        //adapter.addFrag(registerFragment, "New Visitor");
        adapter.addFrag(vendorListFragment, "Vendors");


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
}
