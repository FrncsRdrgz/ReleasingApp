package com.example.releasingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransactionActivity extends AppCompatActivity implements SeedGrowerAdapter.SGClicked {
    public static final String TAG = "TransactionActivity";
    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
    private RecyclerView rvSeedGrower;

    private LinearLayoutManager linearLayoutManager;
    private ArrayList<SeedGrower> seedGrowerList;
    private RecyclerView.Adapter adapter;

    Intent intent;
    String globalId;
    UserDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        bottomNavigation();
        database = UserDatabase.getInstance(this);
        globalId = database.userDao().isOnline();
        String userName = database.userDao().getUserName(globalId);
        TextView textView = findViewById(R.id.textViewUser);

        textView.setText(userName);
        rvSeedGrower = findViewById(R.id.rvAllOrder);

        seedGrowerList = new ArrayList<>();
        adapter = new SeedGrowerAdapter(this ,seedGrowerList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        getSeedGrowers();
    }
    public void getSeedGrowers() {
        RequestQueue queue = Volley.newRequestQueue(this);
        //for production
        //final String url = "https://rsis.philrice.gov.ph/rsis/seed_ordering/releasing/api/getAllOrder";
        //stagingdev
        //final String url = "https://stagingdev.philrice.gov.ph/rsis/seed_ordering/releasing/api/getAllOrder";
        //laptop sa bahay
        final String url = "http://192.168.1.77/seed_ordering/releasing/api/getAllOrder";
        //localhost
        //final String url = "http://192.168.11.106/seed_ordering/releasing/api/getAllOrder";
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            Log.e(TAG, "onResponse: "+json );
                            if (json != null && json.length() >0) {

                                for(int i = 0 ; i < json.length();i++) {
                                    JSONObject jsonObject = json.getJSONObject(i);

                                    String fullname = jsonObject.getString("fullname");
                                    String orderId = jsonObject.getString("orderId");
                                    int status = jsonObject.getInt("status");
                                    SeedGrower seedGrower = new SeedGrower();
                                    seedGrower.setFullname(fullname);
                                    seedGrower.setOrderId(orderId);
                                    seedGrower.setStatus(status);
                                    seedGrowerList.add(seedGrower);
                                }

                                rvSeedGrower.setLayoutManager(new LinearLayoutManager(TransactionActivity.this, LinearLayoutManager.VERTICAL, false));
                                rvSeedGrower.setItemAnimator(new DefaultItemAnimator());
                                SeedGrowerAdapter adapter = new SeedGrowerAdapter(TransactionActivity.this, seedGrowerList);
                                adapter.setSgClickedListener(TransactionActivity.this);
                                rvSeedGrower.setAdapter(adapter);
                            } else {
                                Toast.makeText(TransactionActivity.this, "No Transactions.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("philrice_idno",globalId);
                return params;
            }
        };
        queue.add(sr);

    }

    @Override
    public void onSGclicked(final String orderId) {

        RequestQueue queue = Volley.newRequestQueue(this);
        //production
        //final String url = "https://rsis.philrice.gov.ph/rsis/seed_ordering/releasing/api/getOrder";
        //staging
        //final String url = "https://stagingdev.philrice.gov.ph/rsis/seed_ordering/releasing/api/getOrder";
        //localhost
        final String url = "http://192.168.1.77/seed_ordering/releasing/api/getOrder";
        //final String url = "http://192.168.11.106/seed_ordering/releasing/api/getOrder";

        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        intent = new Intent(TransactionActivity.this,SeedDetailsActivity.class);
                        intent.putExtra(EXTRA_MESSAGE,response);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(TransactionActivity.this, "Not connected to server.", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("orderId",orderId);
                params.put("philrice_idno",globalId);
                return params;
            }
        };
        queue.add(sr);

    }


    public void bottomNavigation() {
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_scan:
                        Intent intent = new Intent(TransactionActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.navigation_list:
                        Intent intent2 = new Intent(TransactionActivity.this, TransactionActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                }
                return false;

            }
        });
    }
}
