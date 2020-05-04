package com.zjheng.jobseed.jobseed.UnUsedFiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.UserReview;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUser;
import com.zjheng.jobseed.jobseed.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhen on 5/5/2017.
 */

public class UserRatingTab extends Fragment {

    private RecyclerView mreviewlist;
    private LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<UserReview, BlogViewHolder> firebaseRecyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReview, mUserAccount;
    private Query mQuery;

    private CardView mreviewcardview;
    private LinearLayout mreviewLay;

    private static final String TAG = "UserRating_Rateable";

    private String userid , ReviewCount, reducedReviewCount;
    private ProgressDialog mProgress;

    private RelativeLayout mnoreviewLay;

    private long reviewcount5 = 0, reviewcount4 = 0, reviewcount3 = 0, reviewcount2 = 0 , reviewcount1 = 0, totalreviewcount = 0;
    private int reviewlimit = 20, removeCount = 0, ratestar = 0, rateval, reviewcounter;
    private Long reviewcount;
    private int rateablecount;

    Activity context;
    View rootView;

    public static UserRatingTab newInstance(String userid, int rateablecount) {
        UserRatingTab result = new UserRatingTab();
        Bundle bundle = new Bundle();
        bundle.putString("useruid", userid);
        bundle.putInt("rateablecount", rateablecount);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        userid = bundle.getString("useruid");
        rateablecount = bundle.getInt("rateablecount");

        Log.d(TAG, "USER RATING RATEABLECOUNT: " + rateablecount);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_review, container, false);

        context = getActivity();

        Log.d(TAG, "UserRating_Rateable");

        mreviewlist = (RecyclerView)rootView.findViewById(R.id.reviewlist);
        mreviewcardview = (CardView)rootView.findViewById(R.id.reviewcardview);
        mreviewLay = (LinearLayout)rootView.findViewById(R.id.reviewLay);
        mnoreviewLay = (RelativeLayout) rootView.findViewById(R.id.noreviewLay);

        mAuth = FirebaseAuth.getInstance();

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        if (rateablecount == 1){
            mreviewLay.setVisibility(GONE);
        }
        else if (rateablecount == 2){
            mreviewLay.setVisibility(VISIBLE);
        }
        else{
            mreviewLay.setVisibility(GONE);
        }

        mreviewlist.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mreviewlist.setLayoutManager(mLayoutManager);

        setupview();

        mreviewlist.setAdapter(firebaseRecyclerAdapter);

        Log.d(TAG, "useruid "+ userid);

        mreviewcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showreviewdialog();
            }
        });

        mUserReview.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Review")){

                    mnoreviewLay.setVisibility(GONE);

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
                else{
                    mnoreviewLay.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void updateReview(){

        reviewcounter = 0;

        mUserReview.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Review")){
                    mnoreviewLay.setVisibility(GONE);

                    if(dataSnapshot.child("Review").getChildrenCount()>reviewlimit){
                        Log.d(TAG, "getChildrenCount " + dataSnapshot.child("Review").getChildrenCount());
                        mUserReview.child(userid).child("Review").orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot usersdataSnapshot : dataSnapshot.getChildren()) {
                                    reviewcounter++;
                                    if(reviewcounter<reviewlimit){
                                        Log.d(TAG, "getChildrenKey " + usersdataSnapshot.getKey());
                                        Log.d(TAG, "getChildrenRef " + usersdataSnapshot.getRef());
                                        usersdataSnapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

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
                    if (totalreviewcount !=0) {
                        long starcount = ((5*reviewcount5)+(4*reviewcount4)+(3*reviewcount3)+(2*reviewcount2)+(1*reviewcount1))/(totalreviewcount);
                        rateval = Math.round(starcount);
                    }
                    ShortListedApplicantUserProfile.mRatingBar.setRating(rateval);
                    ShortListedApplicantUserProfile.mratingtxt.setText(totalreviewcount+" Reviews");
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupview(){
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserReview, UserRatingTab.BlogViewHolder>(
                UserReview.class,
                R.layout.review_row,
                UserRatingTab.BlogViewHolder.class,
                mUserReview.child(userid).child("Review").orderByChild("time").limitToFirst(reviewlimit)
        ) {

            @Override
            protected void populateViewHolder(UserRatingTab.BlogViewHolder viewHolder, UserReview model, final int position) {

                final String userid = getRef(position).getKey();

                viewHolder.setusername(model.getusername());
                viewHolder.setreviewmessage(model.getreviewmessage());
                viewHolder.settime(model.gettime());
                viewHolder.setuserimage(context, model.getuserimage());
                viewHolder.setratingbar(model.getrating());

                viewHolder.userimagepic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent otheruserintent = new Intent(context, OtherUser.class);
                        otheruserintent.putExtra("user_uid",userid);
                        startActivity(otheruserintent);
                    }
                });
            }
        };
    }

    private void showreviewdialog(){

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
        final EditText mreviewtxt = (EditText) dialog.findViewById(R.id.reviewtxt);
        final Button postbtn = (Button) dialog.findViewById(R.id.postBtn);
        final Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);

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

                    mUserReview.child(userid).child("Review").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                Long rating = (Long) dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("rating").getValue();
                                if (rating == 5) { reducedReviewCount = "Rate5"; }
                                else if (rating == 4) { reducedReviewCount = "Rate4"; }
                                else if (rating == 3) { reducedReviewCount = "Rate3"; }
                                else if (rating == 2) { reducedReviewCount = "Rate2"; }
                                else if (rating == 1) { reducedReviewCount = "Rate1"; }
                                if (rating != ratestar) {
                                    deleteandincrement(dialog,reviewval,reducedReviewCount,ReviewCount);
                                }
                            }
                            else{
                                incrementonly(dialog,reviewval,ReviewCount);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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

    public void incrementonly(final Dialog dialog,final String reviewval, String ReviewCount) {
        final DatabaseReference newReview = mUserReview.child(userid).child("Review");
        Log.d(TAG, "ReviewCount " + ReviewCount);
        mUserReview.child(userid).child(ReviewCount).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                    reviewcount = Long.valueOf(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                    reviewcount = (Long) currentData.getValue() + 1;
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                mUserReview.child(userid).child("Notification").setValue("true");

                mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map< String, Object > checkoutData = new HashMap<>();
                        checkoutData.put("time", ServerValue.TIMESTAMP);
                        newReview.child(mAuth.getCurrentUser().getUid()).setValue(checkoutData);

                        newReview.child(mAuth.getCurrentUser().getUid()).child("userimage").setValue(dataSnapshot.child("image").getValue());

                        if(TextUtils.isEmpty(reviewval)){
                            newReview.child(mAuth.getCurrentUser().getUid()).child("reviewmessage").setValue("none");
                        }
                        else{
                            newReview.child(mAuth.getCurrentUser().getUid()).child("reviewmessage").setValue(reviewval);
                        }

                        newReview.child(mAuth.getCurrentUser().getUid()).child("username").setValue(dataSnapshot.child("name").getValue());
                        newReview.child(mAuth.getCurrentUser().getUid()).child("rating").setValue(ratestar).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateReview();
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

    public void deleteandincrement(final Dialog dialog, final String reviewval, String reducedReviewCount, final String ReviewCount) {
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
                incrementonly(dialog,reviewval,ReviewCount);
            }
        });
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        CircleImageView userimagepic;
        TextView txtTime, txtuserComment, postName;
        RatingBar muserratingbar;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            userimagepic = (CircleImageView) mView.findViewById(R.id.userimage);
            txtTime = (TextView) mView.findViewById(R.id.txtTime);
            postName = (TextView) mView.findViewById(R.id.postName);
            txtuserComment = (TextView) mView.findViewById(R.id.txtuserComment);
            muserratingbar = (RatingBar) mView.findViewById(R.id.userratingbar);

        }

        public void setusername(String username){
            if(username!=null){
                postName.setText(username);
            }
        }
        public void setreviewmessage(String reviewmessage){
            if(reviewmessage!=null){
                if(reviewmessage.equals("none")){
                    txtuserComment.setVisibility(GONE);
                    txtuserComment.setText("");
                }
                else{
                    txtuserComment.setVisibility(VISIBLE);
                    txtuserComment.setText(reviewmessage);
                }
            }
        }
        public void setratingbar(int ratingval){
            muserratingbar.setRating(ratingval);
        }
        public void settime(Long time){
            Long tsLong = System.currentTimeMillis();
            CharSequence result = DateUtils.getRelativeTimeSpanString(time, tsLong, DateUtils.SECOND_IN_MILLIS);
            txtTime.setText(result);
        }
        public void setuserimage(Context ctx, String userimage){
            if (userimage != null) {

                if (!userimage.equals("default")) {
                    Glide.with(ctx).load(userimage)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .error(R.drawable.defaultprofile_pic)
                            .placeholder(R.drawable.loading_spinner)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(userimagepic);
                }
            }
        }
    }
}

