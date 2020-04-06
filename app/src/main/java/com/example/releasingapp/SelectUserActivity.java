package com.example.releasingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amitshekhar.DebugDB;

import java.util.ArrayList;

public class SelectUserActivity extends AppCompatActivity implements UsersAdapter.UserClicked{

    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
    private static final String TAG = "SelectUserActivity";
    UserDatabase database;
    Intent intent;

    ArrayList<User> users;
    UsersAdapter adapter;
    RecyclerView rvUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        database = UserDatabase.getInstance(this);
        intent = getIntent();
        final String idNo = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE);
        rvUsers = (RecyclerView) findViewById(R.id.myAccounts);

        Log.e(TAG, "onCreate: "+ DebugDB.getAddressLog());
        // get all the users in User Dao
        //users = new ArrayList<>();
        users = getAllUsers();
        Log.e(TAG, "All users: "+users );
        rvUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvUsers.setItemAnimator(new DefaultItemAnimator());
        adapter = new UsersAdapter(this, users);
        adapter.setUserClickedListener(this);
        rvUsers.setAdapter(adapter);


        /*final String idNo = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE);

        final String[] users = database.userDao().getAll(idNo);

        ListView listView = (ListView) findViewById(R.id.usersList);

        final ArrayAdapter<User> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                final int updateStatus = database.userDao().updateStatus();
                Toast.makeText(SwitchAccountActivity.this, users[position], Toast.LENGTH_SHORT).show();
                String fullName = users[position];

                database.userDao().switchAccount(fullName);
                final String user =database.userDao().isOnline();

                intent = new Intent(SwitchAccountActivity.this, HomeActivity.class);
                intent.putExtra(EXTRA_MESSAGE,user);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
*/
    }

    private ArrayList<User> getAllUsers() {
        ArrayList<User> data = new ArrayList<>();

        for (User s : database.userDao().userAll()) {
            User details = new User(s.getIdNo(), s.fullName, s.status);
            data.add(details);
        }

        return data;
    }

    public void onClick(View v) {

        Intent intent = new Intent(this, ScannedBarcodeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUserSwitch(String idNo) {
        Log.e(TAG, "onUserSwitch: " + idNo);
        Toast.makeText(this, String.valueOf(idNo), Toast.LENGTH_SHORT).show();
        database.userDao().updateStatus();
        database.userDao().updateStatusOnline(idNo);
        final String user =database.userDao().isOnline();

        intent = new Intent(SelectUserActivity.this, HomeActivity.class);
        intent.putExtra(EXTRA_MESSAGE,user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
