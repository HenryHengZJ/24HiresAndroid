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

public class HowTalentWorksFrag extends Fragment {

    View rootView;
    private ImageButton mTalentbackBtn;
    private String howitworks;
    private TextView mhowitworkstxt;

    public static HowTalentWorksFrag newInstance(String howitworks) {
        HowTalentWorksFrag result = new HowTalentWorksFrag();
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

        rootView = inflater.inflate(R.layout.howtalentworks_frag, container, false);

        mTalentbackBtn = (ImageButton) rootView.findViewById(R.id.backBtn);
        mhowitworkstxt = (TextView) rootView.findViewById(R.id.howitworkstxt);

        if (howitworks.equals("true")) {
            mTalentbackBtn.setVisibility(View.GONE);
            mhowitworkstxt.setVisibility(View.GONE);
        }

        mTalentbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("here", "gg");
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }
}

