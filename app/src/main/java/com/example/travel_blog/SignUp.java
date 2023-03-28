package com.example.travel_blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private BlogPreferences mausmi;

    EditText SignUpName, SignUpEmail, SignUpMob;
    private String name, email, mobileNo;
    Button SignUp, Login;
    FirebaseFirestore database;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SignUpEmail = findViewById(R.id.editTextTextPersonName3);
        SignUpName = findViewById(R.id.editTextTextPersonName);
        SignUpMob = findViewById(R.id.editTextPhone2);
        SignUp = findViewById(R.id.button3);
        Login = findViewById(R.id.button4);
        progressBar = findViewById(R.id.progressBar3);

        mAuth = FirebaseAuth.getInstance();

        mausmi = new BlogPreferences(this);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp.this.OnSignUpClicked();
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp.this.OnLoginClicked();
            }
        });

    }

    private void OnLoginClicked() {
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
    }

    private void OnSignUpClicked(){
        name = SignUpName.getText().toString();
        email = SignUpEmail.getText().toString();
        mobileNo = SignUpMob.getText().toString();
        if(name.isEmpty()){
            SignUpName.setError("Please Enter a name!");
        } else if(email.isEmpty()){
            SignUpEmail.setError("Please Enter a Email");
        } else if(mobileNo.isEmpty()){
            SignUpMob.setError("Please Enter a valid Mobile No.");
        }else if (mobileNo.length() != 10){
            showErrorDialog();
        }else {
            performSignup();
        }
    }
    private void showErrorDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage("Invalid Mobile Number")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void performSignup(){

        SignUpMob.setEnabled(false);
        SignUpEmail.setEnabled(false);
        SignUpName.setEnabled(false);
        Login.setVisibility(View.INVISIBLE);
        SignUp.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        // Now First Lets try to send the OTP
        sendOpt();
    }

    private void sendOpt() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                SignUpMob.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                SignUp.setVisibility(View.VISIBLE);
                Login.setVisibility(View.VISIBLE);
                Toast.makeText(SignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                database = FirebaseFirestore.getInstance();
                progressBar.setVisibility(View.GONE);
                SignUp.setVisibility(View.VISIBLE);
                Login.setVisibility(View.VISIBLE);
                Users user = new Users(name, email, mobileNo);
                mausmi.setUserName(user.name);
                mausmi.setUserMob(user.mobile);
                mausmi.setUserEmail(user.email);

                // Lets Add this user to firestore database of Users:
                // Add a new document with a generated ID
                database.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Response", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Response", "Error adding document", e);
                            }
                        });

                // Lets redirect the user to OTP verification Page
                Intent intent = new Intent(SignUp.this, OTPVerification.class);
                intent.putExtra("phone", mobileNo.trim());
                intent.putExtra("verificationId", verificationId);
                startActivity(intent);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + mobileNo.trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


}