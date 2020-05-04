package com.zjheng.jobseed.jobseed.ActivitiesScene;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;
import com.zjheng.jobseed.jobseed.ApplicantsScene.Applicant;
import com.zjheng.jobseed.jobseed.ActivitiesScene.EditPostScene.EditPost;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.PostScene.Post;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.HiredApplicantsScene.ClosedHiredApplicants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.actionBtn;
import static com.zjheng.jobseed.jobseed.R.id.closednumApplicantstxt;
import static com.zjheng.jobseed.jobseed.R.id.newapplicantstxt;
import static com.zjheng.jobseed.jobseed.R.id.numApplicants;
import static com.zjheng.jobseed.jobseed.R.id.numApplicantstxt;

/**
 * Created by zhen on 5/5/2017.
 */

public class PostedTab extends Fragment {

    private RecyclerView mPostedList;
    private LinearLayoutManager mLayoutManager;

    private FloatingActionButton maddjobBtn;

    private RelativeLayout mstartpostedLay, mnoInternetLay;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities , mJob, mGeoFire, mUserPosted, mDefaultJobPhotos,
            mUserPostedPendingApplicants, mUserPostedShortlistedApplicants, mUserPostedHiredApplicants;

    private static final String TAG = "PostedTab";

    private String userid, mjobbg1, mjobbg2, mjobbg3 ;

    private CardView mretryBtn;

    private FirebaseRecyclerAdapter<Job, BlogViewHolder> firebaseRecyclerAdapter;

    private Boolean isStarted = false;
    private Boolean isVisible = false;
    private Boolean firsttime = false;

    Activity context;
    View rootView;

    private ProgressDialog myLoadingDialog;

