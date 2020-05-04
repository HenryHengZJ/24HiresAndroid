package com.zjheng.jobseed.jobseed.LoginScene;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.zjheng.jobseed.jobseed.CreateProfile;
import com.zjheng.jobseed.jobseed.HomeScene.BannerDetails;
import com.zjheng.jobseed.jobseed.LoginScene.EmailLogin.LoginwithEmail;
import com.zjheng.jobseed.jobseed.LoginScene.SignUp.RegisterActivity;
import com.zjheng.jobseed.jobseed.MainActivity;
import com.zjheng.jobseed.jobseed.R;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.facebook.FacebookSdk.getApplicationContext;
//import static com.google.android.gms.fitness.data.zzs.Te;

public class Login extends AppCompatActivity {


    private Button  mRegisterBtn, mEmailloginBtn;
    private LoginButton mFbloginBtn;
    private SignInButton mGoogleBtn;
    private TextView mTnCtxt;
    private ImageButton mbackBtn;

    private DatabaseReference mUserAccount, mTalent, mJob;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mcallbackManager;

    // variable to track event time
    private long mLastClickTime = 0;

    private ProgressDialog mProgress;
    private ProgressDialog authProgress;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = Login.class.getName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private int currentPage = -1;
    private int NUM_PAGES = 4;
    private Timer timer;
    private long DELAY_MS = 5000;//delay in milliseconds before task is to be executed
    private long PERIOD_MS = 5000; // time in milliseconds between successive task executions.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);

        mProgress = new ProgressDialog(this);
        authProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mTalent = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Talent");

        mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("Job");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setPageTransformer(false, new FadePageTransformer());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        mEmailloginBtn = (Button) findViewById(R.id.emailoginBtn);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mFbloginBtn = (LoginButton) findViewById(R.id.fbloginBtn);
        mGoogleBtn = (SignInButton) findViewById(R.id.googleloginBtn);
        mTnCtxt = (TextView) findViewById(R.id.TnCtxt);
        mbackBtn = (ImageButton) findViewById(R.id.backBtn);

        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Terms of services Clicked", Toast.LENGTH_SHORT).show();
                String urllink = "https://24hires.com/terms-of-use/";
                Intent detailintent = new Intent(Login.this, TnCDetails.class);
                detailintent.putExtra("linkurl", urllink);
                detailintent.putExtra("title", "Terms of Use");
                startActivity(detailintent);
            }
        };

        ClickableSpan privacyPolicyClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Privacy Policy Clicked", Toast.LENGTH_SHORT).show();
                String urllink = "https://24hires.com/privacy-policy/";
                Intent detailintent = new Intent(Login.this, TnCDetails.class);
                detailintent.putExtra("linkurl", urllink);
                detailintent.putExtra("title", "Privacy Policy");
                startActivity(detailintent);
            }
        };

        makeLinks(mTnCtxt, new String[] { "Terms of Use", "Privacy Policy" }, new ClickableSpan[] {
                termsOfServicesClick, privacyPolicyClick
        });

        mcallbackManager = CallbackManager.Factory.create();
        mFbloginBtn.setReadPermissions("email", "public_profile");

        //This will scroll page-by-page so that you can view scroll happening
                /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                mViewPager.setCurrentItem(++currentPage, true);
                // go to initial page i.e. position 0
                if (currentPage == NUM_PAGES -1) {
                    currentPage = -1;
                    // ++currentPage will make currentPage = 0
                }
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                currentPage = position;

                if(position == NUM_PAGES -1){
                    currentPage = -1;
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                authProgress.setMessage("Authenticating");
                authProgress.show();
                signIn();
            }
        });

        mFbloginBtn.registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                handleFacebookAccessToken(loginResult.getAccessToken());
                mFbloginBtn.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        mEmailloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signupIntent = new Intent(Login.this, LoginwithEmail.class);
                signupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signupIntent);
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(Login.this, RegisterActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            }
        });

        FirebaseUser mUser= mAuth.getCurrentUser();

        if (mUser != null) {
            // User is signed in
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            String uid = mAuth.getCurrentUser().getUid();
            intent.putExtra("user_id", uid);
            startActivity(intent);
            finish();
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                }

            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("848133085531-n50o57k22bqdpmu2pf7hc22jb33hme7t.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Google Login Error", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nochange, R.anim.pulldown);
    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    //Google Sign In Functions
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {

                            final String uid=task.getResult().getUser().getUid();
                            final String name=task.getResult().getUser().getDisplayName();
                            final String email=task.getResult().getUser().getEmail();
                            String image=task.getResult().getUser().getPhotoUrl().toString();
                            image = image.replace("/s96-c/","/s300-c/");
                            final String finalImage = image;

                            final Map<String, Object> providerData = new HashMap<>();

                            mUserAccount.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()){

                                        providerData.put("id", uid);
                                        providerData.put("name", name);
                                        providerData.put("email", email);
                                        providerData.put("image", finalImage);
                                        providerData.put("provider", "google");
                                        mUserAccount.child(uid).updateChildren(providerData);

                                        // Go to CreateProfile
                                        Intent intent = new Intent(getApplicationContext(), CreateProfile.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {

                                        providerData.put("provider", "google");
                                        mUserAccount.child(uid).updateChildren(providerData);

                                        // Go to MainActivity
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.putExtra("user_id",uid);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                        mProgress.dismiss();
                    }
                });
    }

    //Facebook Login
    private void handleFacebookAccessToken(AccessToken accessToken) {

        mProgress.setMessage("Signing In");
        mProgress.setCancelable(false);
        mProgress.show();

        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Facebook Login Error", Toast.LENGTH_LONG).show();
                } else {

                    final String uid=task.getResult().getUser().getUid();
                    final String name=task.getResult().getUser().getDisplayName();
                    final String email=task.getResult().getUser().getEmail();
                    // String image=task.getResult().getUser().getPhotoUrl().toString();

                    String facebookUserId = "";
                    FirebaseUser muser = FirebaseAuth.getInstance().getCurrentUser();
                    // find the Facebook profile and get the user's id
                    for(UserInfo profile : muser.getProviderData()) {
                        // check if the provider id matches "facebook.com"
                        if(profile.getProviderId().equals("facebook.com")) {
                            facebookUserId = profile.getUid();
                        }
                    }
                    // construct the URL to the profile picture, with a custom height
                    // alternatively, use '?type=small|medium|large' instead of ?height=
                    final String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large&width=1080";

                    final Map<String, Object> providerData = new HashMap<>();

                    mUserAccount.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){

                                providerData.put("id", uid);
                                providerData.put("name", name);
                                providerData.put("email", email);
                                providerData.put("image", photoUrl);
                                providerData.put("provider", "facebook");
                                mUserAccount.child(uid).updateChildren(providerData);

                                // Go to CreateProfile
                                Intent intent = new Intent(getApplicationContext(), CreateProfile.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                providerData.put("provider", "facebook");
                                mUserAccount.child(uid).updateChildren(providerData);

                                // Go to MainActivity
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("user_id",uid);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                mProgress.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mcallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            authProgress.dismiss();

            mProgress.setMessage("Signing In");
            mProgress.setCancelable(false);
            mProgress.show();

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(), "Google Login Error", Toast.LENGTH_LONG).show();
                mProgress.dismiss();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    LoginFragment1 tab1 = new LoginFragment1();
                    return tab1;
                case 1:
                    LoginFragment2 tab2 = new LoginFragment2();
                    return tab2;
                case 2:
                    LoginFragment3 tab3 = new LoginFragment3();
                    return tab3;
                case 3:
                    LoginFragment4 tab4 = new LoginFragment4();
                    return tab4;
                default:
                    return null;
            }


        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

    }

    public class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {

            view.setTranslationX(view.getWidth() * -position);

            if(position <= -1.0F || position >= 1.0F) {
                view.setAlpha(0.0F);
            } else if( position == 0.0F ) {
                view.setAlpha(1.0F);
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }
}