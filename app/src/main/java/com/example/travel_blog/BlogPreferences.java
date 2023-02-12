package com.example.travel_blog;


import android.content.Context;
import android.content.SharedPreferences;

public class BlogPreferences {

    private static final String KEY_LOGIN_STATE = "key_login_state";
    private SharedPreferences sharedPreferences;

    BlogPreferences(Context context){
        sharedPreferences = context.getSharedPreferences("travel_blog",context.MODE_PRIVATE);
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(KEY_LOGIN_STATE,false);
    }

    public void setLogIn(boolean flag){
        sharedPreferences.edit().putBoolean(KEY_LOGIN_STATE,flag).apply();
    }

}