package com.zjheng.jobseed.jobseed;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import me.leolin.shortcutbadger.ShortcutBadger;

import static android.R.attr.data;

/**
 * Created by zhen on 3/13/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private DatabaseReference mUserAllNotification;
    private FirebaseAuth mAuth;
    private String title, body, receiveruid;
    private String jobpost, jobcity;
    private long num;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //Check if message contains data
        if(remoteMessage.getData().size()>0){
            num = remoteMessage.getData().size();
            Log.d(TAG, "Message data: " + remoteMessage.getData().get("receiverid"));
            receiveruid = remoteMessage.getData().get("receiverid");
            jobpost = remoteMessage.getData().get("jobpost");
            jobcity = remoteMessage.getData().get("jobcity");
        }

        //Check if message contains notification
        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "Message body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "Message body: " + remoteMessage.getNotification().getTitle());
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        if(jobpost!= null && jobcity!= null ){
            sendJobNotification(body, title, jobpost, jobcity);
        }
        else {
            sendNotification(body, title, receiveruid, num);
        }

    }

    private void sendNotification(String body, String title, String receiveruid, Long num){

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("post_uid",receiveruid);
        Log.d(TAG, "receiveruid: " + receiveruid);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.appsicon6)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendJobNotification(String body, String title, String jobpost,String jobcity){

        Intent jobdetailintent = new Intent(this, JobDetail.class);
        jobdetailintent.putExtra("post_id", jobpost);
        jobdetailintent.putExtra("city_id", jobcity);
        jobdetailintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, jobdetailintent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.appsicon6)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
