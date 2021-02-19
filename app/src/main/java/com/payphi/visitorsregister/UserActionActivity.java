package com.payphi.visitorsregister;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Locale;

public class UserActionActivity extends AppCompatActivity {
    ImageView gicon,callicon ;
    String location;
    String mobileNo;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_action);
        gicon = (ImageView) findViewById(R.id.googleiconId);
        callicon =(ImageView) findViewById(R.id.callid);
         intent = getIntent();

        gicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGoogleMap();
            }
        });
        callicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallUser();
            }
        });
    }

    private void CallUser() {
        if(intent.getSerializableExtra("MobileNo")!=null){
            mobileNo = intent.getSerializableExtra("MobileNo").toString();
            Intent intent = new Intent(Intent.ACTION_CALL);

            intent.setData(Uri.parse("tel:" + mobileNo));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
          startActivity(intent);
        }
    }

    private void OpenGoogleMap() {
        if(intent.getSerializableExtra("location")!=null){
            location = intent.getSerializableExtra("location").toString();
            String arr[] = location.split(",");
            String strUri = "http://maps.google.com/maps?q=loc:"+location;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

            startActivity(intent);
           // finish();
        }
    }
}
