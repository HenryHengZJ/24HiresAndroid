package com.zjheng.jobseed.jobseed.TalentActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.RemovedJob;
import com.zjheng.jobseed.jobseed.RemovedTalent;
import com.zjheng.jobseed.jobseed.TalentDetails.BookingForm;
import com.zjheng.jobseed.jobseed.TalentDetails.TalentDetail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
//import static com.google.android.gms.fitness.data.zzr.St;
import static com.zjheng.jobseed.jobseed.R.id.cardview;
import static com.zjheng.jobseed.jobseed.UnUsedFiles.ShortListedApplicantUserProfile.mRatingBar;
import static com.zjheng.jobseed.jobseed.UnUsedFiles.ShortListedApplicantUserProfile.mratingtxt;

/**
 * Created by zhen on 5/5/2017.
 */

public class BookingsMadeTab extends Fragment {

    private RecyclerView mBookingList;
    private LinearLayoutManager mLayoutManager;

    private RelativeLayout mstartbookLay, mnoInternetLay;
    private FirebaseRecyclerAdapter<TalentInfo, BlogViewHolder> firebaseRecyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserBookingMade,mUserMyTalentPendingBookings, mTalent, mUserMyTalentAcceptedBookings, mUserTalentReview, mUserMyTalent, mUserAccount, mUserActivities;

    private CardView mretryBtn;

    private TextView mstarttxt;
    private ImageView mstartImg;

    private static final String TAG = "BookingsMadeTab";

    private String ownuserid, ReviewCount, reducedReviewCount;

    private long reviewcount5 = 0, reviewcount4 = 0, reviewcount3 = 0, reviewcount2 = 0 , reviewcount1 = 0, totalreviewcount = 0;

    private int rateval;

    private ProgressDialog mProgress;

    private int ratestar = 0;
    private Boolean editpost = false;


