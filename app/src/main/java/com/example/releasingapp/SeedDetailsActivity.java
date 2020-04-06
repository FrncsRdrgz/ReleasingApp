package com.example.releasingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeedDetailsActivity extends AppCompatActivity {
    public static final String TAG = "SeedDetailsActivity";
    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
    private RecyclerView rvSeed;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Seed> seedList;
    Button releasedBtn;
    Intent intent;
    String globalId;
    UserDatabase database;
    TextView textSgName, textOrderNo;

    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_details);

        textSgName = (TextView) findViewById(R.id.sgName);
        textOrderNo = (TextView) findViewById(R.id.orderNo);
        intent = getIntent();
        String response = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE);
        rvSeed = findViewById(R.id.main_list);
        seedList = new ArrayList<>();

        database = UserDatabase.getInstance(this);
        globalId =database.userDao().isOnline();

        Log.e(TAG, "test response: "+response );
        //viewBtn = (Button) findViewById(R.id.viewBtn);
        try {
            JSONArray json = new JSONArray(response);
            if (json != null && json.length() >0) {
                for(int i = 0 ; i < json.length();i++) {
                    JSONObject jsonObject = json.getJSONObject(i);

                    String fName = jsonObject.getString("fName");
                    String lName = jsonObject.getString("lName");
                    String orderId = jsonObject.getString("orderId");
                    textSgName.setText(fName+" "+lName);
                    textOrderNo.setText(jsonObject.getString("orderId"));

                    JSONArray jsonArrayVarieties = jsonObject.getJSONArray("varieties");
                    for(int x = 0; x< jsonArrayVarieties.length(); x++) {
                        JSONObject jsonObjectVariety = jsonArrayVarieties.getJSONObject(x);

                        Seed seed = new Seed();
                        seed.setVariety(jsonObjectVariety.getString("variety"));
                        seed.setpalletName(jsonObjectVariety.getString("palletName"));
                        seed.setQuantity(jsonObjectVariety.getString("quantity"));

                        seedList.add(seed);
                    }
                }

                rvSeed.setLayoutManager(new LinearLayoutManager(SeedDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
                rvSeed.setItemAnimator(new DefaultItemAnimator());
                ReleasingAdapter adapter = new ReleasingAdapter(SeedDetailsActivity.this, seedList);
                rvSeed.setAdapter(adapter);
            } else {
                Toast.makeText(SeedDetailsActivity.this, "Incorrect Spa number.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void releasedSeeds(View view) {

        releasedBtn = (Button) findViewById(R.id.releaseBtn);
        final String spaNo = textOrderNo.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://192.168.1.77/seed_ordering/releasing/api/releaseOrder";

        //for production
        //final String url = "https://rsis.philrice.gov.ph/rsis/seed_ordering/releasing/api/releaseOrder";
        //for staging
        //final String url = "https://stagingdev.philrice.gov.ph/rsis/seed_ordering/releasing/api/releaseOrder";
        //final String url = "http://192.168.11.106/seed_ordering/releasing/api/releaseOrder/";
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //alert dialog
                        intent = new Intent(SeedDetailsActivity.this,HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(SeedDetailsActivity.this, R.string.server_success, Toast.LENGTH_SHORT).show();


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
                params.put("orderId",spaNo);
                params.put("philrice_idno",globalId);
                return params;
            }
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }*/
        };
        queue.add(sr);
    }
}
