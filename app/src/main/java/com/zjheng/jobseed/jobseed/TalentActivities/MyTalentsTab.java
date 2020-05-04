package com.zjheng.jobseed.jobseed.TalentActivities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Rating;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;
import com.zjheng.jobseed.jobseed.ActivitiesScene.EditPostScene.EditPost;
import com.zjheng.jobseed.jobseed.ApplicantsScene.Applicant;
import com.zjheng.jobseed.jobseed.BookingScene.Bookings;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.HiredApplicantsScene.ClosedHiredApplicants;
import com.zjheng.jobseed.jobseed.JobDetail;
import com.zjheng.jobseed.jobseed.LoginScene.EmailLogin.ResetPassword;
import com.zjheng.jobseed.jobseed.MainActivity;
import com.zjheng.jobseed.jobseed.PostScene.Post;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.ShowcaseTalentScene.ShowcaseTalent;
import com.zjheng.jobseed.jobseed.TalentDetails.TalentDetail;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.category;
import static android.R.attr.path;
import static android.R.attr.x;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.zjheng.jobseed.jobseed.R.drawable.ic_star_white_24dp;
import static com.zjheng.jobseed.jobseed.R.id.actionBtn;
import static com.zjheng.jobseed.jobseed.R.id.closednumApplicantstxt;
import static com.zjheng.jobseed.jobseed.R.id.newapplicantstxt;
import static com.zjheng.jobseed.jobseed.R.id.newbookingtxt;
import static com.zjheng.jobseed.jobseed.R.id.newreviewtxt;
import static com.zjheng.jobseed.jobseed.R.id.numApplicants;
import static com.zjheng.jobseed.jobseed.R.id.numApplicantstxt;
import static com.zjheng.jobseed.jobseed.R.id.postDescrip;
import static com.zjheng.jobseed.jobseed.R.id.postTitle;
import static com.zjheng.jobseed.jobseed.R.id.reviewcount;
import static com.zjheng.jobseed.jobseed.R.id.reviewstar;
import static com.zjheng.jobseed.jobseed.R.id.totalbookingtxt;

/**
 * Created by zhen on 5/5/2017.
 */

public class MyTalentsTab extends Fragment {

    private RecyclerView mMyTalentList;
    private LinearLayoutManager mLayoutManager;

    private FloatingActionButton maddTalentBtn;

    private RelativeLayout mstartpostedLay, mnoInternetLay;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserMyTalent , mTalent, mUserActivities, mUserBookingMade, mUserMyTalentAcceptedBookings, mUserMyTalentPendingBookings, mTalentVerify;

    private static final String TAG = "MyTalentsTab";

    private String ownuserid;

    private CardView mretryBtn;

    private FirebaseRecyclerAdapter<TalentInfo, BlogViewHolder> firebaseRecyclerAdapter;

    private FirebaseApp fbApp;
    private FirebaseStorage fbStorage;
    private StorageReference storageRef;
    private ArrayList<String> postimagelist;
    private int imagecount;

    private TextView mstarttxt, mstarttxt2;
    private ImageView mstartImg;

    Activity context;
    View rootView;