    private AVLoadingIndicatorView mavi;
    private RelativeLayout mloadingLay;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.postedtab, container, false);

        context = getActivity();

        Log.d(TAG, "posted");

        firsttime = true;

        mavi = (AVLoadingIndicatorView)rootView.findViewById(R.id.avi);

        mloadingLay= (RelativeLayout)rootView.findViewById(R.id.loadingLay);


        mstartpostedLay = (RelativeLayout) rootView.findViewById(R.id.startpostedLay);
        mnoInternetLay = (RelativeLayout)rootView.findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)rootView.findViewById(R.id.retryBtn);
        mPostedList = (RecyclerView)rootView.findViewById(R.id.postedlist);
        maddjobBtn = (FloatingActionButton)rootView.findViewById(R.id.addjobBtn);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                //Connected
                mnoInternetLay.setVisibility(GONE);
                if(mstartpostedLay.getVisibility() == View.GONE){
                    mPostedList.setVisibility(VISIBLE);
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                //Connected
                mnoInternetLay.setVisibility(GONE);
                if(mstartpostedLay.getVisibility() == View.GONE){
                    mPostedList.setVisibility(VISIBLE);
                }
            }
        } else {
            //Disconnected
            mnoInternetLay.setVisibility(VISIBLE);

            if(mstartpostedLay.getVisibility() == View.GONE){
                mPostedList.setVisibility(GONE);
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
                            if(mstartpostedLay.getVisibility() == View.GONE){
                                mPostedList.setVisibility(VISIBLE);
                            }
                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            //Connected
                            mnoInternetLay.setVisibility(GONE);
                            if(mstartpostedLay.getVisibility() == View.GONE){
                                mPostedList.setVisibility(VISIBLE);
                            }
                        }
                    } else {
                        //Disconnected
                        mnoInternetLay.setVisibility(VISIBLE);

                        if (mstartpostedLay.getVisibility() == View.GONE) {
                            mPostedList.setVisibility(GONE);
                        }
                    }
                }
            });
        }

        mPostedList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPostedList.setLayoutManager(mLayoutManager);
        mPostedList.setAdapter(new SampleRecycler());

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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

        if (firsttime) {
            loadPostedData();
            firsttime = false;
        }

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

    private void fadeOut(final RelativeLayout view) {
        final AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0F,  0.0F);
        fadeOutAnimation.setDuration(1000);
        fadeOutAnimation.setInterpolator(new AccelerateInterpolator());
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                mavi.smoothToHide();
            }
        });
        view.startAnimation(fadeOutAnimation);
    }

    private void loadPostedData() {

       // myLoadingDialog = new ProgressDialog(context,R.style.MyPostedProgressTheme);
       // myLoadingDialog.setCancelable(false);
       // myLoadingDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
       // myLoadingDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
       // myLoadingDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                fadeOut(mloadingLay);
           }
        }, 1000); // 650 milliseconds delay

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();

        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserPosted =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPosted");

        mUserPostedPendingApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedPendingApplicants");

        mUserPostedShortlistedApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedShortlistedApplicants");

        mUserPostedHiredApplicants =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserPostedHiredApplicants");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation");

        mDefaultJobPhotos =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("DefaultJobPhotos");

        mDefaultJobPhotos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("jobbg1")) {
                    mjobbg1 = dataSnapshot.child("jobbg1").getValue().toString();
                }
                if (dataSnapshot.hasChild("jobbg2")) {
                    mjobbg2 = dataSnapshot.child("jobbg2").getValue().toString();
                }
                if (dataSnapshot.hasChild("jobbg3")) {
                    mjobbg3 = dataSnapshot.child("jobbg3").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        maddjobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Post.class);
                startActivityForResult(intent, 10001);
                context.overridePendingTransition(R.anim.pullup,R.anim.nochange);
            }
        });

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Job, BlogViewHolder>(
                Job.class,
                R.layout.postedrow,
                BlogViewHolder.class,
                mUserPosted.child(userid)

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Job model, final int position) {

                final String postkey = getRef(position).getKey();
                final String city = model.getCity();
                final String closedval = model.getclosed();
                final String postimage = model.getpostImage();
                final String postTitle = model.getTitle();
                final String postDesc = model.getDesc();
                final Long totalapplicantscount = model.getapplicantscount();
                final Long totalhiredcount = model.gettotalhiredcount();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setCompany(model.getCompany());
                viewHolder.setpostImage(context.getApplicationContext(), model.getpostImage());
                // viewHolder.setApplicantStatus( model.getapplicantscount(),  model.getpostImage());
                viewHolder.setJobStatus(model.getclosed(), model.gettotalhiredcount(), model.getapplicantscount(), model.getnewapplicantscount());
                viewHolder.setNewStatus(model.getpressed());

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
                                Intent jobdetailintent = new Intent(context, JobDetail.class);
                                jobdetailintent.putExtra("post_id",postkey);
                                jobdetailintent.putExtra("city_id",city);
                                startActivity(jobdetailintent);
                                mProgressDialog.dismiss();
                            }

                        }.start();

                    }
                });

                viewHolder.mapplicantcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.mnumApplicants.setVisibility(GONE);
                        viewHolder.mnewapplicantstxt.setVisibility(GONE);

                        viewHolder.mnotifiBadge.setVisibility(GONE);
                        viewHolder.mnotifiBadge1.setVisibility(GONE);

                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewPosted").setValue("false");

                        final DatabaseReference newPosted = mUserPosted.child(mAuth.getCurrentUser().getUid()).child(postkey);
                        newPosted.child("pressed").setValue("true");
                        newPosted.child("newapplicantscount").setValue(0);

                        checkDates(postkey);

                        if(closedval!=null){
                            //If job is closed, go to shortlistedapplicant list
                            if(closedval.equals("true")){
                                Intent applicantsintent = new Intent(context, ClosedHiredApplicants.class);
                                applicantsintent.putExtra("post_id",postkey);
                                applicantsintent.putExtra("city",city);
                                applicantsintent.putExtra("post_title",postTitle);
                                applicantsintent.putExtra("post_desc",postDesc);
                                startActivity(applicantsintent);
                            }
                            //if not, go to applicant list
                            else{
                                Intent applicantsintent = new Intent(context, Applicant.class);
                                applicantsintent.putExtra("post_id",postkey);
                                applicantsintent.putExtra("city",city);
                                applicantsintent.putExtra("post_title",postTitle);
                                applicantsintent.putExtra("post_desc",postDesc);
                                startActivity(applicantsintent);
                            }
                        }
                    }
                });

                viewHolder.mactionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.actionbtn_dialog);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.gravity = Gravity.CENTER;

                        dialog.getWindow().setAttributes(lp);

                        Button editBtn = (Button) dialog.findViewById(R.id.editBtn);
                        Button removeBtn = (Button) dialog.findViewById(R.id.removeBtn);
                        Button closeBtn = (Button) dialog.findViewById(R.id.closeBtn);

                        if(closedval!=null) {
                            if (closedval.equals("true")) {
                                editBtn.setVisibility(GONE);
                                closeBtn.setVisibility(GONE);
                            }
                        }

                        dialog.show();

                        editBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent editpostintent = new Intent(context, EditPost.class);
                                editpostintent.putExtra("post_id",postkey);
                                editpostintent.putExtra("city_id",city);
                                startActivity(editpostintent);
                                context.overridePendingTransition(R.anim.pullup,R.anim.nochange);

                                dialog.dismiss();
                            }
                        });

                        closeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final ProgressDialog mProgress = new ProgressDialog(getActivity());
                                mProgress.setMessage("Closing Job..");
                                mProgress.setCancelable(false);
                                mProgress.show();

                                // Close the job at mJob so it wont show at HomePage
                                mJob.child(city).child(postkey).child("closed").setValue("true");
                                //Close the job at Posted Tab, so it will display Job Closed. Remove all new applicnats
                                mUserPosted.child(mAuth.getCurrentUser().getUid()).child(postkey).child("closed").setValue("true");
                                mUserPosted.child(mAuth.getCurrentUser().getUid()).child(postkey).child("applicantscount").setValue(0);
                                mUserPosted.child(mAuth.getCurrentUser().getUid()).child(postkey).child("newapplicantscount").setValue(0);

                                if (totalapplicantscount > 0) {
                                    //Notify all PENDING applicants who has applied to the job about the job has closed
                                    mUserPostedPendingApplicants.child(userid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                                final String userid = userSnaphot.getKey();
                                                mUserActivities.child(userid).child("Applied").child(postkey).child("closed").setValue("true");
                                                mUserActivities.child(userid).child("Applied").child(postkey).child("status").setValue("appliedrejected");
                                            }
                                            mUserPostedPendingApplicants.child(userid).child(postkey).removeValue();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Notify all SHORTLISTED applicants who has applied to the job about the job has closed
                                    mUserPostedShortlistedApplicants.child(userid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                                final String userid = userSnaphot.getKey();
                                                mUserActivities.child(userid).child("Applied").child(postkey).child("closed").setValue("true");
                                                mUserActivities.child(userid).child("Applied").child(postkey).child("status").setValue("appliedrejected");
                                            }
                                            mUserPostedShortlistedApplicants.child(userid).child(postkey).removeValue();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Notify all HIRED applicants who has applied to the job about the job has closed
                                    mUserPostedHiredApplicants.child(userid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                                final String userid = userSnaphot.getKey();
                                                mUserActivities.child(userid).child("Applied").child(postkey).child("closed").setValue("true");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    mProgress.dismiss();
                                    viewHolder.mclosedapplicantsRlay.setVisibility(VISIBLE);
                                    viewHolder.mclosedtext.setVisibility(VISIBLE);
                                    viewHolder.mclosedview.setVisibility(VISIBLE);
                                    viewHolder.mclosednumApplicantstxt.setText(String.valueOf(totalhiredcount) + " total hired applicants");

                                }
                                else {
                                    mProgress.dismiss();
                                    viewHolder.mclosedapplicantsRlay.setVisibility(VISIBLE);
                                    viewHolder.mclosedtext.setVisibility(VISIBLE);
                                    viewHolder.mclosedview.setVisibility(VISIBLE);
                                    viewHolder.mclosednumApplicantstxt.setText(" No hired candidates");
                                }

                                dialog.dismiss();
                            }
                        });

                        removeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final ProgressDialog mProgress = new ProgressDialog(getActivity());
                                mProgress.setMessage("Removing Job..");
                                mProgress.setCancelable(false);
                                mProgress.show();

                                if (city != null) {
                                    mJob.child(city).child(postkey).removeValue();
                                    mGeoFire.child(postkey).removeValue();
                                    mUserPosted.child(mAuth.getCurrentUser().getUid()).child(postkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mProgress.dismiss();
                                            dialog.dismiss();
                                        }
                                    });
                                }


                                //Notify all PENDING applicants who has applied to the job about the job has removed
                                mUserPostedPendingApplicants.child(userid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                            final String userid = userSnaphot.getKey();
                                           // mUserActivities.child(userid).child("Applied").child(postkey).child("closed").setValue("false");
                                            mUserActivities.child(userid).child("Applied").child(postkey).child("status").setValue("removed");

                                        }
                                        mUserPostedPendingApplicants.child(userid).child(postkey).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                //Notify all SHORTLISTED applicants who has applied to the job about the job has removed
                                mUserPostedShortlistedApplicants.child(userid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                            final String userid = userSnaphot.getKey();
                                            //mUserActivities.child(userid).child("Applied").child(postkey).child("closed").setValue("false");
                                            mUserActivities.child(userid).child("Applied").child(postkey).child("status").setValue("removed");
                                        }
                                        mUserPostedShortlistedApplicants.child(userid).child(postkey).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                //Notify all HIRED applicants who has applied to the job about the job has removed
                                mUserPostedHiredApplicants.child(userid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                            final String userid = userSnaphot.getKey();
                                           // mUserActivities.child(userid).child("Applied").child(postkey).child("closed").setValue("false");
                                            mUserActivities.child(userid).child("Applied").child(postkey).child("status").setValue("removed");
                                        }
                                        mUserPostedHiredApplicants.child(userid).child(postkey).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseStorage storage = FirebaseStorage.getInstance();

                                if (mjobbg1 == null || mjobbg2 == null || mjobbg3 == null) {
                                    return;
                                }

                                if(!postimage.equals(mjobbg1) && !postimage.equals(mjobbg2) && !postimage.equals(mjobbg3)){
                                    StorageReference oldpath = storage.getReferenceFromUrl(postimage);
                                    oldpath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
                                            Log.d(TAG, "onSuccess: deleted file");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                            Log.d(TAG, "onFailure: did not delete file");
                                        }
                                    });
                                }

                            }
                        });
                    }
                });

            }
        };

        mPostedList.setAdapter(firebaseRecyclerAdapter);

        //Just display a layout to cover the reclerview when no jobs posted yet
        mUserPosted.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mstartpostedLay.setVisibility(GONE);
                }
                else{
                    mstartpostedLay.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            Log.d(TAG, "posted renew");

            final ProgressDialog mdialog = new ProgressDialog(context,R.style.MyTheme);
            mdialog.setCancelable(false);
            mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            mdialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(PostedTab.this).attach(PostedTab.this).commit();

                    mdialog.dismiss();
                }
            }, 500); //time seconds

        }
    }*/

    private void checkDates(final String postkey) {

        //Check which hired applicant can now be reviewed
        mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnaphot : dataSnapshot.getChildren()) {

                    final String userid = postSnaphot.getKey();

                    if(!postSnaphot.hasChild("reviewed")) {

                        if(postSnaphot.hasChild("date")) {

                            String dateval = postSnaphot.child("date").getValue().toString();

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

                                //If end date > time date NOW, show REVIEW
                                if (c.getTime().compareTo(datenow) < 0) {

                                    if (!postSnaphot.hasChild("reviewpressed")) {
                                        mUserPostedHiredApplicants.child(mAuth.getCurrentUser().getUid()).child(postkey).child(userid).child("reviewpressed").setValue("false");
                                    }

                                }

                            } catch (Exception exception) {
                                Log.e("DIDN'T WORK", "exception " + exception);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView, mclosedview, mnotifiBadge, mnotifiBadge1;
        CircleImageView post_image;
        RelativeLayout mRlayout, mapplicantsRlay, mclosedapplicantsRlay;
        TextView post_desc, mnumApplicantstxt, mnewapplicantstxt, mclosedtext, mclosednumApplicantstxt;
        CardView cardview, mapplicantcardview;
        Button mnumApplicants;
        ImageButton mactionBtn;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mRlayout = (RelativeLayout) mView.findViewById(R.id.Rlayout);
            mclosedapplicantsRlay = (RelativeLayout) mView.findViewById(R.id.closedapplicantsRlay);
            mapplicantsRlay = (RelativeLayout) mView.findViewById(R.id.applicantsRlay);

            post_image = (CircleImageView) mView.findViewById(R.id.postImage);

            cardview = (CardView) mView.findViewById(R.id.cardview);
            mapplicantcardview = (CardView) mView.findViewById(R.id.applicantcardview);

            mnumApplicantstxt = (TextView)mView.findViewById(numApplicantstxt);
            mnewapplicantstxt = (TextView)mView.findViewById(newapplicantstxt);
            mclosednumApplicantstxt = (TextView)mView.findViewById(closednumApplicantstxt);

            mnumApplicants = (Button)mView.findViewById(numApplicants);
            mapplicantsRlay.setVisibility(View.VISIBLE);

            mactionBtn = (ImageButton)mView.findViewById(actionBtn);
            mactionBtn.setVisibility(View.VISIBLE);

            mclosedtext = (TextView) mView.findViewById(R.id.closedtext);
            mclosedview = (View)mView.findViewById(R.id.closedview);

            mnotifiBadge = (View)mView.findViewById(R.id.notifiBadge);
            mnotifiBadge1 = (View)mView.findViewById(R.id.notifiBadge1);

            mclosedapplicantsRlay.setVisibility(GONE);
            mclosedtext.setVisibility(GONE);
            mclosedview.setVisibility(GONE);

        }

       /* public void setApplicantStatus (final String postkey){
            if( postkey!=null){

                mclosedapplicantsRlay.setVisibility(GONE);
                mclosedtext.setVisibility(GONE);
                mclosedview.setVisibility(GONE);

                mUserActivities.child(mAuth.getCurrentUser().getUid()).child("Posted").child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("applicants")) {
                            long totalapplicants = dataSnapshot.child("applicants").getChildrenCount();
                            String totalapplicantscount = String.valueOf(totalapplicants);
                            mnumApplicantstxt.setText(totalapplicantscount + " total applicants");

                            mnumApplicants.setVisibility(View.GONE);
                            mnewapplicantstxt.setVisibility(View.GONE);

                            if (dataSnapshot.hasChild("newapplicants")) {
                                mnumApplicants.setVisibility(View.VISIBLE);
                                mnewapplicantstxt.setVisibility(View.VISIBLE);

                                long newapplicants = dataSnapshot.child("newapplicants").getChildrenCount();
                                String totalnewapplicants = String.valueOf(newapplicants);

                                mnumApplicants.setText(totalnewapplicants);
                                mnewapplicantstxt.setText(" new applicants");
                                mnumApplicantstxt.setText(totalapplicantscount + " total applicants");
                            }
                        }
                        else{
                            mnumApplicants.setVisibility(View.GONE);
                            mnewapplicantstxt.setVisibility(View.GONE);

                            mnumApplicantstxt.setText(" No applicants yet");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }*/

        //Display red dot if job is shortlisted, rejected
        public void setNewStatus(final String pressed){
            if(pressed!=null){
                if (pressed.equals("false")) {
                    mnotifiBadge.setVisibility(VISIBLE);
                    mnotifiBadge1.setVisibility(VISIBLE);
                }
                else{
                    mnotifiBadge.setVisibility(GONE);
                    mnotifiBadge1.setVisibility(GONE);
                }
            }
        }

        public void setJobStatus(final String closed, final Long totalhiredcount, final Long applicantscount, final Long newapplicantscount){
            if(closed!=null && totalhiredcount!=null && applicantscount!=null && newapplicantscount!=null){

                mclosedapplicantsRlay.setVisibility(GONE);
                mclosedtext.setVisibility(GONE);
                mclosedview.setVisibility(GONE);

                if(closed.equals("true")) {
                    Log.d(TAG, "closed" + closed);
                    mclosedapplicantsRlay.setVisibility(VISIBLE);
                    mclosedtext.setVisibility(VISIBLE);
                    mclosedview.setVisibility(VISIBLE);

                    mclosednumApplicantstxt.setText(String.valueOf(totalhiredcount) + " total hired applicants");

                }
                else {

                    Log.d(TAG, "applicantscount" + applicantscount);

                    if (applicantscount > 0) {

                        mnumApplicantstxt.setText(String.valueOf(applicantscount) + " total applicants");

                        if (newapplicantscount == 0) {
                            mnumApplicants.setVisibility(View.GONE);
                            mnewapplicantstxt.setVisibility(View.GONE);
                        }
                        else if (newapplicantscount > 0) {
                            mnumApplicants.setVisibility(View.VISIBLE);
                            mnewapplicantstxt.setVisibility(View.VISIBLE);
                            mnumApplicants.setText(String.valueOf(newapplicantscount));
                            mnewapplicantstxt.setText(" new applicants");
                        }
                    }
                    else {
                        mnumApplicants.setVisibility(View.GONE);
                        mnewapplicantstxt.setVisibility(View.GONE);
                        mnumApplicantstxt.setText(" No applicants yet");
                    }
                }
            }
        }

        public void setTitle(String title){
            if(title!=null) {
                TextView post_title = (TextView) mView.findViewById(R.id.postName);
                post_title.setText(title);
                Log.d(TAG, "posted title" + title);
            }
        }
        public void setDesc(String desc){
            if(desc!=null) {
                post_desc = (TextView) mView.findViewById(R.id.postDescrip);
                post_desc.setText(desc);
            }
        }
        public void setCompany(String company){
            if(company!=null) {
                TextView post_company = (TextView) mView.findViewById(R.id.postCompany);
                post_company.setText(company);
            }
        }
        public void setpostImage(Context ctx, String postimage){
            if (postimage != null) {

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
    }
}
