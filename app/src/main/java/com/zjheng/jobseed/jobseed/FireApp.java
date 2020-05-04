package com.zjheng.jobseed.jobseed;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by zhen on 2/11/2017.
 */

public class FireApp extends Application{


    private static final String TAG = "FireApp";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate(){
        super.onCreate();

        if(!FirebaseApp.getApps(this).isEmpty()){
            Log.d(TAG, "fireapp ");
            //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        FirebaseApp.initializeApp(this);
        AppEventsLogger.activateApp(this);
        registerActivityLifecycleCallbacks(new FirebaseDatabaseConnectionHandler());

    }
}
