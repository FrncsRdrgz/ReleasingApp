package govph.rsis.seedreleasingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class ScanFragment extends Fragment {
    public static final String TAG = "Scan Fragment";
    public static final String EXTRA_RESULT = "govph.rsis.releasingapp.EXTRA_RESULT";
    public static final String EXTRA_SPA = "govph.rsis.releasingapp.EXTRA_SPA";
    private UserViewModel userViewModel;
    private SeedViewModel seedViewModel;
    UserDatabase database;
    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    TextView textView,tvVersion;
    User user;
    Intent intent;
    FragmentManager fragmentManager;
    View view;
    Button scanBtn;
    AlertDialog dialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scan,container,false);
        database = UserDatabase.getInstance(getActivity());
        user = database.userDao().isOnline();
        tvVersion = view.findViewById(R.id.tvVersion);
        scanBtn = view.findViewById(R.id.btnScanBarcode);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        //textView.setText(user.getName());
        fragmentManager = getActivity().getSupportFragmentManager();
        scannerView = view.findViewById(R.id.fragmentCodeScanner);
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        dialog = builder.create();
        mCodeScanner.startPreview();
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                        getSeedDetails(result.toString());
                    }
                });

            }
        });

        /*try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            tvVersion.setText("Version: "+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/
        /*tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });*/
        /*scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanIntent();
            }
        });*/
        return view;
    }

    public void scanIntent() {
        intent = new Intent(getActivity(),ScannedBarcodeActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE,"ScanFragment");
        startActivity(intent);
    }

    private void getSeedDetails(final String orderId){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //for production
        Log.e(TAG, "user_idno: "+user.getIdNo()+" order id: " + orderId );
        final String url = DecVar.receiver()+"/getOrder";
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e(TAG, "onResponse: "+response );
                        dialog.hide();
                        if(response.isEmpty() || response.equals("[]")) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("Incorrect Order Slip QR code")
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
                        //dialog.dismiss();
                        Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(getContext(), "Not connected to server.", Toast.LENGTH_SHORT).show();
                        //onBackPressed();
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

    private void saveSeedDetails(String response){
        try {
            JSONArray json = new JSONArray(response);
            //Log.e(TAG, "decodeReponse: "+json.getString(1) );
            if (json != null && json.length() >0) {
                intent = new Intent(getActivity(),SeedDetailsActivity.class);
                SeedDetailsFragment seedDetailsFragment = new SeedDetailsFragment();
                Bundle args = new Bundle();
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
                                //Log.e(TAG, "dapat dito: " );
                            }
                            else{
                                seedViewModel = ViewModelProviders.of(getActivity()).get(SeedViewModel.class);
                                seedViewModel.insert(seed);
                            }
                        }else{
                            seedViewModel = ViewModelProviders.of(getActivity()).get(SeedViewModel.class);
                            seedViewModel.insert(seed);
                        }
                    }

                    args.putString("order_id",jsonObject.getString("orderId"));
                    args.putString("fullname",fullname);
                }
                dialog.dismiss();
                mCodeScanner.releaseResources();
                seedDetailsFragment.setArguments(args);
                //Log.e(TAG, "saveSeedDetails: "+getFragmentManager() );
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, seedDetailsFragment).commit();
                //startActivity(intent);
                //finish();
            } else {
                Toast.makeText(getActivity(), "Incorrect Spa number.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
