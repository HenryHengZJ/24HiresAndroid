package com.zjheng.jobseed.jobseed;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by zhen on 3/13/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInsIDService";
    private DatabaseReference mUser;
    private FirebaseAuth mAuth;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "New Token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        //sendRegistrationToServer(refreshedToken);
    }

    /*public void sendRegistrationToServer(String refreshedToken){
        mUser =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference current_user_db = mUser.child(mAuth.getCurrentUser().getUid());
        current_user_db.child("notificationTokens").child(refreshedToken).setValue(true);
    }*/
}
