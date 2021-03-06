package govph.rsis.seedreleasingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScannedBarcodeActivity extends AppCompatActivity {
    private static final String TAG = "ScannedBarcodeActivity";
    public static final String EXTRA_RESULT = "com.example.releasingapp.EXTRA_RESULT";
    public static final String EXTRA_SPA = "com.example.releasingapp.EXTRA_SPA";
    public static final String SCANNED_QR = "com.example.releasingapp.MESSAGE";
    private int CAMERA_PERMISSION_CODE = 1;
    private CodeScanner mCodeScanner;
    private boolean mPermissionCameraGranted;
    private UserViewModel userViewModel;
    private SeedViewModel seedViewModel;
    private CodeScannerView scannerView;
    private UserDatabase database;
    private Intent intent;
    String sender;
    private User user;
    LayoutInflater inflater;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        database = UserDatabase.getInstance(this);
        user = database.userDao().isOnline();
        intent = getIntent();
        sender = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        inflater = getLayoutInflater();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        dialog = builder.create();
        scanFunction();
    }

    public void scanFunction() {
        if(ContextCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mPermissionCameraGranted = true;
            mCodeScanner.startPreview();
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    ScannedBarcodeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                            switch (sender){
                                case "ScanFragment":
                                    getSeedDetails(result.toString());
                                    break;
                                case "LoginActivity":
                                    Log.e(TAG, "run: "+result.toString() );
                                    loginUser(result.toString());
                                    break;
                            }
                        }
                    });

                }
            });
        }
        else{
            requestCameraPermission();
        }
    }
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Allow this app to access camera to scan your QR CODE.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                            mPermissionCameraGranted = true;

                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            mPermissionCameraGranted = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPermissionCameraGranted) {
            mCodeScanner.startPreview();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPermissionCameraGranted) {
            mCodeScanner.releaseResources();
        }

    }

    private void getSeedDetails(final String orderId){
        RequestQueue queue = Volley.newRequestQueue(this);
        //for production

        final String url = DecVar.receiver()+"/getOrder";
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e(TAG, "onResponse: "+response );
                        dialog.dismiss();
                        if(response.isEmpty() || response.equals("[]")) {
                            new AlertDialog.Builder(ScannedBarcodeActivity.this)
                                    .setMessage("INCORRECT SPA NUMBER")
                                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mCodeScanner.startPreview();
                                        }
                                    }).show();
                        }
                        else{
                            saveSeedDetails(response);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(ScannedBarcodeActivity.this, "Not connected to server.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void saveSeedDetails(String response){
        try {
            JSONArray json = new JSONArray(response);
            //Log.e(TAG, "decodeReponse: "+json.getString(1) );
            if (json != null && json.length() >0) {
                intent = new Intent(ScannedBarcodeActivity.this,SeedDetailsActivity.class);
                for(int i = 0 ; i < json.length();i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    String fullname = jsonObject.getString("fullname");
                    /*if(database.seedDao().existing(jsonObject.getString("orderId")) > 0){
                        database.seedDao().deleteById(jsonObject.getString("orderId"));
                    }*/
                    intent.putExtra(EXTRA_RESULT,fullname);
                    intent.putExtra(EXTRA_SPA,jsonObject.getString("orderId"));

                    JSONArray jsonArrayVarieties = jsonObject.getJSONArray("varieties");
                    for(int x = 0; x< jsonArrayVarieties.length(); x++) {
                        JSONObject jsonObjectVariety = jsonArrayVarieties.getJSONObject(x);

                        String orderId = jsonObject.getString("orderId");
                        String variety = jsonObjectVariety.getString("variety");
                        String pallet_id = jsonObjectVariety.getString("pallet_id");
                        String quantity = jsonObjectVariety.getString("quantity");
                        String lotCode = jsonObjectVariety.getString("lotCode");

                        Seed seed = new Seed();
                        seed.setOrderId(orderId);
                        seed.setVariety(variety);
                        seed.setPallet_code(pallet_id);
                        seed.setQuantity(quantity);
                        seed.setLotCode(lotCode);
                        seed.setVerified("0");

                         Seed seed1 = database.seedDao().existing(jsonObject.getString("orderId"),jsonObjectVariety.getString("lotCode"));

                        if(seed1 != null){
                            if(seed1.getOrderId().equals(orderId) && seed1.getVariety().equals(variety) && seed1.getPallet_code().equals(pallet_id)
                                    && seed1.getQuantity().equals(quantity) && seed1.getLotCode().equals(lotCode)){
                                Log.e(TAG, "dapat dito: " );
                            }
                            else{
                                seedViewModel = ViewModelProviders.of(ScannedBarcodeActivity.this).get(SeedViewModel.class);
                                seedViewModel.insert(seed);
                            }
                        }else{
                            seedViewModel = ViewModelProviders.of(ScannedBarcodeActivity.this).get(SeedViewModel.class);
                            seedViewModel.insert(seed);
                        }

                        /*if(database.seedDao().existing(jsonObject.getString("orderId")) > 0){

                        }else{*/

                        //}
                    }

                }
                dialog.dismiss();
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ScannedBarcodeActivity.this, "Incorrect Spa number.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void loginUser(String userId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = DecVar.receiver()+"/users/"+userId;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: "+response );
                if (response.isEmpty() || response.equals("[]")) {

                    Toast.makeText(ScannedBarcodeActivity.this, "Sorry, invalid Id number", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    mCodeScanner.startPreview();
                } else {
                    try {

                        //decoding json object w/out array
                        JSONObject temp = new JSONObject(response);
                        String idNo = temp.getString("philrice_idno");
                        String fullName = temp.getString("fullname");
                        //checking if idNo already exists in database
                        if (database.userDao().isExisting(idNo) == 0) {
                            Log.e(TAG, "onResponse: test" );
                            //inserting new user to database
                            mCodeScanner.startPreview();
                            User user = new User(idNo,fullName,true);
                            userViewModel.insert(user);
                        }
                        else{
                            Log.e(TAG, "onResponse: " );
                            mCodeScanner.startPreview();
                            database.userDao().updateStatusOnline(idNo);
                            //userViewModel.update(user);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mCodeScanner.startPreview();
                    }

                    Toast.makeText(ScannedBarcodeActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    intent =  new Intent(ScannedBarcodeActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    dialog.dismiss();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                mCodeScanner.startPreview();
                Toast.makeText(ScannedBarcodeActivity.this, "Unexpected response error code", Toast.LENGTH_SHORT).show();
                VolleyLog.e("Error: " + error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
