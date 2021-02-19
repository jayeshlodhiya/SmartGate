package com.payphi.visitorsregister.invoice;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.model.Poll;
import com.payphi.visitorsregister.model.Transaction;

public class TransDetailsActivity extends AppCompatActivity {

    Transaction transaction;
    TextView name,amount,txn,date,remark;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getExtras().get("TranObj")!=null){
            transaction = (Transaction) getIntent().getSerializableExtra("TranObj");;

        }

        name = (TextView) findViewById(R.id.nameId);
        amount =(TextView) findViewById(R.id.amnId);
        txn =(TextView) findViewById(R.id.txnId);
        date =(TextView) findViewById(R.id.dateId);
        remark =(TextView) findViewById(R.id.remarkId);
        img = (ImageView) findViewById(R.id.statusImageId);


        name.setText(transaction.getName());
        amount.setText(transaction.getAmt()+" Rs");
        txn.setText(transaction.getTransId());
        date.setText(transaction.getDate());

        if (transaction.getRespCode().equals("000") || transaction.getRespCode().equals("0000") ) {
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/test-184bf.appspot.com/o/approved.jpg?alt=media&token=60fb68b2-af6d-4c60-8d64-a2055a6064b2")
                    .into(img);
        } else {
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/test-184bf.appspot.com/o/reject.png?alt=media&token=20bd8ee6-ada9-411a-b3aa-04571225763e")
                    .into(img);
        }

    }

}
