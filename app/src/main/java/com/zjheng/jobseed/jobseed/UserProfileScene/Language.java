package com.zjheng.jobseed.jobseed.UserProfileScene;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class Language extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfo;

    private EditText mlanguagetxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        mAuth = FirebaseAuth.getInstance();

        mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserInfo");

        mlanguagetxt = (EditText)findViewById(R.id.languagetxt);
        mlanguagetxt.requestFocus();

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Language");
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mUserInfo.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Language")){

                    String Language = dataSnapshot.child("Language").getValue().toString();

                    mlanguagetxt.setText(Language);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startPosting() {

        final String languagetxt = mlanguagetxt.getText().toString().trim();

        final DatabaseReference newLanguage = mUserInfo.child(mAuth.getCurrentUser().getUid()).child("Language");

        if(TextUtils.isEmpty(languagetxt)){
            newLanguage.removeValue();
            UserProfileInfo.mlanguagetxt1.setText("");
            UserProfileInfo.mlanguagetxt1.setHint("Add your college / university");
        }

        if(!TextUtils.isEmpty(languagetxt)){
            newLanguage.setValue(languagetxt);
            UserProfileInfo.mlanguagetxt1.setText(languagetxt);
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
                onBackPressed();
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
