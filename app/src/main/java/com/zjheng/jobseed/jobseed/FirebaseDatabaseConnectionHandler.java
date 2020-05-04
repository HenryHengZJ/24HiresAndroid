package com.zjheng.jobseed.jobseed;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseConnectionHandler implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = FirebaseDatabaseConnectionHandler.class.getSimpleName();

    private int count = 0;
    private final long delayedTimeMillis = 5000; // change this if you want different timeout
    private Handler mHandler = new Handler();

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        count++;
        Log.d(TAG, "onActivityStarted: count=" + count);
        if (count > 0)
            FirebaseDatabase.getInstance().goOnline();
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        count--;
        Log.d(TAG, "onActivityStopped: count=" + count);
        if (count == 0) {

            Log.d(TAG, "onActivityStopped: going offline in 5 seconds..");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // just make sure that in the defined seconds no other activity is brought to front
                    Log.d(TAG, "run: confirming if it is safe to go offline. Activity count: " + count);
                    if (count == 0) {
                        Log.d(TAG, "run: going offline...");
                        FirebaseDatabase.getInstance().goOffline();
                    } else {
                        Log.d(TAG, "run: Not going offline..");
                    }
                }
            }, delayedTimeMillis);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}