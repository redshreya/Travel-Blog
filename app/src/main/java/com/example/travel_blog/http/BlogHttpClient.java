package com.example.travel_blog.http;

import android.util.Log;
import android.view.textclassifier.TextLinks;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class BlogHttpClient {
    public static final BlogHttpClient INSTANCE = new BlogHttpClient();

    public static final String BASE_URL ="https://bitbucket.org/dmytrodanylyk/travel-blog-resources";
    public static final String BASE_PATH ="/raw/3eede691af3e8ff795bf6d31effb873d484877be";
    private static final String BLOG_ARTICLES_URL = BASE_URL + BASE_PATH + "/blog_articles.json";


    private Executor executor;
    private OkHttpClient client;
    private Gson gson;

    private BlogHttpClient(){
        executor = Executors.newFixedThreadPool(4);
        client = new OkHttpClient();
        gson = new Gson();
    }

    public void loadBlogArticles(BlogArticlesCallback callback){
        Log.v("URL is: ", BLOG_ARTICLES_URL);
        Request request = new Request.Builder()
                .get()
                .url(BLOG_ARTICLES_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // error
                callback.onError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try{
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String json = responseBody.string();
                        Log.v("Response: ", json);
                        BlogData blogData = gson.fromJson(json, BlogData.class);
                        if(blogData != null ){
                            // sucessful get request status 200
                            callback.onSuccess(blogData.getData());
                            return;
                        }
                    }
                } catch (IOException e){
                    Log.e("BlogHttpClient","API Fucked up..." + e.toString(),e);
                }

            }
        });

    }
}
