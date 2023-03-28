package com.example.travel_blog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    // Object for accessing Database
    FirebaseFirestore database;
    private BlogPreferences mausmi;
    private EditText mobilenumber;
    private Button SendOtpButton;
    private ProgressBar progressBar;
    private Button SignUpBtn;

    private ImageButton GoogleLogInBtn,FacebookLogInBtn;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mausmi = new BlogPreferences(this);

        mAuth = FirebaseAuth.getInstance();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);


        if(mausmi.isLoggedIn()){
            Log.v("Login State", mausmi.getUserName());
            startMainActivity();
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        SendOtpButton = findViewById(R.id.button2);
        SignUpBtn = findViewById(R.id.button);
        mobilenumber = findViewById(R.id.editTextTextPersonName2);
        progressBar = findViewById(R.id.progressBar2);
        TextView or = findViewById(R.id.textView);
        GoogleLogInBtn = findViewById(R.id.imageButton2);
        FacebookLogInBtn = findViewById(R.id.imageButton3);

        GoogleLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        SendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.onLoginClicked();
            }
        });
        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.onSignUpClicked();
            }
        });
    }

    // Creating OnSignUpClicked Method:
    private void onSignUpClicked(){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
    // Validating the email address and password
    private void onLoginClicked(){
        String number = mobilenumber.getText().toString();
        if (number.isEmpty()) {
            mobilenumber.setError("Mobile Number must not be empty");
        } else if (number.length() != 10){
            showErrorDialog();
        } else {
            performLogin();
        }
    }

    // Defining error dialog
    // .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
    private void showErrorDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage("Invalid Mobile Number")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Defining loading/progress function
    private void performLogin(){
        mobilenumber.setEnabled(false);
        SendOtpButton.setVisibility(View.INVISIBLE);
        SignUpBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        checkUser();
        /*
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Logic for sending otp
            sendOpt();
        }, 2000);
         */
    }

    private void checkUser() {
        // Creating Database instance
        database = FirebaseFirestore.getInstance();
        CollectionReference users = database.collection("users");
        Query checkUser = users.whereEqualTo("mobile", mobilenumber.getText().toString().trim());
        checkUser
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Invalid Mobile Number")
                                        .setMessage("Please Sign Up :)")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            dialog.dismiss();
                                            mobilenumber.setEnabled(true);
                                            progressBar.setVisibility(View.GONE);
                                            SendOtpButton.setVisibility(View.VISIBLE);
                                            SignUpBtn.setVisibility(View.VISIBLE);
                                        })
                                        .show();
                            }
                            else{
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    Users user = document.toObject(Users.class);
                                    mausmi.setUserName(user.name);
                                    mausmi.setUserMob(user.mobile);
                                    mausmi.setUserEmail(user.email);
                                }
                                sendOpt();
                            }
                        } else {

                        }
                    }
                });
    }

    private void sendOpt() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mobilenumber.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                SendOtpButton.setVisibility(View.VISIBLE);
                SignUpBtn.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressBar.setVisibility(View.GONE);
                SendOtpButton.setVisibility(View.VISIBLE);
                SignUpBtn.setVisibility(View.VISIBLE);
                Intent intent = new Intent(LoginActivity.this, OTPVerification.class);
                intent.putExtra("phone", mobilenumber.getText().toString().trim());
                intent.putExtra("verificationId", verificationId);
                startActivity(intent);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + mobilenumber.getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount user = GoogleSignIn.getLastSignedInAccount(this);
                if(user != null){
                    String personName = user.getDisplayName();
                    String email = user.getEmail();
                    Log.v("personName" , personName+" "+email);
                }
                startMainActivity();
            }
            catch (ApiException e){
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}