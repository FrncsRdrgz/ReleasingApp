package com.example.releasingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
    public static final String TAG = "HomeActivity";
    private String barCode;
    private CodeScanner mCodeScanner;

    Intent intent;
    UserDatabase database;
    String globalId;


    Button viewBtn;
    EditText spaNo;
    List<User> checkDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        database = UserDatabase.getInstance(this);

        intent = getIntent();
        //String idNo = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //globalId = idNo;
        final String user =database.userDao().isOnline();
        globalId = user;
        String userName = database.userDao().getUserName(user);
        TextView textView = findViewById(R.id.textView);
        textView.setText(userName);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.navigation_scan:
                        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.navigation_list:
                        Intent intent2 = new Intent(HomeActivity.this, TransactionActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                }
                return false;

            }
        });
    }
    public void logout(View view) {
        Log.e(TAG, "logout: "+globalId );
        database.userDao().logout(globalId);
        intent = new Intent(this, SwitchAccountActivity.class);
        //intent.putExtra(EXTRA_MESSAGE,globalId);
        startActivity(intent);
    }
    public void switchAccount(View view){

        intent = new Intent(this, SwitchAccountActivity.class);
        intent.putExtra(EXTRA_MESSAGE,globalId);
        startActivity(intent);
    }

    public void scanSpa(View view) {

        Intent intent = new Intent(HomeActivity.this,ScannedBarcodeActivity.class);
        intent.putExtra(EXTRA_MESSAGE,"scanSPA");
        startActivity(intent);
    }

    public void getAllTransaction(View view) {

        // Request a string response from the provided URL.
        /*JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Display the first 500 characters of the response string.
                //textView.setText("Response is: "+ response.substring(0,500));
                Log.e(TAG, "onResponse: "+response );
                if (response != null && response.length() >0) {
                    try {

                        for(int i = 0 ; i < response.length();i++) {
                            JSONObject jsonObject = response.getJSONObject(i);

                            String fName = jsonObject.getString("fName");
                            String lName = jsonObject.getString("lName");
                            String orderId = jsonObject.getString("orderId");

                            JSONArray jsonArrayVarieties = jsonObject.getJSONArray("varieties");
                            for(int x = 0; x< jsonArrayVarieties.length(); x++) {
                                JSONObject jsonObjectVariety = jsonArrayVarieties.getJSONObject(x);

                                String variety = jsonObjectVariety.getString("variety");
                                String palletName = jsonObjectVariety.getString("palletName");
                                int quantity = jsonObjectVariety.getInt("quantity");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(HomeActivity.this, "Success!", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(HomeActivity.this, "Sorry", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("Error: " + error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);*/
    }
}
