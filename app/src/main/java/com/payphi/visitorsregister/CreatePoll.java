package com.payphi.visitorsregister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.payphi.visitorsregister.Vendor.VendorActivity;
import com.payphi.visitorsregister.Vendor.VendorAddFragment;
import com.payphi.visitorsregister.Vendor.VendorListFragment;
import com.payphi.visitorsregister.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreatePoll extends AppCompatActivity implements RecognitionListener {
    RelativeLayout tablayout;
    private ViewPager viewPager;
    private VendorAddFragment vendorFragment;
    private VendorListFragment vendorListFragment;
    private createpollfrgament createpollfrgament;
    private GenerateTicketFragment generateTicketFragment;
    private VisitorsListFragment visitorsListFragment;
    private TabLayout tabLayout;
    PollListFragment pollListFragment;
    ImageView scanView;
    User user;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "sosessionPref";
    private SpeechRecognizer speech;
    private Intent recognizerIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poll_activity);
        tabLayout = (TabLayout) findViewById(R.id.regtabsId);
        viewPager = (ViewPager) findViewById(R.id.regviewpager);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
       //startActivity(recognizerIntent);
     //   speech.startListening(recognizerIntent);
        //getSpeechInput();


    }
    public void getSpeechInput() {

       /* Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }*/


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //   txvResult.setText(result.get(0));
                    Toast.makeText(this,result.get(0).toString(),Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    public void FinishGoBack(View view){
        finish();
    }
    private void setupViewPager(ViewPager viewPager) {
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        GetUserData();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        createpollfrgament = new createpollfrgament();
        pollListFragment = new PollListFragment();
        //vendorListFragment = new VendorListFragment();
        final Bundle args = new Bundle();
        args.putString("TAG", "createPoll");
        createpollfrgament.setArguments(args);

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
        adapter.addFrag(createpollfrgament, "Create Poll");
        adapter.addFrag(pollListFragment, "Poll List");
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

    @Override
    public void onReadyForSpeech(Bundle params) {
       // Toast.makeText(this,"R",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBeginningOfSpeech() {
        //Toast.makeText(this,"B",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        //Toast.makeText(this,"End",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches) {
            text += result + "\n";
        }
        Toast.makeText(this,matches.get(0),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

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
