package com.payphi.visitorsregister;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.payphi.visitorsregister.adapters.EmergencyAdapter;
import com.payphi.visitorsregister.model.User;

public class EmergencyActivity extends AppCompatActivity {
    RadioGroup radioGroup,radioGroup2;
    RadioButton animalButton,fireButton,medicalButton,liftButton,visitorButton;
  public static  String selectedText="";
    Button button;
    String msg="";
    User user;
    private static final String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;
    GridView gridview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_grid_option);
      //  Toolbar toolbar = findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        GetUserData();
         gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new EmergencyAdapter(this));
        button = (Button) findViewById(R.id.notifybutton);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(selectedText.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Please Select One Option!",Toast.LENGTH_LONG).show();
                }else{
                    if(user!=null && user.getRole().equalsIgnoreCase("S")){
                        msg= "Security Guard "+user.getFullName()+" has raised "+selectedText+" emergency alert!!  ";
                    }else {
                        msg= "Resident"+user.getFullName()+" has raised "+selectedText+" emergency alert!!  ";
                    }

                    HomeDashboard.getInstance().SendAlertToAll(msg);
                }

                return false;
            }
        });

      /*
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        animalButton = (RadioButton) findViewById(R.id.animalTreatId);
        fireButton = (RadioButton) findViewById(R.id.fireTreatId);
        medicalButton =(RadioButton) findViewById(R.id.medicalTreatId);
        liftButton = (RadioButton) findViewById(R.id.liftTreatId);
        visitorButton = (RadioButton) findViewById(R.id.visitorTreatId);

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(selectedText.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Please Select One Option!",Toast.LENGTH_LONG).show();
                }else{
                    if(user!=null && user.getRole().equalsIgnoreCase("S")){
                        msg= "Security Guard "+user.getFullName()+" has raised "+selectedText+" emergency alert!!  ";
                    }else {
                        msg= "Resident"+user.getFullName()+" has raised "+selectedText+" emergency alert!!  ";
                    }

                    HomeDashboard.getInstance().SendAlertToAll(msg);
                }

                return false;
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    selectedText = rb.getTag().toString();
               //     Toast.makeText(getApplicationContext(), rb.getTag().toString(), Toast.LENGTH_SHORT).show();
                  //  status = rb.getText().toString();
                    if(selectedText.equalsIgnoreCase("animal")){
                        animalButton.setBackgroundResource(R.drawable.animaltreaticon);
                        fireButton.setBackgroundResource(R.drawable.firetreat);
                        medicalButton.setBackgroundResource(R.drawable.medicaltreat);
                        liftButton.setBackgroundResource(R.drawable.lifttreat);
                        visitorButton.setBackgroundResource(R.drawable.visitortreat);
                    }else{
                        animalButton.setBackgroundResource(R.drawable.animaltreat);
                    }



                    if(selectedText.equalsIgnoreCase("fire")){
                        animalButton.setBackgroundResource(R.drawable.animaltreat);
                        fireButton.setBackgroundResource(R.drawable.g_firetreat);
                        medicalButton.setBackgroundResource(R.drawable.medicaltreat);
                        liftButton.setBackgroundResource(R.drawable.lifttreat);
                        visitorButton.setBackgroundResource(R.drawable.visitortreat);
                    }else{
                        fireButton.setBackgroundResource(R.drawable.emergency_fire);
                    }

                    if(selectedText.equalsIgnoreCase("medical")){
                        animalButton.setBackgroundResource(R.drawable.animaltreat);
                        fireButton.setBackgroundResource(R.drawable.emergency_fire);
                        medicalButton.setBackgroundResource(R.drawable.emergency_medical);
                        liftButton.setBackgroundResource(R.drawable.lifttreat);
                        visitorButton.setBackgroundResource(R.drawable.visitortreat);
                    }else{
                        medicalButton.setBackgroundResource(R.drawable.medicaltreat);
                    }







                }

            }
        });

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                selectedText = rb.getTag().toString();
             //   Toast.makeText(getApplicationContext(), rb.getTag().toString(), Toast.LENGTH_SHORT).show();
                if (null != rb && checkedId > -1) {
                    if(selectedText.equalsIgnoreCase("visitor")){
                        animalButton.setBackgroundResource(R.drawable.animaltreat);
                        fireButton.setBackgroundResource(R.drawable.emergency_fire);
                        medicalButton.setBackgroundResource(R.drawable.medicaltreat);
                        liftButton.setBackgroundResource(R.drawable.lifttreat);
                        visitorButton.setBackgroundResource(R.drawable.svisitortreat);
                    }else{
                        visitorButton.setBackgroundResource(R.drawable.visitortreat);
                    }

                    if(selectedText.equalsIgnoreCase("lift")){
                        animalButton.setBackgroundResource(R.drawable.animaltreat);
                        fireButton.setBackgroundResource(R.drawable.emergency_fire);
                        medicalButton.setBackgroundResource(R.drawable.medicaltreat);
                        liftButton.setBackgroundResource(R.drawable.slifttreat);
                        visitorButton.setBackgroundResource(R.drawable.visitortreat);
                    }else{
                        liftButton.setBackgroundResource(R.drawable.lifttreat);
                    }

                }
            }
        });
*/
    }
    public void FinishGoBack(View view){
        finish();
    }

    private void GetUserData() {
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

}
