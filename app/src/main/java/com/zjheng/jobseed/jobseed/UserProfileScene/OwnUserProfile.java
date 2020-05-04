package com.zjheng.jobseed.jobseed.UserProfileScene;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
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
import com.michael.easydialog.EasyDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zjheng.jobseed.jobseed.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.anim.slide_down;
import static com.zjheng.jobseed.jobseed.R.id.delete;

public class OwnUserProfile extends AppCompatActivity {

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

    private RelativeLayout mtipsLay;
    private Button mlearnmoreBtn;

    private static final int ProfileImage_GALLERY_INTENT = 1;
    private static final int CoverImage_GALLERY_INTENT = 2;
    private static final int ProfileImage_CAMERA_REQUEST_CODE = 3;
    private static final String TAG = "OwnUserProfile";

    private String mCurrentPhotoPath;
    private Bitmap mImageBitmap;
    private Uri mCoverImageUri= null;
    private Uri mProfileImageUri= null;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private RatingBar mRatingBar;
    private int rateval = 0;
    private TextView mratingtxt;
    private long reviewcount5 = 0, reviewcount4 = 0, reviewcount3 = 0, reviewcount2 = 0 , reviewcount1 = 0, totalreviewcount = 0;

    public static ImageButton mprofileeditbtn, mchangeprofilepic;

    private Toolbar mToolbar;

    private String user_uid, user_name, post_userimage, userimage, CoverImage;

