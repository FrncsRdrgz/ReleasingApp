package govph.rsis.seedreleasingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeedDetailsFragment extends Fragment {
    public static final String TAG = "SeedDetailsFragment";
    View view;
    private UserDatabase database;
    private User user;
    private SeedViewModel seedViewModel;
    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    String order_id;
    RecyclerView rvSeed;
    TextView sdf_grower_name,sdf_order_id;
    EditText sdf_order_no;
    Button release_btn,scanner_back_btn;
    RelativeLayout sdf_order_details_layout, sdf_camera_layout;
    AlertDialog dialog;
    Seed seeds;
    public SeedDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_seed_details, container, false);
        database = UserDatabase.getInstance(getActivity());
        order_id = getArguments().getString("order_id");
        String fullname = getArguments().getString("fullname");

        user = database.userDao().isOnline();
        sdf_camera_layout = view.findViewById(R.id.sdf_camera_layout);
        sdf_order_details_layout = view.findViewById(R.id.sdf_order_details_layout);

        sdf_grower_name = view.findViewById(R.id.sdf_grower_name);
        sdf_order_id = view.findViewById(R.id.sdf_order_id);
        sdf_order_no = view.findViewById(R.id.sdf_order_no);
        release_btn = view.findViewById(R.id.release_btn);
        scannerView = view.findViewById(R.id.fragmentCodeScanner);
        scanner_back_btn =  view.findViewById(R.id.scanner_back_btn);
        mCodeScanner = new CodeScanner(getActivity(),scannerView);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        dialog = builder.create();

        sdf_grower_name.setText(fullname);
        sdf_order_id.setText(order_id);

        rvSeed = view.findViewById(R.id.main_list);

        release_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.show();
                if(!sdf_order_no.getText().toString().isEmpty()){
                    releasedSeeds();
                }else{
                    //dialog.hide();
                    Toast.makeText(getContext(), "Please enter the OR Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvSeed.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        final ReleasingAdapter adapter = new ReleasingAdapter();
        rvSeed.setAdapter(adapter);
        seedViewModel = ViewModelProviders.of(getActivity()).get(SeedViewModel.class);
        seedViewModel.getByOrderId(order_id).observe(getActivity(), new Observer<List<Seed>>() {
            @Override
            public void onChanged(List<Seed> seeds) {
                adapter.setSeeds(seeds);
                seeds = seeds;
            }
        });

        if(seedViewModel.isNotVerified(order_id) > 0){
            Log.e("Frag", "May hindi pa verified " );
        }
        else{
            Log.e("Frag", "Zero na" );
            release_btn.setVisibility(View.VISIBLE);
        }
        scanner_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sdf_camera_layout.setVisibility(View.INVISIBLE);
                sdf_order_details_layout.setVisibility(View.VISIBLE);
                mCodeScanner.releaseResources();
            }
        });

        adapter.setScanTagClickedListener(new ReleasingAdapter.scanTagClicked() {
            @Override
            public void scanTag(final Seed seeds, int position , final View v) {
                Log.e(TAG, "scanTag: "+position );
                sdf_camera_layout.setVisibility(View.VISIBLE);
                sdf_order_details_layout.setVisibility(View.INVISIBLE);

                mCodeScanner.startPreview();
                mCodeScanner.setDecodeCallback(new DecodeCallback() {
                    @Override
                    public void onDecoded(@NonNull final Result result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                                //scanAuthTag(result.getText(),seeds.getPallet_code());
                                RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                                            Toast.makeText(getActivity(), "Sorry, wrong authenticity tag", Toast.LENGTH_SHORT).show();
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
                                            if(Float.parseFloat(seeds.getCompare_quantity()) >= Float.parseFloat(seeds.getQuantity())){
                                                seeds.setVerified("1");
                                                //seeds.setAuthTag(result.getText());
                                                dialog.dismiss();

                                                try {
                                                    Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
                                                    sdf_camera_layout.setVisibility(View.INVISIBLE);
                                                    sdf_order_details_layout.setVisibility(View.VISIBLE);
                                                    mCodeScanner.releaseResources();
                                                    //decoding json object w/out array
                                                    JSONObject temp = new JSONObject(response);
                                                    //checking if idNo already exists in database

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                if(seedViewModel.isNotVerified(order_id) > 0){
                                                    Log.e(TAG, "May hindi pa verified " );
                                                }
                                                else{
                                                    Log.e(TAG, "Zero na" );
                                                    release_btn.setVisibility(View.VISIBLE);
                                                }
                                            }



                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        dialog.hide();
                                        Toast.makeText(getActivity(), "Unexpected response error code", Toast.LENGTH_SHORT).show();
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
        });

        return view;
    }

    public void releasedSeeds() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = DecVar.receiver()+"/releaseOrder";
        Log.e(TAG, "releasedSeeds: "+sdf_order_no.getText().toString() );
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("success")){

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Release Successfully")
                                    .setMessage("This order has been released.")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            /*intent = new Intent(SeedDetailsActivity.this,HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            finish();*/

                                            Toast.makeText(getActivity(), R.string.server_success, Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            seedViewModel.isReleased(order_id);
                                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                                        }
                                    }).show();
                        }
                        else{
                            dialog.hide();
                            new AlertDialog.Builder(getActivity())
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
                        new AlertDialog.Builder(getActivity())
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
                params.put("orderId",order_id);
                params.put("or_no",sdf_order_no.getText().toString());
                params.put("philrice_idno",user.getIdNo());
                return params;
            }
        };
        queue.add(sr);
    }
}