    Activity context;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.savedtab, container, false);

        context = getActivity();

        mstartbookLay = (RelativeLayout) rootView.findViewById(R.id.startsavedLay);
        mnoInternetLay = (RelativeLayout)rootView.findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)rootView.findViewById(R.id.retryBtn);
        mBookingList = (RecyclerView)rootView.findViewById(R.id.savedlist);
        mstarttxt = rootView.findViewById(R.id.starttxt);
        mstartImg = rootView.findViewById(R.id.startImg);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                //Connected
                mnoInternetLay.setVisibility(GONE);

                if(mstartbookLay.getVisibility() == View.GONE){
                    mBookingList.setVisibility(VISIBLE);
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                //Connected
                mnoInternetLay.setVisibility(GONE);

                if(mstartbookLay.getVisibility() == View.GONE){
                    mBookingList.setVisibility(VISIBLE);
                }
            }
        } else {
            //Disconnected
            mnoInternetLay.setVisibility(VISIBLE);

            if(mstartbookLay.getVisibility() == View.GONE){
                mBookingList.setVisibility(GONE);
            }

            mretryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // connected to the internet
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.d(TAG, "connected to wifi");
                            //Connected
                            mnoInternetLay.setVisibility(GONE);

                            if(mstartbookLay.getVisibility() == View.GONE){
                                mBookingList.setVisibility(VISIBLE);
                            }
                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            //Connected
                            mnoInternetLay.setVisibility(GONE);

                            if(mstartbookLay.getVisibility() == View.GONE){
                                mBookingList.setVisibility(VISIBLE);
                            }
                        }
                    } else {
                        //Disconnected
                        mnoInternetLay.setVisibility(VISIBLE);

                        if(mstartbookLay.getVisibility() == View.GONE){
                            mBookingList.setVisibility(GONE);
                        }
                    }
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        ownuserid = mAuth.getCurrentUser().getUid();

        mUserBookingMade = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserBookingMade");

        mTalent = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        mUserMyTalentAcceptedBookings = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserMyTalentAcceptedBookings");

        mUserMyTalentPendingBookings = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserMyTalentPendingBookings");

        mUserTalentReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserTalentReview");

        mUserMyTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("MyTalent");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mBookingList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBookingList.setLayoutManager(mLayoutManager);


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TalentInfo, BlogViewHolder>(
                TalentInfo.class,
                R.layout.bookingmade_row,
                BlogViewHolder.class,
                mUserBookingMade.child(mAuth.getCurrentUser().getUid()).orderByChild("time")

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, TalentInfo model, final int position) {

                final String statusval = model.getstatus();
                final String postkey = getRef(position).getKey();
                final String city = model.getcity();
                final String posttitle = model.gettitle();
                final String postdescrip = model.getdesc();
                final String pressed = model.getpressed();
                final String uid = model.getuid();
                final String reviewpressedval = model.getreviewpressed();
                final String reviewedval = model.getreviewed();
                final String post_date = model.getdates();

                if (statusval != null && city ==null && posttitle ==null && postdescrip ==null ) {
                    mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(postkey).removeValue();
                    return;
                }

                final String postimage0;

                final String postimage = model.getpostimage();
                if (postimage.contains(" , ")) {
                    String postimages[] = postimage.split(" , ");
                    postimage0 = postimages[0];
                } else {
                    postimage0 = postimage;
                }

                String category = model.getcategory();
                String categorys[] = category.split(" / ");
                final String maincategory = categorys[0];
                final String subcategory = categorys[1];


                viewHolder.setRating(mTalent, city,maincategory,subcategory,postkey );
                viewHolder.setTitle_Descrip(posttitle, postdescrip);
                viewHolder.setPostImage(context.getApplicationContext(), postimage0);
                viewHolder.setBookStatus(post_date, statusval, reviewedval);
                viewHolder.setNewStatus(pressed);
                viewHolder.setNewReviewNewStatus(reviewpressedval);

                viewHolder.mpendingbookingCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showbookingform(uid, postkey,city,maincategory, subcategory, post_date );
                    }
                });

                viewHolder.macceptedCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(postkey).child("pressed").setValue("true");
                        viewHolder.macceptnotifiBadge.setVisibility(GONE);
                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewBookingsMade").setValue("false");
                        showbookingform(uid, postkey,city, maincategory, subcategory, post_date);
                    }
                });

                viewHolder.mupdateCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(postkey).child("pressed").setValue("true");
                        viewHolder.mupdatenotifiBadge.setVisibility(GONE);
                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewBookingsMade").setValue("false");
                        showbookingform(uid, postkey,city, maincategory, subcategory, post_date);
                    }
                });

                viewHolder.mrejectedCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(postkey).child("pressed").setValue("true");
                        viewHolder.mrejectnotifiBadge.setVisibility(GONE);
                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewBookingsMade").setValue("false");
                        showbookingform(uid, postkey,city, maincategory, subcategory, post_date);
                    }
                });


                viewHolder.mremoveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // custom dialog
                        final Dialog dialog = new Dialog(context);
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

                        hirebtn.setText("DELETE");
                        hirebtn.setTextColor(Color.parseColor("#ff669900"));
                        mdialogtxt.setText("Are you sure you to delete this booked talent? ");

                        dialog.show();

                        hirebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(postkey).removeValue();
                                viewHolder.cardview.setVisibility(GONE);

                                mUserMyTalentPendingBookings.child(uid).child(postkey).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            mUserMyTalentPendingBookings.child(uid).child(postkey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            decrementapplicantscount(uid, postkey);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

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
                });

                viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final ProgressDialog mProgressDialog;
                        mProgressDialog = new ProgressDialog(context,R.style.MyTheme);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        mProgressDialog.show();

                        new Thread()
                        {

                            public void run()
                            {

                                mTalent.child(city).child(maincategory).child(subcategory).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Intent detailintent = new Intent(context, TalentDetail.class);
                                            detailintent.putExtra("post_id", postkey);
                                            detailintent.putExtra("city_id", city);
                                            detailintent.putExtra("maincategory", maincategory);
                                            detailintent.putExtra("subcategory", subcategory);
                                            context.startActivity(detailintent);
                                            mProgressDialog.dismiss();
                                        }
                                        else{
                                            Intent jobdetailintent = new Intent(context, RemovedTalent.class);
                                            startActivity(jobdetailintent);
                                            mProgressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }.start();

                    }
                });

            }
        };

        mBookingList.setAdapter(firebaseRecyclerAdapter);

        mUserBookingMade.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mstartbookLay.setVisibility(GONE);
                }
                else{
                    mstartbookLay.setVisibility(VISIBLE);
                    mstarttxt.setText("Still haven't book any talents? Start looking for one today!");
                    mstartImg.setImageResource(R.drawable.talent_bookingsmade2);
                    mstartImg.setAlpha(0.8f);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    public void decrementapplicantscount(String uid, String postkey) {

        mUserMyTalent.child(uid).child(postkey).child("totalbookingcount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() != null) {
                    if((Long)currentData.getValue() == 0){
                        currentData.setValue(0);
                    }
                    else {
                        currentData.setValue((Long) currentData.getValue() - 1);
                    }
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    private void showbookingform(final String talentowner_uid, final String post_key,final String city, final String maincategory, final String subcategory, final String dateval) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.activity_hiredapplicant_hireform);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        final CardView macceptedCardView = (CardView) dialog.findViewById(R.id.acceptedCardView);
        final CardView mrejectedCardView = (CardView) dialog.findViewById(R.id.rejectedCardView);
        final CardView mpendingCardView = (CardView) dialog.findViewById(R.id.pendingCardView);
        final CardView mviewmoreCardView = (CardView) dialog.findViewById(R.id.viewmoreCardView);
        final CardView mreviewcardview = (CardView) dialog.findViewById(R.id.reviewcardview);

        final LinearLayout mreviewLay = (LinearLayout) dialog.findViewById(R.id.reviewLay);
        final LinearLayout mhoursLay = (LinearLayout) dialog.findViewById(R.id.hoursLay);

        final CircleImageView mpostImage = (CircleImageView) dialog.findViewById(R.id.postImage);

        final TextView mpostName = (TextView) dialog.findViewById(R.id.postName);
        final TextView mdatetxt = (TextView) dialog.findViewById(R.id.datetxt);
        final TextView mlocationtxt = (TextView) dialog.findViewById(R.id.locationtxt);
        final TextView mpostNumDates = (TextView) dialog.findViewById(R.id.postNumDates);
        final TextView mpostBasicPay = (TextView) dialog.findViewById(R.id.postBasicPay);
        final TextView mpostTotalBasicPay = (TextView) dialog.findViewById(R.id.postTotalBasicPay);
        final TextView mbasicratetxt = (TextView) dialog.findViewById(R.id.basicratetxt);
        final TextView mpostNumHours = (TextView) dialog.findViewById(R.id.postNumHours);
        final TextView mnumtxt = (TextView) dialog.findViewById(R.id.numtxt);
        final TextView mtextView = dialog.findViewById(R.id.textView);
        mtextView.setText("Talentee");
        final TextView macceptedtxt = (TextView) dialog.findViewById(R.id.acceptedtxt);
        macceptedtxt.setText("BOOKING ACCEPTED");
        final TextView mrejectedtxt = (TextView) dialog.findViewById(R.id.rejectedtxt);
        mrejectedtxt.setText("BOOKING REJECTED");
        final TextView mpendingtxt = dialog.findViewById(R.id.pendingtxt);
        mpendingtxt.setText("BOOKING PENDING");

        final TextView mpostTipsPay = (TextView) dialog.findViewById(R.id.postTipsPay);
        final TextView mpostTotalAllPay = (TextView) dialog.findViewById(R.id.postTotalAllPay);
        final TextView mpostPaymentDate = (TextView) dialog.findViewById(R.id.postPaymentDate);
        final TextView mpostAddNote = (TextView) dialog.findViewById(R.id.postAddNote);

        mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("basicpay")) {
                        mpostBasicPay.setText(dataSnapshot.child("basicpay").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("basictotalpay")) {
                        mpostTotalBasicPay.setText(dataSnapshot.child("basictotalpay").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("dates")) {
                        mdatetxt.setText(dataSnapshot.child("dates").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("location")) {
                        mlocationtxt.setText(dataSnapshot.child("location").getValue().toString());
                    }

                    if (dataSnapshot.hasChild("name")) {
                        mpostName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("numdates")) {
                        mpostNumDates.setText(dataSnapshot.child("numdates").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("numhours")) {
                        mhoursLay.setVisibility(VISIBLE);
                        mpostNumHours.setText(dataSnapshot.child("numhours").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("paymentdate")) {
                        mpostPaymentDate.setText(dataSnapshot.child("paymentdate").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("totalallpay")) {
                        mpostTotalAllPay.setText(dataSnapshot.child("totalallpay").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("tipspay")) {
                        mpostTipsPay.setText(dataSnapshot.child("tipspay").getValue().toString());
                    }
                    else {
                        mpostTipsPay.setText("0");
                    }
                    if (dataSnapshot.hasChild("additionalnote")) {
                        mpostAddNote.setText(dataSnapshot.child("additionalnote").getValue().toString());
                    }
                    else {
                        mpostAddNote.setText("-");
                    }
                    if (dataSnapshot.hasChild("spinnerrate") && dataSnapshot.hasChild("spinnercurrency")) {
                        String spinnerrate_val = dataSnapshot.child("spinnerrate").getValue().toString();
                        String spinnercurrency_val = dataSnapshot.child("spinnercurrency").getValue().toString();
                        mbasicratetxt.setText("Basic Pay " + spinnerrate_val + " (" + spinnercurrency_val + ")");
                        if (spinnerrate_val.equals("per month")) {
                            mnumtxt.setText("Num of Months");
                        }
                    }

                    if (dataSnapshot.hasChild("image")) {
                        if (dataSnapshot.child("image").getValue().toString().equals("default")) {

                            Glide.with(context).load(R.drawable.defaultprofile_pic)
                                    .centerCrop()
                                    .error(R.drawable.defaultprofile_pic)
                                    .placeholder(R.drawable.defaultprofile_pic)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .dontAnimate()
                                    .into(mpostImage);
                        } else  {

                            Glide.with(context).load(dataSnapshot.child("image").getValue().toString())
                                    .centerCrop()
                                    .error(R.drawable.defaultprofile_pic)
                                    .placeholder(R.drawable.defaultprofile_pic)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .dontAnimate()
                                    .into(mpostImage);
                        }
                    }
                    if (dataSnapshot.hasChild("status")) {

                        String status_val = dataSnapshot.child("status").getValue().toString();

                        if (status_val.equals("acceptedbooking")) {
                            macceptedCardView.setVisibility(VISIBLE);

                            if (dateval != null) {
                                String[] separated;
                                String lastdate;
                                Date enddate;

                                if (!dateval.contains("to")) {

                                    separated = dateval.split(" / ");

                                    lastdate = separated[separated.length - 1];
                                }
                                else if (dateval.contains("to")) {
                                    separated = dateval.split(" to ");

                                    lastdate = separated[1];
                                }

                                else {
                                    lastdate = dateval;
                                }

                                SimpleDateFormat dates = new SimpleDateFormat("dd MMM yy");

                                try {
                                    enddate = dates.parse(lastdate);

                                    final long tsLong = System.currentTimeMillis();
                                    Date datenow = new Date(tsLong);

                                    Calendar c = Calendar.getInstance();
                                    c.setTime(enddate);

                                    //If end date < time date NOW, show REVIEW
                                    if (c.getTime().compareTo(datenow) < 0) {
                                        mreviewLay.setVisibility(VISIBLE);
                                        mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(post_key).child("reviewpressed").setValue("true");
                                    }

                                } catch (Exception exception) {
                                    Log.e("DIDN'T WORK", "exception " + exception);
                                }
                            }
                        }
                        else if (status_val.equals("rejectedbooking")) {
                            mrejectedCardView.setVisibility(VISIBLE);
                        }
                        else {
                            mpendingCardView.setVisibility(VISIBLE);
                        }
                    }
                    else {
                        mpendingCardView.setVisibility(VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dialog.show();

        mviewmoreCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTalent.child(city).child(maincategory).child(subcategory).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            dialog.dismiss();
                            Intent bookintent = new Intent(context, BookingForm.class);
                            bookintent.putExtra("talentowner_uid", talentowner_uid);
                            bookintent.putExtra("post_key", post_key);
                            bookintent.putExtra("city", city);
                            bookintent.putExtra("maincategory", maincategory);
                            bookintent.putExtra("subcategory", subcategory);
                            bookintent.putExtra("update", "true");
                            context.startActivity(bookintent);
                        }
                        else {
                            Toast.makeText(context, "Talent has been removed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

        mreviewcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showreviewdialog(talentowner_uid, post_key);
            }
        });

    }

    private void showreviewdialog(final String talentowner_uid, final String post_key){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reviewdialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        final RatingBar muserratingbar = (RatingBar) dialog.findViewById(R.id.userratingbar);
        final Button postbtn = (Button) dialog.findViewById(R.id.postBtn);
        final Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
        final EditText mreviewtxt = (EditText) dialog.findViewById(R.id.reviewtxt);


        mUserTalentReview.child(talentowner_uid).child(post_key).child("Review").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    Long rating_long = (Long) dataSnapshot.child("rating").getValue();
                    muserratingbar.setRating(rating_long);

                    String reviewmessage = (String) dataSnapshot.child("reviewmessage").getValue();
                    mreviewtxt.setText(reviewmessage);

                    if (rating_long == 5) { reducedReviewCount = "Rate5"; }
                    else if (rating_long == 4) { reducedReviewCount = "Rate4"; }
                    else if (rating_long == 3) { reducedReviewCount = "Rate3"; }
                    else if (rating_long == 2) { reducedReviewCount = "Rate2"; }
                    else if (rating_long == 1) { reducedReviewCount = "Rate1"; }

                    editpost = true;
                }
                else{
                    editpost = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dialog.show();

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ratestar = 0;

                final String reviewval = mreviewtxt.getText().toString().trim();
                final float starval = muserratingbar.getRating();
                if(starval!=0){
                    ratestar = Math.round(starval);
                }
                if(ratestar == 0){
                    new AlertDialog.Builder(context)
                            .setTitle("Invalid Start Rating")
                            .setMessage("Star Rating has to be at least one")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    return;
                }
                else{

                    mProgress = new ProgressDialog(context);
                    mProgress.setMessage("Submitting..");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    if(ratestar == 5){ReviewCount = "Rate5";}
                    else if(ratestar == 4){ReviewCount = "Rate4";}
                    else if(ratestar == 3){ReviewCount = "Rate3";}
                    else if(ratestar == 2){ReviewCount = "Rate2";}
                    else if(ratestar == 1){ReviewCount = "Rate1";}

                    if (editpost) {
                        deleteandincrement(talentowner_uid, post_key, dialog, reviewval, reducedReviewCount, ReviewCount);
                    }
                    else {
                        incrementonly("false", talentowner_uid, post_key, dialog, reviewval, ReviewCount);
                    }

                }
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }


    private void incrementUserMyTalent_ReviewCountStar(final String deleted, final String talentowner_uid, final String post_key, final String city,final String maincategory, final String subcategory) {

        Log.e(TAG, "deleted " + deleted );

        mTalent.child(city).child(maincategory).child(subcategory).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("negatedtime") && dataSnapshot.hasChild("reviewcount")) {

                    Long reviewcount = (Long) dataSnapshot.child("reviewcount").getValue();
                    Long negatedtime = (Long) dataSnapshot.child("negatedtime").getValue();

                    Long new_reviewcount_negatedtime = -1* ((reviewcount * 10000000000L) + (-1*negatedtime));

                    mTalent.child(city).child(maincategory).child(subcategory).child(post_key).child("reviewcount_negatedtime").setValue(new_reviewcount_negatedtime);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (deleted.equals("false")) {

            mUserMyTalent.child(talentowner_uid).child(post_key).child("reviewcount").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                    } else {
                        currentData.setValue((Long) currentData.getValue() + 1);
                    }

                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });

            mTalent.child(city).child(maincategory).child(subcategory).child(post_key).child("reviewcount").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                    } else {
                        currentData.setValue((Long) currentData.getValue() + 1);
                    }

                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    mTalent.child(city).child(maincategory).child(subcategory).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("reviewcount_negatedtime") && dataSnapshot.hasChild("reviewcount")) {

                                Long reviewcount = (Long) dataSnapshot.child("reviewcount").getValue();
                                Long reviewcount_negatedtime = (Long) dataSnapshot.child("reviewcount_negatedtime").getValue();

                                Long new_reviewcount_negatedtime = -1* ((reviewcount * 10000000000L) + (-1*reviewcount_negatedtime));

                                Log.e(TAG, "new_reviewcount_negatedtime " + new_reviewcount_negatedtime );

                                mTalent.child(city).child(maincategory).child(subcategory).child(post_key).child("reviewcount_negatedtime").setValue(new_reviewcount_negatedtime);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        }

        mUserTalentReview.child(talentowner_uid).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    reviewcount5 = 0;
                    reviewcount4 = 0;
                    reviewcount3 = 0;
                    reviewcount2 = 0;
                    reviewcount1 = 0;
                    totalreviewcount = 0;
                    rateval = 0;

                    if(dataSnapshot.hasChild("Rate5")){
                        reviewcount5 = (Long)dataSnapshot.child("Rate5").getValue();
                    }
                    if(dataSnapshot.hasChild("Rate4")){
                        reviewcount4 = (Long)dataSnapshot.child("Rate4").getValue();
                    }
                    if(dataSnapshot.hasChild("Rate3")){
                        reviewcount3 = (Long)dataSnapshot.child("Rate3").getValue();
                    }
                    if(dataSnapshot.hasChild("Rate2")){
                        reviewcount2 = (Long)dataSnapshot.child("Rate2").getValue();
                    }
                    if(dataSnapshot.hasChild("Rate1")){
                        reviewcount1 = (Long)dataSnapshot.child("Rate1").getValue();
                    }
                    totalreviewcount = reviewcount5+reviewcount4+reviewcount3+reviewcount2+reviewcount1;
                }

                if (totalreviewcount !=0) {
                    long starcount = ((5*reviewcount5)+(4*reviewcount4)+(3*reviewcount3)+(2*reviewcount2)+(1*reviewcount1))/(totalreviewcount);
                    rateval = Math.round(starcount);
                    mUserMyTalent.child(talentowner_uid).child(post_key).child("reviewstar").setValue(rateval);
                    mTalent.child(city).child(maincategory).child(subcategory).child(post_key).child("reviewstar").setValue(rateval);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void incrementReviewCount_ReviewStar(final String deleted, final String talentowner_uid, final String post_key) {

        mUserMyTalent.child(talentowner_uid).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String city = dataSnapshot.child("city").getValue().toString();
                    String category = dataSnapshot.child("category").getValue().toString();
                    String categorys[] = category.split(" / ");
                    final String maincategory = categorys[0];
                    final String subcategory = categorys[1];

                    incrementUserMyTalent_ReviewCountStar(deleted, talentowner_uid, post_key, city, maincategory, subcategory);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void incrementonly(final String deleted, final String talentowner_uid, final String post_key, final Dialog dialog,final String reviewval, String ReviewCount) {

        final DatabaseReference newReview = mUserTalentReview.child(talentowner_uid).child(post_key).child("Review");

        mUserTalentReview.child(talentowner_uid).child(post_key).child(ReviewCount).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                mUserMyTalent.child(talentowner_uid).child(post_key).child("newreview").setValue("true");

                mUserBookingMade.child(mAuth.getCurrentUser().getUid()).child(post_key).child("reviewed").setValue("true");

                incrementReviewCount_ReviewStar(deleted,talentowner_uid, post_key);

                mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map< String, Object > reviewData = new HashMap<>();
                        final Long tsLong = System.currentTimeMillis()/1000;
                        reviewData.put("time", ServerValue.TIMESTAMP);
                        reviewData.put("negatedtime", (-1*tsLong));
                        reviewData.put("userimage",dataSnapshot.child("image").getValue());

                        if(TextUtils.isEmpty(reviewval)) {
                            reviewData.put("reviewmessage", "none");
                        }
                        else{
                            reviewData.put("reviewmessage",reviewval);
                        }

                        reviewData.put("username",dataSnapshot.child("name").getValue());
                        reviewData.put("rating",ratestar);

                        newReview.child(mAuth.getCurrentUser().getUid()).setValue(reviewData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mProgress.dismiss();
                                dialog.dismiss();
                                Toast.makeText(context, "Review Successfully Submitted!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void deleteandincrement(final String talentowner_uid, final String post_key, final Dialog dialog, final String reviewval, String reducedReviewCount, final String ReviewCount) {
        Log.d(TAG, "ReviewCount " + ReviewCount);
        Log.d(TAG, "reducedReviewCount " + reducedReviewCount);
        mUserTalentReview.child(talentowner_uid).child(post_key).child(reducedReviewCount).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() != null) {
                    currentData.setValue((Long) currentData.getValue() - 1);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                incrementonly("true", talentowner_uid, post_key, dialog, reviewval, ReviewCount);
            }
        });
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView, mupdatenotifiBadge, mrejectnotifiBadge, macceptnotifiBadge;
        CircleImageView post_image;
        RelativeLayout mremovedRlay;
        TextView mpostTitle, mpostDescrip, mreviewstar, mreviewcount, mnormaltxt ;
        CardView cardview, mpendingbookingCardView, macceptedCardView, mrejectedCardView, mupdateCardView;
        ImageButton mremoveBtn;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mupdatenotifiBadge = mView.findViewById(R.id.updatenotifiBadge);
            mrejectnotifiBadge = mView.findViewById(R.id.rejectnotifiBadge);
            macceptnotifiBadge = mView.findViewById(R.id.acceptnotifiBadge);

            mremovedRlay = mView.findViewById(R.id.removedRlay);

            post_image =  mView.findViewById(R.id.postImage);

            cardview =  mView.findViewById(R.id.cardview);
            mpendingbookingCardView =  mView.findViewById(R.id.pendingbookingCardView);
            macceptedCardView = mView.findViewById(R.id.acceptedCardView);
            mrejectedCardView =  mView.findViewById(R.id.rejectedCardView);
            mupdateCardView = mView.findViewById(R.id.updateCardView);

            mpostTitle =  mView.findViewById(R.id.postTitle);
            mpostDescrip = mView.findViewById(R.id.postDescrip);
            mreviewstar =  mView.findViewById(R.id.reviewstar);
            mreviewcount = mView.findViewById(R.id.reviewcount);
            mnormaltxt = mView.findViewById(R.id.normaltxt);

            mremoveBtn = mView.findViewById(R.id.removeBtn);
            mremoveBtn.setVisibility(VISIBLE);
        }

        public void setRating(DatabaseReference mTalent, String city, String maincategory, String subcategory, String postkey){
            if(city!=null && maincategory!=null & subcategory!=null & postkey!=null) {

                mTalent.child(city).child(maincategory).child(subcategory).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            Long reviewcount = (Long) dataSnapshot.child("reviewcount").getValue();
                            Long reviewstar = (Long) dataSnapshot.child("reviewstar").getValue();

                            mreviewstar.setText(String.valueOf(reviewstar));
                            mreviewcount.setText("(" + String.valueOf(reviewcount) + ")");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        public void setNewReviewNewStatus(String reviewpressedval) {

            if (reviewpressedval != null) {
                if (reviewpressedval.equals("false")) {
                    macceptnotifiBadge.setVisibility(VISIBLE);
                }
                else {
                    macceptnotifiBadge.setVisibility(GONE);
                }
            }
        }

        //Display red dot if job is shortlisted, rejected
        public void setNewStatus(final String pressed){
            if(pressed!=null){
                if (pressed.equals("false")) {
                    mupdatenotifiBadge.setVisibility(VISIBLE);
                    mrejectnotifiBadge.setVisibility(VISIBLE);
                    macceptnotifiBadge.setVisibility(VISIBLE);
                }
                else{
                    mupdatenotifiBadge.setVisibility(GONE);
                    mrejectnotifiBadge.setVisibility(GONE);
                    macceptnotifiBadge.setVisibility(GONE);
                }
            }
        }

        public void setTitle_Descrip(String title, String desc){

            if (title != null && desc != null) {
                mpostTitle.setText(title);
                mpostDescrip.setText(desc);
            }
        }


        public void setPostImage(Context ctx, String postimage){
            if (postimage != null) {

                Glide.with(ctx).load(postimage)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .error(R.drawable.error3)
                        .placeholder(R.drawable.loading_spinner)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(post_image);

            }
        }

        private void checkDates(String post_date, String reviewedval) {

            String[] separated;
            String lastdate;
            Date enddate;

            if (!post_date.contains("to")) {

                separated = post_date.split(" / ");

                lastdate = separated[separated.length - 1];
            }
            else {
                separated = post_date.split(" to ");

                lastdate = separated[1];
            }

            SimpleDateFormat dates = new SimpleDateFormat("dd MMM yy");

            try {
                enddate = dates.parse(lastdate);

                final long tsLong = System.currentTimeMillis();
                Date datenow = new Date(tsLong);

                Calendar c = Calendar.getInstance();
                c.setTime(enddate);

                //If end date < time date NOW, show REVIEW
                if (c.getTime().compareTo(datenow) < 0) {

                    if (reviewedval != null) {
                        if (reviewedval.equals("false")) {
                            mnormaltxt.setText("You can now review!");
                        }
                        else {
                            mnormaltxt.setText("Booking accepted");
                        }
                    }
                    else {
                        mnormaltxt.setText("You can now review!");
                    }

                }
                else {
                    mnormaltxt.setText("Booking accepted");
                }


            } catch (Exception exception) {
                Log.e("DIDN'T WORK", "exception " + exception);
            }
        }


        public void setBookStatus(String post_date, String statusval, final String reviewedval) {

                if (statusval != null) {

                    if (statusval.equals("removed")) {
                        mremovedRlay.setVisibility(VISIBLE);
                        mpendingbookingCardView.setVisibility(GONE);
                        macceptedCardView.setVisibility(GONE);
                        mrejectedCardView.setVisibility(GONE);
                        mupdateCardView.setVisibility(GONE);
                        mremoveBtn.setVisibility(VISIBLE);
                    }
                    else if (statusval.equals("booked")) {
                        mremovedRlay.setVisibility(GONE);
                        mpendingbookingCardView.setVisibility(VISIBLE);
                        macceptedCardView.setVisibility(GONE);
                        mrejectedCardView.setVisibility(GONE);
                        mupdateCardView.setVisibility(GONE);
                        mremoveBtn.setVisibility(VISIBLE);
                    }
                    else if (statusval.equals("acceptedbooking")) {

                        checkDates(post_date, reviewedval);

                        mremovedRlay.setVisibility(GONE);
                        mpendingbookingCardView.setVisibility(GONE);
                        macceptedCardView.setVisibility(VISIBLE);
                        mrejectedCardView.setVisibility(GONE);
                        mupdateCardView.setVisibility(GONE);
                        mremoveBtn.setVisibility(VISIBLE);
                    }
                    else if (statusval.equals("rejectedbooking")) {
                        mremovedRlay.setVisibility(GONE);
                        mpendingbookingCardView.setVisibility(GONE);
                        macceptedCardView.setVisibility(GONE);
                        mrejectedCardView.setVisibility(VISIBLE);
                        mupdateCardView.setVisibility(GONE);
                        mremoveBtn.setVisibility(VISIBLE);
                    }
                }


        }
    }
}

