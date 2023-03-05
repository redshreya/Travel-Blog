package com.example.travel_blog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.travel_blog.http.Blog;
import com.example.travel_blog.http.BlogArticlesCallback;
import com.example.travel_blog.http.BlogHttpClient;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.List;

public class BlogDetailsActivity extends AppCompatActivity {
    private String BASE_URL = "https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/3eede691af3e8ff795bf6d31effb873d484877be";

    private static final String EXTRAS_BLOG = "EXTRAS_BLOG";
    TextView Title;
    TextView date;
    TextView author;
    RatingBar stars;
    TextView textStar;
    TextView views;
    TextView caption;
    ImageView IMG1, IMG2;
    ProgressBar loading;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);


        IMG1 = (ImageView) findViewById(R.id.imageView2);

        IMG2 = (ImageView) findViewById(R.id.imageView3);

        Title = findViewById(R.id.textView3);
        date = findViewById(R.id.textView2);
        author = findViewById(R.id.textView4);
        stars = findViewById(R.id.ratingBar);
        stars.setVisibility(View.INVISIBLE);
        textStar = findViewById(R.id.textView5);
        views = findViewById(R.id.textView6);
        caption = findViewById(R.id.textView7);
        loading = findViewById(R.id.progressBar);

        showData(getIntent()
                .getExtras()
                .getParcelable(EXTRAS_BLOG));


    }

    public static void startBlogDetailsActivity(Activity activity, Blog blog){
        Intent intent = new Intent(activity, BlogDetailsActivity.class);
        intent.putExtra(EXTRAS_BLOG, blog);
        activity.startActivity(intent);
    }

    private void showData(Blog blog){
        loading.setVisibility(View.GONE);
        Title.setText(blog.getTitle());
        date.setText(blog.getDate());
        author.setText(blog.getAuthor().getName());
        textStar.setText(String.valueOf(blog.getRating()));
        views.setText(String.format("(%d views)",blog.getViews()));
        caption.setText(Html.fromHtml(blog.getDescription()));
        stars.setRating(blog.getRating());
        stars.setVisibility(View.VISIBLE);
        Log.v("Image URL: ",blog.getImage());
        Glide.with(this)
                .load(BASE_URL + blog.getImage())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(IMG1);

        Glide.with(this)
                .load(BASE_URL + blog.getAuthor().getAvatar())
                .transform(new CircleCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(IMG2);
    }

}