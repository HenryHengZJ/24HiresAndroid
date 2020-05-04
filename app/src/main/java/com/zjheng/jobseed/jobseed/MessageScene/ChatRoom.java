package com.zjheng.jobseed.jobseed.MessageScene;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomUIClass.CustomEditText;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUser;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.CustomObjectClass.UserChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.id.message;

public class ChatRoom extends AppCompatActivity {

    private String receiver_uid, receiver_name, receiver_image, useruid, username, userimage, newUserListkey, chatkey, owner_image;
    private RecyclerView mchatlist;
    private ImageView sendButton;
    private ImageButton mbackBtn, mremoveBtn;
    private CustomEditText messageArea;
    private ScrollView scrollView;
    private LinearLayoutManager mLayoutManager;
    private CircleImageView mprofilepic;
    private TextView musernametxt, lastseentxt;
    private CardView msendcardview;
    private UserChat userchats;

    private Query mQueryLoadMore;

    private ChatRecyclerAdapter chatrecyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mChatRoom, mUserChatList, mUserAccount, mUserActivities, mUserInfo, mUserChatNotification, mUserBlock, mJob, mTalent;
    private Query mQueryMore, mQueryNoMore;
    private ChildEventListener mChildMore, mNoMoreChild;

    private static final String TAG = "ChatRoom";
    private int firstloadcount = 0, count;
    private boolean firsttime = true;
    private boolean loading = false;
    private boolean atbottom = false;
    private List<UserChat> userchatlist;

