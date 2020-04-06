package com.example.releasingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.amitshekhar.DebugDB;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwitchAccountActivity extends AppCompatActivity{

    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
    private static final String TAG = "SwitchAccountActivity";
    UserDatabase database;
    Intent intent;

    ArrayList<User> users;
    UsersAdapter adapter;
    RecyclerView rvUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_account);
    }

    public void selectUser(View view) {
        intent = new Intent(this, SelectUserActivity.class);
        startActivity(intent);
    }

    public void addNewUser(View view) {
        intent = new Intent(this, ScannedBarcodeActivity.class);
        intent.putExtra(EXTRA_MESSAGE,"login");
        startActivity(intent);
    }
}
