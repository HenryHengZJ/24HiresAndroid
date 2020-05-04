package com.zjheng.jobseed.jobseed.OtherUserScene;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhen on 5/5/2017.
 */

public class OtherUserDetails extends Fragment {

    private RecyclerView mSavedList;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserActivities, mUserLocation, mUserAccount, mUserInfo , mJob, mUserReport;

    private LinearLayout mGender, mAge, mHeight, mWeight, mverifiLay;
    private TextView mgendertxt, magetxt, mheighttxt, mweighttxt;

    private TextView mAbouttxt, mcontacttxt1, mphonecontact;
    private TextView mworktitletxt, mworktitletxt2,mworktitletxt3,mworktitletxt4,mworktitletxt5;
    private TextView mworkcompanytxt,mworkcompanytxt2,mworkcompanytxt3,mworkcompanytxt4,mworkcompanytxt5;
    private TextView meducationtxt1, mlanguagetxt1;
    private TextView mreporttxt;
    private TextView  mworktime1, mworktime2, mworktime3, mworktime4, mworktime5;

    private LinearLayout mworkexp2, mworkexp3, mworkexp4, mworkexp5;
    private ExpandableRelativeLayout expandableLayout4, expandableLayout5;
    private CardView mseemorecardview;

    private ImageView mfbImg, mgoogleImg, memailImg;

    private String userid;

    Activity context;
    View rootView;

