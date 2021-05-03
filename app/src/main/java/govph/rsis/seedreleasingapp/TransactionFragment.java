package govph.rsis.seedreleasingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {
    private ArrayList<SeedGrower> seedGrowerList;
    private RecyclerView rv_released_order;
    private SeedViewModel seedViewModel;
    SeedGrowerAdapter adapter;
    UserDatabase database;
    TextView emptyView,transactionList;
    User user;
    LinearLayout linearHeader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        database = UserDatabase.getInstance(getActivity());
        user = database.userDao().isOnline();
        View view = inflater.inflate(R.layout.fragment_transaction,container,false);
        rv_released_order = view.findViewById(R.id.rv_released_order);
        emptyView = view.findViewById(R.id.empty_view);
        transactionList = view.findViewById(R.id.transactionList);
        linearHeader = view.findViewById(R.id.linearHeader);
        rv_released_order.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_released_order.setItemAnimator(new DefaultItemAnimator());

        adapter = new SeedGrowerAdapter();
        rv_released_order.setAdapter(adapter);
        seedViewModel = ViewModelProviders.of(this).get(SeedViewModel.class);

        seedViewModel.getReleasedOrders().observe(this, new Observer<List<Seed>>() {
            @Override
            public void onChanged(List<Seed> seeds) {
                transactionList.setText("Transaction List: ("+seeds.size()+")");
                adapter.setOrders(seeds);
                if(seeds.isEmpty()){
                    emptyView.setVisibility(View.VISIBLE);
                    rv_released_order.setVisibility(View.GONE);
                    linearHeader.setVisibility(View.GONE);
                    //tvDeleteAll.setClickable(false);
                    //tvDeleteAll.setTextColor(Color.GRAY);
                }else{
                    rv_released_order.setVisibility(View.VISIBLE);
                    linearHeader.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    adapter.setOrders(seeds);
                    //tvDeleteAll.setClickable(true);
                    //tvDeleteAll.setTextColor(Color.WHITE);
                }
            }
        });
        return view;
    }

    /*public void getSeedGrowers() {
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

                                rv_released_order.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                rv_released_order.setItemAnimator(new DefaultItemAnimator());
                                SeedGrowerAdapter adapter = new SeedGrowerAdapter(getActivity(), seedGrowerList);
                                rv_released_order.setAdapter(adapter);
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

    }*/
}
