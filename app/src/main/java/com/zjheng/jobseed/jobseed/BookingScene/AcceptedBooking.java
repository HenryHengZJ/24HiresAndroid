package com.zjheng.jobseed.jobseed.BookingScene;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUser;
import com.zjheng.jobseed.jobseed.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.zjheng.jobseed.jobseed.R.id.cardview;
import static com.zjheng.jobseed.jobseed.R.id.postRates;
import static com.zjheng.jobseed.jobseed.R.id.postVenue;

public class AcceptedBooking extends Fragment {

    private RecyclerView mApplicantList;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mstartapplyLay;

    private FirebaseAuth mAuth;
    private DatabaseReference mTalent, mUserInfo, mUserMyTalentAcceptedBookings, mUserActivities,
            mUserBookingMade, mUserTalentReview, mUserMyTalent, mUserAccount;

    private TextView mnotxt;

    private static final String TAG = "AcceptedBooking";

    private String ownuserid, post_key, ReviewCount, reducedReviewCount;

    private ProgressDialog mProgress;

    private int ratestar = 0;
    private Boolean editpost = false;

    Activity context;
    View rootView;

    public static AcceptedBooking newInstance(String post_id) {
        AcceptedBooking result = new AcceptedBooking();
        Bundle bundle = new Bundle();
        bundle.putString("post_id", post_id);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        post_key = bundle.getString("post_id");

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_applicants_list, container, false);

        context = getActivity();

        mAuth = FirebaseAuth.getInstance();

        ownuserid = mAuth.getCurrentUser().getUid();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserMyTalentAcceptedBookings = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserMyTalentAcceptedBookings");

        mTalent = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserBookingMade = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserBookingMade");

        mUserTalentReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserTalentReview");

        mUserMyTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("MyTalent");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mstartapplyLay = (RelativeLayout) rootView.findViewById(R.id.startapplyLay);

        mnotxt  =  rootView.findViewById(R.id.notxt);
        mnotxt.setText("No Bookings");

        mApplicantList = (RecyclerView)rootView.findViewById(R.id.applicantlist);
        mApplicantList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mApplicantList.setLayoutManager(mLayoutManager);


