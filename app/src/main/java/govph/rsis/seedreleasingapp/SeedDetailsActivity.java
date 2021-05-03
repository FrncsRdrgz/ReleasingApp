package govph.rsis.seedreleasingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeedDetailsActivity extends AppCompatActivity {
    public static final String TAG = "SeedDetailsActivity";
    private String fullname,orderId;
    private RecyclerView rvSeed;
    private SeedViewModel seedViewModel;
    private ArrayList<Seed> seedList = new ArrayList<>();
    TextView textSgName, textOrderNo;
    private UserDatabase database;
    Intent intent;
    Button releasedBtn, scanTag, scannerBackBtn;
    User user;
    LayoutInflater inflater;
    AlertDialog dialog;
    EditText etOrderNo;
    RelativeLayout relativeLayout;
    ScrollView scrollView;
    CodeScanner mCodeScanner;
    CodeScannerView scannerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_details);
        intent = getIntent();
        fullname = intent.getStringExtra(ScannedBarcodeActivity.EXTRA_RESULT);
        orderId = intent.getStringExtra(ScannedBarcodeActivity.EXTRA_SPA);
        database = UserDatabase.getInstance(this);
        user = database.userDao().isOnline();
        rvSeed = findViewById(R.id.main_list);
        //textSgName = findViewById(R.id.sgName);
        //textOrderNo = findViewById(R.id.orderNo);
        //releasedBtn = findViewById(R.id.releaseBtn);
        //etOrderNo = findViewById(R.id.orNo);
        //scannerBackBtn = (Button) findViewById(R.id.scannerBackBtn);

        inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        dialog = builder.create();
        //scannerView = findViewById(R.id.scanner_view2);
        mCodeScanner = new CodeScanner(this,scannerView);


        textSgName.setText(fullname);
        textOrderNo.setText(orderId);


        releasedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.show();
                if(!etOrderNo.getText().toString().isEmpty()){
                    releasedSeeds();
                }else{
                    //dialog.hide();
                    Toast.makeText(SeedDetailsActivity.this, "Please enter the OR Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvSeed.setLayoutManager(new LinearLayoutManager(SeedDetailsActivity.this, LinearLayoutManager.VERTICAL, false));

        final ReleasingAdapter adapter = new ReleasingAdapter();
        rvSeed.setAdapter(adapter);
        seedViewModel = ViewModelProviders.of(SeedDetailsActivity.this).get(SeedViewModel.class);
        seedViewModel.getByOrderId(orderId).observe(SeedDetailsActivity.this, new Observer<List<Seed>>() {
            @Override
            public void onChanged(List<Seed> seeds) {
                adapter.setSeeds(seeds);

            }
        });

        /*if(seedViewModel.isNotVerified(orderId) > 0){
            Log.e(TAG, "May hindi pa verified " );
        }
        else{
            Log.e(TAG, "Zero na" );
            releasedBtn.setVisibility(View.VISIBLE);
        }

        scannerBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutScanner);
                relativeLayout.setVisibility(View.INVISIBLE);
                scrollView = (ScrollView) findViewById(R.id.scrollView);
                scrollView.setVisibility(View.VISIBLE);
                mCodeScanner.releaseResources();
            }
        });
        adapter.setScanTagClickedListener(new ReleasingAdapter.scanTagClicked() {
            @Override
            public void scanTag(final Seed seeds, int position , final View v) {
                Log.e(TAG, "scanTag: "+position );
                relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutScanner);
                relativeLayout.setVisibility(View.VISIBLE);
                scrollView = (ScrollView) findViewById(R.id.scrollView);
                scrollView.setVisibility(View.INVISIBLE);

                mCodeScanner.startPreview();
                mCodeScanner.setDecodeCallback(new DecodeCallback() {
                    @Override
                    public void onDecoded(@NonNull final Result result) {
                        SeedDetailsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                                //scanAuthTag(result.getText(),seeds.getPallet_code());
                                RequestQueue queue = Volley.newRequestQueue(SeedDetailsActivity.this);
                                final String url = DecVar.receiver()+"/checkTag/"+result.getText()+"/"+seeds.getPallet_code();
                                Log.e(TAG, "scanAuthTag: "+url );
                                // Request a string response from the provided URL.
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        Log.e(TAG, "onResponse: "+response );
                                        if (response.isEmpty() || response.equals("[]")) {

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.hide();
                                                    mCodeScanner.startPreview();
                                                }
                                            },300);
                                            Toast.makeText(SeedDetailsActivity.this, "Sorry, wrong authenticity tag", Toast.LENGTH_SHORT).show();
                                        } else {
                                            dialog.hide();
                                            mCodeScanner.startPreview();
                                            //quantity = Integer.parseInt(seeds.getCompare_quantity()+20);
                                            String compare_quantity = (seeds.getCompare_quantity() != null) ? seeds.getCompare_quantity() : "0";
                                            int quantity = Integer.parseInt(compare_quantity) + 20;
                                            seeds.setCompare_quantity(String.valueOf(quantity));
                                            String authTag = (seeds.getAuthTag() != null) ? seeds.getAuthTag()+ ", " : "";
                                            seeds.setAuthTag(authTag + result.getText() );

                                            Log.e(TAG, "authTag: "+seeds.getAuthTag() );
                                            Log.e(TAG, "compare quantity: "+seeds.getCompare_quantity());
                                            seedViewModel.update(seeds);
                                            if(Float.parseFloat(seeds.getQuantity()) == Float.parseFloat(seeds.getCompare_quantity())){
                                                seeds.setVerified("1");
                                                //seeds.setAuthTag(result.getText());
                                                dialog.dismiss();

                                                try {
                                                    Toast.makeText(SeedDetailsActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                                                    relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutScanner);
                                                    relativeLayout.setVisibility(View.INVISIBLE);
                                                    scrollView = (ScrollView) findViewById(R.id.scrollView);
                                                    scrollView.setVisibility(View.VISIBLE);
                                                    mCodeScanner.releaseResources();
                                                    //decoding json object w/out array
                                                    JSONObject temp = new JSONObject(response);
                                                    //checking if idNo already exists in database

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                if(seedViewModel.isNotVerified(orderId) > 0){
                                                    Log.e(TAG, "May hindi pa verified " );
                                                }
                                                else{
                                                    Log.e(TAG, "Zero na" );
                                                    releasedBtn.setVisibility(View.VISIBLE);
                                                }
                                            }



                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        dialog.hide();
                                        Toast.makeText(SeedDetailsActivity.this, "Unexpected response error code", Toast.LENGTH_SHORT).show();
                                        VolleyLog.e("Error: " + error);
                                    }
                                });

                                // Add the request to the RequestQueue.
                                queue.add(stringRequest);
                            }
                        });
                    }
                });
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dialog.hide();
        /*frameLayout = (FrameLayout) findViewById(R.id.frameLayoutScanner);
        frameLayout.setVisibility(View.INVISIBLE);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setVisibility(View.VISIBLE);*/
    }

    public void releasedSeeds() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = DecVar.receiver()+"/releaseOrder";
        Log.e(TAG, "releasedSeeds: "+etOrderNo.getText().toString() );
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("success")){
                            dialog.hide();
                            new AlertDialog.Builder(SeedDetailsActivity.this)
                                    .setTitle("Release Successfully")
                                    .setMessage("This order has been released.")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            intent = new Intent(SeedDetailsActivity.this,HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            finish();
                                            Toast.makeText(SeedDetailsActivity.this, R.string.server_success, Toast.LENGTH_SHORT).show();
                                            seedViewModel.deleteById(orderId);
                                        }
                                    }).show();
                        }
                        else{
                            new AlertDialog.Builder(SeedDetailsActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Error on releasing seeds")
                                    .setNegativeButton("Try Again",null).show();
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
                params.put("orderId",orderId);
                params.put("or_no",etOrderNo.getText().toString());
                params.put("philrice_idno",user.getIdNo());
                return params;
            }
        };
        queue.add(sr);
    }
}
