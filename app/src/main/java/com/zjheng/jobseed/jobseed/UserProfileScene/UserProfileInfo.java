package com.zjheng.jobseed.jobseed.UserProfileScene;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.Settings;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class UserProfileInfo extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount, mUserInfo, mUserLocation;

    public static LinearLayout mGender, mAge, mHeight, mWeight;
    public static TextView mgendertxt, magetxt, mheighttxt, mweighttxt;

    public static TextView mworktime1, mworktime2, mworktime3, mworktime4, mworktime5;

    public static LinearLayout mworkexp1, mworkexp2, mworkexp3, mworkexp4, mworkexp5, mverifiLay;
    public static TextView mworkexp1txt, mworkexp2txt, mworkexp3txt, mworkexp4txt, mworkexp5txt;
    public static TextView mworkcompany1txt, mworkcompany2txt, mworkcompany3txt, mworkcompany4txt, mworkcompany5txt;
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

    private TextView muserprofilecomplete;
    private ProgressBar muserprogressbar;

    private ProgressDialog mProgressDialog;

    private static int PLACE_PICKER_REQUEST = 1;
    private String useraddress;

    private static final int GALLERY_INTENT = 3;
    private static final String TAG = "UserProfile";

    private String mCurrentPhotoPath;
    private Bitmap mImageBitmap;
    private Uri mImageUri= null;

    private int scoremarks = 0;

    private ImageView mfbImg, mgoogleImg, memailImg;

    Activity context;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_user_profile_info2, container, false);

        context=getActivity();

        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");

        mProgressDialog = new ProgressDialog(getActivity());

        mAge = (LinearLayout) rootView.findViewById(R.id.AgeLay);
        mGender = (LinearLayout) rootView.findViewById(R.id.GenderLay);
        mHeight = (LinearLayout) rootView.findViewById(R.id.HeightLay);
        mWeight = (LinearLayout) rootView.findViewById(R.id.WeightLay);

        muserprofilecomplete = (TextView) rootView.findViewById(R.id.userprofilecomplete);
        muserprogressbar = rootView.findViewById(R.id.userprogressbar);

        magetxt = (TextView) rootView.findViewById(R.id.useragetxt);
        mheighttxt = (TextView) rootView.findViewById(R.id.userheightttxt);
        mgendertxt = (TextView) rootView.findViewById(R.id.gendertxt);
        mweighttxt = (TextView) rootView.findViewById(R.id.userweighttxt);

        mworktime1 = (TextView) rootView.findViewById(R.id.worktime1);
        mworktime2 = (TextView) rootView.findViewById(R.id.worktime2);
        mworktime3 = (TextView) rootView.findViewById(R.id.worktime3);
        mworktime4 = (TextView) rootView.findViewById(R.id.worktime4);
        mworktime5 = (TextView) rootView.findViewById(R.id.worktime5);

        mworkexp1 = (LinearLayout) rootView.findViewById(R.id.workexp1);
        mworkexp2 = (LinearLayout) rootView.findViewById(R.id.workexp2);
        mworkexp3 = (LinearLayout) rootView.findViewById(R.id.workexp3);
        mworkexp4 = (LinearLayout) rootView.findViewById(R.id.workexp4);
        mworkexp5 = (LinearLayout) rootView.findViewById(R.id.workexp5);

        mworkexp1txt = (TextView) rootView.findViewById(R.id.worktitletxt);
        mworkexp2txt = (TextView) rootView.findViewById(R.id.worktitletxt2);
        mworkexp3txt = (TextView) rootView.findViewById(R.id.worktitletxt3);
        mworkexp4txt = (TextView) rootView.findViewById(R.id.worktitletxt4);
        mworkexp5txt = (TextView) rootView.findViewById(R.id.worktitletxt5);

        mworkcompany1txt = (TextView) rootView.findViewById(R.id.workcompanytxt);
        mworkcompany2txt = (TextView) rootView.findViewById(R.id.workcompanytxt2);
        mworkcompany3txt = (TextView) rootView.findViewById(R.id.workcompanytxt3);
        mworkcompany4txt = (TextView) rootView.findViewById(R.id.workcompanytxt4);
        mworkcompany5txt = (TextView) rootView.findViewById(R.id.workcompanytxt5);

        mworkexpeditbtn = (ImageButton) rootView.findViewById(R.id.workexpeditbtn);

        mcontacttxt1 = (TextView) rootView.findViewById(R.id.contacttxt1);
        mphonecontact= (TextView) rootView.findViewById(R.id.phonecontact);
        mcontacteditbtn = (ImageButton) rootView.findViewById(R.id.contacteditbtn);

        meducationtxt1 = (TextView) rootView.findViewById(R.id.educationtxt1);
        meducationeditbtn = (ImageButton) rootView.findViewById(R.id.educationeditbtn);

        mlanguagetxt1 = (TextView) rootView.findViewById(R.id.languagetxt1);
        mlanguageeditbtn = (ImageButton) rootView.findViewById(R.id.languageeditbtn);

        mabouteditbtn = (ImageButton) rootView.findViewById(R.id.abouteditbtn);
        mAbouttxt = (TextView) rootView.findViewById(R.id.Abouttxt);

        mlocationeditbtn = (ImageButton) rootView.findViewById(R.id.locationeditbtn);
        mlocationtxt1 = (TextView) rootView.findViewById(R.id.locationtxt1);


        mworkexpeditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent workexpintent = new Intent(context, WorkExperience.class);
                startActivity(workexpintent);
            }
        });

        mcontacteditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactintent = new Intent(context, Contact.class);
                startActivity(contactintent);
            }
        });

        meducationeditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent educationintent = new Intent(context, Education.class);
                startActivity(educationintent);
            }
        });

        mlanguageeditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent languageintent = new Intent(context, Language.class);
                startActivity(languageintent);
            }
        });

        mabouteditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editintent = new Intent(context, EditProfile.class);
                startActivity(editintent);
            }
        });

        mlocationeditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog.setMessage("Loading");
                mProgressDialog.show();

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent;
                try {
                    intent = builder.build(context);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        mverifiLay = (LinearLayout) rootView.findViewById(R.id.verifiLay);
        mfbImg = (ImageView) rootView.findViewById(R.id.fbImg);
        mgoogleImg = (ImageView) rootView.findViewById(R.id.googleImg);
        memailImg = (ImageView) rootView.findViewById(R.id.emailImg);

        mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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


        mUserInfo.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("WorkExp1") && dataSnapshot.child("WorkExp1").hasChild("worktitle") && dataSnapshot.child("WorkExp1").hasChild("workcompany")) {

                    scoremarks += 10;

                    String worktitle = dataSnapshot.child("WorkExp1").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp1").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp1").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp1").child("worktime").getValue().toString();
                        mworktime1.setVisibility(VISIBLE);
                        mworktime1.setText("- "+worktime);
                    }

                    mworkexp1txt.setText(worktitle);
                    mworkcompany1txt.setText(workcompany);

                }
                if (dataSnapshot.hasChild("WorkExp2") && dataSnapshot.child("WorkExp2").hasChild("worktitle") && dataSnapshot.child("WorkExp2").hasChild("workcompany")) {

                    String worktitle = dataSnapshot.child("WorkExp2").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp2").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp2").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp2").child("worktime").getValue().toString();
                        mworktime2.setText("- "+worktime);
                    }

                    mworkexp2txt.setText(worktitle);
                    mworkcompany2txt.setText(workcompany);

                    Log.d(TAG, "workexp2 notempty");

                    mworkexp2.setVisibility(VISIBLE);

                }
                if (dataSnapshot.hasChild("WorkExp3") && dataSnapshot.child("WorkExp3").hasChild("worktitle") && dataSnapshot.child("WorkExp3").hasChild("workcompany")) {

                    String worktitle = dataSnapshot.child("WorkExp3").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp3").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp3").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp3").child("worktime").getValue().toString();
                        mworktime3.setText("- "+worktime);
                    }

                    mworkexp3txt.setText(worktitle);
                    mworkcompany3txt.setText(workcompany);

                    mworkexp3.setVisibility(VISIBLE);

                }
                if (dataSnapshot.hasChild("WorkExp4") && dataSnapshot.child("WorkExp4").hasChild("worktitle") && dataSnapshot.child("WorkExp4").hasChild("workcompany")) {

                    String worktitle = dataSnapshot.child("WorkExp4").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp4").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp4").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp4").child("worktime").getValue().toString();
                        mworktime4.setText("- "+worktime);
                    }

                    mworkexp4txt.setText(worktitle);
                    mworkcompany4txt.setText(workcompany);
                    mworkexp4.setVisibility(VISIBLE);
                }
                else{
                    mworkexp4.setVisibility(GONE);
                }
                if (dataSnapshot.hasChild("WorkExp5") && dataSnapshot.child("WorkExp5").hasChild("worktitle") && dataSnapshot.child("WorkExp5").hasChild("workcompany")) {

                    String worktitle = dataSnapshot.child("WorkExp5").child("worktitle").getValue().toString();
                    String workcompany = dataSnapshot.child("WorkExp5").child("workcompany").getValue().toString();

                    if(dataSnapshot.child("WorkExp5").hasChild("worktime")){
                        String worktime = dataSnapshot.child("WorkExp5").child("worktime").getValue().toString();
                        mworktime5.setText("- "+worktime);
                    }

                    mworkexp5txt.setText(worktitle);
                    mworkcompany5txt.setText(workcompany);
                    mworkexp5.setVisibility(VISIBLE);
                }
                else{
                    mworkexp5.setVisibility(GONE);
                }

                if (dataSnapshot.hasChild("Gender")) {
                    scoremarks += 10;
                    mGender.setVisibility(VISIBLE);
                    String Gender = dataSnapshot.child("Gender").getValue().toString();
                    magetxt.setText(Gender);
                }
                else{
                    mGender.setVisibility(GONE);
                }

                if (dataSnapshot.hasChild("Age")) {
                    scoremarks += 10;
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

                if (dataSnapshot.hasChild("Email")) {
                    scoremarks += 10;
                    String email = dataSnapshot.child("Email").getValue().toString();
                    mcontacttxt1.setText(email);
                }

                if (dataSnapshot.hasChild("Phone")) {
                    String phone = dataSnapshot.child("Phone").getValue().toString();
                    mphonecontact.setText(phone);
                }

                if (dataSnapshot.hasChild("Education")) {
                    scoremarks += 10;
                    String education = dataSnapshot.child("Education").getValue().toString();
                    meducationtxt1.setText(education);
                }

                if (dataSnapshot.hasChild("Language")) {
                    scoremarks += 10;
                    String language = dataSnapshot.child("Language").getValue().toString();
                    mlanguagetxt1.setText(language);
                }
                if (dataSnapshot.hasChild("About")) {
                    scoremarks += 10;
                    String about = dataSnapshot.child("About").getValue().toString();
                    mAbouttxt.setText(about);
                }
                if (dataSnapshot.hasChild("Address")) {
                    String address = dataSnapshot.child("Address").getValue().toString();
                    mlocationtxt1.setText(address);
                } else {
                    mUserLocation.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("CurrentCity")) {
                                String Address = dataSnapshot.child("CurrentCity").getValue().toString();
                                mlocationtxt1.setText(Address);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    }

                    Log.d(TAG, "scoremarks =" + scoremarks);
                    computescore(scoremarks);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        return rootView;
    }

    private void computescore(int scoremarks){

        double totalscore1 = (double)scoremarks / (double)70;
        double totalscore2 = (totalscore1 * 100);
        int finalscore = (int) totalscore2;

        muserprofilecomplete.setText("Profile Complete: " + String.valueOf(finalscore) + "%");
        muserprogressbar.setProgress(finalscore);

        if (finalscore <25) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable progressDrawable = muserprogressbar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                muserprogressbar.setProgressDrawable(progressDrawable);
                muserprofilecomplete.setTextColor(Color.parseColor("#FFF23215"));

            }else {
                muserprogressbar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                muserprofilecomplete.setTextColor(Color.parseColor("#FFF23215"));
            }
        }
        else if (finalscore >25 && finalscore <75) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable progressDrawable = muserprogressbar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(Color.parseColor("#f7b51a"), android.graphics.PorterDuff.Mode.SRC_IN);
                muserprogressbar.setProgressDrawable(progressDrawable);
                muserprofilecomplete.setTextColor(Color.parseColor("#f7b51a"));

            }else {
                muserprogressbar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#f7b51a")));
                muserprofilecomplete.setTextColor(Color.parseColor("#f7b51a"));
            }
        }
        else if (finalscore >75) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable progressDrawable = muserprogressbar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(Color.parseColor("#6dda61"), android.graphics.PorterDuff.Mode.SRC_IN);
                muserprogressbar.setProgressDrawable(progressDrawable);
                muserprofilecomplete.setTextColor(Color.parseColor("#109502"));

            }else {
                muserprogressbar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#6dda61")));
                muserprofilecomplete.setTextColor(Color.parseColor("#109502"));
            }
        }

    }

    /*@Override
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

        MenuItem item = menu.findItem(R.id.menuSearch);
        item.setVisible(false);

        MenuItem itemPublish = menu.findItem(R.id.menuPublish);
        itemPublish.setVisible(false);

        MenuItem itemSave = menu.findItem(R.id.menuSave);
        itemSave.setVisible(false);
    }*/

    public static UserProfileInfo newInstance(String bar) {
        return new UserProfileInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(context.getApplicationContext(), data);
                String address = place.getAddress().toString();

                Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    if (addresses.size() > 0) {

                        String[] addressSlice = place.getAddress().toString().split(", ");
                        useraddress = addresses.get(0).getAdminArea();
                        String postCode = addresses.get(0).getPostalCode();
                        String countryname = addresses.get(0).getCountryName();

                        if (useraddress == null) {
                            useraddress = addresses.get(0).getCountryName();
                            mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Address").setValue(useraddress);
                            mlocationtxt1.setText(useraddress);
                        }
                        else {

                            if (useraddress.equals(postCode)) {
                                useraddress = addressSlice[addressSlice.length - 3];
                            }
                            if (address.indexOf("Pulau Pinang") > -1) {
                                useraddress = "Penang";
                            } else if (address.indexOf("Kuala Lumpur") > -1) {
                                useraddress = "Kuala Lumpur";
                            } else if (address.indexOf("Labuan") > -1) {
                                useraddress = "Labuan";
                            } else if (address.indexOf("Putrajaya") > -1) {
                                useraddress = "Putrajaya";
                            } else if (address.indexOf("Johor") > -1) {
                                useraddress = "Johor";
                            } else if (address.indexOf("Kedah") > -1) {
                                useraddress = "Kedah";
                            } else if (address.indexOf("Kelantan") > -1) {
                                useraddress = "Kelantan";
                            } else if (address.indexOf("Melaka") > -1) {
                                useraddress = "Melacca";
                            } else if (address.indexOf("Negeri Sembilan") > -1) {
                                useraddress = "Negeri Sembilan";
                            }
                            //
                            else if (address.indexOf("Pahang") > -1) {
                                useraddress = "Pahang";
                            } else if (address.indexOf("Perak") > -1) {
                                useraddress = "Perak";
                            } else if (address.indexOf("Perlis") > -1) {
                                useraddress = "Perlis";
                            } else if (address.indexOf("Sabah") > -1) {
                                useraddress = "Sabah";
                            } else if (address.indexOf("Sarawak") > -1) {
                                useraddress = "Sarawak";
                            } else if (address.indexOf("Selangor") > -1) {
                                useraddress = "Selangor";
                            } else if (address.indexOf("Terengganu") > -1) {
                                useraddress = "Terengganu";
                            }

                            mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Address").setValue(useraddress);
                            mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").setValue(useraddress);
                            mlocationtxt1.setText(useraddress+ ", " +countryname);
                        }

                        mProgressDialog.dismiss();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            else if (resultCode == RESULT_CANCELED) {
                mProgressDialog.dismiss();
            }
        }
    }
}
