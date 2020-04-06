package com.example.releasingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
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
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ScannedBarcodeActivity extends AppCompatActivity {
    private static final String TAG = "ScannedBarcodeActivity";
    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
    private String barCode;
    private CodeScanner mCodeScanner;
    UserDatabase database;
    String globalId;
    Intent homeIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        Log.e(TAG, "onCreate: " + DebugDB.getAddressLog());
        database = UserDatabase.getInstance(this);

        homeIntent = getIntent();
        final String sender = homeIntent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        globalId =database.userDao().isOnline();
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                ScannedBarcodeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(ScannedBarcodeActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        barCode = result.toString();
                        //Log.e(TAG, "run: "+barCode );
                        if(sender.equals("login")) {
                            Log.e(TAG, "run: login " );
                            responseAction(barCode);
                        }else {
                            getSeedDetails(barCode);
                        }


                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: " + barCode);
        mCodeScanner.releaseResources();
        super.onPause();

        //sendRequest();

    }

    //get the users from web server
    void responseAction(String x) {

        RequestQueue queue = Volley.newRequestQueue(this);

        //for production
        //final String url = "https://rsis.philrice.gov.ph/rsis/seed_ordering/users/api/" +x;
        //for staging url
        //final String url = "https://stagingdev.philrice.gov.ph/rsis/seed_ordering/users/api/" +x;
        //localhost
        final String url = "http://192.168.1.77/seed_ordering/users/api/" + x;
        //final String url = "http://192.168.11.106/seed_ordering/users/api/" + x;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the first 500 characters of the response string.
                //textView.setText("Response is: "+ response.substring(0,500));

                if (response.equals("false")) {
                    Toast.makeText(ScannedBarcodeActivity.this, "Sorry, invalid Id number", Toast.LENGTH_SHORT).show();
                } else {

                    try {

                        //decoding json object w/out array
                        JSONObject temp = new JSONObject(response.toString());

                        //int id = temp.getInt("id");
                        String idNo = temp.getString("philrice_idno");
                        String fullName = temp.getString("fullname");
                        //String created_at = temp.getString("created_at");
                        int status = 1;

                        final int updateStatus = database.userDao().updateStatus();

                        //checking if idNo already exists in database
                        if (database.userDao().isExisting(idNo) == 0) {
                            //inserting new user to database
                            User newUser = new User(idNo,fullName,status);
                            database.userDao().insertUser(newUser);
                        }
                        else{
                            database.userDao().updateStatusOnline(idNo);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(ScannedBarcodeActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                    //Log.e(TAG, "True " + response);
                    final String user =database.userDao().isOnline();

                    if(user != null){
                        Log.e(TAG, "is not null: "+user );
                        homeIntent = new Intent(ScannedBarcodeActivity.this, HomeActivity.class);
                        homeIntent.putExtra(EXTRA_MESSAGE,user);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("Error: " + error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public void getSeedDetails(final String orderId){

        RequestQueue queue = Volley.newRequestQueue(this);
        //for production
        //final String url = "https://rsis.philrice.gov.ph/rsis/seed_ordering/releasing/api/getOrder";
        //staging url
        //final String url = "https://stagingdev.philrice.gov.ph/rsis/seed_ordering/releasing/api/getOrder";
        //localhost
        final String url = "http://192.168.1.77/seed_ordering/releasing/api/getOrder";
        //final String url = "http://192.168.11.106/seed_ordering/releasing/api/getOrder";

        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        homeIntent = new Intent(ScannedBarcodeActivity.this,SeedDetailsActivity.class);
                        homeIntent.putExtra(EXTRA_MESSAGE,response);
                        startActivity(homeIntent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(ScannedBarcodeActivity.this, "Not connected to server.", Toast.LENGTH_SHORT).show();
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
}