    private SharedPreferences firstuseprefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_fragment2);

        Log.d(TAG, "OwnUserProfile");

        firstuseprefs = getSharedPreferences("com.mycompany.my24Hires", MODE_PRIVATE);

        mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setTitle(" ");
        mToolbar.setSubtitle(" ");

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadData();

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_other);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_toolbar_other);
        collapsingToolbarLayout.setTitle(" ");

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if(user_name!=null) {
                        collapsingToolbarLayout.setTitle(user_name);
                        isShow = true;
                    }
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

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
                    return UserRatingTab.newInstance(user_uid);
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



    private void loadData() {

        mAuth = FirebaseAuth.getInstance();

        user_uid = mAuth.getCurrentUser().getUid();

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserReview");

        mprofileeditbtn = (ImageButton) findViewById(R.id.profileeditbtn);
        muserNametxt = (TextView) findViewById(R.id.userNametxt);
        mprofilepic = (CircleImageView) findViewById(R.id.profilepic);
        mpostImage = (ImageView) findViewById(R.id.postImage);
        mratingtxt = (TextView) findViewById(R.id.ratingtxt);
        mchangeprofilepic = (ImageButton) findViewById(R.id.changeprofilepic);

        mtipsLay = findViewById(R.id.tipsLay);

        mchangeprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAction();
            }
        });


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mRatingBar = (RatingBar) findViewById(R.id.userratingbar);
        mlearnmoreBtn =  findViewById(R.id.learnmoreBtn);

        View tabOne = (View) LayoutInflater.from(OwnUserProfile.this).inflate(R.layout.review_title_tab, null);
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
                startActivityForResult(galleryIntent,CoverImage_GALLERY_INTENT );
            }
        });

        mlearnmoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mtipsLay.getVisibility() == VISIBLE) {

                    Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down);

                    mtipsLay.startAnimation(slide_down);
                    mtipsLay.setVisibility(GONE);
                }

                final Dialog dialog = new Dialog(OwnUserProfile.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.compcard_dialog);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);

                final Button mokBtn = dialog.findViewById(R.id.okBtn);

                dialog.show();

                mokBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
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

        mUserReview.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
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

                    user_name = dataSnapshot.child("Name").getValue().toString();
                    muserNametxt.setText(user_name);
                } else {
                    mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild("name")) {
                                user_name = dataSnapshot.child("name").getValue().toString();
                                muserNametxt.setText(user_name);
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

        loadUserImage();

        checkfirstrun();

    }

    private void checkfirstrun() {

        if (firstuseprefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // IF first run, set user tokens
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mtipsLay.setVisibility(VISIBLE);
                    Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up);

                    mtipsLay.startAnimation(slide_up);
                }
            }, 1500);
            firstuseprefs.edit().putBoolean("firstrun", false).commit();
        }
    }

    private void loadUserImage() {

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                            Glide.with(getApplicationContext()).load(post_userimage)
                                    .thumbnail(0.5f)
                                    .centerCrop()
                                    .error(R.drawable.defaultprofile_pic)
                                    .placeholder(R.drawable.defaultprofile_pic)
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(mprofilepic);

                            if(dataSnapshot.hasChild("CoverImage")) {
                                String CoverImage = dataSnapshot.child("CoverImage").getValue().toString();
                                if (CoverImage != null) {
                                    Glide.with(getApplicationContext()).load(CoverImage)
                                            .thumbnail(0.5f)
                                            .error(R.drawable.profilebg3)
                                            .placeholder(R.drawable.profilebg3)
                                            .dontAnimate()
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(mpostImage);
                                }
                            }
                            else{
                                Glide.with(getApplicationContext()).load(post_userimage)
                                        .thumbnail(0.5f)
                                        .centerCrop()
                                        .bitmapTransform(new BlurTransformation(getApplicationContext(), 100))
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
                                        Glide.with(getApplicationContext()).load(post_userimage)
                                                .thumbnail(0.5f)
                                                .centerCrop()
                                                .error(R.drawable.defaultprofile_pic)
                                                .placeholder(R.drawable.defaultprofile_pic)
                                                .dontAnimate()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(mprofilepic);

                                        if(dataSnapshot.hasChild("CoverImage")) {
                                            String CoverImage = dataSnapshot.child("CoverImage").getValue().toString();
                                            if (CoverImage != null) {
                                                Glide.with(getApplicationContext()).load(CoverImage)
                                                        .thumbnail(0.5f)
                                                        .error(R.drawable.profilebg3)
                                                        .placeholder(R.drawable.profilebg3)
                                                        .dontAnimate()
                                                        .centerCrop()
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                        .into(mpostImage);
                                            }
                                        }
                                        else{
                                            Glide.with(getApplicationContext()).load(post_userimage)
                                                    .thumbnail(0.5f)
                                                    .centerCrop()
                                                    .bitmapTransform(new BlurTransformation(getApplicationContext(), 100))
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

    private void showAction() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addphoto_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        RelativeLayout mcameraActionbtn = (RelativeLayout) dialog.findViewById(R.id.cameraAction);

        RelativeLayout mgalleryActionbtn = (RelativeLayout) dialog.findViewById(R.id.galleryAction);

        dialog.show();

        mcameraActionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i(TAG, "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, ProfileImage_CAMERA_REQUEST_CODE);
                    }
                }

                dialog.dismiss();
            }
        });

        mgalleryActionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/* ");
                startActivityForResult(galleryIntent,ProfileImage_GALLERY_INTENT );
                dialog.dismiss();
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ProfileImage_GALLERY_INTENT && resultCode == RESULT_OK){

            Uri imageuri = data.getData();
            mProfileImageUri = imageuri;
            mCoverImageUri = null;

            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    //.setMaxCropResultSize(2500,1500)
                    .start(this);
        }
        else if (requestCode == ProfileImage_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Uri imageuri = Uri.parse(mCurrentPhotoPath);
                mProfileImageUri = imageuri;
                mCoverImageUri = null;

                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));

                CropImage.activity(imageuri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setMaxCropResultSize(2500,1500)
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == CoverImage_GALLERY_INTENT && resultCode == RESULT_OK){

            Uri imageuri = data.getData();
            mCoverImageUri = imageuri;
            mProfileImageUri = null;

            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //.setMaxCropResultSize(2500,1500)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(OwnUserProfile.this);
        }
        //set the image into imageview
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            Log.d(TAG, "cropact");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final ProgressDialog mProgress = new ProgressDialog(OwnUserProfile.this);
                mProgress.setMessage("Uploading..");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setCancelable(false);
                mProgress.show();

                final FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference storageRef = storage.getReferenceFromUrl("gs://jobseed-2cb76.appspot.com");
                StorageReference filepath;
                final DatabaseReference newImage;

                if (mCoverImageUri != null) {
                    mCoverImageUri = result.getUri();
                    Log.d(TAG, "mCoverImageUri " + mCoverImageUri);
                    filepath = storageRef.child("CoverPhotos").child(mCoverImageUri.getLastPathSegment());
                    newImage = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("CoverImage");
                    delete_uploadimage("CoverImage", storage, filepath, mCoverImageUri, newImage, mProgress);
                }
                else {
                    mProfileImageUri = result.getUri();
                    Log.d(TAG, "mProfileImageUri " + mProfileImageUri);
                    filepath = storageRef.child("ProfilePhotos").child(mProfileImageUri.getLastPathSegment());
                    newImage = mUserAccount.child(mAuth.getCurrentUser().getUid()).child("image");
                    delete_uploadimage("UserImage", storage, filepath, mProfileImageUri, newImage, mProgress);
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
            else if (resultCode == RESULT_CANCELED){
                mCoverImageUri = null;
            }
            else{
                mCoverImageUri = null;
            }
        }
    }

    private void delete_uploadimage(final String deleteName, final FirebaseStorage storage, final StorageReference filepath, final Uri imageuri, final DatabaseReference newImage, final ProgressDialog mdialog ) {

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(deleteName)) {

                    String deleteName_val = dataSnapshot.child(deleteName).getValue().toString();
                    Log.d(TAG, "deleteName_val: " + deleteName_val);

                    StorageReference oldpath = storage.getReferenceFromUrl(deleteName_val);

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

        if (deleteName.equals("UserImage")) {
            mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("image")) {

                        String image_val = dataSnapshot.child("image").getValue().toString();
                        Log.d(TAG, "image_val: " + image_val);

                        if ((!image_val.contains("google")) && (!image_val.contains("facebook"))) {
                            StorageReference oldpath = storage.getReferenceFromUrl(image_val);

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
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                if (deleteName.equals("UserImage")) {

                    Glide.with(getApplicationContext()).load(downloadUrl)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .dontAnimate()
                            .error(R.drawable.defaultprofile_pic)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mprofilepic);

                    newImage.setValue(downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mdialog.dismiss();
                        }
                    });
                }
                else {
                    Glide.with(getApplicationContext()).load(downloadUrl)
                            .thumbnail(0.5f)
                            .centerCrop()
                            .dontAnimate()
                            .error(R.drawable.defaultprofile_pic)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mpostImage);

                    newImage.setValue(downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mdialog.dismiss();
                        }
                    });
                }
            }
        });

        filepath.putFile(imageuri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "failupload");
                Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                mdialog.dismiss();
            }
        });
    }

}
