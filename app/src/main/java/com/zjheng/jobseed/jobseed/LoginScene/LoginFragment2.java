package com.zjheng.jobseed.jobseed.LoginScene;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zjheng.jobseed.jobseed.R;

/**
 * Created by zhen on 5/5/2017.
 */

public class LoginFragment2 extends Fragment {

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.loginfragment2, container, false);
        return rootView;
    }
}