    public static OtherUserDetails newInstance(String userid) {
        OtherUserDetails result = new OtherUserDetails();
        Bundle bundle = new Bundle();
        bundle.putString("useruid", userid);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        userid = bundle.getString("useruid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_otheruserdetails2, container, false);

        context = getActivity();

        mAuth = FirebaseAuth.getInstance();

        mUserActivities = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mUserReport =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReport");

        mJob = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mGender = (LinearLayout) rootView.findViewById(R.id.GenderLay);
        mAge = (LinearLayout) rootView.findViewById(R.id.AgeLay);
        mHeight = (LinearLayout) rootView.findViewById(R.id.HeightLay);
        mWeight = (LinearLayout) rootView.findViewById(R.id.WeightLay);
        mverifiLay = (LinearLayout) rootView.findViewById(R.id.verifiLay);

        magetxt = (TextView) rootView.findViewById(R.id.useragetxt);
        mgendertxt = (TextView) rootView.findViewById(R.id.gendertxt);
        mheighttxt = (TextView) rootView.findViewById(R.id.userheightttxt);
        mweighttxt = (TextView) rootView.findViewById(R.id.userweighttxt);

        mfbImg = (ImageView) rootView.findViewById(R.id.fbImg);
        mgoogleImg = (ImageView) rootView.findViewById(R.id.googleImg);
        memailImg = (ImageView) rootView.findViewById(R.id.emailImg);

        mAbouttxt = (TextView) rootView.findViewById(R.id.Abouttxt);
        mcontacttxt1 = (TextView) rootView.findViewById(R.id.contacttxt1);

        mworktitletxt = (TextView) rootView.findViewById(R.id.worktitletxt);
        mworkcompanytxt = (TextView) rootView.findViewById(R.id.workcompanytxt);

        mworktitletxt2 = (TextView) rootView.findViewById(R.id.worktitletxt2);
        mworkcompanytxt2 = (TextView) rootView.findViewById(R.id.workcompanytxt2);

        mworktitletxt3 = (TextView) rootView.findViewById(R.id.worktitletxt3);
        mworkcompanytxt3 = (TextView) rootView.findViewById(R.id.workcompanytxt3);

        mworktitletxt4 = (TextView) rootView.findViewById(R.id.worktitletxt4);
        mworkcompanytxt4 = (TextView) rootView.findViewById(R.id.workcompanytxt4);

        mworktitletxt5 = (TextView) rootView.findViewById(R.id.worktitletxt5);
        mworkcompanytxt5 = (TextView) rootView.findViewById(R.id.workcompanytxt5);

        meducationtxt1 = (TextView) rootView.findViewById(R.id.educationtxt1);
        mlanguagetxt1 = (TextView) rootView.findViewById(R.id.languagetxt1);
        mphonecontact = (TextView) rootView.findViewById(R.id.phonecontact);
        mreporttxt = (TextView) rootView.findViewById(R.id.reportusertxt);

        mworktime1 = (TextView) rootView.findViewById(R.id.worktime1);
        mworktime2 = (TextView) rootView.findViewById(R.id.worktime2);
        mworktime3 = (TextView) rootView.findViewById(R.id.worktime3);
        mworktime4 = (TextView) rootView.findViewById(R.id.worktime4);
        mworktime5 = (TextView) rootView.findViewById(R.id.worktime5);

        mworkexp2 = (LinearLayout) rootView.findViewById(R.id.workexp2);
        mworkexp3 = (LinearLayout) rootView.findViewById(R.id.workexp3);
        mworkexp4 = (LinearLayout) rootView.findViewById(R.id.workexp4);
        mworkexp5 = (LinearLayout) rootView.findViewById(R.id.workexp5);

        mseemorecardview = (CardView) rootView.findViewById(R.id.seemorecardview);

        expandableLayout4 = (ExpandableRelativeLayout) rootView.findViewById(R.id.expandableLayout4);
        expandableLayout5 = (ExpandableRelativeLayout) rootView.findViewById(R.id.expandableLayout5);


        mseemorecardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mseemorecardview.setVisibility(GONE);

                expandableLayout4.toggle(); // toggle expand and collapse
                expandableLayout5.toggle(); // toggle expand and collapse
               // expandableLayout4.setVisibility(VISIBLE);
                //expandableLayout5.setVisibility(VISIBLE);

            }
        });

        mUserAccount.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("provider")) {
                    String providerval = dataSnapshot.child("provider").getValue().toString();

                    if (providerval.equals("facebook")) {
                        mverifiLay.setVisibility(VISIBLE);
                        mfbImg.setVisibility(VISIBLE);
                    }
                    else if (providerval.equals("google")) {
                        mverifiLay.setVisibility(VISIBLE);
                        mgoogleImg.setVisibility(VISIBLE);
                    }
                    else {
                        mverifiLay.setVisibility(VISIBLE);
                        memailImg.setVisibility(VISIBLE);
                    }
                }
                else {
                    mverifiLay.setVisibility(VISIBLE);
                    memailImg.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserInfo.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("About")) {

                    String About = dataSnapshot.child("About").getValue().toString();
                    mAbouttxt.setText(About);
                }
                else{
                    mAbouttxt.setHint("No Description Added");
                }

                if (dataSnapshot.hasChild("Gender")) {
                    mGender.setVisibility(VISIBLE);
                    String Gender = dataSnapshot.child("Gender").getValue().toString();
                    mgendertxt.setText(Gender);
                }
                else{
                    mGender.setVisibility(GONE);
                }

                if (dataSnapshot.hasChild("Age")) {
                    mAge.setVisibility(VISIBLE);
                    String Age = dataSnapshot.child("Age").getValue().toString();
                    magetxt.setText(Age);
                }
                else{
                    mAge.setVisibility(GONE);
                }

                if (dataSnapshot.hasChild("Weight")) {
                    mWeight.setVisibility(VISIBLE);
                    String Weight = dataSnapshot.child("Weight").getValue().toString();
                    mweighttxt.setText(Weight+ " kg");
                }
                else{
                    mWeight.setVisibility(GONE);
                }

                if (dataSnapshot.hasChild("Height")) {
                    mHeight.setVisibility(VISIBLE);
                    String Height = dataSnapshot.child("Height").getValue().toString();
                    mheighttxt.setText(Height+ " cm");
                }
                else{
                    mHeight.setVisibility(GONE);
                }

                if(dataSnapshot.hasChild("Education")) {

                    String Education = dataSnapshot.child("Education").getValue().toString();
                    meducationtxt1.setText(Education);
                }
                else{
                    meducationtxt1.setHint("No Education Added");
                }
                if(dataSnapshot.hasChild("Email")) {

                    String Email = dataSnapshot.child("Email").getValue().toString();
                    mcontacttxt1.setText(Email);
                }
                else{
                    mcontacttxt1.setHint("No Email Added");
                }
                if(dataSnapshot.hasChild("Phone")) {

                    String Phone = dataSnapshot.child("Phone").getValue().toString();
                    mphonecontact.setText(Phone);
                }
                else{
                    mphonecontact.setHint("No Phone Added");
                }
                if(dataSnapshot.hasChild("Language")) {

                    String Language = dataSnapshot.child("Language").getValue().toString();
                    mlanguagetxt1.setText(Language);
                }
                else{
                    mlanguagetxt1.setHint("No Languages added");
                }
                if(dataSnapshot.hasChild("WorkExp1")) {

                    String worktitle = dataSnapshot.child("WorkExp1").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp1").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp1").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp1").child("worktime").getValue().toString();
                        mworktime1.setVisibility(VISIBLE);
                        mworktime1.setText("- "+worktime);
                    }

                    mworktitletxt.setText(worktitle);
                    mworkcompanytxt.setText(workcompany);
                }
                else{
                    mworkcompanytxt.setHint("No Work Experiences");
                    mworktitletxt.setVisibility(View.GONE);
                }
                if(dataSnapshot.hasChild("WorkExp2")) {

                    String worktitle = dataSnapshot.child("WorkExp2").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp2").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp2").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp2").child("worktime").getValue().toString();
                        mworktime2.setVisibility(VISIBLE);
                        mworktime2.setText("- "+worktime);
                    }

                    mworkexp2.setVisibility(View.VISIBLE);

                    mworktitletxt2.setText(worktitle);
                    mworkcompanytxt2.setText(workcompany);
                }
                else{
                    mworkexp2.setVisibility(View.GONE);
                    mworkexp3.setVisibility(View.GONE);
                }
                if(dataSnapshot.hasChild("WorkExp3")) {

                    String worktitle = dataSnapshot.child("WorkExp3").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp3").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp3").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp3").child("worktime").getValue().toString();
                        mworktime3.setVisibility(VISIBLE);
                        mworktime3.setText("- "+worktime);
                    }

                    mworkexp3.setVisibility(View.VISIBLE);

                    mworktitletxt3.setText(worktitle);
                    mworkcompanytxt3.setText(workcompany);
                }
                else{
                    mworkexp3.setVisibility(View.GONE);
                }
                if (dataSnapshot.hasChild("WorkExp4") && dataSnapshot.child("WorkExp4").hasChild("worktitle") && dataSnapshot.child("WorkExp4").hasChild("workcompany")) {

                    String worktitle = dataSnapshot.child("WorkExp4").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp4").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp4").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp4").child("worktime").getValue().toString();
                        mworktime4.setText("- "+worktime);
                    }

                    mworktitletxt4.setText(worktitle);
                    mworkcompanytxt4.setText(workcompany);

                    mseemorecardview.setVisibility(VISIBLE);

                    mworkexp4.setVisibility(View.VISIBLE);
                    expandableLayout4.setVisibility(View.VISIBLE);
                }
                else{
                    mworkexp4.setVisibility(View.GONE);
                    expandableLayout4.setVisibility(View.GONE);
                }
                if (dataSnapshot.hasChild("WorkExp5") && dataSnapshot.child("WorkExp5").hasChild("worktitle") && dataSnapshot.child("WorkExp5").hasChild("workcompany")) {

                    String worktitle = dataSnapshot.child("WorkExp5").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp5").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp5").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp5").child("worktime").getValue().toString();
                        mworktime5.setText("- "+worktime);
                    }

                    mworktitletxt5.setText(worktitle);
                    mworkcompanytxt5.setText(workcompany);

                    mseemorecardview.setVisibility(VISIBLE);

                    mworkexp5.setVisibility(View.VISIBLE);
                    expandableLayout5.setVisibility(View.VISIBLE);
                }
                else{
                    mworkexp5.setVisibility(View.GONE);
                    expandableLayout5.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mreporttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.report_dialog);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);

                CardView mspamcardview = (CardView)  dialog.findViewById(R.id.spamcardview);
                CardView mirrelavantcardview = (CardView)  dialog.findViewById(R.id.irrelavantcardview);
                mirrelavantcardview.setVisibility(GONE);
                CardView minappropriatecardview = (CardView)  dialog.findViewById(R.id.inappropriatecardview);

                dialog.show();

                mspamcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserReport.child(mAuth.getCurrentUser().getUid()).child("User").child("Spam").child(userid).setValue(userid);
                        showreportdialog();

                        dialog.dismiss();
                    }
                });

                minappropriatecardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mUserReport.child(mAuth.getCurrentUser().getUid()).child("User").child("Inappropriate").child(userid).setValue(userid);
                        showreportdialog();

                        dialog.dismiss();
                    }
                });
            }
        });


        return rootView;
    }

    private void showreportdialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.applicantsdialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
        cancelbtn.setVisibility(GONE);
        TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
        Button okbtn = (Button) dialog.findViewById(R.id.hireBtn);

        okbtn.setText("OK");
        okbtn.setTextColor(Color.parseColor("#0e52a5"));
        mdialogtxt.setText("Thanks for making our community better with this feedback. We will take appropriate action against abuse.");

        dialog.show();

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }
}

