package com.payphi.visitorsregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.payphi.visitorsregister.Auth.AuthActivity;
import com.payphi.visitorsregister.adapters.SlideAdapter;

/**
 * Created by swapnil.g on 6/21/2018.
 */
public class IntroSlider extends AppCompatActivity {
    private ViewPager viewPager;
    private SlideAdapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_slider);
        viewPager = (ViewPager) findViewById(R.id.introviewpager);
        myadapter = new SlideAdapter(this);
        viewPager.setAdapter(myadapter);

    }

    public void GotoAuthActivity(View view) {
        Intent intent = new Intent(IntroSlider.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
