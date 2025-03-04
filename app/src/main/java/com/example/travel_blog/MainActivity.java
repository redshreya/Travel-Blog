package com.example.travel_blog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travel_blog.adapter.MainAdapter;
import com.example.travel_blog.http.Blog;
import com.example.travel_blog.http.BlogArticlesCallback;
import com.example.travel_blog.http.BlogHttpClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    ImageView imageView;


    private MainAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private static final int SORT_TITLE = 0;
    private static final int SORT_DATE = 1;
    private int currentSort = SORT_DATE;
    private BlogPreferences mausmi;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mausmi = new BlogPreferences(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.add:
                        startActivity(new Intent(getApplicationContext(),AddBlog.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.home:
                        return true;

                    case R.id.person:
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }

        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.sort){
                onSortClicked();
            }
            if(item.getItemId() == R.id.logout){
                googleSignInOptions = new GoogleSignInOptions.Builder()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        mausmi.setLogIn(false);
                        mausmi.setUserEmail("");
                        mausmi.setUserName("");
                        mausmi.setUserMob("");
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Signed Out")
                                .setMessage("See you soon ;)")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .show();
                    }
                });
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
        Snackbar snackbar = Snackbar.make(rootView,"Please Check Your Internet Connection.",Snackbar.LENGTH_INDEFINITE);
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