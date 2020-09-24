package govph.rsis.releasingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeedDetailsActivity extends AppCompatActivity {
    public static final String TAG = "SeedDetailsActivity";
    private String response;
    private RecyclerView rvSeed;
    private ArrayList<Seed> seedList = new ArrayList<>();
    TextView textSgName, textOrderNo;
    UserDatabase database;
    Intent intent;
    Button releasedBtn;
    User user;
    LayoutInflater inflater;
    AlertDialog dialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_details);
        intent = getIntent();
        response = intent.getStringExtra(ScannedBarcodeActivity.EXTRA_RESULT);
        database = UserDatabase.getInstance(this);
        user = database.userDao().isOnline();
        rvSeed = findViewById(R.id.main_list);
        textSgName = findViewById(R.id.sgName);
        textOrderNo = findViewById(R.id.orderNo);
        releasedBtn = findViewById(R.id.releaseBtn);

        inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        dialog = builder.create();


        releasedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                releasedSeeds();
            }
        });

        decodeReponse(response);
    }

    private void decodeReponse(String response) {

        try {
            JSONArray json = new JSONArray(response);
            //Log.e(TAG, "decodeReponse: "+json.getString(1) );
            if (json != null && json.length() >0) {

                for(int i = 0 ; i < json.length();i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    Log.e(TAG, "decodeReponse:12 "+jsonObject );
                    String fullname = jsonObject.getString("fullname");

                    textSgName.setText(fullname);
                    textOrderNo.setText(jsonObject.getString("orderId"));

                    JSONArray jsonArrayVarieties = jsonObject.getJSONArray("varieties");
                    for(int x = 0; x< jsonArrayVarieties.length(); x++) {
                        JSONObject jsonObjectVariety = jsonArrayVarieties.getJSONObject(x);

                        Seed seed = new Seed();
                        seed.setVariety(jsonObjectVariety.getString("variety"));
                        seed.setPallet_code(jsonObjectVariety.getString("pallet_code"));
                        seed.setQuantity(jsonObjectVariety.getString("quantity"));
                        seed.setLotCode(jsonObjectVariety.getString("lotCode"));
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

    public void releasedSeeds() {
        final String spaNo = textOrderNo.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = DecVar.receiver()+"/releaseOrder";

        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if(response.equals("success")){
                            new AlertDialog.Builder(SeedDetailsActivity.this)
                                    .setTitle("Release Successfully")
                                    .setMessage("This order has been released.")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            intent = new Intent(SeedDetailsActivity.this,HomeActivity.class);
                                            finish();
                                            Toast.makeText(SeedDetailsActivity.this, R.string.server_success, Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        }
                        else{
                            new AlertDialog.Builder(SeedDetailsActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Error on releasing seeds")
                                    .setNegativeButton("Try Againg",null).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        new AlertDialog.Builder(SeedDetailsActivity.this)
                                .setCancelable(false)
                                .setMessage("Not connected to server")
                                .setNegativeButton("Try Again", null).show();
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("orderId",spaNo);
                params.put("philrice_idno",user.getIdNo());
                return params;
            }
        };
        queue.add(sr);
    }
}
