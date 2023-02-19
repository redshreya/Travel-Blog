package com.example.travel_blog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.travel_blog.adapter.MainAdapter;
import com.example.travel_blog.http.Blog;
import com.example.travel_blog.http.BlogArticlesCallback;
import com.example.travel_blog.http.BlogHttpClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new MainAdapter();
        RecyclerView recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadData();
    }
    private void loadData() {
        BlogHttpClient.INSTANCE.loadBlogArticles(new BlogArticlesCallback() {
            @Override
            public void onSuccess(List<Blog> blogList) {
                runOnUiThread(()-> {
                    adapter.submitList(blogList);
                });
            }

            @Override
            public void onError() {
                runOnUiThread(()-> {
                    showErrorSnackBar();
                });

            }
        });
    }

    private void showErrorSnackBar(){
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView,"Tumhara network fuckedup hai!! use JIO",Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.orange500));
        snackbar.setAction("Retry",v -> {
            loadData();
            snackbar.dismiss();
        });
        snackbar.show();
    }
}