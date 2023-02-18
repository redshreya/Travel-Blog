package com.example.travel_blog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
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

import java.util.List;

public class BlogDetailsActivity extends AppCompatActivity {
    public static final String IMAGE_URL ="https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/3436e16367c8ec2312a0644bebd2694d484eb047/images/sydney_image.jpg";
    public static final String AVATAR_URL = "https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/3436e16367c8ec2312a0644bebd2694d484eb047/avatars/avatar1.jpg";

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
    protected void onCreate(Bundle savedInstanceState) {
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
        //start data loading
        loadData();


    }
    private void loadData(){
        BlogHttpClient.INSTANCE.loadBlogArticles(new BlogArticlesCallback() {
            @Override
            public void onSuccess(List<Blog> blogList){
                runOnUiThread(() -> showData(blogList.get(0)));
            }

            @Override
            public void onError(){
                //handle Error
                runOnUiThread(() -> showErrorSnackbar());
            }
        });
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
        Glide.with(this)
                .load(blog.getImage())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(IMG1);

        Glide.with(this)
                .load(blog.getAuthor().getAvatar())
                .transform(new CircleCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(IMG2);
    }

    private void showErrorSnackbar(){
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