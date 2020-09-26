package govph.rsis.releasingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import android.widget.FrameLayout;
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
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    Button releasedBtn, scanTag;
    User user;
    LayoutInflater inflater;
    AlertDialog dialog;

    FrameLayout frameLayout;
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
        textSgName = findViewById(R.id.sgName);
        textOrderNo = findViewById(R.id.orderNo);
        releasedBtn = findViewById(R.id.releaseBtn);

        inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        dialog = builder.create();
        scannerView = findViewById(R.id.scanner_view2);
        mCodeScanner = new CodeScanner(this,scannerView);


        textSgName.setText(fullname);
        textOrderNo.setText(orderId);


        releasedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                releasedSeeds();
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

        if(seedViewModel.isNotVerified(orderId) > 0){
            Log.e(TAG, "May hindi pa verified " );
        }
        else{
            Log.e(TAG, "Zero na" );
            releasedBtn.setVisibility(View.VISIBLE);
        }

        adapter.setScanTagClickedListener(new ReleasingAdapter.scanTagClicked() {
            @Override
            public void scanTag(final Seed seeds, int position , final View v) {
                Log.e(TAG, "scanTag: "+position );
                frameLayout = (FrameLayout) findViewById(R.id.frameLayoutScanner);
                frameLayout.setVisibility(View.VISIBLE);
                scrollView = (ScrollView) findViewById(R.id.scrollView);
                scrollView.setVisibility(View.INVISIBLE);

                mCodeScanner.startPreview();
                mCodeScanner.setDecodeCallback(new DecodeCallback() {
                    @Override
                    public void onDecoded(@NonNull final Result result) {
                        SeedDetailsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(result.getText().contains(seeds.getPallet_code())){
                                    Toast.makeText(SeedDetailsActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                                    frameLayout = (FrameLayout) findViewById(R.id.frameLayoutScanner);
                                    frameLayout.setVisibility(View.INVISIBLE);
                                    scrollView = (ScrollView) findViewById(R.id.scrollView);
                                    scrollView.setVisibility(View.VISIBLE);
                                    seeds.setVerified("1");
                                    database.seedDao().updateSeed(seeds);
                                    scanTag = v.findViewById(R.id.scan_tag);
                                    seedViewModel.update(seeds);

                                    if(seedViewModel.isNotVerified(orderId) > 0){
                                        Log.e(TAG, "May hindi pa verified " );
                                    }
                                    else{
                                        Log.e(TAG, "Zero na" );
                                        releasedBtn.setVisibility(View.VISIBLE);
                                    }

                                }
                                else{
                                    Toast.makeText(SeedDetailsActivity.this, "Not equal", Toast.LENGTH_SHORT).show();
                                    new androidx.appcompat.app.AlertDialog.Builder(SeedDetailsActivity.this)
                                            .setMessage("Incorrect Tag")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mCodeScanner.startPreview();
                                                }
                                            }).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*frameLayout = (FrameLayout) findViewById(R.id.frameLayoutScanner);
        frameLayout.setVisibility(View.INVISIBLE);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setVisibility(View.VISIBLE);*/
    }

    public void releasedSeeds() {
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
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                params.put("orderId",orderId);
                params.put("philrice_idno",user.getIdNo());
                return params;
            }
        };
        queue.add(sr);
    }
}
