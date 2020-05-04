package com.zjheng.jobseed.jobseed.LoginScene.EmailLogin;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.zjheng.jobseed.jobseed.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhen on 7/2/2017.
 */

public class ResetPassword extends AppCompatActivity {

    private EditText  mEmailField;
    private Button mResetBtn;
    private ImageButton mbackBtn;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        mEmailField = (EditText)findViewById(R.id.emailField);
        mResetBtn = (Button) findViewById(R.id.resetBtn);
        mbackBtn = (ImageButton) findViewById(R.id.backBtn);

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startReset();
            }
        });

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void startReset(){
        final String email = mEmailField.getText().toString().trim();

        if(!TextUtils.isEmpty(email)){

            final Dialog dialog = new Dialog(ResetPassword.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.passwordreset_dialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
            ImageView mfblogo = (ImageView) dialog.findViewById(R.id.fblogo);
            mfblogo.setVisibility(GONE);
            ImageView memaillogo = (ImageView) dialog.findViewById(R.id.emaillogo);
            memaillogo.setVisibility(VISIBLE);
            ImageView mgooglelogo = (ImageView) dialog.findViewById(R.id.googlelogo);
            mgooglelogo.setVisibility(GONE);
            Button okbtn = (Button) dialog.findViewById(R.id.hireBtn);
            Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);

            okbtn.setText("OK");
            okbtn.setTextColor(Color.parseColor("#0e52a5"));
            mdialogtxt.setText("An email will be sent to your registered email address to reset your password ");
            cancelbtn.setText("CANCEL");

            dialog.show();

            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPassword.this, "Email sent!. ", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(ResetPassword.this, "Email not found ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    dialog.dismiss();
                }
            });

            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
    }
}
