package com.zjheng.jobseed.jobseed.ActivitiesScene;

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
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.ApplicantsScene.HireForm;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.RemovedJob;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.rating;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.drawable.postbtn;
import static com.zjheng.jobseed.jobseed.R.id.ratingval;
import static junit.runner.Version.id;

/**
 * Created by zhen on 5/5/2017.
 */

public class AppliedTab extends Fragment {

    private RecyclerView mAppliedList;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mstartapplyLay, mnoInternetLay;
    private FirebaseRecyclerAdapter<Job, BlogViewHolder> firebaseRecyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities , mJob, mUserShortlistNotification, mUserPostedHiredApplicants,
            mUserInfo, mUserAccount, mUserPosted, mUserReview, mUserHireNotification;

    private CardView mretryBtn;

    private String ownuserid, ReviewCount, reducedReviewCount;
    private ProgressDialog mProgress;

    private int ratestar = 0;
    private Boolean editpost = false;

    private static final String TAG = "AppliedTab";

    Activity context;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.appliedtab, container, false);

        context = getActivity();

        Log.d(TAG, "applied");

        mstartapplyLay = (RelativeLayout) rootView.findViewById(R.id.startapplyLay);
        mnoInternetLay = (RelativeLayout)rootView.findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)rootView.findViewById(R.id.retryBtn);
        mAppliedList = (RecyclerView)rootView.findViewById(R.id.appliedlist);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                //Connected
                mnoInternetLay.setVisibility(GONE);
                if(mstartapplyLay.getVisibility() == View.GONE){
                    mAppliedList.setVisibility(VISIBLE);
                }

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                //Connected
                mnoInternetLay.setVisibility(GONE);
                if(mstartapplyLay.getVisibility() == View.GONE){
                    mAppliedList.setVisibility(VISIBLE);
                }
            }
        } else {
            Log.d(TAG, "not connected");
            //Disconnected
            mnoInternetLay.setVisibility(VISIBLE);

            if(mstartapplyLay.getVisibility() == View.GONE){
                mAppliedList.setVisibility(GONE);
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
                            if(mstartapplyLay.getVisibility() == View.GONE){
                                mAppliedList.setVisibility(VISIBLE);
                            }

                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            //Connected
                            mnoInternetLay.setVisibility(GONE);
                            if(mstartapplyLay.getVisibility() == View.GONE){
                                mAppliedList.setVisibility(VISIBLE);
                            }
                        }
                    } else {
                        Log.d(TAG, "not connected");
                        //Disconnected
                        mnoInternetLay.setVisibility(VISIBLE);

                        if(mstartapplyLay.getVisibility() == View.GONE){
                            mAppliedList.setVisibility(GONE);
                        }
                    }
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        ownuserid = mAuth.getCurrentUser().getUid();

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");
        mUserActivities.child(ownuserid).child("Applied").keepSynced(true);

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mUserShortlistNotification = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ShortlistedNotification");

        mUserPostedHiredApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedHiredApplicants");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");

        mUserHireNotification = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("HireNotification");

        mAppliedList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mAppliedList.setLayoutManager(mLayoutManager);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Job,BlogViewHolder>(
                Job.class,
                R.layout.activitiesrow,
                BlogViewHolder.class,
                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").orderByChild("time")

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Job model, final int position) {

                final String postkey = getRef(position).getKey();
                final String city = model.getCity();
                final String userid = model.getUid();
                final String statusval = model.getstatus();
                final String dateval = model.getDate();
                final String reviewpressedval = model.getreviewpressed();
                final String reviewedval = model.getreviewed();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setCompany(model.getCompany());
                viewHolder.setpostImage(context.getApplicationContext(), model.getpostImage(), model.getstatus());
                viewHolder.setApplicationStatus(statusval,model.gettime(), reviewpressedval, reviewedval,model.getclosed(), model.getDate(), postkey, userid, ownuserid, mUserActivities);
                viewHolder.setNewStatus(model.getpressed());
                viewHolder.setNewReviewNewStatus(reviewpressedval);

                viewHolder.mpendingofferCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showhiredform(userid, postkey,statusval, dateval);
                    }
                });

                viewHolder.macceptedhiredCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.macceptedhirednotifiBadge.setVisibility(GONE);
                        showhiredform(userid, postkey,statusval, dateval);
                    }
                });

                viewHolder.mupdatehiredCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showhiredform(userid, postkey,statusval, dateval);
                    }
                });

                viewHolder.mrejectedhiredCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showhiredform(userid, postkey,statusval, dateval);
                    }
                });

                viewHolder.macceptCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setContentView(R.layout.applicantsdialog);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.gravity = Gravity.CENTER;

                        dialog.getWindow().setAttributes(lp);

                        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
                        TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
                        Button mhirebtn = (Button) dialog.findViewById(R.id.hireBtn);

                        mhirebtn.setText("ACCEPT");
                        cancelbtn.setText("CANCEL");
                        mhirebtn.setTextColor(Color.parseColor("#ff669900"));
                        mdialogtxt.setText("Are you sure you want to accept the offer?");

                        dialog.show();

                        mhirebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mUserActivities.child(userid).child("NewMainNotification").setValue("true");
                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey).child("status").setValue("acceptedoffer");
                                mUserPostedHiredApplicants.child(userid).child(postkey).child(mAuth.getCurrentUser().getUid()).child("offerstatus").setValue("accepted");
                                mUserPostedHiredApplicants.child(userid).child(postkey).child(mAuth.getCurrentUser().getUid()).child("pressed").setValue("false");
                                mUserActivities.child(userid).child("NewPosted").setValue("true");
                                mUserPosted.child(userid).child(postkey).child("pressed").setValue("false");
                                mUserActivities.child(userid).child("NewApplicant").setValue("true");
                                viewHolder.macceptedhiredCardView.setVisibility(VISIBLE);
                                viewHolder.mhireCardView.setVisibility(GONE);
                                viewHolder.mrejectedhiredCardView.setVisibility(GONE);
                                viewHolder.mupdatehiredCardView.setVisibility(GONE);
                                deleteHiredNotifications();
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

                viewHolder.mrejectCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setContentView(R.layout.applicantsdialog);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.gravity = Gravity.CENTER;

                        dialog.getWindow().setAttributes(lp);

                        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
                        TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
                        Button mrejectbtn = (Button) dialog.findViewById(R.id.hireBtn);

                        mrejectbtn.setText("REJECT");
                        cancelbtn.setText("CANCEL");
                        mrejectbtn.setTextColor(Color.parseColor("#ffff4444"));
                        mdialogtxt.setText("Are you sure you want to reject the offer?");

                        dialog.show();

                        mrejectbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mUserActivities.child(userid).child("NewMainNotification").setValue("true");
                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey).child("status").setValue("rejectedoffer");
                                mUserPostedHiredApplicants.child(userid).child(postkey).child(mAuth.getCurrentUser().getUid()).child("offerstatus").setValue("rejected");
                                mUserPostedHiredApplicants.child(userid).child(postkey).child(mAuth.getCurrentUser().getUid()).child("pressed").setValue("false");
                                mUserActivities.child(userid).child("NewPosted").setValue("true");
                                mUserPosted.child(userid).child(postkey).child("pressed").setValue("false");
                                mUserActivities.child(userid).child("NewApplicant").setValue("true");
                                viewHolder.mrejectedhiredCardView.setVisibility(VISIBLE);
                                viewHolder.mhireCardView.setVisibility(GONE);
                                viewHolder.macceptedhiredCardView.setVisibility(GONE);
                                viewHolder.mupdatehiredCardView.setVisibility(GONE);
                                deleteHiredNotifications();
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
                        mdialogtxt.setText("Delete this applied job post?");

                        dialog.show();

                        hirebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (statusval != null) {
                                    if (statusval.equals("pendingoffer") || statusval.equals("changedoffer")) {
                                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Shortlisted").child(postkey).setValue("true");
                                    }
                                    else if (statusval.equals("acceptedoffer")) {
                                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Hired").child(postkey).setValue("true");
                                    }
                                    else if (statusval.equals("rejectedoffer")) {
                                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("RejectedOffer").child(postkey).setValue("true");
                                    }
                                    else if (statusval.equals("shortlisted")) {
                                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Shortlisted").child(postkey).setValue("true");
                                    }
                                    mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey).removeValue();
                                }

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

                        Log.d(TAG, "cardview pressed");

                        //Disappear the red dot
                        viewHolder.mchatnotifiBadge.setVisibility(GONE);

                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewApplied").setValue("false");
                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(postkey).child("pressed").setValue("true");

                        //Retrieve notifications key stored inside mUserActivities/uid/ShortlistedNotification, and delete the keys at mUserShortListNotification, also delete itself  location later
                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("ShortListedNotification").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                    final String notificationkey = userSnaphot.getKey();
                                    if(notificationkey!=null) {
                                        mUserShortlistNotification.child("ShortListed").child(notificationkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("ShortListedNotification").child(notificationkey).removeValue();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        final ProgressDialog mProgressDialog;
                        mProgressDialog = new ProgressDialog(context,R.style.MyTheme);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        mProgressDialog.show();

                        new Thread()
                        {

                            public void run()
                            {
                                mJob.child(city).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Intent jobdetailintent = new Intent(context, JobDetail.class);
                                            jobdetailintent.putExtra("post_id", postkey);
                                            jobdetailintent.putExtra("city_id", city);
                                            startActivity(jobdetailintent);
                                            mProgressDialog.dismiss();
                                        }
                                        else{
                                            Intent jobdetailintent = new Intent(context, RemovedJob.class);
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

        mAppliedList.setAdapter(firebaseRecyclerAdapter);


        //Check if there are any applied jobs, if no, display Start Applying Now RelativeLayout
        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mstartapplyLay.setVisibility(GONE);
                }
                else{
                    mstartapplyLay.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }


    private void deleteHiredNotifications() {
        //Retrieve notifications key stored inside mUserActivities/uid/ShortlistedNotification, and delete the keys at mUserShortListNotification, also delete itself  location later
        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("HiredNotification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                    final String notificationkey = userSnaphot.getKey();
                    if(notificationkey!=null) {
                        mUserHireNotification.child("Hire").child(notificationkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("HiredNotification").child(notificationkey).removeValue();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showhiredform(final String hirer_uid,final String post_key, final String statusval, final String dateval) {

        final Dialog mdialog = new Dialog(context);
        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdialog.setCanceledOnTouchOutside(true);
        mdialog.setContentView(R.layout.activity_applied_hireform);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mdialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        mdialog.getWindow().setAttributes(lp);

        final CardView mhireCardView = (CardView) mdialog.findViewById(R.id.hireCardView);
        final CardView mrejectedCardView = (CardView) mdialog.findViewById(R.id.rejectedCardView);
        final CardView macceptedCardView = (CardView) mdialog.findViewById(R.id.acceptedCardView);
        final CardView mrejectOfferCardView = (CardView) mdialog.findViewById(R.id.rejectOfferCardView);
        final CardView macceptOfferCardView = (CardView) mdialog.findViewById(R.id.acceptOfferCardView);
        final CardView mreviewcardview = (CardView) mdialog.findViewById(R.id.reviewcardview);

        final LinearLayout mreviewLay = (LinearLayout) mdialog.findViewById(R.id.reviewLay);
        final LinearLayout mhoursLay = (LinearLayout) mdialog.findViewById(R.id.hoursLay);

        final CircleImageView mpostImage = (CircleImageView) mdialog.findViewById(R.id.postImage);

        final TextView mpostName = (TextView) mdialog.findViewById(R.id.postName);
        final TextView mdatetxt = (TextView) mdialog.findViewById(R.id.datetxt);
        final TextView mlocationtxt = (TextView) mdialog.findViewById(R.id.locationtxt);
        final TextView mpostNumDates = (TextView) mdialog.findViewById(R.id.postNumDates);
        final TextView mpostBasicPay = (TextView) mdialog.findViewById(R.id.postBasicPay);
        final TextView mpostTotalBasicPay = (TextView) mdialog.findViewById(R.id.postTotalBasicPay);
        final TextView mbasicratetxt = (TextView) mdialog.findViewById(R.id.basicratetxt);
        final TextView mpostNumHours = (TextView) mdialog.findViewById(R.id.postNumHours);
        final TextView mnumtxt = (TextView) mdialog.findViewById(R.id.numtxt);

        final TextView mpostTipsPay = (TextView) mdialog.findViewById(R.id.postTipsPay);
        final TextView mpostTotalAllPay = (TextView) mdialog.findViewById(R.id.postTotalAllPay);
        final TextView mpostPaymentDate = (TextView) mdialog.findViewById(R.id.postPaymentDate);
        final TextView mpostAddNote = (TextView) mdialog.findViewById(R.id.postAddNote);

        displayHirerName_Image(mpostName, mpostImage, hirer_uid);

        mUserPostedHiredApplicants.child(hirer_uid).child(post_key).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("basicpay")) {
                        mpostBasicPay.setText(dataSnapshot.child("basicpay").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("basictotalpay")) {
                        mpostTotalBasicPay.setText(dataSnapshot.child("basictotalpay").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("date")) {
                        mdatetxt.setText(dataSnapshot.child("date").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("location")) {
                        mlocationtxt.setText(dataSnapshot.child("location").getValue().toString());
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (statusval.equals("pendingoffer") || statusval.equals("changedoffer")) {
            mhireCardView.setVisibility(VISIBLE);
        }
        else if (statusval.equals("acceptedoffer")) {

            macceptedCardView.setVisibility(VISIBLE);

            if (dateval != null) {
                String[] separated;
                String lastdate;
                Date enddate;

                if (dateval.contains("/")) {

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
                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(post_key).child("reviewpressed").setValue("true");
                    }

                } catch (Exception exception) {
                    Log.e("DIDN'T WORK", "exception " + exception);
                }
            }
        }
        else if (statusval.equals("rejectedoffer")) {
            mrejectedCardView.setVisibility(VISIBLE);
        }

        macceptOfferCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.applicantsdialog);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);

                Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
                TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
                Button mhirebtn = (Button) dialog.findViewById(R.id.hireBtn);

                mhirebtn.setText("ACCEPT");
                cancelbtn.setText("CANCEL");
                mhirebtn.setTextColor(Color.parseColor("#ff669900"));
                mdialogtxt.setText("Are you sure you want to accept the offer?");

                dialog.show();

                mhirebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mUserActivities.child(hirer_uid).child("NewMainNotification").setValue("true");
                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(post_key).child("status").setValue("acceptedoffer");
                        mUserPostedHiredApplicants.child(hirer_uid).child(post_key).child(mAuth.getCurrentUser().getUid()).child("offerstatus").setValue("accepted");
                        mUserPostedHiredApplicants.child(hirer_uid).child(post_key).child(mAuth.getCurrentUser().getUid()).child("pressed").setValue("false");
                        mUserActivities.child(hirer_uid).child("NewPosted").setValue("true");
                        mUserPosted.child(hirer_uid).child(post_key).child("pressed").setValue("false");
                        mUserActivities.child(hirer_uid).child("NewApplicant").setValue("true");
                        macceptedCardView.setVisibility(VISIBLE);
                        mhireCardView.setVisibility(GONE);
                        mrejectedCardView.setVisibility(GONE);
                        deleteHiredNotifications();
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

        mrejectOfferCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.applicantsdialog);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);

                Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
                TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
                Button mrejectbtn = (Button) dialog.findViewById(R.id.hireBtn);

                mrejectbtn.setText("REJECT");
                cancelbtn.setText("CANCEL");
                mrejectbtn.setTextColor(Color.parseColor("#ffff4444"));
                mdialogtxt.setText("Are you sure you want to reject the offer?");

                dialog.show();

                mrejectbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mUserActivities.child(hirer_uid).child("NewMainNotification").setValue("true");
                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(post_key).child("status").setValue("rejectedoffer");
                        mUserPostedHiredApplicants.child(hirer_uid).child(post_key).child(mAuth.getCurrentUser().getUid()).child("offerstatus").setValue("rejected");
                        mUserPostedHiredApplicants.child(hirer_uid).child(post_key).child(mAuth.getCurrentUser().getUid()).child("pressed").setValue("false");
                        mUserActivities.child(hirer_uid).child("NewPosted").setValue("true");
                        mUserPosted.child(hirer_uid).child(post_key).child("pressed").setValue("false");
                        mUserActivities.child(hirer_uid).child("NewApplicant").setValue("true");
                        mrejectedCardView.setVisibility(VISIBLE);
                        mhireCardView.setVisibility(GONE);
                        macceptedCardView.setVisibility(GONE);
                        deleteHiredNotifications();
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


        mreviewcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showreviewdialog(hirer_uid, post_key);
            }
        });

        mdialog.show();

    }

    private void showreviewdialog(final String userid, final String post_key){

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


        mUserReview.child(userid).child("Review").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        deleteandincrement(userid, dialog,reviewval,reducedReviewCount,ReviewCount, post_key);
                    }
                    else {
                        incrementonly(userid, dialog,reviewval,ReviewCount, post_key);
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

    public void incrementonly(final String userid,final Dialog dialog,final String reviewval, String ReviewCount,final String post_key) {
        final DatabaseReference newReview = mUserReview.child(userid).child("Review");
        Log.d(TAG, "ReviewCount " + ReviewCount);
        mUserReview.child(userid).child(ReviewCount).runTransaction(new Transaction.Handler() {
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

                mUserReview.child(userid).child("Notification").setValue("true");

                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Applied").child(post_key).child("reviewed").setValue("true");

                mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map< String, Object > reviewData = new HashMap<>();
                        reviewData.put("time", ServerValue.TIMESTAMP);
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

    public void deleteandincrement(final String userid,final Dialog dialog, final String reviewval, String reducedReviewCount, final String ReviewCount, final String post_key) {
        Log.d(TAG, "ReviewCount " + ReviewCount);
        Log.d(TAG, "reducedReviewCount " + reducedReviewCount);
        mUserReview.child(userid).child(reducedReviewCount).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() != null) {
                    currentData.setValue((Long) currentData.getValue() - 1);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                incrementonly(userid,dialog,reviewval,ReviewCount, post_key);
            }
        });
    }

    private void displayHirerName_Image(final TextView mpostName, final CircleImageView mpostImage, final String hirer_uid) {
        mUserInfo.child(hirer_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("UserImage")) {
                    String user_image = dataSnapshot.child("UserImage").getValue().toString();

                    if (user_image.equals("default")) {

                        Glide.with(context).load(R.drawable.defaultprofile_pic)
                                .centerCrop()
                                .error(R.drawable.defaultprofile_pic)
                                .placeholder(R.drawable.defaultprofile_pic)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .into(mpostImage);
                    } else  {

                        Glide.with(context).load(user_image)
                                .centerCrop()
                                .error(R.drawable.defaultprofile_pic)
                                .placeholder(R.drawable.defaultprofile_pic)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .into(mpostImage);
                    }
                }

                else{
                    mUserAccount.child(hirer_uid).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String user_image = dataSnapshot.getValue().toString();

                            if (user_image.equals("default")) {

                                Glide.with(context).load(R.drawable.defaultprofile_pic)
                                        .centerCrop()
                                        .error(R.drawable.defaultprofile_pic)
                                        .placeholder(R.drawable.defaultprofile_pic)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .dontAnimate()
                                        .into(mpostImage);
                            } else  {

                                Glide.with(context).load(user_image)
                                        .centerCrop()
                                        .error(R.drawable.defaultprofile_pic)
                                        .placeholder(R.drawable.defaultprofile_pic)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .dontAnimate()
                                        .into(mpostImage);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(dataSnapshot.hasChild("Name")) {
                    String user_name = dataSnapshot.child("Name").getValue().toString();
                    mpostName.setText(user_name);
                }

                else{
                    mUserAccount.child(hirer_uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String user_name = dataSnapshot.getValue().toString();
                            mpostName.setText(user_name);
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

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        CircleImageView post_image;
        RelativeLayout mRlayout, mwaitingRlay, mshortlistedRlay, mrejectedRlay, mremovedRlay, mprofileLay;
        TextView post_desc, mtimetxt, mclosedtext, mnormaltxt;
        ProgressBar mprogressbar;
        CardView cardview, mhireCardView, macceptedhiredCardView, mrejectedhiredCardView, mupdatehiredCardView,
                mpendingofferCardView, mrejectCardView, macceptCardView;
        View mchatnotifiBadge,mchatnotifiBadge1, mclosedview, mhirednotifiBadge, macceptedhirednotifiBadge;
        CountDownTimer mCountDownTimer;
        ImageButton mremoveBtn;
        ImageView mhirebadgeImg;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mRlayout = (RelativeLayout) mView.findViewById(R.id.Rlayout);
            mprofileLay= (RelativeLayout) mView.findViewById(R.id.profileLay);
            mremovedRlay = (RelativeLayout) mView.findViewById(R.id.removedRlay);
            mwaitingRlay = (RelativeLayout) mView.findViewById(R.id.waitingRlay);
            mshortlistedRlay = (RelativeLayout) mView.findViewById(R.id.shortlistedRlay);
            mrejectedRlay = (RelativeLayout) mView.findViewById(R.id.rejectedRlay);

            cardview = (CardView) mView.findViewById(R.id.cardview);

            mhireCardView = (CardView) mView.findViewById(R.id.hireCardView);
            mpendingofferCardView = (CardView) mView.findViewById(R.id.pendingofferCardView);
            mrejectCardView = (CardView) mView.findViewById(R.id.rejectCardView);
            macceptCardView = (CardView) mView.findViewById(R.id.acceptCardView);

            macceptedhiredCardView = (CardView) mView.findViewById(R.id.acceptedhiredCardView);
            mrejectedhiredCardView = (CardView) mView.findViewById(R.id.rejectedhiredCardView);
            mupdatehiredCardView = (CardView) mView.findViewById(R.id.updatehiredCardView);

            post_image = (CircleImageView) mView.findViewById(R.id.postImage);

            mprogressbar = (ProgressBar)mView.findViewById(R.id.progressbar);

            mtimetxt = (TextView) mView.findViewById(R.id.timetxt);
            mclosedtext = (TextView) mView.findViewById(R.id.closedtext);
            mnormaltxt = (TextView) mView.findViewById(R.id.normaltxt);

            mchatnotifiBadge = (View)mView.findViewById(R.id.chatnotifiBadge);
            mchatnotifiBadge1 = (View)mView.findViewById(R.id.chatnotifiBadge1);
            mclosedview = (View)mView.findViewById(R.id.closedview);
            mhirednotifiBadge = (View)mView.findViewById(R.id.hirednotifiBadge);
            macceptedhirednotifiBadge = (View)mView.findViewById(R.id.acceptedhirednotifiBadge);

            mremoveBtn = (ImageButton)mView.findViewById(R.id.removeBtn);
            mhirebadgeImg = (ImageView)mView.findViewById(R.id.hirebadgeImg);
            mhirebadgeImg.setVisibility(GONE);
        }

        public void setNewReviewNewStatus(String reviewpressedval) {

            Log.e(TAG, "reviewpressedval "+ reviewpressedval);
            if (reviewpressedval != null) {
                if (reviewpressedval.equals("false")) {
                    macceptedhirednotifiBadge.setVisibility(VISIBLE);
                }
                else {
                    macceptedhirednotifiBadge.setVisibility(GONE);
                }
            }
        }

        //Display red dot if job is shortlisted, rejected
        public void setNewStatus(final String pressed){
            if(pressed!=null){
                if (pressed.equals("false")) {
                    mchatnotifiBadge.setVisibility(VISIBLE);
                    mchatnotifiBadge1.setVisibility(VISIBLE);
                }
                else{
                    mchatnotifiBadge.setVisibility(GONE);
                    mchatnotifiBadge1.setVisibility(GONE);
                }
            }
        }

        private void checkClosed(final String closed) {

            if (closed !=null) {
                if (closed.equals("true")) {
                    mclosedtext.setVisibility(VISIBLE);
                    mclosedview.setVisibility(VISIBLE);
                }
                else {
                    mclosedtext.setVisibility(GONE);
                    mclosedview.setVisibility(GONE);
                }
            }
        }

        private void hidehiringDetails() {

            mupdatehiredCardView.setVisibility(GONE);
            mhireCardView.setVisibility(GONE);
            macceptedhiredCardView.setVisibility(GONE);
            mrejectedhiredCardView.setVisibility(GONE);
            mhirebadgeImg.setVisibility(GONE);
        }

        private void hideotherdetails() {

            mshortlistedRlay.setVisibility(GONE);
            mRlayout.setAlpha(1f);
            mrejectedRlay.setVisibility(GONE);
            mwaitingRlay.setVisibility(GONE);
            mprogressbar.setVisibility(View.GONE);
            mprogressbar.setProgress(0);
            mremovedRlay.setVisibility(GONE);
            mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
        }

        private void checkDates(String post_date, String reviewedval) {

            String[] separated;
            String lastdate;
            Date enddate;

            if (post_date.contains("/")) {

                separated = post_date.split(" / ");

                lastdate = separated[separated.length - 1];
            }
            else if (post_date.contains("to")) {
                separated = post_date.split(" to ");

                lastdate = separated[1];
            }
            else {
                lastdate = post_date;
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
                            mnormaltxt.setText(" You have accepted job offer");
                        }
                    }
                    else {
                        mnormaltxt.setText("You can now review!");
                    }

                }
                else {
                    mnormaltxt.setText(" You have accepted job offer");
                }


            } catch (Exception exception) {
                Log.e("DIDN'T WORK", "exception " + exception);
            }
        }



        public void setApplicationStatus(final String statusval, final Long time, final String reviewpressedval, final String reviewedval, final String closed, final String date,
                                         final String postkey, final String jobownerrid, final String ownuserid, final DatabaseReference mUserActivities){

            mclosedtext.setVisibility(GONE);
            mclosedview.setVisibility(GONE);
            mRlayout.setAlpha(1f);

            Log.d(TAG, "statusval HERE = " + statusval);

            if (time == null && jobownerrid == null) {
                cardview.setVisibility(GONE);
                mUserActivities.child(ownuserid).child("Applied").child(postkey).removeValue();
            }

            if (statusval != null ) {

                if (statusval.equals("removed")) {
                    mRlayout.setAlpha(0.7f);
                    mclosedtext.setVisibility(GONE);
                    mclosedview.setVisibility(GONE);
                    mremovedRlay.setVisibility(VISIBLE);
                    mremoveBtn.setVisibility(VISIBLE);

                    mshortlistedRlay.setVisibility(GONE);
                    mrejectedRlay.setVisibility(GONE);
                    mwaitingRlay.setVisibility(GONE);
                    mprogressbar.setVisibility(View.GONE);
                    mprogressbar.setProgress(0);
                    mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
                    return;
                }

                if (closed != null && time != null) {
                    if (statusval.equals("removed")) {
                        mRlayout.setAlpha(0.7f);
                        mclosedtext.setVisibility(GONE);
                        mclosedview.setVisibility(GONE);
                        mremovedRlay.setVisibility(VISIBLE);
                        mremoveBtn.setVisibility(VISIBLE);

                        mshortlistedRlay.setVisibility(GONE);
                        mrejectedRlay.setVisibility(GONE);
                        mwaitingRlay.setVisibility(GONE);
                        mprogressbar.setVisibility(View.GONE);
                        mprogressbar.setProgress(0);
                        mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
                        return;
                    }

                    if (statusval.equals("applied")) {

                        Log.d(TAG, "applied");

                        if (closed.equals("false")) {

                            hidehiringDetails();

                            mremoveBtn.setVisibility(GONE);
                            mRlayout.setAlpha(1f);
                            mwaitingRlay.setVisibility(VISIBLE);
                            mprogressbar.setVisibility(VISIBLE);
                            mrejectedRlay.setVisibility(GONE);
                            mshortlistedRlay.setVisibility(GONE);
                            mremovedRlay.setVisibility(GONE);
                            mprofileLay.setBackgroundResource(R.color.colorTransparent);

                            Date d = new Date(time);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(d);
                            final long endtimeinmilliseconds = cal.getTimeInMillis()+(24*60*60*1000);
                            Log.d(TAG, "endtimeinmilliseconds "+endtimeinmilliseconds);

                            final long tsLong = System.currentTimeMillis();
                            Log.d(TAG, "tsLong "+tsLong);
                            Date datenow = new Date(tsLong);

                            final long timeleft = endtimeinmilliseconds - tsLong;

                            int mHourLeft   = (int) ((timeleft / (1000*60*60)) % 24);

                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            c.add(Calendar.HOUR, 24);


                            //If applied starting time date + 24 hours < time date NOW, stop timer and display REJECTED
                            if (c.getTime().compareTo(datenow) < 0) {
                                mremoveBtn.setVisibility(VISIBLE);
                                mrejectedRlay.setVisibility(VISIBLE);
                                mwaitingRlay.setVisibility(GONE);
                                mprogressbar.setVisibility(View.GONE);
                                mprogressbar.setProgress(0);
                                mshortlistedRlay.setVisibility(GONE);
                                mremovedRlay.setVisibility(GONE);
                                mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
                                if (mCountDownTimer!= null) {
                                    mCountDownTimer.cancel();
                                }
                            } else {
                                mprogressbar.setMax(100);
                                float fractions = (float) mHourLeft / (float) 24;
                                mprogressbar.setProgress((int) (fractions * 100));

                                if (mCountDownTimer!= null) {
                                    mCountDownTimer.cancel();
                                }

                                mCountDownTimer = new CountDownTimer(timeleft, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                        long seconds = millisUntilFinished / 1000;
                                        long minutes = seconds / 60;
                                        long hours = minutes / 60;

                                        String time = String.format("%02d:%02d:%02d", hours % 24, minutes % 60 , seconds % 60);
                                        mtimetxt.setText(time);

                                        int mHourTick   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
                                        float fraction = (float) mHourTick / (float) 24;
                                        mprogressbar.setProgress((int) (fraction * 100));

                                    }

                                    @Override
                                    public void onFinish() {
                                        mremoveBtn.setVisibility(VISIBLE);
                                        mrejectedRlay.setVisibility(VISIBLE);
                                        mwaitingRlay.setVisibility(GONE);
                                        mprogressbar.setVisibility(View.GONE);
                                        mshortlistedRlay.setVisibility(GONE);
                                        mremovedRlay.setVisibility(GONE);
                                        mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);

                                        mUserActivities.child(ownuserid).child("Applied").child(postkey).child("status").setValue("appliedrejected");
                                    }
                                };

                                mCountDownTimer.start();
                            }
                        }

                    }

                    else if (statusval.equals("appliedrejected")) {

                        Log.d(TAG, "appliedrejected");

                        hidehiringDetails();
                        checkClosed(closed);

                        mremoveBtn.setVisibility(VISIBLE);
                        mRlayout.setAlpha(1f);
                        mrejectedRlay.setVisibility(VISIBLE);
                        mwaitingRlay.setVisibility(GONE);
                        mprogressbar.setVisibility(View.GONE);
                        mprogressbar.setProgress(0);
                        mshortlistedRlay.setVisibility(GONE);
                        mremovedRlay.setVisibility(GONE);
                        mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
                    }

                    else if (statusval.equals("shortlisted")) {

                        Log.d(TAG, "shortlisted");

                        hidehiringDetails();
                        checkClosed(closed);

                        mremoveBtn.setVisibility(VISIBLE);
                        mRlayout.setAlpha(1f);
                        mshortlistedRlay.setVisibility(VISIBLE);
                        mrejectedRlay.setVisibility(GONE);
                        mwaitingRlay.setVisibility(GONE);
                        mprogressbar.setVisibility(View.GONE);
                        mprogressbar.setProgress(0);
                        mremovedRlay.setVisibility(GONE);
                        mprofileLay.setBackgroundResource(R.drawable.applicant_grey_round);
                    }
                    else if (statusval.equals("acceptedoffer")) {

                        Log.d(TAG, "acceptedoffer");

                        hideotherdetails();
                        checkClosed(closed);

                        checkDates(date, reviewedval);

                        macceptedhiredCardView.setVisibility(VISIBLE);
                        mhireCardView.setVisibility(GONE);
                        mrejectedhiredCardView.setVisibility(GONE);
                        mupdatehiredCardView.setVisibility(GONE);
                        mhirebadgeImg.setVisibility(GONE);
                        mremoveBtn.setVisibility(VISIBLE);
                    }
                    else if (statusval.equals("rejectedoffer")) {

                        Log.d(TAG, "rejectedoffer");

                        hideotherdetails();
                        checkClosed(closed);

                        mrejectedhiredCardView.setVisibility(VISIBLE);
                        mhireCardView.setVisibility(GONE);
                        macceptedhiredCardView.setVisibility(GONE);
                        mupdatehiredCardView.setVisibility(GONE);
                        mhirebadgeImg.setVisibility(GONE);
                        mremoveBtn.setVisibility(VISIBLE);
                    }
                    else if (statusval.equals("pendingoffer")) {

                        Log.d(TAG, "pendingoffer");

                        hideotherdetails();
                        checkClosed(closed);

                        mhireCardView.setVisibility(VISIBLE);
                        macceptedhiredCardView.setVisibility(GONE);
                        mrejectedhiredCardView.setVisibility(GONE);
                        mupdatehiredCardView.setVisibility(GONE);
                        mhirebadgeImg.setVisibility(VISIBLE);
                        mremoveBtn.setVisibility(GONE);
                    }
                    else if (statusval.equals("changedoffer")) {

                        Log.d(TAG, "changedoffer");

                        hideotherdetails();
                        checkClosed(closed);

                        mupdatehiredCardView.setVisibility(VISIBLE);
                        mhireCardView.setVisibility(GONE);
                        macceptedhiredCardView.setVisibility(GONE);
                        mrejectedhiredCardView.setVisibility(GONE);
                        mhirebadgeImg.setVisibility(GONE);
                        mremoveBtn.setVisibility(GONE);
                    }
                }
            }



        }

        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.postName);
            post_title.setText(title);
            Log.d(TAG, "APPLIED title" + title);
        }
        public void setDesc(String desc){
            post_desc = (TextView)mView.findViewById(R.id.postDescrip);
            post_desc.setText(desc);
        }
        public void setCompany(String company){
            TextView post_company = (TextView)mView.findViewById(R.id.postCompany);
            post_company.setText(company);
        }
        public void setpostImage(Context ctx, String postimage, String status){

            if(status!=null && postimage != null){
                if(status.equals("removed")){
                    post_image.setImageResource(R.drawable.error3);
                }
                else{

                    Glide.with(ctx).load(postimage)
                            .thumbnail(0.5f)
                            .fitCenter()
                            .error(R.drawable.error3)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(post_image);

                }
            }
            else{
                post_image.setImageResource(R.drawable.error3);
            }
        }
    }
}

