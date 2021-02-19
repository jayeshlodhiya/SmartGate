package com.payphi.visitorsregister;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.payphi.visitorsregister.FirebaseNotification.LocalService;

import java.util.Locale;

/**
 * Created by swapnil.g on 1/25/2018.
 */
public class Splashscreen extends Activity {
    LinearLayout rootFrame;
    Thread splashTread;
    ImageView imageView;
    String location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        //imageView = (ImageView) findViewById(R.id.imgView_logo);
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        rootFrame = (LinearLayout) findViewById(R.id.lin_lay);
       /* int[] ids = new int[]{R.drawable.s_img,R.drawable.s_image_black, R.drawable.s_image_black2};
        Random randomGenerator = new Random();
        int r= randomGenerator.nextInt(ids.length);
        this.imageView.setImageDrawable(getResources().getDrawable(ids[r]));
*/

       Intent intent = getIntent();
       if(intent.getSerializableExtra("location")!=null){
           location = intent.getSerializableExtra("location").toString();
           String arr[] = location.split(",");
           String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.parseDouble(arr[0]),Double.parseDouble(arr[1]));
           Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
           startActivity(intent1);
           finish();
       }

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
      //  imageView.startAnimation(animation);
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    CheckForLoggedIn();

                    //               startRevealAnimation();

                    //      Intent intent = new Intent(Splashscreen.this, AuthActivity.class);
                    //    startActivity(intent);
                    //  Splashscreen.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // do nothing
                } finally {
                    //Splashscreen.this.finish();
                }

            }
        };
        splashTread.start();
    }

    private void CheckForLoggedIn() {
        System.out.println("Inside CheckForLoggedIn");
        String PREF_NAME = "sosessionPref";
        int PRIVATE_MODE = 0;
        SharedPreferences sharedPreferences;
        Intent intent = null;
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        System.out.println("Is loged in==" + sharedPreferences.getBoolean("IS_LOGGED_IN", false));
        if (sharedPreferences.getBoolean("IS_LOGGED_IN", false) == true && sharedPreferences.getBoolean("IS_PROFILE_COMP", false == true)) {
            //   showProgressDialog();
            intent = new Intent(Splashscreen.this, HomeDashboard.class);
            //  intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        } else if (sharedPreferences.getBoolean("IS_LOGGED_IN", false) == false) {
            //     intent = new Intent(Splashscreen.this, AuthActivity.class);
            //   intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            intent = new Intent(Splashscreen.this, IntroSlider.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        } else if (sharedPreferences.getBoolean("IS_PROFILE_COMP", false) == false) {
            //CheckUserExits();
            intent = new Intent(Splashscreen.this, ProfieCompleteActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }

        startActivity(intent);
        Splashscreen.this.finish();

    }


}
