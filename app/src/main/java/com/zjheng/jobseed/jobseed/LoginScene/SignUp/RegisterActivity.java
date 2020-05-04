package com.zjheng.jobseed.jobseed.LoginScene.SignUp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zjheng.jobseed.jobseed.LoginScene.EmailLogin.LoginwithEmail;
import com.zjheng.jobseed.jobseed.LoginScene.EmailLogin.ResetPassword;
import com.zjheng.jobseed.jobseed.LoginScene.Login;
import com.zjheng.jobseed.jobseed.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameField, mEmailField, mPasswordField;
    private Button mRegisterBtn;
    private ImageButton maboutaddBtn;
    private CircleImageView mprofileBtn;
    private ImageButton mbackBtn;

    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_REQUEST_CODE = 4;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount;

    private Uri mResultUri= null;

    private static final String TAG = "RegisterActivity";
    private ProgressDialog mProgress;

    private String mCurrentPhotoPath;
    private Bitmap mImageBitmap;

    private boolean mImgClick = false;

    // variable to track event time
    private long mLastClickTime = 0;

    private String uid;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://jobseed-2cb76.firebaseio.com").child("UserAccount");

        mNameField = (EditText)findViewById(R.id.nameField1);
        mEmailField = (EditText)findViewById(R.id.emailField1);
        mPasswordField = (EditText)findViewById(R.id.passwordField1);
        maboutaddBtn = (ImageButton) findViewById(R.id.aboutaddBtn);
        mbackBtn = (ImageButton) findViewById(R.id.backBtn);

        mprofileBtn =(CircleImageView) findViewById(R.id.profile_image);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startRegister();
            }
        });

        maboutaddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAction();
            }
        });

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void showDialog(){
        final String email = mEmailField.getText().toString().trim();

        if(!TextUtils.isEmpty(email)){

            final Dialog dialog = new Dialog(RegisterActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.passwordreset_dialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);
            dialog.setCancelable(false);

            TextView mdialogtxt = (TextView) dialog.findViewById(R.id.dialogtxt);
            ImageView mfblogo = (ImageView) dialog.findViewById(R.id.fblogo);
            mfblogo.setVisibility(GONE);
            ImageView memaillogo = (ImageView) dialog.findViewById(R.id.emaillogo);
            memaillogo.setVisibility(VISIBLE);
            ImageView mgooglelogo = (ImageView) dialog.findViewById(R.id.googlelogo);
            mgooglelogo.setVisibility(GONE);
            Button okbtn = (Button) dialog.findViewById(R.id.hireBtn);
            Button cancelbtn = (Button) dialog.findViewById(R.id.cancelBtn);
            cancelbtn.setVisibility(GONE);

            okbtn.setText("OK");
            okbtn.setTextColor(Color.parseColor("#0e52a5"));
            mdialogtxt.setText("An email has been sent to your registered email address to verify your account ");

            dialog.show();

            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();

                    onBackPressed();
                }
            });
        }
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            showDialog();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do
                            Toast.makeText(RegisterActivity.this, "Please Try Again", Toast.LENGTH_LONG).show();
                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }

    private void startRegister(){

        final String name = mNameField.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mProgress.setMessage("Signing Up");
            mProgress.setCancelable(false);
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        uid = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mUserAccount.child(uid);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("email").setValue(email);
                        current_user_db.child("id").setValue(uid);
                        current_user_db.child("newuser").setValue("true");

                        if (!mImgClick){
                            //set default pic
                            current_user_db.child("image").setValue("default").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Go to LoginActivity for transferring to MainActivity
                                    mProgress.dismiss();
                                    sendVerificationEmail();
                                   // Intent intent = new Intent(getApplicationContext(), Login.class);
                                   // startActivity(intent);
                                   // finish();
                                }
                            });
                        }
                        else{
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://jobseed-2cb76.appspot.com");
                            StorageReference filepath = storageRef.child("ProfilePhotos").child(mResultUri.getLastPathSegment());
                            filepath.putFile(mResultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    DatabaseReference current_user_db = mUserAccount.child(uid);
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    current_user_db.child("image").setValue(downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // Go to LoginActivity for transferring to MainActivity
                                            mProgress.dismiss();
                                            sendVerificationEmail();
                                           // Intent intent = new Intent(getApplicationContext(), Login.class);
                                          //  startActivity(intent);
                                          //  finish();
                                        }
                                    });
                                }
                            });
                        }
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

                    else{
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Email already exists, please create a new one", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        else if (TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this, "Please Fill in Display Name", Toast.LENGTH_LONG).show();
            mNameField.requestFocus();
        }
        else if (TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Please Fill in Email", Toast.LENGTH_LONG).show();
            mEmailField.requestFocus();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "Please Fill in Password", Toast.LENGTH_LONG).show();
            mPasswordField.requestFocus();
        }
    }

    private void showAction() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addphoto_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        RelativeLayout mcameraActionbtn = (RelativeLayout) dialog.findViewById(R.id.cameraAction);

        RelativeLayout mgalleryActionbtn = (RelativeLayout) dialog.findViewById(R.id.galleryAction);

        dialog.show();

        mcameraActionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i(TAG, "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }
                }

                dialog.dismiss();
            }
        });

        mgalleryActionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/* ");
                startActivityForResult(galleryIntent,GALLERY_INTENT );
                dialog.dismiss();
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri imageuri = data.getData();

            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Uri imageuri = Uri.parse(mCurrentPhotoPath);

                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));

                CropImage.activity(imageuri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
                        .start(this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImgClick = true;
                mResultUri = result.getUri();
                mprofileBtn.setImageURI(mResultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();

            }
            else if (resultCode == RESULT_CANCELED){
                mImgClick= false;
                mResultUri = null;
            }
            else{
                mImgClick= false;
                mResultUri = null;
            }
        }
    }
}
