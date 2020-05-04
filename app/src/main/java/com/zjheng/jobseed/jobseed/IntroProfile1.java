package com.zjheng.jobseed.jobseed;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zjheng.jobseed.jobseed.OtherUserScene.OtherUser;
import com.zjheng.jobseed.jobseed.UserProfileScene.EditProfile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by zhen on 4/8/2018.
 */

public class IntroProfile1 extends AppCompatActivity {

    private CardView mnextCardView;
    private Button mskipBtn;
    private Spinner mgenderspinner;
    private EditText magetxt;
    private Calendar myCalendar;
    private String birthdates;
    private DatabaseReference mUserInfo;
    private FirebaseAuth mAuth;
    private ImageButton mcleardatesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introprofile1);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Step 1 of 4");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mnextCardView = findViewById(R.id.nextCardView);

        mskipBtn = findViewById(R.id.skipBtn);

        mgenderspinner = findViewById(R.id.genderspinner);

        magetxt = findViewById(R.id.agetxt);

        mcleardatesBtn = findViewById(R.id.cleardatesBtn);

        String[] items = new String[]{"", "Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(IntroProfile1.this, android.R.layout.simple_spinner_dropdown_item, items);
        mgenderspinner.setAdapter(adapter);

        mcleardatesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magetxt.setText("");
                birthdates = "";
            }
        });


        myCalendar = Calendar.getInstance();
        final String myFormat = "dd MMMM yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                magetxt.setText(sdf.format(myCalendar.getTime()));
                birthdates = sdf.format(myCalendar.getTime());
            }

        };


        mskipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroProfile1.this, IntroProfile2.class);
                startActivity(intent);
            }
        });

        magetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog birthdatePickerDialog = new DatePickerDialog(IntroProfile1.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                birthdatePickerDialog.show();
            }
        });

        mnextCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String gender_val = mgenderspinner.getSelectedItem().toString().trim();
                final String age_val = magetxt.getText().toString().trim();

                if (!TextUtils.isEmpty(gender_val)) {
                    mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Gender").setValue(gender_val);
                }

                if (!TextUtils.isEmpty(age_val)) {
                    mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Age").setValue(birthdates);
                }

                if (TextUtils.isEmpty(age_val) && TextUtils.isEmpty(gender_val)) {
                    Toast.makeText(IntroProfile1.this, "Gender and Birth Dates are empty. Press SKIP above to skip this step", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(IntroProfile1.this, IntroProfile2.class);
                    startActivity(intent);
                }

            }
        });
    }

    public int getAge(int DOByear, int DOBmonth, int DOBday) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOByear;

        if(DOBmonth > currentMonth){
            --age;
        }
        else if(DOBmonth == currentMonth){
            if(DOBday > todayDay){
                --age;
            }
        }
        return age;
    }

}
