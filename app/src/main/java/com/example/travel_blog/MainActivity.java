package com.example.travel_blog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView TV =  findViewById(R.id.textView);
        TV.setText("Fuck World!!!! YO!");

        startActivity(new Intent(this, BlogDetailsActivity.class));
    }
}