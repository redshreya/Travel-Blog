package com.example.travel_blog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


public class LoginActivity extends AppCompatActivity {

    private BlogPreferences mausmi;
    private EditText username,password;
    private Button loginButton;
    private ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mausmi = new BlogPreferences(this);
        if(mausmi.isLoggedIn()){
            startMainActivity();
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.button2);
        username = findViewById(R.id.editTextTextPersonName2);
        password = findViewById(R.id.editTextTextPassword2);
        progressBar = findViewById(R.id.progressBar2);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.onLoginClicked();
            }
        });
    }
    // Validating the email address and password
    private void onLoginClicked(){
        String email = username.getText().toString();
        String pswd = password.getText().toString();
        if (email.isEmpty()) {
            username.setError("Username must not be empty");
        } else if (pswd.isEmpty()) {
            password.setError("Password must not be empty");
        } else if (!email.equals("admin") || !pswd.equals("admin")){
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
                .setMessage("Username or Passsword is incorrect")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Defining loading/progress function
    private void performLogin(){
        username.setEnabled(false);
        password.setEnabled(false);
        loginButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mausmi.setLogIn(true);
            //your code
            startMainActivity();
            finish();
        }, 2000);
    }
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}