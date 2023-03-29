package com.example.travel_blog;


import android.content.Context;
import android.content.SharedPreferences;

public class BlogPreferences {

    private static final String KEY_LOGIN_STATE = "key_login_state";
    private static final String UserName = "SignedIn_UserName";
    private static final String UserEmail = "SignedIn_UserEmail";
    private static final String UserMob = "SignedIn_UserMob";
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

    public String getUserName(){
        return sharedPreferences.getString(UserName, "");
    }

    public String getUserEmail(){
        return sharedPreferences.getString(UserEmail, "");
    }

    public String getUserMob(){
        return sharedPreferences.getString(UserMob, "");
    }

    public void setUserName(String name){
        sharedPreferences.edit().putString(UserName,name).apply();
    }
    public void setUserEmail(String email){
        sharedPreferences.edit().putString(UserEmail,email).apply();
    }

    public void setUserMob(String mob){

        sharedPreferences.edit().putString(UserMob,mob).apply();
    }
}