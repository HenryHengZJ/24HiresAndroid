package com.zjheng.jobseed.jobseed.UnUsedFiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.Settings;
import com.zjheng.jobseed.jobseed.UserProfileScene.*;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class UserProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount, mUserInfo, mUserLocation, mUserReview;
    public static CircleImageView mprofilepic;

    public static LinearLayout mAge, mHeight, mWeight;
    public static TextView magetxt, mheighttxt, mweighttxt;

    public static TextView muserNametxt;
    public static TextView mworktime1, mworktime2, mworktime3;

    public static RelativeLayout mworkexp1, mworkexp2, mworkexp3;
    public static TextView mworkexp1txt, mworkexp2txt, mworkexp3txt, mworkcompany1txt, mworkcompany2txt, mworkcompany3txt;
    public static ImageButton mworkexpeditbtn;

    public static TextView mcontacttxt1, mphonecontact;
    public static ImageButton mcontacteditbtn;

    public static TextView meducationtxt1;
    public static ImageButton meducationeditbtn;

    public static TextView mlanguagetxt1;
    public static ImageButton mlanguageeditbtn;

    public static ImageButton mabouteditbtn;
    public static TextView mAbouttxt;

    public static ImageButton mlocationeditbtn;
    public static TextView mlocationtxt1;

    public static ImageView mpostImage;
    private ProgressDialog mProgressDialog;

    private static int PLACE_PICKER_REQUEST = 1;
    private String useraddress;

    private static final int GALLERY_INTENT = 3;
    private static final String TAG = "UserProfile";

    private String mCurrentPhotoPath;
    private Bitmap mImageBitmap;
    private Uri mImageUri= null;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private RatingBar mRatingBar;
    private int rateval = 0;
    private TextView mratingtxt;
    private long reviewcount5 = 0, reviewcount4 = 0, reviewcount3 = 0, reviewcount2 = 0 , reviewcount1 = 0, totalreviewcount = 0;

    public static ImageButton mprofileeditbtn;

    private String user_uid;

    private CardView msettingsCardView;

    private Boolean isStarted = false;
    private Boolean isVisible = false;
    private Boolean firsttime = false;

    Activity context;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_user_profile_fragment, container, false);

        context=getActivity();

        Log.d(TAG, "UserProfileFragment");

        firsttime = true;

        setHasOptionsMenu(true);

        Toolbar mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar_other);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
       // ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");

        return rootView;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    UserProfileInfo tab1 = new UserProfileInfo();
                    return tab1;
                case 1:
                    return com.zjheng.jobseed.jobseed.UserProfileScene.UserRatingTab.newInstance(user_uid);
                default:
                    return null;
            }


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Profile";
                case 1:
                    return "Review";
            }
            return null;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent settingintent = new Intent(context, Settings.class);
                startActivity(settingintent);

                return false;
            }
        });

        MenuItem itemSearch = menu.findItem(R.id.menuSearch2);
        itemSearch.setVisible(false);

        MenuItem item = menu.findItem(R.id.menuSearch);
        item.setVisible(false);

        MenuItem itemPublish = menu.findItem(R.id.menuPublish);
        itemPublish.setVisible(false);

        MenuItem itemSave = menu.findItem(R.id.menuSave);
        itemSave.setVisible(false);
    }

    public static UserProfileFragment newInstance(String bar) {
        return new UserProfileFragment();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint");
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

        Log.d(TAG, "view did appear ");

        if (firsttime) {
            loadData();
            firsttime = false;
        }

    }

    private void loadData() {

        mAuth = FirebaseAuth.getInstance();

        user_uid = mAuth.getCurrentUser().getUid();

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");
        mUserAccount.keepSynced(true);

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");
        mUserInfo.keepSynced(true);

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");
        mUserReview.keepSynced(true);

        mprofileeditbtn = (ImageButton) rootView.findViewById(R.id.profileeditbtn);
        muserNametxt = (TextView) rootView.findViewById(R.id.userNametxt);
        mprofilepic = (CircleImageView) rootView.findViewById(R.id.profilepic);
        mpostImage = (ImageView) rootView.findViewById(R.id.postImage);
        mratingtxt = (TextView) rootView.findViewById(R.id.ratingtxt);

        msettingsCardView = (CardView)rootView.findViewById(R.id.settingsCardView);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mRatingBar = (RatingBar) rootView.findViewById(R.id.userratingbar);

        View tabOne = (View) LayoutInflater.from(getActivity()).inflate(R.layout.review_title_tab, null);
        final TextView tv_title = (TextView) tabOne.findViewById(R.id.tv_title);
        tv_title.setTextColor(Color.parseColor("#84878c"));
        tv_title.setText("REVIEW");
        final TextView tv_count = (TextView) tabOne.findViewById(R.id.tv_count);
        tabLayout.getTabAt(1).setCustomView(tabOne);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 1){
                    tv_title.setTextColor(Color.parseColor("#FF67B8ED"));
                    tv_count.setVisibility(GONE);
                    mUserReview.child(mAuth.getCurrentUser().getUid()).child("Notification").setValue("false");
                }
                else{
                    tv_title.setTextColor(Color.parseColor("#84878c"));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mprofileeditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/* ");
                startActivityForResult(galleryIntent,GALLERY_INTENT );
            }
        });

        msettingsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent settingintent = new Intent(context, Settings.class);
                startActivity(settingintent);
            }
        });

        mUserReview.child(mAuth.getCurrentUser().getUid()).child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null){
                    String Notification = dataSnapshot.getValue().toString();

                    if(Notification.equals("true")){
                        tv_count.setVisibility(VISIBLE);
                    }
                    else{
                        tv_count.setVisibility(GONE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserReview.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_uid)){
                    if(dataSnapshot.child(user_uid).hasChild("Rate5")){
                        reviewcount5 = (Long)dataSnapshot.child(user_uid).child("Rate5").getValue();
                    }
                    if(dataSnapshot.child(user_uid).hasChild("Rate4")){
                        reviewcount4 = (Long)dataSnapshot.child(user_uid).child("Rate4").getValue();
                    }
                    if(dataSnapshot.child(user_uid).hasChild("Rate3")){
                        reviewcount3 = (Long)dataSnapshot.child(user_uid).child("Rate3").getValue();
                    }
                    if(dataSnapshot.child(user_uid).hasChild("Rate2")){
                        reviewcount2 = (Long)dataSnapshot.child(user_uid).child("Rate2").getValue();
                    }
                    if(dataSnapshot.child(user_uid).hasChild("Rate1")){
                        reviewcount1 = (Long)dataSnapshot.child(user_uid).child("Rate1").getValue();
                    }
                    totalreviewcount = reviewcount5+reviewcount4+reviewcount3+reviewcount2+reviewcount1;
                }
                mratingtxt.setText(totalreviewcount+" Reviews");

                if (totalreviewcount !=0) {
                    long starcount = ((5*reviewcount5)+(4*reviewcount4)+(3*reviewcount3)+(2*reviewcount2)+(1*reviewcount1))/(totalreviewcount);
                    rateval = Math.round(starcount);
                }
                mRatingBar.setRating(rateval);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("Name")) {

                    String Name = dataSnapshot.child("Name").getValue().toString();
                    muserNametxt.setText(Name);
                } else {
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild("name")) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                muserNametxt.setText(name);
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

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("UserImage")) {

                    Log.d(TAG, "got UserImage");

                    String post_userimage = dataSnapshot.child("UserImage").getValue().toString();
                    if (post_userimage != null) {
                        if (post_userimage.equals("default")) {
                            mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                            mpostImage.setBackgroundColor(Color.parseColor("#67b8ed"));
                        } else {

                            Log.d(TAG, "load UserImage");

                            Glide.with(getActivity()).load(post_userimage)
                                    .thumbnail(0.5f)
                                    .centerCrop()
                                    .error(R.drawable.ic_clear_black_24dp)
                                    .placeholder(R.drawable.defaultprofile_pic)
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(mprofilepic);

                            if(dataSnapshot.hasChild("CoverImage")) {
                                String CoverImage = dataSnapshot.child("CoverImage").getValue().toString();
                                if (CoverImage != null) {
                                    Glide.with(getActivity()).load(CoverImage)
                                            .thumbnail(0.5f)
                                            .error(R.drawable.ic_clear_black_24dp)
                                            .placeholder(R.color.colorPrimaryDark)
                                            .dontAnimate()
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mpostImage);
                                }
                            }
                            else{
                                Glide.with(getActivity()).load(post_userimage)
                                        .thumbnail(0.5f)
                                        .centerCrop()
                                        .bitmapTransform(new BlurTransformation(getActivity(), 100))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mpostImage);
                            }
                        }
                    }
                }

                else{

                    mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {

                            if(dataSnapshot1.hasChild("image")){
                                Log.d(TAG, "gt post_userimage");

                                String post_userimage = dataSnapshot1.child("image").getValue().toString();
                                if (post_userimage != null) {
                                    if (post_userimage.equals("default")) {
                                        mprofilepic.setImageResource(R.drawable.defaultprofile_pic);
                                    } else {
                                        Glide.with(getActivity()).load(post_userimage)
                                                .thumbnail(0.5f)
                                                .centerCrop()
                                                .error(R.drawable.ic_clear_black_24dp)
                                                .placeholder(R.drawable.defaultprofile_pic)
                                                .dontAnimate()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(mprofilepic);

                                        if(dataSnapshot.hasChild("CoverImage")) {
                                            String CoverImage = dataSnapshot.child("CoverImage").getValue().toString();
                                            if (CoverImage != null) {
                                                Glide.with(getActivity()).load(CoverImage)
                                                        .thumbnail(0.5f)
                                                        .error(R.drawable.ic_clear_black_24dp)
                                                        .placeholder(R.color.colorPrimaryDark)
                                                        .dontAnimate()
                                                        .centerCrop()
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                        .into(mpostImage);
                                            }
                                        }
                                        else{
                                            Glide.with(getActivity()).load(post_userimage)
                                                    .thumbnail(0.5f)
                                                    .centerCrop()
                                                    .bitmapTransform(new BlurTransformation(getActivity(), 100))
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(mpostImage);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            Uri imageuri = data.getData();
            Log.d(TAG, "imageuri " + imageuri);

            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //.setMaxCropResultSize(2500,1500)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(getContext(),UserProfileFragment.this);
        }
        //set the image into imageview
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            Log.d(TAG, "cropact");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                Log.d(TAG, "mImageUri " + mImageUri);

                final FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference storageRef = storage.getReferenceFromUrl("gs://jobseed-2cb76.appspot.com");
                StorageReference filepath = storageRef.child("CoverPhotos").child(mImageUri.getLastPathSegment());
                final DatabaseReference newImage = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("CoverImage");

                final ProgressDialog mProgress = new ProgressDialog(getActivity());
                mProgress.setMessage("Uploading..");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setCancelable(false);
                mProgress.show();

                mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("CoverImage")) {

                            String UserImage = dataSnapshot.child("CoverImage").getValue().toString();
                            StorageReference oldpath = storage.getReferenceFromUrl(UserImage);
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        newImage.setValue(downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Log.d(TAG, "onSuccessUpload");

                                Glide.with(getActivity()).load(mImageUri)
                                        .thumbnail(0.5f)
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mpostImage);

                                mProgress.dismiss();
                            }
                        });
                    }
                });

                filepath.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG, "failupload");
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
            else if (resultCode == RESULT_CANCELED){
                mImageUri = null;
            }
            else{
                mImageUri = null;
            }
        }
    }

}
