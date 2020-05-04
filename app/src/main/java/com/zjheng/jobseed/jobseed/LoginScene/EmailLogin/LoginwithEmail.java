package com.zjheng.jobseed.jobseed.LoginScene.EmailLogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zjheng.jobseed.jobseed.CreateProfile;
import com.zjheng.jobseed.jobseed.LoginScene.Login;
import com.zjheng.jobseed.jobseed.MainActivity;
import com.zjheng.jobseed.jobseed.R;

public class LoginwithEmail extends AppCompatActivity {

    private EditText mEmailField, mPasswordField;
    private Button memailsigninBtn;
    private ImageButton mbackBtn;
    private DatabaseReference mUserAccount;
    private FirebaseAuth mAuth;
    private TextView mforgotpwtxt;

    // variable to track event time
    private long mLastClickTime = 0;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginwith_email);
        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);

        memailsigninBtn = (Button) findViewById(R.id.emailoginBtn);
        mbackBtn = (ImageButton) findViewById(R.id.backBtn);
        mforgotpwtxt = (TextView) findViewById(R.id.forgotpwtxt);
        mforgotpwtxt.setText(R.string.forgotpw);

        memailsigninBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                checkLogin();
            }
        });

        mforgotpwtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent resetintent = new Intent(LoginwithEmail.this, ResetPassword.class);
                startActivity(resetintent);
            }
        });

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


    private void checkLogin(){
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password)) {
            mProgress.setMessage("Signing In");
            mProgress.setCancelable(false);
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkUserExist();
                        mProgress.dismiss();
                    }
                    else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                        mProgress.dismiss();
                        mEmailField.requestFocus();
                        mEmailField.setError("Invalid Email Address");
                    }
                    else if (password.length() < 6) {
                        mProgress.dismiss();
                        mPasswordField.requestFocus();
                        mPasswordField.setError("Password should be more than 6 characters long");
                    }
                    else {
                        Toast.makeText(LoginwithEmail.this, "Login Failed", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }
                }

            });
        }
        else if(TextUtils.isEmpty(email))  {

            Toast.makeText(LoginwithEmail.this, "Please Fill in Email", Toast.LENGTH_LONG).show();
            mEmailField.requestFocus();
        }

        else if(TextUtils.isEmpty(password))  {

            Toast.makeText(LoginwithEmail.this, "Please Fill in Password", Toast.LENGTH_LONG).show();
            mPasswordField.requestFocus();
        }
    }

    private void checkUserExist(){

        if(mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mUserAccount.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        checkIfEmailVerified(user_id);

                    } else {
                        Toast.makeText(LoginwithEmail.this, "User doesn't exists", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkIfEmailVerified(final String user_id)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            mUserAccount.child(user_id).child("newuser").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){

                        // User is not new
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                        finish();
                    }
                    else {

                        // User is new, Go to CreateProfile
                        Intent intent = new Intent(getApplicationContext(), CreateProfile.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Toast.makeText(LoginwithEmail.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(LoginwithEmail.this, Login.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(LoginwithEmail.this, "Please verify your account from your registered email address", Toast.LENGTH_SHORT).show();

            //restart this activity

        }
    }

}
