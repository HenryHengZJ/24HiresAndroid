package com.zjheng.jobseed.jobseed.OtherUserScene;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.zjheng.jobseed.jobseed.CustomObjectClass.TalentInfo;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.ShowcaseTalentScene.ShowcaseTalent;
import com.zjheng.jobseed.jobseed.TalentDetails.TalentDetail;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.zjheng.jobseed.jobseed.R.id.actionBtn;
import static com.zjheng.jobseed.jobseed.R.id.newbookingtxt;
import static com.zjheng.jobseed.jobseed.R.id.newreviewtxt;
import static com.zjheng.jobseed.jobseed.R.id.postDescrip;
import static com.zjheng.jobseed.jobseed.R.id.postTitle;
import static com.zjheng.jobseed.jobseed.R.id.reviewcount;
import static com.zjheng.jobseed.jobseed.R.id.reviewstar;
import static com.zjheng.jobseed.jobseed.R.id.totalbookingtxt;

/**
 * Created by fg8hqq on 3/9/2018.
 */

public class OtherUserTalents extends Fragment {

    private RecyclerView mMyTalentList;
    private LinearLayoutManager mLayoutManager;

    private RelativeLayout mstartpostedLay;

    private DatabaseReference mUserMyTalent;

    private static final String TAG = "OtherUserTalents";

    private String userid;

    private ImageView mstartingdate_tickimg;

    private TextView mtextView22;

    private FirebaseRecyclerAdapter<TalentInfo, BlogViewHolder> firebaseRecyclerAdapter;

    Activity context;
    View rootView;

    public static OtherUserTalents newInstance(String userid) {
        OtherUserTalents result = new OtherUserTalents();
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

        rootView = inflater.inflate(R.layout.userpostedtab, container, false);

        context = getActivity();

        mUserMyTalent =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("MyTalent");

        mstartpostedLay = (RelativeLayout) rootView.findViewById(R.id.startpostedLay);

        mstartingdate_tickimg = rootView.findViewById(R.id.startingdate_tickimg);
        mstartingdate_tickimg.setImageResource(R.drawable.talent_mytalent);
        mstartingdate_tickimg.setAlpha(0.8f);

        mtextView22 = rootView.findViewById(R.id.textView22);
        mtextView22.setText("User has not showcased any talents yet");

        mMyTalentList = (RecyclerView)rootView.findViewById(R.id.postedlist);

        mMyTalentList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mMyTalentList.setLayoutManager(mLayoutManager);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TalentInfo, BlogViewHolder>(
                TalentInfo.class,
                R.layout.mytalentrow,
                BlogViewHolder.class,
                mUserMyTalent.child(userid)

        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, TalentInfo model, final int position) {

                final String postkey = getRef(position).getKey();
                final String city = model.getcity();
                final String posttitle = model.gettitle();
                final String postdescrip = model.getdesc();
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

                viewHolder.setRating(reviewcount,reviewstar);
                viewHolder.setTitle_Descrip(posttitle, postdescrip);
                viewHolder.setPostImage(context.getApplicationContext(), postimage0);

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

            }
        };

        mMyTalentList.setAdapter(firebaseRecyclerAdapter);

        if(userid!=null) {
            //Just display a layout to cover the reclerview when no jobs posted yet
            mUserMyTalent.child(userid).addValueEventListener(new ValueEventListener() {
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

        return rootView;
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        CircleImageView post_image;
        RelativeLayout mapplicantsRlay;
        TextView mreviewstar, mreviewcount, mpostTitle, mpostDescrip;
        CardView cardview, mbookingCardView, mpendingCardView;
        RatingBar mratingstar;
        ImageButton mactionBtn;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mapplicantsRlay =  mView.findViewById(R.id.applicantsRlay);

            mbookingCardView = mView.findViewById(R.id.bookingCardView);
            mbookingCardView.setVisibility(GONE);
            mpendingCardView = mView.findViewById(R.id.pendingCardView);
            mpendingCardView.setVisibility(GONE);

            post_image = mView.findViewById(R.id.postImage);

            cardview =  mView.findViewById(R.id.cardview);

            mratingstar = mView.findViewById(R.id.ratingstar);

            mreviewstar = mView.findViewById(reviewstar);
            mreviewcount = mView.findViewById(reviewcount);
            mpostTitle = mView.findViewById(postTitle);
            mpostDescrip = mView.findViewById(postDescrip);

            mactionBtn = mView.findViewById(actionBtn);
            mactionBtn.setVisibility(GONE);

        }

        public void setRating(final Long reviewcount, final Long reviewstar){
            if(reviewcount!=null && reviewstar!=null) {

                mreviewstar.setText(String.valueOf(reviewstar));
                mreviewcount.setText("(" + String.valueOf(reviewcount) + ")");

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

