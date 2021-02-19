
package com.payphi.visitorsregister.Beakon;






import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guo.duoduo.randomtextview.RandomTextView;
import com.payphi.visitorsregister.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class BeaconActivity extends AppCompatActivity implements BeaconConsumer {

    public static final String TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager;
    TextView addr, regionin, regionout;
    RandomTextView randomTextView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        addr = (TextView) findViewById(R.id.distance);
        regionin = (TextView) findViewById(R.id.enterregion);
        regionout = (TextView) findViewById(R.id.exitregion);
        randomTextView = (RandomTextView) findViewById(
                R.id.random_textview);
        imageView = (ImageView) findViewById(R.id.imageid);
        imageView.setVisibility(View.INVISIBLE);
        beaconManager = BeaconManager.getInstanceForApplication(this);


beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));


        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.bind(this);
        SearchAmin();

    }

    private void SearchAmin() {


        randomTextView.setOnRippleViewClickListener(
                new RandomTextView.OnRippleViewClickListener() {
                    @Override
                    public void onRippleViewClicked(View view) {

                        // startActivity(new Intent(this, RefreshProgressActivity.class));
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //         randomTextView.addKeyWord("彭丽媛");
                //       randomTextView.addKeyWord("习近平");
                randomTextView.show();
            }
        }, 2 * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("myBeaons", null, null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    //  regionin.setText("didEnterRegion");
                    //  Toast.makeText(getApplicationContext(),"didEnterRegion",Toast.LENGTH_LONG).show();
                    showProgress("didEnterRegion", regionin);
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    //regionout.setText("didExitRegion");
                    //Toast.makeText(getApplicationContext(),"didExitRegion",Toast.LENGTH_LONG).show();
                    showProgress("didExitRegion", regionout);
                    beaconManager.stopRangingBeaconsInRegion(region);
                    hideResults();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon oneBeacon : beacons) {

                    System.out.println("Beacon=" + oneBeacon.getParserIdentifier());
                    Log.d(TAG, "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
                    String dis = "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3();
                    showProgress(dis, addr);

                    randomTextView.addKeyWord(String.valueOf(oneBeacon.getId1()));

                    // Toast.makeText(getApplicationContext(),dis,Toast.LENGTH_LONG).show();
                    //addr.setText(dis);
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.menu_beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

  if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    protected void runOnUiThreadIfNotDestroyed(final Runnable runnable) {
        runOnUiThread(runnable);
    }

    protected void showProgress(final String message, final TextView textid) {
        runOnUiThreadIfNotDestroyed(new Runnable() {
            @Override
            public void run() {
                // view.findViewById(R.id.progress_section).setVisibility(View.VISIBLE);
                //  view.findViewById(R.id.progress).setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
                textid.setText(message);
                imageView.setVisibility(View.VISIBLE);
                // view.findViewById(R.id.signature_section).setVisibility(View.GONE);
            }
        });
    }

    protected void hideResults() {
        runOnUiThreadIfNotDestroyed(new Runnable() {
            @Override
            public void run() {
                // view.findViewById(R.id.progress_section).setVisibility(View.VISIBLE);
                //  view.findViewById(R.id.progress).setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);

//                randomTextView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                // view.findViewById(R.id.signature_section).setVisibility(View.GONE);
            }
        });
    }
}

