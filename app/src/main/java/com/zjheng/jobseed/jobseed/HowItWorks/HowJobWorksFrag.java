package com.zjheng.jobseed.jobseed.HowItWorks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zjheng.jobseed.jobseed.R;


/**
 * Created by zhen on 5/5/2017.
 */

public class HowJobWorksFrag extends Fragment {

    View rootView;
    private ImageButton mJobbackBtn;
    private String howitworks;
    private TextView mhowitworkstxt;

    public static HowJobWorksFrag newInstance(String howitworks) {
        HowJobWorksFrag result = new HowJobWorksFrag();
        Bundle bundle = new Bundle();
        bundle.putString("howitworks", howitworks);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        howitworks = bundle.getString("howitworks");
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.howjobworks_frag, container, false);

        mJobbackBtn = (ImageButton) rootView.findViewById(R.id.backBtn);
        mhowitworkstxt = (TextView) rootView.findViewById(R.id.howitworkstxt);

        if (howitworks.equals("true")) {
            mJobbackBtn.setVisibility(View.GONE);
            mhowitworkstxt.setVisibility(View.GONE);
        }

        mJobbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("here", "gg");
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }
}

