package govph.rsis.releasingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;


import java.util.List;

public class SelectUserActivity extends AppCompatActivity{
    private Intent intent;
    private UserDatabase database;
    private UsersAdapter adapter;
    private UserViewModel userViewModel;
    private RecyclerView recyclerView;
    TextView tvVersion1;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        tvVersion1 = findViewById(R.id.tvVersion1);
        recyclerView = findViewById(R.id.myAccounts);
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

        adapter.setUserClickedListener(new UsersAdapter.UserClicked() {
            @Override
            public void onUserSwitch(User user) {
                user.setStatus(true);
                userViewModel.update(user);
                intent = new Intent(SelectUserActivity.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion1.setText("Version: "+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
