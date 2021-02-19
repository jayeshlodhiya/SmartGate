package com.payphi.visitorsregister;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class UserLocation extends Activity {
    //jfbsjhdfb
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

       if(getIntent().getSerializableExtra("location")!=null){
            location =String.valueOf(getIntent().getSerializableExtra("location"));

       }

        OpenMap();
    }

    private void OpenMap() {
        String destlocaarray[]=location.split(",");
    /*    String uri = "http://maps.google.com/maps?&daddr=" + destlocaarray[0] + "," + destlocaarray[1];
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setComponent(new ComponentName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"));

        startActivity(intent);*/
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+destlocaarray[0]+","+destlocaarray[1]);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
