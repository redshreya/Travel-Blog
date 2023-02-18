package com.example.travel_blog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.travel_blog.http.Blog;
import com.example.travel_blog.http.BlogArticlesCallback;
import com.example.travel_blog.http.BlogHttpClient;

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
        textStar = findViewById(R.id.textView5);
        views = findViewById(R.id.textView6);
        caption = findViewById(R.id.textView7);

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
            }
        });
    }

    private void showData(Blog blog){
        Title.setText(blog.getTitle());
        date.setText(blog.getDate());
        author.setText(blog.getAuthor().getName());
        textStar.setText(String.valueOf(blog.getRating()));
        views.setText(String.format("(%d views)",blog.getViews()));
        caption.setText(blog.getDescription());
        stars.setRating(blog.getRating());

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
}