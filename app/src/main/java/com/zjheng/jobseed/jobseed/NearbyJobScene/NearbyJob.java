package com.zjheng.jobseed.jobseed.NearbyJobScene;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CustomObjectClass.Job;
import com.zjheng.jobseed.jobseed.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.map;

/**
 * Created by zhen on 5/14/2017.
 *
 */

public class NearbyJob extends AppCompatActivity {

    private RecyclerView mnearbyjoblist;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mnojobLay;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserChatList, mChatRoom, mUserAccount, mUserInfo, mGeoFire, mJob;
    private GeoFire geoFire;
    private GeoQuery geoQuery;

    private static final String TAG = "NearbyJob";
    private static int REQ_PERMISSION = 1;

    private String currentuserid, userimage, currentcity, oldkey;

    private SimpleLocation location;
    private int count;

    private boolean keyentered = false;

    private List <Job> joblist;
    private List<Marker> markerList;
    private NearbyJobRecyclerAdapter recyclerAdapter;

    private GoogleMap mgoogleMap;
    private MapView mMapView;

    private Toolbar mToolbar;

    // class property
    private static final String KEY_MAP_SAVED_STATE = "mapState";

   /* public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "first");
        mMapView = (MapView) getView().findViewById(map);
        MapsInitializer.initialize(context);
        mMapView = (MapView) rootView.findViewById(map);
        Bundle mapState = (savedInstanceState != null)
                ? savedInstanceState.getBundle(KEY_MAP_SAVED_STATE): null;
        mMapView.onCreate(mapState);
        //....
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearbyjob);

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();

        mUserChatList =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserChatList");

        mChatRoom =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("ChatRoom");

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "connected to wifi");
                //Connected
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "connected to data");
                //Connected
            }
        } else {
            //Disconnected
            Toast.makeText(NearbyJob.this, "Network Not Available", Toast.LENGTH_LONG).show();
        }

        mnojobLay = (RelativeLayout) findViewById(R.id.nojobLay);

        mnearbyjoblist = (RecyclerView)findViewById(R.id.nearbyjoblist);
        mnearbyjoblist.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);

        markerList = new ArrayList<Marker>();
        joblist = new ArrayList<Job>();
        recyclerAdapter = new NearbyJobRecyclerAdapter(mJob,joblist,NearbyJob.this);
        mnearbyjoblist.setLayoutManager(mLayoutManager);
        mnearbyjoblist.setAdapter(recyclerAdapter);

        mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setTitle(" ");
        mToolbar.setSubtitle(" ");

        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarLayout mAppBarLayout = (AppBarLayout)findViewById(R.id.appbar_toolbar_other);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_other);
        collapsingToolbarLayout.setTitle(" ");

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Nearby Jobs");
                    isShow = true;

                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        mMapView = (MapView) findViewById(map);
        /*try {
            // Temporary fix for crash issue
            mMapView.onCreate(savedInstanceState);
        } catch (Throwable t) {
            t.printStackTrace();
        }*/