        FirebaseRecyclerAdapter<TalentInfo, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TalentInfo, BlogViewHolder>(
                TalentInfo.class,
                R.layout.incoming_bookingsrow,
                BlogViewHolder.class,
                mUserMyTalentAcceptedBookings.child(mAuth.getCurrentUser().getUid()).child(post_key).orderByChild("time")

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, TalentInfo model, int position) {

                final String otheruserid = getRef(position).getKey();
                final String pressed = model.getpressed();
                final String username = model.getname();
                final String userimage = model.getimage();
                final String dates = model.getdates();
                final String spinnerrate = model.getspinnerrate();
                final String spinnercurrency = model.getspinnercurrency();
                final String basicpay = model.getbasicpay();
                final String location = model.getlocation();

                viewHolder.setDetails(username, spinnerrate, spinnercurrency, basicpay, location, dates);
                viewHolder.setPostImage(mUserInfo, getApplicationContext(), userimage, otheruserid);

                viewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showbookinginfo(otheruserid, post_key);
                    }
                });

                viewHolder.mprofilepic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent otheruserintent = new Intent(context, OtherUser.class);
                        otheruserintent.putExtra("user_uid",otheruserid);
                        startActivity(otheruserintent);
                    }
                });
            }
        };

        mApplicantList.setAdapter(firebaseRecyclerAdapter);

        mUserMyTalentAcceptedBookings.child(mAuth.getCurrentUser().getUid()).child(post_key).addValueEventListener(new ValueEventListener() {
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

    private void showbookinginfo(final String customer_id,final String post_key) {

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
        final CardView mcongratzCardView = (CardView) mdialog.findViewById(R.id.congratzCardView);

        final LinearLayout mreviewLay = (LinearLayout) mdialog.findViewById(R.id.reviewLay);
        mreviewLay.setVisibility(GONE);
        final LinearLayout mhoursLay = (LinearLayout) mdialog.findViewById(R.id.hoursLay);

        final CircleImageView mpostImage = (CircleImageView) mdialog.findViewById(R.id.postImage);

        final TextView mtextView = (TextView) mdialog.findViewById(R.id.textView);
        mtextView.setText("Customer");
        final TextView mpostName = (TextView) mdialog.findViewById(R.id.postName);
        final TextView mdatetxt = (TextView) mdialog.findViewById(R.id.datetxt);
        final TextView mlocationtxt = (TextView) mdialog.findViewById(R.id.locationtxt);
        final TextView mpostNumDates = (TextView) mdialog.findViewById(R.id.postNumDates);
        final TextView mpostBasicPay = (TextView) mdialog.findViewById(R.id.postBasicPay);
        final TextView mpostTotalBasicPay = (TextView) mdialog.findViewById(R.id.postTotalBasicPay);
        final TextView mbasicratetxt = (TextView) mdialog.findViewById(R.id.basicratetxt);
        final TextView mpostNumHours = (TextView) mdialog.findViewById(R.id.postNumHours);
        final TextView mnumtxt = (TextView) mdialog.findViewById(R.id.numtxt);
        final TextView macceptTxt = (TextView) mdialog.findViewById(R.id.acceptTxt);
        macceptTxt.setText("BOOKING ACCEPTED");
        final TextView mrejectTxt = (TextView) mdialog.findViewById(R.id.rejectTxt);
        mrejectTxt.setText("BOOKING REJECTED");

        final TextView mpostTipsPay = (TextView) mdialog.findViewById(R.id.postTipsPay);
        final TextView mpostTotalAllPay = (TextView) mdialog.findViewById(R.id.postTotalAllPay);
        final TextView mpostPaymentDate = (TextView) mdialog.findViewById(R.id.postPaymentDate);
        final TextView mpostAddNote = (TextView) mdialog.findViewById(R.id.postAddNote);

        mcongratzCardView.setVisibility(GONE);
        mhireCardView.setVisibility(GONE);
        macceptedCardView.setVisibility(VISIBLE);

        mUserMyTalentAcceptedBookings.child(mAuth.getCurrentUser().getUid()).child(post_key).child(customer_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("image")) {
                        String imageval = dataSnapshot.child("image").getValue().toString();

                        if (imageval.equals("default")) {
                            Glide.with(context).load(R.drawable.defaultprofile_pic)
                                    .centerCrop()
                                    .error(R.drawable.defaultprofile_pic)
                                    .placeholder(R.drawable.defaultprofile_pic)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .dontAnimate()
                                    .into(mpostImage);
                        }
                        else {
                            Glide.with(context).load(imageval)
                                    .centerCrop()
                                    .error(R.drawable.defaultprofile_pic)
                                    .placeholder(R.drawable.defaultprofile_pic)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .dontAnimate()
                                    .into(mpostImage);
                        }
                    }
                    if (dataSnapshot.hasChild("name")) {
                        mpostName.setText(dataSnapshot.child("name").getValue().toString());
                    }
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

        mreviewcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // showreviewdialog(customer_id, post_key);
            }
        });

        mdialog.show();

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        RelativeLayout mprofileLay;
        CircleImageView mprofilepic;
        CardView mcardview, mviewmoreCardView;
        TextView mpostName, mpostVenue, mpostRates, mpostDate;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mprofilepic =  mView.findViewById(R.id.profilepic);
            mcardview =  mView.findViewById(cardview);
            mprofileLay =  mView.findViewById(R.id.profileLay);

            mpostVenue = mView.findViewById(postVenue);
            mpostName = mView.findViewById(R.id.postName);
            mpostRates = mView.findViewById(postRates);
            mpostDate = mView.findViewById(R.id.postDate);

            mviewmoreCardView = mView.findViewById(R.id.viewmoreCardView);
        }


        public void setDetails(String username, String spinnerrate, String spinnercurrency, String basicpay, String location, String dates){
            if( username!=null && spinnerrate!=null && spinnercurrency!=null && basicpay!=null && dates!= null) {

                mpostName.setText(username);
                mpostVenue.setText(location);
                mpostRates.setText( spinnerrate + " " + basicpay + " " + spinnercurrency );
                mpostDate.setText(dates);

            }
        }


        public void setPostImage(final DatabaseReference mUserInfo, final Context ctx, final String postimage, final String otheruserid){

            if(otheruserid!= null && postimage!=null) {

                mUserInfo.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("UserImage")) {
                            String post_userimage = dataSnapshot.child("UserImage").getValue().toString();
                            if (post_userimage != null){
                                if(post_userimage.equals("default")){
                                    mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                } else {
                                    Glide.with(ctx).load(post_userimage)
                                            .thumbnail(0.5f)
                                            .centerCrop()
                                            .error(R.drawable.defaultprofile_pic)
                                            .placeholder(R.drawable.loading_spinner)
                                            .dontAnimate()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mprofilepic);
                                }
                            }
                        } else {
                            if (postimage.equals("default")) {
                                Glide.with(ctx).load(R.drawable.defaultprofile_pic)
                                        .thumbnail(0.5f)
                                        .centerCrop()
                                        .error(R.drawable.defaultprofile_pic)
                                        .placeholder(R.drawable.loading_spinner)
                                        .dontAnimate()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mprofilepic);
                            } else {
                                Glide.with(ctx).load(postimage)
                                        .thumbnail(0.5f)
                                        .centerCrop()
                                        .error(R.drawable.defaultprofile_pic)
                                        .placeholder(R.drawable.loading_spinner)
                                        .dontAnimate()
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
    }
}
