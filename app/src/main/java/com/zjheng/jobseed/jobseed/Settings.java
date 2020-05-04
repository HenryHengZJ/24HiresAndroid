package com.zjheng.jobseed.jobseed;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.android.device.DeviceName;
import com.zjheng.jobseed.jobseed.LoginScene.Login;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhen on 3/29/2017.
 */

public class Settings extends AppCompatActivity {

    private static final String TAG = "sETTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction().replace(R.id.Rlay, new SettingsScreen()).commit();
    }

    public static class SettingsScreen extends PreferenceFragment {

        private FirebaseAuth mAuth;
        private DatabaseReference mUserActivities, mUserAccount;
        private String current_userid, userprovider;
        private Boolean fBandGoogle;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            addPreferencesFromResource(R.xml.settings_screen);
            mAuth = FirebaseAuth.getInstance();
            current_userid = mAuth.getCurrentUser().getUid();
            final String token = FirebaseInstanceId.getInstance().getToken();

            mUserActivities = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities");

            mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

            final DatabaseReference myConnectionsRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities").child(mAuth.getCurrentUser().getUid()).child("Connections");
            // stores the timestamp of my last disconnect (the last time I was seen online)
            final DatabaseReference lastOnlineRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserActivities").child(mAuth.getCurrentUser().getUid()).child("Lastonline");

            Preference mchgpassword = (Preference) findPreference("resetpassword");

            final SwitchPreference mchatnotifications = (SwitchPreference) findPreference("chatnotifications");
            final SwitchPreference mapplicantsnotifications = (SwitchPreference) findPreference("applicantsnotifications");
            final SwitchPreference mshortlistnotifications = (SwitchPreference) findPreference("shortlistnotifications");
            final SwitchPreference mbookingnotifications = (SwitchPreference) findPreference("bookingnotifications");
            final SwitchPreference mhirednotifications = (SwitchPreference) findPreference("hirednotifications");

            Preference msendfeedback = (Preference) findPreference("sendfeedback");

            Preference mreportfraud = (Preference) findPreference("reportfraud");

            Preference mlogout = (Preference) findPreference("logout");

            mUserActivities.child(current_userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("ChatTokens").exists()) {
                        mchatnotifications.setChecked(true);
                    } else {
                        mchatnotifications.setChecked(false);
                    }


                    if (dataSnapshot.child("ApplyTokens").exists()) {
                        mapplicantsnotifications.setChecked(true);
                    } else {
                        mapplicantsnotifications.setChecked(false);
                    }


                    if (dataSnapshot.child("ShortlistTokens").exists()) {
                        mshortlistnotifications.setChecked(true);
                    } else {
                        mshortlistnotifications.setChecked(false);
                    }

                    if (dataSnapshot.child("HireTokens").exists()) {
                        mhirednotifications.setChecked(true);
                    } else {
                        mhirednotifications.setChecked(false);
                    }


                    if (dataSnapshot.child("BookingTokens").exists()) {
                        mbookingnotifications.setChecked(true);
                    } else {
                        mbookingnotifications.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


            if (mchatnotifications != null) {
                mchatnotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference arg0, Object newValue) {
                        boolean isNotificationOn = (Boolean) newValue;
                        if (isNotificationOn) {
                            whenNotificationOn("ChatTokens");
                        } else {
                            whenNotificationNotOn("ChatTokens");
                        }
                        return true;
                    }
                });
            }

            if (mapplicantsnotifications != null) {
                mapplicantsnotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference arg0, Object newValue) {
                        boolean isNotificationOn = (Boolean) newValue;
                        if (isNotificationOn) {
                            whenNotificationOn("ApplyTokens");
                        } else {
                            whenNotificationNotOn("ApplyTokens");
                        }
                        return true;
                    }
                });
            }

            if (mshortlistnotifications != null) {
                mshortlistnotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference arg0, Object newValue) {
                        boolean isNotificationOn = (Boolean) newValue;
                        if (isNotificationOn) {
                            whenNotificationOn("ShortlistTokens");
                        } else {
                            whenNotificationNotOn("ShortlistTokens");
                        }
                        return true;
                    }
                });
            }

            if (mhirednotifications != null) {
                mhirednotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference arg0, Object newValue) {
                        boolean isNotificationOn = (Boolean) newValue;
                        if (isNotificationOn) {
                            whenNotificationOn("HireTokens");
                        } else {
                            whenNotificationNotOn("HireTokens");
                        }
                        return true;
                    }
                });
            }

            if (mbookingnotifications != null) {
                mbookingnotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference arg0, Object newValue) {
                        boolean isNotificationOn = (Boolean) newValue;
                        if (isNotificationOn) {
                            whenNotificationOn("BookingTokens");
                        } else {
                            whenNotificationNotOn("BookingTokens");
                        }
                        return true;
                    }
                });
            }

            mlogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    // when this device disconnects, remove it
                    myConnectionsRef.removeValue();
                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.setValue(ServerValue.TIMESTAMP);

                    mAuth.signOut();

                    LoginManager.getInstance().logOut();

                    Intent intent = new Intent(getActivity(), Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }
            });

            mreportfraud.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    String verName = BuildConfig.VERSION_NAME;
                    int verCode = BuildConfig.VERSION_CODE;

                    String deviceName = DeviceName.getDeviceName();

                    Log.d(TAG, "verCode " + verCode);
                    Log.d(TAG, "verName " + verName);

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "24hiresmy@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n*Please do not delete the following information:\nUserID: " + mAuth.getCurrentUser().getUid() + " App version: " + verName
                            + " Device: " + deviceName);
                    startActivity(Intent.createChooser(emailIntent, "Report Fraud"));

                    return true;
                }
            });

            msendfeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    String verName = BuildConfig.VERSION_NAME;
                    int verCode = BuildConfig.VERSION_CODE;

                    String deviceName = DeviceName.getDeviceName();

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "24hiresmy@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n*Please do not delete the following information:\nUserID: " + mAuth.getCurrentUser().getUid() + " App version: " + verName
                            + " Device: " + deviceName);
                    startActivity(Intent.createChooser(emailIntent, "Send Feedback"));

                    return true;
                }
            });


            mchgpassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    FirebaseUser muser = FirebaseAuth.getInstance().getCurrentUser();
                    // find the Facebook profile and get the user's id
                    for(UserInfo profile : muser.getProviderData()) {
                        // check if the provider id matches "facebook.com"
                        if (profile.getProviderId().equals("facebook.com") || profile.getProviderId().equals("google.com")) {
                            Log.d(TAG, "getProviderId" + profile.getProviderId());
                            fBandGoogle = true;
                        }
                        else {
                            fBandGoogle = false;
                        }
                    }

                    if (fBandGoogle) {
                        Log.d(TAG, "isFBandGoogle");

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.passwordreset_dialog);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.gravity = Gravity.CENTER;

                        dialog.getWindow().setAttributes(lp);

                        TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
                        ImageView mfblogo = (ImageView) dialog.findViewById(R.id.fblogo);
                        mfblogo.setVisibility(VISIBLE);
                        ImageView memaillogo = (ImageView) dialog.findViewById(R.id.emaillogo);
                        memaillogo.setVisibility(GONE);
                        ImageView mgooglelogo = (ImageView) dialog.findViewById(R.id.googlelogo);
                        mgooglelogo.setVisibility(VISIBLE);
                        Button okbtn = (Button) dialog.findViewById(R.id.hireBtn);
                        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
                        cancelbtn.setVisibility(GONE);

                        okbtn.setText("CLOSE");
                        okbtn.setTextColor(Color.parseColor("#0e52a5"));
                        mdialogtxt.setText("Facebook and Google login users don't have password");

                        dialog.show();

                        okbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialog.dismiss();
                            }
                        });
                    }
                    else {
                        Log.d(TAG, "NOTfBandGoogle");

                        mUserAccount.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("email")) {
                                    final String email = dataSnapshot.child("email").getValue().toString();
                                    if (email != null) {

                                        final Dialog dialog = new Dialog(getActivity());
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.passwordreset_dialog);

                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                        lp.copyFrom(dialog.getWindow().getAttributes());
                                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                        lp.gravity = Gravity.CENTER;

                                        dialog.getWindow().setAttributes(lp);

                                        TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
                                        ImageView mfblogo = (ImageView) dialog.findViewById(R.id.fblogo);
                                        mfblogo.setVisibility(GONE);
                                        ImageView memaillogo = (ImageView) dialog.findViewById(R.id.emaillogo);
                                        memaillogo.setVisibility(VISIBLE);
                                        ImageView mgooglelogo = (ImageView) dialog.findViewById(R.id.googlelogo);
                                        mgooglelogo.setVisibility(GONE);
                                        Button okbtn = (Button) dialog.findViewById(R.id.hireBtn);
                                        Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);

                                        okbtn.setText("OK");
                                        okbtn.setTextColor(Color.parseColor("#0e52a5"));
                                        mdialogtxt.setText("An email will be sent to your registered email address to reset your password ");
                                        cancelbtn.setText("CANCEL");

                                        dialog.show();

                                        okbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                FirebaseAuth auth = FirebaseAuth.getInstance();

                                                auth.sendPasswordResetEmail(email)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getActivity(), "Email sent!. ", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

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
                                } else {
                                    Toast.makeText(getActivity(), "Email not found. ", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    return true;
                }
            });
        }

        private void whenNotificationOn(final String tokenNames) {
            String token = FirebaseInstanceId.getInstance().getToken();
            current_userid = mAuth.getCurrentUser().getUid();
            DatabaseReference current_user_db = mUserActivities.child(current_userid);
            current_user_db.child(tokenNames).child(token).setValue(true);
        }

        private void whenNotificationNotOn(final String tokenNames) {
            current_userid = mAuth.getCurrentUser().getUid();
            mUserActivities.child(current_userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(tokenNames).exists()) {
                        mUserActivities.child(current_userid).child(tokenNames).removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
