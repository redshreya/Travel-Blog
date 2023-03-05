package com.example.travel_blog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.travel_blog.adapter.MainAdapter;
import com.example.travel_blog.http.Blog;
import com.example.travel_blog.http.BlogArticlesCallback;
import com.example.travel_blog.http.BlogHttpClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private static final int SORT_TITLE = 0;
    private static final int SORT_DATE = 1;
    private int currentSort = SORT_DATE;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.sort){
                onSortClicked();
            }
            return false;
        });

        MenuItem searchItem = toolbar.getMenu().findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        adapter = new MainAdapter(blog -> BlogDetailsActivity.startBlogDetailsActivity(this, blog));
        RecyclerView recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        refreshLayout  = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this::loadData);

        loadData();
    }
    private void loadData() {
        refreshLayout.setRefreshing(true);
        BlogHttpClient.INSTANCE.loadBlogArticles(new BlogArticlesCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(List<Blog> blogList) {
                runOnUiThread(()-> {
                    refreshLayout.setRefreshing(false);
                    adapter.setData(blogList);
                    sortData();
                });
            }

            @Override
            public void onError() {
                runOnUiThread(()-> {
                    refreshLayout.setRefreshing(false);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onSortClicked(){
        String[] items = {"Title", "Date"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("Sort Order")
                .setSingleChoiceItems(items, currentSort, ((dialog, which) -> {
                    dialog.dismiss();
                    currentSort = which;
                    sortData();
                })).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortData(){
        if(currentSort == SORT_TITLE) {
            adapter.sortByTitle();
        }else if(currentSort == SORT_DATE){
            adapter.sortByDate();
        }
    }
}