    private long initialmessagecount = 0;
    private Long firstposttime;
    private Long variablechildcount;
    private int loadlimit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                //Connected
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                //Connected
            }
        } else {
            //Disconnected
            Toast.makeText(ChatRoom.this, "Network Unavailable", Toast.LENGTH_LONG).show();
        }

        mAuth = FirebaseAuth.getInstance();

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserChatNotification =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Notification");

        mUserBlock = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserBlock");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mTalent = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        receiver_uid = getIntent().getStringExtra("post_uid");
        receiver_image = getIntent().getStringExtra("receiver_image");
        owner_image = getIntent().getStringExtra("owner_image");

        sendButton = (ImageView)findViewById(R.id.sendButton);
        mbackBtn = (ImageButton) findViewById(R.id.backBtn);
        mremoveBtn = (ImageButton) findViewById(R.id.removeBtn);
        messageArea = (CustomEditText) findViewById(R.id.messageArea);
        mprofilepic = (CircleImageView)findViewById(R.id.profilepic);
        musernametxt = (TextView)findViewById(R.id.usernametxt);
        lastseentxt = (TextView)findViewById(R.id.lastseentxt);
        msendcardview = (CardView)findViewById(R.id.sendcardview);

        mchatlist = (RecyclerView) findViewById(R.id.chatlist);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        mchatlist.setHasFixedSize(true);
        // mchatlist.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        //mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(false);

        userchatlist = new ArrayList<UserChat>();
        mchatlist.setLayoutManager(mLayoutManager);
        chatrecyclerAdapter = new ChatRecyclerAdapter(mAuth.getCurrentUser().getUid(),mTalent,mJob, mUserInfo, mUserAccount, userchatlist, receiver_image, owner_image, this);
        mchatlist.setAdapter(chatrecyclerAdapter);

        mchatlist.scrollToPosition(mchatlist.getAdapter().getItemCount() - 1);

        recyclerviewlistener();


        mchatlist.postDelayed(new Runnable() {
            @Override
            public void run() {

                mLayoutManager.smoothScrollToPosition(mchatlist, null,  chatrecyclerAdapter.getItemCount());
            }
        }, 300);


        final FirebaseUser mUser= mAuth.getCurrentUser();

        if (mUser != null) {

            //Get the uid for the currently logged in User from intent data passed to this activity
            useruid = mUser.getUid();

            if (useruid != null && useruid != "") {

                attachchildlistener();

                mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("Name")) {
                            username = dataSnapshot.child("Name").getValue().toString();
                        }
                        else{
                            mUserAccount.child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        username = (String) dataSnapshot.getValue();
                                    }
                                    else {
                                        username = "";
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                       /* mchatlist.scrollToPosition(chatrecyclerAdapter.getItemCount() - 1);
                        mchatlist.smoothScrollToPosition(mchatlist.getAdapter().getItemCount());

                        mchatlist.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                mchatlist.scrollToPosition(mchatlist.getAdapter().getItemCount() - 1);
                                mchatlist.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });*/

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        mUserInfo.child(receiver_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Name")) {

                    receiver_name = dataSnapshot.child("Name").getValue().toString();
                    musernametxt.setText(receiver_name);
                }
                else{
                    mUserAccount.child(receiver_uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            receiver_name = dataSnapshot.getValue().toString();
                            musernametxt.setText(receiver_name);
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


        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReferenceFromUrl ("https://jobseed-2cb76.firebaseio.com").child("UserActivities").child(receiver_uid);
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {

                if(datasnapshot.hasChild("Lastonline")) {

                    Long tsLong = System.currentTimeMillis();

                    Long time = datasnapshot.child("Lastonline").getValue(Long.class);

                    CharSequence result = DateUtils.getRelativeTimeSpanString(time, tsLong, 0);
                    lastseentxt.setText("last seen "+ result);
                }

                if(datasnapshot.hasChild("Connections")){
                    lastseentxt.setText("Online");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mChatRoom.child(useruid).child(useruid + "_" + receiver_uid).child("UnpressedNotification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String notificationkey = userSnaphot.getKey();
                    if(notificationkey!=null) {
                        mUserChatNotification.child("Messages").child(notificationkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mChatRoom.child(useruid).child(useruid + "_" + receiver_uid).child("UnpressedNotification").child(notificationkey).removeValue();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        messageArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSoftKeyboard();

            }
        });


        messageArea.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    sendButton.setEnabled(true);
                    sendButton.setImageResource(R.drawable.send_button2);
                }
                else{
                    sendButton.setEnabled(false);
                    sendButton.setImageResource(R.drawable.send_button1);
                }
            }
        });

        msendcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUserBlock.child(receiver_uid).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            blockchatting();
                        }
                        else{
                            startchatting();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(receiver_uid!=null){

                    Intent chatroomintent = new Intent(ChatRoom.this, OtherUser.class);
                    chatroomintent.putExtra("user_uid",receiver_uid);
                    startActivity(chatroomintent);
                }
            }
        });

        mremoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ChatRoom.this, mremoveBtn);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.block:
                                Log.d(TAG, "block: ");
                                blockchat(receiver_name);
                                break;
                            case R.id.delete:
                                Log.d(TAG, "delete: ");
                                deletechat(receiver_name);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });

        mchatlist.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) scrollToBottom();
            }
        });

    }

    private void recyclerviewlistener() {
        mchatlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);

                if(dy < 0) {

                    atbottom = false;

                    int visibleItemCount = mLayoutManager.getChildCount();

                    int totalItemCount = mLayoutManager.getItemCount();

                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ((pastVisibleItems + visibleItemCount) >= totalItemCount) {
                        Log.d(TAG, "ntg");
                    }
                    else if (pastVisibleItems == 0){

                        Log.d(TAG, "dunoapa");

                        if (loading)
                        {
                            Log.d(TAG, "loading");
                            //End of list
                            loading = false;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    userchatlist.remove(0);
                                    chatrecyclerAdapter.notifyItemRemoved(0);
                                    loadmore(userchatlist.size());
                                }
                            }, 750); //time seconds
                        }
                    }
                }

                if(dy > 0) {
                    int lastVisibleItems = mLayoutManager.findLastVisibleItemPosition();

                    if (lastVisibleItems == userchatlist.size() - 1) {
                        Log.d(TAG, "at btm");
                        atbottom = true;
                    }
                }
            }
        });
    }

    private void attachchildlistener() {
        mQueryMore =  mChatRoom.child(useruid).child(useruid + "_" + receiver_uid).child("ChatList").limitToLast(loadlimit);
        mChildMore = mQueryMore.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "childadded");

                firstloadcount++;

                userchats = new UserChat();

                if(dataSnapshot.hasChild("message")){
                    String message = dataSnapshot.child("message").getValue().toString();
                    Log.d(TAG, "messageoo " + message);
                    userchats.setmessage(message);
                }

                if(dataSnapshot.hasChild("ownerid")){
                    String ownerid = dataSnapshot.child("ownerid").getValue().toString();
                    userchats.setownerid(ownerid);
                }

                if(dataSnapshot.hasChild("ownername")){
                    String ownername = dataSnapshot.child("ownername").getValue().toString();
                    userchats.setownername(ownername);
                }

                if(dataSnapshot.hasChild("receivername")){
                    String receivername = dataSnapshot.child("receivername").getValue().toString();
                    userchats.setreceivername(receivername);
                }

                if(dataSnapshot.hasChild("oldtime")){
                    Long oldtime = (Long) dataSnapshot.child("oldtime").getValue();
                    userchats.setoldtime(oldtime);
                }

                if(dataSnapshot.hasChild("time")){
                    Long time = (Long) dataSnapshot.child("time").getValue();
                    userchats.settime(time);
                }

                if(dataSnapshot.hasChild("negatedtime")){
                    Long negatedtime = (Long) dataSnapshot.child("negatedtime").getValue();

                    if(firsttime){
                        firstposttime = negatedtime;
                        firsttime= false;
                    }
                }

                if(dataSnapshot.hasChild("actiontitle")){
                    String actiontitle = dataSnapshot.child("actiontitle").getValue().toString();
                    userchats.setactiontitle(actiontitle);
                }

                if(dataSnapshot.hasChild("jobtitle")){
                    String jobtitle = dataSnapshot.child("jobtitle").getValue().toString();
                    userchats.setjobtitle(jobtitle);
                }

                if(dataSnapshot.hasChild("jobdescrip")){
                    String jobdescrip = dataSnapshot.child("jobdescrip").getValue().toString();
                    userchats.setjobdescrip(jobdescrip);
                }

                if(dataSnapshot.hasChild("city")){
                    String city = dataSnapshot.child("city").getValue().toString();
                    userchats.setcity(city);
                }

                if(dataSnapshot.hasChild("postkey")){
                    String postkey = dataSnapshot.child("postkey").getValue().toString();
                    userchats.setpostkey(postkey);
                }

                if(dataSnapshot.hasChild("maincategory")){
                    String maincategory = dataSnapshot.child("maincategory").getValue().toString();
                    userchats.setmaincategory(maincategory);
                }

                if(dataSnapshot.hasChild("subcategory")){
                    String subcategory = dataSnapshot.child("subcategory").getValue().toString();
                    userchats.setsubcategory(subcategory);
                }


                if(chatrecyclerAdapter!=null) {

                    if (firstloadcount == loadlimit) {
                        Log.d(TAG, "firstloadcount == loadlimit" + userchatlist.size());
                        userchatlist.add(0, null);
                        loading = true;
                        // chatrecyclerAdapter.notifyItemInserted(0);
                    }

                    Log.d(TAG, "userchatlist.notifyDataSetChanged");
                    userchatlist.add(userchats);

                    chatrecyclerAdapter.notifyDataSetChanged();

                    if(atbottom){
                        mchatlist.scrollToPosition(mchatlist.getAdapter().getItemCount() - 1);

                        mchatlist.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                mLayoutManager.smoothScrollToPosition(mchatlist, null,  chatrecyclerAdapter.getItemCount());
                            }
                        }, 300);
                    }

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged");

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
            }
        });
    }

    private void loadmore(final int oldsize ){

        Log.d(TAG, "firstposttime " + firstposttime);

        count = 0;

        mQueryLoadMore = mChatRoom.child(useruid).child(useruid + "_" + receiver_uid).child("ChatList").orderByChild("negatedtime").limitToFirst(loadlimit+1).startAt(firstposttime);
        mQueryLoadMore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean firstkey = true;

                for (DataSnapshot usersdataSnapshot1 : dataSnapshot.getChildren()) {

                    userchats = new UserChat();

                    if (usersdataSnapshot1.hasChild("message")) {
                        String message = usersdataSnapshot1.child("message").getValue().toString();
                        Log.d(TAG, "messagelaodmorexxx " + message);
                        userchats.setmessage(message);
                    }

                    if (usersdataSnapshot1.hasChild("ownerid")) {
                        String ownerid = usersdataSnapshot1.child("ownerid").getValue().toString();
                        userchats.setownerid(ownerid);
                    }

                    if (usersdataSnapshot1.hasChild("ownername")) {
                        String ownername = usersdataSnapshot1.child("ownername").getValue().toString();
                        userchats.setownername(ownername);
                    }

                    if (usersdataSnapshot1.hasChild("receivername")) {
                        String receivername = usersdataSnapshot1.child("receivername").getValue().toString();
                        userchats.setreceivername(receivername);
                    }

                    if (usersdataSnapshot1.hasChild("oldtime")) {
                        Long oldtime = (Long) usersdataSnapshot1.child("oldtime").getValue();
                        userchats.setoldtime(oldtime);
                    }

                    if (usersdataSnapshot1.hasChild("time")) {
                        Long time = (Long) usersdataSnapshot1.child("time").getValue();
                        userchats.settime(time);
                    }

                    if(usersdataSnapshot1.hasChild("negatedtime")){
                        Long negatedtime = (Long) usersdataSnapshot1.child("negatedtime").getValue();
                        firstposttime = negatedtime;
                    }

                    if(dataSnapshot.hasChild("actiontitle")){
                        String actiontitle = dataSnapshot.child("actiontitle").getValue().toString();
                        userchats.setactiontitle(actiontitle);
                    }

                    if(dataSnapshot.hasChild("jobtitle")){
                        String jobtitle = dataSnapshot.child("jobtitle").getValue().toString();
                        userchats.setjobtitle(jobtitle);
                    }

                    if(dataSnapshot.hasChild("jobdescrip")){
                        String jobdescrip = dataSnapshot.child("jobdescrip").getValue().toString();
                        userchats.setjobdescrip(jobdescrip);
                    }

                    if(dataSnapshot.hasChild("city")){
                        String city = dataSnapshot.child("city").getValue().toString();
                        userchats.setcity(city);
                    }

                    if(dataSnapshot.hasChild("postkey")){
                        String postkey = dataSnapshot.child("postkey").getValue().toString();
                        userchats.setpostkey(postkey);
                    }

                    if(dataSnapshot.hasChild("maincategory")){
                        String maincategory = dataSnapshot.child("maincategory").getValue().toString();
                        userchats.setmaincategory(maincategory);
                    }

                    if(dataSnapshot.hasChild("subcategory")){
                        String subcategory = dataSnapshot.child("subcategory").getValue().toString();
                        userchats.setsubcategory(subcategory);
                    }

                    count++;

                    if (firstkey) {
                        firstkey = false;
                    } else {
                        userchatlist.add(0, userchats);
                        if (count == loadlimit+1) {
                            userchatlist.add(0,null);
                            Log.d(TAG, "chat oldsize " + oldsize);
                            Log.d(TAG, "userchatlist.size() " + userchatlist.size());

                        }
                    }
                }

                Log.d(TAG, "chat count " + count);

                if (count < loadlimit + 1) {
                    Log.d(TAG, "chat end loading");
                    chatrecyclerAdapter.notifyItemRangeInserted(0, userchatlist.size() - oldsize);
                    loading = false;
                }

                else {
                    Log.d(TAG, "chat cont loading");
                    chatrecyclerAdapter.notifyItemRangeInserted(0, userchatlist.size() - oldsize);
                    loading = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void scrollToBottom() {

        //mchatlist.smoothScrollToPosition(0);
        mLayoutManager.smoothScrollToPosition(mchatlist, null, chatrecyclerAdapter.getItemCount());
    }

    private void blockchat(String receiver_name){
        // custom dialog
        final Dialog dialog = new Dialog(ChatRoom.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.applicantsdialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
        TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
        Button hirebtn = (Button) dialog.findViewById(R.id.hireBtn);

        hirebtn.setText("OK");
        hirebtn.setTextColor(Color.parseColor("#0e52a5"));
        mdialogtxt.setText("Block "+receiver_name + " ? Blocked contacts will no longer be able to send you messages.");

        dialog.show();

        hirebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUserBlock.child(mAuth.getCurrentUser().getUid()).child(receiver_uid).setValue("true");
                mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + receiver_uid).removeValue();
                mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").child(receiver_uid).removeValue();
                onBackPressed();

                dialog.dismiss();
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void deletechat(String receiver_name){
        // custom dialog
        final Dialog dialog = new Dialog(ChatRoom.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.applicantsdialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
        TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
        Button hirebtn = (Button) dialog.findViewById(R.id.hireBtn);

        hirebtn.setText("OK");
        hirebtn.setTextColor(Color.parseColor("#0e52a5"));
        mdialogtxt.setText("Delete chat with "+receiver_name + " ?");

        dialog.show();

        hirebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + receiver_uid).removeValue();
                mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").child(receiver_uid).child("lastmessage").removeValue();
                mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").child(receiver_uid).child("time").setValue(0);

                onBackPressed();

                dialog.dismiss();
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageArea.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public void showSoftKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(messageArea, InputMethodManager.SHOW_IMPLICIT);

        messageArea.requestFocus();

    }

    private void blockchatting(){
        final String messageText = messageArea.getText().toString().trim();

        if (!TextUtils.isEmpty(messageText) && receiver_name!=null && receiver_image!=null && receiver_uid!=null ) {
            final DatabaseReference OwnerChat = mChatRoom.child(mAuth.getCurrentUser().getUid());
            final DatabaseReference newOwnerChat = OwnerChat.child(useruid+"_"+receiver_uid).child("ChatList").push();

            final DatabaseReference OwnerChatList = mUserChatList.child(useruid).child("UserList");
            final DatabaseReference newOwnerChatList = OwnerChatList.child(receiver_uid);

            final Map<String, Object> chatData = new HashMap<>();
            chatData.put("time", ServerValue.TIMESTAMP);
            Long tsLong = System.currentTimeMillis();
            chatData.put("negatedtime", (-1*tsLong));
            chatData.put("message", messageText);
            chatData.put("ownername", username);
            chatData.put("ownerid", mAuth.getCurrentUser().getUid());
            chatData.put("receivername", receiver_name);
            chatData.put("receiverid", receiver_uid);

            final Map<String, Object> ownerchatlistData = new HashMap<>();
            ownerchatlistData.put("negatedtime", (-1*tsLong));
            ownerchatlistData.put("time", ServerValue.TIMESTAMP);
            ownerchatlistData.put("ownername", receiver_name);
            ownerchatlistData.put("ownerimage", receiver_image);
            ownerchatlistData.put("lastmessage", messageText);
            ownerchatlistData.put("ownerid", receiver_uid);

            mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child(receiver_uid).exists()){

                            chatData.put("oldtime", dataSnapshot.child(receiver_uid).child("time").getValue());

                            newOwnerChat.setValue(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        newOwnerChatList.setValue(ownerchatlistData);
                                    }
                                    else{
                                        Log.d(TAG, "failed");
                                    }
                                }
                            });
                        }
                        else{
                            chatData.put("oldtime", 0);
                            newOwnerChat.setValue(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        newOwnerChatList.setValue(ownerchatlistData);
                                    }
                                    else{
                                        Log.d(TAG, "failed");
                                    }
                                }
                            });
                        }
                    }
                    else{
                        chatData.put("oldtime", 0);
                        newOwnerChat.setValue(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    newOwnerChatList.setValue(ownerchatlistData);
                                }
                                else{
                                    Log.d(TAG, "failed");
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        messageArea.setText("");

        messageArea.requestFocus();

        mchatlist.scrollToPosition(mchatlist.getAdapter().getItemCount() - 1);

        mchatlist.postDelayed(new Runnable() {
            @Override
            public void run() {
                mchatlist.scrollToPosition(mchatlist.getAdapter().getItemCount()-1);
                //mchatlist.smoothScrollToPosition(0);
            }
        }, 300);

    }


    private void startchatting(){


        final String messageText = messageArea.getText().toString().trim();

        if (!TextUtils.isEmpty(messageText) && receiver_name!=null && receiver_image!=null && receiver_uid!=null) {

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    Log.d(TAG, "connected to wifi");
                    //Connected
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    Log.d(TAG, "connected to data");
                    //Connected
                }
                else {
                    //Disconnected
                    Toast.makeText(ChatRoom.this, "Your message will be sent once network is available", Toast.LENGTH_LONG).show();
                    messageArea.setText("");
                }
            } else {
                //Disconnected
                Toast.makeText(ChatRoom.this, "Your message will be sent once network is available", Toast.LENGTH_LONG).show();
                messageArea.setText("");
            }

            final DatabaseReference newNotification = mUserChatNotification.child("Messages").push();
            final String notificationKey = newNotification.getKey();

            final DatabaseReference OwnerChat = mChatRoom.child(mAuth.getCurrentUser().getUid());
            final DatabaseReference ReceiverChat = mChatRoom.child(receiver_uid);
            final DatabaseReference newReceiverChat = ReceiverChat.child(receiver_uid+"_"+useruid).child("ChatList").push();
            final String newChatListkey = newReceiverChat.getKey();
            final DatabaseReference newOwnerChat = OwnerChat.child(useruid+"_"+receiver_uid).child("ChatList").child(newChatListkey);

            final DatabaseReference ReceiverChatList = mUserChatList.child(receiver_uid).child("UserList");
            final DatabaseReference OwnerChatList = mUserChatList.child(useruid).child("UserList");
            final DatabaseReference newReceiverChatList = ReceiverChatList.child(useruid);
            newUserListkey = newReceiverChatList.getKey();
            final DatabaseReference newOwnerChatList = OwnerChatList.child(receiver_uid);

            final Map<String, Object> chatData = new HashMap<>();
            Long tsLong = System.currentTimeMillis();
            chatData.put("negatedtime", (-1*tsLong));
            chatData.put("time", ServerValue.TIMESTAMP);
            chatData.put("message", messageText);
            chatData.put("ownername", username);
            chatData.put("ownerid", mAuth.getCurrentUser().getUid());
            chatData.put("receivername", receiver_name);
            chatData.put("receiverid", receiver_uid);

            final Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("ownerUid", useruid);
            notificationData.put("receiverUid", receiver_uid);
            notificationData.put("ownerName", username);
            notificationData.put("lastmessage", messageText);
            notificationData.put("key", notificationKey);

            final Map<String, Object> receiverchatlistData = new HashMap<>();
            receiverchatlistData.put("negatedtime", (-1*tsLong));
            receiverchatlistData.put("time", ServerValue.TIMESTAMP);
            receiverchatlistData.put("ownername", username);
            receiverchatlistData.put("ownerimage", owner_image);
            receiverchatlistData.put("lastmessage", messageText);
            receiverchatlistData.put("ownerid", useruid);

            final Map<String, Object> ownerchatlistData = new HashMap<>();
            ownerchatlistData.put("negatedtime", (-1*tsLong));
            ownerchatlistData.put("time", ServerValue.TIMESTAMP);
            ownerchatlistData.put("ownername", receiver_name);
            ownerchatlistData.put("ownerimage", receiver_image);
            ownerchatlistData.put("lastmessage", messageText);
            ownerchatlistData.put("ownerid", receiver_uid);

            //Set notification
            mChatRoom.child(receiver_uid).child(receiver_uid + "_" + mAuth.getCurrentUser().getUid()).child("UnreadMessagePressed").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        String unreadval = dataSnapshot.getValue().toString();
                        Log.d(TAG, "unreadval " + unreadval);
                        Log.d(TAG, "receiver_uid " + receiver_uid);

                        //If user is NOT opening chat window
                        if(unreadval.equals("false")){

                            mChatRoom.child(receiver_uid).child(receiver_uid + "_" +  mAuth.getCurrentUser().getUid()).child("UnreadMessagePressed").setValue("false");
                            mUserChatList.child(receiver_uid).child("Pressed").setValue("false");
                            receiverchatlistData.put("newmessage", "false");

                            newNotification.setValue(notificationData);
                            mChatRoom.child(receiver_uid).child(receiver_uid + "_" +  mAuth.getCurrentUser().getUid()).child("UnpressedNotification").child(notificationKey).setValue(notificationKey);

                            // mUserAllNotification.child(receiver_uid).child("Chat").child(notificationKey).setValue(notificationKey);

                        }
                        //If user is opening chat window
                        else if (unreadval.equals("true")){

                            mChatRoom.child(receiver_uid).child(receiver_uid + "_" +  mAuth.getCurrentUser().getUid()).child("UnreadMessagePressed").setValue("true");
                            mUserChatList.child(receiver_uid).child("Pressed").setValue("true");
                            receiverchatlistData.put("newmessage", "true");

                        }
                    }
                    //If user is first time chat
                    else{

                        mChatRoom.child(receiver_uid).child(receiver_uid + "_" +  mAuth.getCurrentUser().getUid()).child("UnreadMessagePressed").setValue("false");
                        mUserChatList.child(receiver_uid).child("Pressed").setValue("false");
                        receiverchatlistData.put("newmessage", "false");

                        newNotification.setValue(notificationData);
                        mChatRoom.child(receiver_uid).child(receiver_uid + "_" +  mAuth.getCurrentUser().getUid()).child("UnpressedNotification").child(notificationKey).setValue(notificationKey);

                        // mUserAllNotification.child(receiver_uid).child("Chat").child(notificationKey).setValue(notificationKey);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child(receiver_uid).exists()){

                            chatData.put("oldtime", dataSnapshot.child(receiver_uid).child("time").getValue());

                            newOwnerChat.setValue(chatData);

                            newReceiverChat.setValue(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        newReceiverChatList.setValue(receiverchatlistData);
                                        newOwnerChatList.setValue(ownerchatlistData);
                                    }
                                    else{
                                        Log.d(TAG, "failed");
                                    }
                                }
                            });
                        }
                        else{

                            chatData.put("oldtime", 0);

                            newOwnerChat.setValue(chatData);

                            newReceiverChat.setValue(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        newReceiverChatList.setValue(receiverchatlistData);
                                        newOwnerChatList.setValue(ownerchatlistData);
                                    }
                                    else{
                                        Log.d(TAG, "failed");
                                    }
                                }
                            });
                        }
                    }
                    else{

                        chatData.put("oldtime", 0);

                        newOwnerChat.setValue(chatData);

                        newReceiverChat.setValue(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    newReceiverChatList.setValue(receiverchatlistData);
                                    newOwnerChatList.setValue(ownerchatlistData);
                                }
                                else{
                                    Log.d(TAG, "failed");
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        messageArea.setText("");

        messageArea.requestFocus();

        mchatlist.scrollToPosition(mchatlist.getAdapter().getItemCount() - 1);

        mchatlist.postDelayed(new Runnable() {
            @Override
            public void run() {
                mchatlist.scrollToPosition(mchatlist.getAdapter().getItemCount()-1);
                //mchatlist.smoothScrollToPosition(0);
            }
        }, 300);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + receiver_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mChatRoom.child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid() + "_" + receiver_uid).child("UnreadMessagePressed").setValue("false");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").child(receiver_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUserChatList.child(mAuth.getCurrentUser().getUid()).child("Pressed").setValue("true");
                    mUserChatList.child(mAuth.getCurrentUser().getUid()).child("UserList").child(receiver_uid).child("newmessage").setValue("true");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if ( mQueryMore != null && mChildMore != null) {
            Log.d(TAG, "mQueryMore stopped");
            mQueryMore.removeEventListener(mChildMore);
        }

        if ( mQueryNoMore != null && mNoMoreChild != null) {
            Log.d(TAG, "mQueryNoMore stopped");
            mQueryNoMore.removeEventListener(mNoMoreChild);
        }

        Log.d(TAG, "chatroom stopped");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //attachchildlistener();
        Log.d(TAG, "chatroom start");
    }
}
