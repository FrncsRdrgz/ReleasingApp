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
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate: "+DebugDB.getAddressLog() );
        database = UserDatabase.getInstance(this);
        checkDb = database.userDao().checkDB();

        Log.e(TAG, "onCreate: "+checkDb );
        user = database.userDao().isOnline();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(checkDb != null && checkDb.isEmpty()){
                    homeIntent = new Intent(MainActivity.this, LoginActivity.class);
                }
                else{
                    if(user != null){
                        homeIntent = new Intent(MainActivity.this, HomeActivity.class);
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