    private AVLoadingIndicatorView mavi;
    private RelativeLayout mloadingLay;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.postedtab, container, false);

        context = getActivity();

        Log.d(TAG, "My Talents");

        mavi = (AVLoadingIndicatorView)rootView.findViewById(R.id.avi);
        mloadingLay= (RelativeLayout)rootView.findViewById(R.id.loadingLay);
        mloadingLay.setVisibility(GONE);
        mavi.hide();

        mAuth = FirebaseAuth.getInstance();
        ownuserid = mAuth.getCurrentUser().getUid();

        mUserMyTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("MyTalent");
        
        mUserActivities =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        mUserBookingMade = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserBookingMade");

        mUserMyTalentAcceptedBookings = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserMyTalentAcceptedBookings");

        mUserMyTalentPendingBookings = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserMyTalentPendingBookings");

        mTalentVerify =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("TalentVerify");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAX3s8Y0CA2RwBF5vxhX9tVqV5Gg1S2UHE")
                .setApplicationId("1:1004980108230:android:4ecc908d28953c07")
                //.setStorageBucket("gs://my-gg-app.appspot.com")
                .setDatabaseUrl("https://my-gg-app.firebaseio.com")
                .build();

        boolean hasBeenInitialized = false;
        List<FirebaseApp> fbsLcl = FirebaseApp.getApps(context);
        for (FirebaseApp app : fbsLcl) {
            if (app.getName().equals("LanceApp")) {
                hasBeenInitialized = true;
                fbApp = app;
            }
        }

        if (!hasBeenInitialized) {
            fbApp = FirebaseApp.initializeApp(getApplicationContext(), options, "LanceApp"/*""*/);
        }

        fbStorage = FirebaseStorage.getInstance(fbApp);

        postimagelist = new ArrayList<String>();

        mstartpostedLay = (RelativeLayout) rootView.findViewById(R.id.startpostedLay);
        mnoInternetLay = (RelativeLayout)rootView.findViewById(R.id.noInternetLay);
        mretryBtn = (CardView)rootView.findViewById(R.id.retryBtn);
        mMyTalentList = (RecyclerView)rootView.findViewById(R.id.postedlist);
        maddTalentBtn = (FloatingActionButton)rootView.findViewById(R.id.addjobBtn);
        maddTalentBtn.setImageResource(R.drawable.ic_star_white_24dp);

        mstarttxt = rootView.findViewById(R.id.starttxt);
        mstarttxt2 = rootView.findViewById(R.id.starttxt2);
        mstartImg = rootView.findViewById(R.id.startImg);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                //Connected
                mnoInternetLay.setVisibility(GONE);
                if(mstartpostedLay.getVisibility() == View.GONE){
                    mMyTalentList.setVisibility(VISIBLE);
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                //Connected
                mnoInternetLay.setVisibility(GONE);
                if(mstartpostedLay.getVisibility() == View.GONE){
                    mMyTalentList.setVisibility(VISIBLE);
                }
            }
        } else {
            //Disconnected
            mnoInternetLay.setVisibility(VISIBLE);

            if(mstartpostedLay.getVisibility() == View.GONE){
                mMyTalentList.setVisibility(GONE);
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
                                mMyTalentList.setVisibility(VISIBLE);
                            }
                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "connected to data");
                            //Connected
                            mnoInternetLay.setVisibility(GONE);
                            if(mstartpostedLay.getVisibility() == View.GONE){
                                mMyTalentList.setVisibility(VISIBLE);
                            }
                        }
                    } else {
                        //Disconnected
                        mnoInternetLay.setVisibility(VISIBLE);

                        if (mstartpostedLay.getVisibility() == View.GONE) {
                            mMyTalentList.setVisibility(GONE);
                        }
                    }
                }
            });
        }

        mMyTalentList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mMyTalentList.setLayoutManager(mLayoutManager);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TalentInfo, BlogViewHolder>(
                TalentInfo.class,
                R.layout.mytalentrow,
                BlogViewHolder.class,
                mUserMyTalent.child(ownuserid)

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, TalentInfo model, final int position) {

                final String postkey = getRef(position).getKey();
                final String city = model.getcity();
                final String posttitle = model.gettitle();
                final String postdescrip = model.getdesc();
                final String pressed = model.getpressed();
                final String newreview = model.getnewreview();
                final String verified = model.getverified();
                final Long newbookingcount = model.getnewbookingcount();
                final Long totalbookingcount = model.gettotalbookingcount();
                final Long reviewcount = model.getreviewcount();
                final Long reviewstar = model.getreviewstar();

                final String postimage0;

                final String postimage = model.getpostimage();
                if (postimage.contains(" , ")) {
                    String postimages[] = postimage.split(" , ");
                    postimage0 = postimages[0];
                } else {
                    postimage0 = postimage;
                }

                Log.d(TAG, "postimage0 " + postimage0);

                String category = model.getcategory();
                String categorys[] = category.split(" / ");
                final String maincategory = categorys[0];
                final String subcategory = categorys[1];

                viewHolder.setVerified(verified);
                viewHolder.setRating(reviewcount,reviewstar);
                viewHolder.setTitle_Descrip(posttitle, postdescrip);
                viewHolder.setPostImage(context.getApplicationContext(), postimage0);
                viewHolder.setTalentStatus(totalbookingcount, newbookingcount);
                viewHolder.setNewStatus(pressed);
                viewHolder.setNewReview(newreview);

                viewHolder.mpendingCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPendingVerification();
                    }
                });

                viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserMyTalent.child(ownuserid).child(postkey).child("newreview").removeValue();

                        final ProgressDialog mProgressDialog;
                        mProgressDialog = new ProgressDialog(context,R.style.MyTheme);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        mProgressDialog.show();

                        new Thread()
                        {

                            public void run()
                            {
                                Intent detailintent = new Intent(context, TalentDetail.class);
                                detailintent.putExtra("post_id", postkey);
                                detailintent.putExtra("city_id", city);
                                detailintent.putExtra("maincategory", maincategory);
                                detailintent.putExtra("subcategory", subcategory);
                                context.startActivity(detailintent);
                                mProgressDialog.dismiss();
                            }

                        }.start();

                    }
                });

                viewHolder.mbookingCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.mnewbookingBtn.setVisibility(GONE);
                        viewHolder.mnewbookingtxt.setVisibility(GONE);

                        viewHolder.mnotifiBadge.setVisibility(GONE);

                        mUserActivities.child(mAuth.getCurrentUser().getUid()).child("NewMyTalents").setValue("false");

                        final DatabaseReference newPosted = mUserMyTalent.child(mAuth.getCurrentUser().getUid()).child(postkey);
                        newPosted.child("pressed").setValue("true");
                        newPosted.child("newbookingcount").setValue(0);

                        Intent applicantsintent = new Intent(context, Bookings.class);
                        applicantsintent.putExtra("post_id",postkey);
                        applicantsintent.putExtra("post_title",posttitle);
                        applicantsintent.putExtra("post_desc",postdescrip);
                        applicantsintent.putExtra("city",city);
                        applicantsintent.putExtra("maincategory",maincategory);
                        applicantsintent.putExtra("subcategory",subcategory);
                        startActivity(applicantsintent);

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
                        removeBtn.setText("Remove Talent");
                        Button closeBtn = (Button) dialog.findViewById(R.id.closeBtn);
                        closeBtn.setVisibility(GONE);

                        dialog.show();

                        editBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent editpostintent = new Intent(context, EditTalent.class);
                                editpostintent.putExtra("post_id",postkey);
                                editpostintent.putExtra("city_id",city);
                                editpostintent.putExtra("maincategory",maincategory);
                                editpostintent.putExtra("subcategory",subcategory);
                                startActivity(editpostintent);
                                context.overridePendingTransition(R.anim.pullup,R.anim.nochange);

                                dialog.dismiss();
                            }
                        });


                        removeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                imagecount = 0;

                                final ProgressDialog mProgress = new ProgressDialog(getActivity());
                                mProgress.setMessage("Removing Talent..");
                                mProgress.setCancelable(false);
                                mProgress.show();

                                if (postimage.contains(" , ")) {
                                    String[] separatedimages = postimage.split(" , ");
                                    for (int x = 0; x < separatedimages.length; x++) {
                                        if (separatedimages[x].contains("youtube")) {
                                        }
                                        else {
                                            postimagelist.add(separatedimages[x]);
                                        }

                                    }
                                } else {
                                    if (postimage.contains("youtube")) {
                                    }
                                    else {
                                        postimagelist.add(postimage);
                                    }
                                }

                                if  (!postimagelist.isEmpty()) {
                                    for (int x = 0; x< postimagelist.size(); x++){
                                        storageRef = fbStorage.getReferenceFromUrl(postimagelist.get(x));
                                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                                imagecount++;
                                                Log.d(TAG, "onSuccess: deleted file");

                                                //REACH LAST ONE
                                                if (imagecount == postimagelist.size()) {
                                                    postimagelist.clear();
                                                    mTalent.child(city).child(maincategory).child(subcategory).child(postkey).removeValue();
                                                    mUserMyTalent.child(mAuth.getCurrentUser().getUid()).child(postkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            mProgress.dismiss();
                                                            dialog.dismiss();
                                                        }
                                                    });

                                                    mTalentVerify.child(mAuth.getCurrentUser().getUid()).child(postkey).removeValue();

                                                    //Notify all PENDING booking customers about the talent has removed
                                                    mUserMyTalentPendingBookings.child(ownuserid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                                                final String customer_id = userSnaphot.getKey();
                                                                mUserBookingMade.child(customer_id).child(postkey).child("status").setValue("removed");

                                                            }
                                                            mUserMyTalentPendingBookings.child(ownuserid).child(postkey).removeValue();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    //Notify all ACCEPTED booking customers about the talent has removed
                                                    mUserMyTalentAcceptedBookings.child(ownuserid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                                                final String customer_id = userSnaphot.getKey();
                                                                mUserBookingMade.child(customer_id).child(postkey).child("status").setValue("removed");
                                                            }
                                                            mUserMyTalentAcceptedBookings.child(ownuserid).child(postkey).removeValue();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                                Log.d(TAG, "onFailure: did not delete file");
                                                postimagelist.clear();
                                                mTalent.child(city).child(maincategory).child(subcategory).child(postkey).removeValue();
                                                mUserMyTalent.child(mAuth.getCurrentUser().getUid()).child(postkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mProgress.dismiss();
                                                        dialog.dismiss();
                                                    }
                                                });

                                                //Notify all PENDING booking customers about the talent has removed
                                                mUserMyTalentPendingBookings.child(ownuserid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                                            final String customer_id = userSnaphot.getKey();
                                                            mUserBookingMade.child(customer_id).child(postkey).child("status").setValue("removed");

                                                        }
                                                        mUserMyTalentPendingBookings.child(ownuserid).child(postkey).removeValue();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                //Notify all ACCEPTED booking customers about the talent has removed
                                                mUserMyTalentAcceptedBookings.child(ownuserid).child(postkey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot userSnaphot : dataSnapshot.getChildren()) {
                                                            final String customer_id = userSnaphot.getKey();
                                                            mUserBookingMade.child(customer_id).child(postkey).child("status").setValue("removed");
                                                        }
                                                        mUserMyTalentAcceptedBookings.child(ownuserid).child(postkey).removeValue();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });

            }
        };

        mMyTalentList.setAdapter(firebaseRecyclerAdapter);

        //Just display a layout to cover the reclerview when no jobs posted yet
        mUserMyTalent.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mstartpostedLay.setVisibility(GONE);
                }
                else{
                    mstartpostedLay.setVisibility(VISIBLE);
                    mstarttxt.setText("Start unleashing your talents today!");
                    mstarttxt2.setText("");
                    mstartImg.setImageResource(R.drawable.talent_mytalent);
                    mstartImg.setAlpha(0.8f);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        maddTalentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingintent = new Intent(context, ShowcaseTalent.class);
                startActivity(settingintent);
                context.overridePendingTransition(R.anim.pullup,R.anim.nochange);
            }
        });

        return rootView;
    }

    private void showPendingVerification() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.passwordreset_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        TextView mdialogtxt =  dialog.findViewById(R.id.dialogtxt);

        ImageView memaillogo = (ImageView) dialog.findViewById(R.id.emaillogo);
        memaillogo.setImageResource(R.drawable.invisible_128);
        memaillogo.setVisibility(VISIBLE);

        Button okbtn = (Button) dialog.findViewById(R.id.hireBtn);
        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
        cancelbtn.setVisibility(GONE);

        okbtn.setText("OK");
        okbtn.setTextColor(Color.parseColor("#0e52a5"));
        mdialogtxt.setText("Your Talent is under verification. It will not be visible to others until it is verified");

        dialog.show();

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });
    }
    

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView, mnotifiBadge;
        CircleImageView post_image;
        RelativeLayout mapplicantsRlay;
        TextView mtotalbookingtxt, mnewbookingtxt, mreviewstar, mreviewcount, mpostTitle, mpostDescrip, mnewreviewtxt;
        CardView cardview, mbookingCardView, mwholeCardView, mpendingCardView;
        Button mnewbookingBtn;
        ImageButton mactionBtn;
        RatingBar mratingstar;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mapplicantsRlay =  mView.findViewById(R.id.applicantsRlay);

            post_image = mView.findViewById(R.id.postImage);

            cardview =  mView.findViewById(R.id.cardview);
            mbookingCardView = mView.findViewById(R.id.bookingCardView);
            mwholeCardView = mView.findViewById(R.id.wholeCardView);
            mpendingCardView = mView.findViewById(R.id.pendingCardView);

            mnewbookingBtn = mView.findViewById(R.id.newbookingBtn);

            mratingstar = mView.findViewById(R.id.ratingstar);

            mtotalbookingtxt = mView.findViewById(totalbookingtxt);
            mnewbookingtxt = mView.findViewById(newbookingtxt);
            mreviewstar = mView.findViewById(reviewstar);
            mreviewcount = mView.findViewById(reviewcount);
            mpostTitle = mView.findViewById(postTitle);
            mpostDescrip = mView.findViewById(postDescrip);
            mnewreviewtxt = mView.findViewById(newreviewtxt);

            mactionBtn = mView.findViewById(actionBtn);

            mnotifiBadge = mView.findViewById(R.id.notifiBadge);

        }

        public void setVerified(String verified) {
            if (verified != null) {

                if (verified.equals("false")) {
                    mpendingCardView.setVisibility(VISIBLE);
                    mbookingCardView.setVisibility(GONE);
                }
                else {
                    mpendingCardView.setVisibility(GONE);
                    mbookingCardView.setVisibility(VISIBLE);
                }
            }
        }

        public void setRating(final Long reviewcount, final Long reviewstar){
            if(reviewcount!=null && reviewstar!=null) {

                mreviewstar.setText(String.valueOf(reviewstar));
                mreviewcount.setText("(" + String.valueOf(reviewcount) + ")");

            }
        }

        public void setNewReview(final String newreview){
            if(newreview!=null){
                if (newreview.equals("true")) {
                    mnewreviewtxt.setVisibility(VISIBLE);
                }
                else{
                    mnewreviewtxt.setVisibility(GONE);
                }
            }
            else {
                mnewreviewtxt.setVisibility(GONE);
            }
        }

        //Display red dot if job is shortlisted, rejected
        public void setNewStatus(final String pressed){
            if(pressed!=null){
                if (pressed.equals("false")) {
                    mnotifiBadge.setVisibility(VISIBLE);
                }
                else{
                    mnotifiBadge.setVisibility(GONE);
                }
            }
        }

        public void setTalentStatus(final Long totalbookingcount, final Long newbookingcount){
            if(totalbookingcount!=null && newbookingcount!=null){

                if (totalbookingcount > 0) {

                    mtotalbookingtxt.setText(String.valueOf(totalbookingcount) + " total bookings");

                    if (newbookingcount == 0) {
                        mnewbookingBtn.setVisibility(View.GONE);
                        mnewbookingtxt.setVisibility(View.GONE);
                    }
                    else if (newbookingcount > 0) {

                        mnewbookingBtn.setVisibility(View.VISIBLE);
                        mnewbookingBtn.setText(String.valueOf(newbookingcount));

                        mnewbookingtxt.setVisibility(View.VISIBLE);
                        mnewbookingtxt.setText(" new bookings");
                    }
                }
                else {
                    mnewbookingBtn.setVisibility(View.GONE);
                    mnewbookingtxt.setVisibility(View.GONE);
                    mtotalbookingtxt.setText(" No bookings yet");
                }

            }
        }

        public void setTitle_Descrip(String title, String descrip){
            if(title!=null && descrip!=null) {
                Log.d(TAG, "title sini " + title);
                Log.d(TAG, "descrip sini " + descrip);
                mpostTitle.setText(title);
                mpostDescrip.setText(descrip);
            }
        }

        public void setPostImage(Context ctx, String postimage){
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
