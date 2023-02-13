package com.example.travel_blog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class BlogDetailsActivity extends AppCompatActivity {
    public static final String IMAGE_URL ="https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/3436e16367c8ec2312a0644bebd2694d484eb047/images/sydney_image.jpg";
    public static final String AVATAR_URL = "https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/3436e16367c8ec2312a0644bebd2694d484eb047/avatars/avatar1.jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);


        ImageView IMG1 = (ImageView) findViewById(R.id.imageView2);

        Glide.with(this)
                .load(IMAGE_URL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(IMG1);


        ImageView IMG2 = (ImageView) findViewById(R.id.imageView3);

        Glide.with(this)
                .load(AVATAR_URL)
                .transform(new CircleCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(IMG2);

        TextView Title = findViewById(R.id.textView3);
        TextView date = findViewById(R.id.textView2);
        TextView author = findViewById(R.id.textView4);
        RatingBar stars = findViewById(R.id.ratingBar);
        TextView textStar = findViewById(R.id.textView5);
        TextView views = findViewById(R.id.textView6);
        TextView caption = findViewById(R.id.textView7);
        // IMG1.setImageResource(R.drawable.sydney_image);
        // IMG2.setImageResource(R.drawable.avatar);
        Title.setText("G'day from Sydney");
        date.setText("12th February 2023");
        author.setText("Grayson Wells");
        stars.setNumStars(5);
        stars.setRating((float)4.4);
        textStar.setText("4.4");
        views.setText("(8529 views)");
        caption.setText("Australia is one of the most popular travel destinations in the world.");


    }
}