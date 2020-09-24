package govph.rsis.releasingapp;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TransactionFragment extends Fragment {
    private ArrayList<SeedGrower> seedGrowerList;
    private RecyclerView rvSeedGrower;
    UserDatabase database;
    TextView textView,tvVersion;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        database = UserDatabase.getInstance(getActivity());
        user = database.userDao().isOnline();
        View view = inflater.inflate(R.layout.fragment_transaction,container,false);
        textView = view.findViewById(R.id.textViewUser);
        tvVersion = view.findViewById(R.id.tvVersion);
        rvSeedGrower = view.findViewById(R.id.rvAllOrder);
        seedGrowerList = new ArrayList<>();
        textView.setText(user.getName());

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            tvVersion.setText("Version: "+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        getSeedGrowers();
        return view;
    }

    public void getSeedGrowers() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        final String url = DecVar.receiver()+"/getAllOrder";

        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
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

                                rvSeedGrower.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                rvSeedGrower.setItemAnimator(new DefaultItemAnimator());
                                SeedGrowerAdapter adapter = new SeedGrowerAdapter(getActivity(), seedGrowerList);
                                rvSeedGrower.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity(), "No Transactions.", Toast.LENGTH_SHORT).show();
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
                params.put("philrice_idno",user.getIdNo());
                return params;
            }
        };
        queue.add(sr);

    }
}
