package com.payphi.visitorsregister.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.VoteForPoll;
import com.payphi.visitorsregister.model.Poll;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.recyclerview.SelectableAdapter;

import java.util.ArrayList;
import java.util.Random;

public class PollListAdapter extends SelectableAdapter<PollListAdapter.MyViewHolder> {
    private static Context mContext;
    PollListAdapter pollListAdapter;
  static   private ArrayList<Poll> pollList;
    private MyViewHolder.ClickListener clickListener;
    MyViewHolder myViewHolder;
    Poll poll;
    String defaultPic="https://firebasestorage.googleapis.com/v0/b/test-184bf.appspot.com/o/pielogo.jpg?alt=media&token=abe1708c-69af-473a-b845-99fcfe1cc3e4";

    public PollListAdapter(Context mContext, ArrayList<Poll> pollList, PollListAdapter adapter) {
        this.mContext = mContext;
        this.pollList = pollList;
        this.pollListAdapter = adapter;
    }
    public void updateList(ArrayList<Poll> list) {
        pollList = list;
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvName, rtype;
        public TextView tvTime;
        public TextView tvLastChat, flatnoid;
        public ImageView userPhoto;
        public boolean online = false;
        private final View onlineView=null;
        public CheckBox checked;
        private MyViewHolder.ClickListener listener;
        //private final View selectedOverlay;



        public MyViewHolder(View itemLayoutView, MyViewHolder.ClickListener listener) {
            super(itemLayoutView);

            this.listener = listener;

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            //selectedOverlay = (View) itemView.findViewById(R.id.selected_overlay);
            tvTime = (TextView) itemLayoutView.findViewById(R.id.tv_time);
            //  tvLastChat = (TextView) itemLayoutView.findViewById(R.id.tv_last_chat);
            userPhoto = (ImageView) itemLayoutView.findViewById(R.id.iv_user_photo);

            checked = (CheckBox) itemLayoutView.findViewById(R.id.chk_list);
            flatnoid = (TextView) itemLayoutView.findViewById(R.id.flatnoid);
            rtype = (TextView) itemLayoutView.findViewById(R.id.rtypeId);
            itemLayoutView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        Poll clickedDataItem = pollList.get(pos);
                        //Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getOptions(), Toast.LENGTH_SHORT).show();
                        OpenActivity(clickedDataItem);
                    }
                }
            });
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        myViewHolder = holder;
        poll = pollList.get(position);
        setAnimation(holder.itemView, position);
        Bitmap src;
        //bookingAdapter.notifyDataSetChanged();

        //SetProfile(position);

        myViewHolder.tvName.setText(pollList.get(position).getName());
        myViewHolder.flatnoid.setVisibility(View.INVISIBLE);
        String rtype = "";
       /* if (memberList.get(position).getrType().equalsIgnoreCase("O")) {
            rtype = "Owner";
        } else if (memberList.get(position).getrType().equalsIgnoreCase("T")) {
            rtype = "Tenant";
        }*/
        myViewHolder.rtype.setText(pollList.get(position).getQuestion());
        if (isSelected(position)) {
            myViewHolder.checked.setChecked(true);
            myViewHolder.checked.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.checked.setChecked(false);
            myViewHolder.checked.setVisibility(View.GONE);
        }
       // System.out.println("Poll list size="+pollList.size());
       // System.out.println("User Pic in adapter="+pollList.get(position).getUserPic());
        if(pollList.get(position).getUserPic()!=null) {
            if (pollList.get(position).getUserPic().contains(".jpg")) {
                Glide.with(mContext)
                        .load(pollList.get(position).getUserPic())
                        .into(myViewHolder.userPhoto);
            } else {
                Glide.with(mContext)
                        .load(pollList.get(position).getUserPic())
                        .into(myViewHolder.userPhoto);
            }
        }else{
            Glide.with(mContext)
                    .load(defaultPic)
                    .into(myViewHolder.userPhoto);
        }

        //  myViewHolder.tvTime.setText(memberList.get(position).getTime());




    }

    private void SetProfile(int pos) {
     Poll poll = pollList.get(pos);
        if (poll.getUserPic().contains(".jpg")) {
            Glide.with(mContext)
                    .load(poll.getUserPic())
                    .into(myViewHolder.userPhoto);
        } else {
            Glide.with(mContext)
                    .load(poll.getUserPic())
                    .into(myViewHolder.userPhoto);
        }
    }
    private static void OpenActivity(Poll poll) {





        Intent intent = new Intent(mContext,VoteForPoll.class);
        intent.putExtra("PollObj", poll);
        mContext.startActivity(intent);



    }

    @Override
    public int getItemCount() {

        return pollList.size();
    }
}
