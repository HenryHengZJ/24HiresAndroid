package com.zjheng.jobseed.jobseed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.zjheng.jobseed.jobseed.R.id.workcompany1;
import static com.zjheng.jobseed.jobseed.R.id.workcompany2;
import static com.zjheng.jobseed.jobseed.R.id.worktitle2;

/**
 * Created by zhen on 4/8/2018.
 */

public class IntroProfile4 extends AppCompatActivity {

    private CardView mnextCardView;
    private Button mskipBtn;

    private CardView mcardview1, mcardview2, mcardview3, mcardview4,mcardview5, maddnewcardview;
    private EditText mworktitle1,mworktitle2, mworktitle3, mworktitle4, mworktitle5;
    private EditText mworkcompany1,mworkcompany2, mworkcompany3, mworkcompany4, mworkcompany5;
    private TextView mtxtremove1,mtxtremove2,mtxtremove3, mtxtremove4, mtxtremove5;
    private TextView mworktime1, mworktime2, mworktime3, mworktime4, mworktime5;
    private ScrollView mscrollview;
    private SeekBar mseekBar1, mseekBar2, mseekBar3, mseekBar4, mseekBar5;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introprofile4);

        mAuth = FirebaseAuth.getInstance();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Step 4 of 4");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mnextCardView = findViewById(R.id.nextCardView);

        mskipBtn = findViewById(R.id.skipBtn);

        mcardview1 = (CardView) findViewById(R.id.cardview1);
        mcardview2 = (CardView) findViewById(R.id.cardview2);
        mcardview3 = (CardView) findViewById(R.id.cardview3);
        mcardview4 = (CardView) findViewById(R.id.cardview4);
        mcardview5 = (CardView) findViewById(R.id.cardview5);
        maddnewcardview = (CardView) findViewById(R.id.addnewcardview);

        mworktitle1 = (EditText)findViewById(R.id.worktitle1);
        mworktitle2 = (EditText)findViewById(worktitle2);
        mworktitle3 = (EditText)findViewById(R.id.worktitle3);
        mworktitle4 = (EditText)findViewById(R.id.worktitle4);
        mworktitle5 = (EditText)findViewById(R.id.worktitle5);

        mworkcompany1 = (EditText)findViewById(workcompany1);
        mworkcompany2 = (EditText)findViewById(workcompany2);
        mworkcompany3 = (EditText)findViewById(R.id.workcompany3);
        mworkcompany4 = (EditText)findViewById(R.id.workcompany4);
        mworkcompany5 = (EditText)findViewById(R.id.workcompany5);

        mtxtremove1 = (TextView) findViewById(R.id.txtremove1);
        mtxtremove2 = (TextView) findViewById(R.id.txtremove2);
        mtxtremove3 = (TextView) findViewById(R.id.txtremove3);
        mtxtremove4 = (TextView) findViewById(R.id.txtremove4);
        mtxtremove5 = (TextView) findViewById(R.id.txtremove5);

        mseekBar1 = (SeekBar)findViewById(R.id.seekBar1);
        mseekBar1.setMax(7);
        mworktime1 = (TextView)findViewById(R.id.worktime1);

        mseekBar2 = (SeekBar)findViewById(R.id.seekBar2);
        mseekBar2.setMax(7);
        mworktime2 = (TextView)findViewById(R.id.worktime2);

        mseekBar3 = (SeekBar)findViewById(R.id.seekBar3);
        mseekBar3.setMax(7);
        mworktime3 = (TextView)findViewById(R.id.worktime3);

        mseekBar4 = (SeekBar)findViewById(R.id.seekBar4);
        mseekBar4.setMax(7);
        mworktime4 = (TextView)findViewById(R.id.worktime4);

        mseekBar5 = (SeekBar)findViewById(R.id.seekBar5);
        mseekBar5.setMax(7);
        mworktime5 = (TextView)findViewById(R.id.worktime5);

        mscrollview = ((ScrollView) findViewById(R.id.mscrollview));

        mskipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                String uid = mAuth.getCurrentUser().getUid();
                intent.putExtra("user_id", uid);
                intent.putExtra("newuser", "true");
                startActivity(intent);
                finish();
            }
        });

        mnextCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();

            }
        });

        mseekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setseekbartext(progress,mworktime1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mseekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setseekbartext(progress,mworktime2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mseekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setseekbartext(progress,mworktime3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mseekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setseekbartext(progress,mworktime4);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mseekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setseekbartext(progress,mworktime5);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        maddnewcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If only WorkExp1
                if ( mcardview1.getVisibility() == VISIBLE && mcardview2.getVisibility() == GONE && mcardview3.getVisibility() == GONE
                        && mcardview4.getVisibility() == GONE && mcardview5.getVisibility() == GONE) {
                    mtxtremove1.setVisibility(GONE);
                    mtxtremove2.setVisibility(VISIBLE);
                    mtxtremove3.setVisibility(GONE);
                    mtxtremove4.setVisibility(GONE);
                    mtxtremove5.setVisibility(GONE);

                    mcardview2.setVisibility(VISIBLE);
                    mworktitle2.requestFocus();
                }
                //If WorkExp2 & WorkExp1
                else if (mcardview1.getVisibility() == VISIBLE && mcardview2.getVisibility() == VISIBLE && mcardview3.getVisibility() == GONE
                        && mcardview4.getVisibility() == GONE && mcardview5.getVisibility() == GONE) {
                    mtxtremove1.setVisibility(GONE);
                    mtxtremove2.setVisibility(GONE);
                    mtxtremove3.setVisibility(VISIBLE);
                    mtxtremove4.setVisibility(GONE);
                    mtxtremove5.setVisibility(GONE);

                    mcardview3.setVisibility(VISIBLE);
                    mworktitle3.requestFocus();
                }
                //If WorkExp3 & WorkExp2 & WorkExp1
                else if (mcardview1.getVisibility() == VISIBLE && mcardview2.getVisibility() == VISIBLE && mcardview3.getVisibility() == VISIBLE
                        && mcardview4.getVisibility() == GONE && mcardview5.getVisibility() == GONE) {
                    mtxtremove1.setVisibility(GONE);
                    mtxtremove2.setVisibility(GONE);
                    mtxtremove3.setVisibility(GONE);
                    mtxtremove4.setVisibility(VISIBLE);
                    mtxtremove5.setVisibility(GONE);

                    mcardview4.setVisibility(VISIBLE);
                    mworktitle4.requestFocus();
                }
                //If WorkExp4 & WorkExp3 & WorkExp2 & WorkExp1
                else if (mcardview1.getVisibility() == VISIBLE && mcardview2.getVisibility() == VISIBLE && mcardview3.getVisibility() == VISIBLE
                        && mcardview4.getVisibility() == VISIBLE && mcardview5.getVisibility() == GONE) {
                    mtxtremove1.setVisibility(GONE);
                    mtxtremove2.setVisibility(GONE);
                    mtxtremove3.setVisibility(GONE);
                    mtxtremove4.setVisibility(GONE);
                    mtxtremove5.setVisibility(VISIBLE);

                    mcardview5.setVisibility(VISIBLE);

                    maddnewcardview.setVisibility(GONE);

                    mworktitle5.requestFocus();
                }
                //If No EXP
                else if (mcardview2.getVisibility() == GONE && mcardview3.getVisibility() == GONE && mcardview1.getVisibility() == GONE
                        && mcardview4.getVisibility() == GONE && mcardview5.getVisibility() == GONE){
                    mtxtremove1.setVisibility(VISIBLE);
                    mtxtremove2.setVisibility(GONE);
                    mtxtremove3.setVisibility(GONE);
                    mtxtremove4.setVisibility(GONE);
                    mtxtremove5.setVisibility(GONE);

                    mcardview1.setVisibility(VISIBLE);
                    mworktitle1.setText("");
                    mworkcompany1.setText("");
                    mworktitle1.requestFocus();
                }

                mscrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        mtxtremove1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcardview1.setVisibility(GONE);
                mworktitle1.setText("");
                mworkcompany1.setText("");
                maddnewcardview.setVisibility(VISIBLE);
            }
        });

        mtxtremove2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtxtremove1.setVisibility(VISIBLE);
                mcardview2.setVisibility(GONE);
                mworktitle2.setText("");
                mworkcompany2.setText("");
                maddnewcardview.setVisibility(VISIBLE);
            }
        });

        mtxtremove3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtxtremove1.setVisibility(GONE);
                mtxtremove2.setVisibility(VISIBLE);

                mcardview3.setVisibility(GONE);

                mworktitle3.setText("");
                mworkcompany3.setText("");
                maddnewcardview.setVisibility(VISIBLE);
            }
        });

        mtxtremove4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtxtremove1.setVisibility(GONE);
                mtxtremove2.setVisibility(GONE);
                mtxtremove3.setVisibility(VISIBLE);

                mcardview4.setVisibility(GONE);

                mworktitle4.setText("");
                mworkcompany4.setText("");
                maddnewcardview.setVisibility(VISIBLE);
            }
        });

        mtxtremove5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtxtremove1.setVisibility(GONE);
                mtxtremove2.setVisibility(GONE);
                mtxtremove3.setVisibility(GONE);
                mtxtremove4.setVisibility(VISIBLE);

                mcardview5.setVisibility(GONE);

                mworktitle5.setText("");
                mworkcompany5.setText("");
                maddnewcardview.setVisibility(VISIBLE);
            }
        });

    }

    private void setseekbartext(int progress, TextView worktxtview){
        if(progress==0){worktxtview.setText("Less than a month");}
        else if(progress==1){worktxtview.setText("Less than 3 months");}
        else if(progress==2){worktxtview.setText("Less than 6 months");}
        else if(progress==3){worktxtview.setText("Less than 1 year");}
        else if(progress==4){worktxtview.setText("2 years +");}
        else if(progress==5){worktxtview.setText("5 years +");}
        else if(progress==6){worktxtview.setText("10 years +");}
        else if(progress==7){worktxtview.setText("20 years +");}
    }


    private void startPosting(){

        final String worktitle1 = mworktitle1.getText().toString().trim();
        final String worktitle2 = mworktitle2.getText().toString().trim();
        final String worktitle3 = mworktitle3.getText().toString().trim();
        final String worktitle4 = mworktitle4.getText().toString().trim();
        final String worktitle5 = mworktitle5.getText().toString().trim();

        final String workcompany1 = mworkcompany1.getText().toString().trim();
        final String workcompany2 = mworkcompany2.getText().toString().trim();
        final String workcompany3 = mworkcompany3.getText().toString().trim();
        final String workcompany4 = mworkcompany4.getText().toString().trim();
        final String workcompany5 = mworkcompany5.getText().toString().trim();

        final String worktime1 = mworktime1.getText().toString().trim();
        final String worktime2 = mworktime2.getText().toString().trim();
        final String worktime3 = mworktime3.getText().toString().trim();
        final String worktime4 = mworktime4.getText().toString().trim();
        final String worktime5 = mworktime5.getText().toString().trim();

        final DatabaseReference newInfo1 = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("WorkExp1");
        final DatabaseReference newInfo2 = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("WorkExp2");
        final DatabaseReference newInfo3 = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("WorkExp3");
        final DatabaseReference newInfo4 = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("WorkExp4");
        final DatabaseReference newInfo5 = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("WorkExp5");

        if(mcardview1.getVisibility()==VISIBLE) {
            if (!TextUtils.isEmpty(worktitle1) && !TextUtils.isEmpty(workcompany1)) {
                newInfo1.child("worktitle").setValue(worktitle1);
                newInfo1.child("workcompany").setValue(workcompany1);
                newInfo1.child("worktime").setValue(worktime1);

            } else if (!TextUtils.isEmpty(worktitle1) && TextUtils.isEmpty(workcompany1)) {

                mworkcompany1.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (!TextUtils.isEmpty(workcompany1) && TextUtils.isEmpty(worktitle1)) {

                mworktitle1.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (TextUtils.isEmpty(worktitle1) && TextUtils.isEmpty(workcompany1)) {

                mworkcompany1.setError("Field can't be left empty");
                mworktitle1.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;
            }
        }
        else{
            newInfo1.removeValue();
        }


        if(mcardview2.getVisibility()==VISIBLE) {
            if (!TextUtils.isEmpty(worktitle2) && !TextUtils.isEmpty(workcompany2)) {
                newInfo2.child("worktitle").setValue(worktitle2);
                newInfo2.child("workcompany").setValue(workcompany2);
                newInfo2.child("worktime").setValue(worktime2);

            } else if (!TextUtils.isEmpty(worktitle2) && TextUtils.isEmpty(workcompany2)) {

                mworkcompany2.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (!TextUtils.isEmpty(workcompany2) && TextUtils.isEmpty(worktitle2)) {

                mworktitle2.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (TextUtils.isEmpty(worktitle2) && TextUtils.isEmpty(workcompany2)) {

                mworkcompany2.setError("Field can't be left empty");
                mworktitle2.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;
            }
        }
        else {
            newInfo2.removeValue();
        }


        if(mcardview3.getVisibility()==VISIBLE) {
            if (!TextUtils.isEmpty(worktitle3) && !TextUtils.isEmpty(workcompany3)) {
                newInfo3.child("worktitle").setValue(worktitle3);
                newInfo3.child("workcompany").setValue(workcompany3);
                newInfo3.child("worktime").setValue(worktime3);

            } else if (!TextUtils.isEmpty(worktitle3) && TextUtils.isEmpty(workcompany3)) {
                mworkcompany3.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (!TextUtils.isEmpty(workcompany3) && TextUtils.isEmpty(worktitle3)) {
                mworktitle3.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (TextUtils.isEmpty(worktitle3) && TextUtils.isEmpty(workcompany3)) {
                mworkcompany3.setError("Field can't be left empty");
                mworktitle3.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;
            }
        }
        else{
            newInfo3.removeValue();
        }


        if(mcardview4.getVisibility()==VISIBLE) {
            if (!TextUtils.isEmpty(worktitle4) && !TextUtils.isEmpty(workcompany4)) {
                newInfo4.child("worktitle").setValue(worktitle4);
                newInfo4.child("workcompany").setValue(workcompany4);
                newInfo4.child("worktime").setValue(worktime4);

            } else if (!TextUtils.isEmpty(worktitle4) && TextUtils.isEmpty(workcompany4)) {
                mworkcompany4.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (!TextUtils.isEmpty(workcompany4) && TextUtils.isEmpty(worktitle4)) {
                mworktitle4.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (TextUtils.isEmpty(worktitle4) && TextUtils.isEmpty(workcompany4)) {
                mworkcompany4.setError("Field can't be left empty");
                mworktitle4.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;
            }
        }
        else{
            newInfo4.removeValue();
        }


        if(mcardview5.getVisibility()==VISIBLE) {
            if (!TextUtils.isEmpty(worktitle5) && !TextUtils.isEmpty(workcompany5)) {
                newInfo5.child("worktitle").setValue(worktitle5);
                newInfo5.child("workcompany").setValue(workcompany5);
                newInfo5.child("worktime").setValue(worktime5);

            } else if (!TextUtils.isEmpty(worktitle5) && TextUtils.isEmpty(workcompany5)) {
                mworkcompany5.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (!TextUtils.isEmpty(workcompany5) && TextUtils.isEmpty(worktitle5)) {
                mworktitle5.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;

            } else if (TextUtils.isEmpty(worktitle5) && TextUtils.isEmpty(workcompany5)) {
                mworkcompany5.setError("Field can't be left empty");
                mworktitle5.setError("Field can't be left empty");
                Toast.makeText(IntroProfile4.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
                return;
            }
        }
        else{
            newInfo5.removeValue();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        String uid = mAuth.getCurrentUser().getUid();
        intent.putExtra("user_id", uid);
        intent.putExtra("newuser", "true");
        startActivity(intent);
        finish();

    }
}
