package com.payphi.visitorsregister.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.payphi.visitorsregister.MemberListActivity;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.RegisterFragment;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Visitor;
import com.payphi.visitorsregister.recyclerview.SelectableAdapter;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by swapnil.g on 6/7/2018.
 */
public class MemberListAdapter extends SelectableAdapter<MemberListAdapter.MyViewHolder> {

    private Context mContext;
    MemberListAdapter memberListAdapter;
    private ArrayList<User> memberList;
    Visitor visitor = null;
    User user;
    String distance = "";
    int pos;
    int progress = 0;
    MyViewHolder myViewHolder;
    MyCountDownTimer myCountDownTimer = new MyCountDownTimer(10000, 1000);
    private MyViewHolder.ClickListener clickListener;

    //
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvName, rtype,flatNo;
        public TextView tvTime;
        public TextView tvLastChat, flatnoid;
        public ImageView userPhoto;
        public boolean online = false;
        private final View onlineView;
        public CheckBox checked;
        private ClickListener listener;
        RelativeLayout relativeLayout;

        //private final View selectedOverlay;


        public MyViewHolder(View itemLayoutView, ClickListener listener) {
            super(itemLayoutView);

            this.listener = listener;

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            //selectedOverlay = (View) itemView.findViewById(R.id.selected_overlay);
            tvTime = (TextView) itemLayoutView.findViewById(R.id.tv_time);
            //  tvLastChat = (TextView) itemLayoutView.findViewById(R.id.tv_last_chat);
            userPhoto = (ImageView) itemLayoutView.findViewById(R.id.iv_user_photo);
            onlineView = (View) itemLayoutView.findViewById(R.id.onlineid);
            checked = (CheckBox) itemLayoutView.findViewById(R.id.chk_list);
            flatnoid = (TextView) itemLayoutView.findViewById(R.id.flatnoid);
            rtype = (TextView) itemLayoutView.findViewById(R.id.rtypeId);
            flatNo = (TextView) itemLayoutView.findViewById(R.id.flatNo);
            relativeLayout = (RelativeLayout) itemLayoutView.findViewById(R.id.relativeLayoutId);
            itemLayoutView.setOnClickListener(this);

            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition());
            }
            return false;
        }

        public interface ClickListener {
            public void onItemClicked(int position);

            public boolean onItemLongClicked(int position);

            boolean onCreateOptionsMenu(Menu menu);
        }
    }


    public MemberListAdapter(Context mContext, ArrayList<User> userList, MemberListAdapter adapter) {
        this.mContext = mContext;
        this.memberList = userList;
        this.memberListAdapter = adapter;
    }

    public void updateList(ArrayList<User> list) {
        memberList = list;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);

        return new MyViewHolder(itemView, clickListener);
    }

    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        myViewHolder = holder;
        user = memberList.get(position);
        setAnimation(holder.itemView, position);

        Bitmap src;
        //bookingAdapter.notifyDataSetChanged();

        SetProfile(position);



        myViewHolder.tvName.setText(memberList.get(position).getFullName());
        myViewHolder.flatnoid.setText(memberList.get(position).getFlatNo());
        myViewHolder.flatNo.setText(memberList.get(position).getFlatNo());

        String rtype = "";
        if (memberList.get(position).getrType().equalsIgnoreCase("O")) {
            rtype = "Owner";
        } else if (memberList.get(position).getrType().equalsIgnoreCase("T")) {
            rtype = "Tenant";
        }
        if(user.getOnline().equalsIgnoreCase("Yes")){
            myViewHolder.onlineView.setVisibility(View.VISIBLE);
        }else if(user.getOnline().equalsIgnoreCase("No")){
            myViewHolder.onlineView.setVisibility(View.GONE);
        }
        myViewHolder.rtype.setText(rtype);
        if (isSelected(position)) {
            myViewHolder.checked.setChecked(true);
            myViewHolder.checked.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.checked.setChecked(false);
            myViewHolder.checked.setVisibility(View.GONE);
        }
        myViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegisterFragment.getInstance()!=null){
                    User user = memberList.get(position);
                    RegisterFragment.getInstance().getMemberData(user.getFlatNo(),user.getFullName());
                    if(MemberListActivity.getInstance()!=null){
                        MemberListActivity.getInstance().finish();
                    }
                }

            }
        });
        //  myViewHolder.tvTime.setText(memberList.get(position).getTime());




       /* if (memberList.get(position).getOnline()) {
            myViewHolder.onlineView.setVisibility(View.VISIBLE);
        } else
            myViewHolder.onlineView.setVisibility(View.INVISIBLE);
*/
        //myViewHolder.tvLastChat.setText(memberList.get(position).getLastChat());
    }

    private void SetProfile(int pos) {
        if (user.getPhoto().contains(".jpg")) {
            Glide.with(mContext)
                    .load(memberList.get(pos).getPhoto())
                    .into(myViewHolder.userPhoto);
        } else {
            Glide.with(mContext)
                    .load(user.getPhoto())
                    .into(myViewHolder.userPhoto);
        }
    }

    private void OpenVisitorInfo(int pos) {
       /* Bookings bookings= bookingsList.get(pos);
        Toast.makeText(mContext,"Shop=="+bookings.getShopName(), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(mContext, Shop_Profile.class);
        //System.out.println("Email="+bookings.getShopAddress()Email());
        i.putExtra("ShopName",bookings.getShopAddress());
        i.putExtra("ShopObj", (Serializable) bookings);
        mContext.startActivity(i);*/
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            progress = (int) (millisUntilFinished / 1000);


        }

        @Override
        public void onFinish() {

        }
    }

}
