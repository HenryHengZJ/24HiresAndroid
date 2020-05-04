package com.zjheng.jobseed.jobseed.UserProfileScene;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.R;

import static com.zjheng.jobseed.jobseed.R.id.phonetxt;

public class Contact extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfo;

    private EditText memailtxt,mphonetxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mAuth = FirebaseAuth.getInstance();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Contacts");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        memailtxt = (EditText)findViewById(R.id.emailtxt);
        memailtxt.requestFocus();
        mphonetxt = (EditText)findViewById(phonetxt);

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Email")){

                    String email = dataSnapshot.child("Email").getValue().toString();

                    memailtxt.setText(email);
                }
                if(dataSnapshot.hasChild("Phone")){

                    String phone = dataSnapshot.child("Phone").getValue().toString();

                    mphonetxt.setText(phone);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startPosting() {

        final String emailtxt = memailtxt.getText().toString().trim();
        final String phonetxt = mphonetxt.getText().toString().trim();

        final DatabaseReference newEmail = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Email");
        final DatabaseReference newPhone = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Phone");

        if(TextUtils.isEmpty(emailtxt) && !TextUtils.isEmpty(phonetxt)){
            newEmail.removeValue();
            newPhone.setValue(phonetxt);
            UserProfileInfo.mphonecontact.setText(phonetxt);
            UserProfileInfo.mcontacttxt1.setText("");
            UserProfileInfo.mcontacttxt1.setHint("Add your email");

            onBackPressed();
        }

        else if(TextUtils.isEmpty(phonetxt) && !TextUtils.isEmpty(emailtxt)){

            if (!emailtxt.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
               memailtxt.requestFocus();
               memailtxt.setError("Invalid Email Address");
            }
            else{
                newPhone.removeValue();
                newEmail.setValue(emailtxt);
                UserProfileInfo.mcontacttxt1.setText(emailtxt);
                UserProfileInfo.mphonecontact.setText("");
                UserProfileInfo.mphonecontact.setHint("Add your phone contact");

                onBackPressed();
            }
        }

        else if(!TextUtils.isEmpty(phonetxt) && !TextUtils.isEmpty(emailtxt)){

            if (!emailtxt.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                memailtxt.requestFocus();
                memailtxt.setError("Invalid Email Address");
            }
            else {

                newEmail.setValue(emailtxt);
                newPhone.setValue(phonetxt);
                UserProfileInfo.mcontacttxt1.setText(emailtxt);
                UserProfileInfo.mphonecontact.setText(phonetxt);

                onBackPressed();
            }
        }

        else if(TextUtils.isEmpty(phonetxt) && TextUtils.isEmpty(emailtxt)){
            newEmail.removeValue();
            newPhone.removeValue();

            UserProfileInfo.mphonecontact.setText("");
            UserProfileInfo.mphonecontact.setHint("Add your phone contact");
            UserProfileInfo.mcontacttxt1.setText("");
            UserProfileInfo.mcontacttxt1.setHint("Add your email");

            onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem itemSave = menu.findItem(R.id.menuSave);

        MenuItem itemSettings = menu.findItem(R.id.menuSettings);
        itemSettings.setVisible(false);

        MenuItem itemSearch = menu.findItem(R.id.menuSearch);
        itemSearch.setVisible(false);

        MenuItem item = menu.findItem(R.id.menuSearch2);
        item.setVisible(false);

        MenuItem itemPublish = menu.findItem(R.id.menuPublish);
        itemPublish.setVisible(false);

        itemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startPosting();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