        mMapView.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null);
        //mMapView.onCreate(savedInstanceState);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getApplicationContext());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                mgoogleMap = googleMap;

                location = new SimpleLocation(NearbyJob.this);
                // if we can't access the location yet
                if (!location.hasLocationEnabled()) {
                    // ask the user to enable location access
                    SimpleLocation.openSettings(NearbyJob.this);
                }

                final double latitude = location.getLatitude();
                final double longitude = location.getLongitude();

                Log.d(TAG, "latitude: " + latitude);
                Log.d(TAG, "longitude: " + longitude);

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                /*if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(context, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
                    if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                }*/

                if(checkPermission())
                    googleMap.setMyLocationEnabled(true);
                else askPermission();

                LatLng cur_Latlng = new LatLng(latitude, longitude);

                //googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(cur_Latlng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), null);

                //googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.usersign)));
                //CameraPosition markposition = CameraPosition.builder().target(new LatLng(latitude,longitude)).zoom(12).build();
                //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(markposition));

                if (mMapView != null &&
                        mMapView.findViewById(Integer.parseInt("1")) != null) {
                    // Get the button view
                    View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    // and next place it, on bottom right (as Google Maps app)
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            locationButton.getLayoutParams();
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.setMargins(0, 0, 30, 30);
                }

                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        // Cleaning all the markers.

                        Log.d(TAG, "newlatxx " +  googleMap.getCameraPosition().target.latitude);
                        Log.d(TAG, "newlongxx " +  googleMap.getCameraPosition().target.longitude);
                        geoaddress(NearbyJob.this, googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude, googleMap);
                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // Open the info window for the marker
                        marker.showInfoWindow();
                        return true;
                    }
                });
            }

        });
    }


    public void geoaddress(Activity context, final double latitude, final double longitude, final GoogleMap googleMap ) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {

                String address = addressList.get(0).getAddressLine(0);
                String city = addressList.get(0).getLocality();
                String state = addressList.get(0).getAdminArea();
                String country = addressList.get(0).getCountryName();

                String fulladdress = address+city+state+country;

                if(state!=null){
                    currentcity = state;
                }
                else{
                    currentcity = addressList.get(0).getLocality();
                }

                if(fulladdress.contains("Pulau Pinang") || fulladdress.contains("Penang")) {currentcity = "Penang";}
                else if (fulladdress.contains("Kuala Lumpur")) {currentcity = "Kuala Lumpur";}
                else if (fulladdress.contains("Labuan")) {currentcity = "Labuan";}
                else if (fulladdress.contains("Putrajaya")) {currentcity = "Putrajaya";}
                else if (fulladdress.contains("Johor")) {currentcity = "Johor";}
                else if (fulladdress.contains("Kedah")) {currentcity = "Kedah";}
                else if (fulladdress.contains("Kelantan")) {currentcity = "Kelantan";}
                else if (fulladdress.contains("Melaka")|| fulladdress.contains("Melacca")) {currentcity = "Melacca";}
                else if (fulladdress.contains("Negeri Sembilan")|| fulladdress.contains("Seremban")) {currentcity = "Negeri Sembilan";}
                //
                else if (fulladdress.contains("Pahang")) {currentcity = "Pahang";}
                else if (fulladdress.contains("Perak")|| fulladdress.contains("Ipoh")) {currentcity = "Perak";}
                else if (fulladdress.contains("Perlis")) {currentcity = "Perlis";}
                else if (fulladdress.contains("Sabah")) {currentcity = "Sabah";}
                else if (fulladdress.contains("Sarawak")) {currentcity = "Sarawak";}
                else if (fulladdress.contains("Selangor")|| fulladdress.contains("Shah Alam")|| fulladdress.contains("Klang")) {currentcity = "Selangor";}
                else if (fulladdress.contains("Terengganu")) {currentcity = "Terengganu";}

                detectnearbyjob(context, currentcity, latitude, longitude, googleMap);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void detectnearbyjob(final Activity context , final String currentcity, final double latitude, final double longitude, final GoogleMap googleMap){

        if(context != null) {

            oldkey = "";
            count = 0;
            keyentered = false;
            joblist.clear();
            for (Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
            mnojobLay.setVisibility(VISIBLE);

            if (geoQuery != null ) {

                Log.d(TAG, "remove all listener");
                geoQuery.removeAllListeners();
            }

            mGeoFire = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("JobsLocation").child(currentcity);

            geoFire = new GeoFire(mGeoFire);

            geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), 2.5);

            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(final String key, final GeoLocation location) {

                    mnojobLay.setVisibility(GONE);

                    keyentered = true;

                    if (!key.equals(oldkey)) {

                        mJob.child(currentcity).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.child(key).exists()) {

                                        if (dataSnapshot.child(key).hasChild("title") && dataSnapshot.child(key).hasChild("fulladdress")
                                                && dataSnapshot.child(key).hasChild("postimage")
                                                && dataSnapshot.child(key).hasChild("postkey")
                                                && dataSnapshot.child(key).hasChild("city")
                                                && dataSnapshot.child(key).hasChild("closed")
                                                ) {
                                            Job jobs = new Job();

                                            String title = dataSnapshot.child(key).child("title").getValue().toString();
                                            String fulladdress = dataSnapshot.child(key).child("fulladdress").getValue().toString();
                                            String postimage = dataSnapshot.child(key).child("postimage").getValue().toString();
                                            String postkey = dataSnapshot.child(key).child("postkey").getValue().toString();
                                            String city = dataSnapshot.child(key).child("city").getValue().toString();
                                            String closed = dataSnapshot.child(key).child("closed").getValue().toString();

                                            jobs.setTitle(title);
                                            jobs.setFulladdress(fulladdress);
                                            jobs.setpostImage(postimage);
                                            jobs.setpostkey(postkey);
                                            jobs.setCity(city);
                                            jobs.setclosed(closed);

                                            if (closed.equals("false")) {

                                                count++;
                                                jobs.setcount(count);

                                                joblist.add(jobs);
                                                Marker marker;
                                                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).title(title)
                                                        .snippet(fulladdress)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(context, R.drawable.location2, String.valueOf(count))));
                                                marker = googleMap.addMarker(markerOptions);
                                                markerList.add(marker);
                                            }

                                            recyclerAdapter.notifyDataSetChanged();
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    oldkey = key;

                }

                @Override
                public void onKeyExited(String key) {
                    Log.d(TAG, "exitGeokey: " + key);
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    Log.d(TAG, "movedGeokey: " + key);
                }

                @Override
                public void onGeoQueryReady() {
                    Log.d(TAG, "keyentered: " + keyentered);
                    if (!keyentered) {
                        joblist.clear();
                        for (Marker marker : markerList) {
                            marker.remove();
                        }
                        markerList.clear();
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });
        }
    }

    private Bitmap writeTextOnDrawable(Activity context, int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(context, 12));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(context, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 1;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) (((canvas.getHeight() / 2)-13) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);

        return  bm;
    }


    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f) ;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nochange, R.anim.pulldown);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(NearbyJob.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }
    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                NearbyJob.this,
                new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    if(checkPermission())
                        mgoogleMap.setMyLocationEnabled(true);

                } else {
                    // Permission denied

                }
        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        Log.d(TAG, "onDestroy");
        if (geoQuery != null) {
            geoQuery.removeAllListeners();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}