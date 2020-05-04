package com.zjheng.jobseed.jobseed.ApplicantsScene;

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
import com.zjheng.jobseed.jobseed.CustomObjectClass.ApplicantsInfo;
import com.zjheng.jobseed.jobseed.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.zjheng.jobseed.jobseed.R.id.cardview;
import static com.zjheng.jobseed.jobseed.R.id.spinnerrate;

public class HiredApplicants extends Fragment {

    private RecyclerView mApplicantList;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mstartapplyLay;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities , mJob, mUserChatList, mChatRoom, mUserReview,
            mApplyNotification, mUserPostedHiredApplicants, mUserInfo, mUserLocation, mUserAccount;

    private static final String TAG = "Applicants";

    private String userid, post_key, city, post_title, post_desc;

    private String ownuserid, ReviewCount, reducedReviewCount;
    private ProgressDialog mProgress;

    private int ratestar = 0;
    private Boolean editpost = false;

    Activity context;
    View rootView;

    public static HiredApplicants newInstance(String post_id, String city, String post_title, String post_desc) {
        HiredApplicants result = new HiredApplicants();
        Bundle bundle = new Bundle();
        bundle.putString("post_id", post_id);
        bundle.putString("city", city);
        bundle.putString("post_title", post_title);
        bundle.putString("post_desc", post_desc);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        post_key = bundle.getString("post_id");
        city = bundle.getString("city");
        post_title = bundle.getString("post_title");
        post_desc = bundle.getString("post_desc");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_applicants_list, container, false);

        context = getActivity();

        mAuth = FirebaseAuth.getInstance();

        userid = mAuth.getCurrentUser().getUid();

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mApplyNotification =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ApplyNotification");

        mUserPostedHiredApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedHiredApplicants");

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mstartapplyLay = (RelativeLayout) rootView.findViewById(R.id.startapplyLay);

