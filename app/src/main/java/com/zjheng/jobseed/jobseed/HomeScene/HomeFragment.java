package com.zjheng.jobseed.jobseed.HomeScene;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.zjheng.jobseed.jobseed.BuildConfig;
import com.zjheng.jobseed.jobseed.HomeScene.DiscoverTalent.DiscoverTalentFragment3;
import com.zjheng.jobseed.jobseed.HomeScene.ExploreJobs.ExploreJobFragment;
import com.zjheng.jobseed.jobseed.Mlab.ExploreJobFragment2;
import com.zjheng.jobseed.jobseed.R;
import com.zjheng.jobseed.jobseed.SearchJobScene.SearchBar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {


    private FirebaseAuth mAuth;
    private DatabaseReference mUserLocation, mAppVersion;

    //  public static TextView mcurrentLocation;
    private ProgressDialog HomeProgress;

    private static final String TAG = "HomeFragment";
    private static int PLACE_PICKER_REQUEST = 1;

    private String city = "none";

    private SharedPreferences prefs, updateprefs;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private CardView mlocationCardView;

    Activity context;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_home_fragment2, container, false);

        context = getActivity();

        setHasOptionsMenu(true);

        updateprefs = getActivity().getSharedPreferences("updateval", Context.MODE_PRIVATE);

        rateapps();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mlocationCardView = (CardView)rootView.findViewById(R.id.locationCardView);

        Log.d(TAG, "homefrag");

        mAuth = FirebaseAuth.getInstance();

        mUserLocation =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserLocation");
        mUserLocation.child(mAuth.getCurrentUser().getUid()).keepSynced(true);

        checkappsversion();

        checkuserLocation();

        mlocationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchBar.class);
                intent.putExtra("city_id",city);
                Pair<View, String> p1 = Pair.create((View)mlocationCardView, "searchBar");
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context , p1);
                startActivity(intent, optionsCompat.toBundle());
            }
        });

        return rootView;
    }

    private void checkuserLocation(){

        mUserLocation.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //If user has saved location

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("CurrentCity")) {
                        city = dataSnapshot.child("CurrentCity").getValue().toString();
                        if (city != null) {

                            // mcurrentLocation.setText(city);

                            mSectionsPagerAdapter.notifyDataSetChanged();

                        }
                    } else {

                        // mcurrentLocation.setText("Specify your location");

                    }
                }
                //If user first time login, no saved location yet
                else{

                    //  mcurrentLocation.setText("Specify your location");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static HomeFragment newInstance(String foo) {
        return new HomeFragment();
    }

    private void checkappsversion(){

        mAppVersion =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("AppVersion");

        mAppVersion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("AndroidVersion") && dataSnapshot.hasChild("AndroidUpdateType")){

                    String updatetypeval = dataSnapshot.child("AndroidUpdateType").getValue().toString();
                    String realversion = dataSnapshot.child("AndroidVersion").getValue().toString();
                    String userversion = BuildConfig.VERSION_NAME;

                    if(!realversion.equals(userversion)){

                        if (updatetypeval.equals("1")) {
                            optionalUpdate(realversion,userversion,updatetypeval);
                        }
                        else if (updatetypeval.equals("2")) {
                            if(!updateprefs.contains("hideupdate")){
                                optionalUpdate(realversion,userversion,updatetypeval);
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

    private void optionalUpdate(String realversion,String userversion, final String updatetypeval){
        if(!realversion.equals(userversion)){
            // custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.applicantsdialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
            TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
            Button hirebtn = (Button) dialog.findViewById(R.id.hireBtn);

            hirebtn.setText("UPDATE");
            hirebtn.setTextColor(Color.parseColor("#ff669900"));
            mdialogtxt.setText("New version is available. Do you want to update now?");

            cancelbtn.setText("LATER");

            dialog.show();

            hirebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (updatetypeval.equals("2")) {
                        updateprefs.edit().clear().apply();
                    }

                    final String appPackageName = "com.zjheng.jobseed.jobseed"; // package name of the app
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }

                    dialog.dismiss();
                }
            });

            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (updatetypeval.equals("2")) {
                        updateprefs.edit().putString("hideupdate", "yes").apply();
                    }

                    dialog.dismiss();
                }
            });

        }

    }

    private void rateapps(){

        prefs = getActivity().getSharedPreferences("progress", MODE_PRIVATE);
        int appUsedCount = prefs.getInt("appUsedCount",0);
        appUsedCount++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("appUsedCount", appUsedCount);
        editor.apply();

        if (appUsedCount==10 || appUsedCount==50 || appUsedCount==100 || appUsedCount==200 || appUsedCount==300){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.applicantsdialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
            TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
            Button hirebtn = (Button) dialog.findViewById(R.id.hireBtn);

            hirebtn.setText("Rate it");
            cancelbtn.setText("Not now");
            hirebtn.setTextColor(Color.parseColor("#ff669900"));
            mdialogtxt.setText("Like our app? Rate it in playstore! Your feedback is important for us!");

            dialog.show();

            hirebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String appPackageName = "com.zjheng.jobseed.jobseed"; // package name of the app
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }

                    dialog.dismiss();
                }
            });

            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.menuSearch2);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent intent = new Intent(context, SearchBar.class);
                intent.putExtra("city_id",city);
                startActivity(intent);

                return false;
            }
        });

        MenuItem itemSearch = menu.findItem(R.id.menuSearch);
        itemSearch.setVisible(false);

        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setVisible(false);

        MenuItem itemPublish = menu.findItem(R.id.menuPublish);
        itemPublish.setVisible(false);

        MenuItem itemSave = menu.findItem(R.id.menuSave);
        itemSave.setVisible(false);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1111)) {
            // recreate your fragment here
            Log.d(TAG, "here");
        }

        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(HomeFragment.this).attach(HomeFragment.this).commit();
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                HomeProgress.dismiss();

                Place place = PlacePicker.getPlace(context.getApplicationContext(), data);
                city = "";
                String address = place.getAddress().toString();
                Log.d(TAG, "address: " + address);

                Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    if (addresses.size() > 0) {
                        String[] addressSlice = place.getAddress().toString().split(", ");
                        //city = addressSlice[addressSlice.length - 2];
                        city = addresses.get(0).getAdminArea();

                        String postCode = addresses.get(0).getPostalCode();
                        if (city.equals(postCode)) {city = addressSlice[addressSlice.length - 3];}

                        if (city == null) {city = addresses.get(0).getCountryName();}

                        if(address.contains("Pulau Pinang") || address.contains("Penang")) {city = "Penang";}
                        else if (address.contains("Kuala Lumpur")) {city = "Kuala Lumpur";}
                        else if (address.contains("Labuan")) {city = "Labuan";}
                        else if (address.contains("Putrajaya")) {city = "Putrajaya";}
                        else if (address.contains("Johor")) {city = "Johor";}
                        else if (address.contains("Kedah")) {city = "Kedah";}
                        else if (address.contains("Kelantan")) {city = "Kelantan";}
                        else if (address.contains("Melaka")|| address.contains("Melacca")) {city = "Melacca";}
                        else if (address.contains("Negeri Sembilan")|| address.contains("Seremban")) {city = "Negeri Sembilan";}
                        //
                        else if (address.contains("Pahang")) {city = "Pahang";}
                        else if (address.contains("Perak")|| address.contains("Ipoh")) {city = "Perak";}
                        else if (address.contains("Perlis")) {city = "Perlis";}
                        else if (address.contains("Sabah")) {city = "Sabah";}
                        else if (address.contains("Sarawak")) {city = "Sarawak";}
                        else if (address.contains("Selangor")|| address.contains("Shah Alam")|| address.contains("Klang")) {city = "Selangor";}
                        else if (address.contains("Terengganu")) {city = "Terengganu";}

                        Log.d(TAG, "cityhome: " + city);

                        mUserLocation.child(mAuth.getCurrentUser().getUid()).child("CurrentCity").setValue(city);
                        //  mcurrentLocation.setText(city);

                        final ProgressDialog mdialog = new ProgressDialog(context,R.style.MyTheme);
                        mdialog.setCancelable(false);
                        mdialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        mdialog.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //FragmentTransaction ft = getFragmentManager().beginTransaction();
                                //ft.detach(HomeFragment.this).attach(HomeFragment.this).commit();

                                mSectionsPagerAdapter.notifyDataSetChanged();
                                mdialog.dismiss();
                            }
                        }, 500); //time seconds
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            else if (resultCode == RESULT_CANCELED) {
                HomeProgress.dismiss();
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return ExploreJobFragment2.newInstance(city);
                case 1:
                    return DiscoverTalentFragment3.newInstance(city);
                default:
                    return null;
            }


        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try{
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException){
                System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Explore Jobs";
                case 1:
                    return "Discover Talents";
            }
            return null;
        }
    }

}
