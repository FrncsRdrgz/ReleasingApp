package govph.rsis.seedreleasingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import java.util.List;

public class SwitchAccountActivity extends AppCompatActivity{

    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
    private static final String TAG = "SwitchAccountActivity";
    private Intent intent;
    private UsersAdapter adapter;
    private UserViewModel userViewModel;
    private RecyclerView recyclerView;
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_switch_account);
        tvVersion = findViewById(R.id.tvVersion);



        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText("Version: "+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void selectUser(View view) {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(SwitchAccountActivity.this);
        View dialogView = inflater.inflate(R.layout.activity_select_user,null);
        builder.setCancelable(true);
        builder.setView(dialogView);

        recyclerView = dialogView.findViewById(R.id.myAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        adapter = new UsersAdapter();
        recyclerView.setAdapter(adapter);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getAllInactiveUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapter.setUsers(users);
            }
        });

        /*intent = new Intent(this, SelectUserActivity.class);
        startActivity(intent);*/
        builder.setNegativeButton("OK", null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        adapter.setUserClickedListener(new UsersAdapter.UserClicked() {
            @Override
            public void onUserSwitch(User user) {
                user.setStatus(true);
                userViewModel.update(user);
                intent = new Intent(SwitchAccountActivity.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });
    }

    public void addNewUser(View view) {
        intent = new Intent(this, ScannedBarcodeActivity.class);
        intent.putExtra(EXTRA_MESSAGE,"LoginActivity");
        startActivity(intent);
    }
}