        mApplicantList = (RecyclerView)rootView.findViewById(R.id.applicantlist);
        mApplicantList.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);

        mApplicantList.setLayoutManager(mLayoutManager);

        mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "hired dataSnapshot.getChildrenCount " + dataSnapshot.getChildrenCount());
                for (DataSnapshot usersdataSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "hired single dataSnapshot.key " + usersdataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<ApplicantsInfo, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ApplicantsInfo, BlogViewHolder>(
                ApplicantsInfo.class,
                R.layout.applicantsrow,
                BlogViewHolder.class,
                mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).orderByChild("negatedtime")

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, ApplicantsInfo model, int position) {

                final String otheruserid = getRef(position).getKey();
                final String reviewpressedval = model.getreviewpressed();
                final String reviewedval = model.getreviewed();
                final String post_date = model.getdate();
                final String offerstatusval = model.getofferstatus();

                viewHolder.setNameandLocation(mUserInfo, mUserLocation, model.getName(), otheruserid);
                viewHolder.setWorkTitleandCompany(mUserInfo, otheruserid);
                viewHolder.setpostImage(mUserInfo, getApplicationContext(), model.getImage(), otheruserid);
                viewHolder.setNewStatus(model.getpressed(), reviewpressedval);
                viewHolder.checkEndDate(post_date, reviewedval, offerstatusval, otheruserid, mUserPostedHiredApplicants, userid, post_key );


                viewHolder.mcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent otheruserintent = new Intent(context, ApplicantUserProfile.class);
                        otheruserintent.putExtra("user_uid",otheruserid);
                        otheruserintent.putExtra("post_key",post_key);
                        otheruserintent.putExtra("city",city);
                        otheruserintent.putExtra("post_title",post_title);
                        otheruserintent.putExtra("post_desc",post_desc);
                        otheruserintent.putExtra("applicant_status","3");
                        startActivity(otheruserintent);

                    }
                });

                viewHolder.mviewmoreCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewApplicant").setValue("false");
                        viewHolder.mnotifiBadge.setVisibility(GONE);
                        mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(otheruserid).child("pressed").setValue("true");
                        showhiredform(otheruserid, post_date, post_key);
                    }
                });
            }
        };

        mApplicantList.setAdapter(firebaseRecyclerAdapter);

        mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).addValueEventListener(new ValueEventListener() {
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

    private void showhiredform(final String hiredapplicant_uid, final String dateval, final String post_key) {

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

        final TextView mpostTipsPay = (TextView) dialog.findViewById(R.id.postTipsPay);
        final TextView mpostTotalAllPay = (TextView) dialog.findViewById(R.id.postTotalAllPay);
        final TextView mpostPaymentDate = (TextView) dialog.findViewById(R.id.postPaymentDate);
        final TextView mpostAddNote = (TextView) dialog.findViewById(R.id.postAddNote);

        mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(hiredapplicant_uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    if (dataSnapshot.hasChild("offerstatus")) {

                        String offerstatus_val = dataSnapshot.child("offerstatus").getValue().toString();

                        if (offerstatus_val.equals("accepted")) {
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
                                        mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(hiredapplicant_uid).child("reviewpressed").setValue("true");
                                    }

                                } catch (Exception exception) {
                                    Log.e("DIDN'T WORK", "exception " + exception);
                                }
                            }
                        }
                        else if (offerstatus_val.equals("rejected")) {
                            mrejectedCardView.setVisibility(VISIBLE);
                            mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(hiredapplicant_uid).child("reviewpressed").setValue("true");
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
                Intent hireintent = new Intent(context, HireForm.class);
                hireintent.putExtra("user_uid",hiredapplicant_uid);
                hireintent.putExtra("post_key",post_key);
                hireintent.putExtra("city",city);
                hireintent.putExtra("update","true");
                startActivityForResult(hireintent, 100);
                dialog.dismiss();
            }
        });

        mreviewcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showreviewdialog(hiredapplicant_uid);
            }
        });

    }

    private void showreviewdialog(final String userid){

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
                        deleteandincrement(userid, dialog,reviewval,reducedReviewCount,ReviewCount);
                    }
                    else {
                        incrementonly(userid, dialog,reviewval,ReviewCount);
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

    public void incrementonly(final String userid,final Dialog dialog,final String reviewval, String ReviewCount) {
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

                mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(post_key).child(userid).child("reviewed").setValue("true");

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

    public void deleteandincrement(final String userid,final Dialog dialog, final String reviewval, String reducedReviewCount, final String ReviewCount) {
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
                incrementonly(userid,dialog,reviewval,ReviewCount);
            }
        });
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView, mnotifiBadge;
        RelativeLayout mprofileLay;
        CircleImageView mprofilepic;
        CardView mcardview, mviewmoreCardView;
        TextView mpostName, mpostLocation, mworktitle1, mworkcompany1, mworktitle2, mworkcompany2, mworktitle3, mworkcompany3, mviewmoretxt, mreviewtxt ;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mprofilepic = (CircleImageView) mView.findViewById(R.id.profilepic);
            mcardview = (CardView) mView.findViewById(cardview);
            mprofileLay = (RelativeLayout) mView.findViewById(R.id.profileLay);

            mpostName = (TextView)mView.findViewById(R.id.postName);
            mpostLocation = (TextView)mView.findViewById(R.id.postLocation);
            mworktitle1 = (TextView)mView.findViewById(R.id.worktitle1);
            mworkcompany1 = (TextView)mView.findViewById(R.id.workcompany1);
            mworktitle2 = (TextView)mView.findViewById(R.id.worktitle2);
            mworkcompany2 = (TextView)mView.findViewById(R.id.workcompany2);
            mviewmoretxt = (TextView)mView.findViewById(R.id.viewmoretxt);
            mreviewtxt = (TextView)mView.findViewById(R.id.reviewtxt);
            mviewmoreCardView = (CardView)mView.findViewById(R.id.viewmoreCardView);
            mviewmoreCardView.setVisibility(VISIBLE);
            mnotifiBadge = (View)mView.findViewById(R.id.notifiBadge);
            mnotifiBadge.setVisibility(GONE);
        }



        public void checkEndDate(String post_date, String reviewedval, String offerstatusval, String otheruserid, DatabaseReference mUserPostedHiredApplicants, String userid, String post_key) {

            if (post_date!=null) {

                String[] separated;
                String lastdate;
                Date enddate;

                if (!post_date.contains("to")) {

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

                        //Check if applicant is still pending, if YES, delete the applicant

                        Log.d(TAG, "offerstatusval = " + offerstatusval);

                        if (offerstatusval.equals("pending")) {
                            mUserPostedHiredApplicants.child(userid).child(post_key).child(otheruserid).child("offerstatus").setValue("rejected");
                        }
                        else if (offerstatusval.equals("accepted")) {
                            if (reviewedval != null) {
                                if (reviewedval.equals("false")) {
                                    mreviewtxt.setVisibility(VISIBLE);
                                }
                                else {
                                    mreviewtxt.setVisibility(GONE);
                                }
                            }
                            else {
                                mreviewtxt.setVisibility(VISIBLE);
                            }
                        }
                        
                    }
                    else {
                        mreviewtxt.setVisibility(GONE);
                    }


                } catch (Exception exception) {
                    Log.e("DIDN'T WORK", "exception " + exception);
                }
            }
        }


        public void setNewStatus(String pressed, String reviewpressedval) {

            if (pressed!=null) {

                if(pressed.equals("true")){

                    mnotifiBadge.setVisibility(GONE);
                    mviewmoretxt.setTextColor(Color.parseColor("#1a8dfb"));
                    mviewmoretxt.setBackgroundResource(R.drawable.viewmore_background);
                }
                else if(pressed.equals("false")) {
                    mnotifiBadge.setVisibility(VISIBLE);
                    mviewmoretxt.setTextColor(Color.parseColor("#FFFFFF"));
                    mviewmoretxt.setBackgroundResource(R.drawable.viewmore_background_unpressed);
                }
            }
            else {
                mnotifiBadge.setVisibility(GONE);
            }

            if (reviewpressedval!=null) {
                if (reviewpressedval.equals("false")) {
                    mnotifiBadge.setVisibility(VISIBLE);
                    mviewmoretxt.setTextColor(Color.parseColor("#FFFFFF"));
                    mviewmoretxt.setBackgroundResource(R.drawable.viewmore_background_unpressed);
                }
            }

        }


        public void setNameandLocation(final DatabaseReference mUserInfo, final DatabaseReference mUserLocation,final String name, final String otheruserid){
            if(name!=null && otheruserid!= null) {
                mUserInfo.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("Name")) {
                            String user_name = dataSnapshot.child("Name").getValue().toString();
                            mpostName.setText(user_name);
                        }
                        else{
                            mpostName.setText(name);
                        }

                        if (dataSnapshot.hasChild("Address")) {
                            String Address = dataSnapshot.child("Address").getValue().toString();
                            mpostLocation.setText(Address);
                            mpostLocation.setVisibility(VISIBLE);
                        } else {
                            mUserLocation.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("CurrentCity")) {
                                        String Address = dataSnapshot.child("CurrentCity").getValue().toString();
                                        mpostLocation.setText(Address);
                                        mpostLocation.setVisibility(VISIBLE);
                                    } else {
                                        mpostLocation.setText("No Location");
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
            }
        }
        public void setWorkTitleandCompany(final DatabaseReference mUserInfo,final String otheruserid){
            if(otheruserid!= null) {

                mUserInfo.child(otheruserid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("WorkExp1")) {

                            String worktitle = dataSnapshot.child("WorkExp1").child("worktitle").getValue().toString();
                            mworktitle1.setText(worktitle);

                            String workcompany = dataSnapshot.child("WorkExp1").child("workcompany").getValue().toString();
                            mworkcompany1.setText(" at "+workcompany);
                        }
                        else{
                            mworktitle1.setText("No Work Experiences");
                            mworkcompany1.setVisibility(View.INVISIBLE);
                        }

                        if(dataSnapshot.hasChild("WorkExp2")) {

                            String worktitle = dataSnapshot.child("WorkExp2").child("worktitle").getValue().toString();
                            mworktitle2.setVisibility(VISIBLE);
                            mworktitle2.setText(worktitle);

                            String workcompany = dataSnapshot.child("WorkExp2").child("workcompany").getValue().toString();
                            mworkcompany2.setVisibility(VISIBLE);
                            mworkcompany2.setText(" at "+workcompany);
                        }
                        else{
                            mworktitle2.setVisibility(GONE);
                            mworkcompany2.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }


        public void setpostImage(final DatabaseReference mUserInfo, final Context ctx, final String postimage, final String otheruserid){

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
