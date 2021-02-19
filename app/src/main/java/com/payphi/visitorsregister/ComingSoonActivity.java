package com.payphi.visitorsregister;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ComingSoonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
    }
    public void FinishGoBack(View view){
        finish();
    }
}

