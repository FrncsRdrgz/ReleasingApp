package com.example.releasingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.amitshekhar.DebugDB;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
   private static int SPLASH_TIME_OUT = 2000;
   Intent homeIntent;
   UserDatabase database;
    List<User> checkDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = UserDatabase.getInstance(this);
        final String user =database.userDao().isOnline();

        checkDb = database.userDao().fetchAll();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(checkDb != null && checkDb.isEmpty()){
                    homeIntent = new Intent(MainActivity.this, LoginActivity.class);
                }
                else{
                    if(user != null){
                        homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                        homeIntent.putExtra(EXTRA_MESSAGE,user);
                    }
                    else {
                        homeIntent = new Intent(MainActivity.this, SwitchAccountActivity.class);
                    }
                }

                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

}
