package com.payphi.visitorsregister.ChatAssistant;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.payphi.visitorsregister.R;

/**
 * Created by Abhishek on 17/4/18.
 */

public class chat_rec extends RecyclerView.ViewHolder  {



    TextView leftText,rightText;

    public chat_rec(View itemView){
        super(itemView);

        leftText = (TextView)itemView.findViewById(R.id.leftText);
        rightText = (TextView)itemView.findViewById(R.id.rightText);


    }
}
