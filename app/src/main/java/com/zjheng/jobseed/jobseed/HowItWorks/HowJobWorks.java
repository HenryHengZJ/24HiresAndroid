package com.zjheng.jobseed.jobseed.HowItWorks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zjheng.jobseed.jobseed.R;

public class HowJobWorks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_job_works);

        getSupportFragmentManager().findFragmentById(R.id.frame_container);
        //above part is to determine which fragment is in your frame_container
        setFragment(new HowJobWorksFrag());


    }

    protected void setFragment(Fragment fragment) {

        Bundle bundle = new Bundle();
        bundle.putString("howitworks", "false");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }


}
