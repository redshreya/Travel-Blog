package com.example.travel_blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class Profile extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private TextView username, useremail,userno;
    private BlogPreferences mausmi;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mausmi = new BlogPreferences(this);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        username = findViewById(R.id.textView11);
        useremail = findViewById(R.id.textView12);
        userno = findViewById(R.id.textView13);

        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
            ImageView imageView = findViewById(R.id.imageView);
            username.setText(personName);
            useremail.setText(personEmail);
            userno.setText("");
            //imageView.setImageResource(personPhoto);
            Glide.with(this)
                    .load(personPhoto)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .transform(new CircleCrop())
                    .into(imageView);

        }else{
            username.setText(mausmi.getUserName());
            useremail.setText(mausmi.getUserEmail());
            userno.setText(mausmi.getUserMob());
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }


        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.person);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add:
                        startActivity(new Intent(getApplicationContext(), AddBlog.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.person:
                        return true;

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }


        });
    }
}