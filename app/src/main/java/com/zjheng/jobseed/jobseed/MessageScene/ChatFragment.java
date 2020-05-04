package com.zjheng.jobseed.jobseed.MessageScene;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.CustomObjectClass.UserChat;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.postTime;

/**
 * Created by zhen on 5/14/2017.
 */

public class ChatFragment extends Fragment {

    private RecyclerView mUserList;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mstartchatLay;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserChatList, mChatRoom, mUserAccount, mUserInfo;
    private CardView mretryBtn;
    private RelativeLayout mnoInternetLay;
    private static final String TAG = "ChatFragment";

    private String currentuserid, userimage;

    private Boolean isStarted = false;
    private Boolean isVisible = false;
    private Boolean firsttime = false;

    Activity context;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_chat_fragment, container, false);

        context = getActivity();

        Log.d(TAG, "ChatFragment");

        setHasOptionsMenu(true);

        firsttime = true;

        mnoInternetLay = (RelativeLayout)rootView.findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)rootView.findViewById(R.id.retryBtn);

        mstartchatLay = (RelativeLayout) rootView.findViewById(R.id.startchatLay);
        mUserList = (RecyclerView)rootView.findViewById(R.id.userlist);
        mUserList.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        mUserList.setLayoutManager(mLayoutManager);
        mUserList.setAdapter(new SampleRecycler());

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                // connected to wifi
                mnoInternetLay.setVisibility(GONE);
                mUserList.setVisibility(VISIBLE);

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                // connected to the mobile provider's data plan
                mnoInternetLay.setVisibility(GONE);
                mUserList.setVisibility(VISIBLE);
            }
        } else {
            Log.d(TAG, "not connected");
            // not connected to the internet
            mnoInternetLay.setVisibility(VISIBLE);
            mUserList.setVisibility(GONE);

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // connected to the internet
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.d(TAG, "connected to wifi");
                            // connected to wifi
                            mnoInternetLay.setVisibility(GONE);
                            mUserList.setVisibility(VISIBLE);
                            loadData();

                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            // connected to the mobile provider's data plan
                            mnoInternetLay.setVisibility(GONE);
                            mUserList.setVisibility(VISIBLE);
                            loadData();
                        }
                    } else {
                        Log.d(TAG, "not connected");
                        // not connected to the internet
                        mnoInternetLay.setVisibility(VISIBLE);
                        mUserList.setVisibility(GONE);
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint");
        isVisible = isVisibleToUser;
        if (isStarted && isVisible) {
            viewDidAppear();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "on start");
        isStarted = true;
        if (isVisible && isStarted){
            viewDidAppear();
        }
    }

    public void viewDidAppear() {
        // your logic

        Log.d(TAG, "view did appear ");

        if (firsttime) {
            loadData();
            firsttime = false;
        }

    }

    private  void loadData() {

        //mProgress = new ProgressDialog(context);
        //mProgress.setMessage("Loading");
        //mProgress.setCancelable(false);
       // mProgress.show();

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserList.setLayoutManager(mLayoutManager);

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("UserImage")) {
                    userimage = dataSnapshot.child("UserImage").getValue().toString();
                }

                else{
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("image")) {
                                userimage = dataSnapshot.child("image").getValue().toString();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final FirebaseRecyclerAdapter<UserChat, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserChat, BlogViewHolder>(
                UserChat.class,
                R.layout.userchatrow,
                BlogViewHolder.class,
                mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").orderByChild("negatedtime")

        ) {

            public void onDataChanged() {
                Log.d(TAG, "changed chat data");

            }

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, UserChat model, int position) {

                final String post_username = model.getownername();
                final String post_userimage = model.getownerimage();
                final String post_uid = model.getownerid();
                final String postkey = getRef(position).getKey();

                viewHolder.setonwername(model.getownername(), model.getownerid(), mUserInfo);
                viewHolder.setlastmessage(model.getlastmessage());
                viewHolder.setownerimage(context.getApplicationContext(), model.getownerimage(), model.getownerid(), mUserInfo);
                viewHolder.settime(model.gettime());
                viewHolder.setmessagecount( model.getnewmessage());

                viewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(post_uid!=null){
                            //set notification badge at MainActivity

                            mUserChatList.child(mAuth.getCurrentUser().getUid()).child("Pressed").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        mUserChatList.child(mAuth.getCurrentUser().getUid()).child("Pressed").setValue("true");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + post_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + post_uid).child("UnreadMessagePressed").setValue("true");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            mUserInfo.child(post_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild("UserImage")) {

                                        String receiver_image = dataSnapshot.child("UserImage").getValue().toString();
                                        Intent chatroomintent = new Intent(context, ChatRoom.class);
                                        chatroomintent.putExtra("post_uid",post_uid);
                                        chatroomintent.putExtra("owner_image",userimage);
                                        chatroomintent.putExtra("receiver_image",receiver_image);
                                        startActivity(chatroomintent);
                                    }

                                    else{
                                        mUserAccount.child(post_uid).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String receiver_image = dataSnapshot.getValue().toString();
                                                Intent chatroomintent = new Intent(context, ChatRoom.class);
                                                chatroomintent.putExtra("post_uid",post_uid);
                                                chatroomintent.putExtra("owner_image",userimage);
                                                chatroomintent.putExtra("receiver_image",receiver_image);
                                                startActivity(chatroomintent);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });

            }

        };

        mUserList.setAdapter(firebaseRecyclerAdapter);


        mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mstartchatLay.setVisibility(GONE);
                }
                else{
                    mstartchatLay.setVisibility(VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // SampleHolder.java
    public class SampleHolder extends RecyclerView.ViewHolder {
        public SampleHolder(View itemView) {
            super(itemView);
        }
    }

    // SampleRecycler.java
    public class SampleRecycler extends RecyclerView.Adapter<SampleHolder> {
        @Override
        public SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(SampleHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem itemSearch = menu.findItem(R.id.menuSearch2);
        itemSearch.setVisible(false);

        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setVisible(false);

        MenuItem itemPublish = menu.findItem(R.id.menuPublish);
        itemPublish.setVisible(false);

        MenuItem item = menu.findItem(R.id.menuSearch);
        item.setVisible(false);

        MenuItem itemSave = menu.findItem(R.id.menuSave);
        itemSave.setVisible(false);
    }


    public static ChatFragment newInstance(String chat) {
        return new ChatFragment();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        CircleImageView mprofilepic;
        CardView mcardview;
        TextView mpostTime, post_name;
        TextView post_lasttext;
        View mchatnotifiBadge;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mprofilepic = (CircleImageView) mView.findViewById(R.id.profilepic);
            mcardview = (CardView) mView.findViewById(R.id.cardview);
            mpostTime = (TextView)mView.findViewById(postTime);
            post_lasttext = (TextView)mView.findViewById(R.id.postLastText);
            post_name = (TextView)mView.findViewById(R.id.postName);
            mchatnotifiBadge = (View)mView.findViewById(R.id.chatnotifiBadge);
        }

        public void setonwername(final String ownername, final String otheruserid, DatabaseReference mUserInfo){

            if(ownername!=null && otheruserid!= null) {
                mUserInfo.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("Name")) {
                            String user_name = dataSnapshot.child("Name").getValue().toString();
                            post_name.setText(user_name);
                        }
                        else{
                            post_name.setText(ownername);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        public void setlastmessage(String lastmessage){
            if (lastmessage != null) {
                post_lasttext.setText(lastmessage);
            }
            else {
                post_lasttext.setText(" ");
            }
        }

        public void setownerimage(final Context ctx, final String receiverimage, final String otheruserid, DatabaseReference mUserInfo){

            if(otheruserid!= null && receiverimage!=null) {
                mUserInfo.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("UserImage")) {
                            String post_userimage = dataSnapshot.child("UserImage").getValue().toString();
                            if (post_userimage != null) {
                                if (post_userimage.equals("default")) {
                                    mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                } else {
                                    Glide.with(ctx).load(post_userimage)
                                            .thumbnail(0.5f)
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .error(R.drawable.defaultprofile_pic)
                                            .into(mprofilepic);
                                }
                            }
                        } else {
                            if (receiverimage.equals("default")) {
                                Glide.with(ctx).load(R.drawable.defaultprofile_pic)
                                        .thumbnail(0.5f)
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mprofilepic);
                            } else {
                                Glide.with(ctx).load(receiverimage)
                                        .thumbnail(0.5f)
                                        .centerCrop()
                                        .error(R.drawable.defaultprofile_pic)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mprofilepic);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }

        public void settime(Long time) {

            if (time != null) {
                Log.d(TAG, "time: " + time);
                Date d = new Date(time );

                Long tsLong = System.currentTimeMillis();
                Date nowd = new Date(tsLong );

                SimpleDateFormat fmt = new SimpleDateFormat("dd MMM HH:mm");
                mpostTime.setText(fmt.format(d));

            }
        }


        public void setmessagecount( String newmessage) {

            if (newmessage != null) {

                if(newmessage.equals("true")){
                    mchatnotifiBadge.setVisibility(View.GONE);
                    mpostTime.setTextColor(Color.parseColor("#808080"));
                    post_lasttext.setTextColor(Color.parseColor("#808080"));
                }
                else {
                    mchatnotifiBadge.setVisibility(View.VISIBLE);
                    mpostTime.setTextColor(Color.RED);
                    post_lasttext.setTextColor(Color.BLACK);
                }
            }

            if (newmessage == null){
                mchatnotifiBadge.setVisibility(View.GONE);
                mpostTime.setTextColor(Color.parseColor("#808080"));
                post_lasttext.setTextColor(Color.parseColor("#808080"));
            }
        }
    }
}