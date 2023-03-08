package com.example.travel_blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPVerification extends AppCompatActivity {

    private EditText verifyCode;
    private TextView msg;
    private Button verify, resend;
    private String VerificationId;
    private BlogPreferences mausmi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        // Creating object of Blog Preference to Change login state of user on successful OTP Verification
        mausmi = new BlogPreferences(this);

        verifyCode = findViewById(R.id.editTextNumber);
        verify = findViewById(R.id.button5);
        msg = findViewById(R.id.textView8);
        resend = findViewById(R.id.button6);
        msg.setText(String.format("Verification Code Sent to +91-%s", getIntent().getStringExtra("phone")));

        VerificationId = getIntent().getStringExtra("verificationId");

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OTPVerification.this, "OTP Resent", Toast.LENGTH_SHORT).show();
                // Logic likhna baki hai abhi.....
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyCode.getText().toString().trim().isEmpty()){
                    verifyCode.setError("Mitrooo OTP input kar do....");
                } else{
                    if(VerificationId != null){
                        String code = verifyCode.getText().toString().trim();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, code);
                        FirebaseAuth.getInstance()
                                .signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    startMainActivity();
                                } else{
                                    Toast.makeText(OTPVerification.this, "Kya yaar tu galat OTP daal ra hai Chasma Pehen!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void startMainActivity() {
        mausmi.setLogIn(true);
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}